package me.samsuik.accessories.configuration;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public class Configuration {

    public boolean hasTNTSpread;
    public boolean heightParity;
    public boolean regenWalls;
    public RaidableDefences defences;

    public void loadConfig(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        MemoryConfiguration def = new MemoryConfiguration();

        def.set("tnt-spread", true);
        def.set("height-parity", false);
        def.set("regen-walls", true);
        def.set("unraidable-defences", "allowed");

        config.setDefaults(def);
        config.options().copyDefaults(true);

        config.setComments("tnt-spread", List.of(
                "Controls whether TNT upon spawning has random horizontal velocity",
                "Disabling this can help with performance and allow players to make smaller designs."
        ));
        hasTNTSpread = config.getBoolean("tnt-spread");

        config.setComments("height-parity", List.of(
                "Makes Explosions affect Falling Blocks from the same point as TNT"
        ));
        heightParity = config.getBoolean("height-parity");

        config.setComments("regen-walls", List.of(
                "Controls whether regen walls can regenerate cobblestone and stone blocks.",
                "They are disabled for 10 seconds after a TNT explosion has occurred."
        ));
        regenWalls = config.getBoolean("regen-walls");

        config.setComments("unraidable-defences", List.of(
                "Allows the destruction of normally unraidable defences.",
                "\"allowed\": Allow all defences even if they're unraidable.",
                "\"partial\": Prevent completely unraidable defences, but some variations may require concrete cannons to bust.",
                "- Waterlogged defences will be destroyed by TNT.",
                "- Partial full block defences when protected by lava can be destroyed by TNT.",
                "\"disallowed\": Disallow some defences that can interfere with sand stackers",
                "- Powdered Snow, Cobwebs, Bubble Columns will no longer affect TNT and Sand.",
                "- Partial full block defences will all be destroyed by TNT.",
                "NOTE: TNT and Sand have to travel at least 48 blocks before this is applied."
        ));
        defences = Objects.requireNonNullElse(getDefences(config.getString("unraidable-defences")), RaidableDefences.ALLOWED);

        plugin.saveConfig();
    }

    private RaidableDefences getDefences(String string) {
        try {
            return RaidableDefences.values()[Integer.parseInt(string)];
        } catch (NumberFormatException ignored) {}

        try {
            return RaidableDefences.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public enum RaidableDefences {
        ALLOWED,    // Allow all defences even if they're unraidable.
        PARTIAL,    // Prevent waterlogged defences and _watered_ partial full blocks
        DISALLOWED; // Prevent all variations of "unraidable" defences even if they can be raided.
    }

}
