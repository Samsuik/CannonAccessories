package me.samsuik.accessories.configuration;

import org.bukkit.plugin.java.JavaPlugin;

public final class Configuration extends BaseConfiguration {
    public TNTSpreadType tntSpread;
    public boolean heightParity;
    public boolean regenWalls;
    public UnraidableDefences unraidableDefences;

    public Configuration(JavaPlugin plugin) {
        super(plugin);
    }

    protected void loadValues() {
        this.tntSpread = this.getEnum("tnt-spread-mode", TNTSpreadType.ALL,
                "Controls whether TNT upon spawning has random horizontal velocity",
                "Disabling this can help with performance and allow players to make smaller designs.",
                "\"all\": Allow TNT to spread, same as vanilla",
                "\"up\": Only allow TNT to bounce upon spawning",
                "\"none\": Completely disables TNT spread"
        );
        this.heightParity = this.getBoolean("height-parity", false, "Makes Explosions affect Falling Blocks from the same point as TNT");
        this.regenWalls = this.getBoolean("regen-walls", true,
                "Controls whether regen walls can regenerate cobblestone and stone blocks.",
                "They are disabled for 10 seconds after a TNT explosion has occurred."
        );
        this.unraidableDefences = this.getEnum("unraidable-defences", UnraidableDefences.ALLOWED,
                "Allows the destruction of normally unraidable defences.",
                "\"allowed\": Allow all defences even if they're unraidable.",
                "\"partial\": Prevent completely unraidable defences, but some variations may require concrete cannons to bust.",
                "- Waterlogged defences will be destroyed by TNT.",
                "- Partial full block defences when protected by lava can be destroyed by TNT.",
                "\"disallowed\": Disallow some defences that can interfere with sand stackers",
                "- Powdered Snow, Cobwebs, Bubble Columns will no longer affect TNT and Sand.",
                "- Partial full block defences will all be destroyed by TNT.",
                "NOTE: TNT and Sand have to travel at least 48 blocks before this is applied."
        );
    }

    public enum TNTSpreadType {
        ALL,
        UP,
        NONE
    }

    public enum UnraidableDefences {
        ALLOWED,
        PARTIAL,
        DISALLOWED;

        public final boolean isPartialAllowed() {
            return this != ALLOWED;
        }
    }
}
