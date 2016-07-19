package me.heldplayer.permissions.command;

import me.heldplayer.permissions.Permissions;
import net.specialattack.spacore.api.command.AbstractMultiCommand;
import net.specialattack.spacore.command.HelpSubCommand;
import net.specialattack.spacore.command.VersionSubCommand;

public class PermissionsMainCommand extends AbstractMultiCommand {

    public PermissionsMainCommand(Permissions plugin) {
        new VersionSubCommand(this, plugin.getDescription(), "version", "permissions.command", "about");
        new ReloadSubCommand(this, plugin, "reload", "permissions.command.reload");
        new SaveSubCommand(this, plugin, "save", "permissions.command.save");
        new CheckSubCommand(this, plugin, "check", "permissions.command.check");
        new InfoSubCommand(this, plugin, "info", "permissions.command.info");
        new GroupSubCommand(this, plugin, "group", "permissions.command.group");
        new PlayerSubCommand(this, plugin, "player", "permissions.command.player");
        new NodeSubCommand(this, plugin, "node", "permissions.command.node");
        new HelpSubCommand(this, "help", "permissions.command.help", "?");
    }

    @Override
    public String getDefaultCommand() {
        return "version";
    }
}
