package me.heldplayer.permissions.core;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
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
        this.groups = new ArrayList<>();
        this.players = new TreeSet<>();
        this.groupNames = new TreeSet<>();
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

        this.players.stream().filter(player -> !player.isEmpty()).forEach(player -> {
            ConfigurationSection playerSection = users.createSection(player.uuid.toString());
            player.save(playerSection);
        });
    }

    public void release() {
        this.groups.forEach(GroupPermissions::release);
        this.groups.clear();
        this.players.forEach(PlayerPermissions::release);
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
        HashMap<String, Boolean> result = new HashMap<>();

        PlayerPermissions permissions = this.getPlayer(player.getName());
        permissions.buildPermissions(result, player.getWorld().getName());

        return result;
    }

    public List<String> getPlayersInGroup(String groupname) {
        ArrayList<String> result = new ArrayList<>();
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
}
