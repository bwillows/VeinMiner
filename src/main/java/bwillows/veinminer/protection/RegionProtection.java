package bwillows.veinminer.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface RegionProtection {
    String getName();
    boolean canBreak(Player player, Location location);
}
