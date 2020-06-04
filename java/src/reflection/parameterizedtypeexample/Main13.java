package reflection.parameterizedtypeexample;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class Main13 {

    public static void main(String[] args) throws Exception {
        Class<Sub> subClass = Sub.class;
        Type genericSuperclass = subClass.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            //获取父类泛型类型数组
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (Type type : actualTypeArguments) {
                System.out.println(type + " is ParameterizedType -> " + (type instanceof ParameterizedType));
            }
        }
        Field field = subClass.getDeclaredField("clazz");
        Type genericType = field.getGenericType();
        System.out.println(genericType + " is ParameterizedType -> " + (genericType instanceof ParameterizedType));
    }

    public static class Person {

    }

    public static abstract class Supper<T, E> {

    }

    public static class Sub extends Supper<String, List<Person>> {

    }
}

// class java.lang.String is ParameterizedType -> false
// java.util.List<org.throwable.inherited.Main13$Person> is ParameterizedType -> true
// java.lang.Class<?> is ParameterizedType -> true