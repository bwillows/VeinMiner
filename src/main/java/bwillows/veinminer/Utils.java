package bwillows.veinminer;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Utils {
    public static String generateRandomString(int length) {
        // Define the characters to choose from (letters + digits)
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Create a Random object
        Random random = new Random();

        // StringBuilder to hold the generated string
        StringBuilder stringBuilder = new StringBuilder(length);

        // Generate the random string
        for (int i = 0; i < length; i++) {
            // Pick a random index from the characters string
            int randomIndex = random.nextInt(characters.length());
            // Append the character at the random index to the result
            stringBuilder.append(characters.charAt(randomIndex));
        }

        // Return the generated string
        return stringBuilder.toString();
    }

    /**
     * Checks if the server is running on a version >= 1.13.
     *
     * @return true if the server version is 1.13 or higher, false otherwise
     */
    public static boolean isVersionAtLeast1_13() {
        // Get the full version string from Bukkit
        String version = Bukkit.getVersion();  // e.g. "git-PaperSpigot-445 (MC: 1.13.2)"

        // Extract the part after "MC: " and split on dots
        String[] versionParts = version.split("MC: ")[1].split("\\."); // ["1","13","2)"]

        try {
            int majorVersion = Integer.parseInt(versionParts[0]);  // e.g. 1
            int minorVersion = Integer.parseInt(versionParts[1]);  // e.g. 13

            // Return true if version is greater than 1, or exactly 1.13+
            return majorVersion > 1 || (majorVersion == 1 && minorVersion >= 13);
        } catch (Exception e) {
            // Fallback on parse error
            Bukkit.getLogger().warning("Failed to parse Minecraft version string: " + version);
            return false;
        }
    }
    /**
     * Checks if the server is running on a version > 1.12.2.
     *
     * @return true if the server version is newer than 1.12.2, false otherwise
     */
    public static boolean isVersionAtLeast1_12_2() {
        String version = Bukkit.getVersion(); // e.g. "git-Spigot-21f7e28-687aab5 (MC: 1.13.2)"

        try {
            String mcVersion = version.split("MC: ")[1].replaceAll("[^0-9.]", ""); // "1.13.2"
            String[] parts = mcVersion.split("\\.");

            int major = Integer.parseInt(parts[0]); // e.g. 1
            int minor = Integer.parseInt(parts[1]); // e.g. 13
            int patch = parts.length >= 3 ? Integer.parseInt(parts[2]) : 0; // optional patch version

            if (major > 1) return true;
            if (minor > 12) return true;
            if (minor == 12 && patch > 2) return true;

            return false;
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to parse Minecraft version string: " + version);
            return false;
        }
    }
    /**
     * Checks if the server is running on a version >= 1.9.
     *
     * @return true if the server version is 1.9 or higher, false otherwise
     */
    public static boolean isVersionAtLeast1_9() {
        // Get the full version string from Bukkit
        String version = Bukkit.getVersion();  // e.g. "git-PaperSpigot-445 (MC: 1.13.2)"

        // Extract the part after "MC: " and split on dots
        String[] versionParts = version.split("MC: ")[1].split("\\."); // ["1","13","2)"]

        try {
            int majorVersion = Integer.parseInt(versionParts[0].replaceAll("[^0-9]", ""));
            int minorVersion = Integer.parseInt(versionParts[1].replaceAll("[^0-9]", ""));

            // Return true if version is greater than 1, or exactly 1.9+
            return majorVersion > 1 || (majorVersion == 1 && minorVersion >= 9);
        } catch (Exception e) {
            // Fallback on parse error
            Bukkit.getLogger().warning("Failed to parse Minecraft version string: " + version);
            return false;
        }
    }

    /**
     * Checks if the server is running on Minecraft version 1.8.x.
     *
     * @return true if the server version is 1.8, false otherwise
     */
    public static boolean isVersionAtLeast1_8() {
        String version = Bukkit.getVersion(); // e.g. "git-Spigot-21f7e28-687aab5 (MC: 1.8.8)"

        try {
            String mcVersion = version.split("MC: ")[1].replaceAll("[^0-9.]", ""); // e.g. "1.8.8"
            String[] parts = mcVersion.split("\\.");

            int major = Integer.parseInt(parts[0]); // e.g. 1
            int minor = Integer.parseInt(parts[1]); // e.g. 8

            return major == 1 && minor == 8;
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to parse Minecraft version string: " + version);
            return false;
        }
    }

    public static boolean isInventoryFull(Player player) {
        Inventory inventory = player.getInventory();

        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                return false; // Found an empty slot
            }
        }

        return true; // No empty slots found
    }
}
