package me.samsuik.accessories;

import me.samsuik.accessories.configuration.Configuration;
import me.samsuik.accessories.modules.*;
import me.samsuik.accessories.modules.Module;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CannonAccessories extends JavaPlugin {
    private final List<Module> modules = List.of(new UnraidableDefenceModule(), new FallingBlockParityModule(), new RegenWallsModule(), new TNTSpreadModule());

    @Override
    public void onEnable() {
        Configuration config = new Configuration();
        config.loadConfig(this);

        for (Module module : modules) {
            if (module.isEnabled(config)) {
                module.registerModule(this, config);
            }
        }
    }
}
