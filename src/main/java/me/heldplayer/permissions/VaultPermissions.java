package me.heldplayer.permissions;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VaultPermissions extends Permission {

    private final Permissions plugin;

    public VaultPermissions(Permissions plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "HeldPermissions";
    }

    @Override
    public boolean isEnabled() {
        return this.plugin.isEnabled();
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        Player bPlayer = Bukkit.getPlayer(player);
        return bPlayer != null && bPlayer.hasPermission(permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player setperm " + player + " " + permission + " true");
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player unsetperm " + player + " " + permission);
    }

    @Override
    public boolean playerInGroup(String world, String playerName, String group) {
        PlayerPermissions player = this.plugin.getPermissionsManager().getPlayer(playerName);
        if (player == null) {
            return false;
        }
        Collection<String> groups = player.getGroupNames();

        return groups.contains(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return world == null && this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player addgroup " + player + " " + group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return world == null && this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player removegroup " + player + " " + group);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    @Override
    public String[] getGroups() {
        Collection<String> groups = this.plugin.getPermissionsManager().getAllGroupNames();
        return groups.toArray(new String[groups.size()]);
    }

    @Override
    public String getPrimaryGroup(String world, String playerName) {
        PlayerPermissions player = this.plugin.getPermissionsManager().getPlayer(playerName);
        if (player == null) {
            return null;
        }
        List<GroupPermissions> groups = player.getGroups();
        if (groups.isEmpty()) {
            GroupPermissions defaultGroup = this.plugin.getPermissionsManager().defaultGroup;
            return defaultGroup == null ? null : defaultGroup.name;
        }
        return groups.get(0).name;
    }

    @Override
    public String[] getPlayerGroups(String world, String playerName) {
        PlayerPermissions player = this.plugin.getPermissionsManager().getPlayer(playerName);
        if (player == null) {
            return null;
        }
        Collection<String> groups = player.getGroupNames();

        return groups.toArray(new String[groups.size()]);
    }

    @Override
    public boolean groupHas(String world, String groupName, String permission) {
        BasePermissions.Perms map = new BasePermissions.Perms();
        @Nullable GroupPermissions group = this.plugin.getPermissionsManager().getGroup(groupName);
        if (group == null) {
            return false;
        }
        group.buildPermissions(map, world);
        return map.has(permission) && map.get(permission).value;
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group setperm " + group + " " + permission + " true");
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group unsetperm " + group + " " + permission);
    }
}
