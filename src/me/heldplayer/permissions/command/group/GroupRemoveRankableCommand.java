package me.heldplayer.permissions.command.group;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupRemoveRankableCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final GroupEasyParameter group;
    private final GroupEasyParameter.Rankables rankable;

    public GroupRemoveRankableCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.group = new GroupEasyParameter(plugin));
        this.addParameter(this.rankable = new GroupEasyParameter.Rankables(plugin, this.group, true));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        GroupPermissions group = this.group.get();
        GroupPermissions rankable = this.rankable.get();

        if (!group.getAllRankables().contains(rankable.name)) {
            sender.sendMessage(ChatFormat.format("Group '%s' can't rank '%s'", ChatColor.RED, group.name, rankable.name));
            return;
        }

        group.removeRankable(rankable);
        sender.sendMessage(ChatFormat.format("Made '%s' unable to rank '%s'", ChatColor.GREEN, group.name, rankable.name));

        try {
            this.plugin.savePermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }
}
