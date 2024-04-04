package me.samsuik.accessories.modules;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import me.samsuik.accessories.configuration.Configuration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class RegenWallsModule extends Module {
    private static final Set<Material> REGENERATION_BLOCKS = Set.of(Material.COBBLESTONE, Material.STONE);
    private static final int DISABLE_CHUNK_RADIUS = 6;

    private final Map<Chunk, Integer> disabledChunks = new HashMap<>();

    @Override
    public boolean isEnabled(Configuration configuration) {
        return !configuration.regenWalls;
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        Chunk chunk = this.getChunkWithoutLoading(event.getBlock());
        if (!this.disabledChunks.containsKey(chunk))
            return;
        // cancel fluids forming blocks
        if (event.getBlock().isLiquid() && REGENERATION_BLOCKS.contains(event.getNewState().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onServerTick(ServerTickStartEvent event) {
        if (event.getTickNumber() % 20 == 0) {
            this.disabledChunks.values().removeIf(tick -> event.getTickNumber() - tick >= 200);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
           this.disableSurroundingChunks(event.getLocation().getBlock());
        }
    }

    private void disableSurroundingChunks(Block block) {
        for (int x = -1; x <= 1; x += 2) {
            for (int z = -1; z <= 1; z += 2) {
                Block relative = block.getRelative(x * DISABLE_CHUNK_RADIUS, 0, z * DISABLE_CHUNK_RADIUS);
                Chunk chunk = this.getChunkWithoutLoading(relative);
                this.disabledChunks.put(chunk, Bukkit.getCurrentTick());
            }
        }
    }

    private Chunk getChunkWithoutLoading(Block block) {
        Location location = block.getLocation();
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        return block.getWorld().getChunkAt(chunkX, chunkZ, false);
    }
}
