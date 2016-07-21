package me.heldplayer.permissions.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class PermTest {

    @Test
    public void diffNameAllow() {
        Perm perm1 = new Perm("test.permission1", Perm.Value.ALLOW);
        Perm perm2 = new Perm("test.permission2", Perm.Value.ALLOW);
        assertEquals(perm1.compareTo(perm2), -perm2.compareTo(perm1));
        assertFalse(perm1.equals(perm2));
        assertNotEquals(perm1.hashCode(), perm2.hashCode());
        assertTrue(perm1.compareTo(perm2) < 0);
    }

    @Test
    public void diffNameDeny() {
        Perm perm1 = new Perm("test.permission1", Perm.Value.DENY);
        Perm perm2 = new Perm("test.permission2", Perm.Value.DENY);
        assertEquals(perm1.compareTo(perm2), -perm2.compareTo(perm1));
        assertFalse(perm1.equals(perm2));
        assertNotEquals(perm1.hashCode(), perm2.hashCode());
        assertTrue(perm1.compareTo(perm2) < 0);
    }

    @Test
    public void diffNameNever() {
        Perm perm1 = new Perm("test.permission1", Perm.Value.NEVER);
        Perm perm2 = new Perm("test.permission2", Perm.Value.NEVER);
        assertEquals(perm1.compareTo(perm2), -perm2.compareTo(perm1));
        assertFalse(perm1.equals(perm2));
        assertNotEquals(perm1.hashCode(), perm2.hashCode());
        assertTrue(perm1.compareTo(perm2) < 0);
    }

    @Test
    public void sameNameAllow() {
        Perm perm1 = new Perm("test.permission", Perm.Value.ALLOW);
        Perm perm2 = new Perm("test.permission", Perm.Value.ALLOW);
        assertEquals(perm1.compareTo(perm2), -perm2.compareTo(perm1));
        assertTrue(perm1.equals(perm2));
        assertEquals(perm1.hashCode(), perm2.hashCode());
        assertEquals(0, perm1.compareTo(perm2));
    }

    @Test
    public void sameNameDeny() {
        Perm perm1 = new Perm("test.permission", Perm.Value.DENY);
        Perm perm2 = new Perm("test.permission", Perm.Value.DENY);
        assertEquals(perm1.compareTo(perm2), -perm2.compareTo(perm1));
        assertTrue(perm1.equals(perm2));
        assertEquals(perm1.hashCode(), perm2.hashCode());
        assertEquals(0, perm1.compareTo(perm2));
    }

    @Test
    public void sameNameNever() {
        Perm perm1 = new Perm("test.permission", Perm.Value.NEVER);
        Perm perm2 = new Perm("test.permission", Perm.Value.NEVER);
        assertEquals(perm1.compareTo(perm2), -perm2.compareTo(perm1));
        assertTrue(perm1.equals(perm2));
        assertEquals(perm1.hashCode(), perm2.hashCode());
        assertEquals(0, perm1.compareTo(perm2));
    }

    @Test
    public void sameNameAllowDeny() {
        Perm perm1 = new Perm("test.permission", Perm.Value.ALLOW);
        Perm perm2 = new Perm("test.permission", Perm.Value.DENY);
        assertEquals(perm1.compareTo(perm2), -perm2.compareTo(perm1));
        assertTrue(perm1.equals(perm2));
        assertEquals(perm1.hashCode(), perm2.hashCode());
        assertEquals(0, perm1.compareTo(perm2));
    }

    @Test
    public void sameNameAllowNever() {
        Perm perm1 = new Perm("test.permission", Perm.Value.ALLOW);
        Perm perm2 = new Perm("test.permission", Perm.Value.NEVER);
        assertEquals(perm1.compareTo(perm2), -perm2.compareTo(perm1));
        assertTrue(perm1.equals(perm2));
        assertEquals(perm1.hashCode(), perm2.hashCode());
        assertEquals(0, perm1.compareTo(perm2));
    }

    @Test
    public void sameNameDenyAllow() {
        Perm perm1 = new Perm("test.permission", Perm.Value.DENY);
        Perm perm2 = new Perm("test.permission", Perm.Value.ALLOW);
        assertEquals(perm1.compareTo(perm2), -perm2.compareTo(perm1));
        assertTrue(perm1.equals(perm2));
        assertEquals(perm1.hashCode(), perm2.hashCode());
        assertEquals(0, perm1.compareTo(perm2));
    }

    @Test
    public void sameNameDenyNever() {
        Perm perm1 = new Perm("test.permission", Perm.Value.DENY);
        Perm perm2 = new Perm("test.permission", Perm.Value.NEVER);
        assertEquals(perm1.compareTo(perm2), -perm2.compareTo(perm1));
        assertTrue(perm1.equals(perm2));
        assertEquals(perm1.hashCode(), perm2.hashCode());
        assertEquals(0, perm1.compareTo(perm2));
    }

    @Test
    public void sameNameNeverAllow() {
        Perm perm1 = new Perm("test.permission", Perm.Value.NEVER);
        Perm perm2 = new Perm("test.permission", Perm.Value.ALLOW);
        assertEquals(perm1.compareTo(perm2), -perm2.compareTo(perm1));
        assertTrue(perm1.equals(perm2));
        assertEquals(perm1.hashCode(), perm2.hashCode());
        assertEquals(0, perm1.compareTo(perm2));
    }

    @Test
    public void sameNameNeverDeny() {
        Perm perm1 = new Perm("test.permission", Perm.Value.NEVER);
        Perm perm2 = new Perm("test.permission", Perm.Value.DENY);
        assertEquals(perm1.compareTo(perm2), -perm2.compareTo(perm1));
        assertTrue(perm1.equals(perm2));
        assertEquals(perm1.hashCode(), perm2.hashCode());
        assertEquals(0, perm1.compareTo(perm2));
    }
}
