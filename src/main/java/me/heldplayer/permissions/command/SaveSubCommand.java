package me.heldplayer.permissions.command;

import java.util.logging.Level;
import me.heldplayer.permissions.Consts;
import me.heldplayer.permissions.Permissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.util.ChatUtil;
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
        Permissions.notify(ChatUtil.constructMessage("Saving permissions..."), sender, Consts.PERM_LISTEN_CONFIG);

        try {
            this.plugin.savePermissions();
            Exception error = this.plugin.getPermissionsManager().getError();
            if (error != null) {
                throw error;
            }

            Permissions.notify(ChatUtil.constructMessage(ChatColor.GREEN, "Permissions saved!"), sender, Consts.PERM_LISTEN_CONFIG);
        } catch (Exception e) {
            this.plugin.log.log(Level.SEVERE, "Error saving permissions", e);

            Permissions.notify(ChatUtil.constructMessage(ChatColor.RED,
                    "There was a problem saving the permissions. Please check the console for more information."), sender, Consts.PERM_LISTEN_CONFIG);
        }

        Permissions.notify(ChatUtil.constructMessage("Saving added permissions..."), sender, Consts.PERM_LISTEN_CONFIG);

        try {
            this.plugin.saveAddedPermissions();

            Permissions.notify(ChatUtil.constructMessage(ChatColor.GREEN, "Added permissions saved!"), sender, Consts.PERM_LISTEN_CONFIG);
        } catch (Exception e) {
            this.plugin.log.log(Level.SEVERE, "Error saving added permissions", e);

            Permissions.notify(ChatUtil.constructMessage(ChatColor.RED,
                    "There was a problem saving the added permissions. Please check the console for more information."), sender, Consts.PERM_LISTEN_CONFIG);
        }
    }
}
