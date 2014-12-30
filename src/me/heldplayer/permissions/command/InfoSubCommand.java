package me.heldplayer.permissions.command;

import me.heldplayer.permissions.command.easy.PermissionEasyParameter;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import net.specialattack.bukkit.core.util.ChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class InfoSubCommand extends AbstractSubCommand {

    private final PermissionEasyParameter permission;

    public InfoSubCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.permission = new PermissionEasyParameter());
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        String permission = this.permission.getValue();
        Permission perm = Bukkit.getPluginManager().getPermission(permission);

        if (perm == null) {
            sender.sendMessage(ChatFormat.format("Unknown permission %s", ChatColor.RED, permission));
        } else {
            sender.sendMessage(ChatFormat.format("Info on permission %s:", ChatColor.GREEN, perm.getName()));
            sender.sendMessage(ChatFormat.format("Default: %s", ChatColor.GREEN, perm.getDefault()));
            if ((perm.getDescription() != null) && (perm.getDescription().length() > 0)) {
                sender.sendMessage(ChatFormat.format("Description: %s", ChatColor.GREEN, perm.getDescription()));
            }
            if ((perm.getChildren() != null) && (perm.getChildren().size() > 0)) {
                sender.sendMessage(ChatFormat.format("Children: %s", ChatColor.GREEN, perm.getChildren().size()));
            }
        }
    }

}
