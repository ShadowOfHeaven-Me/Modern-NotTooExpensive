package anvil.fix;

import org.bukkit.plugin.java.JavaPlugin;

@MainClass
public final class Main extends JavaPlugin {

    public static Main instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        if (!getServer().getPluginManager().isPluginEnabled("packetevents")) {
            this.getLogger().info("Missing dependency: packetevents! Download from https://www.spigotmc.org/resources/packetevents-api.80279/ in order to use this plugin.");
            this.setEnabled(false);
            return;
        }
        this.getServer().getPluginManager().registerEvents(new Events(), this);
        PacketListener.init();
    }

    @Override
    public void onDisable() {
    }
}