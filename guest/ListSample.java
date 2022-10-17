package dev.ishikawa.test;

import java.util.ArrayList;
import java.util.List;

class ListSample {
    static void main() {
        List<Integer> list = new ArrayList(10); // 105
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(40);
        list.add(50);
        list.add(60);
        list.add(70);
        list.add(80);
        list.add(90);
        list.add(100);
        list.add(110);

        int a1 = list.get(4); // 50
        int a2 = list.get(6); // 70
        int a3 = list.get(8); // 90
        int a4 = list.get(10); // 110
        list.set(10, -10);
        int a5 = list.get(10); // -10
        int size = list.size(); // 11
        boolean isEmpty = list.isEmpty(); // 0
        boolean has50 = list.contains(50); // 1
        boolean has51 = list.contains(51); // 0

//        list.set(12, 123); // this should throw IllegalArgumentException
    }
}