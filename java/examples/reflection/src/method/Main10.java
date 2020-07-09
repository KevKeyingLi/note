package method;

import java.lang.reflect.Method;

public class Main10 {

    public static void main(String[] args) throws Exception{
        Class<Supper> supperClass = Supper.class;
        Supper supper = supperClass.newInstance();
        Method sayHello = supperClass.getDeclaredMethod("sayHello", String.class);
        sayHello.setAccessible(Boolean.TRUE);
        sayHello.invoke(supper,"throwable");
    }

    public static class Supper{

        private void sayHello(String name){
            System.out.println(String.format("%s say hello!", name));
        }
    }
}

// throwable say hello!