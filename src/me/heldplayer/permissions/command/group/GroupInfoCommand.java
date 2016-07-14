package me.heldplayer.permissions.command.group;

import java.util.ArrayList;
import java.util.Collection;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import net.specialattack.bukkit.core.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupInfoCommand extends AbstractSubCommand {

    private final GroupEasyParameter group;

    public GroupInfoCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.group = new GroupEasyParameter());
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        GroupPermissions group = this.group.get();

        sender.sendMessage(ChatFormat.format("Information on group %s", ChatColor.LIGHT_PURPLE, group.name));
        // Print the amount of players in the group:
        Collection<String> list = Permissions.instance.getPermissionsManager().getPlayersInGroup(group.name);
        sender.sendMessage(ChatFormat.format("Number of players in group: %s", ChatColor.LIGHT_PURPLE, list.size()));
        // Print the groups that the group can rank to
        list = group.getAllRankables();
        String message = "Rankable groups: %s";

        for (int i = 1; i < list.size(); i++) {
            message += ", %s";
        }
        if (list.isEmpty()) {
            sender.sendMessage(ChatFormat.format(message, ChatColor.LIGHT_PURPLE, "none"));
        } else {
            sender.sendMessage(ChatFormat.format(message, ChatColor.LIGHT_PURPLE, list.toArray()));
        }
        // Print the groups the group inherits from directly
        list = group.getParents();
        message = "Direct parents: %s";

        for (int i = 1; i < list.size(); i++) {
            message += ", %s";
        }
        if (list.isEmpty()) {
            sender.sendMessage(ChatFormat.format(message, ChatColor.LIGHT_PURPLE, "none"));
        } else {
            sender.sendMessage(ChatFormat.format(message, ChatColor.LIGHT_PURPLE, list.toArray()));
        }
        // Print the groups the group inherits from indirectly
        {
            Collection<String> temp = list;
            list = new ArrayList<>(group.getAllGroupNames());
            list.removeAll(temp);
        }
        message = "Indirect parents: %s";

        for (int i = 1; i < list.size(); i++) {
            message += ", %s";
        }
        if (list.isEmpty()) {
            sender.sendMessage(ChatFormat.format(message, ChatColor.LIGHT_PURPLE, "none"));
        } else {
            sender.sendMessage(ChatFormat.format(message, ChatColor.LIGHT_PURPLE, list.toArray()));
        }
    }
}
