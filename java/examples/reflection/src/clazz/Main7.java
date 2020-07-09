package clazz;

import java.lang.reflect.Method;

public class Main7 {

    public static void main(String[] args) throws Exception {
        Outter outter = new Outter();
        outter.print();
    }

    public static class Outter {

        public void print(){
            //方法print中定义的内部类
            class Inner {

            }

            Class<Inner> innerClass = Inner.class;
            Class<?> enclosingClass = innerClass.getEnclosingClass();
            System.out.println(enclosingClass.getName());
            Method enclosingMethod = innerClass.getEnclosingMethod();
            System.out.println(enclosingMethod.getName());
        }
    }
}

// org.throwable.inherited.Main7$Outter
// print
// 在这里，Inner是Outter的print方法里面定义的方法内部类，它也只能在Outter的print方法里面使用，通过Inner的Class的getEnclosingMethod()方法获取到的就是Outter的print方法。