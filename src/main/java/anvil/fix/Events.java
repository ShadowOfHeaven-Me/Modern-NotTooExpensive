package anvil.fix;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;

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

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        Player player = (Player) event.getView().getPlayer();
        AnvilInventory inv = event.getInventory();
        //Bukkit.broadcastMessage("MAX " + inv.getMaximumRepairCost() + " COST: " + inv.getRepairCost() + " AMOUNT: " + inv.getRepairCostAmount());
/*        if (inv.getRepairCost() < 0) {
            inv.setRepairCost(30);
            Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
                inv.setRepairCost(30);
            }, 1L);
            Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
                inv.setRepairCost(30);
            }, 2L);
            Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
                inv.setRepairCost(30);
            }, 3L);
        }*/
        inv.setMaximumRepairCost(40_000);

        ItemStack item = inv.getItem(1);
        if (item == null) return;

        if ((inv.getRepairCost() >= 40 || inv.getRepairCost() <= 0) && item.getType() == Material.ENCHANTED_BOOK) {
            /*if (player.getLevel() < 39) {
                inv.setRepairCost(39);
                return;
            }*/
            //Bukkit.broadcastMessage("MMMMMMMMMMMMM");
            ItemStack res = inv.getItem(0);
            if (res == null) return;

            ItemStack result = res.clone();//already done with "asMirrorCopy", but whatever

            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            assert meta != null;

            for (Map.Entry<Enchantment, Integer> e : meta.getStoredEnchants().entrySet()) {
                if (result.getEnchantmentLevel(e.getKey()) < e.getValue())
                    result.addUnsafeEnchantment(e.getKey(), e.getValue());
            }

            event.setResult(result);
            inv.setRepairCost(39);
/*            Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
                inv.setRepairCost(30);
            }, 1L);
            Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
                inv.setRepairCost(30);
            }, 2L);
            Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
                inv.setRepairCost(30);
            }, 3L);*/
        }

        /*
        AnvilInventory inv = event.getInventory();

        if (setMaximumRepairCost != null) this.modernHandling(event);
        else this.oldHandling(event);

        Bukkit.broadcastMessage("PRE TO SET " + event.getInventory().getRepairCost());*/
    }

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