package me.heldplayer.permissions.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static me.heldplayer.test.TestUtils.assertNoThrow;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PermsTest {

    private PermCollection perms;

    @Before
    public void setUp() {
        this.perms = new PermCollection();
    }

    @After
    public void tearDown() {
        this.perms = null;
    }

    @Test
    public void isEmpty() {
        assertTrue(this.perms.isEmpty());
        this.perms.put("test.permission", Perm.Value.ALLOW);
        assertFalse(this.perms.isEmpty());
        this.perms.clear();
        assertTrue(this.perms.isEmpty());
    }

    @Test
    public void clear() {
        assertTrue(this.perms.isEmpty());
        this.perms.clear();
        assertTrue(this.perms.isEmpty());
        this.perms.put("test.permission", Perm.Value.ALLOW);
        this.perms.clear();
        assertTrue(this.perms.isEmpty());
    }

    @Test
    public void size() {
        assertEquals(0, this.perms.size());
        this.perms.put("test.permission", Perm.Value.ALLOW);
        assertEquals(1, this.perms.size());
        this.perms.put("test.permission2", Perm.Value.ALLOW);
        assertEquals(2, this.perms.size());
        this.perms.clear();
        assertEquals(0, this.perms.size());
    }

    @Test
    public void has() {
        assertFalse(this.perms.has("test.permission"));
        this.perms.put("test.permission", Perm.Value.ALLOW);
        assertTrue(this.perms.has("test.permission"));
        this.perms.clear();
        assertFalse(this.perms.has("test.permission"));
    }

    @Test
    public void get() {
        assertNull(this.perms.get("test.permission"));
        this.perms.put("test.permission", Perm.Value.ALLOW);
        assertEquals(Perm.Value.ALLOW, this.perms.get("test.permission"));
        assertNull(this.perms.get("test.permission2"));
        this.perms.put("test.permission", Perm.Value.DENY);
        assertEquals(Perm.Value.DENY, this.perms.get("test.permission"));
        assertNull(this.perms.get("test.permission2"));
        this.perms.put("test.permission", Perm.Value.NEVER);
        assertEquals(Perm.Value.NEVER, this.perms.get("test.permission"));
        assertNull(this.perms.get("test.permission2"));
        this.perms.clear();
        assertNull(this.perms.get("test.permission"));
    }

    @Test
    public void put() {
        assertNoThrow(() -> this.perms.put("test.permission", Perm.Value.ALLOW));
        assertEquals(Perm.Value.ALLOW, this.perms.get("test.permission"));
        assertNoThrow(() -> this.perms.put("test.permission", Perm.Value.DENY));
        assertEquals(Perm.Value.DENY, this.perms.get("test.permission"));
        assertNoThrow(() -> this.perms.put("test.permission", Perm.Value.NEVER));
        assertEquals(Perm.Value.NEVER, this.perms.get("test.permission"));
        assertNoThrow(() -> this.perms.put("test.permission", Perm.Value.DENY));
        assertEquals(Perm.Value.DENY, this.perms.get("test.permission"));
        assertNoThrow(() -> this.perms.put("test.permission", Perm.Value.ALLOW));
        assertEquals(Perm.Value.ALLOW, this.perms.get("test.permission"));
    }

    @Test
    public void add() {
        assertNoThrow(() -> this.perms.add(new Perm("test.permission", Perm.Value.ALLOW)));
        assertEquals(Perm.Value.ALLOW, this.perms.get("test.permission"));
        assertNoThrow(() -> this.perms.add(new Perm("test.permission", Perm.Value.DENY)));
        assertEquals(Perm.Value.DENY, this.perms.get("test.permission"));
        assertNoThrow(() -> this.perms.add(new Perm("test.permission", Perm.Value.NEVER)));
        assertEquals(Perm.Value.NEVER, this.perms.get("test.permission"));
        assertNoThrow(() -> this.perms.add(new Perm("test.permission", Perm.Value.DENY)));
        assertEquals(Perm.Value.DENY, this.perms.get("test.permission"));
        assertNoThrow(() -> this.perms.add(new Perm("test.permission", Perm.Value.ALLOW)));
        assertEquals(Perm.Value.ALLOW, this.perms.get("test.permission"));
    }

    @Test
    public void remove() {
        this.perms.put("test.permission", Perm.Value.ALLOW);
        assertTrue(this.perms.has("test.permission"));
        this.perms.remove("test.permission");
        assertFalse(this.perms.has("test.permission"));
        this.perms.remove("test.permission");
        assertFalse(this.perms.has("test.permission"));
    }
}
