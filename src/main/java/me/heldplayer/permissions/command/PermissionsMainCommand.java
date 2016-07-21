package me.heldplayer.permissions.command;

import me.heldplayer.permissions.Consts;
import me.heldplayer.permissions.Permissions;
import net.specialattack.spacore.api.command.AbstractMultiCommand;
import net.specialattack.spacore.command.HelpSubCommand;
import net.specialattack.spacore.command.VersionSubCommand;

public class PermissionsMainCommand extends AbstractMultiCommand {

    public PermissionsMainCommand(Permissions plugin) {
        new VersionSubCommand(this, plugin.getDescription(), "version", Consts.PERM_COMMAND_VERSION, "about");
        new ReloadSubCommand(this, plugin, "reload", Consts.PERM_COMMAND_RELOAD);
        new SaveSubCommand(this, plugin, "save", Consts.PERM_COMMAND_SAVE);
        new CheckSubCommand(this, plugin, "check", Consts.PERM_COMMAND_CHECK);
        new InfoSubCommand(this, plugin, "info", Consts.PERM_COMMAND_INFO);
        new GroupSubCommand(this, plugin, "group", Consts.PERM_COMMAND_GROUP);
        new PlayerSubCommand(this, plugin, "player", Consts.PERM_COMMAND_PLAYER);
        new NodeSubCommand(this, plugin, "node", Consts.PERM_COMMAND_NODE);
        new HelpSubCommand(this, "help", Consts.PERM_COMMAND_HELP, "?");
    }

    @Override
    public String getDefaultCommand() {
        return "version";
    }
}
