- [基本语法](#基本语法)
    * [声明注解与元注解](#声明注解与元注解)
        * [@Target](#-Target)
        * [@Retention](#-Retention)
    * [注解元素及其数据类型](#注解元素及其数据类型)
    * [编译器对默认值的限制](#编译器对默认值的限制)
    * [注解不支持继承](#注解不支持继承)
    * [快捷方式](#快捷方式)
    * [Java内置注解与其它元注解](#Java内置注解与其它元注解)
- [注解与反射机制](#注解与反射机制)
- [运行时注解处理器](#运行时注解处理器)

---
* Refs:
    * > https://blog.csdn.net/javazejian/article/details/71860633
---
### 基本语法
#### 声明注解与元注解
* 注解例子:
    ```java
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Test {
    
    } 
    ```
    * 使用了`@interface`声明了Test注解
    * 并使用`@Target`注解传入`ElementType.METHOD`参数来标明`@Test`只能用于方法上，
    * `@Retention(RetentionPolicy.RUNTIME)`则用来表示该注解生存期是运行时，从代码上看注解的定义很像接口的定义，毕竟在编译后也会生成Test.class文件。
    * 对于`@Target`和`@Retention`是由Java提供的元注解。
* 元注解就是标记其他注解的注解

##### @Target
* `@Target`用来约束注解可以应用的地方，其中ElementType是枚举类型。
    ```java
    public enum ElementType {
        TYPE, // 标明该注解可以用于类、接口（包括注解类型）或enum声明
        FIELD, // 标明该注解可以用于字段(域)声明，包括enum实例
        METHOD, // 标明该注解可以用于方法声明
        PARAMETER, // 标明该注解可以用于参数声明
        CONSTRUCTOR, // 标明注解可以用于构造函数声明
        LOCAL_VARIABLE, // 标明注解可以用于局部变量声明
        ANNOTATION_TYPE, // 标明注解可以用于注解声明
        PACKAGE, // 标明注解可以用于包声明
        TYPE_PARAMETER, // 标明注解可以用于类型参数声明
        TYPE_USE // 类型使用声明
    }
    ```
* 当注解未指定Target值时，则此注解可以用于任何元素之上。
* 多个值使用`{}`包含并用逗号隔开。

##### @Retention
* `@Retention`用来约束注解的生命周期
    * `SOURCE`: 该类型的注解信息只会保留在源码里，源码经过编译后，注解信息会被丢弃，不会保留在编译好的class文件里。
    * `CLASS`: 该类型的注解信息会保留在源码里和class文件里，在执行的时候，不会加载到虚拟机中，当注解未定义Retention值时，默认值是CLASS，如Java内置注解，`@Override`、`@Deprecated`、`@SuppressWarnning`等。
    * `RUNTIME`: 注解信息将在运行期(JVM)也保留，因此可以通过反射机制读取注解的信息，如SpringMvc中的`@Controller`、`@Autowired`、`@RequestMapping`等。

#### 注解元素及其数据类型
* 在自定义注解中，一般都会包含一些元素以表示某些值，方便处理器使用。
    ```java
    @Target(ElementType.TYPE)//只能应用于类上
    @Retention(RetentionPolicy.RUNTIME)//保存到运行时
    public @interface DBTable {
        String name() default "";
    }
    ```
    * 声明一个String类型的name元素，其默认值为空字符，但是必须注意到对应任何元素的声明应采用方法的声明方式，同时可选择使用default提供默认值。
* `@DBTable`使用方式
    ```java
    //在类上使用该注解
    @DBTable(name = "MEMBER")
    public class Member {
        //.......
    }
    ```
* 注解支持的元素数据类型
    * 所有基本类型
    * String
    * Class
    * enum
    * Annotation
    * 上述类型的数组
* 例子: `./java-annotation.java`

#### 编译器对默认值的限制
* 元素不能有不确定的值，要么具有默认值，要么在使用注解时提供元素的值。
* 对于非基本类型的元素，无论是在源代码中声明，还是在注解接口中定义默认值，都不能以null作为值。为了绕开这个限制，只能定义一些特殊的值，例如空字符串或负数，表示某个元素不存在。

#### 注解不支持继承
* 注解是不支持继承的，因此不能使用关键字extends来继承某个`@interface`，但注解在编译后，编译器会自动继承`java.lang.annotation.Annotation`接口。

#### 快捷方式
* 所谓的快捷方式就是注解中定义了名为value的元素，并且在使用该注解时，如果该元素是唯一需要赋值的一个元素，那么此时无需使用`key=value`的语法，而只需在括号内给出value元素所需的值即可。
    ```java
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface IntegerVaule{
        int value() default 0;
        String name() default "";
    }
    
    //使用注解
    public class QuicklyWay {
        //当只想给value赋值时,可以使用以下快捷方式
        @IntegerVaule(20)
        public int age;
    
        //当name也需要赋值时必须采用key=value的方式赋值
        @IntegerVaule(value = 10000,name = "MONEY")
        public int money;
    }
    ```

#### Java内置注解与其它元注解
* Java提供的内置注解
    * `@Override`
        * 用于标明此方法覆盖了父类的方法
            ```java
            @Target(ElementType.METHOD)
            @Retention(RetentionPolicy.SOURCE)
            public @interface Override {}
            ```
    * `@Deprecated`
        * 用于标明已经过时的方法或类
            ```java
            @Documented
            @Retention(RetentionPolicy.RUNTIME)
            @Target(value={CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, PARAMETER, TYPE})
            public @interface Deprecated {
            }
            ```
    * `@SuppressWarnnings`
        * 用于有选择的关闭编译器对类、方法、成员变量、变量初始化的警告
            ```java
            @Target({TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE})
            @Retention(RetentionPolicy.SOURCE)
            public @interface SuppressWarnings {
                String[] value();
            }
            ```
* 其它元注解
    * `@Documented`
        * 被修饰的注解会生成到javadoc中
            ```java
            @Documented
            @Target(ElementType.TYPE)
            @Retention(RetentionPolicy.RUNTIME)
            public @interface DocumentA {
            }
            
            //没有使用@Documented
            @Target(ElementType.TYPE)
            @Retention(RetentionPolicy.RUNTIME)
            public @interface DocumentB {
            }
            
            //使用注解
            @DocumentA
            @DocumentB
            public class DocumentDemo {
                public void A(){
                }
            }
            ```
            * 可以发现使用`@Documented`元注解定义的注解(`@DocumentA`)将会生成到javadoc中,而`@DocumentB`则没有在doc文档中出现，这就是元注解`@Documented`的作用。
    * `@Inherited`
        * 可以让注解被继承，但这并不是真的继承，只是通过使用`@Inherited`，可以让子类Class对象使用`getAnnotations()`获取父类被`@Inherited`修饰的注解。

### 注解与反射机制
* Java所有注解都继承了Annotation接口，也就是说Java使用Annotation接口代表注解元素，该接口是所有Annotation类型的父接口。
* 同时为了运行时能准确获取到注解的相关信息，Java在`java.lang.reflect`反射包下新增了`AnnotatedElement`接口，它主要用于表示目前正在VM中运行的程序中已使用注解的元素，通过该接口提供的方法可以利用反射技术地读取注解的信息，如反射包的`Constructor`类、`Field`类、`Method`类、`Package`类和`Class`类都实现了`AnnotatedElement`接口。

    * `<T extends Annotation> T getAnnotation​(Class<T> annotationClass)`
        * 该元素如果存在指定类型的注解，则返回这些注解，否则返回null。
            ```java
            Class<?> clazz = DocumentDemo.class;
            DocumentA documentA=clazz.getAnnotation(DocumentA.class);
            ```
    * `Annotation[] getAnnotations​()`
        * 返回此元素上存在的所有注解，包括从父类继承的。
            ```java
            Class<?> clazz = DocumentDemo.class;
            Annotation[] an= clazz.getAnnotations();
            ```
    * `default boolean isAnnotationPresent​(Class<? extends Annotation> annotationClass)`
        * 如果指定类型的注解存在于此元素上，则返回true，否则返回false。
            ```java
            Class<?> clazz = DocumentDemo.class;
            boolean b=clazz.isAnnotationPresent(DocumentA.class);
            ```
    * `Annotation[] getDeclaredAnnotations​()`
        * 返回直接存在于此元素上的所有注解，不包括父类的注解，调用者可以随意修改返回的数组；这不会对其他调用者返回的数组产生任何影响，没有则返回长度为0的数组。
            ```java
            Class<?> clazz = DocumentDemo.class;
            Annotation[] an2=clazz.getDeclaredAnnotations();
            ```

### 运行时注解处理器
* 演示利用运行时注解来组装数据库SQL的构建语句的过程。
    ```java
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DBTable {
        String name() default "";
    }
  
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SQLInteger {
        //该字段对应数据库表列名
        String name() default "";
        //嵌套注解
        Constraints constraint() default @Constraints;
    }
 
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SQLString {
    
        //对应数据库表的列名
        String name() default "";
    
        //列类型分配的长度，如varchar(30)的30
        int value() default 0;
    
        Constraints constraint() default @Constraints;
    }
    
    @Target(ElementType.FIELD)//只能应用在字段上
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Constraints {
        //判断是否作为主键约束
        boolean primaryKey() default false;
        //判断是否允许为null
        boolean allowNull() default false;
        //判断是否唯一
        boolean unique() default false;
    }
    
    @DBTable(name = "MEMBER")
    public class Member {
        //主键ID
        @SQLString(name = "ID",value = 50, constraint = @Constraints(primaryKey = true))
        private String id;
    
        @SQLString(name = "NAME" , value = 30)
        private String name;
    
        @SQLInteger(name = "AGE")
        private int age;
    
        @SQLString(name = "DESCRIPTION" ,value = 150 , constraint = @Constraints(allowNull = true))
        private String description;//个人描述
    
       //省略set get.....
    }
    ```
    * 嵌套注解`@Constraints`，该注解主要用于判断字段是否为null或者字段是否唯一。
    * `@Retention(RetentionPolicy.RUNTIME)`，即运行时，这样才可以使用反射机制获取其信息。

