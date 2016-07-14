package me.heldplayer.permissions.command.easy;

import java.util.List;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.command.easy.parameter.AbstractEasyParameter;
import org.bukkit.command.CommandSender;

public class PermissionEasyParameter extends AbstractEasyParameter<String> {

    public PermissionEasyParameter() {
        this.setName("permission");
    }

    @Override
    public boolean parse(CommandSender sender, String value) {
        this.setValue(value);

        return true;
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String input) {
        return TabHelper.tabAnyPermission(input);
    }

    @Override
    public boolean takesAll() {
        return false;
    }
}
