package dev.ishikawa.sample;

import java.util.HashMap;
import java.util.Map;

class MapSample2 {
    static void main(String[] args) {
        Map<String, Integer> map = new HashMap(100); // 105
        map.put("hello", 1);
        map.put("world", 10);
        map.put("!!", 100);
        int a = map.get("hello") + map.get("world") + map.get("!!"); // 111

        map.put("hello", 2);
        map.put("world", 20);
        map.put("!!", 200);
        int b = map.get("hello") + map.get("world") + map.get("!!"); // 222

        map.remove("hello");
        int c = map.getOrDefault("hello", -100); // -100
    }
}
