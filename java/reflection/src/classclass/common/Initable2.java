package classclass.common;

public class Initable2 {
    //静态成员变量
    static int staticNonFinal = 147;

    static {
        System.out.println("Initializing Initable2");
    }
}
