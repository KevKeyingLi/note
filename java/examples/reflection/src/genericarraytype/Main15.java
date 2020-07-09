package genericarraytype;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public class Main15<T> {


    public static void main(String[] args) throws Exception {
        Method[] methods = Main15.class.getMethods();
        for (Method method : methods) {
            if ("method".equals(method.getName())) {
                Type[] genericParameterTypes = method.getGenericParameterTypes();
                for (Type type : genericParameterTypes) {
                    if (type instanceof GenericArrayType) {
                        System.out.println("GenericArrayType --> " + type + " getGenericComponentType --> "
                                + ((GenericArrayType) type).getGenericComponentType());
                    } else {
                        System.out.println("Not GenericArrayType --> " + type);
                    }
                }
            }
        }
    }

    public static <T> void method(String[] strings, List<String> ls, List<String>[] lsa, T[] ts, List<T>[] tla, T[][] tts) {

    }
}

// Not GenericArrayType --> class [Ljava.lang.String;
// Not GenericArrayType --> java.util.List<java.lang.String>
// GenericArrayType --> java.util.List<java.lang.String>[] getGenericComponentType --> java.util.List<java.lang.String>
// GenericArrayType --> T[] getGenericComponentType --> T
// GenericArrayType --> java.util.List<T>[] getGenericComponentType --> java.util.List<T>
// GenericArrayType --> T[][] getGenericComponentType --> T[]