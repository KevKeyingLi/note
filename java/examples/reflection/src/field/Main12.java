package field;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Main12 {

    public static void main(String[] args) throws Exception {
        Class<Supper> supperClass = Supper.class;
        Supper supper = supperClass.newInstance();
        Method sayHello = supperClass.getDeclaredMethod("sayHello");
        sayHello.setAccessible(Boolean.TRUE);
        Field name = supperClass.getDeclaredField("name");
        name.setAccessible(Boolean.TRUE);
        name.set(supper,"throwable");
        System.out.println("Field get-->" + name.get(supper));
        sayHello.invoke(supper);
        name.set(supper, "throwable-10086");
        System.out.println("Field get-->" + name.get(supper));
        sayHello.invoke(supper);
    }

    public static class Supper {

        private String name;

        private void sayHello() {
            System.out.println(String.format("%s say hello!", name));
        }
    }
}

// Field get-->throwable
// throwable say hello!
// Field get-->throwable-10086
// throwable-10086 say hello!
