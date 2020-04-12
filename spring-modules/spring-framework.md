- [The IoC Container](#The_IoC_Container)
    * [Spring IoC Container and Beans](#Spring_IoC_Container_and_Beans)
    * [Container Overview](#Container_Overview)
        * [Configuration Metadata](#Configuration_Metadata)
        * [Instantiating a Container](#Instantiating_a_Container)
        * [Using the Container](#Using_the_Container)
    * [Bean Overview](#Bean_Overview)
        * [Naming Beans](#Naming_Beans)
        * [Instantiating Beans](#Instantiating_Beans)
    * [Dependencies](#Dependencies)
        * [Dependency Injection](#Dependency_Injection)
        * [Dependencies and Configuration in Detail](#Dependencies_and_Configuration_in_Detail)
        * [Using depends-on](#Using_depends_on)
        * [Lazy-initialized Beans](#Lazy_initialized_Beans)
---
* > https://docs.spring.io/spring/docs/5.2.5.RELEASE/spring-framework-reference/
---

## The IoC Container
### Spring IoC Container and Beans
*  IoC is also known as dependency injection (DI). 
    * It is a process whereby objects define their dependencies only through constructor arguments, arguments to a factory method, or properties that are set on the object instance after it is constructed or returned from a factory method. The container then injects those dependencies when it creates the bean. 
    * This process is fundamentally the inverse of the bean itself controlling the instantiation or location of its dependencies by using direct construction of classes or a mechanism such as the Service Locator pattern.
* The `org.springframework.beans` and `org.springframework.context` packages are the basis for Spring Framework’s IoC container. 
* The `BeanFactory` interface provides an advanced configuration mechanism capable of managing any type of object. 
* `ApplicationContext` is a sub-interface of `BeanFactory`. It adds:
    * Easier integration with Spring’s AOP features
    * Message resource handling
    * Event publication
    * Application-layer specific contexts such as the WebApplicationContext for use in web applications
* The BeanFactory provides the configuration framework and basic functionality, and the ApplicationContext adds more enterprise-specific functionality. 
* A bean is an object that is instantiated, assembled, and otherwise managed by a Spring IoC container. Beans, and the dependencies among them, are reflected in the configuration metadata used by a container.


### Container Overview
* The `org.springframework.context.ApplicationContext` interface represents the Spring IoC container and is responsible for instantiating, configuring, and assembling the beans.
* The container gets its instructions on what objects to instantiate, configure, and assemble by reading configuration metadata which is represented in XML, Java annotations, or Java code。
* Instance of `ClassPathXmlApplicationContext` or `FileSystemXmlApplicationContext` are commonly created for `ApplicationContext` interface.
* In most application scenarios, explicit user code is not required to instantiate one or more instances of a Spring IoC container.
* High-level view of how Spring works:
    ```
                              Business Objects (POJOs)
                                          |
                                          V
                              +----------------------+
    Configuration Metadata -> | The Spring Container |
                              +----------------------+
                                          | produces
                                          V
                     +--------------------------------------+
                     | Fully configured system ready for use|
                     +--------------------------------------+
    ```
    * Your application classes are combined with configuration metadata so that, after the `ApplicationContext` is created and initialized, you have a fully configured and executable system or application.


#### Configuration Metadata
* This configuration metadata tells the Spring container how to instantiate, configure, and assemble the objects in your application.
* Three configurations:
    * XML-based configuration
    * Annotation-based configuration
    * Java-based configuration
* Spring configuration consists of at least one and typically more than one bean definition that the container must manage. XML-based configuration metadata configures these beans as `<bean/>` elements inside a top-level `<beans/>` element.
* These bean definitions correspond to the actual objects that make up your application.
    * Typically, you define service layer objects, data access objects (DAOs), presentation objects such as Struts Action instances, infrastructure objects such as Hibernate SessionFactories, JMS Queues, and so forth.
    * Typically, one does not configure fine-grained domain objects in the container, because it is usually the responsibility of DAOs and business logic to create and load domain objects.
    * However, you can use Spring’s integration with AspectJ to configure objects that have been created outside the control of an IoC container.
* XML-based configuration metadata Example:
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            https://www.springframework.org/schema/beans/spring-beans.xsd">

        <bean id="..." class="...">  
            <!-- collaborators and configuration for this bean go here -->
        </bean>
        <!-- more bean definitions go here -->
    </beans>
    ```
    * The `id` attribute is a string that identifies the individual bean definition.
    * The `class` attribute defines the type of the bean and uses the fully qualified classname.


#### Instantiating a Container
* The location path or paths supplied to an `ApplicationContext` constructor are resource strings that let the container load configuration metadata from a variety of external resources, such as the local file system, the Java CLASSPATH, and so on.
* Example:
    ```java
    ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");
    ```
    * The following example shows the service layer objects (services.xml) configuration file:
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            https://www.springframework.org/schema/beans/spring-beans.xsd">

        <!-- services -->
        <bean id="petStore" class="org.springframework.samples.jpetstore.services.PetStoreServiceImpl">
            <property name="accountDao" ref="accountDao"/>
            <property name="itemDao" ref="itemDao"/>
        </bean>
    </beans>
    ```
    * The following example shows the data access objects daos.xml file:
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            https://www.springframework.org/schema/beans/spring-beans.xsd">

        <bean id="accountDao"class="org.springframework.samples.jpetstore.dao.jpa.JpaAccountDao"></bean>
        <bean id="itemDao" class="org.springframework.samples.jpetstore.dao.jpa.JpaItemDao"></bean>
    </beans>
    ```
    * In the preceding example, the service layer consists of the `PetStoreServiceImpl` class and two data access objects of the types `JpaAccountDao` and `JpaItemDao` (based on the JPA Object-Relational Mapping standard). 
    * The `property name` element refers to the name of the JavaBean property, and the `ref` element refers to the name of another bean definition.
    * This linkage between `id` and `ref` elements expresses the dependency between collaborating objects.


##### Composing XML-based Configuration Metadata
* It can be useful to have bean definitions span multiple XML files. Often, each individual XML configuration file represents a logical layer or module in your architecture.
* You can use the application context constructor to load bean definitions from all these XML fragments. 
    * This constructor takes multiple Resource locations.
    * Alternatively, use one or more occurrences of the `<import/>` element to load bean definitions from another file or files.
    * Example: 
        ```xml
        <beans>
            <import resource="services.xml"/>
            <import resource="resources/messageSource.xml"/>
            <import resource="/resources/themeSource.xml"/>
            <bean id="bean1" class="..."/>
            <bean id="bean2" class="..."/>
        </beans>
        ```


#### Using the Container
* The `ApplicationContext` is the interface for an advanced factory capable of maintaining a registry of different beans and their dependencies. By using the method `T getBean(String name, Class<T> requiredType)`, you can retrieve instances of your beans.
* The `ApplicationContext` lets you read bean definitions and access them
    ```java
    ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");
    PetStoreService service = context.getBean("petStore", PetStoreService.class);
    List<String> userList = service.getUsernameList();
    ```
* Use getBean to retrieve instances of beans.


### Bean Overview
* A Spring IoC container manages one or more beans. These beans are created with the configuration metadata that you supply to the container. Within the container itself, these bean definitions are represented as BeanDefinition objects.
* A set of properties that make up each bean definition:
    * Class
    * Name
    * Scope
    * Constructor arguments
    * Properties
    * Autowiring mode
    * Lazy initialization mode
    * Initialization method
    * Destruction method


#### Naming Beans
* Every bean has one or more identifiers. These identifiers must be unique within the container that hosts the bean. If it requires more than one, the extra ones can be considered aliases.
* In XML-based configuration metadata, you use the id attribute, the name attribute, or both to specify the bean identifiers. The id attribute lets you specify exactly one id. 
* Conventionally, these names are alphanumeric, but they can contain special characters as well. If you want to introduce other aliases for the bean, you can also specify them in the name attribute, separated by a comma (,), semicolon (;), or white space.
* You are not required to supply a name or an id for a bean. If you do not supply a name or id explicitly, the container generates a unique name for that bean. However, if you want to refer to that bean by name, through the use of the ref element or a Service Locator style lookup, you must provide a name.


##### Aliasing a Bean outside the Bean Definition
* In a bean definition itself, you can supply more than one name for the bean, by using a combination of up to one name specified by the `id` attribute and any number of other names in the `name` attribute.
* It is sometimes desirable to introduce an alias for a bean that is defined elsewhere. This is commonly the case in large systems where configuration is split amongst each subsystem, with each subsystem having its own set of object definitions. 
* In XML-based configuration metadata, you can use the `<alias/>` element to accomplish this. 
* The following example shows how to do so:
    ```xml
    <alias name="fromName" alias="toName"/>
    ```
    * a bean (in the same container) named `fromName` may also, after the use of this alias definition, be referred to as `toName`.


#### Instantiating Beans
* A bean definition is essentially a recipe for creating one or more objects. The container looks at the recipe for a named bean when asked and uses the configuration metadata encapsulated by that bean definition to create (or acquire) an actual object.
* If you use XML-based configuration metadata, you specify the type (or class) of object that is to be instantiated in the `class` attribute of the `<bean/>` element. 
* We can use the Class property in one of two ways:
    * Typically, to specify the bean class to be constructed in the case where the container itself directly creates the bean by calling its constructor reflectively, somewhat equivalent to Java code with the `new` operator.
    * To specify the actual class containing the `static` factory method that is invoked to create the object, in the less common case where the container invokes a `static` factory method on a class to create the bean. The object type returned from the invocation of the `static` factory method may be the same class or another class entirely.

##### Instantiation with a Constructor
* When you create a bean by the constructor approach, all normal classes are usable by and compatible with Spring. 
* We can use default constructor or constructors with arguments.
* With XML-based configuration metadata you can specify your bean class as follows:
    ```xml
    <bean id="exampleBean" class="examples.ExampleBean"/>
    <bean name="anotherExample" class="examples.ExampleBeanTwo"/>
    ```


##### Instantiation with a Static Factory Method
* When defining a bean that you create with a static factory method, use the class attribute to specify the class that contains the static factory method and an attribute named factory-method to specify the name of the factory method itself.
* The following bean definition specifies that the bean be created by calling a factory method. The definition does not specify the type (class) of the returned object, only the class containing the factory method. In this example, the createInstance() method must be a static method. 
    ```xml
    <bean id="clientService" class="examples.ClientService" factory-method="createInstance"/>
    ```
    ```java
    public class ClientService {
        private static ClientService clientService = new ClientService();
        private ClientService() {}
        public static ClientService createInstance() { return clientService; }
    }
    ```


##### Instantiation by Using an Instance Factory Method
* To use this mechanism, leave the `class` attribute empty and, in the `factory-bean` attribute, specify the name of a bean in the current (or parent or ancestor) container that contains the instance method that is to be invoked to create the object. Set the name of the factory method itself with the `factory-method` attribute. 
    ```xml
    <bean id="serviceLocator" class="examples.DefaultServiceLocator"></bean>
    <bean id="clientService" factory-bean="serviceLocator" factory-method="createClientServiceInstance"/>
    ```
    ```java
    public class DefaultServiceLocator {
        private static ClientService clientService = new ClientServiceImpl();
        public ClientService createClientServiceInstance() { return clientService; }
    }
    ```
* One factory class can also hold more than one factory method:
    ```xml
    <bean id="serviceLocator" class="examples.DefaultServiceLocator"></bean>
    <bean id="clientService" factory-bean="serviceLocator" factory-method="createClientServiceInstance"/>
    <bean id="accountService" factory-bean="serviceLocator" factory-method="createAccountServiceInstance"/>
    ```
    ```java
    public class DefaultServiceLocator {
        private static ClientService clientService = new ClientServiceImpl();
        private static AccountService accountService = new AccountServiceImpl();
        public ClientService createClientServiceInstance() { return clientService; }
        public AccountService createAccountServiceInstance() { return accountService; }
    }
    ```


### Dependencies
* A typical enterprise application does not consist of a single object. Even the simplest application has a few objects that work together to present what the end-user sees as a coherent application.


#### Dependency Injection
* Dependency injection (DI) is a process whereby objects define their dependencies only through constructor arguments, arguments to a factory method, or properties that are set on the object instance after it is constructed or returned from a factory method. The container then injects those dependencies when it creates the bean. 
* Code is cleaner with the DI principle, and decoupling is more effective when objects are provided with their dependencies. The object does not look up its dependencies and does not know the location or class of the dependencies. As a result, your classes become easier to test, particularly when the dependencies are on interfaces or abstract base classes, which allow for stub or mock implementations to be used in unit tests.


##### Constructor-based Dependency Injection
* Constructor-based DI is accomplished by the container invoking a constructor with a number of arguments, each representing a dependency. Calling a `static` factory method with specific arguments to construct the bean is nearly equivalent, and this discussion treats arguments to a constructor and to a `static` factory method similarly.
    ```java
    public class SimpleMovieLister {
        private MovieFinder movieFinder;
        public SimpleMovieLister(MovieFinder movieFinder) { this.movieFinder = movieFinder; }
    }
    ```

##### Constructor Argument Resolution
* Constructor argument resolution matching occurs by using the argument’s type. If no potential ambiguity exists in the constructor arguments of a bean definition, the order in which the constructor arguments are defined in a bean definition is the order in which those arguments are supplied to the appropriate constructor when the bean is being instantiated.
    ```java
    package x.y;
    public class ThingOne {
        public ThingOne(ThingTwo thingTwo, ThingThree thingThree) { }
    }
    ```
    * Assuming that `ThingTwo` and `ThingThree` classes are not related by inheritance, no potential ambiguity exists. Thus, the following configuration works fine, and you do not need to specify the constructor argument indexes or types explicitly in the `<constructor-arg/>` element.
    ```xml
    <beans>
        <bean id="beanOne" class="x.y.ThingOne">
            <constructor-arg ref="beanTwo"/>
            <constructor-arg ref="beanThree"/>
        </bean>
        <bean id="beanTwo" class="x.y.ThingTwo"/>
        <bean id="beanThree" class="x.y.ThingThree"/>
    </beans>
    ```
* When another bean is referenced, the type is known, and matching can occur.When a simple type is used, Spring cannot determine the type of the value, and so cannot match by type without help.
    ```java
    package examples;
    public class ExampleBean {
        private int years;
        private String ultimateAnswer;
        public ExampleBean(int years, String ultimateAnswer) {
            this.years = years;
            this.ultimateAnswer = ultimateAnswer;
        }
    }
    ```

###### Constructor argument type matching
* In the preceding scenario, the container can use type matching with simple types if you explicitly specify the type of the constructor argument by using the `type` attribute.
    ```xml
    <bean id="exampleBean" class="examples.ExampleBean">
        <constructor-arg type="int" value="7500000"/>
        <constructor-arg type="java.lang.String" value="42"/>
    </bean>
    ```

###### Constructor argument index
* use the index attribute to specify explicitly the index of constructor arguments
    ```xml
    <bean id="exampleBean" class="examples.ExampleBean">
        <constructor-arg index="0" value="7500000"/>
        <constructor-arg index="1" value="42"/>
    </bean>
    ```

###### Constructor argument name
* use the constructor parameter name for value disambiguation
    ```xml
    <bean id="exampleBean" class="examples.ExampleBean">
        <constructor-arg name="years" value="7500000"/>
        <constructor-arg name="ultimateAnswer" value="42"/>
    </bean>
    ```

##### Setter-based Dependency Injection
* Setter-based DI is accomplished by the container calling setter methods on your beans after invoking a no-argument constructor or a no-argument static factory method to instantiate your bean.
    ```java
    public class SimpleMovieLister {
        private MovieFinder movieFinder;
        public void setMovieFinder(MovieFinder movieFinder) { this.movieFinder = movieFinder; }
    }
    ```

##### Constructor-based or setter-based DI
* The Spring team generally advocates constructor injection, as it lets you implement application components as immutable objects and ensures that required dependencies are not null. Furthermore, constructor-injected components are always returned to the client (calling) code in a fully initialized state. As a side note, a large number of constructor arguments is a bad code smell, implying that the class likely has too many responsibilities and should be refactored to better address proper separation of concerns.
* Setter injection should primarily only be used for optional dependencies that can be assigned reasonable default values within the class. Otherwise, not-null checks must be performed everywhere the code uses the dependency. One benefit of setter injection is that setter methods make objects of that class amenable to reconfiguration or re-injection later. 


##### Dependency Resolution Process
* The container performs bean dependency resolution as follows:
    * The `ApplicationContext` is created and initialized with configuration metadata that describes all the beans. Configuration metadata can be specified by XML, Java code, or annotations.
    * For each bean, its dependencies are expressed in the form of properties, constructor arguments, or arguments to the static-factory method. These dependencies are provided to the bean, when the bean is actually created.
    * Each property or constructor argument is an actual definition of the value to set, or a reference to another bean in the container.
    * Each property or constructor argument that is a value is converted from its specified format to the actual type of that property or constructor argument. By default, Spring can convert a value supplied in string format to all built-in types, such as int, long, String, boolean, and so forth.

##### Examples of Dependency Injection
* setter-based DI
    ```xml
    <bean id="exampleBean" class="examples.ExampleBean">
        <property name="beanOne">
            <ref bean="anotherExampleBean"/>
        </property>
        <property name="beanTwo" ref="yetAnotherBean"/>
        <property name="integerProperty" value="1"/>
    </bean>
    <bean id="anotherExampleBean" class="examples.AnotherBean"/>
    <bean id="yetAnotherBean" class="examples.YetAnotherBean"/>
    ```
    ```java
    public class ExampleBean {
        private AnotherBean beanOne;
        private YetAnotherBean beanTwo;
        private int i;
        public void setBeanOne(AnotherBean beanOne) { this.beanOne = beanOne; }
        public void setBeanTwo(YetAnotherBean beanTwo) { this.beanTwo = beanTwo; }
        public void setIntegerProperty(int i) { this.i = i; }
    }
    ```
* constructor-based DI
    * with constructor
        ```xml
        <bean id="exampleBean" class="examples.ExampleBean">
            <constructor-arg>
                <ref bean="anotherExampleBean"/>
            </constructor-arg>
            <constructor-arg ref="yetAnotherBean"/>
            <constructor-arg type="int" value="1"/>
        </bean>
        <bean id="anotherExampleBean" class="examples.AnotherBean"/>
        <bean id="yetAnotherBean" class="examples.YetAnotherBean"/>
        ```
        ```java
        public class ExampleBean {
            private AnotherBean beanOne;
            private YetAnotherBean beanTwo;
            private int i;
            public ExampleBean(
                AnotherBean anotherBean, YetAnotherBean yetAnotherBean, int i) {
                this.beanOne = anotherBean;
                this.beanTwo = yetAnotherBean;
                this.i = i;
            }
        }
        ```
    * with factory method
        ```xml
        <bean id="exampleBean" class="examples.ExampleBean" factory-method="createInstance">
            <constructor-arg ref="anotherExampleBean"/>
            <constructor-arg ref="yetAnotherBean"/>
            <constructor-arg value="1"/>
        </bean>
        <bean id="anotherExampleBean" class="examples.AnotherBean"/>
        <bean id="yetAnotherBean" class="examples.YetAnotherBean"/>
        ```
        ```java
        public class ExampleBean {
            private ExampleBean(...) { }
            public static ExampleBean createInstance (
                AnotherBean anotherBean, YetAnotherBean yetAnotherBean, int i) {
                ExampleBean eb = new ExampleBean (...);
                return eb;
            }
        }
        ```

#### Dependencies and Configuration in Detail
* Spring’s XML-based configuration metadata supports sub-element types within its `<property/>` and `<constructor-arg/>` elements for this purpose.

##### Straight Values (Primitives, Strings, and so on)
* The `value` attribute of the `<property/>` element specifies a property or constructor argument as a human-readable string representation. Spring’s conversion service is used to convert these values from a `String` to the actual type of the property or argument. 
    ```xml
    <bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/mydb"/>
        <property name="username" value="root"/>
        <property name="password" value="masterkaoli"/>
    </bean>
    ```
* uses the p-namespace for even more succinct XML configuration.
    ```xml
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:p="http://www.springframework.org/schema/p"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

        <bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource"
            destroy-method="close"
            p:driverClassName="com.mysql.jdbc.Driver"
            p:url="jdbc:mysql://localhost:3306/mydb"
            p:username="root"
            p:password="masterkaoli"/>

    </beans>
    ```
* You can also configure a `java.util.Properties` instance, as follows:
    ```xml
    <bean id="mappings" class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer">
        <property name="properties">
            <value>
                jdbc.driver.className=com.mysql.jdbc.Driver
                jdbc.url=jdbc:mysql://localhost:3306/mydb
            </value>
        </property>
    </bean>
    ```
    * The Spring container converts the text inside the `<value/>` element into a java.util.Properties instance by using the JavaBeans PropertyEditor mechanism. 

##### The idref element
* The `idref` element is simply an error-proof way to pass the id (a string value - not a reference) of another bean in the container to a `<constructor-arg/>` or `<property/>` element. 
    ```xml
    <bean id="theTargetBean" class="..."/>
    <bean id="theClientBean" class="...">
        <property name="targetName">
            <idref bean="theTargetBean"/>
        </property>
    </bean>
    ```
    * The preceding bean definition snippet is exactly equivalent (at runtime) to:
    ```xml
    <bean id="theTargetBean" class="..." />
    <bean id="client" class="...">
        <property name="targetName" value="theTargetBean"/>
    </bean>
    ```
    * The first form is preferable to the second, because using the `idref` tag lets the container validate at deployment time that the referenced, named bean actually exists. In the second variation, no validation is performed on the value that is passed to the `targetName` property of the `client` bean. Typos are only discovered (with most likely fatal results) when the `client` bean is actually instantiated. 

##### References to Other Beans (Collaborators)
* The `ref` element is the final element inside a `<constructor-arg/>` or `<property/>` definition element. Here, you set the value of the specified property of a bean to be a reference to another bean (a collaborator) managed by the container. The referenced bean is a dependency of the bean whose property is to be set, and it is initialized on demand as needed before the property is set. 
* Specifying the target bean through the bean attribute of the `<ref/>` tag is the most general form and allows creation of a reference to any bean in the same container or parent container, regardless of whether it is in the same XML file. The value of the bean attribute may be the same as the id attribute of the target bean or be the same as one of the values in the name attribute of the target bean. 
    ```xml
    <ref bean="someBean"/>
    ```
* Specifying the target bean through the `parent` attribute creates a reference to a bean that is in a parent container of the current container. The value of the `parent` attribute may be the same as either the `id` attribute of the target bean or one of the values in the `name` attribute of the target bean. The target bean must be in a parent container of the current one. You should use this bean reference variant mainly when you have a hierarchy of containers and you want to wrap an existing bean in a parent container with a proxy that has the same name as the parent bean. 
    ```xml
    <!-- in the parent context -->
    <bean id="accountService" class="com.something.SimpleAccountService"></bean>
    ```
    ```xml
    <!-- in the child (descendant) context -->
    <!-- bean name is the same as the parent bean -->
    <bean id="accountService" class="org.springframework.aop.framework.ProxyFactoryBean">
        <property name="target">
            <ref parent="accountService"/> <!-- notice how we refer to the parent bean -->
        </property>
    </bean>
    ```

##### Inner Beans
* A `<bean/>` element inside the `<property/>` or `<constructor-arg/>` elements defines an inner bean
    ```xml
    <bean id="outer" class="...">
        <property name="target">
            <bean class="com.example.Person">
                <property name="name" value="Fiona Apple"/>
                <property name="age" value="25"/>
            </bean>
        </property>
    </bean>
    ```
* An inner bean definition does not require a defined ID or name. If specified, the container does not use such a value as an identifier. The container also ignores the `scope` flag on creation, because inner beans are always anonymous and are always created with the outer bean. It is not possible to access inner beans independently or to inject them into collaborating beans other than into the enclosing bean.

##### Collections
* The `<list/>`, `<set/>`, `<map/>`, and `<props/>` elements set the properties and arguments of the Java `Collection` types `List`, `Set`, `Map`, and `Properties`, respectively.
    ```xml
    <bean id="moreComplexObject" class="example.ComplexObject">
        <!-- results in a setAdminEmails(java.util.Properties) call -->
        <property name="adminEmails">
            <props>
                <prop key="administrator">administrator@example.org</prop>
                <prop key="support">support@example.org</prop>
                <prop key="development">development@example.org</prop>
            </props>
        </property>
        <!-- results in a setSomeList(java.util.List) call -->
        <property name="someList">
            <list>
                <value>a list element followed by a reference</value>
                <ref bean="myDataSource" />
            </list>
        </property>
        <!-- results in a setSomeMap(java.util.Map) call -->
        <property name="someMap">
            <map>
                <entry key="an entry" value="just some string"/>
                <entry key ="a ref" value-ref="myDataSource"/>
            </map>
        </property>
        <!-- results in a setSomeSet(java.util.Set) call -->
        <property name="someSet">
            <set>
                <value>just some string</value>
                <ref bean="myDataSource" />
            </set>
        </property>
    </bean>
    ```

###### Collection Merging
* The Spring container also supports merging collections. An application developer can define a parent `<list/>`, `<map/>`, `<set/>` or `<props/>` element and have child `<list/>`, `<map/>`, `<set/>` or `<props/>` elements inherit and override values from the parent collection. That is, the child collection’s values are the result of merging the elements of the parent and child collections, with the child’s collection elements overriding values specified in the parent collection.
    ```xml
    <beans>
        <bean id="parent" abstract="true" class="example.ComplexObject">
            <property name="adminEmails">
                <props>
                    <prop key="administrator">administrator@example.com</prop>
                    <prop key="support">support@example.com</prop>
                </props>
            </property>
        </bean>
        <bean id="child" parent="parent">
            <property name="adminEmails">
                <!-- the merge is specified on the child collection definition -->
                <props merge="true">
                    <prop key="sales">sales@example.com</prop>
                    <prop key="support">support@example.co.uk</prop>
                </props>
            </property>
        </bean>
    <beans>
    ```
    * Notice the use of the `merge=true` attribute on the `<props/>` element of the `adminEmails` property of the `child` bean definition. When the `child` bean is resolved and instantiated by the container, the resulting instance has an `adminEmails` `Properties` collection that contains the result of merging the child’s `adminEmails` collection with the parent’s `adminEmails` collection. 
    ```
    administrator=administrator@example.com
    sales=sales@example.com
    support=support@example.co.uk
    ```
    * The child `Properties` collection’s value set inherits all property elements from the parent `<props/>`, and the child’s value for the `support` value overrides the value in the parent collection.


#### Using depends-on
* If a bean is a dependency of another bean, that usually means that one bean is set as a property of another.
* However, sometimes dependencies between beans are less direct. An example is when a static initializer in a class needs to be triggered, such as for database driver registration. 
* The depends-on attribute can explicitly force one or more beans to be initialized before the bean using this element is initialized. 
    ```xml
    <bean id="beanOne" class="ExampleBean" depends-on="manager"/>
    <bean id="manager" class="ManagerBean" />
    ```
* To express a dependency on multiple beans, supply a list of bean names as the value of the depends-on attribute (commas, whitespace, and semicolons are valid delimiters)
    ```xml
    <bean id="beanOne" class="ExampleBean" depends-on="manager,accountDao">
        <property name="manager" ref="manager" />
    </bean>
    <bean id="manager" class="ManagerBean" />
    <bean id="accountDao" class="x.y.jdbc.JdbcAccountDao" />
    ```


#### Lazy-initialized Beans
* Generally, this pre-instantiation is desirable, because errors in the configuration or surrounding environment are discovered immediately.
* When this behavior is not desirable, you can prevent pre-instantiation of a singleton bean by marking the bean definition as being lazy-initialized.
* A lazy-initialized bean tells the IoC container to create a bean instance when it is first requested, rather than at startup.
* In XML, this behavior is controlled by the lazy-init attribute on the `<bean/>` element
    ```xml
    <bean id="lazy" class="com.something.ExpensiveToCreateBean" lazy-init="true"/>
    <bean name="not.lazy" class="com.something.AnotherBean"/>
    ```
    * When the preceding configuration is consumed by an `ApplicationContext`, the lazy bean is not eagerly pre-instantiated when the `ApplicationContext` starts, whereas the `not.lazy` bean is eagerly pre-instantiated.










