package reflection.classexample;

import java.lang.reflect.Constructor;

public class Main6 {

    public static void main(String[] args) throws Exception {
        Outter outter = new Outter();
    }

    public static class Outter {

        //Outter的无参数构造器
        public Outter() {
            //构造中定义的内部类
            class Inner {

            }

            Class<Inner> innerClass = Inner.class;
            Class<?> enclosingClass = innerClass.getEnclosingClass();
            System.out.println(enclosingClass.getName());
            Constructor<?> enclosingConstructor = innerClass.getEnclosingConstructor();
            System.out.println(enclosingConstructor.getName());
        }
    }
}

// org.throwable.inherited.Main6$Outter
// org.throwable.inherited.Main6$Outter
// 在这里，Inner是Outter的无参数构造里面定义的构造内部类，它也只能在Outter的无参数构造里面使用，通过Inner的Class的getEnclosingConstructor()方法获取到的就是Outter的无参数构造。