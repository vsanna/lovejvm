package dev.ishikawa.test;

class InstanceNew2 {
    private final String name;
    private final long age;

    public InstanceNew2(String name, long age) {
        this.name = name;
        this.age = age;
    }

    static public void main() {
        InstanceNew2 instance = new InstanceNew2("world", -100L);
    }
}

