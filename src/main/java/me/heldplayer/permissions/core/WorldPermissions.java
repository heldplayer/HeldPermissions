package me.heldplayer.permissions.core;

import javax.annotation.Nonnull;

public class WorldPermissions extends BasePermissions implements Comparable<WorldPermissions> {

    public final String worldname;

    public WorldPermissions(PermissionsManager manager, String name) {
        super(manager);
        this.worldname = name;
    }

    @Override
    public int compareTo(@Nonnull WorldPermissions other) {
        return this.worldname.compareTo(other.worldname);
    }
}
