package dev.ishikawa.test;

class InstanceNew {
    private final String name;
    private final int age;

    public InstanceNew(String name, int age) {
        this.name = name;
        this.age = age;
    }

    static public void main() {
        InstanceNew instance = new InstanceNew("world", -100);
    }
}

