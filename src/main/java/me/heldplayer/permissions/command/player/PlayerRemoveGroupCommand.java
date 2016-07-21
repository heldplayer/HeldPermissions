package me.heldplayer.permissions.command.player;

import java.util.ArrayList;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupCollectionEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.api.command.parameter.AnyPlayerCollectionEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import net.specialattack.spacore.util.Container;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PlayerRemoveGroupCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<List<String>> players;
    private final AbstractEasyParameter<List<GroupPermissions>> groups;

    public PlayerRemoveGroupCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.players = new AnyPlayerCollectionEasyParameter().setName("players"));
        this.addParameter(this.groups = new GroupCollectionEasyParameter(plugin).setTakeAll());
        this.finish();
    }

    @Override
    public void runCommand(final CommandSender sender) {
        List<String> players = this.players.get();
        final List<GroupPermissions> groups = this.groups.get();

        players.forEach(player -> {
            PlayerPermissions permissions = this.plugin.getPermissionsManager().getPlayer(player);

            if (permissions == null) {
                sender.sendMessage(ChatFormat.format("%s does not exist", ChatColor.RED, player));
                return;
            }

            final List<GroupPermissions> playerGroups = new ArrayList<>(permissions.getGroups());
            final List<String> removed = new ArrayList<>();

            final StringBuilder message = new StringBuilder("Removed groups: ");

            final Container<Boolean> changed = new Container<>(false);

            groups.forEach(group -> {
                if (playerGroups.contains(group)) {
                    playerGroups.remove(group);
                    removed.add(group.name);

                    if (changed.value) {
                        message.append(", ");
                    }
                    message.append("%s");

                    changed.value = true;
                }
            });

            if (changed.value) {
                permissions.setGroups(playerGroups);

                sender.sendMessage(ChatFormat.format("Removed %s groups from player %s", ChatColor.GREEN, removed.size(), player));
                sender.sendMessage(ChatFormat.format(message.toString(), ChatColor.GREEN, removed.toArray()));

                this.plugin.savePermissionsBy(sender);

                this.plugin.recalculatePermissions(player);
            } else {
                sender.sendMessage(ChatFormat.format("%s was not in any of the groups!", ChatColor.RED, player));
            }
        });
    }
}
