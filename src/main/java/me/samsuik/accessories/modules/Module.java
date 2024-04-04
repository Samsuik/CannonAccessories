package me.samsuik.accessories.modules;

import com.google.common.base.Preconditions;
import me.samsuik.accessories.CannonAccessories;
import me.samsuik.accessories.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class Module implements Listener {
    private Configuration configuration = null;
    private CannonAccessories plugin = null;

    public final Configuration getConfiguration() {
        Preconditions.checkNotNull(this.configuration, "Cannot retrieve configuration before registering module");
        return this.configuration;
    }

    public final CannonAccessories getPlugin() {
        Preconditions.checkNotNull(this.plugin, "Cannot retrieve plugin before registering module");
        return this.plugin;
    }

    public abstract boolean isEnabled(Configuration configuration);

    public final void registerModule(CannonAccessories plugin, Configuration configuration) {
        Preconditions.checkArgument(this.plugin == null, "Module already registered");
        this.plugin = plugin;
        this.configuration = configuration;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
