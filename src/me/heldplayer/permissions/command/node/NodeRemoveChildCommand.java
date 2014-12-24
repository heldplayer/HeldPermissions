package me.heldplayer.permissions.command.node;

import java.io.IOException;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.added.AddedPermission;
import me.heldplayer.permissions.core.added.AddedPermissionsManager;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class NodeRemoveChildCommand extends AbstractSubCommand {

    public NodeRemoveChildCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 2) {
            sender.sendMessage(Permissions.format("Expected %s parameters, no more, no less.", ChatColor.RED, 2));
            return;
        }

        AddedPermissionsManager manager = Permissions.instance.getAddedPermissionsManager();

        String node = args[0];
        String childNode = args[1];
        AddedPermission permissions = manager.getPermission(node);

        if (permissions == null) {
            sender.sendMessage(Permissions.format("Permissions definition '%s' doesn't exist", ChatColor.RED, node));
            return;
        }

        Permission permission = Bukkit.getPluginManager().getPermission(node);

        if (permission == null) {
            sender.sendMessage(Permissions.format("Permission '%s' doesn't exist?!", ChatColor.RED, node));
            return;
        }

        permissions.children.remove(childNode);
        permission.getChildren().remove(childNode);

        sender.sendMessage(Permissions.format("Made '%s' a child of '%s'", ChatColor.GREEN, childNode, node));

        try {
            Permissions.instance.saveAddedPermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        if (args.length == 1) {
            return TabHelper.tabAnyAddedPermission(args[0]);
        }

        if (args.length == 2) {
            return TabHelper.tabAnyChild(args[0]);
        }

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name + " <name> <description>" };
    }

}
