package typevariable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

public class Main14 {

    public static void main(String[] args) throws Exception {
        Class<Supper> subClass = Supper.class;
        TypeVariable<Class<Supper>>[] typeParameters = subClass.getTypeParameters();
        for (TypeVariable<Class<Supper>> typeVariable : typeParameters) {
            System.out.println("getBounds --> " + Arrays.toString(typeVariable.getBounds()));
            System.out.println("getGenericDeclaration  --> " + typeVariable.getGenericDeclaration());
            System.out.println("getName --> " + typeVariable.getName());
            AnnotatedType[] annotatedBounds = typeVariable.getAnnotatedBounds();
            StringBuilder stringBuilder = new StringBuilder("getAnnotatedBounds --> ");
            for (AnnotatedType annotatedType : annotatedBounds) {
                java.lang.annotation.Annotation[] annotations = annotatedType.getAnnotations();
                for (java.lang.annotation.Annotation annotation : annotations) {
                    stringBuilder.append(annotation).append(",");
                }
            }
            System.out.println(stringBuilder.toString());
            System.out.println("===================");
        }
    }

    @Target(ElementType.TYPE)
    public @interface Annotation {

    }

    interface InterFace {

    }

    public static class Person {

    }

    public static abstract class Supper<T extends Person & InterFace, E extends Annotation> {

    }
}

// getBounds --> [class org.throwable.inherited.Main14$Person, interface org.throwable.inherited.Main14$InterFace]
// getGenericDeclaration  --> class org.throwable.inherited.Main14$Supper
// getName --> T
// getAnnotatedBounds -->
// ===================
// getBounds --> [interface org.throwable.inherited.Main14$Annotation]
// getGenericDeclaration  --> class org.throwable.inherited.Main14$Supper
// getName --> E
// getAnnotatedBounds -->
// ===================