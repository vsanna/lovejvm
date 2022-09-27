package dev.ishikawa.test;

class InstanceNew2 {
    final String name;
    final long age;

    public InstanceNew2(String name, long age) {
        this.name = name;
        this.age = age;
    }

    static public void main() {
        InstanceNew2 instance = new InstanceNew2("world", -123L);
        long a = instance.age;
        String b = instance.name;
    }
}

