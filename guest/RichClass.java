class RichClass {
    private String s;
    private int i;

    static private String ss;
    static private int ii;

    public RichClass(String s) {
        this.s = s;
    }

    static public void main() {
        int a = RichClass.ii + 1;
    }
}