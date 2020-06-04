package reflection.parameterexample;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class Main11 {

    public static void main(String[] args) throws Exception {
        Class<Supper> supperClass = Supper.class;
        Method sayHello = supperClass.getDeclaredMethod("sayHello", String.class);
        sayHello.setAccessible(Boolean.TRUE);
        Parameter[] parameters = sayHello.getParameters();
        for (Parameter parameter : parameters) {
            System.out.println("isNamePresent->" + parameter.isNamePresent());
            System.out.println("isImplicit->" + parameter.isImplicit());
            System.out.println("getName->" + parameter.getName());
            System.out.println("=====================");
        }

    }

    public static class Supper {

        private void sayHello(String name) {
            System.out.println(String.format("%s say hello!", name));
        }
    }
}

// isNamePresent->true
// isImplicit->false
// getName->name
// =====================

// 如果不设置编译参数-parameters，会输出下面的结果：
//
// isNamePresent->false
// isImplicit->false
// getName->arg0
// =====================