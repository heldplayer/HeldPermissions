package me.heldplayer.permissions;

import java.util.Collection;
import java.util.HashMap;
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
        if (bPlayer != null) {
            return bPlayer.hasPermission(permission);
        }
        return false;
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
        Collection<String> groups = Permissions.instance.getManager().getPlayer(player).getGroupNames();

        return groups.contains(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        if (world != null) {
            return false;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player addgroup " + player + " " + group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        if (world != null) {
            return false;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player removegroup " + player + " " + group);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    @Override
    public String[] getGroups() {
        return Permissions.instance.getManager().getAllGroupNames().toArray(new String[0]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        Collection<GroupPermissions> groups = Permissions.instance.getManager().getPlayer(player).getGroups();
        for (GroupPermissions group : groups) {
            return group.name;
        }
        return Permissions.instance.getManager().defaultGroup.name;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        Collection<String> groups = Permissions.instance.getManager().getPlayer(player).getGroupNames();

        return groups.toArray(new String[groups.size()]);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        HashMap<String, Boolean> map = new HashMap<String, Boolean>();
        Permissions.instance.getManager().getGroup(group).buildPermissions(map, world);
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
