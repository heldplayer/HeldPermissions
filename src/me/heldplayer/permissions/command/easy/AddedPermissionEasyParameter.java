package me.heldplayer.permissions.command.easy;

import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.added.AddedPermission;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.command.CommandException;
import net.specialattack.bukkit.core.command.easy.parameter.AbstractEasyParameter;
import org.bukkit.command.CommandSender;

public class AddedPermissionEasyParameter extends AbstractEasyParameter<AddedPermission> {

    public AddedPermissionEasyParameter() {
        this.setName("permission");
    }

    @Override
    public boolean parse(CommandSender sender, String value) {
        AddedPermission result = Permissions.instance.getAddedPermissionsManager().getPermission(value);
        this.setValue(result);
        if (result == null) {
            throw new CommandException("%s is not defined as an added permission!", value);
        }
        return true;
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String input) {
        return TabHelper.tabAnyAddedPermission(input);
    }

    @Override
    public boolean takesAll() {
        return false;
    }

    public static class Child extends AbstractEasyParameter<String> {

        private final AddedPermissionEasyParameter source;

        public Child(AddedPermissionEasyParameter source) {
            this.setName("permission");
            this.source = source;
        }

        @Override
        public boolean parse(CommandSender sender, String value) {
            AddedPermission parent = this.source.getValue();
            for (String child : parent.children) {
                if (child.equalsIgnoreCase(value)) {
                    this.setValue(value);
                    return true;
                }
            }
            this.setValue(null);
            throw new CommandException("%s is not a child of %s!", value, parent.name);
        }

        @Override
        public List<String> getTabComplete(CommandSender sender, String input) {
            return TabHelper.tabAnyAddedChild(this.source.getValue(), input);
        }

        @Override
        public boolean takesAll() {
            return false;
        }

    }

}
