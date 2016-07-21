package me.heldplayer.permissions.util;

public class WorldlyPermission {

    public String world;
    public String permission;

    public WorldlyPermission(String input) {
        this.world = null;
        this.permission = input;
        if (this.permission.indexOf(":") > 0) {
            this.world = this.permission.split(":", 2)[0];
            this.permission = this.permission.split(":", 2)[1];
        } else {
            if (this.permission.indexOf(":") == 0) {
                this.permission = this.permission.substring(1);
            }
        }
    }

    @Override
    public String toString() {
        return (this.world == null ? "" : this.world) + ":" + this.permission;
    }
}
