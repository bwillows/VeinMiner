package bwillows.veinminer.protection.hooks;

import bwillows.veinminer.protection.RegionProtection;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WorldGuardHook implements RegionProtection {

    private WorldGuardPlugin worldGuard;

    public WorldGuardHook() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if (!(plugin instanceof WorldGuardPlugin)) {
            throw new IllegalStateException("WorldGuard plugin not found or incompatible.");
        }

        this.worldGuard = (WorldGuardPlugin) plugin;
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        RegionManager regionManager = worldGuard.getRegionManager(location.getWorld());
        if (regionManager == null) return true; // allow if no region manager

        ApplicableRegionSet regions = regionManager.getApplicableRegions(location);
        if (regions.size() == 0) return true; // no regions = allow

        for (ProtectedRegion region : regions) {
            if (!region.isMember(worldGuard.wrapPlayer(player)) &&
                    !region.isOwner(worldGuard.wrapPlayer(player))) {
                return false; // deny if not member or owner
            }
        }
        return true; // allow if member/owner in all regions
    }

    @Override
    public String getName() {
        return "WorldGuard";
    }
}
