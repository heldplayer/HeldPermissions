package me.heldplayer.permissions.command.easy;

import java.util.List;
import java.util.function.Supplier;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.added.AddedPermission;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.spacore.api.command.CommandException;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import org.bukkit.command.CommandSender;

public class AddedPermissionEasyParameter extends AbstractEasyParameter<AddedPermission> {

    private final Permissions plugin;

    public AddedPermissionEasyParameter(Permissions plugin) {
        this.setName("permission");
        this.plugin = plugin;
    }

    @Override
    public boolean parse(CommandSender sender, String value) {
        AddedPermission result = this.plugin.getAddedPermissionsManager().getPermission(value);
        this.setValue(result);
        if (result == null) {
            throw new CommandException("%s is not defined as an added permission!", value);
        }
        return true;
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String input) {
        return TabHelper.tabAnyAddedPermission(this.plugin.getAddedPermissionsManager(), input);
    }

    @Override
    public boolean takesAll() {
        return false;
    }

    public static class Child extends AbstractEasyParameter<String> {

        private final Supplier<AddedPermission> source;

        public Child(Supplier<AddedPermission> source) {
            this.setName("permission");
            this.source = source;
        }

        @Override
        public boolean parse(CommandSender sender, String value) {
            AddedPermission parent = this.source.get();
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
            return TabHelper.tabAnyAddedChild(this.source.get(), input);
        }

        @Override
        public boolean takesAll() {
            return false;
        }
    }
}
