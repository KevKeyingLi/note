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

---

## Spring的核心
### Spring之旅
#### 简化Java开发
* 为了降低Java开发的复杂性，Spring采取了以下4种关键策略：
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
    * `AnnotationConfigApplicationContext`：从一个或多个基于Java的配置类中加载Spring应用上下文。
    * `AnnotationConfigWebApplicationContext`：从一个或多个基于Java的配置类中加载Spring Web应用上下文。
    * `ClassPathXmlApplicationContext`：从类路径下的一个或多个XML配置文件中加载上下文定义，把应用上下文的定义文件作为类资源。
    * `FileSystemXmlapplicationcontext`：从文件系统下的一个或多个XML配置文件中加载上下文定义。
    * `XmlWebApplicationContext`：从Web应用下的一个或多个XML配置文件中加载上下文定义。
* 无论是从文件系统中装载应用上下文还是从类路径下装载应用上下文，将bean加载到bean工厂的过程都是相似的。
    ```java
    ApplicationContext context = new FileSystemXmlApplicationContext("c:/knight.xml");
    ApplicationContext context = new ClassPathXmlApplicationContext("knight.xml");
    ```
    * 使用`FileSystemXmlApplicationContext`和使用`ClassPathXmlApplicationContext`的区别在于：       
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