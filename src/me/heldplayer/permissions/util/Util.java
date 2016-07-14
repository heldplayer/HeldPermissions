package me.heldplayer.permissions.util;

import java.util.HashMap;
import java.util.Set;

public class Util {

    public static <K, V> void joinMaps(HashMap<K, V> map1, HashMap<K, V> map2) {
        Set<K> map2Keys = map2.keySet();

        for (K key : map2Keys) {
            if (map1.containsKey(key)) {
                map1.remove(key);
            }

            map1.put(key, map2.get(key));
        }
    }
}
