package bwillows.veinminer.protection;

import bwillows.veinminer.protection.hooks.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class RegionProtectionHandler {

    private final List<RegionProtection> protections = new ArrayList<>();

    public RegionProtectionHandler(){
        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (worldGuard != null && worldGuard.isEnabled()) {
            protections.add(new WorldGuardHook());
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
