package reflection.memberexample;

public class Main {

    private static class Inner {
    }
    static void checkSynthetic (String name) {
        try {
            System.out.println (name + " : " + Class.forName (name).isSynthetic ());
        } catch (ClassNotFoundException exc) {
            exc.printStackTrace (System.out);
        }
    }
    public static void main(String[] args) throws Exception
    {
        new Inner ();
        checkSynthetic ("com.fcc.test.Main");
        checkSynthetic ("com.fcc.test.Main$Inner");
        checkSynthetic ("com.fcc.test.Main$1");
    }
}