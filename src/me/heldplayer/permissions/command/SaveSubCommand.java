package me.heldplayer.permissions.command;

import java.util.List;
import java.util.logging.Level;
import me.heldplayer.permissions.Permissions;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SaveSubCommand extends AbstractSubCommand {

    public SaveSubCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 0) {
            sender.sendMessage(ChatColor.RED + "Expected no parameters");
            return;
        }

        try {
            Permissions.instance.savePermissions();
            sender.sendMessage(ChatColor.GREEN + "Permissions saved!");
        } catch (Exception e) {
            Permissions.log.log(Level.SEVERE, "Error saving permissions", e);
            sender.sendMessage(ChatColor.RED + "There was a problem saving the permissions. Please check the console for more information.");
        }

        try {
            Permissions.instance.saveAddedPermissions();
            sender.sendMessage(ChatColor.GREEN + "Added permissions saved!");
        } catch (Exception e) {
            Permissions.log.log(Level.SEVERE, "Error saving added permissions", e);
            sender.sendMessage(ChatColor.RED + "There was a problem saving the added permissions. Please check the console for more information.");
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
