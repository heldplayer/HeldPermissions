package me.heldplayer.permissions.command.group;

import java.io.IOException;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PermissionsManager;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupAddRankableCommand extends AbstractSubCommand {

    public GroupAddRankableCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
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
        String rankable = args[1];
        GroupPermissions permissions = permissionsManager.getGroup(group);
        GroupPermissions rankableGroup = permissionsManager.getGroup(rankable);

        if (permissions == null) {
            sender.sendMessage(Permissions.format("Group '%s' doesn't exists", ChatColor.RED, group));
            return;
        }
        if (rankableGroup == null) {
            sender.sendMessage(Permissions.format("Group '%s' doesn't exists", ChatColor.RED, rankable));
            return;
        }

        if (rankableGroup.doesInheritFrom(permissions)) {
            sender.sendMessage(Permissions.format("Group '%s' can already rank '%s'", ChatColor.RED, group, rankable));
            return;
        }

        permissions.addRankable(rankableGroup);
        sender.sendMessage(Permissions.format("Made '%s' able to rank '%s'", ChatColor.GREEN, group, rankable));

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

            return TabHelper.tabAnyGroupExcept(permissions.getRankables());
        }

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name + " <group> <rankable>" };
    }

}
