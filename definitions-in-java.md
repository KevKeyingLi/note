
- [Java Ecosystem](#Java-Ecosystem)
    * [Java Virtual Machine](#Java-Virtual-Machine)
- [Methods](#Methods)
    * [Pass by Value](#Pass-by-Value)
    * [Pass by Reference](#Pass-by-Reference)
    * [Method Overloading](#Method-Overloading)
- [Classes](#Classes)
    * [Accessibility](#Accessibility)
    * [The Object Class](#The-Object-Class)
    * [Constructors](#Constructors)
    * [Initialization](#Initialization)
    * [Nested Classes](#Nested-Classes)
    * [Abstract Classes](#Abstract-Classes)
    * [Final Class](#Abstract-Classes)
    * [Super Keyword](#Super-Keyword)
    * [Finalize](#Finalize)
- [Interfaces](Interfaces)
- [Inheritance](#Inheritance)
- [Lambda Expressions](#Lambda-Expressions)
- [Generics](#Generics)
    * [Raw Types](#Raw-Types)
    * [Generic Types](#Generic-Types)
    * [Bounded Types](#Bounded-Types)
    * [Type Inference](#Type-Inference)
    * [Erasure](#Erasure)
    * [Bridge Methods](#Bridge-Methods)
    * [Wildcard](#Wildcard)
    * [The Get & the Put Principle](#The-Get-&-the-Put-Principle)
- [Memory Management](#Memory-Management)
    * [Memory Areas](#Memory-Areas)
    * [Reference Strengths](#Reference-Strengths)
    * [Garbage Collection](#Garbage-Collection)
    * [Memory Tuning](#Memory-Tuning)
- [Collections](#Collections)
    * [Collection Types](#Collection-Types)
    * [Iterating in Java](#Iterating-in-Java)
- [Exceptions](#Exceptions)
    * [Checked vs Unchecked](#Checked-vs-Unchecked)
    * [Catch Block](#Catch-Block)
- [Reflection](#Reflection)
    * [java.lang.Class](#java.lang.Class)
    * [Class Modifiers](#Class-Modifiers)
    * [Instantiation using Reflection](#Instantiation-using-Reflection)
    * [Classloaders](#Classloaders)
- [Serialization](#Serialization)
- [Miscellaneous Topics](#Miscellaneous-Topics)
    * [Types](#Types)
    * [Keywords](#Keywords)
    * [Annotations](#Annotations)
    * [Boxing](#Boxing)
    * [Unboxing](#Unboxing)
    * [Package](#Package)
    * [Strings](#Strings)
    * [Casting](#Casting)
- [Java in Practice](#Java-in-Practice)
    * [Quick Note](#Quick-Note)
    * [Object Creation](#Object-Creation)
    * [Using Objects](#Using-Objects)
    * [Designing Classes](#Designing-Classes)
    * [Inheritance vs Composition](#Inheritance-vs-Composition)
    * [Interfaces vs Abstract Classes](#Interfaces-vs-Abstract-Classes)
    * [Using Generics](#Using-Generics)
    * [Enums & Annotations](#Enums-&-Annotations)
    * [Method Design](#Method-Design)
    * [General Best Practices](#General-Best-Practices)
    * [Exceptions Handling](#Exceptions-Handling)
    * [Concurrency](#Concurrency)

---

## Java Ecosystem
* Java is platform independent.
    * Once a Java program is written, it gets compiled into byte code, which can then be run on any Java Virtual Machine.
    * Different operating systems and hardware architectures have JVMs custom designed for themselves and all JVMs can run the same bytecode.
* Java platform consist of:
    * Java Virtual Machine (JVM)
    * Java Application Programming Interface (Java API)
* The Java Runtime Environment (JRE) includes the Java Virtual Machine and the standard Java APIs. The JRE contains just enough to execute a Java application, but not to compile it.
* The Java Software Development Kit (Java SDK) is JRE plus the Java compiler, and a set of other tools.
* javac compiler compile .java file to .class file.


### Java Virtual Machine
* JVM specification should be able to run code compiled into Java bytecode irrespective of the language in which the code was originally written.
* JVM architecture
    * Class Loader Subsystem(类装载器)
        * responsible for more than just locating and importing the binary data for classes.
        * It must also verify the correctness of imported classes, allocate and initialize memory for class variables, and assist in the resolution of symbolic references.
    * Runtime Data Area(运行数据区)
        * The memory areas allocated by the JVM are called Runtime Data Area. 
        * These consist of method area, heap area, stack, pc registers and native stack.
            * 方法区(Method Area)：用于存储类结构信息的地方，包括常量池、静态变量、构造函数等。虽然JVM规范把方法区描述为堆的一个逻辑部分， 但它却有个别名non-heap(非堆)，所以大家不要搞混淆了。方法区还包含一个运行时常量池。
            * java堆(Heap)：存储java实例或者对象的地方。这块是GC的主要区域(后面解释)。从存储的内容我们可以很容易知道，方法区和堆是被所有java线程共享的。
            * java栈(Stack)：java栈总是和线程关联在一起，每当创建一个线程时，JVM就会为这个线程创建一个对应的java栈。在这个java栈中又会包含多个栈帧，每运行一个方法就创建一个栈帧，用于存储局部变量表、操作栈、方法返回值等。每一个方法从调用直至执行完成的过程，就对应一个栈帧在java栈中入栈到出栈的过程。所以java栈是现成私有的。
            * 程序计数器(PC Register)：用于保存当前线程执行的内存地址。由于JVM程序是多线程执行的(线程轮流切换)，所以为了保证线程切换回来后，还能恢复到原先状态，就需要一个独立的计数器，记录之前中断的地方，可见程序计数器也是线程私有的。
            * 本地方法栈(Native Method Stack)：和java栈的作用差不多，只不过是为JVM使用到的native方法服务的。
    * Execution Engine(执行引擎)
        * The execution is responsible for the actual execution of the bytecode.
        * It consists of three components: 
            * interpreter
                * a translator that converts Java bytecode into native machine code
            * just-in-time compiler
                * faster but requires more memory
                * In this scheme, the bytecodes of a method are compiled to native machine code the first time the method is invoked. The native machine code for the method is then cached, so it can be re-used the next time that same method is invoked.
            * garbage collector
    ```
                +-------------+  +------------------------+
                | class files |->| Class Loader Subsystem |
                +-------------+  +------------------------+
                                            ||
    +------------------------------------------------------------------------------------+
    | +-------------+ +------+ +-------------+ +--------------+ +----------------------+ |
    | | method area | | heap | | Java stacks | | pc registers | | native method stacks | |
    | +-------------+ +------+ +-------------+ +--------------+ +----------------------+ |
    |                                Runtime Data Area                                   |
    +------------------------------------------------------------------------------------+
                                        ||
    +------------------+   +--------------------------+  +-------------------------+
    | execution engine |<->| native method interface  |<-| native method libraries |
    +------------------+   +--------------------------+  +-------------------------+
    ```
* A runtime instance of the Java virtual machine runs a single Java application. When a Java application starts, a runtime instance is born. When the application completes, the instance dies.


## Methods
### Pass by Value
* 值传递：方法调用时，实际参数把它的值传递给对应的形式参数，方法执行中形式参数值的改变不影响实际参数的值。
* 传递值的数据类型：八种基本数据类型和String(这样理解可以，但是事实上String也是传递的地址,只是string对象和其他对象是不同的，string对象是不能被改变的，内容改变就会产生新对象。那么StringBuffer就可以了，但只是改变其内容。不能改变外部变量所指向的内存地址)。

### Pass by Reference
* 引用传递：也称为传地址。方法调用时，实际参数的引用(地址，而不是参数的值)被传递给方法中相对应的形式参数，在方法执行中，对形式参数的操作实际上就是对实际参数的操作，方法执行中形式参数值的改变将会影响实际参数的值。
* 传递地址值的数据类型：除String以外的所有复合数据类型，包括数组、类和接口 

### Method Overloading
* 重载(overloading) 是在一个类里面，方法名字相同，而参数不同。返回类型可以相同也可以不同。
* 重载规则:
    * 被重载的方法必须改变参数列表(参数个数或类型不一样)；
    * 被重载的方法可以改变返回类型；
    * 被重载的方法可以改变访问修饰符；
    * 被重载的方法可以声明新的或更广的检查异常；
    * 方法能够在同一个类中或者在一个子类中被重载。
    * 无法以返回值类型作为重载函数的区分标准。
* the static main method can be overloaded.

## Classes
### Accessibility
* public
    * 对所有类可见。
    * 如果几个相互访问的 public 类分布在不同的包中，则需要导入相应 public 类所在的包。由于类的继承性，类所有的公有方法和变量都能被其子类继承。
* private
    * 在同一类内可见。
    * 类和接口不能声明为private。
* default
    * 在同一包内可见，不使用任何修饰符。
* protected
    * 对同一包内的类和所有子类可见。
    * Protected访问修饰符不能修饰类和接口，方法和成员变量能够声明为protected，但是接口的成员变量和成员方法不能声明为protected。
    * 子类能访问Protected修饰符声明的方法和变量，这样就能保护不相关的类使用这些方法和变量。
* 方法继承的规则：
    * 父类中声明为 public 的方法在子类中也必须为 public。
    * 父类中声明为 protected 的方法在子类中要么声明为 protected，要么声明为 public，不能声明为 private。
    * 父类中声明为 private 的方法，不能够被继承。

### The Object Class
* Object类是所有类的父类
* clone()方法
    * 保护方法，实现对象的浅复制，只有实现了Cloneable接口才可以调用该方法，否则抛出CloneNotSupportedException异常。
    * Shallow Clone与Deep Clone
        * Shallow Clone仅仅是简单地执行域对域的copy，产生新的reference,而不是数据
        * deep Clone产生一个完全一样的object
* toString()方法
    * 返回一个字符串，该字符串由类名(对象是该类的一个实例)、at 标记符“@”和此对象哈希码的无符号十六进制表示组成。
* getClass()方法
    * 返回次Object的运行时类类型。
    * 不可重写，要调用的话，一般和getName()联合使用，如getClass().getName();
* finalize()方法
    * 该方法用于释放资源。
    * 它的工作原理是：一旦垃圾回收器准备好释放对象占用的存储空间，将首先调用其finalize()方法。并且在下一次垃圾回收动作发生时，才会真正回收对象占用的内存。
    * 关于垃圾回收，有三点需要记住：
        1. 对象可能不被垃圾回收。只要程序没有濒临存储空间用完的那一刻，对象占用的空间就总也得不到释放。
        2. 垃圾回收并不等于“析构”。
        3. 垃圾回收只与内存有关。使用垃圾回收的唯一原因是为了回收程序不再使用的内存。
* equals()方法
    * Object中的equals方法是直接判断this和obj本身的值是否相等，即用来判断调用equals的对象和形参obj所引用的对象是否是同一对象
* hashCode()方法
    * 返回该对象的哈希码值
    * 该方法用于哈希查找，可以减少在查找中使用equals的次数，重写了equals方法一般都要重写hashCode方法。这个方法在一些具有哈希功能的Collection中用到。
* wait()方法
    * wait方法就是使当前线程等待该对象的锁，当前线程必须是该对象的拥有者，也就是具有该对象的锁。wait()方法一直等待，直到获得锁或者被中断。
    * 调用该方法后当前线程进入睡眠状态，直到以下事件发生。
        1. 其他线程调用了该对象的notify方法。
        2. 其他线程调用了该对象的notifyAll方法。
        3. 其他线程调用了interrupt中断该线程。
        4. 时间间隔到了。
* notify()方法
    * 该方法唤醒在该对象上等待的某个线程。
* notifyAll方法
    * 该方法唤醒在该对象上等待的所有线程。

### Constructors
* 构造器是在对象建立时由jvm调用, 给对象初始化。
* 构造器名要与类名一样，没有返回值类型。
* 如果不在类中创建自己的构造器，编译器会自动生成默认的不带参数的构造器。
* 如果只创建了带参数的构造器，那么编译器不会自动添加无参的构造器。
* 在每个构造器中，如果使用了重载构造器this()方法，或者父类的构造器super()方法，那么this()方法或者super()方法必须放在第一行。
* 除了编译器生成的构造器，而且没有显式地调用super()方法，那么编译器会插入一个super()无参调用。
* 抽象类有构造器。

### Initialization
```
           +-------------------------------------------+                    +------------------------------------------------------------------+
+-------+  |                   Linking                 |  +--------------+  |                          Object Live Circle                      |
|Loading|->|+------------+  +-----------+  +----------+|->|Initialization|->|+---------------------+  +------------------+  +-----------------+|
+-------+  ||Verification|->|Preparation|->|Resolution||  +--------------+  ||Object Initialization|->|Garbage Collection|->|Object terminated||
           |+------------+  +-----------+  +----------+|                    |+---------------------+  +------------------+  +-----------------+|
           +-------------------------------------------+                    +------------------------------------------------------------------+
```
* Java类的初始化
    * Java编译器把所有的类变量初始化语句和静态初始化器通通收集到`<clinit>`方法中，该方法只能被JVM调用，专门承担初始化工作。
    * 初始化一个类必须保证其直接超类已被初始化。
    * 并非所有类都拥有`<clinit>()`方法。以下类不会拥有`<clinit>`方法：
        * 该类既没有声明任何类变量，也没有静态初始化语句。
        * 该类声明了类变量，但没有使用类变量初始化语句或静态初始化语句初始化。
        * 该类只包含静态final变量的类变量初始化语句，并且类变量初始化语句是常量表达式。
    * Java类初始化的时机
        * 规范定义类的初始化时机为“initialize on first active use”，即“在首次主动使用时初始化”。装载和链接在初始化之前就要完成。
        * 首次主动使用的情形：
            * 创建类的新实例--new，反射，克隆或反序列化；
            * 调用类的静态方法；
            * 操作类和接口的静态字段；(final字段除外)
            * 调用Java的特定的反射方法；
            * 初始化一个类的子类；
            * 指定一个类作为Java虚拟机启动时的初始化类(含有main方法的启动类)。
* Java对象初始化
    * 编译器为每个类生成至少一个实例初始化方法，即`<init>`()方法。此方法与源程序里的每个构造方法对应。如果类没有声明构造方法，则生成一个默认构造方法，该方法仅调用父类的默认构造方法，同时生成与该默认构造方法对应的`<init>`()方法。
    * `<init>`()方法内容大概为：
        * 调用另一个`<init>`()方法(本类的另外一个`<init>()`方法或父类的`<init>`()方法);
        * 初始化实例变量;
        * 与其对应的构造方法内的字节码
    * Java对象初始化的时机
        * 对象初始化又称为对象实例化。Java对象在其被创建时初始化。有两种方式创建Java对象：
            * 一种是显示对象创建，通过new关键字来调用一个类的构造函数，通过构造函数创建一个对象。
            * 一种是隐式对象创建：
                * 加载一个包含String字面量的类或接口会引起一个新的String对象创建，除非包含相同字面量的String对象已经在JVM中存在了。
                * 自动装箱机制可能会引起一个原子类型的包装类对象被创建。
                * String连接符也可能会引起新的String或者StringBuilder对象被创建，同时还有可能引起原子类型的包装对象被创建。
    * 对象实例初始化流程
        1. 进入子类构造函数
        2. 子类成员变量的内存被分配
        3. 调用父类的构造函数
        4. 父类成员变量的内存被分配
        5. 执行父类构造函数的命令
        7. 父类的成员变量初始化被调用
        8. 执行子类构造函数的命令
        9. 子类的成员变量初始化被调用
* Refs:
    * > https://www.cnblogs.com/jxzheng/p/5191037.html
    * > https://www.cnblogs.com/kevinwu/archive/2012/05/22/2498638.html
    * > https://www.ibm.com/developerworks/cn/java/j-lo-clobj-init/
    * > https://www.javaworld.com/article/3040564/java-101-class-and-object-initialization-in-java.html
    * > https://blog.csdn.net/w1196726224/article/details/56529615

### Nested Classes
* 成员内部类
    * 定义为位于另一个类的内部
    * 可以无条件访问外部类的所有成员属性和成员方法(包括private成员和静态成员)
    * 在外部类中如果要访问成员内部类的成员，必须先创建一个成员内部类的对象，再通过指向这个对象的引用来访问
    * 如果要创建成员内部类的对象，前提是必须存在一个外部类的对象
    * 如果成员内部类Inner用private修饰，则只能在外部类的内部访问，如果用public修饰，则任何地方都能访问，如果用protected修饰，则只能在同一个包下或者继承外部类的情况下访问，如果是默认访问权限，则只能在同一个包下访问。这一点和外部类有一点不一样，外部类只能被public和包访问两种权限修饰
* 局部内部类
    * 局部内部类是定义在一个方法或者一个作用域里面的类，它和成员内部类的区别在于局部内部类的访问仅限于方法内或者该作用域内
    * 局部内部类就像是方法里面的一个局部变量一样，是不能有public、protected、private以及static修饰符的
* 匿名内部类
    * 匿名内部类是唯一一种没有构造器的类。正因为其没有构造器，所以匿名内部类的使用范围非常有限，大部分匿名内部类用于接口回调。
    * 匿名内部类在编译的时候由系统自动起名为Outter$1.class。
    * 一般来说，匿名内部类用于继承其他类或是实现接口，并不需要增加额外的方法，只是对继承方法的实现或是重写。
* 静态内部类
    * 静态内部类是不需要依赖于外部类的，这点和类的静态成员属性有点类似，并且它不能使用外部类的非static成员变量或者方法
* Refs:
    * > https://www.cnblogs.com/dolphin0520/p/3811445.html
    * > https://www.jianshu.com/p/e385ce41ca5b

### Abstract Classes
* 而抽象类是指在普通类的结构里面增加抽象方法的组成部分。拥有抽象方法的类就是抽象类，抽象类要使用abstract关键字声明。
* 抽象方法是指没有方法体的方法，同时抽象方法还必须使用关键字abstract做修饰。
* 抽象类的使用原则
    * 抽象方法必须为public或者protected，缺省情况下默认为public
    * 抽象类不能直接实例化，需要依靠子类采用向上转型的方式处理
    * 抽象类必须有子类，使用extends继承，一个子类只能继承一个抽象类
    * 子类如果不是抽象类，则必须覆写抽象类之中的全部抽象方法
* 虽然一个类的子类可以去继承任意的一个普通类，可是从开发的实际要求来讲，普通类尽量不要去继承另外一个普通类，而是去继承抽象类。
* 抽象类的使用限制
    * 由于抽象类里会存在一些属性，那么抽象类中一定存在构造方法，其存在目的是为了属性的初始化。并且子类对象实例化的时候，依然满足先执行父类构造，再执行子类构造的顺序。
    * 外部抽象类不允许使用static声明，而内部的抽象类运行使用static声明。
    * 任何时候，如果要执行类中的static方法的时候，都可以在没有对象的情况下直接调用，对于抽象类也一样。
* Refs: 
    * > https://blog.csdn.net/wei_zhi/article/details/52736350

### Final Class
* final变量能被显式地初始化并且只能初始化一次。
* 被声明为final的对象的引用不能指向不同的对象。但是final对象里的数据可以被改变。也就是说final对象的引用不能改变，但是里面的值可以改变。
* final修饰符通常和static修饰符一起使用来创建类常量。
* 类中的final方法可以被子类继承，但是不能被子类修改。
* 声明final方法的主要目的是防止该方法的内容被修改。
* final类不能被继承，没有类能够继承 final 类的任何特性。

### Super Keyword
* 主要存在于子类方法中，用于指向子类对象中父类对象
    * 访问父类的属性
    * 访问父类的函数
    * 访问父类的构造函数
* Refs:
    * > https://blog.csdn.net/qq_33642117/article/details/51919528

### Finalize
* 一旦垃圾回收器准备好释放对象占用的存储空间，将首先调用其finalize( )方法，并且在下一次垃圾回收动作发生时，才会真正回收对象占用的内存。所以要是你打算用 finalize( )，就能在“垃圾回收时刻”做一些重要的清除工作。
* 对象可能不被回收。
    * 垃圾回收并不等于“析构”。
        * C++中调用拆构函数，对象一定会被销毁，但是Java中没这个概念，要做类似的清除工作，你必须自己动手创建一个执行清除工作的普通方法。
            * 例如，假设某个对象在创建过程中，会将自己绘制到屏幕上。要是你不明确地从屏幕上将其擦除，它可能永远得不到清除。如果在finalize()里加入某种擦除功能，当“垃圾回收”发生时(不能保证一定会发生)，finalize()得到了调用，图像就会被擦除。
        * 垃圾回收器一直都没有释放你创建的任何对象的存储空间，如果程序执行结束，那些资源会全部交还给操作系统。
    * 垃圾回收只与内存有关。
        * 垃圾回收器存在的唯一原因是为了回收程序不再使用的内存。
        * 如果对象中含有其他对象，finalize()不会释放那些对象呢。无论对象是如何创建的，垃圾回收器都会负责释放对象占据的所有内存。
        * 如果Java虚拟机(JVM)并未面临内存耗尽的情形，它是不会浪费时间在回收垃圾以恢复内存上的。
* Refs:
    * > https://blog.csdn.net/wuha0/article/details/7233890

## Interfaces
* 接口中的所有属性默认的修饰符是public static final
* 接口中的所有方法默认的修饰符是public abstract
* 接口的特点
    * 类实现接口可以通过implements实现，实现接口的时候必须把接口中的所有方法实现,一个类可以实现多个接口。
    * 接口中定义的方法不能有方法体。由于接口中的方法默认都是抽象的，所以不能被实例化。
    * 如果实现类中要访问接口中的成员，不能使用super关键字。因为两者之间没有显示的继承关系，况且接口中的成员成员属性是静态的。可以使用接口名直接访问。
    * 接口没有构造方法。
* A functional interface is an interface that contains only one abstract method. 
* 现在的interface可以提供默认实现体，以方便对老接口的更新。
* Refs:
    * > https://blog.csdn.net/qq_33642117/article/details/51926634

## Inheritance
* 继承是从已有的类中派生出新的类，新的类能吸收已有类的数据属性和行为，并能扩展新的能力。
* 子类即使不扩充父类，也能维持父类的操作。
* 继承的限制
    * 一个子类只能够继承一个父类，存在单继承局限。
    * 在一个子类继承的时候，实际上会继承父类之中的所有操作(属性、方法)，但是需要注意的是，对于所有的非私有操作属于显式继承，而所有的私有操作属于隐式继承(间接完成)，通过setter、getter方法间接的进行操作。
    * 实例化子类对象，会默认先执行父类构造，调用父类构造的方法体执行，而后再实例化子类对象，调用子类的构造方法。而这个时候，对于子类的构造而言，就相当于隐含了一个super()。
    * 默认调用的是无参构造，而如果这个时候父类没有无参构造，则子类必须通过super()调用指定参数的构造方法。
    * super调用父类构造时，一定要放在构造方法的首行上。
* abstract class vs. interface
    * Use an abstract class when subclasses share state or use common functionality. Or you require to declare non-static, non-final fields or need access modifiers other than public.
    * Use an interface if you expect unrelated classes would implement your interface.
* polymorphism
    * Polymorphism is when you can treat an object as a generic version of something, but when you access it, the code determines which exact type it is and calls the associated code. 
* Any method inherited from a class or a superclass is invoked over any default method inherited from an interface.(先类后接口)
* If a subclass defines a static method with the same signature as a static method in the superclass, then the method in the subclass hides the one in the superclass.
* Instance methods are preferred over interface default methods.
* Static methods in interfaces are never inherited.
* Methods that are already overridden by other candidates are ignored. This circumstance can arise when supertypes share a common ancestor.
* If two or more independently defined default methods conflict, or a default method conflicts with an abstract method, then the Java compiler produces a compiler error. You must explicitly override the supertype methods.
* The access specifier for an overriding method can allow more, but not less, access than the overridden method.
* You will get a compile-time error if you attempt to change an instance method in the superclass to a static method in the subclass, and vice versa.

## Lambda Expressions
* Lambda 表达式的加入，使得 Java 拥有了函数式编程的能力。
* 在其它语言中，Lambda 表达式的类型是一个函数；但在 Java 中，Lambda 表达式被表示为对象，因此它们必须绑定到被称为功能接口的特定。
* Lambda 表达式是一个匿名函数，这是一种没有声明的方法，即没有访问修饰符，返回值声明和名称。
* Java中的Lambda表达式通常使用语法是 (argument) -> (body)
* Lambda 表达式的结构：
    * Lambda 表达式可以具有零个，一个或多个参数。
    * 可以显式声明参数的类型，也可以由编译器自动从上下文推断参数的类型。例如 (int a) 与刚才相同 (a)。
    * 参数用小括号括起来，用逗号分隔。例如 (a, b) 或 (int a, int b) 或 (String a, int b, float c)。
    * 空括号用于表示一组空的参数。例如 () -> 42。
    * 当有且仅有一个参数时，如果不显式指明类型，则不必使用小括号。例如 a -> return a*a。
    * Lambda 表达式的正文可以包含零条，一条或多条语句。
    * 如果 Lambda 表达式的正文只有一条语句，则大括号可不用写，且表达式的返回值类型要与匿名函数的返回类型相同。
    * 如果 Lambda 表达式的正文有一条以上的语句必须包含在大括号(代码块)中，且表达式的返回值类型要与匿名函数的返回类型相同。
* 双冒号(::)操作符是Java中的方法引用。
    * 当们使用一个方法的引用时，目标引用放在::之前，目标引用提供的方法名称放在::之后，即“目标引用::方法”。
* 功能接口(Functional interface)
    * 功能接口指只有一个抽象方法的接口。
    * 我们使用匿名内部类实例化功能接口的对象，而使用Lambda表达式，可以简化写法。
* Refs:
    * > https://segmentfault.com/a/1190000009186509
    * > https://www.jianshu.com/p/a01d84c57180
    * > https://blog.csdn.net/jinzhencs/article/details/50748202

## Generics
### Generic Types
* 泛型，即“参数化类型”。
    * 参数化类型就是将类型由原来的具体的类型参数化，类似于方法中的变量参数，此时类型也定义成参数形式(类型形参)，然后在使用/调用时传入具体的类型(类型实参)。
    * 泛型的本质是在不创建新的类型的情况下，通过泛型指定的不同类型来控制形参具体限制的类型。也就是说在泛型使用过程中，操作的数据类型被指定为一个参数，这种参数类型可以用在类、接口和方法中，分别被称为泛型类、泛型接口、泛型方法。
* 泛型只在编译阶段有效，在编译之后程序会采取去泛型化的措施，以下两个泛型容器在编译后被视为类型相同。
    ```java
    List<String> stringArrayList = new ArrayList<String>();
    List<Integer> integerArrayList = new ArrayList<Integer>();
    ```
    * 在编译过程中，正确检验泛型结果后，会将泛型的相关信息擦出，并且在对象进入和离开方法的边界处添加类型检查和类型转换的方法。也就是说，泛型信息不会进入到运行时阶段。
* 泛型的使用
    * 泛型类
        * 通过泛型可以完成对一组类的操作对外开放相同的接口，如：List、Set、Map。
        * 泛型类的最基本写法
            ```java
            public class Generic<T> { 
                private T key;
                public Generic(T key) { this.key = key; }
                public T getKey() { return key; }
            }
            Generic<Integer> genericInteger = new Generic<Integer>(123456);
            Generic<String> genericString = new Generic<String>("key_vlaue");
            ```
        * 定义的泛型类不一定要传入泛型类型实参。如果传入泛型实参，则会根据传入的泛型实参做相应的限制，此时泛型才会起到本应起到的限制作用。如果不传入泛型类型实参的话，在泛型类中使用泛型的方法或成员变量定义的类型可以为任何的类型。
            ```java
            Generic generic = new Generic("111111");
            Generic generic1 = new Generic(4444);
            Generic generic2 = new Generic(55.55);
            Generic generic3 = new Generic(false);
            ```
        * 泛型的类型参数只能是类类型，不能是简单类型，不能对确切的泛型类型使用instanceof操作。
    * 泛型接口
        * 泛型接口与泛型类的定义及使用基本相同。
            ```java
            public interface Generator<T> {
                public T next();
            }
            ```
        * 当实现泛型接口的类，未传入泛型实参时
            * 未传入泛型实参时，与泛型类的定义相同，在声明类的时候，需将泛型的声明也一起加到类中，否则报错。
            ```java
            class FruitGenerator<T> implements Generator<T>{
                @Override
                public T next() {
                    return null;
                }
            }
            ```
        * 当实现泛型接口的类，传入泛型实参时
            * 所有使用泛型的地方都要替换成传入的实参类型
            ```java
            public class FruitGenerator implements Generator<String> {
                private String[] fruits = new String[]{"Apple", "Banana", "Pear"};
                @Override
                public String next() {
                    Random rand = new Random();
                    return fruits[rand.nextInt(3)];
                }
            }
            ```
    * 泛型方法
        * 泛型方法是在调用方法的时候指明泛型的具体类型。
            ```java
            public <T> T genericMethod(Class<T> tClass)throws InstantiationException,IllegalAccessException{
                T instance = tClass.newInstance();
                return instance;
            }
            ```
            * public与返回值中间`<T>`可以理解为声明此方法为泛型方法。
            * 只有声明了<T>的方法才是泛型方法，泛型类中的使用了泛型的成员方法并不是泛型方法。
        * 泛型方法的基本用法
            ```java
            public class GenericTest {
                public class Generic<T>{     
                    private T key;
                    public Generic(T key) {this.key = key;}
                    //虽然在方法中使用了泛型，但是这并不是一个泛型方法。
                    //这只是类中一个普通的成员方法，只不过他的返回值是在声明泛型类已经声明过的泛型。
                    public T getKey(){
                        return key;
                    }
                    /**
                    * 这个方法显然是有问题的，在编译器会给我们提示这样的错误信息"cannot reslove symbol E"
                    * 因为在类的声明中并未声明泛型E，所以在使用E做形参和返回值类型时，编译器会无法识别。
                    public E setKey(E key) {this.key = keu}
                    */
                }

                /** 
                * 这才是一个真正的泛型方法。
                * 首先在public与返回值之间的<T>必不可少，这表明这是一个泛型方法，并且声明了一个泛型T
                * 这个T可以出现在这个泛型方法的任意位置.
                * 泛型的数量也可以为任意多个 
                *    如：public <T,K> K showKeyName(Generic<T> container){...}
                */
                public <T> T showKeyName(Generic<T> container){
                    System.out.println("container key :" + container.getKey());
                    //当然这个例子举的不太合适，只是为了说明泛型方法的特性。
                    T test = container.getKey();
                    return test;
                }

                //这也不是一个泛型方法，这就是一个普通的方法，只是使用了Generic<Number>这个泛型类做形参而已。
                public void showKeyValue1(Generic<Number> obj) { Log.d("泛型测试","key value is " + obj.getKey()); }

                //这也不是一个泛型方法，这也是一个普通的方法，只不过使用了泛型通配符?
                //同时这也印证了泛型通配符章节所描述的，?是一种类型实参，可以看做为Number等所有类的父类
                public void showKeyValue2(Generic<?> obj) { Log.d("泛型测试","key value is " + obj.getKey()); }

                /**
                * 这个方法是有问题的，编译器会为我们提示错误信息："UnKnown class 'E' "
                * 虽然我们声明了<T>,也表明了这是一个可以处理泛型的类型的泛型方法。
                * 但是只声明了泛型类型T，并未声明泛型类型E，因此编译器并不知道该如何处理E这个类型。
                public <T> T showKeyName(Generic<E> container){...}  
                */
                /**
                * 这个方法也是有问题的，编译器会为我们提示错误信息："UnKnown class 'T' "
                * 对于编译器来说T这个类型并未项目中声明过，因此编译也不知道该如何编译这个类。
                * 所以这也不是一个正确的泛型方法声明。
                public void showkey(T genericObj){}
                */
                public static void main(String[] args) {}
            }
            ```
* 泛型的命名规范
    ```java
    E — Element，常用在java Collection里，如：List,Iterator,Set
    K,V — Key，Value，代表Map的键值对
    N — Number，数字
    T — Type，类型，如String，Integer等等
    S,U,V etc. – 2nd, 3rd, 4th 类型，和T的用法一样
    ```
* Refs: 
    * > https://blog.csdn.net/s10461/article/details/53941091
    * > https://www.cnblogs.com/iyangyuan/archive/2013/04/09/3011274.html
    * > https://blog.csdn.net/caihuangshi/article/details/51278793
    * > https://blog.csdn.net/qq_27093465/article/details/73229016
    * > https://blog.csdn.net/briblue/article/details/76736356
    * > https://cloud.tencent.com/developer/article/1007025

### Raw Types
* 缺少实际类型变量的泛型就是一个原始类型
* 实际行为：获取和返回的类型都为Object
* 缺点：无法进行编译时类型检查，将异常的捕获推迟到了运行时，还可能会收到unchecked警告。
    ```java
    public class Box<T> {
        public void set(T t) {}
    }
    Box rawBox = new Box(); // Box就是泛型Box<T>的原始类型。
    ```
* 把一个原始类型赋值给一个参数化类型时会得到一个警告
* 使用一个原始类型调用一个泛型方法，而这个泛型方法定义在对应的泛型中时，也会收到一个警告。这个警告表明原始类型绕过了泛型检查，将对不安全代码的异常捕获推迟到了运行时。因此，你应该避免使用原始类型。
* Refs:
    * > https://blog.csdn.net/FIRE_TRAY/article/details/50583332

### Bounded Types
* 在使用泛型的时候，我们还可以为传入的泛型类型实参进行上下边界的限制。
    * 为泛型添加上边界，即传入的类型实参必须是指定类型的子类型
        ```java
        public class Generic<T extends Number>{
            private T key;
            public Generic(T key) { this.key = key; }
            public T getKey(){ return key; }
        }
        //在泛型方法中添加上下边界限制的时候，必须在权限声明与返回值之间的<T>上添加上下边界，即在泛型声明的时候添加
        //public <T> T showKeyName(Generic<T extends Number> container)，编译器会报错："Unexpected bound"
        public <T extends Number> T showKeyName(Generic<T> container){
            System.out.println("container key :" + container.getKey());
            T test = container.getKey();
            return test;
        }
        ```

### Type Inference
* 调用泛型类的构造器时可以用空尖括号只要上下文可推断，称作diamond。想让type inference推断时必须要有尖括号，不写尖括号会导致unchecked。
    ```java
    Map<String, List<String>> myMap = new HashMap<>();
    ```
* 调用普通泛型方法时
    ```java
    List<String> list = new ArrayList<>();
    list.add("A");// 由于addAll期望获得Collection<? extends String>类型的参数，因此下面的语句无法通过
    list.addAll(new ArrayList<>());
    ```
* 调用有返回值的泛型方法并赋值时，根据target types推断参数类型
    ```java
    static <T> List<T> emptyList();
    List<String> listOne = Collections.emptyList();
    ```
* java 8：根据target types推断参数类型扩展至可以根据target argument来推断
    ```java
    void processStringList(List<String> stringList) { }
    processStringList(Collections.emptyList());
    ```
    * 这里Collections.emptyList()返回的时List，而processStringList方法expect的参数时List，于是编译器推测T是String
* 泛型/非泛型类都可以有泛型的构造器
    * 泛型方法中的泛型构造器
        ```java
        class MyClass<X> {
            <T> MyClass(T t) {}
        }
        MyClass<Integer> myObject = new MyClass<>("");
        ```
        * 我们的编译器就能够自动推导出形式参数X是Integer，T是String了
* Refs:
    * > https://www.cnblogs.com/heimianshusheng/p/5766573.html
    * > https://blog.csdn.net/u010925967/article/details/79459236

### Erasure
* 泛型信息只存在于代码编译阶段，在进入JVM之前，与泛型相关的信息会被擦除掉，专业术语叫做类型擦除。
    * `List<String>`和`List<Integer>`在jvm中的Class都是List.class。
* 这个过程中，泛型类型会被擦除，替换为其限定类型(bounding type)
    * 在泛型类被类型擦除的时候，之前泛型类中的类型参数部分如果没有指定上限，如`<T>`则会被转译成普通的 Object 类型，如果指定了上限如`<T extends String>`则类型参数就被替换成类型上限。
* 使用桥接方法(bridge method)来保证正确处理多态
* 类型擦除，是泛型能够与之前的java版本代码兼容共存的原因。但也因为类型擦除，它会抹掉很多继承相关的特性，这是它带来的局限性。
* Refs:
    * > https://blog.csdn.net/briblue/article/details/76736356

### Bridge Methods
* When compiling a class or interface that extends a parameterized class or implements a parameterized interface, the compiler may need to create a synthetic method, called a bridge method, as part of the type erasure process. You normally don't need to worry about bridge methods, but you might be puzzled if one appears in a stack trace.
* 就是说一个子类在继承(或实现)一个父类(或接口)的泛型方法时，在子类中明确指定了泛型类型，那么在编译时编译器会自动生成桥接方法，这个方法用于泛型的类型安全处理。
* 编译器生成bridge method的意义
    * 简单来说，编译器生成bridge method的目的就是为了和jdk1.5之前的字节码兼容。在jdk1.5之前例如集合的操作都是没有泛型支持的，所以生成的字节码中参数都是用Object接收的，所以也可以往集合中放入任意类型的对象，集合类型的校验也被拖到运行期。
    * 但是在jdk1.5之后引入了泛型，因此集合的内容校验被提前到了编译期，但是为了兼容jdk1.5之前的版本java使用了泛型擦除，所以如果不生成桥接方法就和jdk1.5之前的字节码不兼容了。
    * 上面可以看到在Parent.class中，由于泛型擦除，class文件中泛型都是由Object替代了。所以如果子类中要是不生成bridge method那么子类就没有实现接口中的方法，这个java语义就不对了(虽然已经生成class文件了，不会有编译错误)
* 例子：
    ```java
    public interface SuperClass<T> {
        T method(T param);
    }
    public class SubClass implements SuperClass<String> {
        public String method(String param) { return param; }
    }
    ```
    * SubClass的字节码：
    ```java
    javap -c SubClass.class
    Compiled from "SubClass.java"
    public class com.mikan.SubClass implements com.mikan.SuperClass<java.lang.String> {
    public com.mikan.SubClass();
        flags: ACC_PUBLIC
        Code:
        stack=1, locals=1, args_size=1
            0: aload_0
            1: invokespecial #1                  // Method java/lang/Object."<init>":()V
            4: return
        LineNumberTable:
            line 7: 0
        LocalVariableTable:
            Start  Length  Slot  Name   Signature
                0       5     0  this   Lcom/mikan/SubClass;

    public java.lang.String method(java.lang.String);
        flags: ACC_PUBLIC
        Code:
        stack=1, locals=2, args_size=2
            0: aload_1
            1: areturn
        LineNumberTable:
            line 11: 0
        LocalVariableTable:
            Start  Length  Slot  Name   Signature
                0       2     0  this   Lcom/mikan/SubClass;
                0       2     1 param   Ljava/lang/String;

    public java.lang.Object method(java.lang.Object);
        flags: ACC_PUBLIC, ACC_BRIDGE, ACC_SYNTHETIC
        Code:
        stack=2, locals=2, args_size=2
            0: aload_0
            1: aload_1
            2: checkcast     #2                  // class java/lang/String
            5: invokevirtual #3                  // Method method:(Ljava/lang/String;)Ljava/lang/String;
            8: areturn
        LineNumberTable:
            line 7: 0
        LocalVariableTable:
            Start  Length  Slot  Name   Signature
                0       9     0  this   Lcom/mikan/SubClass;
                0       9     1    x0   Ljava/lang/Object;
    }
    ```
    * SubClass只声明了一个方法，而从字节码可以看到有三个方法
        * 第一个是无参的构造方法(代码中虽然没有明确声明，但是编译器会自动生成)
        * 第二个是我们实现的接口中的方法
        * 第三个就是编译器自动生成的桥接方法。
    * 可以看到flags包括了ACC_BRIDGE和ACC_SYNTHETIC，表示是编译器自动生成的方法，参数类型和返回值类型都是Object。
    * 再看这个方法的字节码，它把Object类型的参数强制转换成了String类型，再调用在SubClass类中声明的方法，转换过来其实就是：
        ```java
        public Object method(Object param) { return this.method(((String) param)); }
        ```
        * 也就是说，桥接方法实际是是调用了实际的泛型方法
* Refs:
    * > https://www.jianshu.com/p/250030ea9b28
    * > https://blog.csdn.net/mhmyqn/article/details/47342577
    * > https://cloud.tencent.com/developer/article/1007025
    * > https://blog.csdn.net/briblue/article/details/76736356

### Wildcard
* SubClass继承了SuperClass，但是不代表`List<SubClass>`和`List<SuperClass>`有继承关系。
* 通配符的出现是为了指定泛型中的类型范围，有3种形式。
    * `<?>`被称作无限定的通配符。
        * 无限定通配符经常与容器类配合使用，它其中的`?`其实代表的是未知类型，容器类无需其真是类型。
        * `<?>`提供了只读的功能，也就是它删减了增加具体类型元素的能力，只保留与具体类型无关的功能。
    * `<? extends T>`被称作有上限的通配符。
    * `<? super T>`被称作有下限的通配符。
        * 不能用于返回类型
* Refs:
    * > https://blog.csdn.net/briblue/article/details/76736356

### The Get & the Put Principle

## Memory Management
### Memory Areas
* (JVM) Stack
    * Java虚拟机栈也是线程私有的，它的生命周期与线程相同。
    * 虚拟机栈描述的是Java方法执行的内存模型：每个方法被执行的时候都会同时创建一个栈帧(Stack Frame)用于存储局部变量表、操作栈、动态链接、方法出口等信息。每一个方法被调用直至执行完成的过程，就对应着一个栈帧在虚拟机栈中从入栈到出栈的过程。
    * 局部变量表存放了编译期可知的各种基本数据类型(boolean、byte、char、short、int、float、long、double)、对象引用(reference类型)，它不等同于对象本身，根据不同的虚拟机实现，它可能是一个指向对象起始地址的引用指针，也可能指向一个代表对象的句柄或者其他与此对象相关的位置)和returnAddress类型(指向了一条字节码指令的地址)。
    * 其中64位长度的long和double类型的数据会占用2个局部变量空间(Slot)，其余的数据类型只占用1个。
    * 局部变量表所需的内存空间在编译期间完成分配，当进入一个方法时，这个方法需要在帧中分配多大的局部变量空间是完全确定的，在方法运行期间不会改变局部变量表的大小。 
    * 在Java虚拟机规范中，对这个区域规定了两种异常状况：
        * 如果线程请求的栈深度大于虚拟机所允许的深度，将抛出StackOverflowError异常；
        * 如果虚拟机栈可以动态扩展(当前大部分的Java虚拟机都可动态扩展，只不过Java虚拟机规范中也允许固定长度的虚拟机栈)，当扩展时无法申请到足够的内存时会抛出OutOfMemoryError异常。
* Heap
    * 对于大多数应用来说，Java堆是Java虚拟机所管理的内存中最大的一块。
    * Java堆是被所有线程共享的一块内存区域，在虚拟机启动时创建。
    * 此内存区域的唯一目的就是存放对象实例，几乎所有的对象实例都在这里分配内存。
    * 从内存分配的角度看，线程共享的Java堆中可能划分出多个线程私有的分配缓冲区。
    * Java堆可以处于物理上不连续的内存空间中，只要逻辑上是连续的即可，就像我们的磁盘空间一样。
    * 在实现时，既可以实现成固定大小的，也可以是可扩展的，不过当前主流的虚拟机都是按照可扩展来实现的(通过-Xmx和-Xms控制)。
    * 如果在堆中没有内存完成实例分配，并且堆也无法再扩展时，将会抛出OutOfMemoryError异常。
* Program Counter (PC) Register
    * 程序计数器是一块较小的内存空间，它的作用可以看做是当前线程所执行的字节码的行号指示器。
    * 在虚拟机的概念模型里(仅是概念模型，各种虚拟机可能会通过一些更高效的方式去实现)，字节码解释器工作时就是通过改变这个计数器的值来选取下一条需要执行的字节码指令，分支、循环、跳转、异常处理、线程恢复等基础功能都需要依赖这个计数器来完成。 
    * 由于Java虚拟机的多线程是通过线程轮流切换并分配处理器执行时间的方式来实现的，在任何一个确定的时刻，一个处理器(对于多核处理器来说是一个内核)只会执行一条线程中的指令。因此，为了线程切换后能恢复到正确的执行位置，每条线程都需要有一个独立的程序计数器，各条线程之间的计数器互不影响，独立存储，我们称这类内存区域为“线程私有”的内存。 
    * 如果线程正在执行的是一个Java方法，这个计数器记录的是正在执行的虚拟机字节码指令的地址；如果正在执行的是Natvie方法，这个计数器值则为空(Undefined)。
    * 此内存区域是唯一一个在Java虚拟机规范中没有规定任何OutOfMemoryError情况的区域。
* Method area and Runtime constant pool
    * Method area
        * 方法区与Java堆一样，是各个线程共享的内存区域，它用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。
        * 虽然Java虚拟机规范把方法区描述为堆的一个逻辑部分，但是它却有一个别名叫做Non-Heap，目的应该是与Java堆区分开来。
        * Java虚拟机规范对这个区域的限制非常宽松，除了和Java堆一样不需要连续的内存和可以选择固定大小或者可扩展外，还可以选择不实现垃圾收集。相对而言，垃圾收集行为在这个区域是比较少出现的，但并非数据进入了方法区就如永久代的名字一样“永久”存在了。这个区域的内存回收目标主要是针对常量池的回收和对类型的卸载，一般来说这个区域的回收“成绩”比较难以令人满意，尤其是类型的卸载，条件相当苛刻，但是这部分区域的回收确实是有必要的。
        * 对于HotSpot虚拟机，方法区对应为永久代(Permanent Generation)，但本质上，两者并不等价，仅仅是因为HotSpot虚拟机的设计团队是用永久代来实现方法区而已，对于其他的虚拟机(JRockit、J9)来说，是不存在永久代这一概念的。
        * 当方法区无法满足内存分配需求时，将抛出OutOfMemoryError异常。
    * Runtime constant pool
        * 运行时常量池是方法区的一部分。
        * Class文件中除了有类的版本、字段、方法、接口等描述等信息外，还有一项信息是常量池，用于存放编译期生成的各种字面量和符号引用，这部分内容将在类加载后存放到方法区的运行时常量池中。 Java虚拟机对Class文件的每一部分的格式都有严格的规定，每一个字节用于存储哪种数据都必须符合规范上的要求，这样才会被虚拟机认可、装载和执行。但对于运行时常量池，Java虚拟机规范没有做任何细节的要求，不同的提供商实现的虚拟机可以按照自己的需要来实现这个内存区域。不过，一般来说，除了保存Class文件中描述的符号引用外，还会把翻译出来的直接引用也存储在运行时常量池中。 
        * 运行时常量池相对于Class文件常量池的另外一个重要特征是具备动态性，Java语言并不要求常量一定只能在编译期产生，也就是并非预置入Class文件中常量池的内容才能进入方法区运行时常量池，运行期间也可能将新的常量放入池中，这种特性被开发人员利用得比较多的便是String类的intern()方法。 
        * 既然运行时常量池是方法区的一部分，自然会受到方法区内存的限制，当常量池无法再申请到内存时会抛出OutOfMemoryError异常。
* Native Stack
    * 本地方法栈与虚拟机栈所发挥的作用是非常相似的，其区别不过是虚拟机栈为虚拟机执行Java方法(也就是字节码)服务，而本地方法栈则是为虚拟机使用到的Native方法服务。
    * 虚拟机规范中对本地方法栈中的方法使用的语言、使用方式与数据结构并没有强制规定，因此具体的虚拟机可以自由实现它。
    * 与虚拟机栈一样，本地方法栈区域也会抛出StackOverflowError和OutOfMemoryError异常。
* 对象访问
    * 最普通的程序行为，但即使是最简单的访问，也会却涉及Java栈、Java堆、方法区这三个最重要内存区域之间的关联关系，如下面的这句代码：
        ```java
        Object obj = new Object();
        ```
        * `Object obj`这部分的语义将会反映到Java栈的本地变量表中，作为一个reference类型数据出现。
        * `new Object()`这部分的语义将会反映到Java堆中，形成一块存储了Object类型所有实例数据值(Instance Data，对象中各个实例字段的数据)的结构化内存，根据具体类型以及虚拟机实现的对象内存布局的不同，这块内存的长度是不固定的。
        * 另外，在Java堆中还必须包含能查找到此对象类型数据(如对象类型、父类、实现的接口、方法等)的地址信息，这些类型数据则存储在方法区中。
    * 由于reference类型在Java虚拟机规范里面只规定了一个指向对象的引用，并没有定义这个引用应该通过哪种方式去定位，以及访问到Java堆中的对象的具体位置，因此不同虚拟机实现的对象访问方式会有所不同，主流的访问方式有两种：使用句柄和直接指针。
        * 如果使用句柄访问方式，Java堆中将会划分出一块内存来作为句柄池，reference中存储的就是对象的句柄地址，而句柄中包含了对象实例数据和类型数据各自的具体地址信息。
            * 使用句柄访问方式的最大好处就是reference中存储的是稳定的句柄地址，在对象被移动(垃圾收集时移动对象是非常普遍的行为)时只会改变句柄中的实例数据指针，而reference本身不需要被修改。
        * 如果使用的是直接指针访问方式，Java 堆对象的布局中就必须考虑如何放置访问类型数据的相关信息，reference中直接存储的就是对象地址。
            * 使用直接指针访问方式的最大好处就是速度更快，它节省了一次指针定位的时间开销，由于对象的访问在Java中非常频繁，因此这类开销积少成多后也是一项非常可观的执行成本。
* Refs:
    * > https://www.cnblogs.com/gw811/archive/2012/10/18/2730117.html
    * > https://www.jianshu.com/p/f8d71e1e8821

### Reference Strengths
### Garbage Collection
### Memory Tuning

## Collections
### Collection Types
### Iterating in Java

## Exceptions
### Checked vs Unchecked
### Catch Block

## Reflection
### java.lang.Class
### Class Modifiers
### Instantiation using Reflection
### Classloaders

## Serialization

## Miscellaneous Topics
### Types
### Keywords
### Annotations
### Boxing
### Unboxing
### Package
### Strings
### Casting

## Java in Practice
### Quick Note
### Object Creation
### Using Objects
### Designing Classes
### Inheritance vs Composition
### Interfaces vs Abstract Classes
### Using Generics
### Enums & Annotations
### Method Design
### General Best Practices
### Exceptions Handling
### Concurrency