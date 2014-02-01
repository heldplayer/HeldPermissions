
package me.heldplayer.permissions.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class WorldlyPermissions extends BasePermissions {

    private List<WorldPermissions> worldPermissions;
    private List<String> worldNames;

    public WorldlyPermissions(PermissionsManager manager) {
        super(manager);
        this.worldPermissions = new ArrayList<WorldPermissions>();
        this.worldNames = new ArrayList<String>();
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section == null ? null : section.getConfigurationSection("permissions"));
        if (section != null) {
            ConfigurationSection worlds = section.getConfigurationSection("worlds");
            if (worlds != null) {
                Map<String, Object> worldsMap = worlds.getValues(false);

                for (String world : worldsMap.keySet()) {
                    Object obj = worldsMap.get(world);
                    if (obj instanceof ConfigurationSection) {
                        WorldPermissions permissions = new WorldPermissions(this.manager, world.toLowerCase());
                        permissions.load((ConfigurationSection) obj);
                        this.worldPermissions.add(permissions);
                        this.worldNames.add(world.toLowerCase());
                    }
                }
            }
        }
    }

    @Override
    public void save(ConfigurationSection section) {
        if (!super.isEmpty()) {
            super.save(section.createSection("permissions"));
        }
        if (section != null) {
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

                    for (WorldPermissions world : this.worldPermissions) {
                        if (!world.isEmpty()) {
                            ConfigurationSection worldSection = worlds.createSection(world.worldname.toLowerCase());
                            world.save(worldSection);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void release() {
        for (BasePermissions permission : this.worldPermissions) {
            permission.release();
        }
        this.worldPermissions.clear();
        this.worldPermissions = null;
        this.worldNames.clear();
        this.worldNames = null;
        super.release();
    }

    @Override
    public void buildPermissions(HashMap<String, Boolean> initial, String world) {
        super.buildPermissions(initial, world);

        if (world != null) {
            for (WorldPermissions currentWorld : this.worldPermissions) {
                if (currentWorld.worldname.equalsIgnoreCase(world)) {
                    currentWorld.buildPermissions(initial, world);
                }
            }
        }
    }

    @Override
    public boolean isEmpty() {
        if (this.worldPermissions.isEmpty()) {
            return super.isEmpty();
        }
        else {
            for (WorldPermissions world : this.worldPermissions) {
                if (!world.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }

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

    public List<String> getWorldNames() {
        return Collections.unmodifiableList(this.worldNames);
    }

}
