package reflection.src.classloading;

import classloading.common.FileClassLoader;

public class TwoClassesEquality {

    public static void main(String[] args) throws ClassNotFoundException {
        String rootDir="/Users/zhezhuang/LEARNING/note/java/reflection/src/";
        //创建两个不同的自定义类加载器实例
        FileClassLoader loader1 = new FileClassLoader(rootDir);
        FileClassLoader loader2 = new FileClassLoader(rootDir);
        //通过findClass创建类的Class对象
        Class<?> object1=loader1.findClass("classloading.common.DemoObj");
        Class<?> object2=loader2.findClass("classloading.common.DemoObj");

        System.out.println("findClass->obj1:"+object1.hashCode());
        System.out.println("findClass->obj2:"+object2.hashCode());
    }

}
