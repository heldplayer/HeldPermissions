package me.heldplayer.permissions.command.group;

import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PermissionsManager;
import me.heldplayer.permissions.core.PlayerPermissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupEmptyCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<GroupPermissions> group;

    public GroupEmptyCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.group = new GroupEasyParameter(plugin));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        GroupPermissions group = this.group.get();

        PermissionsManager permissionsManager = this.plugin.getPermissionsManager();

        int count = 0;

        for (PlayerPermissions playerPermissions : permissionsManager.players) {
            if (playerPermissions.removeGroup(group)) {
                count++;
            }
        }

        sender.sendMessage(ChatFormat.format("Emptied group '%s', removed the group from %s players", ChatColor.GREEN, group.name, count));

        this.plugin.recalculatePermissions();

        this.plugin.savePermissionsBy(sender);
    }
}
