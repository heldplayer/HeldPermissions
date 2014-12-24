package me.heldplayer.permissions.command.group;

import java.io.IOException;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PermissionsManager;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupNewCommand extends AbstractSubCommand {

    public GroupNewCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 1) {
            sender.sendMessage(Permissions.format("Expected %s parameter, no more, no less.", ChatColor.RED, 1));
            return;
        }

        PermissionsManager permissionsManager = Permissions.instance.getPermissionsManager();

        String group = args[0];
        GroupPermissions permissions = permissionsManager.getGroup(group);

        if (permissions != null) {
            sender.sendMessage(Permissions.format("Group '%s' already exists", ChatColor.RED, group));
            return;
        }

        permissions = new GroupPermissions(permissionsManager, group);
        permissionsManager.addGroup(permissions);

        sender.sendMessage(Permissions.format("Created a new empty group '%s'", ChatColor.GREEN, group));

        try {
            Permissions.instance.savePermissions();
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
        return new String[] { this.name + " <group>" };
    }

}
