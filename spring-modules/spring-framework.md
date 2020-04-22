- [Spring的核心](#Spring的核心)
    * [Spring之旅](#Spring之旅)
        * [简化Java开发](#简化Java开发)
            * [激发POJO的潜能](#激发POJO的潜能)
            * [依赖注入](#依赖注入)
            * [应用切面](#应用切面)
            * [使用模板消除样式代码](#使用模板消除样式代码)
        * [容纳你的Bean](#容纳你的Bean)
            * [使用应用上下文](#使用应用上下文)
            * [bean的生命周期](#bean的生命周期)
        * [俯瞰Spring风景线](#俯瞰Spring风景线)
            * [Spring模块](#Spring模块)
    * [装配Bean](#装配Bean)
        * [Spring配置的可选方案](#Spring配置的可选方案)
        * [自动化装配bean](#自动化装配bean)
            * [创建可被发现的bean](#创建可被发现的bean)
            * [为组件扫描的bean命名](#为组件扫描的bean命名)
            * [设置组件扫描的基础包](#设置组件扫描的基础包)
            * [通过为bean添加注解实现自动装配](#通过为bean添加注解实现自动装配)
            * [验证自动装配](#验证自动装配)
        * [通过Java代码装配bean](#通过Java代码装配bean)
            * [创建配置类](#创建配置类)
            * [借助JavaConfig实现注入](#借助JavaConfig实现注入)
        * [通过XML装配bean](#通过XML装配bean)
            * [创建XML配置规范](#创建XML配置规范)
            * [声明一个简单的<bean>](#声明一个简单的<bean>)
            * [借助构造器注入初始化bean](#借助构造器注入初始化bean)
            * [设置属性](#设置属性)
    * [高级装配](#高级装配)
        * [环境与profile](#环境与profile)
            * [配置profile bean](#配置profile-bean)
            * [激活profile](#激活profile)
        * [条件化的bean](#条件化的bean)

---

## Spring的核心
### Spring之旅
#### 简化Java开发
* 为了降低Java开发的复杂性，Spring采取了以下4种关键策略: 
    * 基于POJO的轻量级和最小侵入性编程；
    * 通过依赖注入和面向接口实现松耦合；
    * 基于切面和惯例进行声明式编程；
    * 通过切面和模板减少样板式代码。

##### 激发POJO的潜能
* 很多框架通过强迫应用继承它们的类或实现它们的接口从而导致应用与框架绑死。Spring竭力避免因自身的API而弄乱你的应用代码。Spring不会强迫你实现Spring规范的接口或继承Spring规范的类，相反，在基于Spring构建的应用中，它的类通常没有任何痕迹表明你使用了Spring。
* Spring的非侵入编程模型意味着这个类在Spring应用和非Spring应用中都可以发挥同样的作用。

##### 依赖注入
###### DI功能是如何实现的
* 任何一个有实际意义的应用都会由两个或者更多的类组成，这些类相互之间进行协作来完成特定的业务逻辑。按照传统的做法，每个对象负责管理与自己相互协作的对象的引用，这将会导致高度耦合和难以测试的代码。
    ```java
    public class DamselRescuingKnight implements Knight {
        private RescueDamselQuest quest;
        public DamselRescuingKnight() { this.quest = new RescueDamselQuest();}
        public void embarkOnQuest() { quest.embark(); }
    }
    ```
    * `DamselRescuingKnight`在它的构造函数中自行创建了`Rescue DamselQuest`，这使得`DamselRescuingKnight`紧密地和`RescueDamselQuest`耦合到了一起。
    * 为这个`DamselRescuingKnight`编写单元测试将出奇地困难。在这样的一个测试中，你必须保证当骑士的`embarkOnQuest()`方法被调用的时候，探险的`embark()`方法也要被调用。但是没有一个简单明了的方式能够实现这一点。
* 耦合具有两面性
    * 紧密耦合的代码难以测试、难以复用、难以理解
    * 一定程度的耦合又是必须的，完全没有耦合的代码什么也做不了，不同的类必须以适当的方式进行交互
* 通过DI，对象的依赖关系将由系统中负责协调各对象的第三方组件在创建对象的时候进行设定。对象无需自行创建或管理它们的依赖关系，依赖关系将被自动注入到需要它们的对象当中去。
    ```java
    public class BraveKnight implements Knight {
        private Quest quest;
        public BraveKnight(Quest quest) { this.quest = quest; }
        public void embarkOnQuest() { quest.embark(); }
    }
    ```
    * 不同于之前的`DamselRescuingKnight`，`BraveKnight`没有自行创建探险任务，而是在构造的时候把探险任务作为构造器参数传入。这是依赖注入的方式之一，即构造器注入。
    * 更重要的是，传入的探险类型是`Quest`，也就是所有探险任务都必须实现的一个接口。所以，`BraveKnight`能够响应`RescueDamselQuest`、`SlayDragonQuest`、`MakeRoundTableRounderQuest`等任意的`Quest`实现。
* `DI`所带来的最大收益是松耦合。如果一个对象只通过接口来表明依赖关系，那么这种依赖就能够在对象本身毫不知情的情况下，用不同的具体实现进行替换。
* 对依赖进行替换的一个最常用方法就是在测试的时候使用`mock`实现。我们无法充分地测试`DamselRescuingKnight`，因为它是紧耦合的；但是可以轻松地测试`BraveKnight`，只需给它一个`Quest`的`mock`实现即可.
    ```java
    import static org.mockito.Mockito.*;
    import org.junit.Test;
    public class BraveKnightTest {
        @Test
        public void knightShouldEmbarkOnQuest() {
            Quest mockQuest = mock(Quest.class);
            BraveKnight knight = new BraveKnight(mockQuest);
            knight.embarkOnQuest();
            verify(mockQuest, times(1)).embark();
        }
    }
    ```
    * 你可以使用`mock`框架`Mockito`去创建一个`Quest`接口的`mock`实现。通过这个`mock`对象，就可以创建一个新的`BraveKnight`实例，并通过构造器注入这个`mock Quest`。当调用`embarkOnQuest()`方法时，你可以要求`Mockito`框架验证`Quest`的`mock`实现的`embark()`方法仅仅被调用了一次。

###### 将Quest注入到Knight中
    ```java
    import java.io.PrintStream;
    public class SlayDragonQuest implements Quest {
        private PrintStream stream;
        public SlayDragonQuest(PrintStream stream) { this.stream = stream; }
        public void embark() { stream.println("Embarking on quest to slay the dragon!"); }
    }
    ```
* 创建应用组件之间协作的行为通常称为装配(wiring)。Spring有多种装配bean的方式，采用XML是很常见的一种装配方式。
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
        <bean id="knight" class="sia.knights.BraveKnight">
            <constructor-arg ref="quest" />
        </bean>
        <bean id="quest" class="sia.knights.SlayDragonQuest">
            <constructor-arg value="#{T(System).out}" />
        </bean>
    </beans>
    ```
    * `BraveKnight`和`SlayDragonQuest`被声明为Spring中的bean。
    * 就`BraveKnight bean`来讲，它在构造时传入了对`SlayDragonQuest bean`的引用，将其作为构造器参数。
* Spring还支持使用Java来描述配置。
    ```java
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    @Configuration
    public class KnightConfig {
        @Bean
        public Knight knight() { return new BraveKnight(quest()); }
        @Bean
        public Quest quest() { return new SlayDragonQuest(System.out); }
    }
    ```
* 不管使用的是基于XML的配置还是基于Java的配置，DI所带来的收益都是相同的。
* 尽管`BraveKnight`依赖于`Quest`，但是它并不知道传递给它的是什么类型的`Quest`，也不知道这个`Quest`来自哪里。与之类似，`SlayDragonQuest`依赖于`PrintStream`，但是在编码时它并不需要知道这个`PrintStream`是什么样子的。只有Spring通过它的配置，能够了解这些组成部分是如何装配起来的。这样的话，就可以在不改变所依赖的类的情况下，修改依赖关系。

###### 观察它如何工作
* Spring通过应用上下文(Application Context)装载bean的定义并把它们组装起来。
* Spring应用上下文全权负责对象的创建和组装。
* Spring自带了多种应用上下文的实现，它们之间主要的区别仅仅在于如何加载配置。
* 因为`knights.xml`中的bean是使用XML文件进行配置的，所以选择`ClassPathXmlApplicationContext`作为应用上下文相对是比较合适的。
    ```java
    import org.springframework.context.support.ClassPathXmlApplicationContext;
    public class KnightMain {
        public static void main(String[] args) throws Exception {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/knight.xml");
            Knight knight = context.getBean(Knight.class);
            knight.embarkOnQuest();
            context.close();
        }
    }
    ```
    * 这里的`main()`方法基于`knights.xml`文件创建了 Spring 应用上下文。
    * 随后它调用该应用上下文获取一个ID为`knight`的 bean。
    * 得到`Knight`对象的引用后，只需简单调用`embarkOnQuest()`方法就可以执行所赋予的探险任务了。
    * 注意这个类完全不知道我们的英雄骑士接受哪种探险任务，而且完全没有意识到这是由`BraveKnight`来执行的。只有`knights.xml`文件知道哪个骑士执行哪种探险任务。

##### 应用切面
* 面向切面编程(aspect-oriented programming，AOP)允许你把遍布应用各处的功能分离出来形成可重用的组件。
* 面向切面编程往往被定义为促使软件系统实现关注点的分离一项技术。系统由许多不同的组件组成，每一个组件各负责一块特定功能。除了实现自身核心的功能之外，这些组件还经常承担着额外的职责。诸如日志、事务管理和安全这样的系统服务经常融入到自身具有核心业务逻辑的组件中去，这些系统服务通常被称为横切关注点，因为它们会跨越系统的多个组件。
* 如果将这些关注点分散到多个组件中去，你的代码将会带来双重的复杂性。
    * 实现系统关注点功能的代码将会重复出现在多个组件中。这意味着如果你要改变这些关注点的逻辑，必须修改各个模块中的相关实现。即使你把这些关注点抽象为一个独立的模块，其他模块只是调用它的方法，但方法的调用还是会重复出现在各个模块中。
    * 组件会因为那些与自身核心业务无关的代码而变得混乱。一个向地址簿增加地址条目的方法应该只关注如何添加地址，而不应该关注它是不是安全的或者是否需要支持事务。
* AOP 能够使这些服务模块化，并以声明的方式将它们应用到它们需要影响的组件中去。所造成的结果就是这些组件会具有更高的内聚性并且会更加关注自身的业务，完全不需要了解涉及系统服务所带来复杂性。总之，AOP能够确保POJO的简单性。
* 我们可以把切面想象为覆盖在很多组件之上的一个外壳。应用是由那些实现各自业务功能的模块组成的。借助AOP，可以使用各种功能层去包裹核心业务层。这些层以声明的方式灵活地应用到系统中，你的核心应用甚至根本不知道它们的存在。这是一个非常强大的理念，可以将安全、事务和日志关注点与核心业务逻辑相分离。

###### AOP应用
* 用`Minstrel`这个服务类来记载`Knight`的所有事迹。
    ```java
    import java.io.PrintStream;
    public class Minstrel {
        private PrintStream stream;
        public Minstrel(PrintStream stream) { this.stream = stream; }
        public void singBeforeQuest() { stream.println("Fa la la, the knight is so brave!"); }
        public void singAfterQuest() { stream.println("Tee hee hee, the brave knight " + "did embark on a quest!"); }
    }
    ```
* 我们适当做一下调整从而让`BraveKnight`可以使用`Minstrel`。
    ```java
    public class BraveKnight implements Knight {
        private Quest quest;
        private Minstrel minstrel;
        public BraveKnight(Quest quest, Minstrel minstrel) {
            is.quest = quest;
            this.minstrel = minstrel;
        }
        public void embarkOnQuest() throws QuestException {
            minstrel.singBeforeQuest();
            quest.embark();
            minstrl.singAfterQuest();
        }
    } 
    ```
    * 把吟游诗人注入到`BarveKnight`类中，这不仅使`BraveKnight`的代码复杂化了，而且还疑惑是否还需要一个不需要吟游诗人的骑士。
* 利用AOP，你可以声明吟游诗人必须歌颂骑士的探险事迹，而骑士本身并不用直接访问`Minstrel`的方法。
* 要将`Minstrel`抽象为一个切面，你所需要做的事情就是在一个Spring配置文件中声明它。
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xsi:schemaLocation="http://www.springframework.org/schema/aop 
                            http://www.springframework.org/schema/aop/spring-aop.xsd
                            http://www.springframework.org/schema/beans 
                            http://www.springframework.org/schema/beans/spring-beans.xsd">
        <bean id="knight" class="sia.knights.BraveKnight">
            <constructor-arg ref="quest" />
        </bean>
        <bean id="quest" class="sia.knights.SlayDragonQuest">
            <constructor-arg value="#{T(System).out}" />
        </bean>
        <bean id="minstrel" class="sia.knights.Minstrel">
            <constructor-arg value="#{T(System).out}" />
        </bean>
        <aop:config>
            <aop:aspect ref="minstrel">
                <aop:pointcut id="embark" expression="execution(* *.embarkOnQuest(..))"/>
                <aop:before pointcut-ref="embark" method="singBeforeQuest"/>
                <aop:after pointcut-ref="embark" method="singAfterQuest"/>
            </aop:aspect>
        </aop:config>
    </beans>
    ```
    * 这里使用了Spring的aop配置命名空间把`Minstrel`bean声明为一个切面。
        1. 需要把`Minstrel`声明为一个bean，然后在元素中引用该bean。
        2. 定义切面，声明(使用)在`embarkOnQuest()`方法执行前调用`Minstrel`的`singBeforeQuest()`方法，这种方式被称为前置通知(before advice)。
        3. 声明(使用)在`embarkOnQuest()`方法执行后调用 singAfterQuest() 方 法。这种方式被称为后置通知(after advice)。
    * 在这两种方式中，`pointcut-ref`属性都引用了名字为`embark`的切入点。该切入点是在前边的元素中定义的，并配置`expression`属性来选择所应用的通知。

##### 使用模板消除样式代码
* 样板式代码的一个常见范例是使用JDBC访问数据库查询数据。
    ```java
    public Employee getEmployeeById(long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        Result rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatment("select id, firstname, lastname, salary from " + "employee where id=?");
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            Employee employee = null;
            if (rs.next()) {
                employee = new Employee();
                employee.setId(rs.getLong("id"));
                employee.setFirstName(rs.getString("firstname"));
                employee.setLastName(rs.getString("lastname"));
                employee.setSalary(rs.getBigDecimal("salary"));
            }
            return employee;
        } catch (SQLException e) {
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    tmt.close();
                } catch (SQLException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }    
        }
        return null;
    }
    ```
* Spring旨在通过模板封装来消除样板式代码。Spring的JdbcTemplate使得执行数据库操作时，避免传统的JDBC样板代码成为了可能。
* 使用Spring的JdbcTemplate重写的`getEmployeeById()`方法仅仅关注于获取员工数据的核心逻辑，而不需要迎合JDBC API的需求。
    ```java
    public Employee getEmployeeById(long id) {
        return jdbcTemplate.queryForObject(
            "select id, firstname, lastname, salary " + "from employee where id=?",
            new RowMapper<Employee>() {
                public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Employee employee = new Employee();
                    employee.setId(rs.getLong("id"));
                    employee.setFirstName(rs.getString("firstname"));
                    employee.setLastName(rs.getString("lastname"));
                    employee.setSalary(rs.getBigDecimal("salary"));
                    return employee;
                }
            }, 
        id);
    }
    ```
    * 新版本的`getEmployeeById()`简单多了，而且仅仅关注于从数据库中查询员工。模板的`queryForObject()`方法需要一个SQL查询语句，一个RowMapper对象(把数据映射为一个域对象)，零个或多个查询参数。`GetEmployeeById()`方法再也看不到以前的JDBC样板式代码了，它们全部被封装到了模板中。

#### 容纳你的Bean
* 在基于Spring的应用中，你的应用对象生存于Spring容器(container)中。Spring容器负责创建对象，装配它们，配置它们并管理它们的整个生命周期，从生存到死亡(在这里，可能就是`new`到`finalize()`)。
* 容器是Spring框架的核心。Spring容器使用DI管理构成应用的组件，它会创建相互协作的组件之间的关联。毫无疑问，这些对象更简单干净，更易于理解，更易于重用并且更易于进行单元测试。
* Spring容器并不是只有一个。Spring自带了多个容器实现，可以归为两种不同的类型。bean工厂是最简单的容器，提供基本的DI支持。应用上下文基于`BeanFactory`构建，并提供应用框架级别的服务，例如从属性文件解析文本信息以及发布应用事件给感兴趣的事件监听者。
* 虽然我们可以在bean工厂和应用上下文之间任选一种，但bean工厂对大多数应用来说往往太低级了，因此，应用上下文要比bean工厂更受欢迎。

##### 使用应用上下文
* Spring 自带了多种类型的应用上下文，下面罗列的几个是你最有可能遇到的。
    * `AnnotationConfigApplicationContext`: 从一个或多个基于Java的配置类中加载Spring应用上下文。
    * `AnnotationConfigWebApplicationContext`: 从一个或多个基于Java的配置类中加载Spring Web应用上下文。
    * `ClassPathXmlApplicationContext`: 从类路径下的一个或多个XML配置文件中加载上下文定义，把应用上下文的定义文件作为类资源。
    * `FileSystemXmlapplicationcontext`: 从文件系统下的一个或多个XML配置文件中加载上下文定义。
    * `XmlWebApplicationContext`: 从Web应用下的一个或多个XML配置文件中加载上下文定义。
* 无论是从文件系统中装载应用上下文还是从类路径下装载应用上下文，将bean加载到bean工厂的过程都是相似的。
    ```java
    ApplicationContext context = new FileSystemXmlApplicationContext("c:/knight.xml");
    ApplicationContext context = new ClassPathXmlApplicationContext("knight.xml");
    ```
    * 使用`FileSystemXmlApplicationContext`和使用`ClassPathXmlApplicationContext`的区别在于:        
        * `FileSystemXmlApplicationContext`在指定的文件系统路径下查找`knight.xml`文件；
        * `ClassPathXmlApplicationContext`是在所有的类路径(包含JAR文件)下查找`knight.xml`文件。
    ```java
    ApplicationContext context = new AnnotationConfigApplicationContext(com.springinaction.knights.config.KnightConfig.class);
    ```
    * Java配置中加载应用上下文

##### bean的生命周期
* 使用Java关键字new进行bean实例化，然后该bean就可以使用了。一旦该bean不再被使用，则由Java自动进行垃圾回收。
* bean装载到Spring应用上下文中的一个典型的生命周期过程
    ```
    实例化 -> 填充属性 -> 调用BeanNameAware的setBeanName() -> 调用BeanFactoryAware的setBeanFactory() -> 调用ApplicationContextAware的setApplicationContext() -> 调用BeanPostProcessor的预初始化方法 -> 调用InitializingBean的afterPropertiesSet() -> 调用自定义初始化方法 -> 调用BeanPostProcessor初始化后方法 -> bean可以使用了 -> 使用bean -> 关闭容器 -> 调用DisposableBean的destory() -> 调用自定义销毁方法 -> 完了
    ```
* 在bean准备就绪之前，bean工厂执行了若干启动步骤。
    1. Spring对bean进行实例化；
    2. Spring将值和bean的引用注入到bean对应的属性中；
    3. 如果bean实现了`BeanNameAware`接口，Spring将bean的ID传递给`setBeanName()`方法；
    4. 如果bean实现了`BeanFactoryAware`接口，Spring将调用`setBeanFactory()`方法，将`BeanFactory`容器实例传入；
    5. 如果bean实现了`ApplicationContextAware`接口，Spring将调用`setApplicationContext()`方法，将bean所在的应用上下文的引用传入进来；
    6. 如果bean实现了`BeanPostProcessor`接口，Spring将调用它们的`postProcessBefore-Initialization()`方法；
    7. 如果bean实现了`InitializingBean`接口，Spring 将调用它们的`afterPropertiesSet()`方法。类似地，如果bean使用`initmethod`声明了初始化方法，该方法也会被调用；
    8. 如果bean实现了`BeanPostProcessor`接口，Spring将调用它们的`postProcessAfter-Initialization()`方法；
    9. 此时，bean已经准备就绪，可以被应用程序使用了，它们将一直驻留在应用上下文中，直到该应用上下文被销毁；
    10. 如果bean实现了`DisposableBean`接口，Spring 将调用它的`destroy()`接口方法。

#### 俯瞰Spring风景线
* 在Spring框架的范畴内，你会发现Spring简化Java开发的多种方式。但在Spring框架之外还存在一个构建在核心框架之上的庞大生态圈，它将Spring扩展到不同的领域，例如Web服务、REST、移动开发以及NoSQL。

##### Spring模块
* 这些模块依据其所属的功能可以划分为6类不同的功能
    * 数据访问集成
        * JDBC、Transaction、ORM、OXM、Messsage和JMS
        * Spring的JDBC和DAO(Data Access Object)模块抽象了这些样板式代码，使我们的数据库代码变得简单明了，还可以避免因为关闭数据库资源失败而引发的问题。该模块在多种数据库服务的错误信息之上构建了一个语义丰富的异常层，以后我们再也不需要解释那些隐晦专有的SQL错误信息。
        * Spring的ORM(Object-Relational Mapping)模块建立在对DAO的支持之上，并为多个ORM框架提供了一种构建DAO的简便方式。Spring对许多流行的ORM框架进行了集成，包括Hibernate、Java Persisternce API、Java Data Object和iBATIS SQL Maps。
        * JMS(Java Message Service)之上构建的Spring抽象层，它会使用消息以异步的方式与其他应用集成。
    * Web与远程调用
        * Web、Web Servlet、Web portlet和WebSocket
    * 面向切面编程
        * AOP、Aspects
        * 这个模块是Spring应用系统中开发切面的基础。与DI一样，AOP可以帮助应用对象解耦。借助于AOP，可以将遍布系统的关注点从它们所应用的对象中解耦出来。
    * Instrumentation
        * Instrument、Instrument Tomcat
    * Spring核心容器
        * Beans、Core、Context、Expressio和Context support
        * 容器是Spring框架最核心的部分，它管理着Spring应用中bean的创建、配置和管理。在该模块中，包括了Spring bean工厂，它为Spring提供了DI的功能。基于bean工厂，我们还会发现有多种Spring应用上下文的实现，每一种都提供了配置Spring的不同方式。
    * 测试

### 装配Bean
#### Spring配置的可选方案
* 当描述bean如何进行装配时，Spring具有非常大的灵活性，它提供了三种主要的装配机制:
    * 在XML中进行显式配置。
    * 在Java中进行显式配置。
    * 隐式的bean发现机制和自动装配。
* Spring的配置风格是可以互相搭配的，一个项目里用几种不同的装配分式。
* 我的建议是尽可能地使用自动配置的机制。显式配置越少越好。当你必须要显式配置bean的时候(比如，有些源码不是由你来维护的，而当你需要为这些代码配置bean的时候)，我推荐使用类型安全并且比XML更加强大的JavaConfig。最后，只有当你想要使用便利的XML命名空间，并且在JavaConfig中没有同样的实现时，才应该使用XML。

#### 自动化装配bean
* 尽管你会发现这些显式装配技术非常有用，但是在便利性方面，最强大的还是Spring的自动化配置。
* Spring 从两个角度来实现自动化装配: 
    * 组件扫描(component scanning): Spring会自动发现应用上下文中所创建的 bean。
    * 自动装配(autowiring): Spring自动满足bean之间的依赖。
* 为了阐述组件扫描和装配，我们需要创建几个bean，它们代表了一个音响系统中的组件。首先，要创建`CompactDisc`类，Spring会发现它并将其创建为一个bean。然后，会创建一个`CDPlayer`类，让Spring发现它，并将`CompactDiscbean`注入进来。

##### 创建可被发现的bean
* CD为我们阐述DI如何运行提供了一个很好的样例。如果你不将CD插入(注入)到CD播放器中，那么CD播放器其实是没有太大用处的。所以，可以这样说CD播放器依赖于CD才能完成它的使命。
* 首先在Java中建立CD的概念
    ```java
    package soundsystem;
    public interface CompactDisc {
        void play();
    }
    ```
    * `CompactDisc`的具体内容并不重要，重要的是你将其定义为一个接口。作为接口，它定义了CD播放器对一盘CD所能进行的操作。它将CD播放器的任意实现与CD本身的耦合降低到了最小的程度。
    * 我们还需要一个`CompactDisc`的实现，我们可以有`CompactDisc`接口的多个实现。
* 创建其中的一个实现，带有`@Component`注解的`CompactDisc`实现类`SgtPeppers`: 
    ```java
    package soundsystem;
    import org.springframework.stereotype.Component;
    @Component
    public class SgtPeppers implements CompactDisc {
        private String title = "Sgt. Pepper's Lonely Hearts Club Band";  
        private String artist = "The Beatles";
        public void play() { System.out.println("Playing " + title + " by " + artist); }
    }
    ```
* 和`CompactDisc`接口一样，`SgtPeppers`的具体内容并不重要。你需要注意的就是`SgtPeppers`类上使用了`@Component`注解。这个简单的注解表明该类会作为组件类，并告知Spring要为这个类创建bean。没有必要显式配置`SgtPeppersbean`，因为这个类使用了`@Component`注解，所以Spring会为你把事情处理妥当。
* 组件扫描默认是不启用的。我们还需要显式配置一下Spring， 从而命令它去寻找带有`@Component`注解的类，并为其创建bean。以下的配置类展现了完成这项任务的最简洁配置，`@ComponentScan`注解启用了组件扫描:
    ```java
    package soundsystem;
    import org.springframework.context.annotation.ComponentScan;
    import org.springframework.context.annotation.Configuration;
    @Configuration
    @ComponentScan
    public class CDPlayerConfig { 
    }
    ```
    * 类`CDPlayerConfig`通过Java代码定义了Spring的装配规则，并没有显式地声明任何bean，只不过它使用了`@ComponentScan`注解，这个注解能够在Spring中启用组件扫描。
    * 如果没有其他配置的话，`@ComponentScan`默认会扫描与配置类相同的包。因为`CDPlayerConfig`类位于`soundsystem`包中，因此Spring将会扫描这个包以及这个包下的所有子包，查找带有`@Component`注解的类。这样的话，就能发现`CompactDisc`，并且会在Spring中自动为其创建一个bean。
* 使用XML来启用组件扫描
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:c="http://www.springframework.org/schema/c"
    xmlns:p="http://www.springframework.org/schema/p"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

        <context:component-scan base-package="soundsystem" />
    </beans>
    ```
* 创建一个简单的JUnit测试，它会创建Spring上下文，并判断`CompactDisc`是不是真的创建出来了。
    ```java
    package soundsystem;
    import static org.junit.Assert.*;
    import org.junit.Rule;
    import org.junit.Test;
    import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
    import org.junit.runner.RunWith;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.test.context.ContextConfiguration;
    import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(classes=CDPlayerConfig.class)
    public class CDPlayerTest {
        @Autowired
        private CompactDisc cd;
        @Test
        public void cdShouldNotBeNull() { assertNotNull(cd); }
    }
    ```
    * `CDPlayerTest`使用了Spring的`SpringJUnit4ClassRunner`，以便在测试开始的时候自动创建Spring的应用上下文。
    * 注解`@ContextConfiguration`会告诉它需要在`CDPlayerConfig`中加载配置。
    * 因为`CDPlayerConfig`类中包含了`@ComponentScan`，因此最终的应用上下文中应该包含`CompactDiscbean`。
    * 为了证明这一点，在测试代码中有一个`CompactDisc`类型的属性，并且这个属性带有`@Autowired`注解，以便于将`CompactDiscbean`注入到测试代码之中。
    * 最后，会有一个简单的测试方法断言`cd`属性不为null。如果它不为null的话，就意味着Spring能够发现`CompactDisc`类，自动在Spring上下文中将其创建为bean并将其注入到测试代码之中。

##### 为组件扫描的bean命名
* Spring应用上下文中所有的bean都会给定一个ID。
    * 在前面的例子中，尽管我们没有明确地为`SgtPeppersbean`设置 ID，但Spring会根据类名为其指定一个ID。具体来讲，这个bean所给定的ID为`sgtPeppers`，也就是将类名的第一个字母变为小写。
* 如果想为这个bean设置不同的ID，你所要做的就是将期望的ID作为值传递给`@Component`注解。
    * 如果想将这个bean标识为`lonelyHeartsClub`，那么你需要将`SgtPeppers`类的`@Component`注解配置为如下
        ```java
        @Componet("lonelyHeartsClub")
        public class SgtPeppers implements CompactDisc {
            // ......
        }
        ```
* 另外一种为bean命名的方式是使用Java依赖注入规范中所提供的`@Named`注解来为bean设置ID
    ```java
    import javax.inject.Named;
    @Named("lonelyHeartsClub")
    public class SgtPeppers implements CompactDisc {
        // ......
    }
    ```
* `@Named`和`@Component`两者之间有一些细微的差异，但是在大多数场景中，它们是可以互相替换的。

##### 设置组件扫描的基础包
* `@ComponentScan`默认情况扫描配置类所在的基础包。如配置文件不在基础包，所需要做的就是在`@ComponentScan`的`value`属性中指明包的名称。
    ```java
    @Configuration
    @ComponentScan("soundsystem")
    public class CDPlayerConfig { }
    ```
* 如果想更加清晰地表明你所设置的是基础包，那么你可以通过`basePackages`属性进行配置。
    ```java
    @Configuration
    @ComponentScan(basePackages="soundsystem")
    public class CDPlayerConfig { }
    ```
* `basePackages`属性使用的是复数形式，可以设置多个基础包，只需要将`basePackages`属性设置为要扫描包的一个数组即可。
    ```java
    @ComponentScan(basePackages={"soundsystem", "video"})
    ```
* 以String类型设置的基础包是可以的，但这种方法是类型不安全。`@ComponentScan`还提供了将其指定为包中所包含的类或接口。
    ```java
    @ComponentScan(basePackageClasses={CDPlayer.class, DVDPlayer.class})
    ```
    * `basePackages`属性被替换成了`basePackageClasses`。
    * 不是再使用String类型的名称来指定包，为`basePackageClasses`属性所设置的数组中包含了类。这些类所在的包将会作为组件扫描的基础包。
    * 尽管在样例中，我为`basePackageClasses`设置的是组件类，但是你可以考虑在包中创建一个用来进行扫描的空标记接口。通过标记接口的方式，你依然能够保持对重构友好的接口引用，但是可以避免引用任何实际的应用程序代码(在稍后重构中，这些应用代码有可能会从想要扫描的包中移除掉)。
* 很多对象会依赖其他的对象才能完成任务，我们就需要有一种方法能够将组件扫描得到的bean和它们的依赖装配在一起，需要自动装配。

##### 通过为bean添加注解实现自动装配
* 自动装配就是让Spring自动满足bean依赖的一种方法，在满足依赖的过程中，会在Spring应用上下文中寻找匹配某个bean需求的其他bean。为了声明要进行自动装配，我们可以借助Spring的`@Autowired`注解。
    ```java
    package soundsystem;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;
    @Component
    public class CDPlayer implements MediaPlayer {
        private CompactDisc cd;
        @Autowired
        public CDPlayer(CompactDisc cd) { this.cd = cd; }
        public void play() { cd.play(); }
    }
    ```
    * 它的构造器上添加了`@Autowired`注解，这表明当Spring创建`CDPlayerbean`的时候，会通过这个构造器来进行实例化并且会传入一个可设置给`CompactDisc`类型的bean，将一个`CompactDisc`注入到`CDPlayer`之中。
* `@Autowired`注解还能用在属性的Setter方法上。
    ```java
    @Autowired
    public void setCompactDisc(CompactDisc cd){
        this.cd = cd;
    }
    ```
    * 在Spring初始化bean之后，它会尽可能得去满足bean的依赖。
* 如果没有匹配的bean，那么在应用上下文创建的时候，Spring会抛出一个异常。为了避免异常的出现，你可以将`@Autowired`的`required`属性设置为false。
    ```java
    @Autowired(required=false)
    public CDPlayer(CompactDisc cd) {
        this.cd = cd;
    }
    ```
    * 将`required`属性设置为false时，Spring会尝试执行自动装配，但是如果没有匹配的bean的话，Spring将会让这个bean处于未装配的状态。如果在你的代码中没有进行null检查的话，这个处于未装配状态的属性有可能会出现`NullPointerException`。
* 如果有多个bean都能满足依赖关系的话，Spring将会抛出一个异常，表明没有明确指定要选择哪个bean进行自动装配。
* `@Autowired`是Spring特有的注解，可以考虑将其替换为`@Inject`。`@Inject`注解来源于Java依赖注入规范，和`@Autowired`之间有着一些细微的差别，但是在大多数场景下，它们都是可以互相替换的。

##### 验证自动装配
* 验证Spring是否将把一个可分配给`CompactDisc`类型的bean自动注入进来。
    ```java
    package soundsystem;
    import static org.junit.Assert.*;
    import org.junit.Rule;
    import org.junit.Test;
    import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
    import org.junit.runner.RunWith;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.test.context.ContextConfiguration;
    import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(classes=CDPlayerConfig.class)
    public class CDPlayerTest {
        @Rule
        public final StandardOutputStreamLog log = new StandardOutputStreamLog();
        @Autowired
        private MediaPlayer player;
        @Autowired
        private CompactDisc cd;
        @Test
        public void cdShouldNotBeNull() { assertNotNull(cd); }
        @Test
        public void play() {
            player.play();
            assertEquals("Playing Sgt. Pepper's Lonely Hearts Club Band by The Beatles\n", log.getLog());
        }
    }
    ```

#### 通过Java代码装配bean
* 在进行显式配置的时候，有两种可选方案: Java和XML。
* 在进行显式配置时，JavaConfig是更好的方案， 因为它更为强大、类型安全并且对重构友好。
* JavaConfig是配置代码，这意味着它不应该包含任何业务逻辑，JavaConfig也不应该侵入到业务逻辑代码之中。

##### 创建配置类
* 创建JavaConfig类的关键在于为其添加`@Configuration`注解，`@Configuration`注解表明这个类是一个配置类，该类应该包含在Spring应用上下文中如何创建bean的细节。
    ```java
    package soundsystem;
    import org.spingframework.context.annotation.Configuration;
    @Configuration
    public class CDPlayerConfig {}
    ```
* 我们都是依赖组件扫描来发现Spring应该创建的bean，移除了`@ComponentScan`注解，此时的`CDPlayerConfig`类就没有任何作用了。

##### 声明简单的bean
* 要在JavaConfig中声明bean，我们需要编写一个方法，这个方法会创建所需类型的实例，然后给这个方法添加`@Bean`注解。
    ```java
    @Bean
    public CompactDisc sgtPeppers() {
        return new SgtPeppers();
    }
    ```
* `@Bean`注解会告诉Spring这个方法将会返回一个对象，该对象要注册为Spring应用上下文中的bean。方法体中包含了最终产生bean实例的逻辑。
* 默认情况下，bean的ID与带有`@Bean`注解的方法名是一样的。在上面的code中，bean的名字将会是`sgtPeppers`。如果想为其设置成一个不同的名字的话，那么可以重命名该方法，也可以通过`name`属性指定一个不同的名字。
    ```java
    @Bean(name="lonelyHeartsClubBand")
    public CompactDisc sgtPeppers() {
        return new SgtPeppers();
    }
    ```

##### 借助JavaConfig实现注入
* 在JavaConfig中装配bean的最简单方式就是引用创建bean的方法。
    ```java
    @Bean
    public CDPlayer cdPlayer() {
        return new CDPlayer(sgtPeppers());
    }
    ```
* `cdPlayer()`方法像`sgtPeppers()`方法一样，同样使用了`@Bean`注解，这表明这个方法会创建一个bean实例并将其注册到Spring应用上下文中。所创建的bean ID为cdPlayer，与方法的名字相同。
* `cdPlayer()`的方法体与`sgtPeppers()`稍微有些区别。在这里并没有使用默认的构造器构建实例，而是调用了需要传入`CompactDisc`对象的构造器来创建`CDPlayer`实例。
* `CompactDisc`是通过调用`sgtPeppers()`得到的，但情况并非完全如此。因为`sgtPeppers()`方法上添加了`@Bean`注解，Spring 将会拦截所有对它的调用，并确保直接返回该方法所创建的bean，而不是每次都对其进行实际的调用。
    ```java
    @Bean
    public CDPlayer cdPlayer() {
    return new CDPlayer(sgtPeppers());
    }

    @Bean
    public CDPlayer anotherCDPlayer() {
    return new CDPlayer(sgtPeppers());
    }
    ```
    * 假如对`sgtPeppers()`的调用就像其他的Java方法调用一样的话，那么每个`CDPlayer`实例都会有一个自己特有的`SgtPeppers`实例。如果我们讨论的是实际的CD播放器和CD光盘的话，这么做是有意义的。如果你有两台CD播放器，在物理上并没有办法将同一张CD光盘放到两个CD播放器中。
* 在软件领域中，我们完全可以将同一个`SgtPeppers`实例注入到任意数量的其他bean之中。默认情况下，Spring中的bean都是单例的，我们并没有必要为第二个`CDPlayer`bean创建完全相同的`SgtPeppers`实例。所以，Spring会拦截对`sgtPeppers()`的调用并确保返回的是Spring所创建的bean，也就是Spring本身在调用`sgtPeppers()`时所创建的`CompactDiscbean`。因此，两个`CDPlayer`bean会得到相同的`SgtPeppers`实例。
* 一种理解起来更为简单的方式
    ```java
    @Bean
    public CDPlayer cdPlayer(CompactDisc compactDisc) {
        return new CDPlayer(compactDisc);
    }
    ```
    * `cdPlayer()`方法请求一个`CompactDisc`作为参数。当Spring调用`cdPlayer()`创建`CDPlayerbean`的时候，它会自动装配一个`CompactDisc`到配置方法之中。然后，方法体就可以按照合适的方式来使用它。借助这种技术，`cdPlayer()`方法也能够将`CompactDisc`注入到`CDPlayer`的构造器中，而且不用明确引用`CompactDisc`的`@Bean`方法。
* 通过这种方式引用其他的bean通常是最佳的选择，因为它不会要求将`CompactDisc`声明到同一个配置类之中。在这里甚至没有要求`CompactDisc`必须要在JavaConfig中声明，实际上它可以通过组件扫描功能自动发现或者通过XML来进行配置。你可以将配置分散到多个配置类、XML文件以及自动扫描和装配bean之中，只要功能完整健全即可。不管`CompactDisc`是采用什么方式创建出来的，Spring都会将其传入到配置方法中，并用来创建`CDPlayer`bean。
* 通过Setter方法注入`CompactDisc`
    ```java
    @Bean
    public CDPlayer cdPlayer(CompactDisc compactDisc) {
        CDPlayer cdPlayer = new CDPlayer(compactDisc);
        cdPlayer.setCompactDisc(compactDisc);
        return cdPlayer;
    }
    ```

#### 通过XML装配bean
##### 创建XML配置规范
* 在XML配置中，这意味着要创建一个XML文件，并且要以元素为根。
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans 
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/context" >
    
    <!-- configuration details go here />
    </beans>
    ```
* 用来装配bean的最基本的XML元素包含在spring-beans模式之中，在上面这个XML文件中，它被定义为根命名空间。是该模式中的一个元素，它是所有Spring配置文件的根元素。

##### 声明一个简单的<bean>
* 要在基于XML的Spring配置中声明一个bean，我们要使用springbeans模式中的另外一个元素: `<bean>`。`<bean>`元素类似于`JavaConfig`中的`@Bean`注解。
    ```xml
    <bean class="soundsystem.SgtPeppers" />
    ```
    * 因为没有明确给定ID，所以这个bean将会根据全限定类名来进行命名。
    * 在本例中，bean的ID将会是`soundsystem.SgtPeppers#0`，其中`#0`是一个计数的形式，用来区分相同类型的其他bean。
* 通过`id`属性为每个bean设置一个你自己选择的名字
    ```xml
    <bean id="compactDisc" class="soundsystem.SgtPeppers" />
    ```
* bean声明的一些特征
    * 第一件需要注意的事情就是你不再需要直接负责创建`SgtPeppers`的实例，XML配置将会调用`SgtPeppers`的默认构造器来创建bean。
    * bean的类型以字符串的形式设置在了`class`属性中，Spring的XML配置并不能从编译期的类型检查中受益。

##### 借助构造器注入初始化bean
* 具体到构造器注入，有两种基本的配置方案可供选择:
    * `<constructor-arg>`元素
    * `c-`命名空间

###### 构造器注入bean引用
* 例子:
    * 已知:
        * `CDPlayer`bean有一个接受`CompactDisc`类型的构造器
        * 已经声明了`SgtPeppers`bean，并且`SgtPeppers`类实现了`CompactDisc`接口
    * 在XML中声明`CDPlayer`并通过ID引用`SgtPeppers`
        ```xml
        <bean id="cdPlayer" class="soundsystem.CDPlayer">
            <constructor-arg ref="compactDisc">
        </bean>
        ```
        * 当Spring遇到这个`<bean>`元素时，它会创建一个`CDPlayer`实例。
        * `<constructor-arg>`元素会告知Spring要将一个ID为`compactDisc`的bean引用传递到`CDPlayer`的构造器中。
    * 也可以使用Spring的`c-`命名空间。
        ```xml
        <bean id="cdPlayer" class="soundsystem.CDPlayer" c:cd-ref="compactDisc" />
        ```
        ```
        c:cd-ref="compactDisc"  
        c-命名空间前缀:构造器参数名-注入bean引用=要注入的bean的ID
        ```
    * `c-`命名空间使用参数在整个参数列表中的位置信息。
        ```xml
        <bean id="cdPlayer" class="soundsystem.CDPlayer" c:_0-ref="compactDisc" />
        ```

###### 将字面量注入到构造器中
* CompactDisc的一个新实现
    ```java
    package soundsystem;
    import java.util.List;
    public class BlankDisc implements CompactDisc {
        private String title;
        private String artist;
        public BlankDisc(String title, String artist) {
            this.title = title;
            this.artist = artist;
        }
        public void play() { System.out.println("Playing " + title + " by " + artist); }
    }
    ```
    ```xml
    <bean id="compactDisc" class="soundsystem.BlankDisc">
        <constructor-arg value="Sgt. Pepper's Lonely Hearts Club Band" />
        <constructor-arg value="The Beatles" />
    </bean>
    ```
    * 没有使用`ref`属性来引用其他的bean，而是使用了`value`属性，通过该属性表明给定的值要以字面量的形式注入到构造器之中。
    * 使用`c-`命名空间
        * 引用构造器参数的名字
            ```xml
            <bean id="compactDisc" class="soundsystem.BlankDisc" c:_title="Sgt. Pepper's Lonely Hearts Club Band" c:_artist="The Beatles" />
            ```
            * 装配字面量与装配引用的区别在于属性名中去掉了`-ref`后缀。
        * 通过参数索引装配相同的字面量值
            ```xml
            <bean id="compactDisc" class="soundsystem.BlankDisc" c:_0="Sgt. Pepper's Lonely Hearts Club Band" c:_1="The Beatles" />
            ```

###### 装配集合
* 新的`BlankDisc`
    ```java
    package soundsystem;
    import java.util.List;
    public class BlankDisc implements CompactDisc {
        private String title;
        private String artist;
        private List<String> tracks;
        public BlankDisc(String title, String artist, List<String> tracks) {
            this.title = title;
            this.artist = artist;
            this.tracks = tracks;
        }
        public void play() {
            System.out.println("Playing " + title + " by " + artist);
            for (String track : tracks) {
                System.out.println("-Track: " + track);
            }
        }
    }
    ```
    * 在声明bean的时候，我们必须要提供一个磁道列表。
    * 最简单的办法是将列表设置为null。
        ```xml
        <bean id="compactDisc" class="soundsystem.BlankDisc">
            <constructor-arg value="Sgt. Pepper's Lonely Hearts Club Band" />
            <constructor-arg value="The Beatles" />
            <constructor-arg><null/></constructor-arg>
        </bean>
        ```
        * `<null/>`元素所做的事情与你的期望是一样的: 将null传递给构造器。
    * 使用元素将其声明为一个列表
        ```xml
        <?xml version="1.0" encoding="UTF-8"?>
        <beans xmlns="http://www.springframework.org/schema/beans"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xmlns:c="http://www.springframework.org/schema/c"
               xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
            <bean id="compactDisc" 
                  class="soundsystem.BlankDisc" 
                  c:_0="Sgt. Pepper's Lonely Hearts Club Band" 
                  c:_1="The Beatles">
                <constructor-arg>
                    <list>
                        <value>Sgt. Pepper's Lonely Hearts Club Band</value>
                        <value>With a Little Help from My Friends</value>
                        <value>Lucy in the Sky with Diamonds</value>
                        <value>Getting Better</value>
                        <value>Fixing a Hole</value>
                        <!-- ...other tracks omitted for brevity... -->
                    </list>
                </constructor-arg>
            </bean>
        </beans>
        ```
* 也可以使用`<ref>`元素替代`<value>`
    * 有一个`Discography`类
        * 它的构造器如
            ```java
            public Discography(String artist, List<CompactDisc> cds) { ... }
            ```
        * 配置`Discography`bean
            ```xml
            <bean id="beatlesDiscography" class="soundsystem.Discography" >
                <constructor-arg>
                    <list>
                        <ref bean="sgtPeppers" />
                        <ref bean="whiteAlbum" />
                        <ref bean="hardDaysNight" />
                        <ref bean="revolver" />
                    </list>
                </constructor-arg>
            </bean>
            ```
* 可以按照同样的方式使用`<set>`元素
* 在装配集合方面，`<constructor-arg>`比`c-`命名空间的属性更有优势。目前，使用`c-`命名空间的属性无法实现装配集合的功能。

##### 设置属性
* 对强依赖使用构造器注入，而对可选性的依赖使用属性注入。
* 使用Spring XML实现属性注入
    ```java
    package soundsystem;
    import org.springframework.beans.factory.annotation.Autowired;
    public class CDPlayer implements MediaPlayer {
        private CompactDisc cd;
        @Autowired
        public CDPlayer(CompactDisc cd) { this.cd = cd; }
        public void play() { cd.play(); }
    }
    ```
    * CDPlayer没有任何含参构造器，它也没有任何的强依赖。如果声明bean，会导致`NullPointerException`。
    * 用属性注入
        ```xml
        <bean id="cdPlayer" class="soundsystem.CDPlayer" >
            <property name="compactDisc" ref="compactDisc" />
        </bean>
        ```
* Spring提供了更加简洁的`p-`命名空间，作为`<property>`元素的替代方案。
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:p="http://www.springframework.org/schema/p"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
                               http://www.springframework.org/schema/beans/spring-beans.xsd">
        <bean id="cdPlayer" class="soundsystem.CDPlayer" p:compactDisc-ref="compactDisc" />
    </beans>
    ```
    * 属性的名字使用了`p:`前缀，表明我们所设置的是一个属性。接下来就是要注入的属性名。最后，属性的名称以`-ref`结尾，这会提示Spring要进行装配的是引用，而不是字面量。

###### 将字面量注入到属性中
* 属性也可以注入字面量，这与构造器参数非常类似.
    ```java
    package soundsystem;
    import java.util.List;
    public class BlankDisc implements CompactDisc {
        private String title;
        private String artist;
        private List<String> tracks;
        public BlankDisc(String title, String artist, List<String> tracks) {
            this.title = title;
            this.artist = artist;
            this.tracks = tracks;
        }
        public void play() {
            System.out.println("Playing " + title + " by " + artist);
            for (String track : tracks) {
                System.out.println("-Track: " + track);
            }
        }
    }
    ```
    ```xml
    <bean id="compactDisc" class="soundsystem.BlankDisc">
        <property name="title" value="Sgt. Pepper's Lonely Hearts Club Band" />
        <property name="artist" value="The Beatles">
        <property name="tracks">
            <list>
                <value>Sgt. Pepper's Lonely Hearts Club Band</value>
                <value>With a Little Help from My Friends</value>
                <value>Lucy in the Sky with Diamonds</value>
                <value>Getting Better</value>
                <value>Fixing a Hole</value>
            <!-- ...other tracks omitted for brevity... -->
            </list>
        </property>
    </bean>
    ```
* 不能使用`p-`命名空间来装配集合,可以使用`util-`命名空间辅助实现
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:p="http://www.springframework.org/schema/p"
           xmlns:util="http://www.springframework.org/schema/util"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
                               http://www.springframework.org/schema/beans/spring-beans.xsd
                               http://www.springframework.org/schema/util
                               http://www.springframework.org/schema/util/spring-util.xsd">

        <bean id="compactDisc" class="soundsystem.BlankDisc"
              p:title="Sgt. Pepper's Lonely Hearts Club Band"
              p:artist="The Beatles"
              p:tracks-ref="trackList" />

        <util:list id="trackList">
            <value>Sgt. Pepper's Lonely Hearts Club Band</value>
            <value>With a Little Help from My Friends</value>
            <value>Lucy in the Sky with Diamonds</value>
            <value>Getting Better</value>
            <value>Fixing a Hole</value>
        </util:list>
    </beans>
    ```

### 高级装配
#### 环境与profile
* 在开发软件的时候，有一个很大的挑战就是将应用程序从一个环境迁移到另外一个环境。开发阶段中，某些环境相关做法可能并不适合迁移到生产环境中，甚至即便迁移过去也无法正常工作。数据库配置、加密算法以及与外部系统的集成是跨环境部署时会发生变化的几个典型例子。
* 比如数据库配置
    * 在开发环境中，我们可能会使用嵌入式数据库，并预先加载测试数据
    * JNDI管理的DataSource更加适合于生产环境
    * QA环境中，你可以选择完全不同的DataSource配置，可以配置为Commons DBCP连接池

##### 配置profile bean
* Spring为环境相关的bean所提供的解决方案其实与构建时的方案没有太大的差别。在这个过程中需要根据环境决定该创建哪个bean和不创建哪个bean。不过Spring并不是在构建的时候做出这样的决策，而是等到运行时再来确定。这样的结果就是同一个部署单元(可能会是 WAR 文件)能够适用于所有的环境，没有必要进行重新构建。
* Spring引入了bean profile的功能。要使用profile，你首先要将所有不同的bean定义整理到一个或多个profile之中，在将应用部署到每个环境时，要确保对应的profile处于激活的状态。
* 在Java配置中，可以使用`@Profile`注解指定某个bean属于哪一个profile。
* 在配置类中，嵌入式数据库的DataSource可能会配置成如下
    ```java
    package com.myapp;
    import javax.sql.DataSource;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;
    import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
    import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
    @Configuration
    @Profile("dev")
    public class DataSourceConfig {
        @Bean(destroyMethod = "shutdown")
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:test-data.sql")
                .build();
        }
    }
    ```
    * `@Profile`告诉Spring这个配置类中的bean只有在dev profile激活时才会创建
* 还需要有一个适用于生产环境的配置
    ```java
    package com.myapp;
    import javax.sql.DataSource;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.jndi.JndiObjectFactoryBean;
    @Configuration
    @Profile("prod")
    public class DataSourceConfig {
        @Bean
        public DataSource dataSource() {
            JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
            jndiObjectFactoryBean.setJndiName("jdbc/myDS");
            jndiObjectFactoryBean.setResourceRef(true);
            jndiObjectFactoryBean.setProxyInterface(javax.sql.DataSource.class);
            return (DataSource) jndiObjectFactoryBean.getObject();
        }
    }
    ```
* 也可以在方法级别上使用`@Profile`注解，与`@Bean`注解一同使用
    ```java
    @Configuration
    public class DataSourceConfig {
        @Bean(destroyMethod = "shutdown")
        @Profile("dev")
        public DataSource embeddedDataSource() {
            return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:test-data.sql")
                .build();
        }

        @Bean
        @Profile("prod")
        public DataSource jndiDataSource() {
            JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
            jndiObjectFactoryBean.setJndiName("jdbc/myDS");
            jndiObjectFactoryBean.setResourceRef(true);
            jndiObjectFactoryBean.setProxyInterface(javax.sql.DataSource.class);
            return (DataSource) jndiObjectFactoryBean.getObject();
        }
    }
    ```
* 没有指定profile的bean始终都会被创建，与激活哪个profile没有关系。

###### 在XML中配置profile
* 我们也可以通过元素的profile属性，在XML中配置profile bean。
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
           xmlns:jdbc="http://www.springframework.org/schema/jdbc"
           xmlns:jee="http://www.springframework.org/schema/jee" 
           xmlns:p="http://www.springframework.org/schema/p"
           xsi:schemaLocation=" http://www.springframework.org/schema/jee
                                http://www.springframework.org/schema/jee/spring-jee.xsd
                                http://www.springframework.org/schema/jdbc
                                http://www.springframework.org/schema/jdbc/spring-jdbc.xsd
                                http://www.springframework.org/schema/beans
                                http://www.springframework.org/schema/beans/spring-beans.xsd">

        <beans profile="dev">
            <jdbc:embedded-database id="dataSource" type="H2">
                <jdbc:script location="classpath:schema.sql" />
                <jdbc:script location="classpath:test-data.sql" />
            </jdbc:embedded-database>
        </beans>
        
        <beans profile="prod">
            <jee:jndi-lookup id="dataSource"
                             lazy-init="true"
                             jndi-name="jdbc/myDatabase"
                             resource-ref="true"
                             proxy-interface="javax.sql.DataSource" />
        </beans>
    </beans>
    ```

##### 激活profile
* Spring在确定哪个profile处于激活状态时，需要依赖两个独立的属性: `spring.profiles.active`和`spring.profiles.default`。如果设置了`spring.profiles.active`属性的话，那么它的值就会用来确定哪个profile是激活的。但如果没有设置`spring.profiles.active`属性的话，那Spring将会查找`spring.profiles.default`的值。如果`spring.profiles.active`和`pring.profiles.default`均没有设置的话，那就没有激活的profile，因此只会创建那些没有定义在profile中的bean。
* 有多种方式来设置这两个属性: 
    * 作为DispatcherServlet的初始化参数；
    * 作为Web应用的上下文参数；
    * 作为JNDI条目；
    * 作为环境变量；
    * 作为JVM的系统属性；
    * 在集成测试类上，使用`@ActiveProfiles`注解设置。
* 例如，在Web应用中，设置`spring.profiles.default`的`web.xml`文件会如下所示: 
    ```xml
    <context-param>
        <param-name>spring.profiles.default</param-name>
        <param-name>dev</param-name>
    </context-param>
    ```
* 按照这种方式设置`spring.profiles.default`，所有的开发人员都能从版本控制软件中获得应用程序源码，并使用开发环境的设置运行代码，而不需要任何额外的配置。
* 当应用程序部署到QA、生产或其他环境之中时，负责部署的人根据情况使用系统属性、环境变量或JNDI设置`spring.profiles.active`即可。当设置`spring.profiles.active`以后，至于`spring.profiles.default`置成什么值就已经无所谓了；系统会优先使用`spring.profiles.active`中所设置的profile。
* profile使用的都是复数形式。这意味着你可以同时激活多个profile，这可以通过列出多个profile名称，并以逗号分隔来实现。

###### 使用profile进行测试
* Spring提供了`@ActiveProfiles`注解，我们可以使用它来指定运行测试时要激活哪个profile
    ```java
    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(classes={PersistenceTestConfig.class})
    @ActiveProfile("dev")
    public class PersistenceTest {}
    ```

#### 条件化的bean
* `@Conditional`注解，它可以用到带有`@Bean`注解的方法上，如果给定的条件计算结果为true，就会创建这个bean，否则的话，这个bean会被忽略。
* 例如: 假设有一个名为`MagicBean`的类，我们希望只有设置了`magic`环境属性的时候，Spring才会实例化这个类。
    ```java
    @Bean
    @Conditioal(MagicExistsCondition.class)
    public MagicBean magicBean() {
        return new MagicBean();
    }

    public interface Condition {
        boolean matches(ConditionContext ctxt, AnnotatedTypeMetadata metadata);
    }

    package com.habuma.restfun;
    import org.springframework.context.annotation.Condition;
    import org.springframework.context.annotation.ConditionContext;
    import org.springframework.core.env.Environment;
    import org.springframework.core.type.AnnotatedTypeMetadata;
    public class MagicExistsCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Environment env = context.getEnvironment();
            return env.containsProperty("magic");
        }
    }
    ```