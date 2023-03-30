package dev.ishikawa.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

class WebServer {
    static void main(String[] args) {
        Map<String, Runnable> endpoints = new HashMap(10);

        endpoints.put("/hello", (Runnable) () -> {
            System.out.println("world!");
        });
        endpoints.put("/ping", (Runnable) () -> {
            System.out.println("pong!");
        });
        endpoints.put("/health", (Runnable) () -> {
            System.out.println("Ok!");
        });

        String request = "/health";

        Runnable r = endpoints.get(request);
        r.run();
    }
}
