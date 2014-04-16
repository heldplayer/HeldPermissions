
package me.heldplayer.permissions.command;

import net.specialattack.bukkit.core.command.AbstractMultiCommand;
import net.specialattack.bukkit.core.command.HelpSubCommand;

public class PermissionsMainCommand extends AbstractMultiCommand {

    public PermissionsMainCommand() {
        new VersionSubCommand(this, "version", "permissions.command", "about");
        new ReloadSubCommand(this, "reload", "permissions.command.reload");
        new CheckSubCommand(this, "check", "permissions.command.check");
        new InfoSubCommand(this, "info", "permissions.command.info");
        new GroupSubCommand(this, "group", "permissions.command.group");
        new PlayerSubCommand(this, "player", "permissions.command.player");
        new HelpSubCommand(this, "help", "permissions.command.help", "?");
    }

    @Override
    public String getDefaultCommand() {
        return "version";
    }

}
