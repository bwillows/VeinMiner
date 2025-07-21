package bwillows.veinminer.listeners;

import bwillows.veinminer.Utils;
import bwillows.veinminer.VeinMiner;
import bwillows.veinminer.VeinMinerConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void BlockBreakListener(BlockBreakEvent event) {
        if (!VeinMinerConfig.Settings.enabled)
            return;

        Player player = event.getPlayer();
        Block startBlock = event.getBlock();

        if (VeinMinerConfig.Settings.permission_enabled && VeinMinerConfig.Settings.permission_node != null && !player.hasPermission(VeinMinerConfig.Settings.permission_node)) {
            return;
        }

        if (VeinMinerConfig.Settings.shiftToBreak && !player.isSneaking())
            return;

        if(VeinMinerConfig.Settings.blocks == null)
            return;

        if (!VeinMinerConfig.Settings.blocks.contains(startBlock.getType()))
            return;

        event.setCancelled(true);

        veinMine(startBlock, player);
    }



    private void veinMine(Block startBlock, Player player) {
        Set<Block> visited = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        queue.add(startBlock);
        visited.add(startBlock);

        Material targetType = startBlock.getType();
        int maxBlocks = VeinMinerConfig.Settings.maxBlocks;
        int delay = VeinMinerConfig.Settings.breakDelay;
        World world = startBlock.getWorld();

        final int maxRange = VeinMinerConfig.Settings.maxRange;
        final int originX = startBlock.getX();
        final int originY = startBlock.getY();
        final int originZ = startBlock.getZ();

        new BukkitRunnable() {
            int brokenCount = 0;

            @Override
            public void run() {
                int layerSize = queue.size();
                if (layerSize == 0 || brokenCount >= maxBlocks) {
                    cancel();
                    return;
                }

                for (int i = 0; i < layerSize; i++) {
                    Block current = queue.poll();
                    if (current == null || !VeinMiner.instance.regionProtectionHandler.canBreak(player, current.getLocation())) continue;

                    if (VeinMinerConfig.Settings.breakSound_enabled) {
                        if(current != startBlock) {
                            player.playSound(
                                    current.getLocation(),
                                    VeinMinerConfig.Settings.breakSound_sound,
                                    (float) VeinMinerConfig.Settings.breakSound_volume,
                                    (float) VeinMinerConfig.Settings.breakSound_pitch
                            );
                        }
                    }

                    Collection<ItemStack> drops = current.getDrops();

                    ItemStack tool = null;
                    if(VeinMiner.IS_1_9) {
                        try {
                            Method getItemInMainHand = Player.class.getMethod("getItemInMainHand");
                            tool = (ItemStack) getItemInMainHand.invoke(player);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            Bukkit.getLogger().severe("[VeinMiner] Exception occurred while trying to get the item in hand");
                        }
                    } else {
                        tool = player.getInventory().getItemInHand();
                    }

                    if (VeinMinerConfig.Settings.consumeDurability) {
                        if (Utils.isInventoryFull(player)) {
                            current.breakNaturally(tool);
                        } else {
                            handleBlockDropAndDurability(player, current, current.getDrops(tool), tool);
                        }
                    } else {
                        handleBlockDropAndDurability(player, current, current.getDrops(tool), tool);
                    }

                    brokenCount++;
                    if (brokenCount >= maxBlocks) {
                        cancel();
                        return;
                    }

                    for (BlockFace face : BlockFace.values()) {
                        Block adjacent = current.getRelative(face);
                        if (!visited.contains(adjacent) && adjacent.getType() == targetType) {

                            int dx = Math.abs(adjacent.getX() - originX);
                            int dy = Math.abs(adjacent.getY() - originY);
                            int dz = Math.abs(adjacent.getZ() - originZ);
                            int distance = dx + dy + dz;

                            if (distance > maxRange) continue;

                            visited.add(adjacent);
                            queue.add(adjacent);
                        }
                    }
                }

                if (delay <= 0) {
                    run(); // instant ripple
                }
            }
        }.runTaskTimer(VeinMiner.instance, 0L, Math.max(1, delay));
    }

    private void handleBlockDropAndDurability(Player player, Block block, Collection<ItemStack> drops, ItemStack tool) {
        for (ItemStack drop : drops) {
            if (!attemptItemPickupHandler(player, drop)) {
                block.getWorld().dropItemNaturally(block.getLocation(), drop);
            }
        }

        block.setType(Material.AIR);



        if(!(player.getGameMode().equals(GameMode.CREATIVE))) {
            if (!isToolUnbreakable(tool)) {
                if (tool != null && tool.getType() != Material.AIR && tool.getType().getMaxDurability() > 0) {
                    short newDurability = (short) (tool.getDurability() + 1);
                    if (newDurability >= tool.getType().getMaxDurability()) {
                        tool.setAmount(0); // Tool breaks
                    } else {
                        tool.setDurability(newDurability);
                    }
                }
            }
        }
    }


    // TODO : Localized custom enchant hookup
    private boolean isToolUnbreakable(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR || !itemStack.hasItemMeta()) return false;

        ItemMeta meta = itemStack.getItemMeta();

        try {
            if (VeinMiner.IS_1_9) {
                if (VeinMiner.IS_1_12_2) {
                    Method isUnbreakableMethod = meta.getClass().getMethod("isUnbreakable");
                    if ((boolean) isUnbreakableMethod.invoke(meta)) {
                        return true;
                    }
                } else {
                    Method spigotMethod = meta.getClass().getMethod("spigot");
                    Object spigotMeta = spigotMethod.invoke(meta);
                    Method isUnbreakable = spigotMeta.getClass().getMethod("isUnbreakable");
                    if ((boolean) isUnbreakable.invoke(spigotMeta)) {
                        return true;
                    }
                }
            }
        } catch (IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
        }

        return true;
    }

    // TODO : Localized custom enchant / MMO hook
    public boolean attemptItemPickupHandler(Player player, ItemStack item) {
        return false;
    }
}
