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
import net.specialattack.bukkit.core.util.Function;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PlayerAddGroupCommand extends AbstractSubCommand {

    private final AnyPlayerCollectionEasyParameter players;
    private final GroupCollectionEasyParameter groups;

    public PlayerAddGroupCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.players = new AnyPlayerCollectionEasyParameter().setName("players"));
        this.addParameter(this.groups = new GroupCollectionEasyParameter().setTakeAll());
        this.finish();
    }

    @Override
    public void runCommand(final CommandSender sender) {
        EasyCollection<String> players = this.players.getValue();
        final EasyCollection<GroupPermissions> groups = this.groups.getValue();

        players.forEach(new Function<String>() {
            @Override
            public void run(String player) {
                PlayerPermissions permissions = Permissions.instance.getPermissionsManager().getPlayer(player);

                if (permissions == null) {
                    sender.sendMessage(ChatFormat.format("%s does not exist", ChatColor.RED, player));
                    return;
                }

                final List<GroupPermissions> playerGroups = new ArrayList<GroupPermissions>(permissions.getGroups());
                final List<String> added = new ArrayList<String>();

                final StringBuilder message = new StringBuilder("Added groups: ");

                final Container<Boolean> changed = new Container<Boolean>(false);

                groups.forEach(new Function<GroupPermissions>() {
                    @Override
                    public void run(GroupPermissions group) {
                        if (!playerGroups.contains(group)) {
                            playerGroups.add(group);
                            added.add(group.name);

                            if (changed.value) {
                                message.append(", ");
                            }
                            message.append("%s");

                            changed.value = true;
                        }
                    }
                });

                if (changed.value) {
                    permissions.setGroups(playerGroups);

                    sender.sendMessage(ChatFormat.format("Added %s groups for player %s", ChatColor.GREEN, added.size(), player));
                    sender.sendMessage(ChatFormat.format(message.toString(), ChatColor.GREEN, added.toArray()));

                    try {
                        Permissions.instance.savePermissions();
                    } catch (IOException e) {
                        sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
                    }

                    Permissions.instance.recalculatePermissions(player);
                } else {
                    sender.sendMessage(ChatFormat.format("%s is already in all the groups!", ChatColor.RED, player));
                }
            }
        });
    }

}
