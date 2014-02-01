
package me.heldplayer.permissions.core;

public class WorldPermissions extends BasePermissions {

    public final String worldname;

    public WorldPermissions(PermissionsManager manager, String name) {
        super(manager);
        this.worldname = name;
    }

}
