package clazz;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Supper<String, List<Integer>> supper = new Supper<>();
        Class<?> clazz = supper.getClass();
        System.out.println("name->" + clazz.getName());
        System.out.println("canonicalName->" + clazz.getCanonicalName());
        System.out.println("simpleName->" + clazz.getSimpleName());
        System.out.println("======================================");
        String[][] strings = new String[1][1];
        System.out.println("name->" + strings.getClass().getName());
        System.out.println("canonicalName->" + strings.getClass().getCanonicalName());
        System.out.println("simpleName->" + strings.getClass().getSimpleName());
    }

    private static class Supper<K, V> {
        private K key;
        private V value;
        //省略setter和getter方法
    }
}
