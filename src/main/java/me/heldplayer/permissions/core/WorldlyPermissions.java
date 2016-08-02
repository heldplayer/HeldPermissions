package me.heldplayer.permissions.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.configuration.ConfigurationSection;

public class WorldlyPermissions extends BasePermissions {

    private Set<WorldPermissions> worldPermissions;
    private Set<String> worldNames;

    public WorldlyPermissions(PermissionsManager manager) {
        super(manager);
        this.worldPermissions = new TreeSet<>();
        this.worldNames = new TreeSet<>();
    }

    @Override
    public boolean load(@Nonnull ConfigurationSection section, int version) {
        boolean needsSave = false;
        if (section.isConfigurationSection("permissions")) {
            needsSave = super.load(section.getConfigurationSection("permissions"), version);
        }
        ConfigurationSection worlds = section.getConfigurationSection("worlds");
        if (worlds != null) {
            Map<String, Object> worldsMap = worlds.getValues(false);

            for (String world : worldsMap.keySet()) {
                Object obj = worldsMap.get(world);
                if (obj instanceof ConfigurationSection) {
                    WorldPermissions permissions = new WorldPermissions(this.manager, world.toLowerCase());
                    needsSave |= permissions.load((ConfigurationSection) obj, version);
                    this.worldPermissions.add(permissions);
                    this.worldNames.add(world.toLowerCase());
                }
            }
        }
        return needsSave;
    }

    @Override
    public void save(@Nonnull ConfigurationSection section) {
        if (!super.isEmpty()) {
            super.save(section.createSection("permissions"));
        }
        if (!this.worldPermissions.isEmpty()) {
            boolean empty = true;
            for (WorldPermissions world : this.worldPermissions) {
                if (!world.isEmpty()) {
                    empty = false;
                    break;
                }
            }
            if (!empty) {
                ConfigurationSection worlds = section.createSection("worlds");

                this.worldPermissions.stream().filter(world -> !world.isEmpty()).forEach(world -> {
                    ConfigurationSection worldSection = worlds.createSection(world.worldname.toLowerCase());
                    world.save(worldSection);
                });
            }
        }
    }

    @Override
    public void release() {
        this.worldPermissions.forEach(BasePermissions::release);
        this.worldPermissions.clear();
        this.worldPermissions = null;
        this.worldNames.clear();
        this.worldNames = null;
        super.release();
    }

    @Override
    public void buildPermissions(@Nonnull PermCollection initial, @Nullable String world) {
        super.buildPermissions(initial, world);

        if (world != null) {
            this.manager.plugin.debug("Adding world permissions");
            this.worldPermissions.stream().filter(currentWorld -> currentWorld.worldname.equals(world)).forEach(currentWorld -> currentWorld.buildPermissions(initial, world));
        }
    }

    @Override
    public boolean isEmpty() {
        if (!this.worldPermissions.isEmpty()) {
            for (WorldPermissions world : this.worldPermissions) {
                if (!world.isEmpty()) {
                    return false;
                }
            }
        }
        return super.isEmpty();
    }

    @Nonnull
    public WorldPermissions getWorldPermissions(String worldName) {
        for (WorldPermissions world : this.worldPermissions) {
            if (world.worldname.equalsIgnoreCase(worldName)) {
                return world;
            }
        }
        WorldPermissions world = new WorldPermissions(this.manager, worldName.toLowerCase());
        this.worldPermissions.add(world);
        this.worldNames.add(worldName.toLowerCase());
        return world;
    }

    @Nonnull
    public Collection<String> getWorldNames() {
        return Collections.unmodifiableSet(this.worldNames);
    }
}
