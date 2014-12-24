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

public class PlayerSetGroupCommand extends AbstractSubCommand {

    public PlayerSetGroupCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
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

        List<GroupPermissions> groups = new ArrayList<GroupPermissions>();
        Set<String> groupNames = new TreeSet<String>();

        String message = "New groups: %s";

        GroupPermissions group = Permissions.instance.getPermissionsManager().getGroup(args[1]);
        if (group == null) {
            sender.sendMessage(Permissions.format("Unknown group %s", ChatColor.RED, args[1]));
            return;
        }
        groups.add(group);
        groupNames.add(args[1]);

        for (int i = 2; i < args.length; i++) {
            message += ", %s";
            group = Permissions.instance.getPermissionsManager().getGroup(args[i]);
            if (group == null) {
                sender.sendMessage(Permissions.format("Unknown group %s", ChatColor.RED, args[i]));
                return;
            }
            groups.add(group);
            groupNames.add(args[i]);
        }

        permissions.setGroups(groups);

        sender.sendMessage(Permissions.format(message, ChatColor.GREEN, groupNames.toArray()));

        try {
            Permissions.instance.savePermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }

        Permissions.instance.recalculatePermissions(username);
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        if (args.length == 1) {
            return null;
        }

        return TabHelper.tabAnyGroup();
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name + " <player> <group> [group2 [group3 [...]]]" };
    }

}
