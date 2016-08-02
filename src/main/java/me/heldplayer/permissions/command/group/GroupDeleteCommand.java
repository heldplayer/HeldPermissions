package me.heldplayer.permissions.command.group;

import me.heldplayer.permissions.Consts;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PermissionsManager;
import me.heldplayer.permissions.core.PlayerPermissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.CommandException;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupDeleteCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<GroupPermissions> group;

    public GroupDeleteCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.group = new GroupEasyParameter(plugin));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        GroupPermissions group = this.group.get();

        if (group == null) {
            // Should never happen
            throw new CommandException("Group does not exist!");
        }

        PermissionsManager permissionsManager = this.plugin.getPermissionsManager();

        permissionsManager.removeGroup(group);

        for (GroupPermissions groupPermissions : permissionsManager.groups) {
            groupPermissions.removeParent(group);
        }

        for (PlayerPermissions playerPermissions : permissionsManager.players) {
            playerPermissions.removeGroup(group);
        }

        group.release();

        Permissions.notify(ChatUtil.constructMessage(ChatColor.GREEN, "Removed group '", ChatColor.WHITE,
                group.name, ChatColor.RESET, "'"), sender, Consts.PERM_LISTEN_CONFIG);

        this.plugin.recalculatePermissions();

        this.plugin.savePermissionsBy(sender);
    }
}
