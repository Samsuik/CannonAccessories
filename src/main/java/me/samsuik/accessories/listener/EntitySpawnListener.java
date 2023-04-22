package me.samsuik.accessories.listener;

import me.samsuik.accessories.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EntitySpawnListener implements Listener {

    private static Field NMS_ENTITY_EYE_HEIGHT;

    private final Configuration config;
    private final JavaPlugin plugin;

    public EntitySpawnListener(Configuration config, JavaPlugin plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if (!config.hasTNTSpread && entity instanceof TNTPrimed) {
            entity.setVelocity(entity.getVelocity().multiply(new Vector(0,1,0)));
        }

        if (config.heightParity && entity instanceof FallingBlock fb) {
            updateEntityHeight(fb);
        }
    }

    private void updateEntityHeight(FallingBlock fb) {
        try {
            Method method = fb.getClass().getDeclaredMethod("getHandle");
            Object nmsFB = method.invoke(fb);
            Field eyeHeight = findEyeHeight(nmsFB.getClass().getSuperclass());

            assert eyeHeight != null;
            eyeHeight.set(nmsFB, 0.0f);
        } catch (Exception e) {
            Bukkit.getPluginManager().disablePlugin(plugin);
            throw new RuntimeException("Unable to update Falling Block height", e);
        }
    }

    private Field findEyeHeight(Class<?> clazz) {
        if (NMS_ENTITY_EYE_HEIGHT != null) {
            return NMS_ENTITY_EYE_HEIGHT;
        }

        boolean foundEntitySize = false;
        for (Field s : clazz.getDeclaredFields()) {
            if (s.getType().getSimpleName().equals("EntitySize")) {
                foundEntitySize = true;
            } else if (foundEntitySize) {
                s.setAccessible(true);
                return NMS_ENTITY_EYE_HEIGHT = s;
            }
        }

        return null;
    }

}
