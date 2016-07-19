package me.heldplayer.permissions.command.group;

import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupPlayersCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final GroupEasyParameter group;

    public GroupPlayersCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.group = new GroupEasyParameter(plugin));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        GroupPermissions group = this.group.get();

        List<String> players = this.plugin.getPermissionsManager().getPlayersInGroup(group.name);

        String message = "Players in group: %s";

        for (int i = 1; i < players.size(); i++) {
            message += ", %s";
        }

        if (players.isEmpty()) {
            sender.sendMessage(ChatFormat.format(message, ChatColor.GREEN, "none"));
        } else {
            sender.sendMessage(ChatFormat.format(message, ChatColor.GREEN, players.toArray()));
            sender.sendMessage(ChatFormat.format("%s players", ChatColor.GREEN, players.size()));
        }
    }
}
