package me.samsuik.accessories.modules;

import me.samsuik.accessories.configuration.Configuration;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

public final class ProtectSkyLimitModule extends Module {
    @Override
    public boolean isEnabled(Configuration configuration) {
        return configuration.protectBlocksAtSkylimit;
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        World world = event.getLocation().getWorld();
        event.blockList().removeIf(block -> block.getLocation().getY() == world.getMaxHeight() - 1);
    }
}
