package annotatedelement;

import java.lang.annotation.*;

public class Main {

    public static void main(String[] args) {
        Class<?> clazz = Sub.class;
        System.out.println("-----getAnnotations-----");
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println(annotation.toString());
        }
        System.out.println("-----getDeclaredAnnotation-->SupperAnnotation-----");
        SupperAnnotation declaredSupperAnnotation = clazz.getDeclaredAnnotation(SupperAnnotation.class);
        System.out.println(declaredSupperAnnotation);
        System.out.println("-----getAnnotation-->SupperAnnotation-----");
        SupperAnnotation supperAnnotation = clazz.getAnnotation(SupperAnnotation.class);
        System.out.println(supperAnnotation);
        System.out.println("-----getDeclaredAnnotation-->SubAnnotation-----");
        SubAnnotation declaredSubAnnotation = clazz.getDeclaredAnnotation(SubAnnotation.class);
        System.out.println(declaredSubAnnotation);
        System.out.println("-----getDeclaredAnnotationsByType-->SubAnnotation-----");
        SubAnnotation[] declaredSubAnnotationsByType = clazz.getDeclaredAnnotationsByType(SubAnnotation.class);
        for (SubAnnotation subAnnotation : declaredSubAnnotationsByType) {
            System.out.println(subAnnotation);
        }
        System.out.println("-----getDeclaredAnnotationsByType-->SupperAnnotation-----");
        SupperAnnotation[] declaredSupperAnnotationsByType = clazz.getDeclaredAnnotationsByType(SupperAnnotation.class);
        for (SupperAnnotation supperAnnotation1 : declaredSupperAnnotationsByType) {
            System.out.println(supperAnnotation1);
        }
        System.out.println("-----getAnnotationsByType-->SupperAnnotation-----");
        SupperAnnotation[] supperAnnotationsByType = clazz.getAnnotationsByType(SupperAnnotation.class);
        for (SupperAnnotation supperAnnotation2 : supperAnnotationsByType) {
            System.out.println(supperAnnotation2);
        }
    }
    // 可以尝试注释掉@Inherited再运行一次，对比一下结果。如果注释掉@Inherited，从Sub这个类永远无法获取到它的父类Supper中的@SupperAnnotation。
    // Class、Constructor、Method、Field、Parameter都实现了AnnotatedElement接口，所以它们都具备操作注解的功能。


    @SupperAnnotation
    private static class Supper {

    }

    @SubAnnotation
    private static class Sub extends Supper {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @Target(ElementType.TYPE)
    private @interface SupperAnnotation {

        String value() default "SupperAnnotation";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Target(ElementType.TYPE)
    private @interface SubAnnotation {

        String value() default "SubAnnotation";
    }
}
