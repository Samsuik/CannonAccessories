package me.samsuik.accessories.modules;

import me.samsuik.accessories.configuration.Configuration;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class ArrowCannonModule extends Module {
    private static final double BAD_ARROW_VELOCITY = 20.0 * 20.0;

    @Override
    public boolean isEnabled(Configuration configuration) {
        return !configuration.arrowCannons;
    }

    @EventHandler
    public void preventArrowCannons(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow arrow && arrow.getVelocity().lengthSquared() >= BAD_ARROW_VELOCITY) {
            event.setDamage(0.0);
        }
    }
}
