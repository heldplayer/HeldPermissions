package me.heldplayer.permissions.command.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PlayerRemoveGroupCommand extends AbstractSubCommand {

    public PlayerRemoveGroupCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length < 2) {
            sender.sendMessage(Permissions.format("Expected %s parameters or more.", ChatColor.RED, 2));
            return;
        }

        String username = args[0];

        PlayerPermissions permissions = Permissions.instance.getPermissionsManager().getPlayer(username);

        if (permissions == null) {
            sender.sendMessage(Permissions.format("Player %s does not exist", ChatColor.RED, username));

            return;
        }

        List<GroupPermissions> groups = new ArrayList<GroupPermissions>(permissions.getGroups());
        Set<String> removed = new TreeSet<String>();

        String message = "Removed groups: ";

        boolean changed = false;

        for (int i = 1; i < args.length; i++) {
            GroupPermissions group = Permissions.instance.getPermissionsManager().getGroup(args[i]);
            if (group == null) {
                sender.sendMessage(Permissions.format("Unknown group %s", ChatColor.RED, args[i]));
                return;
            }
            if (groups.contains(group)) {
                groups.remove(group);
                removed.add(args[i]);

                if (changed) {
                    message += ", ";
                }
                message += "%s";

                changed = true;
            }
        }

        if (changed) {
            permissions.setGroups(groups);

            sender.sendMessage(Permissions.format(message, ChatColor.GREEN, removed.toArray()));

            try {
                Permissions.instance.savePermissions();
            } catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
            }

            Permissions.instance.recalculatePermissions(username);
        } else {
            sender.sendMessage(ChatColor.RED + "The player was not in any of these groups");
        }
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        if (args.length == 1) {
            return null;
        }

        return TabHelper.tabAnyGroupIn(Permissions.instance.getPermissionsManager().getPlayer(args[0]));
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name + " <player> <group> [group2 [group3 [...]]]" };
    }

}
