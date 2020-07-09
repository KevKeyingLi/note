- [核心类库和方法](#核心类库和方法)
    * [图解反射核心类的体系](#图解反射核心类的体系)
    * [Type接口](#Type接口)
    * [AnnotatedElement接口](#AnnotatedElement接口)
    * [Member接口](#Member接口)
    * [AccessibleObject类](#AccessibleObject类)
    * [GenericDeclaration接口](#GenericDeclaration接口)
    * [Executable类](#Executable类)
    * [Modifier类](#Modifier类)
    * [Class类](#Class类)
    * [Method类](#Method类)
    * [Field类](#Field类)
    * [Parameter类](#Parameter类)
- [数组和枚举](#数组和枚举)
    * [数组类型](#数组类型)
    * [枚举类型](#枚举类型)
- [泛型](#泛型)
    * [泛型的简介](#泛型的简介)
    * [理解类型擦除](#理解类型擦除)
    * [Type体系](#Type体系)
    * [ParameterizedType](#ParameterizedType)
    * [TypeVariable](#TypeVariable)
    * [WildcardType](#WildcardType)
    * [GenericArrayType](#GenericArrayType)
    * [泛型的约束](#泛型的约束)
    * [泛型数组的问题](#泛型数组的问题)
    * [无限定通配符](#无限定通配符)
    * [桥方法](#桥方法)
- [动态代理](#动态代理)
    * [设计模式中的代理模式](#设计模式中的代理模式)
    * [JDK动态代理的核心API](#JDK动态代理的核心API)
    * [JDK动态代理的流程](#JDK动态代理的流程)
    * [JDK动态代理的机制](#JDK动态代理的机制)

---
* refs:
    * > https://www.cnblogs.com/throwable/p/12272229.html
    * > https://www.cnblogs.com/throwable/p/12272244.html
    * > https://www.cnblogs.com/throwable/p/12315988.html
    * > https://www.cnblogs.com/throwable/p/12272262.html
---

### 核心类库和方法
* 反射(Reflection)是一种可以在运行时检查和动态调用类、构造、方法、属性等等的编程语言的能力，甚至可以不需要在编译期感知类的名称、方法的名称等等。
* 反射的缺点:
    * 性能开销：由于反射涉及动态解析的类型，因此无法执行某些Java虚拟机优化。因此，反射操作的性能低于非反射操作，应避免在性能敏感应用程序中频繁调用反射操作代码片段。
    * 安全限制：反射需要运行时权限，不能在安全管理器(security manager)下进行反射操作。
    * 代码可移植性：反射代码打破了抽象，反射的类库有可能随着平台(JDK)升级发生改变，反射代码中允许执行非反射代码的逻辑例如允许访问私有字段，这些问题都有可能影响到代码的可移植性。
* 反射相关的类库集中在`java.lang.reflect`包和`java.lang`包中。

#### 图解反射核心类的体系
* `java.lang.reflect`包反射核心类有核心类`Class`、`Constructor`、`Method`、`Field`、`Parameter`。
    * 共有的父接口是AnnotatedElement。
    * Constructor、Method、Field共有的父类是AnnotatedElement、AccessibleObject和Member。
    * Constructor、Method共有的父类是AnnotatedElement、AccessibleObject、Member、GenericDeclaration和Executable。
* 在Class中，`getXXX()`方法和`getDeclearedXXX()`方法有所区别。注解类型`Annotation`的操作方法例外，因为基于注解的修饰符必定是public的
    * `getDeclaredMethod(s)`: 返回类或接口声明的所有方法，包括公共、保护、默认(包)访问和私有方法，但不包括继承的方法。对于获取Method对象，`Method[] methods = clazz.getDeclaredMethods()`;返回的是clazz本类所有修饰符(public、default、private、protected)的方法数组，但是不包含继承而来的方法。
    * `getMethod(s)`: 返回某个类的所有公用(public)方法包括其继承类的公用方法，当然也包括它所实现接口的方法。对于获取Method对象，`Method[] methods = clazz.getMethods()`;表示返回clazz的父类、父类接口、本类、本类接口中的全部修饰符为public的方法数组。
    * `getDeclaredField(s)`和`getField(s)`、`getDeclaredConstructor(s)`和`getConstructor(s)`同上。
    * `getDeclaredAnnotation(s)`: 返回直接存在于此元素上的所有注解，此方法将忽略继承的注解，准确来说就是忽略`@Inherited`注解的作用。
    * `getAnnotation(s)`: 返回此元素上存在的所有注解，包括继承的所有注解。

#### Type接口
* `java.lang.reflect.Type`接口是Java中所有类型的共同父类，这些类型包括原始类型、泛型类型、数组类型、类型变量和基本类型。

#### AnnotatedElement接口
* `AnnotatedElement`是一个接口，它定义的方法主要和注解操作相关。

#### Member接口
* Member接口注解提供成员属性的一些描述

#### AccessibleObject类
* `AccessibleObject`是一个普通Java类，实现了`AnnotatedElement`接口，但是对应`AnnotatedElement`的非默认方法的实现都是直接抛异常，也就是`AnnotatedElement`的接口方法必须由`AccessibleObject`的子类去实现，个人认为`AccessibleObject`应该设计为抽象类。

* 一般而言，我们需要通过`getModifiers()`方法判断修饰符是否public，如果是非public，则需要调用`setAccessible(true)`进行修饰符抑制，否则会因为无权限访问会抛出异常。

#### GenericDeclaration接口
* `GenericDeclaration`接口继承自`AnnotatedElement`

#### Executable类
* `Executable`是一个抽象类，它继承自`AccessibleObject`，实现了`Member`和`GenericDeclaration`接口。`Executable`的实现类是`Method`和`Constructor`，它的主要功能是从`Method`和`Constructor`抽取出两者可以共用的一些方法例如注解的操作，参数的操作等等。

#### Modifier类
* `Modifier`主要提供一系列的静态方法，用于判断基于int类型的修饰符参数的具体类型，这个修饰符参数来源于`Class`、`Constructor`、`Method`、`Field`、`Parameter`的`getModifiers()`方法。

#### Class类
* `Class`实现了`Serializable`、`GenericDeclaration`、`Type`、`AnnotatedElement`接口，它提供了类型判断、类型实例化、获取方法列表、获取字段列表、获取父类泛型类型等方法
* 可通过类名进行实例化对象和操作，实例化对象可以不依赖new关键字，这就是反射的强大之处。

#### Constructor类
* `Constructor`用于描述一个类的构造函数。它除了能获取到构造的注解信息、参数的注解信息、参数的信息之外，还有一个很重要的作用是可以抑制修饰符进行实例化，而`Class`的实例化方法`newInstance`只能实例化修饰符为public的类。

#### Method类
* `Method`用于描述一个类的方法。它除了能获取方法的注解信息，还能获取方法参数、返回值的注解信息和其他信息。

#### Field类
* `Field`类用来描述一个类里面的属性或者叫成员变量，通过`Field`可以获取属性的注解信息、泛型信息，获取和设置属性的值等等。

#### Parameter类
* `Parameter`用于描述`Method`或者`Constructor`的参数，主要是用于获取参数的名称。因为在Java中没有形式参数的概念，也就是参数都是没有名称的。
* Jdk1.8新增了`Parameter`用来填补这个问题，使用javac编译器的时候加上`-parameters`参数的话，会在生成的`.class`文件中额外存储参数的元信息，这样会导致`.class`文件的大小增加。


### 数组和枚举
#### 数组类型
* 创建数组实例需要定义数组的长度和组件的类型。数组是由Java虚拟机实现(这一点很重要，这就是为什么JDK类库中没有数组对应的类型的原因，array也不是Java中的保留关键字，操作数组的底层方法都是`native`方法)，数组类型只有继承自`java.lang.Object`的方法，数组的`length`方法实际上并不属于数组类型的一部分，数组的`length`方法其实最终调用的是`java.lang.reflect.Array#getLength()`，注意到这个方法是native方法。`java.lang.reflect.Array`是基于反射操作数组的核心类。
* 因为Java泛型擦除的问题，实际上我们使用`Array#newInstance`方法只能得到一个`Object`类型的结果实例，其实这个结果实例的类型就是`ComponentType[]`，这里只是返回了它的父类(Object)类型实例，因此我们可以直接强转。
* 在非反射方式下，我们可以通过数组实例`.class`通过class字面量直接获取数组类型

#### 枚举类型
* 枚举是一种语言结构(Language Construct)，用于定义可以使用一组固定的名值对表示的类型安全的枚举。所有枚举都继承自`java.lang.Enum`。枚举可以包含一个或者多个枚举常量，这些枚举常量都是该枚举的实例。枚举的声明其实和一个普通的Class的声明相似，因为它可以包含字段、方法和构造函数之类的成员。
* 因为枚举就是普通的Java类，因此反射相关类库中并没有添加一个`java.lang.reflect.Enum`类型

### 泛型
#### 泛型的简介
* a type or method to operate on objects of various types while providing compile-time type safety
* Java虚拟机中不存在泛型，只有普通的类和方法，但是字节码中存放着泛型相关的信息。

#### 理解类型擦除
* 无论何时定义一个泛型类型，都自动提供一个相应的原始类型
* 原始类型的类名称就是带有泛型参数的类删去泛型参数后的类型名称，而原始类型会擦除(Erased)类型变量，并且把它们替换为限定类型(如果没有指定限定类型，则擦除为Object类型)
    * `Pair<T>` -> `Pair`
* 在JDK1.5之前，也就是在泛型出现之前，所有的类型包括基本数据类型(int、byte等)、包装类型、其他自定义的类型等等都可以使用类文件(.class)字节码对应的java.lang.Class描述，也就是java.lang.Class类的一个具体实例对象就可以代表任意一个指定类型的原始类型。
* 在JDK1.5之后，数据类型得到了扩充，出历史原始类型扩充了四种泛型类型：参数化类型(`ParameterizedType`)、类型变量类型(`TypeVariable`)、限定符类型(`WildcardType`)、泛型数组类型(`GenericArrayType`)。
* 泛型并不属于当前Java中的基本成分，如果JVM中引入真正的泛型类型，那么必须涉及到JVM指令集和字节码文件的修改(这个修改肯定不是小的修改，因为JDK当时已经迭代了很多年，而类型是编程语言的十分基础的特性，引入泛型从项目功能迭代角度看可能需要整个JVM项目做回归测试)，这个功能的代价十分巨大，所以Java没有在Java虚拟机层面引入泛型。

#### Type体系
* 在JDK1.5中引入了四种新的泛型类型`java.lang.reflect.ParameterizedType`、`java.lang.reflect.TypeVariable`、`java.lang.reflect.WildcardType`、`java.lang.reflect.GenericArrayType`，包括原来存在的`java.lang.Class`，一共存在五种类型。为了程序的扩展性，引入了`java.lang.reflect.Type`类作为这五种类型的公共父接口，这样子就可以使用`java.lang.reflect.Type`类型参数去接收以上五种子类型的实参或者返回值，由此从逻辑上统一了泛型相关的类型和原始存在的`java.lang.Class`描述的类型。

#### ParameterizedType
* `ParameterizedType`也就是参数化类型，注释里面说到`ParameterizedType`表示一个参数化类型，例如`Collection<String>`，实际上只要带有参数化(泛型)标签`<ClassName>`的参数或者属性，都属于`ParameterizedType`。
* `java.lang.reflect.ParameterizedType`接口继承自`java.lang.reflect.Type`接口，实现类是`sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl`

#### TypeVariable
* `TypeVariable`，也就是类型变量，它是各种类型变量的公共父接口，它主要用来表示带有上界的泛型参数的信息，它和`ParameterizedType`不同的地方是，`ParameterizedType`表示的参数的最外层一定是已知具体类型的(如`List<String>`)，而`TypeVariable`面向的是`K、V、E`等这些泛型参数字面量的表示。常见的`TypeVariable`的表示形式是`<T extends KnownType-1 & KnownType-2>`。

#### WildcardType
* `WildcardType`用于表示通配符(?)类型的表达式的泛型参数，例如`<? extends Number>`等。根据`WildcardType`注释提示：现阶段通配符表达式仅仅接受一个上边界或者下边界，这个和定义类型变量时候可以指定多个上边界是不一样。但是为了保持扩展性，这里返回值类型写成了数组形式。实际上现在返回的数组的大小就是1。
* 这里注意的是`List<? extends Number> list`这个参数整体来看是`ParameterizedType`类型，剥掉第一次List之后的`? extends Number`是`WildcardType`类型。

#### GenericArrayType
* `GenericArrayType`，也就是泛型数组，也就是元素类型为泛型类型的数组实现了该接口。它要求元素的类型是`ParameterizedType`或`TypeVariable`(实际中发现元素是`GenericArrayType`也是允许的)。
    ```java
    List<String>[] listArray; //是GenericArrayType,元素是List<String>类型，也就是ParameterizedType类型
    T[] tArray; //是GenericArrayType,元素是T类型，也就是TypeVariable类型
    ```

#### 泛型的约束
* 使用Java泛型的时候需要考虑一些限制，这些限制大多数是由泛型类型擦除引起的。
    * 不能用基本类型实例化类型参数，也就是8种基本类型不能作为泛型参数。
    * 运行时的类型查询只能适用于原始类型(非参数化类型)。
        ```java
        if(a instanceof Pair)   
        ```
    * 不能创建参数化类型的数组，例如`Pair<String>[] arr = new Pair<String>[10]`是非法的。
    * 不能实例化类型变量或者类型变量数组，例如`T t = new T()或者T[] arr = new T[10]`都是非法的。
    * 不能在静态域或者方法中引用类型变量，例如`private static T singleInstance;`这样是非法的。
    * 不能抛出或者捕获泛型类型变量，但是如果在异常规范中使用泛型类型变量则是允许的。
    * 通过使用`@SuppressWarnings("unchecked")`注解可以消除Java类型系统的部分基本限制，一般使用在强制转换原始类型为泛型类型(只是在编译层面告知编译器)的情况

#### 泛型数组的问题
* 在Java泛型约束中，无法实例化参数化类型数组，根本原因在于泛型类型的擦除和数组会记录元素类型的特性。
* 类型变量数组的实例化也是非法的，这是因为类型变量仅仅是编译期的字面量，其实和Java的类型体系是不相关的。
* 参数化类型数组和类型变量数组可以作为方法入参变量或者类的成员变量。

#### 无限定通配符
* 泛型中支持无限定通配符<?>，使用无限定通配符类型的实例有以下限制：
    * 所有的Getter方法只能返回Object类型的值。
    * 所有的Setter方法只能赋值null，其他类型的值的设置都是非法的。
    
#### 桥方法


### 动态代理
* Java动态代理机制的出现，使得Java开发人员不用手工编写代理类，只要简单地指定一组接口及委托类对象，便能动态地获得代理类。代理类会负责将所有的方法调用分派到委托对象上反射执行，在分派执行的过程中，开发人员还可以按需调整委托类对象及其功能，这是一套非常灵活有弹性的代理框架。Java动态代理实际上通过反射技术，把代理对象和被代理对象(真实对象)的代理关系建立延迟到程序运行之后，动态创建新的代理类去完成对真实对象的代理操作(可以改变原来真实对象的方法行为)，这一点成为了当前主流的AOP框架和延迟加载功能的基础。

#### 设计模式中的代理模式
* 代理模式是一种常用的设计模式，其目的就是为其他对象提供一个代理以控制对某个对象的访问。代理类负责为委托类预处理消息，过滤消息并转发消息，以及进行消息被委托类执行后的后续处理。
* 代理模式主要包括三种角色:
    * Subject抽象主题角色: 一般定义为抽象类或者接口，是作为功能的定义，提供一系列抽象的功能方法。
    * RealSubject具体(真实)主题角色: 一般称为被委托角色或者被代理角色，它是Subject的一个具体实现。
    * ProxySubject代理主题角色: 一般称为委托角色或者代理角色，一般ProxySubject也实现(或者继承)Subject，接收一个具体的Subject实例RealSubject，在RealSubject处理前后做预定义或者后置操作，甚至可以直接忽略RealSubject原来的方法。
* 代理模式有几个比较大的优点:
    * 职责清晰: 也就是真实主题角色只需要实现具体的逻辑，不需关注代理类的职责，而代理类也只需要处理预处理和后置的逻辑，类的职责分明。
    * 高扩展性: 由于职责分明，也就是真实主题角色可以随时修改实现，这样就能通过更新或者替换真实主题的实现并且不改变代理主题角色的情况下改变具体功能。
    * 高灵活性: 主要体现在后面提到的动态代理。

#### JDK动态代理的核心API
* JDK动态代理提供外部使用的主要依赖两个类:
    * `java.lang.reflect.Proxy`: 它的核心功能是提供静态方法来为一组接口动态地生成代理类并且返回代理实例对象，类似于代理类实例的工厂类。
    * `java.lang.reflect.InvocationHandler`: 是调用处理器接口，它自定义了一个invoke方法，用于集中处理在动态代理类对象上的方法调用，通常在该方法中实现对委托类的代理访问。

#### JDK动态代理的流程
* DK动态代理的使用流程:
    1. 通过实现`java.lang.reflect.InvocationHandler`接口创建自定义的调用处理器。
    2. 通过为`java.lang.reflect.Proxy`类指定`ClassLoader`对象和一组interface来创建动态代理类。
    3. 通过反射机制获得动态代理类的构造函数，其唯一参数类型是调用处理器接口类型。
    4. 通过构造函数创建动态代理类实例，构造时调用处理器对象作为参数被传入。
* 伪代码: 
    ```java
    // InvocationHandlerImpl 实现了 InvocationHandler 接口，并能实现方法调用从代理类到委托类的分派转发
    // 其内部通常包含指向委托类实例的引用，用于真正执行分派转发过来的方法调用
    InvocationHandler handler = new InvocationHandlerImpl(..); 
     
    // 通过Proxy为包括Interface接口在内的一组接口动态创建代理类的类对象
    Class clazz = Proxy.getProxyClass(classLoader, new Class[] { Interface.class, ... }); 
     
    // 通过反射从生成的类对象获得构造函数对象
    Constructor constructor = clazz.getConstructor(new Class[] { InvocationHandler.class }); 
     
    // 通过构造函数对象创建动态代理类实例
    Interface Proxy = (Interface)constructor.newInstance(new Object[] { handler });
    ```

#### JDK动态代理的机制
* 首先是JDK动态代理生成的代理类本身的特点:
    * 包: 如果所代理的接口都是public的，那么它将被定义在包`com.sun.proxy`；如果所代理的接口中有非public的接口，那么它将被定义在该接口所在包，值得注意的是，如果接口数组中存在非public的接口，那么它们必须在同一个包路径下，否则会抛异常。这样设计的目的是为了最大程度的保证动态代理类不会因为包管理的问题而无法被成功定义并访问。






