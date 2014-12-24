package me.heldplayer.permissions.command;

import java.util.List;
import java.util.logging.Level;
import me.heldplayer.permissions.Permissions;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends AbstractSubCommand {

    public ReloadSubCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 0) {
            sender.sendMessage(ChatColor.RED + "Expected no parameters");
            return;
        }

        try {
            Permissions.instance.loadPermissions();
            sender.sendMessage(ChatColor.GREEN + "Permissions reloaded!");
        } catch (Exception e) {
            Permissions.log.log(Level.SEVERE, "Error loading permissions", e);
            sender.sendMessage(ChatColor.RED + "There was a problem reloading the permissions. Please check the console for more information.");
        }

        try {
            Permissions.instance.loadAddedPermissions();
            sender.sendMessage(ChatColor.GREEN + "Added permissions reloaded!");
        } catch (Exception e) {
            Permissions.log.log(Level.SEVERE, "Error loading added permissions", e);
            sender.sendMessage(ChatColor.RED + "There was a problem reloading the added permissions. Please check the console for more information.");
        }
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name };
    }

}
