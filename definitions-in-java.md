
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
    * [Anonymous and Local Classes](#Anonymous-and-Local-Classes)
    * [Abstract Classes](#Abstract-Classes)
    * [Final Class](#Abstract-Classes)
    * [Super Keyword](#Super-Keyword)
    * [Finalize](#Finalize)
- [Interfaces](Interfaces)
    * [More on Interfaces](#More-on-Interfaces)
- [Inheritance](#Inheritance)
    * [Multiple Inheritance](#Multiple-Inheritance)
    * [Inheritance Gotchas](#Inheritance-Gotchas)
- [Lambda Expressions](#Lambda-Expressions)
- [Generics](#Generics)
    * [Why Generics](#Why-Generics)
    * [Raw Types](#Raw-Types)
    * [Generic Types](#Generic-Types)
    * [Bounded Types](#Bounded-Types)
    * [Type Inference](#Type-Inference)
    * [Erasure](#Erasure)
    * [Bridge Methods](#Bridge-Methods)
    * [Wildcard](#Wildcard)
    * [The Get & the Put Principle](#The-Get-&-the-Put-Principle)
- [Multi-Threading](#Multi-Threading)
    * [Thread Safety](#Thread-Safety)
    * [Mutexes vs Semaphores](#Mutexes-vs-Semaphores)
    * [Synchronized](#Synchronized)
    * [Volatile](#Volatile)
    * [Wait() and Notify()](#Wait-and-Notify)
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
            * 方法区(Method Area)：用于存储类结构信息的地方，包括常量池、静态变量、构造函数等。虽然JVM规范把方法区描述为堆的一个逻辑部分， 但它却有个别名non-heap（非堆），所以大家不要搞混淆了。方法区还包含一个运行时常量池。
            * java堆(Heap)：存储java实例或者对象的地方。这块是GC的主要区域（后面解释）。从存储的内容我们可以很容易知道，方法区和堆是被所有java线程共享的。
            * java栈(Stack)：java栈总是和线程关联在一起，每当创建一个线程时，JVM就会为这个线程创建一个对应的java栈。在这个java栈中又会包含多个栈帧，每运行一个方法就创建一个栈帧，用于存储局部变量表、操作栈、方法返回值等。每一个方法从调用直至执行完成的过程，就对应一个栈帧在java栈中入栈到出栈的过程。所以java栈是现成私有的。
            * 程序计数器(PC Register)：用于保存当前线程执行的内存地址。由于JVM程序是多线程执行的（线程轮流切换），所以为了保证线程切换回来后，还能恢复到原先状态，就需要一个独立的计数器，记录之前中断的地方，可见程序计数器也是线程私有的。
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
    * 返回一个字符串，该字符串由类名（对象是该类的一个实例）、at 标记符“@”和此对象哈希码的无符号十六进制表示组成。
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
* > https://www.cnblogs.com/jxzheng/p/5191037.html
* > https://www.cnblogs.com/kevinwu/archive/2012/05/22/2498638.html
* > https://www.ibm.com/developerworks/cn/java/j-lo-clobj-init/
* > https://www.javaworld.com/article/3040564/java-101-class-and-object-initialization-in-java.html
* > https://blog.csdn.net/w1196726224/article/details/56529615

### Nested Classes
### Anonymous and Local Classes
### Abstract Classes
### Final Class
* final变量能被显式地初始化并且只能初始化一次。
* 被声明为final的对象的引用不能指向不同的对象。但是final对象里的数据可以被改变。也就是说final对象的引用不能改变，但是里面的值可以改变。
* final修饰符通常和static修饰符一起使用来创建类常量。
* 类中的final方法可以被子类继承，但是不能被子类修改。
* 声明final方法的主要目的是防止该方法的内容被修改。
* final 类不能被继承，没有类能够继承 final 类的任何特性。

### Super Keyword
### Finalize

## Interfaces
### More on Interfaces

## Inheritance
### Multiple Inheritance
### Inheritance Gotchas

## Lambda Expressions

## Generics
### Why Generics
### Raw Types
### Generic Types
### Bounded Types
### Type Inference
### Erasure
### Bridge Methods
### Wildcard
### The Get & the Put Principle

## Multi-Threading
### Thread Safety
### Mutexes vs Semaphores
### Synchronized
### Volatile
### Wait() and Notify()

## Memory Management
### Memory Areas
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