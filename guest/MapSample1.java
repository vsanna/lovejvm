package dev.ishikawa.test;

import java.util.HashMap;
import java.util.Map;

class MapSample1 {
    static void main() {
        Map<String, String> map = new HashMap(100); // 105
        map.put("hello", "world");
        map.put("mountain", "ocean");

        String a = map.get("hello");    // 1214 => String(   12)| 00 00 00 00 00 00 04 BF 00 00 00 00
                                        //    => 1215| [B(   20)| 00 00 00 77 00 00 00 6F 00 00 00 72 00 00 00 6C 00 00 00 64
        String b = map.get("mountain"); // 1219 => String(   12)| 00 00 00 00 00 00 04 C4 00 00 00 00
                                        //    => 1220| [B(   20)| 00 00 00 6F 00 00 00 63 00 00 00 65 00 00 00 61 00 00 00 6E
    }
}