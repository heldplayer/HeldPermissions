package me.heldplayer.permissions.loader;

import me.heldplayer.permissions.core.PermissionsManager;
import org.bukkit.configuration.ConfigurationSection;

public interface IPermissionsLoader {

    boolean load(PermissionsManager manager, ConfigurationSection section);
}
