- [Spring概述](#Spring概述)
    * [Spring框架的设计目标，设计理念，和核心是什么](#Spring框架的设计目标-设计理念-和核心是什么)
    * [Spring的优缺点](#Spring的优缺点)
    * [Spring有哪些应用场景](#Spring有哪些应用场景)
    * [Spring模块](#Spring模块)
    * [Spring框架中都用到的设计模式](#Spring框架中都用到的设计模式)
    * [核心容器模块](#核心容器模块)
    * [Spring框架中的事件](#Spring框架中的事件)
    * [Spring应用程序组件](#Spring应用程序组件)
- [Spring控制反转](#Spring控制反转)
    * [控制反转的作用](#控制反转的作用)
    * [控制反转的优点](#IOC的优点)
    * [Spring IoC 的实现机制](#Spring-IoC-的实现机制)
    * [BeanFactory和ApplicationContext的区别](#BeanFactory和ApplicationContext的区别)
    * [BeanFactory和ApplicationContext的关系详解](#BeanFactory和ApplicationContext的关系详解)
    * [ApplicationContext常见的实现](#ApplicationContext常见的实现)
    * [Spring的依赖注入](#Spring的依赖注入)
    * [依赖注入的基本原则](#依赖注入的基本原则)
    * [依赖注入有什么优势](#依赖注入有什么优势)
    * [依赖注入实现方式](#依赖注入实现方式)
- [Spring Beans](#Spring-Beans)
    * [Spring bean的作用域](#Spring-bean的作用域)
    * [Spring框架中的单例bean的线程安全](#Spring框架中的单例bean的线程安全)
    * [Spring如何处理线程并发问题](#Spring如何处理线程并发问题)
    * [bean的生命周期](#bean的生命周期)
    * [内部bean](#内部bean)
- [Spring数据访问](#Spring数据访问)

---
* refs: 
    * > https://thinkwon.blog.csdn.net/article/details/104397516
---

### Spring概述
* Spring是一个轻量级Java开发框架，目的是为了解决企业级应用开发的业务逻辑层和其他各层的耦合问题。它是一个分层的JavaSE/JavaEE full-stack(一站式)轻量级开源框架，为开发Java应用程序提供全面的基础架构支持。Spring负责基础架构，因此Java开发者可以专注于应用程序的开发。
* Spring最根本的使命是解决企业级应用开发的复杂性，即简化Java开发。
* Spring可以做很多事情，它为企业级开发提供给了丰富的功能，但是这些功能的底层都依赖于它的两个核心特性，也就是依赖注入(dependency injection，DI)和面向切面编程(aspect-oriented programming，AOP)。
* 为了降低Java开发的复杂性，Spring采取了以下4种关键策略
    * 基于POJO的轻量级和最小侵入性编程；
    * 过依赖注入和面向接口实现松耦合；
    * 基于切面和惯例进行声明式编程；
    * 通过切面和模板减少样板式代码。

#### Spring框架的设计目标，设计理念，和核心是什么
* Spring设计目标:Spring为开发者提供一个一站式轻量级应用开发平台；
* Spring设计理念:在JavaEE开发中，支持POJO和JavaBean开发方式，使应用面向接口开发，充分支持OO(面向对象)设计方法；Spring通过IoC容器实现对象耦合关系的管理，并实现依赖反转，将对象之间的依赖关系交给IoC容器，实现解耦；
* Spring框架的核心:IoC容器和AOP模块。通过IoC容器管理POJO对象以及他们之间的耦合关系；通过AOP以动态非侵入的方式增强服务。
* IoC让相互协作的组件保持松散的耦合，而AOP编程允许你把遍布于应用各层的功能分离出来形成可重用的功能组件。

#### Spring的优缺点
* 优点
    * 方便解耦，简化开发
        * Spring就是一个大工厂，可以将所有对象的创建和依赖关系的维护，交给Spring管理。
    * AOP编程的支持
        * Spring提供面向切面编程，可以方便的实现对程序进行权限拦截、运行监控等功能。
    * 声明式事务的支持
        * 只需要通过配置就可以完成对事务的管理，而无需手动编程。
    * 方便程序的测试
        * Spring对Junit支持，可以通过注解方便的测试Spring程序。
    * 方便集成各种优秀框架
        * Spring不排斥各种优秀的开源框架，其内部提供了对各种优秀框架的直接支持(如:Struts、Hibernate、MyBatis等)。
    * 降低JavaEE API的使用难度
        * Spring对JavaEE开发中非常难用的一些API(JDBC、JavaMail、远程调用等)，都提供了封装，使这些API应用难度大大降低。
* 缺点:
    * Spring明明一个很轻量级的框架，却给人感觉大而全
    * Spring依赖反射，反射影响性能
    * 使用门槛升高，入门Spring需要较长时间

#### Spring有哪些应用场景
* 应用场景: JavaEE企业应用开发，包括SSH、SSM等
* Spring价值:
    * Spring是非侵入式的框架，目标是使应用程序代码对框架依赖最小化；
    * Spring提供一个一致的编程模型，使应用直接使用POJO开发，与运行环境隔离开来；
    * Spring推动应用设计风格向面向对象和面向接口开发转变，提高了代码的重用性和可测试性；

#### Spring模块
* Spring总共大约有20个模块，由1300多个不同的文件构成。而这些组件被分别整合在`核心容器(Core Container)`、`AOP(Aspect Oriented Programming)`和`设备支持(Instrmentation)`、`数据访问与集成(Data Access/Integeration)`、`Web`、`消息(Messaging)`、`Test`等 6 个模块中。
    * `spring core`: 提供了框架的基本组成部分，包括控制反转和依赖注入功能。
    * `spring beans`: 提供了`BeanFactory`，是工厂模式的一个经典实现，Spring将管理对象称为`Bean`。
    * `spring context`: 构建于`core`封装包基础上的`context`封装包，提供了一种框架式的对象访问方法。
    * `spring jdbc`: 提供了一个`JDBC`的抽象层，消除了烦琐的`JDBC`编码和数据库厂商特有的错误代码解析，用于简化`JDBC`。
    * `spring aop`: 提供了面向切面的编程实现，让你可以自定义拦截器、切点等。
    * `spring Web`: 提供了针对`Web`开发的集成特性，例如文件上传，利用`servlet listeners`进行 ioc 容器初始化和针对`Web`的`ApplicationContext`。
    * `spring test`: 主要为测试提供支持的，支持使用`JUnit`或`TestNG`对Spring组件进行单元测试和集成测试。

#### Spring框架中都用到的设计模式
* 工厂模式: `BeanFactory`就是简单工厂模式的体现，用来创建对象的实例；
* 单例模式: `Bean`默认为单例模式。
* 代理模式: Spring的`AOP`功能用到了JDK的动态代理和CGLIB字节码生成技术；
* 模板方法: 用来解决代码重复的问题。比如: `RestTemplate`, `JmsTemplate`, `JpaTemplate`。
* 观察者模式: 定义对象键一种一对多的依赖关系，当一个对象的状态发生改变时，所有依赖于它的对象都会得到通知被制动更新，如Spring中`listener`的实现`ApplicationListener`。

#### 核心容器模块
* spring context应用上下文
* 这是基本的Spring模块，提供spring框架的基础功能，`BeanFactory`是任何以spring为基础的应用的核心。Spring框架建立在此模块之上，它使Spring成为一个容器。
* Bean工厂是工厂模式的一个实现，提供了控制反转功能，用来把应用的配置和依赖从真正的应用代码中分离。最常用的就是`org.springframework.beans.factory.xml.XmlBeanFactory`，它根据XML文件中的定义加载beans。该容器从XML 文件读取配置元数据并用它去创建一个完全配置的系统或应用。

#### Spring框架中的事件
* Spring 提供了以下5种标准的事件: 
    * 上下文更新事件(`ContextRefreshedEvent`): 在调用`ConfigurableApplicationContext`接口中的`refresh()`方法时被触发。
    * 上下文开始事件(`ContextStartedEvent`): 当容器调用`ConfigurableApplicationContext`的`Start()`方法开始/重新开始容器时触发该事件。
    * 上下文停止事件(`ContextStoppedEvent`): 当容器调用`ConfigurableApplicationContext`的`Stop()`方法停止容器时触发该事件。
    * 上下文关闭事件(`ContextClosedEvent`): 当`ApplicationContext`被关闭时触发该事件。容器被关闭时，其管理的所有单例Bean都被销毁。
    * 请求处理事件(`RequestHandledEvent`): 在Web应用中，当一个http请求(request)结束触发该事件。如果一个bean实现了`ApplicationListener`接口，当一个`ApplicationEvent`被发布以后，bean会自动被通知。

#### Spring应用程序组件
* Spring 应用一般有以下组件:
    * 接口 - 定义功能。
    * Bean类 - 它包含属性，`setter`和`getter`方法，函数等。
    * Bean配置文件 - 包含类的信息以及如何配置它们。
    * Spring面向切面编程 - 提供面向切面编程的功能。
    * 用户程序 - 它使用接口。


### Spring控制反转
* 控制反转即IoC(Inversion of Control)，它把传统上由程序代码直接操控的对象的调用权交给容器，通过容器来实现对象组件的装配和管理。所谓的“控制反转”概念就是对组件对象控制权的转移，从程序代码本身转移到了外部容器。
* Spring IOC负责创建对象，管理对象(通过依赖注入)，装配对象，配置对象，并且管理这些对象的整个生命周期。

#### 控制反转的作用
* 管理对象的创建和依赖关系的维护。对象的创建并不是一件简单的事，在对象关系比较复杂时，如果依赖关系需要程序猿来维护的话，那是相当头疼的
* 解耦，由容器去维护具体的对象
* 托管了类的产生过程，比如我们需要在类的产生过程中做一些处理，最直接的例子就是代理，如果有容器程序可以把这部分处理交给容器，应用程序则无需去关心类是如何完成代理的

#### 控制反转的优点
* 控制反转或依赖注入把应用的代码量降到最低。
* 它使应用容易测试，单元测试不再需要单例和JNDI查找机制。
* 最小的代价和最小的侵入性使松散耦合得以实现。
* 控制反转容器支持加载服务时的starving初始化和lazy加载。

#### Spring IoC的实现机制
* Spring中的IoC的实现原理就是工厂模式加反射机制。
* 示例
    ```java
    interface Fruit {
    public abstract void eat();
    }

    class Apple implements Fruit {
        public void eat(){
            System.out.println("Apple");
        }
    }

    class Orange implements Fruit {
        public void eat(){
            System.out.println("Orange");
        }
    }

    class Factory {
        public static Fruit getInstance(String ClassName) {
            Fruit f=null;
            try {
                f=(Fruit)Class.forName(ClassName).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return f;
        }
    }

    class Client {
        public static void main(String[] a) {
            Fruit f=Factory.getInstance("io.github.dunwu.spring.Apple");
            if(f!=null){
                f.eat();
            }
        }
    }
    ```

#### Spring的IoC支持的功能
* Spring的IoC设计支持以下功能:
    * 依赖注入
    * 依赖检查
    * 自动装配
    * 支持集合
    * 指定初始化方法和销毁方法
    * 支持回调某些方法

#### BeanFactory和ApplicationContext的区别
* `BeanFactory`和`ApplicationContext`是Spring的两大核心接口，都可以当做Spring的容器。其中`ApplicationContext`是`BeanFactory`的子接口。
* 依赖关系
    * `BeanFactory`: 是Spring里面最底层的接口，包含了各种Bean的定义，读取bean配置文档，管理bean的加载、实例化，控制bean的生命周期，维护bean之间的依赖关系。
    * `ApplicationContext`接口作为`BeanFactory`的派生，除了提供`BeanFactory`所具有的功能外，还提供了更完整的框架功能:
        * 继承MessageSource，因此支持国际化。
        * 统一的资源文件访问方式。
        * 提供在监听器中注册bean的事件。
        * 同时加载多个配置文件。
        * 载入多个(有继承关系)上下文 ，使得每一个上下文都专注于一个特定的层次，比如应用的web层。
* 加载方式
    * `BeanFactroy`采用的是`延迟加载`形式来注入Bean的，即只有在使用到某个Bean时(调用getBean())，才对该Bean进行加载实例化。这样，我们就不能发现一些存在的Spring的配置问题。如果Bean的某一个属性没有注入，`BeanFacotry`加载后，直至第一次使用调用`getBean`方法才会抛出异常。
    * `ApplicationContext`，它是在容器启动时，一次性创建了所有的Bean。这样，在容器启动时，我们就可以发现Spring中存在的配置错误，这样有利于检查所依赖属性是否注入。`ApplicationContext`启动后预载入所有的单实例Bean，通过预载入单实例bean ,确保当你需要的时候，你就不用等待，因为它们已经创建好了。
    * 相对于基本的`BeanFactory`，`ApplicationContext`唯一的不足是占用内存空间。当应用程序配置Bean较多时，程序启动较慢。
* 创建方式
    * `BeanFactory`通常以编程的方式被创建，`ApplicationContext`还能以声明的方式创建，如使用`ContextLoader`。
* 注册方式
    * `BeanFactory`和`ApplicationContext`都支持`BeanPostProcessor`、`BeanFactoryPostProcessor`的使用，但两者之间的区别是: `BeanFactory`需要手动注册，而`ApplicationContext`则是自动注册。

#### BeanFactory和ApplicationContext的关系详解
* `BeanFactory`简单粗暴，可以理解为就是个HashMap，Key是BeanName，Value是Bean实例。通常只提供注册(put)，获取(get)这两个功能。我们可以称之为“低级容器”。
* `ApplicationContext`可以称之为“高级容器”。因为他比`BeanFactory`多了更多的功能。他继承了多个接口，因此具备了更多的功能。例如资源的获取，支持多种消息(例如JSP tag的支持)，对`BeanFactory`多了工具级别的支持等待。所以你看他的名字，已经不是`BeanFactory`之类的工厂了，而是“应用上下文”，代表着整个大容器的所有功能。该接口定义了一个`refresh`方法，此方法是所有阅读`Spring`源码的人的最熟悉的方法，用于刷新整个容器，即重新加载/刷新所有的bean。
* 看下面的隶属`ApplicationContext`的 “高级容器”，依赖着 “低级容器”，这里说的是依赖，不是继承哦。他依赖着 “低级容器”的`getBean`功能。而高级容器有更多的功能: 支持不同的信息源头，可以访问文件资源，支持应用事件(Observer模式)。
* 通常用户看到的就是“高级容器”。 但`BeanFactory`也非常够用啦！
* “低级容器”只负载加载Bean，获取Bean。容器其他的高级功能是没有的。
* 加载配置文件，解析成`BeanDefinition`放在Map里。
* 调用`getBean`的时候，从`BeanDefinition`所属的Map里，拿出Class对象进行实例化，同时，如果有依赖关系，将递归调用`getBean`方法 - 完成依赖注入。
* 至于高级容器`ApplicationContext`，他包含了低级容器的功能，当他执行`refresh`模板方法的时候，将刷新整个容器的Bean。同时其作为高级容器，包含了太多的功能。一句话，他不仅仅是 IoC。他支持不同信息源头，支持`BeanFactory`工具类，支持层级容器，支持访问文件资源，支持事件发布通知，支持接口回调等等。

#### ApplicationContext常见的实现
* `FileSystemXmlApplicationContext`: 此容器从一个XML文件中加载beans的定义，XML Bean配置文件的全路径名必须提供给它的构造函数。
* `ClassPathXmlApplicationContext`: 此容器也从一个XML文件中加载beans的定义，需要正确设置classpath因为这个容器将在classpath里找bean配置。
* `WebXmlApplicationContext`: 此容器加载一个XML文件，此文件定义了一个WEB应用的所有bean。

#### Spring的依赖注入
* 控制反转主要实现方式有两种: 依赖注入和依赖查找
* 依赖注入: 相对于IoC而言，依赖注入(DI)更加准确地描述了IoC的设计理念。所谓依赖注入，即组件之间的依赖关系由容器在应用系统运行期来决定，也就是由容器动态地将某种依赖关系的目标对象实例注入到应用系统中的各个关联的组件之中。组件不做定位查询，只提供普通的Java方法让容器去决定依赖关系。

#### 依赖注入的基本原则
* 依赖注入的基本原则是: 应用组件不应该负责查找资源或者其他依赖的协作对象。配置对象的工作应该由IoC容器负责，“查找资源”的逻辑应该从应用组件的代码中抽取出来，交给IoC容器负责。容器全权负责组件的装配，它会把符合依赖关系的对象通过属性(JavaBean中的setter)或者是构造器传递给需要的对象。

#### 依赖注入有什么优势
* 依赖注入之所以更流行是因为它是一种更可取的方式: 让容器全权负责依赖查询，受管组件只需要暴露JavaBean的setter方法或者带参数的构造器或者接口，使容器可以在初始化时组装对象的依赖关系。其与依赖查找方式相比，主要优势为: 
    * 查找定位操作与应用代码完全无关。
    * 不依赖于容器的API，可以很容易地在任何容器以外使用应用对象。
    * 不需要特殊的接口，绝大多数对象可以做到完全不必依赖容器。

#### 依赖注入实现方式
* 构造器依赖注入: 构造器依赖注入通过容器触发一个类的构造器来实现的，该类有一系列参数，每个参数代表一个对其他类的依赖。
* Setter方法注入: Setter方法注入是容器通过调用无参构造器或无参static工厂方法实例化bean之后，调用该bean的setter方法，即实现了基于setter的依赖注入。


### Spring Beans
* Spring beans是那些形成Spring应用的主干的java对象。它们被Spring IOC容器初始化，装配，和管理。这些beans通过容器中配置的元数据创建。

#### Spring容器提供配置元数据
* 这里有三种重要的方法给Spring 容器提供配置元数据:
    * XML配置文件
    * 基于注解的配置
    * 基于java的配置
* Spring基于xml注入bean的几种方式:
    * Set方法注入；
    * 构造器注入：①通过index设置参数的位置；②通过type设置参数类型；
    * 静态工厂注入；
    * 实例工厂；

#### Spring bean的作用域
* Spring框架支持以下五种bean的作用域:
* singleton: bean在每个Spring ioc 容器中只有一个实例。
* prototype: 一个bean的定义可以有多个实例。
* request: 每次http请求都会创建一个bean，该作用域仅在基于web的`ApplicationContext`情形下有效。
* session: 在一个HTTP Session中，一个bean定义对应一个实例。该作用域仅在基于web的`ApplicationContext`情形下有效。
* global-session: 在一个全局的HTTP Session中，一个bean定义对应一个实例。该作用域仅在基于web的`ApplicationContext`情形下有效。

#### Spring框架中的单例bean的线程安全
* Spring框架中的单例bean不是线程安全的。
* spring 中的 bean 默认是单例模式，spring 框架并没有对单例 bean 进行多线程的封装处理。
* 实际上大部分时候spring bean无状态的(比如dao类)，所有某种程度上来说bean也是安全的，但如果bean有状态的话(比如view model对象)，那就要开发者自己去保证线程安全了，最简单的就是改变bean的作用域，把`singleton`变更为`prototype`，这样请求bean相当于`new Bean()`了，所以就可以保证线程安全了。
    * 有状态就是有数据存储功能。
    * 无状态就是不会保存数据。

#### Spring如何处理线程并发问题
* 在一般情况下，只有无状态的Bean才可以在多线程环境下共享，在Spring中，绝大部分Bean都可以声明为singleton作用域，因为Spring对一些Bean中非线程安全状态采用ThreadLocal进行处理，解决线程安全问题。
* ThreadLocal和线程同步机制都是为了解决多线程中相同变量的访问冲突问题。同步机制采用了“时间换空间”的方式，仅提供一份变量，不同的线程在访问前需要获取锁，没获得锁的线程则需要排队。而ThreadLocal采用了“空间换时间”的方式。
* ThreadLocal会为每一个线程提供一个独立的变量副本，从而隔离了多个线程对数据的访问冲突。因为每一个线程都拥有自己的变量副本，从而也就没有必要对该变量进行同步了。ThreadLocal提供了线程安全的共享对象，在编写多线程代码时，可以把不安全的变量封装进ThreadLocal。

#### bean的生命周期
* 使用Java关键字new进行bean实例化，然后该bean就可以使用了。一旦该bean不再被使用，则由Java自动进行垃圾回收。相比之下，Spring容器中的bean的生命周期就显得相对复杂多了。正确理解Spring bean的生命周期非常重要，因为你或许要利用Spring提供的扩展点来自定义bean的创建过程。
    ```
    容器启动 -> 实例化 -> 填充属性 -> 调用BeanNameAware的setBeanName()方法 -> 调用ApplicationContextAware的setApplicationContext()方法 -> 调用BeanPostProcessor的预初始化方法 -> 调用InitializingBean的afterPropertiesSet()方法 -> 调用自定义的初始化方法 -> 调用BeanPostProcessor的初始化后方法 -> bean可以使用
    ```
    * 我们对上图进行详细描述: 
        * Spring对bean进行实例化；
        * Spring将值和bean的引用注入到bean对应的属性中；
        * 如果bean实现了`BeanNameAware`接口，Spring将bean的ID传递给`setBeanName()`方法；
        * 如果bean实现了`BeanFactoryAware`接口，Spring将调用`setBeanFactory()`方法，将`BeanFactory`容器实例传入；
        * 如果bean实现了`ApplicationContextAware`接口，Spring将调用`setApplicationContext()`方法，将bean所在的应用上下文的引用传入进来；
        * 如果bean实现了`BeanPostProcessor`接口，Spring将调用它们的`postProcessBeforeInitialization()`方法；
        * 如果bean实现了`InitializingBean`接口，Spring将调用它们的`afterPropertiesSet()`方法。类似地，如果bean使用`initmethod`声明了初始化方法，该方法也会被调用；
        * 如果bean实现了`BeanPostProcessor`接口，Spring将调用它们的`postProcessAfterInitialization()`方法；
        * 此时，bean已经准备就绪，可以被应用程序使用了，它们将一直驻留在应用上下文中，直到该应用上下文被销毁；
        * 如果bean实现了`DisposableBean`接口，Spring将调用它的`destroy()`接口方法。同样，如果bean使用destroy-method声明了销毁方法，该方法也会被调用。
* 有两个重要的bean生命周期方法，第一个是`setup`，它是在容器加载bean的时候被调用。第二个方法是`teardown`它是在容器卸载类的时候被调用。
* bean标签有两个重要的属性(`init-method`和`destroy-method`)。用它们你可以自己定制初始化和注销方法。它们也有相应的注解（`@PostConstruct`和`@PreDestroy`）。

#### 内部bean
* 在Spring框架中，当一个bean仅被用作另一个bean的属性时，它能被声明为一个内部bean。
* 内部bean可以用setter注入“属性”和构造方法注入“构造参数”的方式来实现，内部bean通常是匿名的，它们的Scope一般是prototype。

#### bean装配
* 装配，或bean装配是指在Spring容器中把bean组装到一起，前提是容器需要知道bean的依赖关系，如何通过依赖注入来把它们装配到一起。
* 在Spring框架中，在配置文件中设定bean的依赖关系是一个很好的机制，Spring容器能够自动装配相互合作的bean，这意味着容器不需要和配置，能通过Bean工厂自动处理bean之间的协作。这意味着Spring可以通过向Bean Factory中注入的方式自动搞定bean之间的依赖关系。自动装配可以设置在每个bean上，也可以设定在特定的bean上。
* 在Spring框架xml配置中共有5种自动装配:
    * `no`: 默认的方式是不进行自动装配的，通过手工设置ref属性来进行装配bean。
    * `byName`: 通过bean的名称进行自动装配，如果一个bean的property与另一bean的name相同，就进行自动装配。
    * `byType`: 通过参数的数据类型进行自动装配。
    * `constructor`: 利用构造函数进行装配，并且构造函数的参数通过`byType`进行装配。
    * `autodetect`: 自动探测，如果有构造方法，通过construct的方式自动装配，否则使用`byType`的方式自动装配。
* 使用`@Autowired`注解来自动装配指定的bean。
* 在启动spring IoC时，容器自动装载了一个`AutowiredAnnotationBeanPostProcessor`后置处理器，当容器扫描到`@Autowied`、`@Resource`或`@Inject`时，就会在IoC容器自动查找需要的bean，并装配给该对象的属性。在使用`@Autowired`时，首先在容器中查询对应类型的bean:
    * 如果查询结果刚好为一个，就将该bean装配给`@Autowired`指定的数据；
    * 如果查询的结果不止一个，那么`@Autowired`会根据名称来查找；
    * 如果上述查找的结果为空，那么会抛出异常。解决方法时，使用`required=false`。
* 自动装配的局限性是:
* 自动装配的局限性是：
    * 重写: 你仍需用和配置来定义依赖，意味着总要重写自动装配。
    * 基本数据类型: 你不能自动装配简单的属性，如基本数据类型，String字符串，和类。
    * 模糊特性: 自动装配不如显式装配精确，如果有可能，建议使用显式装配。


### Spring数据访问
#### 对象关系映射集成模块

