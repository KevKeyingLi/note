package reflection.constructorexample;

import java.lang.reflect.Constructor;

public class Main8 {

    public static void main(String[] args) throws Exception{
        Class<Supper> supperClass = Supper.class;
        Constructor<Supper> constructor = supperClass.getDeclaredConstructor();
        constructor.setAccessible(Boolean.TRUE);
        Supper supper = constructor.newInstance();
        supper.sayHello("throwable");
    }

    private static class Supper {

        public void sayHello(String name) {
            System.out.println(String.format("%s say hello!", name));
        }
    }
}

// throwable say hello!
// 这就是为什么一些IOC容器的实现框架中实例化类的时候优先依赖于无参数构造的原因，如果使用Class#newInstance方法，上面的代码调用逻辑会抛异常。