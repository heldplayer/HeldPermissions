
package me.heldplayer.permissions.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PermissionsManager {

    private ArrayList<GroupPermissions> groups;
    private ArrayList<PlayerPermissions> players;

    private ArrayList<String> groupNames;

    protected GroupPermissions defaultGroup;

    public PermissionsManager() {
        this.groups = new ArrayList<GroupPermissions>();
        this.players = new ArrayList<PlayerPermissions>();
        this.groupNames = new ArrayList<String>();
    }

    public void load(ConfigurationSection section) {
        String defaultGroup = section.getString("default", "default");
        this.defaultGroup = this.getGroup(defaultGroup);

        ConfigurationSection groups = section.getConfigurationSection("groups");
        if (groups != null) {
            Map<String, Object> groupMap = groups.getValues(false);
            for (String key : groupMap.keySet()) {
                Object obj = groupMap.get(key);
                if (obj instanceof ConfigurationSection) {
                    this.groups.add(new GroupPermissions(this, key.toLowerCase()));
                    this.groupNames.add(key.toLowerCase());
                }
            }

            for (String key : groupMap.keySet()) {
                Object obj = groupMap.get(key);
                if (obj instanceof ConfigurationSection) {
                    GroupPermissions group = this.getGroup(key);
                    group.load((ConfigurationSection) obj);
                }
            }
        }

        ConfigurationSection users = section.getConfigurationSection("users");
        if (users != null) {
            Map<String, Object> userMap = users.getValues(false);
            for (String key : userMap.keySet()) {
                Object obj = userMap.get(key);
                if (obj instanceof ConfigurationSection) {
                    PlayerPermissions user = new PlayerPermissions(this, key.toLowerCase());
                    user.load((ConfigurationSection) obj);
                    this.players.add(user);
                }
            }
        }
    }

    public void save(ConfigurationSection section) {
        ConfigurationSection groups = section.createSection("groups");

        for (GroupPermissions group : this.groups) {
            ConfigurationSection groupSection = groups.createSection(group.name);
            group.save(groupSection);
        }

        ConfigurationSection users = section.createSection("users");

        for (PlayerPermissions player : this.players) {
            ConfigurationSection groupSection = users.createSection(player.playerName);
            player.save(groupSection);
        }

        if (this.defaultGroup != null) {
            section.set("default", this.defaultGroup.name);
        }
    }

    public void release() {
        for (GroupPermissions permission : this.groups) {
            permission.release();
        }
        this.groups.clear();
        for (PlayerPermissions permission : this.players) {
            permission.release();
        }
        this.players.clear();
        this.groupNames.clear();
    }

    public GroupPermissions getGroup(String group) {
        for (GroupPermissions permissions : this.groups) {
            if (permissions.name.equalsIgnoreCase(group)) {
                return permissions;
            }
        }
        return null;
    }

    public PlayerPermissions getPlayer(String player) {
        for (PlayerPermissions permissions : this.players) {
            if (permissions.playerName.equalsIgnoreCase(player)) {
                return permissions;
            }
        }
        PlayerPermissions permissions = new PlayerPermissions(this, player.toLowerCase());
        this.players.add(permissions);
        return permissions;
    }

    public HashMap<String, Boolean> getPermissions(Player player) {
        HashMap<String, Boolean> result = new HashMap<String, Boolean>();

        PlayerPermissions permissions = this.getPlayer(player.getName());
        permissions.buildPermissions(result, player != null ? player.getWorld().getName() : null);

        return result;
    }

    public List<String> getPlayersInGroup(String groupname) {
        ArrayList<String> result = new ArrayList<String>();
        for (PlayerPermissions permissions : this.players) {
            for (Iterator<String> i = permissions.getGroupNames().iterator(); i.hasNext();) {
                String group = i.next();
                if (groupname.equalsIgnoreCase(group)) {
                    result.add(permissions.playerName);
                    break;
                }
            }
        }
        return result;
    }

    public List<String> getAllGroupNames() {
        return Collections.unmodifiableList(this.groupNames);
    }

}
