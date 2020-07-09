package reflection.src.classclass.common;

public class Initable {
    //编译期静态常量
    static final int staticFinal = 47;

    //非编译期静态常量
    static final int staticFinal2 = ClassInitialization.rand.nextInt(1000);

    static {
        System.out.println("Initializing Initable");
    }
}
