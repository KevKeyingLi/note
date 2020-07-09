package clazz;

public class Main5 {

    public static void main(String[] args) throws Exception{
        Class<Outter.Inner> clazz = Outter.Inner.class;
        Class<?> enclosingClass = clazz.getEnclosingClass();
        System.out.println(enclosingClass.getName());
    }
    // Inner类是Outter类的成员类
    public static class Outter {

        public static class Inner {

        }
    }
}
// org.throwable.inherited.Main5$Outter
//在这里，Inner就是当前定义的类，它是Outter的静态成员类，或者说Outter是Inner的封闭类，通过Inner的Class的getEnclosingClass()方法获取到的就是Outter的Class实例。