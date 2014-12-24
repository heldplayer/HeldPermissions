package me.heldplayer.permissions.command.group;

import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupPlayersCommand extends AbstractSubCommand {

    public GroupPlayersCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 1) {
            sender.sendMessage(Permissions.format("Expected %s parameter, no more, no less.", ChatColor.RED, 1));
            return;
        }

        List<String> players = Permissions.instance.getPermissionsManager().getPlayersInGroup(args[0]);

        String message = "Players in group: %s";

        for (int i = 1; i < players.size(); i++) {
            message += ", %s";
        }

        if (players.isEmpty()) {
            sender.sendMessage(Permissions.format(message, ChatColor.GREEN, "none"));
        } else {
            sender.sendMessage(Permissions.format(message, ChatColor.GREEN, players.toArray()));
            sender.sendMessage(Permissions.format("%s players", ChatColor.GREEN, players.size()));
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
