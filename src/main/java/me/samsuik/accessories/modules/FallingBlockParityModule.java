package me.samsuik.accessories.modules;

import me.samsuik.accessories.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class FallingBlockParityModule extends Module {
    private static Field NMS_ENTITY_EYE_HEIGHT;

    @Override
    public boolean isEnabled(Configuration configuration) {
        return configuration.heightParity;
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof FallingBlock fb) {
            this.updateEntityHeight(fb);
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
            Bukkit.getPluginManager().disablePlugin(this.getPlugin());
            throw new RuntimeException("Unable to update Falling Block height", e);
        }
    }

    private Field findEyeHeight(Class<?> clazz) {
        if (NMS_ENTITY_EYE_HEIGHT != null) {
            return NMS_ENTITY_EYE_HEIGHT;
        }

        boolean foundEntitySize = false;
        for (Field s : clazz.getDeclaredFields()) {
            if (s.getType().getSimpleName().equals("EntitySize") || s.getType().getSimpleName().equals("EntityDimensions")) {
                foundEntitySize = true;
            } else if (foundEntitySize) {
                s.setAccessible(true);
                return NMS_ENTITY_EYE_HEIGHT = s;
            }
        }

        return null;
    }
}
