package me.heldplayer.permissions.command.group;

import java.util.Collection;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupListCommand extends AbstractSubCommand {

    public GroupListCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 0) {
            sender.sendMessage(ChatColor.RED + "Expected no parameters");
            return;
        }

        Collection<String> groups = Permissions.instance.getPermissionsManager().getAllGroupNames();

        String message = "Groups: %s";

        for (int i = 1; i < groups.size(); i++) {
            message += ", %s";
        }

        if (groups.isEmpty()) {
            sender.sendMessage(Permissions.format(message, ChatColor.GREEN, "none"));
        } else {
            sender.sendMessage(Permissions.format(message, ChatColor.GREEN, (Object[]) groups.toArray()));
            sender.sendMessage(Permissions.format("%s groups", ChatColor.GREEN, groups.size()));
        }
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name };
    }

}
