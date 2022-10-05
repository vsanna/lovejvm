package dev.ishikawa.test;

class InstanceNew2 {
    static private String ss = "helloworld";
    static private int ii = 10000;

    private String s;
    private int i;

    public InstanceNew2(String s, int i) {
        this.s = s;
        this.i = i;
    }

    static public void main() {
        InstanceNew2 in2 = new InstanceNew2(
            "!",
            1000
        );
        //      10000            1000                            10                 1
        int a = InstanceNew2.ii + in2.i + 100 + InstanceNew2.ss.length() + in2.s.length();
    }
}