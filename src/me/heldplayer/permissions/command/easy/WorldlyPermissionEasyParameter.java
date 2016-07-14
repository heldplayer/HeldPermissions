package me.heldplayer.permissions.command.easy;

import java.util.List;
import java.util.function.Supplier;
import me.heldplayer.permissions.core.WorldlyPermissions;
import me.heldplayer.permissions.util.TabHelper;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.bukkit.core.command.easy.parameter.AbstractEasyParameter;
import org.bukkit.command.CommandSender;

public class WorldlyPermissionEasyParameter extends AbstractEasyParameter<WorldlyPermission> {

    public WorldlyPermissionEasyParameter() {
        this.setName("permission");
    }

    @Override
    public boolean parse(CommandSender sender, String value) {
        this.setValue(new WorldlyPermission(value));

        return true;
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String input) {
        return TabHelper.tabAnyPermissionWorldly(input);
    }

    public static class Only extends AbstractEasyParameter<WorldlyPermission> {

        private final Supplier<? extends WorldlyPermissions> source;

        public Only(Supplier<? extends WorldlyPermissions> source) {
            this.setName("permission");
            this.source = source;
        }

        @Override
        public boolean parse(CommandSender sender, String value) {
            this.setValue(new WorldlyPermission(value));

            return true;
        }

        @Override
        public List<String> getTabComplete(CommandSender sender, String input) {
            String world = input.indexOf(':') < 0 ? "" : input.substring(0, input.indexOf(':'));
            if (world.isEmpty()) {
                return TabHelper.tabSetPermission(input, this.source.get());
            } else {
                return TabHelper.tabSetPermission(input, this.source.get().getWorldPermissions(world));
            }
        }
    }
}
