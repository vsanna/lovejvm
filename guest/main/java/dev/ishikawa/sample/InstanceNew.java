package dev.ishikawa.sample;

class InstanceNew {
    private static final int STATIC_VALUE = 2;
    private final int a;
    private final int b;

    public InstanceNew(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int sum() {
        return a + b;
    }

    static public void main(String[] args) {
        InstanceNew instance = new InstanceNew(2000, -1000);
        int c = instance.sum() / STATIC_VALUE; // 500
    }
}

