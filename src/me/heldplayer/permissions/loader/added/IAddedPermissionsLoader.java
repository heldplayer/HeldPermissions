package me.heldplayer.permissions.loader.added;

import me.heldplayer.permissions.core.added.AddedPermissionsManager;
import org.bukkit.configuration.ConfigurationSection;

public interface IAddedPermissionsLoader {

    void load(AddedPermissionsManager manager, ConfigurationSection section);
}
