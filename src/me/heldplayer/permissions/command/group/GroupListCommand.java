package me.heldplayer.permissions.command.group;

import java.util.Collection;
import me.heldplayer.permissions.Permissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupListCommand extends AbstractSubCommand {

    private final Permissions plugin;

    public GroupListCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        Collection<String> groups = this.plugin.getPermissionsManager().getAllGroupNames();

        String message = "Groups: %s";

        for (int i = 1; i < groups.size(); i++) {
            message += ", %s";
        }

        if (groups.isEmpty()) {
            sender.sendMessage(ChatFormat.format(message, ChatColor.GREEN, "none"));
        } else {
            sender.sendMessage(ChatFormat.format(message, ChatColor.GREEN, (Object[]) groups.toArray()));
            sender.sendMessage(ChatFormat.format("%s groups", ChatColor.GREEN, groups.size()));
        }
    }
}
