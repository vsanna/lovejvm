package dev.ishikawa.test;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class BasicClass2 {
    private final Map<String, Integer> cache = new HashMap(10);
    private final List<String> queue = new ArrayList(QUEUE_SIZE);
    private int queuePos = 0;
    static private final int QUEUE_SIZE = 3;

    public int getId(String key) {
        if(cache.containsKey(key)) {
            System.out.println("cache hit!");
            return cache.get(key);
        }

        System.out.println("cache missing!");
        int value = key.hashCode();
        cache.put(key, value);

        return value;
    }

    public void pushRequest(String request) {
        int nextPos = (queuePos + 1)%QUEUE_SIZE;
        this.queue.set(nextPos, request);
        queuePos = nextPos;
    }

    public String peekRequest() {
        return this.queue.get(queuePos);
    }

    static public void main() {
        BasicClass2 bc2 = new BasicClass2(); // 103
        int a1 = bc2.getId("hello");    // 226
        int a2 = bc2.getId("world");    // 247
        int a3 = bc2.getId("mountain"); // 251
        int a4 = bc2.getId("ocean");    // 255
        int a5 = bc2.getId("hello");    // 226
        int a6 = bc2.getId("world");    // 247
        int a7 = bc2.getId("mountain"); // 251
        int a8 = bc2.getId("ocean");    // 255

        bc2.pushRequest("req1");
        System.out.println(bc2.peekRequest());
        bc2.pushRequest("req2");
        System.out.println(bc2.peekRequest());
        bc2.pushRequest("req3");
        System.out.println(bc2.peekRequest());
        bc2.pushRequest("req4");
        System.out.println(bc2.peekRequest());
    }
}