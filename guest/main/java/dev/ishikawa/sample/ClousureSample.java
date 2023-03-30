package dev.ishikawa.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

class ClousureSample {
    static void main(String[] args) {
        Supplier s = supplier();
        System.out.println(s.get());
        System.out.println(s.get());
        System.out.println(s.get());
        System.out.println(s.get());
        System.out.println(s.get());
        System.out.println(s.get());
        System.out.println(s.get());
        System.out.println(s.get());
        System.out.println(s.get());
        System.out.println(s.get());
        System.out.println(s.get());
    }

    static Supplier supplier() {
        final Map<String, Integer> map = new HashMap(10);
        map.put("value", 0);

        Supplier s = () -> {
            map.put("value", map.get("value")+1);
            int x = map.get("value");
            return x;
        };

        return s;
    }
}
