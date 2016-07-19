package me.heldplayer.permissions.command;

import java.util.logging.Level;
import me.heldplayer.permissions.Permissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SaveSubCommand extends AbstractSubCommand {

    private final Permissions plugin;

    public SaveSubCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        try {
            this.plugin.savePermissions();
            sender.sendMessage(ChatColor.GREEN + "Permissions saved!");
        } catch (Exception e) {
            this.plugin.log.log(Level.SEVERE, "Error saving permissions", e);
            sender.sendMessage(ChatColor.RED + "There was a problem saving the permissions. Please check the console for more information.");
        }

        try {
            this.plugin.saveAddedPermissions();
            sender.sendMessage(ChatColor.GREEN + "Added permissions saved!");
        } catch (Exception e) {
            this.plugin.log.log(Level.SEVERE, "Error saving added permissions", e);
            sender.sendMessage(ChatColor.RED + "There was a problem saving the added permissions. Please check the console for more information.");
        }
    }
}
