package array;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.Arrays;

public class ArrayCreationMain {

    /**
     * 这个是我们创建的最终目标数组
     */
    private static String R = "java.math.BigInteger[] bi = {123,234,345}";
    private static final String[] S = new String[]{"123", "234", "345"};

    public static void main(String[] args) throws Exception {
        Class<BigInteger> componentType = BigInteger.class;
        Object arrayObject = Array.newInstance(componentType, 3);
        for (int i = 0; i < S.length; i++) {
            String each = S[i];
            Constructor<BigInteger> constructor = componentType.getConstructor(String.class);
            BigInteger value = constructor.newInstance(each);
            Array.set(arrayObject, i, value);
        }
        Object[] result = (Object[]) arrayObject;
        System.out.println(String.format("%s[] = %s", componentType, Arrays.toString(result)));
        int length = Array.getLength(arrayObject);
        System.out.println("Length = " + length);
        for (int i = 0; i < length; i++) {
            System.out.println(String.format("index = %d,value = %s", i, Array.get(arrayObject, i)));
        }
        Class<?> arrayObjectClass = arrayObject.getClass();
        System.out.println("Is array type:" + arrayObjectClass.isArray());
        System.out.println("Component type:" + arrayObjectClass.getComponentType());
    }
}