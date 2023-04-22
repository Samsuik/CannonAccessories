package me.samsuik.accessories;

import me.samsuik.accessories.configuration.Configuration;
import me.samsuik.accessories.listener.DefenceListener;
import me.samsuik.accessories.listener.EntitySpawnListener;
import me.samsuik.accessories.listener.RegenListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CannonAccessories extends JavaPlugin {

    @Override
    public void onEnable() {
        Configuration config = new Configuration();
        config.loadConfig(this);

        if (!config.hasTNTSpread || config.heightParity) {
            EntitySpawnListener spawnListener = new EntitySpawnListener(config, this);
            Bukkit.getPluginManager().registerEvents(spawnListener, this);
        }

        if (config.defences != Configuration.RaidableDefences.ALLOWED) {
            DefenceListener defenceListener = new DefenceListener(config);
            Bukkit.getPluginManager().registerEvents(defenceListener, this);
        }

        if (!config.regenWalls) {
            RegenListener regenListener = new RegenListener();
            Bukkit.getPluginManager().registerEvents(regenListener, this);
        }
    }

}
