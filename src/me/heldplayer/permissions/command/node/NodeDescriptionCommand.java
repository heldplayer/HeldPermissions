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

public class NodeDescriptionCommand extends AbstractSubCommand {

    public NodeDescriptionCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length < 1) {
            sender.sendMessage(Permissions.format("Expected %s or more parameters.", ChatColor.RED, 1));
            return;
        }

        AddedPermissionsManager manager = Permissions.instance.getAddedPermissionsManager();

        String node = args[0];
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

        String description = "";

        if (args.length > 1) {
            description = args[1];
            for (int i = 2; i < args.length; i++) {
                description += " " + args[i];
            }
        }

        permissions.description = description;
        permission.setDescription(description);

        sender.sendMessage(Permissions.format("Changed the description of '%s' to '%s'", ChatColor.GREEN, node, description));

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

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name + " <name> <description>" };
    }

}
