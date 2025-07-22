package bwillows.veinminer.protection;

import bwillows.veinminer.Utils;
import bwillows.veinminer.protection.hooks.WorldGuardHook;
import bwillows.worldguardbridge.WorldGuardBridge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import worldguardbridge.v6.WorldGuardBridge_v6;
import worldguardbridge.v7.WorldGuardBridge_v7;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RegionProtectionHandler {

    private final List<RegionProtection> protections = new ArrayList<>();

    public RegionProtectionHandler(){
        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if (worldGuard != null && worldGuard.isEnabled()) {
            String version = worldGuard.getDescription().getVersion();

            if(Utils.WorldGuardIntegrityCheck(version)) {
                if (version.startsWith("6.")) {
                    WorldGuardBridge bridge = new WorldGuardBridge_v6();
                    protections.add(new WorldGuardHook(bridge));
                } else if (version.startsWith("7.0")) {
                    WorldGuardBridge bridge = new WorldGuardBridge_v7();
                    protections.add(new WorldGuardHook(bridge));
                } else {
                    Bukkit.getLogger().warning("[VeinMiner] Unsupported WorldGuard version: " + version);
                }
            } else {
                Bukkit.getLogger().warning("[VeinMiner] WorldGuard detected but failed integrity check");
            }
        }
    }

    public boolean canBreak(Player player, Location location) {
        for (RegionProtection protection : protections) {
            if (!protection.canBreak(player, location)) {
                return false;
            }
        }
        return true;
    }

    public List<RegionProtection> getRegisteredProtections() {
        return protections;
    }
}
