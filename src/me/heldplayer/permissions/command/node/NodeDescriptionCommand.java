package me.heldplayer.permissions.command.node;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.AddedPermissionEasyParameter;
import me.heldplayer.permissions.core.added.AddedPermission;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import net.specialattack.bukkit.core.command.easy.parameter.StringEasyParameter;
import net.specialattack.bukkit.core.util.ChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class NodeDescriptionCommand extends AbstractSubCommand {

    private final AddedPermissionEasyParameter permission;
    private final StringEasyParameter description;

    public NodeDescriptionCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.permission = new AddedPermissionEasyParameter());
        this.addParameter(this.description = new StringEasyParameter().setTakeAll().setName("description"));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        AddedPermission permission = this.permission.getValue();
        String description = this.description.getValue();

        Permission node = Bukkit.getPluginManager().getPermission(permission.name);

        if (node == null) {
            sender.sendMessage(ChatFormat.format("Permission '%s' doesn't exist?!", ChatColor.RED, permission.name));
            return;
        }

        permission.description = description;
        node.setDescription(description);

        sender.sendMessage(ChatFormat.format("Changed the description of '%s' to '%s'", ChatColor.GREEN, permission.name, description));

        try {
            Permissions.instance.saveAddedPermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }

}
