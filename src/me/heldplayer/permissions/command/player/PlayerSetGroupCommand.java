package me.heldplayer.permissions.command.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupCollectionEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import net.specialattack.bukkit.core.command.easy.EasyCollection;
import net.specialattack.bukkit.core.command.easy.parameter.AnyPlayerCollectionEasyParameter;
import net.specialattack.bukkit.core.util.ChatFormat;
import net.specialattack.bukkit.core.util.Container;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PlayerSetGroupCommand extends AbstractSubCommand {

    private final AnyPlayerCollectionEasyParameter players;
    private final GroupCollectionEasyParameter groups;

    public PlayerSetGroupCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.players = new AnyPlayerCollectionEasyParameter().setName("players"));
        this.addParameter(this.groups = new GroupCollectionEasyParameter().setTakeAll());
        this.finish();
    }

    @Override
    public void runCommand(final CommandSender sender) {
        EasyCollection<String> players = this.players.get();
        final EasyCollection<GroupPermissions> groups = this.groups.get();

        players.forEach(player -> {
            PlayerPermissions permissions = Permissions.instance.getPermissionsManager().getPlayer(player);

            if (permissions == null) {
                sender.sendMessage(ChatFormat.format("%s does not exist", ChatColor.RED, player));
                return;
            }

            final List<GroupPermissions> playerGroups = new ArrayList<>();
            final List<String> groupNames = new ArrayList<>();

            final StringBuilder message = new StringBuilder("New groups: ");

            final Container<Boolean> changed = new Container<>(false);

            groups.forEach(group -> {
                if (!playerGroups.contains(group)) {
                    playerGroups.add(group);
                    groupNames.add(group.name);

                    if (changed.value) {
                        message.append(", ");
                    }
                    message.append("%s");

                    changed.value = true;
                }
            });

            permissions.setGroups(playerGroups);

            sender.sendMessage(ChatFormat.format("Set %s groups for player %s", ChatColor.GREEN, groupNames.size(), player));
            sender.sendMessage(ChatFormat.format(message.toString(), ChatColor.GREEN, groupNames.toArray()));

            try {
                Permissions.instance.savePermissions();
            } catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
            }

            Permissions.instance.recalculatePermissions(player);
        });
    }
}
