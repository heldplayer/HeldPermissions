package me.heldplayer.permissions.core;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import java.util.*;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.loader.IPermissionsLoader;
import me.heldplayer.permissions.loader.PlayerNameLoader;
import me.heldplayer.permissions.loader.UUIDLoader;
import net.specialattack.bukkit.core.SpACore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PermissionsManager {

    public List<GroupPermissions> groups;
    public Set<PlayerPermissions> players;

    public Set<String> groupNames;

    public GroupPermissions defaultGroup;

    public PermissionsManager() {
        this.groups = new ArrayList<GroupPermissions>();
        this.players = new TreeSet<PlayerPermissions>();
        this.groupNames = new TreeSet<String>();
    }

    public boolean load(ConfigurationSection section) {
        int version = section.getInt("version", 0);
        IPermissionsLoader loader;
        boolean shouldSave = true;
        switch (version) {
            case 0:
                loader = new PlayerNameLoader();
                break;
            default:
                shouldSave = false;
                loader = new UUIDLoader();
                break;
        }

        loader.load(this, section);

        return shouldSave;
    }

    public void save(ConfigurationSection section) {
        section.set("version", 1);
        if (this.defaultGroup != null) {
            section.set("default", this.defaultGroup.name);
        }

        ConfigurationSection groups = section.createSection("groups");

        for (GroupPermissions group : this.groups) {
            ConfigurationSection groupSection = groups.createSection(group.name);
            group.save(groupSection);
        }

        ConfigurationSection users = section.createSection("users");

        for (PlayerPermissions player : this.players) {
            if (!player.isEmpty()) {
                ConfigurationSection playerSection = users.createSection(player.uuid.toString());
                player.save(playerSection);
            }
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

    @Deprecated
    public PlayerPermissions getPlayer(String playerName) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

        if (player != null) {
            for (PlayerPermissions permissions : this.players) {
                if (permissions.uuid.equals(player.getUniqueId())) {
                    return permissions;
                }
            }

            PlayerPermissions permissions = new PlayerPermissions(this, player.getUniqueId());
            this.players.add(permissions);
            return permissions;
        } else {
            for (PlayerPermissions permissions : this.players) {
                if (permissions.getPlayerName().equalsIgnoreCase(playerName)) {
                    return permissions;
                }
            }

            HttpProfileRepository repository = SpACore.getProfileRepository();

            Profile[] profiles = repository.findProfilesByNames(playerName);

            if (profiles.length == 1) {
                UUID uuid = profiles[0].getUUID();

                for (PlayerPermissions permissions : this.players) {
                    if (permissions.uuid.equals(uuid)) {
                        return permissions;
                    }
                }

                PlayerPermissions permissions = new PlayerPermissions(this, uuid);
                this.players.add(permissions);
                return permissions;
            } else if (profiles.length > 1) {
                Permissions.log.warning(String.format("'%s' has %s profiles set", playerName, profiles.length));
            }

            return null;
        }
    }

    public PlayerPermissions getPlayer(UUID uuid) {
        for (PlayerPermissions permissions : this.players) {
            if (permissions.uuid.equals(uuid)) {
                return permissions;
            }
        }

        PlayerPermissions permissions = new PlayerPermissions(this, uuid);
        this.players.add(permissions);
        return permissions;
    }

    public PlayerPermissions getPlayer(OfflinePlayer player) {
        for (PlayerPermissions permissions : this.players) {
            if (permissions.uuid.equals(player.getUniqueId())) {
                return permissions;
            }
        }

        PlayerPermissions permissions = new PlayerPermissions(this, player.getUniqueId());
        this.players.add(permissions);
        return permissions;
    }

    public HashMap<String, Boolean> getPermissions(Player player) {
        HashMap<String, Boolean> result = new HashMap<String, Boolean>();

        PlayerPermissions permissions = this.getPlayer(player.getName());
        permissions.buildPermissions(result, player.getWorld().getName());

        return result;
    }

    public List<String> getPlayersInGroup(String groupname) {
        ArrayList<String> result = new ArrayList<String>();
        for (PlayerPermissions permissions : this.players) {
            for (String group : permissions.getGroupNames()) {
                if (groupname.equalsIgnoreCase(group)) {
                    result.add(permissions.getPlayerName());
                    break;
                }
            }
        }
        return result;
    }

    public Collection<String> getAllGroupNames() {
        return Collections.unmodifiableSet(this.groupNames);
    }

    public void addGroup(GroupPermissions group) {
        if (group != null && !this.groupNames.contains(group.name)) {
            this.groups.add(group);
            this.groupNames.add(group.name);
        }
    }

    public void removeGroup(GroupPermissions group) {
        if (group != null) {
            this.groups.remove(group);
            this.groupNames.remove(group.name);
        }
    }

    private class TemporaryPermissionsRunnbable implements Runnable {

        @Override
        public void run() {
            // TODO Make a handler for temporary permissions

        }

    }

}
