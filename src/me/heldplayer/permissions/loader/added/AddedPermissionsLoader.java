package me.heldplayer.permissions.loader.added;

import java.util.Map;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.added.AddedPermission;
import me.heldplayer.permissions.core.added.AddedPermissionsManager;
import org.bukkit.configuration.ConfigurationSection;

public class AddedPermissionsLoader implements IAddedPermissionsLoader {

    @Override
    public void load(AddedPermissionsManager manager, ConfigurationSection section) {
        ConfigurationSection permissions = section.getConfigurationSection("permissions");
        if (permissions != null) {
            Map<String, Object> groupMap = permissions.getValues(false);
            for (String key : groupMap.keySet()) {
                Object obj = groupMap.get(key);
                if (obj instanceof ConfigurationSection) {
                    AddedPermission permission = new AddedPermission(key.replaceAll("/", "."));
                    manager.addedPermissions.add(permission);
                }
            }

            for (String key : groupMap.keySet()) {
                Object obj = groupMap.get(key);
                if (obj instanceof ConfigurationSection) {
                    AddedPermission permission = manager.getPermission(key.replaceAll("/", "."));
                    if (permission != null) {
                        permission.load((ConfigurationSection) obj);
                    }
                }
            }
        }
    }

}
