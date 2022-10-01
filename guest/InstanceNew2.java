class InstanceNew3 {
    static private String ss = "helloworld";
    static private int ii = 10000;

    private String s;
    private int i;

    public InstanceNew3(String s, int i) {
        this.s = s;
        this.i = i;
    }

    static public void main() {
        InstanceNew3 in3 = new InstanceNew3(
            "!",
            1000
        );
        //      10000            1000                            10                 1
        int a = InstanceNew3.ii + in3.i + 100 + InstanceNew3.ss.length() + in3.s.length();
    }
}