package me.heldplayer.permissions.command.group;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PermissionsManager;
import me.heldplayer.permissions.core.PlayerPermissions;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import net.specialattack.bukkit.core.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupEmptyCommand extends AbstractSubCommand {

    private final GroupEasyParameter group;

    public GroupEmptyCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.group = new GroupEasyParameter());
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        GroupPermissions group = this.group.get();

        PermissionsManager permissionsManager = Permissions.instance.getPermissionsManager();

        int count = 0;

        for (PlayerPermissions playerPermissions : permissionsManager.players) {
            if (playerPermissions.removeGroup(group)) {
                count++;
            }
        }

        sender.sendMessage(ChatFormat.format("Emptied group '%s', removed the group from %s players", ChatColor.GREEN, group.name, count));

        Permissions.instance.recalculatePermissions();

        try {
            Permissions.instance.savePermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }
}
