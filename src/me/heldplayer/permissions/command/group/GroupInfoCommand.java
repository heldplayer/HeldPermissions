package me.heldplayer.permissions.command.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupInfoCommand extends AbstractSubCommand {

    public GroupInfoCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 1) {
            sender.sendMessage(Permissions.format("Expected %s parameter, no more, no less.", ChatColor.RED, 1));
            return;
        }

        String group = args[0];
        GroupPermissions permissions = Permissions.instance.getManager().getGroup(group);

        if (permissions == null) {
            sender.sendMessage(Permissions.format("Group '%s' doesn't exists", ChatColor.RED, group));
            return;
        }

        sender.sendMessage(Permissions.format("Information on group %s", ChatColor.LIGHT_PURPLE, group));
        // Print the amount of players in the group:
        Collection<String> list = Permissions.instance.getManager().getPlayersInGroup(group);
        sender.sendMessage(Permissions.format("Number of players in group: %s", ChatColor.LIGHT_PURPLE, list.size()));
        // Print the groups that the group can rank to
        list = permissions.getRankables();
        String message = "Rankable groups: %s";

        for (int i = 1; i < list.size(); i++) {
            message += ", %s";
        }
        if (list.isEmpty()) {
            sender.sendMessage(Permissions.format(message, ChatColor.LIGHT_PURPLE, "none"));
        } else {
            sender.sendMessage(Permissions.format(message, ChatColor.LIGHT_PURPLE, list.toArray()));
        }
        // Print the groups the group inherits from directly
        list = permissions.getParents();
        message = "Direct parents: %s";

        for (int i = 1; i < list.size(); i++) {
            message += ", %s";
        }
        if (list.isEmpty()) {
            sender.sendMessage(Permissions.format(message, ChatColor.LIGHT_PURPLE, "none"));
        } else {
            sender.sendMessage(Permissions.format(message, ChatColor.LIGHT_PURPLE, list.toArray()));
        }
        // Print the groups the group inherits from indirectly
        {
            Collection<String> temp = list;
            list = new ArrayList<String>(permissions.getAllGroupNames());
            list.removeAll(temp);
        }
        message = "Indirect parents: %s";

        for (int i = 1; i < list.size(); i++) {
            message += ", %s";
        }
        if (list.isEmpty()) {
            sender.sendMessage(Permissions.format(message, ChatColor.LIGHT_PURPLE, "none"));
        } else {
            sender.sendMessage(Permissions.format(message, ChatColor.LIGHT_PURPLE, list.toArray()));
        }
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        if (args.length == 1) {
            return TabHelper.tabAnyGroup();
        }

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name + " <group>" };
    }

}
