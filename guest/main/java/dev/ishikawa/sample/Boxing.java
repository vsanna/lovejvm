package dev.ishikawa.sample;

import java.util.ArrayList;
import java.util.List;

class Boxing {
    static void main(String[] args) {
        Integer i = Integer.valueOf(1);
        Long l = Long.valueOf(10L);
        Float f = Float.valueOf(1.1f);
        Double d = Double.valueOf(2.1D);

        Short s = Short.valueOf((short)100);
        Byte b = Byte.valueOf((byte) 123);
        Character c = Character.valueOf('a');
        Boolean bool = Boolean.TRUE;
    }
}