package me.heldplayer.permissions.core;

import java.util.Locale;
import javax.annotation.Nonnull;

public class Perm implements Comparable<Perm> {

    @Nonnull
    public final String permission;
    @Nonnull
    public final Value value;

    public Perm(@Nonnull String permission, @Nonnull Value value) {
        this.permission = permission.toLowerCase(Locale.ENGLISH);
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Perm perm = (Perm) o;

        return this.permission.equals(perm.permission);
    }

    @Override
    public int hashCode() {
        return this.permission.hashCode();
    }

    /*
     * NOTE: This compare function is not transitive.
     */
    @Override
    public int compareTo(@Nonnull Perm o) {
        // First check if the names match, if they do we want to say they're the same
        int permComp = this.permission.compareTo(o.permission);
        if (permComp == 0) {
            return 0;
        }

        // Otherwise check if they have the same value
        int valComp = this.value.compareTo(o.value);
        if (valComp != 0) {
            return valComp; // If they do return that
        }
        return permComp; // Otherwise return the value from comparing the names
    }

    public enum Value {
        ALLOW(true),
        DENY(false),
        NEVER(false);

        public final boolean value;

        Value(boolean value) {
            this.value = value;
        }
    }
}
