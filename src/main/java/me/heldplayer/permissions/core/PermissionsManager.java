package me.heldplayer.permissions.core;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.SaveException;
import me.heldplayer.permissions.loader.IPermissionsLoader;
import me.heldplayer.permissions.loader.PlayerNameLoader;
import me.heldplayer.permissions.loader.UUIDLoader;
import net.specialattack.spacore.SpACore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PermissionsManager {

    @Nonnull
    protected final Permissions plugin;

    @Nonnull
    public List<GroupPermissions> groups;
    @Nonnull
    public Set<PlayerPermissions> players;

    @Nonnull
    public Set<String> groupNames;
    @Nullable
    public GroupPermissions defaultGroup;

    private Exception error;

    public PermissionsManager(@Nonnull Permissions plugin) {
        this.plugin = plugin;
        this.groups = new ArrayList<>();
        this.players = new TreeSet<>();
        this.groupNames = new TreeSet<>();
    }

    public boolean load(@Nonnull ConfigurationSection section) {
        int version = section.getInt("version", 0);
        IPermissionsLoader loader;
        switch (version) {
            case 0:
                loader = new PlayerNameLoader(this.plugin);
                break;
            case 1:
            case 2:
                loader = new UUIDLoader();
                break;
            default:
                loader = new UUIDLoader();
                break;
        }

        return loader.load(this, section, version);
    }

    public void save(@Nonnull ConfigurationSection section) throws SaveException {
        if (this.error != null) {
            throw new SaveException("Cannot save as loading did not complete correctly");
        }
        section.set("version", 2);
        if (this.defaultGroup != null) {
            section.set("default", this.defaultGroup.name);
        } else {
            section.set("default", "**UNSET**");
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

    @Nullable
    public GroupPermissions getGroup(@Nonnull String group) {
        for (GroupPermissions permissions : this.groups) {
            if (permissions.name.equalsIgnoreCase(group)) {
                return permissions;
            }
        }
        return null;
    }

    @Nullable
    public PlayerPermissions getPlayer(@Nonnull String playerName) {
        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

        if (player != null) {
            return this.getPlayer(player.getUniqueId());
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

                return this.getPlayer(uuid);
            } else if (profiles.length > 1) {
                this.plugin.log.warning(String.format("'%s' has %s profiles set", playerName, profiles.length));
            }

            return null;
        }
    }

    @Nonnull
    public PlayerPermissions getPlayer(@Nonnull UUID uuid) {
        for (PlayerPermissions permissions : this.players) {
            if (permissions.uuid.equals(uuid)) {
                return permissions;
            }
        }

        PlayerPermissions permissions = new PlayerPermissions(this, uuid);
        this.players.add(permissions);
        return permissions;
    }

    @Nonnull
    public PlayerPermissions getPlayer(@Nonnull OfflinePlayer player) {
        return this.getPlayer(player.getUniqueId());
    }

    @Nonnull
    public PermCollection getPermissions(@Nonnull Player player) {
        PermCollection result = new PermCollection();

        this.plugin.debug("Getting permissions for " + player.getName());

        PlayerPermissions permissions = this.getPlayer(player.getUniqueId());
        permissions.buildPermissions(result, player.getWorld().getName());

        return result;
    }

    @Nonnull
    public List<String> getPlayersInGroup(@Nonnull String groupname) {
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

    @Nonnull
    public Collection<String> getAllGroupNames() {
        return Collections.unmodifiableSet(this.groupNames);
    }

    public void addGroup(@Nonnull GroupPermissions group) {
        if (!this.groupNames.contains(group.name)) {
            this.groups.add(group);
            this.groupNames.add(group.name);
        }
    }

    public void removeGroup(@Nonnull GroupPermissions group) {
        this.groups.remove(group);
        this.groupNames.remove(group.name);
    }

    public void setError(@Nonnull Exception e) {
        if (this.error != null) {
            this.plugin.log.log(Level.SEVERE, "More errors: " + e.getMessage());
            this.error.addSuppressed(e);
        } else {
            this.plugin.log.log(Level.SEVERE, "Permissions Manager errored", e);
            this.error = e;
        }
    }

    @Nullable
    public Exception getError() {
        return this.error;
    }
}
