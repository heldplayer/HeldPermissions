package me.heldplayer.permissions.command;

import java.util.logging.Level;
import me.heldplayer.permissions.Consts;
import me.heldplayer.permissions.Permissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.util.ChatUtil;
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
        Permissions.notify(ChatUtil.constructMessage("Reloading permissions..."), sender, Consts.PERM_LISTEN_CONFIG);

        try {
            this.plugin.loadPermissions();
            Exception error = this.plugin.getPermissionsManager().getError();
            if (error != null) {
                throw error;
            }

            Permissions.notify(ChatUtil.constructMessage(ChatColor.GREEN, "Permissions reloaded!"), sender, Consts.PERM_LISTEN_CONFIG);
        } catch (Exception e) {
            this.plugin.log.log(Level.SEVERE, "Error reloading permissions", e);

            Permissions.notify(ChatUtil.constructMessage(ChatColor.RED,
                    "There was a problem reloading the permissions. Please check the console for more information."), sender, Consts.PERM_LISTEN_CONFIG);
        }

        Permissions.notify(ChatUtil.constructMessage("Reloading added permissions..."), sender, Consts.PERM_LISTEN_CONFIG);
        try {
            this.plugin.loadAddedPermissions();

            Permissions.notify(ChatUtil.constructMessage(ChatColor.GREEN, "Added permissions reloaded!"), sender, Consts.PERM_LISTEN_CONFIG);
        } catch (Exception e) {
            this.plugin.log.log(Level.SEVERE, "Error reloading added permissions", e);

            Permissions.notify(ChatUtil.constructMessage(ChatColor.RED,
                    "There was a problem reloading the added permissions. Please check the console for more information."), sender, Consts.PERM_LISTEN_CONFIG);
        }
    }
}
