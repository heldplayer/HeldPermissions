
package me.heldplayer.permissions.core;

import org.bukkit.configuration.ConfigurationSection;

public interface IPermissionsLoader {

    void load(PermissionsManager manager, ConfigurationSection section);

}
