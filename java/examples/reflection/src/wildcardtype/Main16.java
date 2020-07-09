package wildcardtype;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Main16 {

    public static void main(String[] args) {
        Class<Main16> clazz = Main16.class;
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if ("print".equals(method.getName())) {
                Type[] genericParameterTypes = method.getGenericParameterTypes();
                for (Type type : genericParameterTypes) {
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        for (Type actualType : actualTypeArguments) {
                            if (actualType instanceof WildcardType) {
                                WildcardType wildcardType = (WildcardType) actualType;
                                System.out.println("WildcardType --> " + wildcardType + " getUpperBounds--> "
                                        + Arrays.toString(wildcardType.getUpperBounds()) + " getLowerBounds--> " + Arrays.toString(wildcardType.getLowerBounds()));
                            } else {
                                System.out.println("Not WildcardType --> " + actualType);
                            }
                        }

                    }
                }
            }
        }
    }

    interface Person {

    }

    public static void print(List<? extends Number> list, Set<? super Person> persons) {

    }
}

// WildcardType --> ? extends java.lang.Number getUpperBounds--> [class java.lang.Number] getLowerBounds--> []
// WildcardType --> ? super org.throwable.inherited.Main16$Person getUpperBounds--> [class java.lang.Object] getLowerBounds--> [interface org.throwable.inherited.Main16$Person]