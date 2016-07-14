package me.heldplayer.permissions.command.group;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import net.specialattack.bukkit.core.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupAddRankableCommand extends AbstractSubCommand {

    private final GroupEasyParameter group;
    private final GroupEasyParameter.Rankables rankable;

    public GroupAddRankableCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.group = new GroupEasyParameter());
        this.addParameter(this.rankable = new GroupEasyParameter.Rankables(this.group, false));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        GroupPermissions group = this.group.get();
        GroupPermissions rankable = this.rankable.get();

        if (group.getAllRankables().contains(rankable.name)) {
            sender.sendMessage(ChatFormat.format("Group '%s' can already rank '%s'", ChatColor.RED, group.name, rankable.name));
            return;
        }

        group.addRankable(rankable);
        sender.sendMessage(ChatFormat.format("Made '%s' able to rank '%s'", ChatColor.GREEN, group.name, rankable.name));

        try {
            Permissions.instance.savePermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }
}
