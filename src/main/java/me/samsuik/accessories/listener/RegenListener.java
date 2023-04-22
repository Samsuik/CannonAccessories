package me.samsuik.accessories.listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import me.samsuik.accessories.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.HashMap;
import java.util.Map;

public class RegenListener implements Listener {

    private final Map<Chunk, Integer> exploded = new HashMap<>();

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        if (event.getBlock().isLiquid() && (event.getNewState().getType() == Material.COBBLESTONE
            || event.getNewState().getType() == Material.STONE) && regenBlock(event.getBlock())
        ) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onServerTick(ServerTickStartEvent event) {
        if (event.getTickNumber() % 20 == 0) {
            exploded.values().removeIf((tick) -> (event.getTickNumber() - tick) >= 200);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onExplode(EntityExplodeEvent event) {
       if (event.getEntity() instanceof TNTPrimed) {
           chunk(event.getLocation().clone().subtract(6, 0, -6));
           chunk(event.getLocation().clone().subtract(6, 0, 6));
           chunk(event.getLocation().clone().subtract(-6, 0, -6));
           chunk(event.getLocation().clone().subtract(-6, 0, 6));
       }
    }

    private boolean regenBlock(Block block) {
        return exploded.containsKey(block.getChunk());
    }

    private void chunk(Location location) {
        exploded.put(location.getChunk(), Bukkit.getCurrentTick());
    }

}
