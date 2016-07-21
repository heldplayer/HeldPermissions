package me.heldplayer.test;

import org.junit.Assert;

public class TestUtils {

    public static void assertNoThrow(Runnable code) {
        try {
            code.run();
        } catch (Throwable e) {
            Assert.fail("Expected no exception but got " + e.getClass().getName() + (e.getMessage() != null ? ": " + e.getMessage() : ""));
        }
    }

    public static void assertThrow(Runnable code, Class<? extends Throwable> clazz) {
        try {
            code.run();
        } catch (Throwable e) {
            if (!clazz.isAssignableFrom(e.getClass())) {
                Assert.fail("Expected " + clazz.getName() + " thrown but got " + e.getClass().getName() + (e.getMessage() != null ? ": " + e.getMessage() : ""));
            }
            return;
        }
        Assert.fail("Expected " + clazz.getName() + " thrown but got nothing");
    }
}
