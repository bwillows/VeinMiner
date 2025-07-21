package bwillows.veinminer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VeinMinerConfig {
    public VeinMinerConfig() {
        reload();
    }

    private File configFile;
    private File langFile;

    public FileConfiguration configYml;
    public FileConfiguration langYml;

    public static class Settings {
        public static boolean enabled;
        public static boolean shiftToBreak;
        public static int maxBlocks;
        public static int maxRange;
        public static boolean consumeDurability;
        public static boolean permission_enabled;
        public static String permission_node;
        public static int breakDelay;
        public static boolean breakSound_enabled;
        public static Sound breakSound_sound;
        public static double breakSound_volume;
        public static double breakSound_pitch;
        public static Set<Material> blocks;
        public static boolean bStats; // state
    }
    public Settings settings = new Settings();

    public void reload() {
        configFile = new File(VeinMiner.instance.pluginFolder, "config.yml");
        if (!configFile.exists()) {
            VeinMiner.instance.saveResource("config.yml", false);
        }
        configYml = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(configFile);

        langFile = new File(VeinMiner.instance.pluginFolder, "lang.yml");
        if (!langFile.exists()) {
            VeinMiner.instance.saveResource("lang.yml", false);
        }
        langYml = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(langFile);

        // ---

        Settings.enabled = configYml.getBoolean("enabled");
        Settings.shiftToBreak = configYml.getBoolean("shiftToBreak");
        Settings.maxBlocks = configYml.getInt("maxBlocks");
        Settings.maxRange = configYml.getInt("maxRange");
        Settings.consumeDurability = configYml.getBoolean("consumeDurability");
        Settings.permission_enabled = configYml.getBoolean("permission.enabled");
        Settings.permission_node = configYml.getString("permission.node");
        Settings.breakDelay = configYml.getInt("breakDelay");
        Settings.breakSound_enabled = configYml.getBoolean("breakSound.enabled");
        if(Settings.breakSound_enabled) {
            String breakSound_string = configYml.getString("breakSound.sound");
            if(breakSound_string == null) {
                Bukkit.getLogger().warning("[VeinMiner] Invalid breakSound in config.yml");
                Settings.breakSound_enabled = false;
            } else {
                Sound breakSound_sound = Sound.valueOf(breakSound_string);
                if(breakSound_sound == null) {
                    Bukkit.getLogger().warning("[VeinMiner] Invalid breakSound in config.yml");
                    Settings.breakSound_enabled = false;
                } else {
                    Settings.breakSound_sound = breakSound_sound;
                }
            }
        }
        Settings.breakSound_volume = configYml.getDouble("breakSound.volume");
        Settings.breakSound_pitch = configYml.getDouble("breakSound.pitch");
        if(Settings.blocks == null) {
            Settings.blocks = new HashSet<>();
        }

        List<String> breakableBlocks = configYml.getStringList("blocks");
        if(breakableBlocks != null) {
            for(String blockString : breakableBlocks) {
                Material block = Material.matchMaterial(blockString);
                if(block == null) {
                    Bukkit.getLogger().warning("[VeinMiner] Invalid block in config.yml " + blockString);
                } else {
                    Settings.blocks.add(block);
                }
            }
        }

        Settings.bStats = configYml.getBoolean("bStats");

    }
}
