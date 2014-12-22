package me.heldplayer.permissions.command.player;

import java.util.Collection;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PlayerGroupsCommand extends AbstractSubCommand {

    public PlayerGroupsCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 1) {
            sender.sendMessage(Permissions.format("Expected %s parameter, no more, no less.", ChatColor.RED, 1));
            return;
        }

        String username = args[0];

        PlayerPermissions permissions = Permissions.instance.getManager().getPlayer(username);

        if (permissions == null) {
            sender.sendMessage(Permissions.format("Player %s does not exist", ChatColor.RED, username));

            return;
        }

        Collection<String> groups = permissions.getGroupNames();
        Collection<String> subGroups = permissions.getAllGroupNames();
        subGroups.removeAll(groups);

        String message = "Direct groups: ";

        for (int i = 0; i < groups.size(); i++) {
            if (i != 0) {
                message += ", ";
            }
            message += "%s";
        }

        sender.sendMessage(Permissions.format(message, ChatColor.GREEN, groups.toArray()));

        if (subGroups.size() > 0) {
            message = "Indirect groups: ";

            for (int i = 0; i < subGroups.size(); i++) {
                if (i != 0) {
                    message += ", ";
                }
                message += "%s";
            }

            sender.sendMessage(Permissions.format(message, ChatColor.GREEN, subGroups.toArray()));
        }
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        if (args.length == 1) {
            return null;
        }

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name + " <player>" };
    }

}
