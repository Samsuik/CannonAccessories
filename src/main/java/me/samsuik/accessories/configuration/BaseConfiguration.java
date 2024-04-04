package me.samsuik.accessories.configuration;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseConfiguration {
    private final FileConfiguration internal;
    private final JavaPlugin plugin;

    protected BaseConfiguration(JavaPlugin plugin) {
        this.internal = plugin.getConfig();
        this.plugin = plugin;
    }

    public final void loadConfig() {
        this.loadValues();
        this.internal.options().copyDefaults(true);
        this.plugin.saveConfig();
    }

    protected abstract void loadValues();

    protected final String getString(String path, String def, String... comments) {
        this.updateDefaultOf(path, def, comments);
        return this.internal.getString(path, def);
    }

    protected final boolean getBoolean(String path, boolean def, String... comments) {
        this.updateDefaultOf(path, def, comments);
        return this.internal.getBoolean(path, def);
    }

    protected final int getInt(String path, int def, String... comments) {
        this.updateDefaultOf(path, def, comments);
        return this.internal.getInt(path, def);
    }

    protected final <T extends Enum<T>> T getEnum(String path, T def, String... comments) {
        String name = def.name().toLowerCase(Locale.ROOT);
        this.updateDefaultOf(path, name, comments);
        return Enum.valueOf(def.getDeclaringClass(), this.internal.getString(path, name));
    }

    protected final Set<Material> getMaterials(String path, List<Material> materials, String... comments) {
        List<String> materialNames = materials.stream()
                .map(Enum::name)
                .toList();

        this.updateDefaultOf(path, materialNames, comments);

        return internal.getList(path, materials).stream()
                .filter(obj -> obj instanceof String)
                .map(obj -> (String) obj)
                .map(Material::matchMaterial)
                .collect(Collectors.toSet());
    }

    private void updateDefaultOf(String path, Object with, String... comments) {
        this.internal.addDefault(path, with);

        if (comments.length > 0) {
            this.internal.setComments(path, List.of(comments));
        }
    }
}
