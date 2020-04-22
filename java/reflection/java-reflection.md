- [反射的概述](#反射的概述)
- [类加载](#类加载)
    * [类加载的机制的层次结构](#类加载的机制的层次结构)
        * [启动类加载器](#启动类加载器)
        * [扩展类加载器](#扩展类加载器)
        * [系统类加载器](#系统类加载器)
    * [理解双亲委派模式](#理解双亲委派模式)
        * [双亲委派模式工作原理](#双亲委派模式工作原理)
        * [双亲委派模式优势](#双亲委派模式优势)
        * [ClassLoader类](#ClassLoader类)
        * [类加载器间的关系](#类加载器间的关系)
    * [类与类加载器](#类与类加载器)
        * [了解class文件的显示加载与隐式加载的概念](#了解class文件的显示加载与隐式加载的概念)
    * [编写自己的类加载器](#编写自己的类加载器)
        * [自定义File类加载器](#自定义File类加载器)
        * [自定义网络类加载器](#自定义网络类加载器)
        * [热部署类加载器](#热部署类加载器)
- [Class对象](#Class对象)
    * [Class对象的加载及其获取方式](#Class对象的加载及其获取方式)
        * [Class对象的加载](#Class对象的加载)
        * [Class.forName方法](#Class.forName方法)
        * [Class字面常量](#Class字面常量)
        * [理解泛化的Class对象引用](#理解泛化的Class对象引用)
        * [关于类型转换的问题](#关于类型转换的问题)
        * [instanceof关键字与isInstance方法](#instanceof关键字与isInstance方法)
- [理解反射技术](#理解反射技术)
    * [Constructor类及其用法](#Constructor类及其用法)

---
* Ref:
    * > https://blog.csdn.net/pange1991/article/details/81175350
    * > https://blog.csdn.net/javazejian/article/details/73413292
    * > https://blog.csdn.net/javazejian/article/details/70768369
---

### 反射的概述
* 反射是运行中的程序检查自己和软件运行环境的能力，它可以根据它发现的进行改变。通俗的讲就是反射可以在运行时根据指定的类名获得类的信息。
* Java反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能够调用它的任意一个方法和属性；这种动态获取的信息以及动态调用对象的方法的功能称为java语言的反射机制。
* 要想解剖一个类,必须先要获取到该类的字节码文件对象。而解剖使用的就是Class类中的方法.所以先要获取到每一个字节码文件对应的Class类型的对象。

### 类加载
#### 类加载的机制的层次结构
* 这些".java"文件经过Java编译器编译成拓展名为".class"的文件，".class"文件中保存着Java代码经转换后的虚拟机指令，当需要使用某个类时，虚拟机将会加载它的".class"文件，并创建对应的class对象，将class文件加载到虚拟机的内存，这个过程称为类加载，
    ```
    加载(loading) -> 验证(verification) -> 准备(preparation) -> 解析(resolution) -> 初始化(initialization)
    ```
    * 加载: 通过一个类的完全限定查找此类字节码文件，并利用字节码文件创建一个Class对象。
    * 验证: 目的在于确保Class文件的字节流中包含信息符合当前虚拟机要求，不会危害虚拟机自身安全。
        * 主要包括四种验证
            * 文件格式验证
            * 元数据验证
            * 字节码验证
            * 符号引用验证
    * 准备: 为类变量(即static修饰的字段变量)分配内存并且设置该类变量的初始值即0(如`static int i=5`;这里只将i初始化为0，至于5的值将在初始化时赋值)，这里不包含用`final`修饰的`static`，因为`final`在编译的时候就会分配了，注意这里不会为实例变量分配初始化，类变量会分配在方法区中，而实例变量是会随着对象一起分配到Java堆中。
    * 解析: 主要将常量池中的符号引用替换为直接引用的过程。符号引用就是一组符号来描述目标，可以是任何字面量，而直接引用就是直接指向目标的指针、相对偏移量或一个间接定位到目标的句柄。有类或接口的解析，字段解析，类方法解析，接口方法解析。
    * 初始化: 类加载最后阶段，若该类具有超类，则对其进行初始化，执行静态初始化器和静态初始化成员变量(如前面只初始化了默认值的static变量将会在这个阶段赋值，成员变量也将被初始化)。
* 类加载器的任务是根据一个类的全限定名来读取此类的二进制字节流到JVM中，然后转换为一个与目标类对应的`java.lang.Class`对象实例，在虚拟机提供了3种类加载器
    * 引导(Bootstrap)类加载器
    * 扩展(Extension)类加载器
    * 系统(System)类加载器
* 在必要时，我们还可以自定义类加载器，需要注意的是，Java虚拟机对class文件采用的是按需加载的方式，也就是说当需要使用该类时才会将它的class文件加载到内存生成class对象，而且加载某个类的class文件时，Java虚拟机采用的是双亲委派模式即把请求交由父类处理，它一种任务委派模式
 
##### 启动类加载器
* 启动类加载器主要加载的是JVM自身需要的类，这个类加载使用C++语言实现的，是虚拟机自身的一部分。
* 负责将`<JAVA_HOME>/lib`路径下的核心类库或`-Xbootclasspath`参数指定的路径下的jar包加载到内存中，注意必由于虚拟机是按照文件名识别加载jar包的，如rt.jar，如果文件名不被虚拟机识别，即使把jar包丢到lib目录下也是没有作用的。
* 出于安全考虑，Bootstrap启动类加载器只加载包名为`java`、`javax`、`sun`等开头的类。

##### 扩展类加载器
* 扩展类加载器是指Sun公司实现的`sun.misc.Launcher$ExtClassLoader`类，由Java语言实现的，是Launcher的静态内部类，
* 负责加载`<JAVA_HOME>/lib/ext`目录下或者由系统变量`-Djava.ext.dir`指定位路径中的类库，开发者可以直接使用标准扩展类加载器。

##### 系统类加载器
* 也称应用程序加载器，是指`sun.misc.Launcher$AppClassLoader`。
* 负责加载系统类路径`java -classpath`或`-D java.class.path`指定路径下的类库，也就是我们经常用到的classpath路径，开发者可以直接使用系统类加载器，一般情况下该类加载是程序中默认的类加载器，通过`ClassLoader#getSystemClassLoader()`方法可以获取到该类加载器。

#### 理解双亲委派模式
##### 双亲委派模式工作原理
* 双亲委派模式要求除了顶层的启动类加载器外，其余的类加载器都应当有自己的父类加载器，注意`双亲委派模式中的父子关系并非通常所说的类继承关系，而是采用组合关系来复用父类加载器的相关代码`，类加载器间的关系如下:
    ```
                 向上委托               向上委托
    启动类加载起 <--------- 扩展类加载器 <--------- 系统类加载器 <---------自定义类加载器
    ```
* 其工作原理的是，如果一个类加载器收到了类加载请求，它并不会自己先去加载，而是把这个请求委托给父类的加载器去执行，如果父类加载器还存在其父类加载器，则进一步向上委托，依次递归，请求最终将到达顶层的启动类加载器，如果父类加载器可以完成类加载任务，就成功返回，倘若父类加载器无法完成此加载任务，子加载器才会尝试自己去加载。

##### 双亲委派模式优势
* 带有优先级的层次关系
* java核心api中定义类型不会被随意替换，假设通过网络传递一个名为`java.lang.Integer`的类，通过双亲委托模式传递到启动类加载器，而启动类加载器在核心Java API发现这个名字的类，发现该类已被加载，并不会重新加载网络传递的过来的`java.lang.Integer`，而直接返回已加载过的Integer.class，这样便可以防止核心API库被随意篡改。因为`java.lang`是核心API包，需要访问权限，强制加载将会报出如下异常。

##### ClassLoader类
* 类加载器ClassLoader类是一个抽象类。
* ClassLoader中几个比较重要的方法:
    * `protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException`
        * 该方法加载指定名称的二进制类型
        * `loadClass()`方法是`ClassLoader`类自己实现的，该方法中的逻辑就是双亲委派模式的实现
        * 不再建议用户重写
        * `resolve`参数代表是否生成class对象的同时进行解析相关操作。
        * 当类加载请求到来时，先从缓存中查找该类对象，如果存在直接返回，如果不存在则交给该类加载去的父加载器去加载，倘若没有父加载则交给顶级启动类加载器去加载，最后倘若仍没有找到，则使用`findClass()`方法去加载。
        * 可以直接使用`this.getClass().getClassLoder.loadClass("className")`就可以直接调用ClassLoader的`loadClass`方法获取到class对象。
    * `protected Class<?> findClass(String name) throws ClassNotFoundException`
        * 不再建议用户去覆盖`loadClass()`方法，而是建议把自定义的类加载逻辑写在`findClass()`方法中，`findClass()`方法是在`loadClass()`方法中被调用的，当`loadClass()`方法中父加载器加载失败后，则会调用自己的`findClass()`方法来完成类加载，这样就可以保证自定义的类加载器也符合双亲委托模式。
        * ClassLoader类中并没有实现`findClass()`方法的具体代码逻辑，取而代之的是抛出`ClassNotFoundException`异常。
        * `findClass`方法通常是和`defineClass`方法一起使用
    * `protected final Class<?> defineClass​(String name, byte[] b, int off, int len) throws ClassFormatError`
        * `defineClass()`方法是用来将byte字节流解析成JVM能够识别的Class对象，通过这个方法不仅能够通过class文件实例化class对象，也可以通过其他方式实例化class对象，如通过网络接收一个类的字节码，然后转换为byte字节流创建对应的Class对象。
        * `defineClass()`方法通常与`findClass()`方法一起使用，一般情况下，在自定义类加载器时，会直接覆盖ClassLoader的`findClass()`方法并编写加载规则，取得要加载类的字节码后转换成流，然后调用`defineClass()`方法生成类的Class对象。
        * 如果直接调用`defineClass()`方法生成类的Class对象，这个类的Class对象并没有解析，其解析操作需要等待初始化阶段进行。
    * `protected final void resolveClass​(Class<?> c)`
        * 使用该方法可以使用类的Class对象创建完成也同时被解析。
        * 前面我们说链接阶段主要是对字节码进行验证，为类变量分配内存并设置初始值同时将字节码文件中的符号引用转换为直接引用。
* 在编写自定义类加载器时，如果没有太过于复杂的需求，可以直接继承URLClassLoader类，这样就可以避免自己去编写`findClass()`方法及其获取字节码流的方式，使自定义类加载器编写更加简洁    
    
#### 类与类加载器
* 在JVM中表示两个class对象是否为同一个类对象存在两个必要条件。
     * 类的完整类名必须一致，包括包名。
     * 加载这个类的ClassLoader(指ClassLoader实例对象)必须相同。
* 即使这个两个类对象来源同一个Class文件，被同一个虚拟机所加载，但只要加载它们的ClassLoader实例对象不同，那么这两个类对象也是不相等的，这是因为不同的ClassLoader实例对象都拥有不同的独立的类名称空间，所以加载的class对象也会存在不同的类名空间中，但前提是覆写`loadclass`方法，从前面双亲委派模式对`loadClass()`方法的源码分析中可以知，在方法第一步会通过`Class<?> c = findLoadedClass(name)`;从缓存查找，类名完整名称相同则不会再次被加载，因此我们必须绕过缓存查询才能重新加载class对象。
    
##### 了解class文件的显示加载与隐式加载的概念
* 所谓class文件的显示加载与隐式加载的方式是指JVM加载class文件到内存的方式.
* 显示加载指的是在代码中通过调用ClassLoader加载class对象，如直接使用`Class.forName(name)`或`this.getClass().getClassLoader().loadClass()`加载class对象。
* 隐式加载则是不直接在代码中调用ClassLoader的方法加载class对象，而是通过虚拟机自动加载到内存中，如在加载某个类的class文件时，该类的class文件中引用了另外一个类的对象，此时额外引用的类将通过JVM自动加载到内存中。
    
#### 编写自己的类加载器
* 实现自定义类加载器需要继承ClassLoader或者URLClassLoader，继承ClassLoader则需要自己重写`findClass()`方法并编写加载逻辑，继承URLClassLoader则可以省去编写`findClass()`方法以及class文件加载转换成字节码流的代码。
    * 当class文件不在ClassPath路径下，默认系统类加载器无法找到该class文件，在这种情况下我们需要实现一个自定义的ClassLoader来加载特定路径下的class文件生成class对象。
    * 当一个class文件是通过网络传输并且可能会进行相应的加密操作时，需要先对class文件进行相应的解密后再加载到JVM内存中，这种情况下也需要编写自定义的ClassLoader并实现相应的逻辑。
    * 当需要实现热部署功能时(一个class文件通过不同的类加载器产生不同class对象从而实现热部署功能)，需要实现自定义ClassLoader的逻辑。
    
##### 自定义File类加载器
* `./reflection/src/classloading/common/FileClassLoader.java`
* `getClassData()`方法找到class文件并转换为字节流，并重写`findClass()`方法，利用`defineClass()`方法创建了类的class对象。
* 由于启动类加载器、拓展类加载器以及系统类加载器都无法在其路径下找到该类，因此最终将有自定义类加载器加载，即调用`findClass()`方法进行加载。
* 如果继承URLClassLoader实现，那代码就更简洁了。
* `./reflection/src/classloading/common/FileUrlClassLoader.java`
* 非常简洁除了需要重写构造器外无需编写`findClass()`方法及其class文件的字节流转换逻辑。
    
##### 自定义网络类加载器
* 自定义网络类加载器，主要用于读取通过网络传递的class文件（在这里我们省略class文件的解密过程），并将其转换成字节流生成对应的class对象。
* `./reflection/src/classloading/common/NetClassLoader.java`
* 主要是在获取字节码流时的区别，从网络直接获取到字节流再转车字节数组然后利用`defineClass`方法创建class对象，如果继承URLClassLoader类则和前面文件路径的实现是类似的，无需担心路径是filePath还是Url，因为URLClassLoader内的URLClassPath对象会根据传递过来的URL数组中的路径判断是文件还是jar包，然后根据不同的路径创建FileLoader或者JarLoader或默认类Loader去读取对于的路径或者url下的class文件。

##### 热部署类加载器
* 所谓的热部署就是利用同一个class文件不同的类加载器在内存创建出两个不同的class对象(关于这点的原因前面已分析过，即利用不同的类加载实例)，由于JVM在加载类之前会检测请求的类是否已加载过(即在`loadClass()`方法中调用`findLoadedClass()`方法)，如果被加载过，则直接从缓存获取，不会重新加载。
* 注意同一个类加载器的实例和同一个class文件只能被加载器一次，多次加载将报错，因此我们实现的热部署必须让同一个class文件可以根据不同的类加载器重复加载，以实现所谓的热部署。
* 实际上前面的实现的FileClassLoader和FileUrlClassLoader已具备这个功能，但前提是直接调用`findClass()`方法，而不是调用`loadClass()`方法，因为ClassLoader中`loadClass()`方法体中调用`findLoadedClass()`方法进行了检测是否已被加载，因此我们直接调用`findClass()`方法就可以绕过这个问题，当然也可以重新`loadClass`方法，但强烈不建议这么干。
    
    
### Class对象
* RTTI(Run-Time Type Identification运行时类型识别)作用是在运行时识别一个对象的类型和类的信息，这里分两种: 
    * 传统的RRTI，它假定我们在编译期已知道了所有类型。
    * 反射机制，它允许我们在运行时发现和使用类型的信息。
* 在Java中用来表示运行时类型信息的对应类就是Class类，Class类也是一个实实在在的类，存在于JDK的`java.lang`包中。
* 实际上在Java中每个类都有一个Class对象，每当我们编写并且编译一个新创建的类就会产生一个对应Class对象并且这个Class对象会被保存在同名.class文件里。
* 当我们new一个新对象或者引用静态成员变量时，JVM中的类加载器子系统会将对应Class对象加载到JVM中，然后JVM再根据这个类型信息相关的Class对象创建我们需要实例对象或者提供静态变量的引用值。需要特别注意的是，手动编写的每个class类，无论创建多少个实例对象，在JVM中都只有一个Class对象，即在内存中每个类有且只有一个相对应的Class对象。
* Class类只存私有构造函数，因此对应Class对象只能有JVM创建和加载。

#### Class对象的加载及其获取方式
##### Class对象的加载
* 所有的类都是在对其第一次使用时动态加载到JVM中的，当程序创建第一个对类的静态成员引用时，就会加载这个被使用的类。使用new操作符创建类的新实例对象也会被当作对类的静态成员的引用，由此看来Java程序在它们开始运行之前并非被完全加载到内存的，其各个部分是按需加载，所以在使用该类时，类加载器首先会检查这个类的Class类对象是否已被加载，如果还没有加载，默认的类加载器就会先根据类名查找.class文件，在这个类的字节码文件被加载时，它们必须接受相关验证，以确保其没有被破坏并且不包含不良Java代码，完全没有问题后就会被动态加载到内存中，此时相当于Class类对象也就被载入内存了，同时也就可以被用来创建这个类的所有实例对象。

##### Class.forName方法
* `Class.forName()`方法的调用将会返回一个对应类的Class对象，因此如果我们想获取一个类的运行时类型信息并加以使用时，可以调用`Class.forName()`方法获取Class对象的引用，这样做的好处是无需通过持有该类的实例对象引用而去获取Class对象。
* 第2种方式是通过一个实例对象获取一个类的Class对象，其中的`getClass()`是从顶级类Object继承而来的，它将返回表示该对象的实际类型的Class对象引用。
* `forName`方法时需要捕获一个名称为`ClassNotFoundException`的异常，因为`forName`方法在编译器是无法检测到其传递的字符串对应的类是否存在的，只能在程序运行时进行检查，如果不存在就会抛出`ClassNotFoundException`异常
* 例子: `./classclass/common/ForNameMethod.java`

##### Class字面常量
* 在Java中存在另一种方式来生成Class对象的引用，它就是Class字面常量
    ```
    Class clazz = Gum.class;
    ``` 
* 这种方式相对前面两种方法更加简单，更安全。因为它在编译器就会受到编译器的检查同时由于无需调用`forName`方法效率也会更高，因为通过字面量的方法获取Class对象的引用不会自动初始化该类。
* 由于基本数据类型还有对应的基本包装类型，其包装类型有一个标准字段TYPE，而这个TYPE就是一个引用，指向基本数据类型的Class对象，其等价转换，一般情况下更倾向使用.class的形式，这样可以保持与普通类的形式统一。
    ```
    boolean.class = Boolean.TYPE;
    char.class = Character.TYPE;
    byte.class = Byte.TYPE;
    short.class = Short.TYPE;
    int.class = Integer.TYPE;
    long.class = Long.TYPE;
    float.class = Float.TYPE;
    double.class = Double.TYPE;
    void.class = Void.TYPE;
    ```
* 我们获取字面常量的Class引用时，触发的应该是加载阶段，因为在这个阶段Class对象已创建完成，获取其引用并不困难，而无需触发类的最后阶段初始化。    
* 验证例子: `./src/classclass/common/ClassInitialzation.java`
    * 通过字面常量获取方式获取Initable类的Class对象并没有触发Initable类的初始化，
    * `Initable.staticFinal`变量时也没有触发初始化，这是因为`staticFinal`属于编译期静态常量，在编译阶段通过常量传播优化的方式将Initable类的常量`staticFinal`存储到了一个称为NotInitialization类的常量池中，在以后对Initable类常量`staticFinal`的引用实际都转化为对NotInitialization类对自身常量池的引用，所以在编译期后，对编译期常量的引用都将在NotInitialization类的常量池获取，这也就是引用编译期静态常量不会触发Initable类初始化的重要原因。
    * 但在之后调用了`Initable.staticFinal2`变量后就触发了Initable类的初始化，注意`staticFinal2`虽然被static和final修饰，但其值在编译期并不能确定，因此staticFinal2并不是编译期常量，使用该变量必须先初始化Initable类。Initable2和Initable3类中都是静态成员变量并非编译期常量，引用都会触发初始化。至于forName方法获取Class对象，肯定会触发初始化。
* Class对象小结论:
    * 获取Class对象引用的方式3种:
        * 通过继承自Object类的`getClass`方法。
        * 通过Class类的静态方法`forName`。
        * 通过字面常量的方式".class"。
    * 其中实例类的`getClass`方法和Class类的静态方法`forName`都将会触发类的初始化阶段，而字面常量获取Class对象的方式则不会触发初始化。
    * 初始化是类加载的最后一个阶段，也就是说完成这个阶段后类也就加载到内存中，此时可以对类进行各种必要的操作了，注意在这个阶段，才真正开始执行类中定义的Java程序代码或者字节码。
* 在虚拟机规范严格规定了有且只有5种场景必须对类进行初始化:
    * 使用new关键字实例化对象时、读取或者设置一个类的静态字段以及调用静态方法的时候，必须触发类加载的初始化过程。
    * 使用反射包(`java.lang.reflect`)的方法对类进行反射调用时，如果类还没有被初始化，则需先进行初始化，这点对反射很重要。
    * 当初始化一个类的时候，如果其父类还没进行初始化则需先触发其父类的初始化。
    * 当Java虚拟机启动时，用户需要指定一个要执行的主类(包含main方法的类)，虚拟机会先初始化这个主类。
    * 如果一个java.lang.invoke.MethodHandle 实例最后解析结果为REF_getStatic、REF_putStatic、REF_invokeStatic的方法句柄，并且这个方法句柄对应类没有初始化时，必须触发其初始化。
    
##### 理解泛化的Class对象引用
* 由于Class的引用总数指向某个类的Class对象，利用Class对象可以创建实例类，这也就足以说明Class对象的引用指向的对象确切的类型。在Java SE5引入泛型后，使用我们可以利用泛型来表示Class对象更具体的类型，即使在运行期间会被擦除，但编译期足以确保我们使用正确的对象类型。
    ```
    public class ClazzDemo {
        public static void main(String[] args){
            //没有泛型
            Class intClass = int.class;
            //带泛型的Class对象
            Class<Integer> integerClass = int.class;
            integerClass = Integer.class;
            //没有泛型的约束,可以随意赋值
            intClass= double.class;
            //编译期错误,无法编译通过
            //integerClass = double.class
        }
    }
    ```
    * 声明普通的Class对象，在编译器并不会检查Class对象的确切类型是否符合要求，如果存在错误只有在运行时才得以暴露出来。
    * 但是通过泛型声明指明类型的Class对象，编译器在编译期将对带泛型的类进行额外的类型检查，确保在编译期就能保证类型的正确性，实际上`Integer.class`就是一个`Class<Integer>`类的对象。面对下述语句，确实可能令人困惑，但该语句确实是无法编译通过的。
    ```
    //编译无法通过
    Class<Number> numberClass=Integer.class;
    ```
    * Integer的Class对象并非Number的Class对象的子类，前面提到过，所有的Class对象都只来源于Class类，看来事实确实如此。当然我们可以利用通配符“?”来解决问题。
    ```
    Class<?> intClass = int.class;
    intClass = double.class;
    ```
    * 这样的语句并没有什么问题，毕竟通配符指明所有类型都适用，那么为什么不直接使用Class还要使用Class<?>呢？这样做的好处是告诉编译器，我们是确实是采用任意类型的泛型，而非忘记使用泛型约束，因此Class<?>总是优于直接使用Class，至少前者在编译器检查时不会产生警告信息。当然我们还可以使用extends关键字告诉编译器接收某个类型的子类，如解决前面Number与Integer的问题。
    ```
    Class<? extends Number> clazz = Integer.class;
    clazz = double.class;
    clazz = Number.class;
    ```
    
##### 关于类型转换的问题
* 在许多需要强制类型转换的场景，我们更多的做法是直接强制转换类型
    ```
    public class ClassCast {
        public void cast(){
            Animal animal= new Dog();
            //强制转换
            Dog dog = (Dog) animal;
        }
    }
    
    interface Animal{ }
    
    class Dog implements  Animal{ }
    ```
    * 之所可以强制转换，这得归功于RRTI，要知道在Java中，所有类型转换都是在运行时进行正确性检查的，利用RRTI进行判断类型是否正确从而确保强制转换的完成，如果类型转换失败，将会抛出类型转换异常。
* Java SE5中新增一种使用Class对象进行类型转换的方式
    ```
    Animal animal= new Dog();
    //这两句等同于Dog dog = (Dog) animal;
    Class<Dog> dogType = Dog.class;
    Dog dog = dogType.cast(animal);
    ```
    * 利用Class对象的cast方法，其参数接收一个参数对象并将其转换为Class引用的类型。
    * 这种方式似乎比之前的强制转换更麻烦些，而且当类型不能正确转换时，仍然会抛出`ClassCastException`异常。

##### instanceof关键字与isInstance方法
* 关于`instanceof`关键字，它返回一个boolean类型的值，意在告诉我们对象是不是某个特定的类型实例。
    ```
    public void cast2(Object obj){
        if(obj instanceof Animal){
            Animal animal= (Animal) obj;
        }
    }
    ```
* `isInstance`方法则是Class类中的一个Native方法，也是用于判断对象类型的。
    ```
    public void cast2(Object obj){
        //isInstance方法
        if(Animal.class.isInstance(obj)){
            Animal animal= (Animal) obj;
        }
    }
    ```

### 理解反射技术
* 反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能够调用它的任意一个方法和属性，这种动态获取的信息以及动态调用对象的方法的功能称为java语言的反射机制。
* Class类与`java.lang.reflect`类库一起对反射技术进行了全力的支持。
* 常用的类主要有Constructor类表示的是Class 对象所表示的类的构造方法，利用它可以在运行时动态创建对象、Field表示Class对象所表示的类的成员变量，通过它可以在运行时动态修改成员变量的属性值(包含private)、Method表示Class对象所表示的类的成员方法，通过它可以动态调用对象的方法(包含private)。

#### Constructor类及其用法
* Constructor类存在于反射包中，反映的是Class 对象所表示的类的构造方法。
* 获取Constructor对象是通过Class类中的方法获取的，Class类与Constructor相关的主要方法。
    * `public static Class<?> forName​(String className) throws ClassNotFoundException`
        * 返回与带有给定字符串名的类或接口相关联的Class对象。
            ```
            Class<?> clazz = Class.forName("reflection.common.User");
            ```
    * `public Constructor<T> getConstructor​(Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException`
        * 返回指定参数类型、具有public访问权限的构造函数对象。
            ```
            Class<?> clazz = Class.forName("reflection.common.User");
            Constructor cs1 =clazz.getConstructor(String.class);
            User user1= (User) cs1.newInstance("xiaolong");
            user1.setAge(22);
            ```
    * `public Constructor<?>[] getConstructors​() throws SecurityException`
        * 返回所有具有public访问权限的构造函数的Constructor对象数组。
    * `public Constructor<T> getDeclaredConstructor​(Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException`
        * 返回指定参数类型、所有声明的(包括private)构造函数对象。
            ```
            Class<?> clazz = Class.forName("reflection.common.User");
            Constructor cs2=clazz.getDeclaredConstructor(int.class,String.class);
            cs2.setAccessible(true);
            User user2= (User) cs2.newInstance(25,"lidakang");
            ```
    * `public Constructor<?>[] getDeclaredConstructors​() throws SecurityException`
        * 返回所有声明的(包括private)构造函数对象
            ```
            Class<?> clazz = Class.forName("reflection.common.User");
            Constructor<?> cons[] = clazz.getDeclaredConstructors();
            for (int i = 0; i < cons.length; i++) {
                Class<?> clazzs[] = cons[i].getParameterTypes();
                System.out.println("构造函数["+i+"]:"+cons[i].toString() );
                System.out.print("参数类型["+i+"]:(");
                for (int j = 0; j < clazzs.length; j++) {
                    if (j == clazzs.length - 1)
                      System.out.print(clazzs[j].getName());
                    else
                      System.out.print(clazzs[j].getName() + ",");
                }
                System.out.println(")");
            }
            ```
    * `public T newInstance​() throws InstantiationException, IllegalAccessException`
        * 创建此 Class 对象所表示的类的一个新实例。
            ```
            Class<?> clazz = Class.forName("reflection.common.User");
            User user = (User) clazz.newInstance();
            user.setAge(20);
            user.setName("Rollen");
            ```
            * 实例化默认构造方法，User必须无参构造函数,否则将抛异常。
* Constructor类本身一些常用方法
    * `public Class<T> getDeclaringClass​()`
        * 返回Class对象，该对象表示声明由此Constructor对象表示的构造方法的类,其实就是返回真实类型(不包含参数)。
            ```
            Constructor cs3 = clazz.getDeclaredConstructor(int.class,String.class);
            Class uclazz = cs3.getDeclaringClass();
            //Constructor对象表示的构造方法的类
            System.out.println("构造方法的类:" + uclazz.getName());
            ```
    * `public Type[] getGenericParameterTypes​()`
        * 按照声明顺序返回一组Type对象，返回的就是Constructor对象构造函数的形参类型。
            ```
            Constructor cs3 = clazz.getDeclaredConstructor(int.class,String.class);
            Type[] tps=cs3.getGenericParameterTypes();
            for (Type tp:tps) {
                System.out.println("参数名称tp:"+tp);
            }
            ```
    * `public String getName​()`
        * 以字符串形式返回此构造方法的名称。
            ```
            Constructor cs3 = clazz.getDeclaredConstructor(int.class,String.class);
            System.out.println("getName:"+cs3.getName());
            ```
    * `public Class<?>[] getParameterTypes​()`
        * 按照声明顺序返回一组Class对象，即返回Constructor对象所表示构造方法的形参类型。
            ```
            Constructor cs3 = clazz.getDeclaredConstructor(int.class,String.class);
            Class<?> clazzs[] = cs3.getParameterTypes();
            for (Class claz:clazzs) {
                System.out.println("参数名称:"+claz.getName());
            }
            ```
    * `public T newInstance​(Object... initargs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException`
        * 使用此Constructor对象表示的构造函数来创建新实例。
            ```
            Constructor cs3 = clazz.getDeclaredConstructor(int.class,String.class);
            ```
    * `public String toGenericString​()`
        * 返回描述此Constructor的字符串，其中包括类型参数。
            ```
            Constructor cs3 = clazz.getDeclaredConstructor(int.class,String.class);
            System.out.println("getoGenericString():"+cs3.toGenericString());
            ```






