package me.heldplayer.permissions.command.easy;

import java.util.ArrayList;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.command.CommandException;
import net.specialattack.bukkit.core.command.easy.EasyCollection;
import net.specialattack.bukkit.core.command.easy.parameter.AbstractEasyParameter;
import org.bukkit.command.CommandSender;

public class GroupCollectionEasyParameter extends AbstractEasyParameter.Multi<EasyCollection<GroupPermissions>> {

    public GroupCollectionEasyParameter() {
        this.setName("group");
    }

    @Override
    public boolean parse(CommandSender sender, String value) {
        List<GroupPermissions> result = new ArrayList<>();
        String[] split = value.split(" ");
        for (String str : split) {
            GroupPermissions group = Permissions.instance.getPermissionsManager().getGroup(str);
            if (group == null) {
                throw new CommandException("Group %s does not exist", str);
            }
            result.add(group);
        }

        if (result.isEmpty()) {
            this.setValue(null);
            return false;
        }

        this.setValue(new EasyCollection<>(result));
        return true;
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String input) {
        return TabHelper.tabAnyGroup();
    }
}
