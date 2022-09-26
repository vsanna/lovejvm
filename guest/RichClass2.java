class RichClass2 {
    private String s;
    private int i;

    static private String ss;
    static private int ii = 100;

    public RichClass2(String s) {
        this.s = s;
    }

    static public void main() {
        int a = RichClass2.ii + 1;
    }
}