package me.samsuik.accessories.modules;

import me.samsuik.accessories.configuration.Configuration;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.util.Vector;

public final class TNTSpreadModule extends Module {
    private static final Vector TNT_SPREAD_ALL = new Vector(1, 1, 1);
    private static final Vector TNT_SPREAD_UP = new Vector(0, 1, 0);
    private static final Vector TNT_SPREAD_NONE = new Vector(0, 0, 0);

    @Override
    public boolean isEnabled(Configuration configuration) {
        return configuration.tntSpread != Configuration.TNTSpreadType.ALL;
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof TNTPrimed tnt) {
            tnt.setVelocity(tnt.getVelocity().multiply(this.getTntSpread()));
        }
    }

    private Vector getTntSpread() {
        return switch (this.getConfiguration().tntSpread) {
            case ALL -> TNT_SPREAD_ALL;
            case UP -> TNT_SPREAD_UP;
            default -> TNT_SPREAD_NONE;
        };
    }
}
