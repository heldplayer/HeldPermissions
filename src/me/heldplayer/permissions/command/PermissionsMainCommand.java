package me.heldplayer.permissions.command;

import me.heldplayer.permissions.Permissions;
import net.specialattack.bukkit.core.command.AbstractMultiCommand;
import net.specialattack.bukkit.core.command.HelpSubCommand;
import net.specialattack.bukkit.core.command.VersionSubCommand;

public class PermissionsMainCommand extends AbstractMultiCommand {

    public PermissionsMainCommand() {
        new VersionSubCommand(this, Permissions.instance.getDescription(), "version", "permissions.command", "about");
        new ReloadSubCommand(this, "reload", "permissions.command.reload");
        new CheckSubCommand(this, "check", "permissions.command.check");
        new InfoSubCommand(this, "info", "permissions.command.info");
        new GroupSubCommand(this, "group", "permissions.command.group");
        new PlayerSubCommand(this, "player", "permissions.command.player");
        new HelpSubCommand(this, "help", "permissions.command.help", "?");
        new SaveSubCommand(this, "save", "permissions.command.save");
    }

    @Override
    public String getDefaultCommand() {
        return "version";
    }

}
