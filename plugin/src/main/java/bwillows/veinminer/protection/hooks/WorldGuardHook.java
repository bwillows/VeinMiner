package bwillows.veinminer.protection.hooks;

import bwillows.veinminer.protection.RegionProtection;
import bwillows.worldguardbridge.WorldGuardBridge;
import bwillows.worldguardbridge.model.WrappedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public class WorldGuardHook implements RegionProtection {

    private final WorldGuardBridge bridge;

    public WorldGuardHook(WorldGuardBridge bridge) {
        if (bridge == null) {
            throw new IllegalStateException("WorldGuardBridge instance is null.");
        }
        this.bridge = bridge;
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        Set<String> regionIds = bridge.getApplicableRegions(location);
        if (regionIds.isEmpty()) return true; // no regions = allow

        for (String regionId : regionIds) {
            WrappedRegion region = bridge.getRegion(regionId);
            if (region == null) continue;

            boolean isOwner = region.getOwners().contains(player.getUniqueId().toString()) ||
                    region.getOwners().contains(player.getName());
            boolean isMember = region.getMembers().contains(player.getUniqueId().toString()) ||
                    region.getMembers().contains(player.getName());

            if (!isOwner && !isMember) return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "WorldGuard";
    }
}