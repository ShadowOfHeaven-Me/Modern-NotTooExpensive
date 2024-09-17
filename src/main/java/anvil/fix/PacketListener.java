package anvil.fix;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowProperty;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class PacketListener extends PacketListenerAbstract {

    @Override
    public void onPacketSend(PacketSendEvent _event) {
        if (!(_event.getClass() == PacketPlaySendEvent.class)) return;
        PacketPlaySendEvent event = (PacketPlaySendEvent) _event;

        UUID uuid = event.getUser().getUUID();

        //Bukkit.getLogger().info("PACKET: " + event.getPacketType());

        switch (event.getPacketType()) {
            case PLAYER_ABILITIES: {
                if (Events.preparing.containsKey(uuid)) {
                    WrapperPlayServerPlayerAbilities wrapper = new WrapperPlayServerPlayerAbilities(event);
                    wrapper.setInCreativeMode(true);
                    event.markForReEncode(true);
                }
                return;
            }
            case WINDOW_PROPERTY: {
                Integer level = Events.preparing.get(uuid);
                if (level != null) {
                    WrapperPlayServerWindowProperty wrapper = new WrapperPlayServerWindowProperty(event);
                    if (wrapper.getId() != 0) return;// throw new RuntimeException("INVALID: " + wrapper.getId());
                    wrapper.setValue(level);
                    //Bukkit.broadcastMessage("SET TO LVL: " + level);
                    event.markForReEncode(true);
                }
                return;
            }
            case SET_SLOT: {
                Integer level = Events.preparing.get(uuid);
                if (level != null) {
                    WrapperPlayServerSetSlot wrapper = new WrapperPlayServerSetSlot(event);
                    //Bukkit.broadcastMessage("SET SLOT " + wrapper.getSlot() + " ITEM: " + wrapper.getItem().getOrCreateTag().getTags());
                    //if (wrapper.getSlot() == 2) event.setCancelled(true);
                    event.getPostTasks().add(() -> {
                       event.getUser().sendPacket(new WrapperPlayServerWindowProperty((byte) wrapper.getWindowId(), 0, level));
                    });
                }
                return;
            }
        }
    }

    public static WrapperPlayServerPlayerAbilities createExact(Player player) {
        return create(player, player.getGameMode() == GameMode.CREATIVE);
    }

    public static WrapperPlayServerPlayerAbilities create(Player player, boolean creative) {
        return new WrapperPlayServerPlayerAbilities(player.isInvulnerable(), player.isFlying(), player.getAllowFlight(), creative, player.getFlySpeed() / 2, player.getWalkSpeed() / 2);
    }

    public static void init() {
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener());
    }
}