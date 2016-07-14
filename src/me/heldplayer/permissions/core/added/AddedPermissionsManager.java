package me.heldplayer.permissions.core.added;

import java.util.Set;
import java.util.TreeSet;
import me.heldplayer.permissions.loader.added.AddedPermissionsLoader;
import me.heldplayer.permissions.loader.added.IAddedPermissionsLoader;
import org.bukkit.configuration.ConfigurationSection;

public class AddedPermissionsManager {

    public Set<AddedPermission> addedPermissions;

    public AddedPermissionsManager() {
        this.addedPermissions = new TreeSet<>();
    }

    @SuppressWarnings("ConstantConditions")
    public boolean load(ConfigurationSection section) {
        int version = section.getInt("version", 0);
        IAddedPermissionsLoader loader;
        boolean shouldSave;
        switch (version) {
            default:
                shouldSave = false;
                loader = new AddedPermissionsLoader();
                break;
        }

        loader.load(this, section);

        return shouldSave;
    }

    public void save(ConfigurationSection section) {
        section.set("version", 0);

        ConfigurationSection groups = section.createSection("permissions");

        for (AddedPermission permission : this.addedPermissions) {
            ConfigurationSection permissionSection = groups.createSection(permission.name.replaceAll("\\.", "/"));
            permission.save(permissionSection);
        }
    }

    public void release() {
        this.addedPermissions.forEach(AddedPermission::release);
        this.addedPermissions.clear();
    }

    public AddedPermission getPermission(String name) {
        for (AddedPermission permission : this.addedPermissions) {
            if (permission.name.equalsIgnoreCase(name)) {
                return permission;
            }
        }
        return null;
    }
}
