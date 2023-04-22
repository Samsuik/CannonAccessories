package me.samsuik.accessories.listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import me.samsuik.accessories.configuration.Configuration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.VoxelShape;

import java.util.*;

public class DefenceListener implements Listener {

    private static final BoundingBox PARTIAL_BLOCK = new BoundingBox(0.4375, 0.0, 0.4375, 0.5625, 1.0, 0.5625);
    private static final BlockFace[] FACES = BlockFace.values();

    private final Set<Location> checked = new HashSet<>();
    private final Configuration config;

    public DefenceListener(Configuration config) {
        this.config = config;
    }

    @EventHandler
    public void onTick(ServerTickStartEvent event) {
        checked.clear();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onExplosion(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed && isFarFromCannon(event.getEntity())) {
            Location location = event.getEntity().getLocation();

            if (checked.contains(location)) {
                return;
            } else {
                checked.add(location);
            }

            Block block = location.getBlock();
            Material type = block.getType();

            boolean waterLogged = isWaterLogged(block);

            if (waterLogged) {
                event.blockList().add(block);
            }

            if (type == Material.LAVA || type == Material.WATER || waterLogged) {
               searchForPartialBlocks(block.getLocation(), event.blockList(), type == Material.LAVA);
            }
        }
    }

    private void searchForPartialBlocks(Location location, List<Block> blockList, boolean lava) {
        Queue<Location> queue = new LinkedList<>();
        Set<Location> searched = new HashSet<>();

        queue.add(location);

        for (Location l; (l = queue.poll()) != null;) {
            for (int i = 0; i < 6; ++i) {
                BlockFace face = FACES[i];
                Location next = l.clone().add(face.getDirection());

                if (searched.contains(next) || next.distanceSquared(location) >= 4 * 4) {
                    continue;
                } else {
                    searched.add(next);
                }

                Block block = next.getBlock();

                if ((lava || config.defences == Configuration.RaidableDefences.DISALLOWED)
                    && isPartialBlock(block.getCollisionShape()) || isWaterLogged(block)
                ) {
                    blockList.add(block);
                    queue.add(next);
                }
            }
        }
    }

    private boolean isWaterLogged(Block block) {
        return block.getBlockData() instanceof Waterlogged b && b.isWaterlogged();
    }

    private boolean isPartialBlock(VoxelShape shape) {
        for (BoundingBox box : shape.getBoundingBoxes()) {
            if (box.getMinX() + box.getMinZ() == 0.0 || box.getMaxX() + box.getMaxZ() == 2.0) {
                break;
            } else if (box.overlaps(PARTIAL_BLOCK)) {
                return true;
            }
        }

        return false;
    }

    @EventHandler
    public void onInsideBlock(EntityInsideBlockEvent event) {
        if (config.defences == Configuration.RaidableDefences.DISALLOWED
            && (event.getEntity() instanceof TNTPrimed || event.getEntity() instanceof FallingBlock)
            && isFarFromCannon(event.getEntity())
        ) {
            event.setCancelled(true);
        }
    }

    private boolean isFarFromCannon(Entity entity) {
        Location origin = entity.getOrigin();
        Location location = entity.getLocation();

        if (origin == null || origin.getWorld() != location.getWorld()) {
            return false;
        }

        double x = origin.getX() - location.getX();
        double z = origin.getZ() - location.getZ();
        return x * x + z * z >= 48 * 48;
    }

}
