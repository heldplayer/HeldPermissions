package me.heldplayer.permissions;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import me.heldplayer.permissions.core.GroupPermissions;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Vault_Permissions extends Permission {

    public Vault_Permissions(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "HeldPermissions";
    }

    @Override
    public boolean isEnabled() {
        return Permissions.instance.isEnabled();
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
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player setperm " + player + " " + permission + " true");
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player unsetperm " + player + " " + permission);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        Collection<String> groups = Permissions.instance.getPermissionsManager().getPlayer(player).getGroupNames();

        return groups.contains(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return world == null && plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player addgroup " + player + " " + group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return world == null && plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player removegroup " + player + " " + group);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    @Override
    public String[] getGroups() {
        Collection<String> groups = Permissions.instance.getPermissionsManager().getAllGroupNames();
        return groups.toArray(new String[groups.size()]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        List<GroupPermissions> groups = Permissions.instance.getPermissionsManager().getPlayer(player).getGroups();
        if (groups.isEmpty()) {
            return Permissions.instance.getPermissionsManager().defaultGroup.name;
        }
        return groups.get(0).name;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        Collection<String> groups = Permissions.instance.getPermissionsManager().getPlayer(player).getGroupNames();

        return groups.toArray(new String[groups.size()]);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        HashMap<String, Boolean> map = new HashMap<>();
        Permissions.instance.getPermissionsManager().getGroup(group).buildPermissions(map, world);
        return map.get(permission);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group setperm " + group + " " + permission + " true");
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group unsetperm " + group + " " + permission);
    }
}
