package me.heldplayer.permissions.command.group;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PermissionsManager;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupRemoveParentCommand extends AbstractSubCommand {

    public GroupRemoveParentCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 2) {
            sender.sendMessage(Permissions.format("Expected %s parameters, no more, no less.", ChatColor.RED, 2));
            return;
        }

        PermissionsManager permissionsManager = Permissions.instance.getPermissionsManager();

        String group = args[0];
        String parent = args[1];
        GroupPermissions permissions = permissionsManager.getGroup(group);
        GroupPermissions parentGroup = permissionsManager.getGroup(parent);

        if (permissions == null) {
            sender.sendMessage(Permissions.format("Group '%s' doesn't exists", ChatColor.RED, group));
            return;
        }
        if (parentGroup == null) {
            sender.sendMessage(Permissions.format("Group '%s' doesn't exists", ChatColor.RED, parent));
            return;
        }

        if (!permissions.doesInheritFrom(parentGroup)) {
            sender.sendMessage(Permissions.format("Group '%s' isn't a parent of '%s'", ChatColor.RED, group, parent));
            return;
        }

        permissions.removeParent(parentGroup);
        sender.sendMessage(Permissions.format("Removed parent '%s' of '%s'", ChatColor.GREEN, parent, group));

        Permissions.instance.recalculatePermissions();

        try {
            Permissions.instance.savePermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        if (args.length == 1) {
            return TabHelper.tabAnyGroup();
        }

        if (args.length == 2) {
            GroupPermissions permissions = Permissions.instance.getPermissionsManager().getGroup(args[0]);

            if (permissions == null) {
                return emptyTabResult;
            }

            return new ArrayList<String>(permissions.getParents());
        }

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name + " <group> <parent>" };
    }

}
