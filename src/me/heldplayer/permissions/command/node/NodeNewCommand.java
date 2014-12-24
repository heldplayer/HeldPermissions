package me.heldplayer.permissions.command.node;

import java.io.IOException;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.added.AddedPermission;
import me.heldplayer.permissions.core.added.AddedPermissionsManager;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class NodeNewCommand extends AbstractSubCommand {

    public NodeNewCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 1) {
            sender.sendMessage(Permissions.format("Expected %s parameter, no more, no less.", ChatColor.RED, 1));
            return;
        }

        AddedPermissionsManager manager = Permissions.instance.getAddedPermissionsManager();

        String node = args[0];
        AddedPermission permissions = manager.getPermission(node);

        if (permissions != null) {
            sender.sendMessage(Permissions.format("Permissions definition '%s' already exists", ChatColor.RED, node));
            return;
        }

        Permission permission = Bukkit.getPluginManager().getPermission(node);

        if (permission != null) {
            sender.sendMessage(Permissions.format("Permission '%s' already exists", ChatColor.RED, node));
            return;
        }

        permissions = new AddedPermission(node);
        permission = new Permission(node);
        manager.addedPermissions.add(permissions);
        Bukkit.getPluginManager().addPermission(permission);

        sender.sendMessage(Permissions.format("Created a new permissions definition '%s'", ChatColor.GREEN, node));

        try {
            Permissions.instance.saveAddedPermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name + " <name>" };
    }

}
