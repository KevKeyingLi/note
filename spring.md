- [Sring简介](#Sring简介)
- [Spring的核心机制](#Spring的核心机制)
    * [管理Bean](#管理Bean)
    * [Ioc与DI](#Ioc与DI)
    * [依赖注入](#依赖注入)
    * [Spring容器中的Bean](#Spring容器中的Bean)
    * [使用自动装配注入合作者Bean](#使用自动装配注入合作者Bean)
- [创建Bean的3种方式](#创建Bean的3种方式)
    * [使用构造器创建Bean实例](#使用构造器创建Bean实例)
    * [使用静态工厂方法创建Bean](#使用静态工厂方法创建Bean)
    * [调用实例工厂方法创建Bean](#调用实例工厂方法创建Bean)
    * [协调作用域不同步的Bean](#协调作用域不同步的Bean)
- [两种后处理器](#两种后处理器)
    * [Bean后处理器](#Bean后处理器)
    * [容器后处理器](#容器后处理器)
- [Spring的“零配置”支持](#Spring的_零配置_支持)
    * [搜索Bean类](#搜索Bean类)
    * [使用@Resource配置依赖](#使用@Resource配置依赖)
    * [使用@PostConstruct和@PreDestroy定制生命周期行为](#使用@PostConstruct和@PreDestroy定制生命周期行为)
    * [Spring4.0增强的自动装配和精确装配](#Spring4.0增强的自动装配和精确装配)
- [Spring的AOP](#Spring的AOP)
    * [使用AspectJ实现AOP](#使用AspectJ实现AOP)
    * [AOP的基本概念](#AOP的基本概念)
- [Spring资源访问神器-Resource接口](#Spring资源访问神器_Resource接口)
- [Spring的Ioc容器详解](#Spring的Ioc容器详解)

---
* Refs:
    * > https://juejin.im/post/5dde06b46fb9a071a639e852
    * > http://codepub.cn/2015/06/21/Basic-knowledge-summary-of-Spring/#comments
---

## Sring简介
* Spring是一个分层的Java SE/EE应用一站式的轻量级开源框架。Spring核心是IOC和AOP。 
* Spring主要优点包括: 
    * 方便解耦，简化开发，通过Spring提供的IoC容器，我们可以将对象之间的依赖关系交由Spring进行控制，避免硬编码造成的程序耦合度高。
    * AOP编程的支持，通过Spring提供的AOP功能，方便进行面向切面编程。
    * 声明式事务的支持，在Spring中，我们可以从单调烦闷的事务管理代码中解脱出来，通过声明式方式灵活地进行事务的管理，提高开发效率和质量。
    * 方便程序的测试，可以用非容器依赖的编程方式进行几乎所有的测试工作。
    * 方便集成各种优秀框架，Spring提供了对各种优秀框架的直接支持。


## Spring的核心机制
### 管理Bean
* 程序主要是通过Spring容器来访问容器中的Bean，ApplicationContext是Spring容器最常用的接口，该接口有如下两个实现类
    * ClassPathXmlApplicationContext: 从类加载路径下搜索配置文件，并根据配置文件来创建Spring容器
    * FileSystemXmlApplicationContext: 从文件系统的相对路径或绝对路径下去搜索配置文件，并根据配置文件来创建Spring容器
    ```java
    public class BeanTest{
        public static void main(String args[]) throws Exception{
            ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
            Person p = ctx.getBean("person", Person.class);
            p.say();
        }
    }
    ```


### Ioc与DI
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


### 依赖注入
* 在传统模式下通常有两种做法
    * 原始做法: 调用者主动创建被依赖对象，然后再调用被依赖对象的方法
    * 简单工厂模式: 调用者先找到被依赖对象的工厂，然后主动通过工厂去获取被依赖对象，最后再调用被依赖对象的方法
* 主动这必然会导致调用者与被依赖对象实现类的硬编码耦合，非常不利于项目升级的维护。
* 使用Spring框架之后，调用者无需主动获取被依赖对象，调用者只要被动接受Spring容器为调用者的成员变量赋值即可。
* 两种注入方式
    * 设值注入
        * 设值注入是指IoC容器通过成员变量的setter方法来注入被依赖对象。这种注入方式简单、直观，因而在Spring的依赖注入里大量使用。
        * 设值注入有如下优点
            * 与传统的JavaBean的写法更相似，程序开发人员更容易理解、接受。通过setter方法设定依赖关系显得更加直观、自然
            * 对于复杂的依赖关系，如果采用构造注入，会导致构造器过于臃肿，难以阅读。Spring在创建Bean实例时，需要同时实例化其依赖的全部实例，因而导致性能下降。而使用设值注入，则能避免这些问题。
            * 尤其在某些成员变量可选的情况下，多参数的构造器更加笨重
    * 构造注入
        * 利用构造器来设置依赖关系的方式，被称为构造注入。通俗来说，就是驱动Spring在底层以反射方式执行带指定参数的构造器，当执行带参数的构造器时，就可利用构造器参数对成员变量执行初始化——这就是构造注入的本质。
        * 构造注入优势如下
            * 构造注入可以在构造器中决定依赖关系的注入顺序，优先依赖的优先注入
            * 对于依赖关系无需变化的Bean，构造注入更有用处。因为没有setter方法，所有的依赖关系全部在构造器内设定，无须担心后续的代码对依赖关系产生破坏
            * 依赖关系只能在构造器中设定，则只有组件的创建者才能改变组件的依赖关系，对组件的调用者而言，组件内部的依赖关系完全透明，更符合高内聚的原则
    * 建议采用设值注入为主，构造注入为辅的注入策略。对于依赖关系无须变化的注入，尽量采用构造注入；而其他依赖关系的注入，则考虑采用设值注入。


### Spring容器中的Bean
* 对于开发者来说，开发者使用Spring框架主要是做两件事: ①开发Bean；②配置Bean。对于Spring框架来说，它要做的就是根据配置文件来创建Bean实例，并调用Bean实例的方法完成“依赖注入”——这就是所谓IoC的本质。
* 容器中Bean的作用域
    * 当通过Spring容器创建一个Bean实例时，不仅可以完成Bean实例的实例化，还可以为Bean指定特定的作用域。Spring支持如下五种作用域
        * singleton: 单例模式，在整个Spring IoC容器中，singleton作用域的Bean将只生成一个实例
        * prototype: 每次通过容器的getBean()方法获取prototype作用域的Bean时，都将产生一个新的Bean实例
        * request: 对于一次HTTP请求，request作用域的Bean将只生成一个实例，这意味着，在同一次HTTP请求内，程序每次请求该Bean，得到的总是同一个实例。只有在Web应用中使用Spring时，该作用域才真正有效
        * session: 对于一次HTTP会话，session作用域的Bean将只生成一个实例，这意味着，在同一次HTTP会话内，程序每次请求该Bean，得到的总是同一个实例。只有在Web应用中使用Spring时，该作用域才真正有效
        * global session: 每个全局的HTTP Session对应一个Bean实例。在典型的情况下，仅在使用portlet context的时候有效，同样只在Web应用中有效
    * 如果不指定Bean的作用域，Spring默认使用singleton作用域。prototype作用域的Bean的创建、销毁代价比较大。而singleton作用域的Bean实例一旦创建成果，就可以重复使用。因此，应该尽量避免将Bean设置成prototype作用域。


### 使用自动装配注入合作者Bean
* Spring能自动装配Bean与Bean之间的依赖关系，即无须使用ref显式指定依赖Bean，而是由Spring容器检查XML配置文件内容，根据某种规则，为调用者Bean注入被依赖的Bean。
* Spring自动装配可通过`<beans/>`元素的`default-autowire`属性指定，该属性对配置文件中所有的Bean起作用；也可通过对`<bean/>`元素的`autowire`属性指定，该属性只对该Bean起作用。
* `autowire`和`default-autowire`可以接受如下值
    * no: 不使用自动装配。Bean依赖必须通过ref元素定义。这是默认配置，在较大的部署环境中不鼓励改变这个配置，显式配置合作者能够得到更清晰的依赖关系
    * byName: 根据setter方法名进行自动装配。Spring容器查找容器中全部Bean，找出其id与setter方法名去掉set前缀，并小写首字母后同名的Bean来完成注入。如果没有找到匹配的Bean实例，则Spring不会进行任何注入
    * byType: 根据setter方法的形参类型来自动装配。Spring容器查找容器中的全部Bean，如果正好有一个Bean类型与setter方法的形参类型匹配，就自动注入这个Bean；如果找到多个这样的Bean，就抛出一个异常；如果没有找到这样的Bean，则什么都不会发生，setter方法不会被调用
    * constructor: 与byType类似，区别是用于自动匹配构造器的参数。如果容器不能恰好找到一个与构造器参数类型匹配的Bean，则会抛出一个异常
    * autodetect: Spring容器根据Bean内部结构，自行决定使用constructor或byType策略。如果找到一个默认的构造函数，那么就会应用byType策略
* 当一个Bean既使用自动装配依赖，又使用ref显式指定依赖时，则显式指定的依赖覆盖自动装配依赖；对于大型的应用，不鼓励使用自动装配。虽然使用自动装配可减少配置文件的工作量，但大大将死了依赖关系的清晰性和透明性。依赖关系的装配依赖于源文件的属性名和属性类型，导致Bean与Bean之间的耦合降低到代码层次，不利于高层次解耦
    ```xml
    <!--通过设置可以将Bean排除在自动装配之外-->
    <bean id="" autowire-candidate="false"/>
    <!--除此之外，还可以在beans元素中指定，支持模式字符串，如下所有以abc结尾的Bean都被排除在自动装配之外-->
    <beans default-autowire-candidates="*abc"/>
    ```

## 创建Bean的3种方式
### 使用构造器创建Bean实例
* 使用构造器来创建Bean实例是最常见的情况，如果不采用构造注入，Spring底层会调用Bean类的无参数构造器来创建实例，因此要求该Bean类提供无参数的构造器。
* 采用默认的构造器创建Bean实例，Spring对Bean实例的所有属性执行默认初始化，即所有的基本类型的值初始化为0或false；所有的引用类型的值初始化为null。


### 使用静态工厂方法创建Bean
* 使用静态工厂方法创建Bean实例时，class属性也必须指定，但此时class属性并不是指定Bean实例的实现类，而是静态工厂类，Spring通过该属性知道由哪个工厂类来创建Bean实例。
* 除此之外，还需要使用factory-method属性来指定静态工厂方法，Spring将调用静态工厂方法返回一个Bean实例，一旦获得了指定Bean实例，Spring后面的处理步骤与采用普通方法创建Bean实例完全一样。如果静态工厂方法需要参数，则使用`<constructor-arg.../>`元素指定静态工厂方法的参数。


### 调用实例工厂方法创建Bean
* 实例工厂方法与静态工厂方法只有一个不同: 调用静态工厂方法只需使用工厂类即可，而调用实例工厂方法则需要工厂实例。使用实例工厂方法时，配置Bean实例的`<bean.../>`元素无须class属性，配置实例工厂方法使用`factory-bean`指定工厂实例。
* 采用实例工厂方法创建Bean的`<bean.../>`元素时需要指定如下两个属性
    * factory-bean: 该属性的值为工厂Bean的id
    * factory-method: 该属性指定实例工厂的工厂方法
* 若调用实例工厂方法时需要传入参数，则使用`<constructor-arg.../>`元素确定参数值。


### 协调作用域不同步的Bean
* 当singleton作用域的Bean依赖于prototype作用域的Bean时，会产生不同步的现象，原因是因为当Spring容器初始化时，容器会预初始化容器中所有的`singleton Bean`，由于`singleton Bean`依赖于`prototype Bean`，因此Spring在初始化`singleton Bean`之前，会先创建`prototypeBean`——然后才创建`singleton Bean`，接下里将`prototype Bean`注入`singleton Bean`。
* 解决不同步的方法有两种
    * 放弃依赖注入: singleton作用域的Bean每次需要prototype作用域的Bean时，主动向容器请求新的Bean实例，即可保证每次注入的`prototype Bean`实例都是最新的实例
    * 利用方法注入: 方法注入通常使用lookup方法注入，使用lookup方法注入可以让Spring容器重写容器中Bean的抽象或具体方法，返回查找容器中其他Bean的结果，被查找的Bean通常是一个`non-singleton Bean`。Spring通过使用JDK动态代理或cglib库修改客户端的二进制码，从而实现上述要求
* 建议采用第二种方法，使用方法注入。为了使用lookup方法注入，大致需要如下两步
    1. 将调用者Bean的实现类定义为抽象类，并定义一个抽象方法来获取被依赖的Bean
    2. 在`<bean.../>`元素中添加`<lookup-method.../>`子元素让Spring为调用者Bean的实现类实现指定的抽象方法


## 两种后处理器
* Spring提供了两种常用的后处理器
    * Bean后处理器: 这种后处理器会对容器中Bean进行后处理，对Bean进行额外加强
    * 容器后处理器: 这种后处理器会对IoC容器进行后处理，用于增强容器功能


### Bean后处理器
* Bean后处理器是一种特殊的Bean，这种特殊的Bean并不对外提供服务，它甚至可以无须id属性，它主要负责对容器中的其他Bean执行后处理，例如为容器中的目标Bean生成代理等，这种Bean称为Bean后处理器。Bean后处理器会在Bean实例创建成功之后，对Bean实例进行进一步的增强处理。Bean后处理器必须实现`BeanPostProcessor`接口，同时必须实现该接口的两个方法。
    1. `Object postProcessBeforeInitialization(Object bean, String name) throws BeansException`: 该方法的第一个参数是系统即将进行后处理的Bean实例，第二个参数是该Bean的配置id
    2. `Object postProcessAfterinitialization(Object bean, String name) throws BeansException`: 该方法的第一个参数是系统即将进行后处理的Bean实例，第二个参数是该Bean的配置id
* 容器中一旦注册了Bean后处理器，Bean后处理器就会自动启动，在容器中每个Bean创建时自动工作，Bean后处理器两个方法的回调时机如下图
    ```
                    +----------+
                    |注入依赖关系|
                    +----------+
                         |
    +--------------------------------------------+
    ｜回调postProcessBeforeInitialization进行后处理｜
    +--------------------------------------------+
                         ｜
          +--------------+--------------+
          | Bean初始化过程 |              |
          |   +----------+----------+   |
          |   |调用afterPropertiesSet|   |
          |   +----------+----------+   |
          |              |              |
          |+-------------+-------------+|
          ||调用init-methods属性指定的方法||
          |+-------------+-------------+|
          |              |              |
          +--------------+--------------+
                         |
    +--------------------+-----------------------+
    ｜回调postProcessAfterInitialization进行后处理 ｜
    +--------------------+-----------------------+
                         |
                         V
    ```
* 注意一点，如果使用BeanFactory作为Spring容器，则必须手动注册Bean后处理器，程序必须获取Bean后处理器实例，然后手动注册。
    ```java
    BeanPostProcessor bp = (BeanPostProcessor)beanFactory.getBean("bp");
    beanFactory.addBeanPostProcessor(bp);
    Person p = (Person)beanFactory.getBean("person");
    ```


### 容器后处理器
* Bean后处理器负责处理容器中的所有Bean实例，而容器后处理器则负责处理容器本身。容器后处理器必须实现`BeanFactoryPostProcessor`接口，并实现该接口的一个方法`postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)`实现该方法的方法体就是对Spring容器进行的处理，这种处理可以对Spring容器进行自定义扩展，当然也可以对Spring容器不进行任何处理。
* 类似于`BeanPostProcessor`，`ApplicationContext`可自动检测到容器中的容器后处理器，并且自动注册容器后处理器。但若使用`BeanFactory`作为Spring容器，则必须手动调用该容器后处理器来处理BeanFactory容器。


## Spring的“零配置”支持
### 搜索Bean类
* Spring提供如下几个Annotation来标注Spring Bean
    * @Component: 标注一个普通的Spring Bean类
    * @Controller: 标注一个控制器组件类
    * @Service: 标注一个业务逻辑组件类
    * @Repository: 标注一个DAO组件类
* 在Spring配置文件中做如下配置，指定自动扫描的包
    ```xml
    <context:component-scan base-package="edu.shu.spring.domain"/>
    ```


### 使用@Resource配置依赖
* `@Resource`位于`javax.annotation`包下，是来自JavaEE规范的一个`Annotation`，Spring直接借鉴了该`Annotation`，通过使用该`Annotation`为目标Bean指定协作者Bean。
* 使用`@Resource`与`<property.../>`元素的ref属性有相同的效果。
* `@Resource`不仅可以修饰setter方法，也可以直接修饰实例变量，如果使用`@Resource`修饰实例变量将会更加简单，此时Spring将会直接使用JavaEE规范的Field注入，此时连setter方法都可以不要。


### 使用@PostConstruct和@PreDestroy定制生命周期行为
* `@PostConstruct`和`@PreDestroy`同样位于javax.annotation包下，也是来自JavaEE规范的两个Annotation，Spring直接借鉴了它们，用于定制Spring容器中Bean的生命周期行为。它们都用于修饰方法，无须任何属性。其中前者修饰的方法时Bean的初始化方法；而后者修饰的方法时Bean销毁之前的方法。


### Spring4.0增强的自动装配和精确装配
* Spring提供了`@Autowired`注解来指定自动装配，`@Autowired`可以修饰setter方法、普通方法、实例变量和构造器等。
* 当使用`@Autowired`标注setter方法时，默认采用byType自动装配策略。
* 在这种策略下，符合自动装配类型的候选Bean实例常常有多个，这个时候就可能引起异常，为了实现精确的自动装配，Spring提供了`@Qualifier`注解，通过使用`@Qualifier`，允许根据Bean的id来执行自动装配。


## Spring的AOP
* AOP(Aspect Orient Programming)也就是面向切面编程，作为面向对象编程的一种补充，已经成为一种比较成熟的编程方式。
* 其实AOP问世的时间并不太长，AOP和OOP互为补充，面向切面编程将程序运行过程分解成各个切面。
* AOP专门用于处理系统中分布于各个模块(不同方法)中的交叉关注点的问题，在JavaEE应用中，常常通过AOP来处理一些具有横切性质的系统级服务，如事务管理、安全检查、缓存、对象池管理等，AOP已经成为一种非常常用的解决方案。


### 使用AspectJ实现AOP
* AspectJ是一个基于Java语言的AOP框架，提供了强大的AOP功能，其他很多AOP框架都借鉴或采纳其中的一些思想。
* 其主要包括两个部分: 一个部分定义了如何表达、定义AOP编程中的语法规范，通过这套语法规范，可以方便地用AOP来解决Java语言中存在的交叉关注点的问题；另一个部分是工具部分，包括编译、调试工具等。
* AOP实现可分为两类
    * 静态AOP实现: AOP框架在编译阶段对程序进行修改，即实现对目标类的增强，生成静态的AOP代理类，以AspectJ为代表
    * 动态AOP实现: AOP框架在运行阶段动态生成AOP代理，以实现对目标对象的增强，以Spring AOP为代表
* 一般来说，静态AOP实现具有较好的性能，但需要使用特殊的编译器。动态AOP实现是纯Java实现，因此无须特殊的编译器，但是通常性能略差。


### AOP的基本概念
* 关于面向切面编程的一些术语
    * 切面(Aspect): 切面用于组织多个Advice，Advice放在切面中定义
    * 连接点(Joinpoint): 程序执行过程中明确的点，如方法的调用，或者异常的抛出。在Spring AOP中，连接点总是方法的调用
    * 增强处理(Advice): AOP框架在特定的切入点执行的增强处理。处理有“around”、“before”和“after”等类型
    * 切入点(Pointcut): 可以插入增强处理的连接点。简而言之，当某个连接点满足指定要求时，该连接点将被添加增强处理，该连接点也就变成了切入点


## Spring资源访问神器—Resource接口
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



