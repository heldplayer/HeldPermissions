package me.heldplayer.permissions.command;

import java.util.logging.Level;
import me.heldplayer.permissions.Permissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends AbstractSubCommand {

    private final Permissions plugin;

    public ReloadSubCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        try {
            this.plugin.loadPermissions();
            Exception error = this.plugin.getPermissionsManager().getError();
            if (error != null) {
                throw error;
            }
            sender.sendMessage(ChatColor.GREEN + "Permissions reloaded!");
        } catch (Exception e) {
            this.plugin.log.log(Level.SEVERE, "Error loading permissions", e);
            sender.sendMessage(ChatColor.RED + "There was a problem reloading the permissions. Please check the console for more information.");
        }

        try {
            this.plugin.loadAddedPermissions();
            sender.sendMessage(ChatColor.GREEN + "Added permissions reloaded!");
        } catch (Exception e) {
            this.plugin.log.log(Level.SEVERE, "Error loading added permissions", e);
            sender.sendMessage(ChatColor.RED + "There was a problem reloading the added permissions. Please check the console for more information.");
        }
    }
}
