
package me.heldplayer.permissions.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.heldplayer.permissions.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class BasePermissions {

    public List<String> allow;
    public List<String> deny;
    protected final PermissionsManager manager;

    public BasePermissions(PermissionsManager manager) {
        this.manager = manager;
        this.allow = new ArrayList<String>();
        this.deny = new ArrayList<String>();
    }

    public void load(ConfigurationSection section) {
        if (section != null) {
            this.allow = section.getStringList("allow");
            this.deny = section.getStringList("deny");
        }
    }

    public void save(ConfigurationSection section) {
        if (section != null) {
            if (!this.allow.isEmpty()) {
                section.set("allow", this.allow);
            }
            if (!this.deny.isEmpty()) {
                section.set("deny", this.deny);
            }
        }
    }

    public void release() {
        this.allow.clear();
        this.allow = null;
        this.deny.clear();
        this.deny = null;
    }

    public void buildPermissions(HashMap<String, Boolean> initial, String world) {
        HashMap<String, Boolean> result = new HashMap<String, Boolean>();

        for (String key : this.allow) {
            result.put(key, true);
        }

        for (String key : this.deny) {
            if (result.containsKey(key)) {
                result.remove(key);
            }

            result.put(key, false);
        }

        Util.joinMaps(initial, result);
    }

    public boolean hasPermission(String permission, World world) {
        HashMap<String, Boolean> result = new HashMap<String, Boolean>();
        this.buildPermissions(result, world.getName());

        if (result.containsKey(permission)) {
            return result.get(permission);
        }

        Permission perm = Bukkit.getPluginManager().getPermission(permission);

        if (perm != null) {
            if (perm.getDefault() == PermissionDefault.TRUE) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return this.allow.isEmpty() && this.deny.isEmpty();
    }

}
