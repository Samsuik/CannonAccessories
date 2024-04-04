package me.samsuik.accessories.modules;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import me.samsuik.accessories.configuration.Configuration;
import me.samsuik.accessories.configuration.Configuration.UnraidableDefences;
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
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.VoxelShape;

import java.util.*;

public final class UnraidableDefenceModule extends Module {
    private static final BoundingBox PARTIAL_BLOCK = new BoundingBox(0.4375, 0.0, 0.4375, 0.5625, 1.0, 0.5625);
    private static final BlockFace[] DIRECTIONS = Arrays.copyOf(BlockFace.values(), 6);
    private static final double OUTSIDE_CANNON_DISTANCE = 48 * 48;

    private final Set<Location> checked = new HashSet<>();

    @Override
    public boolean isEnabled(Configuration configuration) {
        return configuration.unraidableDefences.isPartialAllowed();
    }

    @EventHandler
    public void onTick(ServerTickStartEvent event) {
        this.checked.clear();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onExplosion(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed tnt && this.getConfiguration().unraidableDefences.isPartialAllowed() && this.isOutsideCannon(tnt)) {
            Location location = tnt.getLocation();

            if (!this.checked.add(location)) {
                return;
            }

            Block block = location.getBlock();
            boolean waterLogged = this.isWaterLogged(block);

            if (waterLogged) {
                event.blockList().add(block);
            }

            Material type = block.getType();
            if (type == Material.LAVA || type == Material.WATER || waterLogged) {
                this.searchForPartialBlocks(block.getLocation(), event.blockList(), type == Material.LAVA);
            }
        }
    }

    private void searchForPartialBlocks(Location location, List<Block> blockList, boolean lava) {
        Queue<Location> queue = new LinkedList<>();
        Set<Location> searched = new HashSet<>();

        queue.add(location);

        for (Location l; (l = queue.poll()) != null;) {
            for (BlockFace face : DIRECTIONS) {
                Location next = l.clone().add(face.getDirection());

                if (!(next.distanceSquared(location) < 4 * 4) || !searched.add(next)) {
                    continue;
                }

                Block block = next.getBlock();

                if ((lava || this.getConfiguration().unraidableDefences == UnraidableDefences.DISALLOWED) && this.isPartialBlock(block.getCollisionShape()) || this.isWaterLogged(block)) {
                    blockList.add(block);
                    queue.add(next);
                }
            }
        }
    }

    private boolean isWaterLogged(Block block) {
        return block.getBlockData() instanceof Waterlogged blockData && blockData.isWaterlogged();
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
        if (this.getConfiguration().unraidableDefences == UnraidableDefences.DISALLOWED && this.isCannonEntity(event.getEntity()) && this.isOutsideCannon(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    private boolean isCannonEntity(Entity entity) {
        return entity instanceof TNTPrimed || entity instanceof FallingBlock;
    }

    private boolean isOutsideCannon(Entity entity) {
        Location origin = entity.getOrigin();
        Location location = entity.getLocation();
        if (origin == null || origin.getWorld() != location.getWorld())
            return false;
        double x = origin.getX() - location.getX();
        double z = origin.getZ() - location.getZ();
        return x * x + z * z >= OUTSIDE_CANNON_DISTANCE;
    }
}
