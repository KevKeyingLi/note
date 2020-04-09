- [Sring简介](#Sring简介)
- [Ioc与DI](#Ioc与DI)

---
* Refs:
    * > https://juejin.im/post/5dde06b46fb9a071a639e852
---

## Sring简介
* Spring是一个分层的Java SE/EE应用一站式的轻量级开源框架。Spring核心是IOC和AOP。 
* Spring主要优点包括: 
    * 方便解耦，简化开发，通过Spring提供的IoC容器，我们可以将对象之间的依赖关系交由Spring进行控制，避免硬编码造成的程序耦合度高。
    * AOP编程的支持，通过Spring提供的AOP功能，方便进行面向切面编程。
    * 声明式事务的支持，在Spring中，我们可以从单调烦闷的事务管理代码中解脱出来，通过声明式方式灵活地进行事务的管理，提高开发效率和质量。
    * 方便程序的测试，可以用非容器依赖的编程方式进行几乎所有的测试工作。
    * 方便集成各种优秀框架，Spring提供了对各种优秀框架的直接支持。

## Ioc与DI
* IoC控制反转和DI依赖注入
    * 传统程序设计中，我们需要使用某个对象的方法，需要先通过new创建一个该对象，我们这时是主动行为；而IoC是我们将创建对象的控制权交给IoC容器，这时是由容器帮忙创建及注入依赖对象，我们的程序被动的接受IoC容器创建的对象，控制权反转，所以叫控制反转。
    * 由于IoC确实不够开门见山，所以提出了DI(依赖注入: Dependency Injection)的概念，即让第三方来实现注入，以移除我们类与需要使用的类之间的依赖关系。总的来说，IoC是目的，DI是手段，创建对象的过程往往意味着依赖的注入。我们为了实现IoC，让生成对象的方式由传统方式(new)反转过来，需要创建相关对象时由IoC容器帮我们注入(DI)。
    * 简单的说，就是我们类里需要另一个类，只需要让Spring帮我们创建 ，这叫做控制反转；然后Spring帮我们将需要的对象设置到我们的类中，这叫做依赖注入。
* 常见的几种注入方法
    1. 使用有参构造方法注入
        ```java
        public class  User{
            private String name;
            public User(String name){
                this.name = name;
            }
        } 
        User user = new User("tom");
        ```
    2. 使用属性注入
        ```java
        public class  User{
            private String name;
            public void setName(String name){
                this.name=name;
            }
        }
        User user = new User();
        user.setName("jack");
        ```
    3. 使用接口注入
        ```java
        // 将调用类所有依赖注入的方法抽取到接口中，调用类通过实现该接口提供相应的注入方法。 
        public interface Dao{
            public void delete(String name);
        } 
        public class DapIml implements Dao{
            private String name;
            public void delete(String name){
                this.name=name;
            }
        }
        ```
    * 上面的注入方式都需要我们手动的进行注入，如果有一个第三方容器能帮助我们完成类的实例化，以及依赖关系的装配，那么我们只需要专注于业务逻辑的开发即可。Spring就是这样的容器，它通过配置文件或注解描述类和类之间的依赖关系，自动完成类的初始化和依赖注入的工作。
* Spring的IoC例子
    1. 创建工程，导入jar包
        * 这里我们只是做IoC的操作，所以只需要导入核心模块里的jar包，beans、core、context、expression等。因为spring中并没有日志相关的jar包，所以我们还需要导入log4j和commons-logging。
    2. 创建一个类
        ```java
        public class User {
            public void add(){
                System.out.println("add.....");
            }
        }
        ```
    3. 创建一个xml配置文件
        ```xml
        <?xml version="1.0" encoding="UTF-8"?>
        <beans xmlns="http://www.springframework.org/schema/beans"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://www.springframework.org/schema/beans
                                http://www.springframework.org/schema/beans/spring-beans.xsd"> 

            //配置要创建的类  
            <bean id="user" class="com.cad.domain.User"/>        
        </beans>
        ```
    4. 进行测试
        ```java
        //这只是用来测试的代码，后期不会这么写
        public class Test {
            @org.junit.Test
            public void test(){
                //加载配置文件
                ApplicationContext context=new ClassPathXmlApplicationContext("bean.xml");
                //获取对象
                User user=(User) context.getBean("user");
                System.out.println(user);
                //调用方法
                user.add();
            }
        }
        ```
        * 在容器启动时，Spring会根据配置文件的描述信息，自动实例化Bean并完成依赖关系的装配，从容器中即可获得Bean实例，就可以直接使用。
        * Spring为什么仅凭一个简单的配置文件，就能神奇的实例化并配置好程序使用的Bean呢？答案是通过 Java的反射技术。
* Spring的DI例子
    * 我们的service层总是用到dao层，以前我们总是在Service层new出dao对象，现在我们使用依赖注入的方式向Service层注入dao层。
        ```java
        // UserDao
        public class UserDao {
            public void add(){
                System.out.println("dao.....");
            }
        }

        // UserService
        public class UserService {
            UserDao userdao;
            public void setUserdao(UserDao userdao){
                this.userdao=userdao;
            }

            public void add(){
                System.out.println("service.......");
                userdao.add();
            }
        }
        ```
        ```xml
        // 配置文件
        <bean id="userdao" class="com.cad.domain.UserDao"></bean> 
        //这样在实例化service的时候，同时装配了dao对象，实现了依赖注入
        <bean id="userservice" class="com.cad.domain.UserService">
            //ref为dao的id值
            <property name="userdao" ref="userdao"></property>
        </bean>
        ```

## Spring资源访问神器——Resource接口
* JDK提供的访问资源的类(如java.NET.URL,File)等并不能很好很方便的满足各种底层资源的访问需求。Spring设计了一个Resource接口，为应用提供了更强的访问底层资源的能力，该接口拥有对应不同资源类型的实现类。
* Resource接口的主要方法
    * `boolean exists()`: 资源是否存在
    * `boolean isOpen()`: 资源是否打开
    * `URL getURL()`: 返回对应资源的URL
    * `File getFile()`: 返回对应的文件对象
    * `InputStream getInputStream()`: 返回对应资源的输入流
* Resource在Spring框架中起着不可或缺的作用，Spring框架使用Resource装载各种资源，包括配置文件资源，国际化属性资源等。
* Resource接口的具体实现类
    * `ByteArrayResource`: 二进制数组表示的资源
    * `ClassPathResource`: 类路径下的资源 ，资源以相对于类路径的方式表示
    * `FileSystemResource`: 文件系统资源，资源以文件系统路径方式表示，如d:/a/b.txt
    * `InputStreamResource`: 对应一个`InputStream`的资源
    * `ServletContextResource`: 为访问容器上下文中的资源而设计的类。负责以相对于web应用根目录的路径加载资源
    * `UrlResource`: 封装了`java.net.URL`。用户能够访问任何可以通过URL表示的资源，如Http资源，Ftp资源等
* Spring的资源加载机制
    * 为了访问不同类型的资源，必须使用相应的Resource实现类，这是比较麻烦的。Spring提供了一个强大的加载资源的机制，仅通过资源地址的特殊标识就可以加载相应的资源。首先，我们了解一下Spring支持哪些资源类型的地址前缀:
        * `classpath`: 例如classpath:com/cad/domain/bean.xml。从类路径中加载资源
        * `file`: 例如 file:com/cad/domain/bean.xml.使用`UrlResource`从文件系统目录中加载资源。
        * `http`: // 例如www.baidu.com/resource/be… 使用`UrlResource`从web服务器加载资源
        * `ftp`: // 例如frp://10.22.10.11/bean.xml 使用`UrlResource`从ftp服务器加载资源
    * Spring定义了一套资源加载的接口。`ResourceLoader`接口仅有一个`getResource(String location)`的方法，可以根据资源地址加载文件资源。资源地址仅支持带资源类型前缀的地址，不支持Ant风格的资源路径表达式。`ResourcePatternResolver`扩展`ResourceLoader`接口，定义新的接口方法`getResources(String locationPattern)`，该方法支持带资源类型前缀以及Ant风格的资源路径的表达式。`PathMatchingResourcePatternResolver`是Spring提供的标准实现类。

## Spring的Ioc容器详解
* BeanFactory
    * `BeanFactory`是一个类工厂，和传统的类工厂不同，传统的类工厂仅负责构造一个类或几个类的实例；而`BeanFactory`可以创建并管理各种类的对象，Spring称这些被创建和管理的Java对象为Bean。
    * `BeanFactory`是一个接口，Spring为`BeanFactory`提供了多种实现，最常用的就是`XmlBeanFactory`。其中，`BeanFactory`接口最主要的方法就是`getBean(String beanName)`，该方法从容器中返回指定名称的Bean。此外，`BeanFactory`接口的功能可以通过实现它的接口进行扩展(比如`ApplicationContext`)。看下面的示例: 
        ```xml
        //我们使用Spring配置文件为User类提供配置信息，然后通过BeanFactory装载配置文件，启动Spring IoC容器。 
        <?xml version="1.0" encoding="UTF-8"?>
        <beans xmlns="http://www.springframework.org/schema/beans"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd">

            <bean id="user" class="com.cad.domain.User"></bean>   
        </beans>
        ```
        ```java
        // 我们通过XmlBeanFactory实现类启动Spring IoC容器 
        public class Test {
            @org.junit.Test
            public void test(){ 
                //获取配置文件
                ResourcePatternResolver  resolver = new PathMatchingResourcePatternResolver(); 
                Resource rs = resolver.getResource("classpath:bean.xml");

                //加载配置文件并启动IoC容器
                BeanFactory bf = new XmlBeanFactory(rs);

                //从容器中获取Bean对象
                User user = (User) bf.getBean("user");

                user.speak();
            }
        }
        ```
        * `XmlBeanFactory`装载Spring配置文件并启动IoC容器，通过`BeanFactory`启动IoC容器时，并不会初始化配置文件中定义的Bean，初始化创建动作在第一个调用时。在初始化`BeanFactory`，必须提供一种日志框架，我们使用`Log4J`。
* ApplicationContext
    * `ApplicationContext`由`BeanFactory`派生而来，提供了更多面向实际应用的功能。在`BeanFactory`中，很多功能需要编程方式来实现，而`ApplicationContext`中可以通过配置的方式来实现。
    * `ApplicationContext`的主要实现类是`ClassPathXmlApplicationContext`和`FileSystemXmlApplicationContext`，前者默认从类路径加载配置文件，后者默认从文件系统中加载配置文件，如下所示: 
        ```java
        // 和BeanFactory初始化相似，ApplicationContext初始化也很简单
        ApplicationContext ac=new ClassPathXmlApplicationContext("bean.xml");
        ```
    * `ApplicationContext`的初始化和`BeanFactory`初始化有一个重大的区别，`BeanFactory`初始化容器时并未初始化Bean，只有第一次访问Bean时才创建；而`ApplicationContext`则在初始化时就实例化所有的单实例的Bean。因此，`ApplicationContext`的初始化时间会稍长一点。
* WebApplicationContext
    * `WebApplicationContext`是专门为Web应用准备的，它允许以相对于Web根目录的路径中加载配置文件完成初始化工作。从`WebApplicationContext`中可以获取`ServletContext`的引用，整个`WebApplicationContext`对象作为属性放置到`ServletContext`中，以便Web应用环境中可以访问Spring应用上下文。`ConfigurableWebApplicationContext`扩展了`WebApplicationContext`,允许通过配置方式实例化`WebApplicationContext`，定义了两个重要方法。
        * `setServletContext(ServletContext servletcontext)`: 为Spring设置`ServletContext`
        * `setConfigLocation(String[] configLocations)`: 设置Spring配置文件地址。
    * `WebApplicationContext`初始化的时机和方式是: 利用Spring提供的`ContextLoaderListener`监听器去监听`ServletContext`对象的创建，当`ServletContext`对象创建时，创建并初始化`WebApplicationContext`对象。因此，我们只需要在`web.xml`配置监听器即可。
        ```java
        <!-- 利用Spring提供的ContextLoaderListener监听器去监听ServletContext对象的创建，并初始化WebApplicationContext对象 -->
        <listener>
            <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
        </listener>

        <!-- Context Configuration locations for Spring XML files(默认查找/WEB-INF/applicationContext.xml) -->
        <context-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:applicationContext.xml</param-value>
        </context-param>
        ```
* BeanFactory、ApplicationContext和WebApplicationContext的联系与区别
    * Spring通过一个配置文件描述Bean与Bean之间的依赖关系，通过Java语言的反射技术能实例化Bean并建立Bean之间的依赖关系。Spring的IoC容器在完成这些底层工作的基础上，还提供了bean实例缓存、生命周期管理、事件发布，资源装载等高级服务。
    * `BeanFactory`是Spring最核心的接口，提供了高级IoC的配置机制。`ApplicationContext`建立在`BeanFactory`的基础上，是`BeanFactory`的子接口，提供了更多面向应用的功能。我们一般称`BeanFactory`为IoC容器，`ApplicationContext`为应用上下文，也称为Spring容器。`WebApplicationContext`是专门为Web应用准备的，它允许以相对于Web根目录的路径中加载配置文件完成初始化工作，是`ApplicationContext`接口的子接口。
    * `BeanFactory`是Spring框架的基础，面向Spring本身；`ApplicationContext`面向使用Spring框架的开发者，几乎所有的应用我们都直接使用`ApplicationContext`而非底层的`BeanFactory`；`WebApplicationContext`是专门用于Web应用
* 父子容器
    * 通过`HierarchicalBeanFactory`接口，Spring的IoC容器可以建立父子层级关联的体系: 子容器可以访问父容器的Bean，父容器不能访问子容器的Bean。
    * Spring使用父子容器实现了很多功能，比如在Spring MVC中，控制器Bean位于子容器中，业务层和持久层Bean位于父容器中。但即使这样，控制器Bean也可以引用持久层和业务层的Bean，而业务层和持久层就看不到控制器Bean。



