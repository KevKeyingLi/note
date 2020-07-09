package reflection.src.classclass.common;

public class Initable3 {
    //静态成员变量
    static int staticNonFinal = 74;

    static {
        System.out.println("Initializing Initable3");
    }
}
