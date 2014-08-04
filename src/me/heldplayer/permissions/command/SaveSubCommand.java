
package me.heldplayer.permissions.command;

import java.util.List;
import java.util.logging.Level;

import me.heldplayer.permissions.Permissions;
import net.specialattack.bukkit.core.command.AbstractMultiCommand;
import net.specialattack.bukkit.core.command.AbstractSubCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SaveSubCommand extends AbstractSubCommand {

    private final String permission;

    public SaveSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        this.permission = permissions;
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        try {
            Permissions.instance.savePermissions();
            sender.sendMessage(ChatColor.GREEN + "Permissions saved!");
        }
        catch (Exception e) {
            Permissions.log.log(Level.SEVERE, "Error saving permissions", e);
            sender.sendMessage(ChatColor.RED + "There was a problem saving the permissions. Please check the console for more information.");
        }
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        if (sender.hasPermission("permissions.command.*")) {
            return true;
        }

        return sender.hasPermission(this.permission);
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage() {
        return new String[] { this.name };
    }

}
