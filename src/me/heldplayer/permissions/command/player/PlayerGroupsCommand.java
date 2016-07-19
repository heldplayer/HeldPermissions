package me.heldplayer.permissions.command.player;

import java.util.Collection;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AnyPlayerCollectionEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PlayerGroupsCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AnyPlayerCollectionEasyParameter players;

    public PlayerGroupsCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.players = new AnyPlayerCollectionEasyParameter().setTakeAll().setName("player"));
        this.finish();
    }

    @Override
    public void runCommand(final CommandSender sender) {
        List<String> players = this.players.get();

        players.forEach(player -> {
            PlayerPermissions permissions = this.plugin.getPermissionsManager().getPlayer(player);

            if (permissions == null) {
                sender.sendMessage(ChatFormat.format("%s does not exist", ChatColor.RED, player));
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

            sender.sendMessage(ChatFormat.format("Group information for %s", ChatColor.GREEN, player));
            sender.sendMessage(ChatFormat.format(message, ChatColor.GREEN, groups.toArray()));

            if (subGroups.size() > 0) {
                message = "Indirect groups: ";

                for (int i = 0; i < subGroups.size(); i++) {
                    if (i != 0) {
                        message += ", ";
                    }
                    message += "%s";
                }

                sender.sendMessage(ChatFormat.format(message, ChatColor.GREEN, subGroups.toArray()));
            }
        });
    }
}
