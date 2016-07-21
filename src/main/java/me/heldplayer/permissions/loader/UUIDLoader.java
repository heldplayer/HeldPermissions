package me.heldplayer.permissions.loader;

import java.util.Map;
import java.util.UUID;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PermissionsManager;
import me.heldplayer.permissions.core.PlayerPermissions;
import org.bukkit.configuration.ConfigurationSection;

public class UUIDLoader implements IPermissionsLoader {

    @Override
    public boolean load(PermissionsManager manager, ConfigurationSection section, int version) {
        boolean needsSave = false;
        ConfigurationSection groups = section.getConfigurationSection("groups");
        if (groups != null) {
            Map<String, Object> groupMap = groups.getValues(false);
            for (String key : groupMap.keySet()) {
                Object obj = groupMap.get(key);
                if (obj instanceof ConfigurationSection) {
                    manager.addGroup(new GroupPermissions(manager, key.toLowerCase()));
                }
            }

            for (String key : groupMap.keySet()) {
                Object obj = groupMap.get(key);
                if (obj instanceof ConfigurationSection) {
                    GroupPermissions group = manager.getGroup(key);
                    if (group == null) {
                        manager.setError(new RuntimeException("Missing group when loading"));
                    }
                    if (group != null) {
                        try {
                            needsSave |= group.load((ConfigurationSection) obj, version);
                        } catch (Exception e) {
                            manager.setError(e);
                        }
                    }
                }
            }
        }

        ConfigurationSection users = section.getConfigurationSection("users");
        if (users != null) {
            Map<String, Object> userMap = users.getValues(false);
            for (String key : userMap.keySet()) {
                Object obj = userMap.get(key);
                if (obj instanceof ConfigurationSection) {
                    PlayerPermissions user = new PlayerPermissions(manager, UUID.fromString(key));
                    try {
                        needsSave |= user.load((ConfigurationSection) obj, version);
                    } catch (Exception e) {
                        manager.setError(e);
                    }
                    manager.players.add(user);
                }
            }
        }

        needsSave |= !section.contains("default");

        manager.defaultGroup = manager.getGroup(section.getString("default", "default"));
        return needsSave;
    }
}
