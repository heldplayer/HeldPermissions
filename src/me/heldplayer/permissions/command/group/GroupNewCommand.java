package me.heldplayer.permissions.command.group;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PermissionsManager;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import net.specialattack.bukkit.core.command.easy.parameter.StringEasyParameter;
import net.specialattack.bukkit.core.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupNewCommand extends AbstractSubCommand {

    private final StringEasyParameter group;

    public GroupNewCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.group = new StringEasyParameter().setName("name"));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        String group = this.group.get();

        PermissionsManager permissionsManager = Permissions.instance.getPermissionsManager();
        GroupPermissions permissions = permissionsManager.getGroup(group);

        if (permissions != null) {
            sender.sendMessage(ChatFormat.format("Group '%s' already exists", ChatColor.RED, group));
            return;
        }

        permissions = new GroupPermissions(permissionsManager, group);
        permissionsManager.addGroup(permissions);

        sender.sendMessage(ChatFormat.format("Created a new empty group '%s'", ChatColor.GREEN, group));

        try {
            Permissions.instance.savePermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }
}
