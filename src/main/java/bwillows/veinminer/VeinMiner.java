package bwillows.veinminer;

import bwillows.veinminer.commands.VeinMinerCommand;
import bwillows.veinminer.listeners.BlockBreakListener;
import bwillows.veinminer.protection.RegionProtectionHandler;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class VeinMiner extends JavaPlugin {
    public static VeinMiner instance;
    public static String version = "Unknown";

    public File pluginFolder;

    public VeinMinerConfig veinMinerConfig;
    public RegionProtectionHandler regionProtectionHandler;

    public static Metrics metrics;

    // TODO : change implementation
    public static boolean IS_1_13 = Utils.isVersionAtLeast1_13();
    public static boolean IS_1_12_2 = Utils.isVersionAtLeast1_12_2();
    public static boolean IS_1_9 = Utils.isVersionAtLeast1_9();
    public static boolean IS_1_8 = Utils.isVersionAtLeast1_8();

    @Override
    public void onEnable() {
        instance = this;

        pluginFolder = new File(getDataFolder().getParent(), getDescription().getName());
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        veinMinerConfig = new VeinMinerConfig();
        regionProtectionHandler = new RegionProtectionHandler();

        // versioning info, bStats ID number, etc.
        loadProperties();

        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        getCommand("veinminer").setExecutor(new VeinMinerCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadProperties() {
        try (InputStream in = getResource("project.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                version = props.getProperty("version");

                if(veinMinerConfig.settings.bStats) {
                    String bStatsIDstring = props.getProperty("bstats-id");
                    if(bStatsIDstring != null) {
                        int bStatsID = Integer.parseInt(bStatsIDstring);
                        metrics = new Metrics(this, bStatsID);
                        getLogger().info("[VeinMiner] Enabled bStats");
                    } else {
                        getLogger().warning("[VeinMiner] bStats ID not found in project.properties.");
                    }
                }
            } else {
                getLogger().warning("[VeinMiner] project.properties not found in plugin jar.");
            }
        } catch (IOException e) {
            getLogger().warning("[VeinMiner] Failed to load version info:");
            e.printStackTrace();
        }
    }
}
