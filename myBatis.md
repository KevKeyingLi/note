- [mybatis](#mybatis)
    * [入门](#入门)
        * [从XML中构建SqlSessionFactory](#从XML中构建SqlSessionFactory)
        * [不使用XML构建SqlSessionFactory](#不使用XML构建SqlSessionFactory)
        * [从SqlSessionFactory中获取SqlSession](#从SqlSessionFactory中获取SqlSession)
        * [作用域(Scope)和生命周期](#作用域(Scope)和生命周期)
    * [XML配置](#XML配置)
        * [properties](#properties)
        * [settings](#settings)
        * [typeAliases](#typeAliases)
        * [typeHandlers](#typeHandlers)
        * [objectFactory](#objectFactory)
        * [plugins](#plugins)
        * [environments](#environments)
            * [environment](#environment)
                * [transactionManager](#transactionManager)
                * [dataSource](#dataSource)
        * [databaseIdProvider](#databaseIdProvider)
        * [mappers](#mappers)
- [mybatis-spring](#mybatis-spring)

### mybatis
* 每个基于MyBatis的应用都是以一个`SqlSessionFactory`的实例为核心的。
* `SqlSessionFactory`的实例可以通过`SqlSessionFactoryBuilder`获得。
* 而`SqlSessionFactoryBuilder`则可以从XML配置文件或一个预先配置的Configuration实例来构建出`SqlSessionFactory`实例。

#### 入门
##### 从XML中构建SqlSessionFactory
* 从XML文件中构建`SqlSessionFactory`的实例非常简单，建议使用类路径下的资源文件进行配置。
* MyBatis包含一个名叫`Resources`的工具类，它包含一些实用方法，使得从类路径或其它位置加载资源文件更加容易。
    ```java
    String resource = "org/mybatis/example/mybatis-config.xml";
    InputStream inputStream = Resources.getResourceAsStream(resource);
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    ```
* XML配置文件中包含了对MyBatis系统的核心设置，包括获取数据库连接实例的数据源(DataSource)以及决定事务作用域和控制方式的事务管理器(TransactionManager)。
    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE configuration
    PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-config.dtd">
    <configuration>
        <environments default="development">
            <environment id="development">
                <transactionManager type="JDBC"/>
                <dataSource type="POOLED">
                    <property name="driver" value="${driver}"/>
                    <property name="url" value="${url}"/>
                    <property name="username" value="${username}"/>
                    <property name="password" value="${password}"/>
                </dataSource>
            </environment>
        </environments>
        <mappers>
            <mapper resource="org/mybatis/example/BlogMapper.xml"/>
        </mappers>
    </configuration>
    ```
    * `environment`元素体中包含了事务管理和连接池的配置。
    * `mappers`元素则包含了一组映射器(mapper)，这些映射器的XML映射文件包含了SQL代码和映射定义信息。

##### 不使用XML构建SqlSessionFactory
* 如果你更愿意直接从Java代码而不是XML文件中创建配置，或者想要创建你自己的配置建造器，MyBatis也提供了完整的配置类，提供了所有与XML文件等价的配置项。
    ```java
    DataSource dataSource = BlogDataSourceFactory.getBlogDataSource();
    TransactionFactory transactionFactory = new JdbcTransactionFactory();
    Environment environment = new Environment("development", transactionFactory, dataSource);
    Configuration configuration = new Configuration(environment);
    configuration.addMapper(BlogMapper.class);
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    ```
    * `configuration`添加了一个映射器类(mapper class)。
    * 映射器类是Java类，它们包含SQL映射注解从而避免依赖XML文件。
    * 不过，由于Java注解的一些限制以及某些MyBatis映射的复杂性，要使用大多数高级映射(比如:嵌套联合映射)，仍然需要使用XML配置。有鉴于此，如果存在一个同名XML配置文件，MyBatis会自动查找并加载它(在这个例子中，基于类路径和`BlogMapper.class`的类名，会加载`BlogMapper.xml`)。

##### 从SqlSessionFactory中获取SqlSession
* `SqlSession`提供了在数据库执行`SQL`命令所需的所有方法。
    ```java
    try (SqlSession session = sqlSessionFactory.openSession()) {
        Blog blog = (Blog) session.selectOne("org.mybatis.example.BlogMapper.selectBlog", 101);
    }
    ```
* 但现在有了一种更简洁的方式:使用和指定语句的参数和返回值相匹配的接口(比如`BlogMapper.class`)，现在你的代码不仅更清晰，更加类型安全，还不用担心可能出错的字符串字面值以及强制类型转换。
    ```java
    try (SqlSession session = sqlSessionFactory.openSession()) {
        BlogMapper mapper = session.getMapper(BlogMapper.class);
        Blog blog = mapper.selectBlog(101);
    }
    ```

##### 探究已映射的SQL语句
* MyBatis提供的所有特性都可以利用基于XML的映射语言来实现，这里给出一个基于XML映射语句的示例，它应该可以满足上个示例中`SqlSession`的调用。
    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
        <mapper namespace="org.mybatis.example.BlogMapper">
            <select id="selectBlog" resultType="Blog">
                select * from Blog where id = #{id}
            </select>
        </mapper>
    ```
    * 为了这个简单的例子，我们似乎写了不少配置，但其实并不多。在一个XML映射文件中，可以定义无数个映射语句，这样一来，XML头部和文档类型声明部分就显得微不足道了。它在命名空间`org.mybatis.example.BlogMapper`中定义了一个名为`selectBlog`的映射语句，这样你就可以用全限定名`org.mybatis.example.BlogMapper.selectBlog`来调用映射语句了
    ```java
    Blog blog = (Blog) session.selectOne("org.mybatis.example.BlogMapper.selectBlog", 101);
    ```
    * 这种方式和用全限定名调用Java对象的方法类似，该命名就可以直接映射到在命名空间中同名的映射器类，并将已映射的`select`语句匹配到对应名称、参数和返回类型的方法。
    * 因此你就可以像上面那样，不费吹灰之力地在对应的映射器接口调用方法，就像下面这样:
    ```java
    BlogMapper mapper = session.getMapper(BlogMapper.class);
    Blog blog = mapper.selectBlog(101);
    ```
    * 第二种方法有很多优势，首先它不依赖于字符串字面值，会更安全一点
    * 其次，如果你的IDE有代码补全功能，那么代码补全可以帮你快速选择到映射好的SQL语句。

###### 对命名空间的一点补充
* 命名空间的作用有两个，一个是利用更长的全限定名来将不同的语句隔离开来，同时也实现了你上面见到的接口绑定。就算你觉得暂时用不到接口绑定，你也应该遵循这里的规定，以防哪天你改变了主意。长远来看，只要将命名空间置于合适的Java包命名空间之中，你的代码会变得更加整洁，也有利于你更方便地使用 MyBatis。
* 命名解析:为了减少输入量，MyBatis 对所有具有名称的配置元素(包括语句，结果映射，缓存等)使用了如下的命名解析规则。
    * 全限定名(比如`com.mypackage.MyMapper.selectAllThings`)将被直接用于查找及使用。
    * 短名称(比如`selectAllThings`)如果全局唯一也可以作为一个单独的引用。如果不唯一，有两个或两个以上的相同名称(比如`com.foo.selectAllThings`和`com.bar.selectAllThings`)，那么使用时就会产生“短名称不唯一”的错误，这种情况下就必须使用全限定名。
* 对于像`BlogMapper`这样的映射器类来说，还有另一种方法来完成语句映射。它们映射的语句可以不用XML来配置，而可以使用Java注解来配置。比如，上面的XML示例可以被替换成如下的配置:
    ```java
    package org.mybatis.example;
    public interface BlogMapper {
        @Select("SELECT * FROM blog WHERE id = #{id}")
        Blog selectBlog(int id);
    }
    ```
    * 使用注解来映射简单语句会使代码显得更加简洁，但对于稍微复杂一点的语句，Java注解不仅力不从心，还会让你本就复杂的SQL语句更加混乱不堪。因此，如果你需要做一些很复杂的操作，最好用XML来映射语句。

##### 作用域(Scope)和生命周期
* 对象生命周期和依赖注入框架
    * 依赖注入框架可以创建线程安全的、基于事务的`SqlSession`和映射器，并将它们直接注入到你的bean中，因此可以直接忽略它们的生命周期。
    * 如果对如何通过依赖注入框架使用MyBatis感兴趣，可以研究一下MyBatis-Spring或MyBatis-Guice两个子项目。
* `SqlSessionFactoryBuilder`
    * 这个类可以被实例化、使用和丢弃，一旦创建了`SqlSessionFactory`，就不再需要它了。 因此`SqlSessionFactoryBuilder`实例的最佳作用域是方法作用域(也就是局部方法变量)。你可以重用`SqlSessionFactoryBuilder`来创建多个`SqlSessionFactory`实例，但最好还是不要一直保留着它，以保证所有的XML解析资源可以被释放给更重要的事情。
* `SqlSessionFactory`
    * `SqlSessionFactory`一旦被创建就应该在应用的运行期间一直存在，没有任何理由丢弃它或重新创建另一个实例。 使用`SqlSessionFactory`的最佳实践是在应用运行期间不要重复创建多次，多次重建`SqlSessionFactory`被视为一种代码“坏习惯”。因此`SqlSessionFactory`的最佳作用域是应用作用域。有很多方法可以做到，最简单的就是使用单例模式或者静态单例模式。
* `SqlSession`
    * 每个线程都应该有它自己的`SqlSession`实例。`SqlSession`的实例不是线程安全的，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域。绝对不能将`SqlSession`实例的引用放在一个类的静态域，甚至一个类的实例变量也不行。 也绝不能将`SqlSession`实例的引用放在任何类型的托管作用域中，比如`Servlet`框架中的`HttpSession`。 如果你现在正在使用一种Web框架，考虑将`SqlSession`放在一个和HTTP请求相似的作用域中。 换句话说，每次收到HTTP请求，就可以打开一个`SqlSession`，返回一个响应后，就关闭它。这个关闭操作很重要，为了确保每次都能执行关闭操作，你应该把这个关闭操作放到`finally`块中。 下面的示例就是一个确保`SqlSession`关闭的标准模式:
        ```java
        try (SqlSession session = sqlSessionFactory.openSession()) {
            // 你的应用逻辑代码
        }
        ```
        * 在所有代码中都遵循这种使用模式，可以保证所有数据库资源都能被正确地关闭。
* 映射器实例
    * 映射器是一些绑定映射语句的接口。映射器接口的实例是从`SqlSession`中获得的。虽然从技术层面上来讲，任何映射器实例的最大作用域与请求它们的`SqlSession`相同。但方法作用域才是映射器实例的最合适的作用域。也就是说，映射器实例应该在调用它们的方法中被获取，使用完毕之后即可丢弃。映射器实例并不需要被显式地关闭。尽管在整个请求作用域保留映射器实例不会有什么问题，但是你很快会发现，在这个作用域上管理太多像`SqlSession`的资源会让你忙不过来。因此，最好将映射器放在方法作用域内。就像下面的例子一样:
    ```java
    try (SqlSession session = sqlSessionFactory.openSession()) {
        BlogMapper mapper = session.getMapper(BlogMapper.class);
        // 你的应用逻辑代码
    }
    ```

#### XML配置
* [properties](#properties)
* [settings](#settings)
* [typeAliases](#typeAliases)
* [typeHandlers](#typeHandlers)
* [objectFactory](#objectFactory)
* [plugins](#plugins)
* [environments](#environments)
    * [environment](#environment)
        * [transactionManager](#transactionManager)
        * [dataSource](#dataSource)
* [databaseIdProvider](#databaseIdProvider)
* [mappers](#mappers)

##### properties
* 属性
* 这些属性可以在外部进行配置，并可以进行动态替换。
    * 你既可以在典型的Java属性文件中配置这些属性，也可以在`properties`元素的子元素中设置。
        ```xml
        <properties resource="org/mybatis/example/config.properties">
            <property name="username" value="dev_user"/>
            <property name="password" value="F2Fa3!33TYyg"/>
        </properties>
        ```
    * 设置好的属性可以在整个配置文件中用来替换需要动态配置的属性值。
        ```xml
        <dataSource type="POOLED">
            <property name="driver" value="${driver}"/>
            <property name="url" value="${url}"/>
            <property name="username" value="${username}"/>
            <property name="password" value="${password}"/>
        </dataSource>
        ```
        * 这个例子中的`username`和`password`将会由`properties`元素中设置的相应值来替换。`driver`和`url`属性将会由`config.properties`文件中对应的值来替换。
    * 也可以在`SqlSessionFactoryBuilder.build()`方法中传入属性值。
        ```java
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, props);
        // ... 或者 ...
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment, props);
        ```
* 如果一个属性在不只一个地方进行了配置，那么，MyBatis将按照下面的顺序来加载:
    * 首先读取在`properties`元素体内指定的属性。
    * 然后根据`properties`元素中的`resource`属性读取类路径下属性文件，或根据`url`属性指定的路径读取属性文件，并覆盖之前读取过的同名属性。
    * 最后读取作为方法参数传递的属性，并覆盖之前读取过的同名属性。
* 通过方法参数传递的属性具有最高优先级，`resource/url`属性中指定的配置文件次之，最低优先级的则是`properties`元素中指定的属性。
* 你可以为占位符指定一个默认值。例如:
    ```xml
    <dataSource type="POOLED">
        <!-- ... -->
        <property name="username" value="${username:ut_user}"/> <!-- 如果属性 'username' 没有被配置，'username' 属性的值将为 'ut_user' -->
    </dataSource>
    ```
* 这个特性默认是关闭的。要启用这个特性，需要添加一个特定的属性来开启这个特性。例如:
    ```xml
    <properties resource="org/mybatis/example/config.properties">
        <!-- ... -->
        <property name="org.apache.ibatis.parsing.PropertyParser.enable-default-value" value="true"/> <!-- 启用默认值特性 -->
    </properties>
    ```
* 如果你在属性名中使用了`":"`字符(如:`db:username`)，或者在SQL映射中使用了OGNL表达式的三元运算符(如:`${tableName != null ? tableName : 'global_constants'}`)，就需要设置特定的属性来修改分隔属性名和默认值的字符。例如：
    ```xml
    <properties resource="org/mybatis/example/config.properties">
        <!-- ... -->
        <property name="org.apache.ibatis.parsing.PropertyParser.default-value-separator" value="?:"/> <!-- 修改默认值的分隔符 -->
    </properties>
    <dataSource type="POOLED">
        <!-- ... -->
        <property name="username" value="${db:username?:ut_user}"/>
    </dataSource>
    ```

##### settings
* 设置
* 这是MyBatis中极为重要的调整设置，它们会改变MyBatis的运行时行为。
| 设置名 |
| --- |
| `cacheEnabled` |
| `lazyLoadingEnabled` |
| `aggressiveLazyLoading` |
| `multipleResultSetsEnabled` |
| `useColumnLabel` |
| `useGeneratedKeys` |
| `autoMappingBehavior` |
| `autoMappingUnknownColumnBehavior` |
| `defaultExecutorType` |
| `defaultStatementTimeout` |
| `defaultFetchSize` |
| `defaultResultSetType` |
| `safeRowBoundsEnabled` |
| `safeResultHandlerEnabled` |
| `mapUnderscoreToCamelCase` |
| `localCacheScope` |
| `jdbcTypeForNull` |
| `lazyLoadTriggerMethods` |
| `defaultScriptingLanguage` |
| `defaultEnumTypeHandler` |
| `callSettersOnNulls` |
| `returnInstanceForEmptyRow` |
| `logPrefix` |
| `logImpl` |
| `proxyFactory` |
| `vfsImpl` |
| `useActualParamName` |
| `configurationFactory` |

##### typeAliases
##### typeHandlers
##### objectFactory
##### plugins
##### environments
##### environment
##### transactionManager
##### dataSource
##### databaseIdProvider
##### mappers

### mybatis-spring