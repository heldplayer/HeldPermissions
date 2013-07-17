
package me.heldplayer.permissions.command;

import net.specialattack.core.command.AbstractMultiCommand;

public class PermissionsMainCommand extends AbstractMultiCommand {

    public PermissionsMainCommand() {
        new VersionSubCommand(this, "version", "permissions.command", "about");
        new ReloadSubCommand(this, "reload", "permissions.command.reload");
        new CheckSubCommand(this, "check", "permissions.command.check");
        new InfoSubCommand(this, "info", "permissions.command.info");
        new GroupSubCommand(this, "group", "permissions.command.group");
        new PlayerSubCommand(this, "player", "permissions.command.player");
    }

    @Override
    public String getDefaultCommand() {
        return "version";
    }

}
