- [基本语法](#基本语法)
    * [声明注解与元注解](#声明注解与元注解)
        * [@Target](#@Target)
        * [@Retention](#@Retention)
    * [注解元素及其数据类型](#注解元素及其数据类型)

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









