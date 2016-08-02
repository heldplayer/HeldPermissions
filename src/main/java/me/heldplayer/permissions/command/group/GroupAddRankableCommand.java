package me.heldplayer.permissions.command.group;

import me.heldplayer.permissions.Consts;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import net.specialattack.spacore.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupAddRankableCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<GroupPermissions> group;
    private final AbstractEasyParameter<GroupPermissions> rankable;

    public GroupAddRankableCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.group = new GroupEasyParameter(plugin));
        this.addParameter(this.rankable = new GroupEasyParameter.Rankables(plugin, this.group, false));
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
        Permissions.notify(ChatUtil.constructMessage(ChatColor.GREEN, "Made '", ChatColor.WHITE,
                group.name, ChatColor.RESET, "' able to rank '", ChatColor.WHITE,
                rankable.name, ChatColor.RESET, "'"), sender, Consts.PERM_LISTEN_CONFIG);

        this.plugin.savePermissionsBy(sender);
    }
}
