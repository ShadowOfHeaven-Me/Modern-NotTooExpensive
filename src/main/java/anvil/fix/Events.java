package anvil.fix;

import com.github.retrooper.packetevents.PacketEvents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Events implements Listener {

/*    private static final Method setMaximumRepairCost;

    static {
        Method method;
        try {
            method = AnvilInventory.class.getMethod("setMaximumRepairCost", int.class);
        } catch (NoSuchMethodException e) {
            method = null;
        }
        setMaximumRepairCost = method;
    }*/

    public static final Map<UUID, Integer> preparing = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        //Bukkit.broadcastMessage("PrepareAnvilEvent");
        if (!(event.getView().getPlayer() instanceof Player player)) return;

        AnvilInventory inv = event.getInventory();

        //Bukkit.broadcastMessage("MAX " + inv.getMaximumRepairCost() + " COST: " + inv.getRepairCost() + " AMOUNT: " + inv.getRepairCostAmount());

        //Bukkit.broadcastMessage("INVOKED WITH COST: " + inv.getRepairCost() + " RENAME: '" + inv.getRenameText() + "'");

        inv.setMaximumRepairCost(Integer.MAX_VALUE - 10);

        //39 is the max
        //at 40, "Too Expensive" shows up
        if (/*inv.getItem(2) == null ||*/ inv.getRepairCost() >= 0 && inv.getRepairCost() <= 39) {
            this.onRemove(player);
            //Bukkit.broadcastMessage("RESULT REMOVEDDDDD");
            return;
        }

        ItemStack combineWith = inv.getItem(1);
        if (combineWith == null || combineWith.getType().isAir()) return;

        Material combineType = combineWith.getType();

        ItemStack input = inv.getItem(0);
        if (input == null) return;

        //Integer level = preparing.get(player.getUniqueId());

        Material inputType = input.getType();

        if (combineType != Material.ENCHANTED_BOOK && combineType != Material.BOOK && combineType != inputType && inv.getItem(2) == null)
            return;

        ItemStack result = input.clone();//already done with "asMirrorCopy", but it's best to be safe
        Map<Enchantment, Integer> enchantsOnInput = input.getEnchantments();

        var meta = combineWith.getItemMeta();
        assert meta != null;
        Map<Enchantment, Integer> enchants = meta instanceof EnchantmentStorageMeta e ? e.getStoredEnchants() : meta.getEnchants();
        Set<Map.Entry<Enchantment, Integer>> enchantsOnBook = enchants.entrySet();

        float cost = this.calculateInitialCost(enchantsOnInput);
        int applied = 0;

        for (Map.Entry<Enchantment, Integer> e : enchantsOnBook) {
            Enchantment enchantment = e.getKey();
            int level = e.getValue();
            if (result.getEnchantmentLevel(enchantment) < level && !this.hasConflicting(enchantsOnInput, enchantment) && enchantment.canEnchantItem(input)) {
                result.addUnsafeEnchantment(enchantment, level);
                applied++;
                //Bukkit.broadcastMessage("VVVVV " + e.getValue());
                cost += level * 1.5f;
            }
        }

        //Bukkit.broadcastMessage("PRE TO APPLIED: " + applied);

        if (applied == 0) return;

        String rename = inv.getRenameText();
        if (rename != null && !rename.isEmpty()) {
            cost++;
            ItemMeta resultMeta = result.getItemMeta();
            assert resultMeta != null;//pretty much useless
            resultMeta.setDisplayName(rename);
            result.setItemMeta(resultMeta);
        }

        event.setResult(result);
        inv.setRepairCost((int) cost);
        //Bukkit.broadcastMessage("APPLIED: " + applied + " COST: " + inv.getRepairCost());
        preparing.put(player.getUniqueId(), (int) cost);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, PacketListener.create(player, true));
    }

    private float calculateInitialCost(Map<Enchantment, Integer> inputEnchants) {
        float cost = 40;
        for (Map.Entry<Enchantment, Integer> e : inputEnchants.entrySet()) {
            cost += e.getValue() / 2.5f;
        }
        //Bukkit.broadcastMessage("INITIAL " + cost);
        return cost;
    }

    private boolean hasConflicting(Map<Enchantment, Integer> enchants, Enchantment toCheckConflict) {
        for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
            if (e.getKey().conflictsWith(toCheckConflict)) return true;
        }
        return false;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (event.getView().getTopInventory().getType() == InventoryType.ANVIL) {
            this.onRemove(player);
            //Bukkit.broadcastMessage("REMOVED");
        }
        //Bukkit.broadcastMessage("CLOSE " + event.getView().getTopInventory().getType());
    }

    private void onRemove(Player player) {
        if (preparing.remove(player.getUniqueId()) != null)
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, PacketListener.createExact(player));
    }

/*    @EventHandler
    public void onClick(InventoryClickEvent event) {
        //event.getAction()
        Bukkit.broadcastMessage("CLICK " + event.getRawSlot());
    }*/

/*    @EventHandler
    public void onClick(InventoryClickEvent event) {
        //event.getAction()
        
    }

    private void modernHandling(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();
        try {
            setMaximumRepairCost.invoke(inv, 100_000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void oldHandling(PrepareAnvilEvent event) {
        Player player = (Player) event.getView().getPlayer();
        AnvilInventory inv = event.getInventory();
        ItemStack item = inv.getItem(1);

        if (item == null) return;

        if (inv.getRepairCost() >= 40 && player.getLevel() >= inv.getRepairCost() && item.getType() == Material.ENCHANTED_BOOK) {
            ItemStack res = inv.getItem(0);
            if (res == null) return;

            ItemStack result = res.clone();//already done with "asMirrorCopy", but whatever

            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

            for (Map.Entry<Enchantment, Integer> e : meta.getStoredEnchants().entrySet()) {
                if (result.getEnchantmentLevel(e.getKey()) < e.getValue())
                    result.addUnsafeEnchantment(e.getKey(), e.getValue());
            }

            event.setResult(result);
            event.getInventory().setRepairCost(0);

        }
    }*/
}