
package me.heldplayer.permissions.command;

import java.util.List;

import me.heldplayer.permissions.Permissions;
import net.specialattack.core.command.AbstractMultiCommand;
import net.specialattack.core.command.AbstractSubCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class VersionSubCommand extends AbstractSubCommand {

    private final String permission;

    public VersionSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        this.permission = permissions;
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        sender.sendMessage(ChatColor.GRAY + "========== " + ChatColor.GREEN + Permissions.instance.getDescription().getFullName() + ChatColor.GRAY + " ==========");
        sender.sendMessage(ChatColor.YELLOW + "Authors: " + ChatColor.GRAY + " heldplayer");
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        if (sender.hasPermission("permissions.command.*")) {
            return true;
        }

        return sender.hasPermission(permission);
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage() {
        return new String[] { this.name };
    }

}
