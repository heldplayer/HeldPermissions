package me.heldplayer.permissions.command.group;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PermissionsManager;
import me.heldplayer.permissions.core.PlayerPermissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupDeleteCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final GroupEasyParameter group;

    public GroupDeleteCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.group = new GroupEasyParameter(plugin));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        GroupPermissions group = this.group.get();

        PermissionsManager permissionsManager = this.plugin.getPermissionsManager();

        permissionsManager.removeGroup(group);

        for (GroupPermissions groupPermissions : permissionsManager.groups) {
            groupPermissions.removeParent(group);
        }

        for (PlayerPermissions playerPermissions : permissionsManager.players) {
            playerPermissions.removeGroup(group);
        }

        group.release();

        sender.sendMessage(ChatFormat.format("Removed group '%s'", ChatColor.GREEN, group.name));

        this.plugin.recalculatePermissions();

        try {
            this.plugin.savePermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }
}
