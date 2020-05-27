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
    * [XML映射器](#XML映射器)
        * [select](#select)
        * [insert,update和delete](#insert,update和delete)
        * [参数](#参数)
        * [结果映射](#结果映射)
        * [自动映射](#自动映射)
        * [缓存](#缓存)
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
* 如果你在属性名中使用了`":"`字符(如:`db:username`)，或者在SQL映射中使用了OGNL表达式的三元运算符(如:`${tableName != null ? tableName : 'global_constants'}`)，就需要设置特定的属性来修改分隔属性名和默认值的字符。例如:
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

| 设置名 | 描述 | 有效值 | 默认值 |
| --- | --- | --- | --- |
| `cacheEnabled` | 全局性地开启或关闭所有映射器配置文件中已配置的任何缓存。 | Boolean | TRUE |
| `lazyLoadingEnabled` | 延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。 特定关联关系中可通过设置 fetchType 属性来覆盖该项的开关状态。 | Boolean | FALSE |
| `aggressiveLazyLoading` | 开启时，任一方法的调用都会加载该对象的所有延迟加载属性。 否则，每个延迟加载属性会按需加载(参考 lazyLoadTriggerMethods)。 | Boolean | false(在3.4.1 及之前的版本中默认为 true) |
| `multipleResultSetsEnabled` | 是否允许单个语句返回多结果集(需要数据库驱动支持)。 | Boolean | TRUE |
| `useColumnLabel` | 使用列标签代替列名。实际表现依赖于数据库驱动，具体可参考数据库驱动的相关文档，或通过对比测试来观察。 | Boolean | TRUE |
| `useGeneratedKeys` | 允许 JDBC 支持自动生成主键，需要数据库驱动支持。如果设置为 true，将强制使用自动生成主键。尽管一些数据库驱动不支持此特性，但仍可正常工作(如 Derby)。 | Boolean | FALSE |
| `autoMappingBehavior` | 指定 MyBatis 应如何自动映射列到字段或属性。 NONE 表示关闭自动映射;PARTIAL 只会自动映射没有定义嵌套结果映射的字段。 FULL 会自动映射任何复杂的结果集(无论是否嵌套)。 | NONE, PARTIAL, FULL | PARTIAL |
| `autoMappingUnknownColumnBehavior` | 指定发现自动映射目标未知列(或未知属性类型)的行为。| NONE,WARNING,FAILING | NONE |
| `defaultExecutorType` | 配置默认的执行器。SIMPLE 就是普通的执行器;REUSE 执行器会重用预处理语句(PreparedStatement); BATCH 执行器不仅重用语句还会执行批量更新。 | SIMPLE REUSE BATCH | SIMPLE |
| `defaultStatementTimeout` | 设置超时时间，它决定数据库驱动等待数据库响应的秒数。 | 任意正整数 | 未设置 (null) |
| `defaultFetchSize` | 为驱动的结果集获取数量(fetchSize)设置一个建议值。此参数只可以在查询设置中被覆盖。 | 任意正整数 | 未设置 (null) |
| `defaultResultSetType` | 指定语句默认的滚动策略。(新增于 3.5.2) | FORWARD_ONLY、SCROLL_SENSITIVE、SCROLL_INSENSITIVE、DEFAULT(等同于未设置) | 未设置 (null) |
| `safeRowBoundsEnabled` | 是否允许在嵌套语句中使用分页(RowBounds)。如果允许使用则设置为 false。 | Boolean | FALSE |
| `safeResultHandlerEnabled` | 是否允许在嵌套语句中使用结果处理器(ResultHandler)。如果允许使用则设置为 false。 | Boolean | TRUE |
| `mapUnderscoreToCamelCase` | 是否开启驼峰命名自动映射，即从经典数据库列名 A_COLUMN 映射到经典 Java 属性名 aColumn。 | Boolean | FALSE |
| `localCacheScope` | MyBatis 利用本地缓存机制(Local Cache)防止循环引用和加速重复的嵌套查询。 默认值为 SESSION，会缓存一个会话中执行的所有查询。 若设置值为 STATEMENT，本地缓存将仅用于执行语句，对相同 SqlSession 的不同查询将不会进行缓存。 | SESSION、STATEMENT | SESSION |
| `jdbcTypeForNull` | 当没有为参数指定特定的 JDBC 类型时，空值的默认 JDBC 类型。 某些数据库驱动需要指定列的 JDBC 类型，多数情况直接用一般类型即可，比如 NULL、VARCHAR 或 OTHER。 | JdbcType 常量，常用值:NULL、VARCHAR 或 OTHER。 | OTHER |
| `lazyLoadTriggerMethods` | 指定对象的哪些方法触发一次延迟加载。 | 用逗号分隔的方法列表。 | equals,clone,hashCode,toString |
| `defaultScriptingLanguage` | 指定动态 SQL 生成使用的默认脚本语言。 | 一个类型别名或全限定类名。 | org.apache.ibatis.scripting.xmltags.XMLLanguageDriver |
| `defaultEnumTypeHandler` | 指定 Enum 使用的默认 TypeHandler 。(新增于 3.4.5) | 一个类型别名或全限定类名。 | org.apache.ibatis.type.EnumTypeHandler |
| `callSettersOnNulls` | 指定当结果集中值为 null 的时候是否调用映射对象的 setter(map 对象时为 put)方法，这在依赖于 Map.keySet() 或 null 值进行初始化时比较有用。注意基本类型(int、boolean 等)是不能设置成 null 的。 | Boolean | FALSE |
| `returnInstanceForEmptyRow` | 当返回行的所有列都是空时，MyBatis默认返回 null。 当开启这个设置时，MyBatis会返回一个空实例。 请注意，它也适用于嵌套的结果集(如集合或关联)。(新增于 3.4.2) | Boolean | FALSE |
| `logPrefix` | 指定 MyBatis 增加到日志名称的前缀。 | 任何字符串 | 未设置 |
| `logImpl` | 指定 MyBatis 所用日志的具体实现，未指定时将自动查找。 | SLF4J,LOG4J,LOG4J2,JDK_LOGGING,COMMONS_LOGGING,STDOUT_LOGGING,NO_LOGGING | 未设置 |
| `proxyFactory` | 指定 Mybatis 创建可延迟加载对象所用到的代理工具。 | CGLIB,JAVASSIST | JAVASSIST (MyBatis 3.3 以上) |
| `vfsImpl` | 指定 VFS 的实现 | 自定义 VFS 的实现的类全限定名，以逗号分隔。 | 未设置 |
| `useActualParamName` | 允许使用方法签名中的名称作为语句参数名称。 为了使用该特性，你的项目必须采用 Java 8 编译，并且加上 -parameters 选项。(新增于 3.4.1) | Boolean | TRUE |
| `configurationFactory` | 指定一个提供 Configuration 实例的类。 这个被返回的 Configuration 实例用来加载被反序列化对象的延迟加载属性值。 这个类必须包含一个签名为static Configuration getConfiguration() 的方法。(新增于 3.2.3) | 一个类型别名或完全限定类名。 | 未设置 |

* 一个配置完整的`settings`元素的示例如下:
    ```xml
    <settings>
        <setting name="cacheEnabled" value="true"/>
        <setting name="lazyLoadingEnabled" value="true"/>
        <setting name="multipleResultSetsEnabled" value="true"/>
        <setting name="useColumnLabel" value="true"/>
        <setting name="useGeneratedKeys" value="false"/>
        <setting name="autoMappingBehavior" value="PARTIAL"/>
        <setting name="autoMappingUnknownColumnBehavior" value="WARNING"/>
        <setting name="defaultExecutorType" value="SIMPLE"/>
        <setting name="defaultStatementTimeout" value="25"/>
        <setting name="defaultFetchSize" value="100"/>
        <setting name="safeRowBoundsEnabled" value="false"/>
        <setting name="mapUnderscoreToCamelCase" value="false"/>
        <setting name="localCacheScope" value="SESSION"/>
        <setting name="jdbcTypeForNull" value="OTHER"/>
        <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
    </settings>
    ```

##### typeAliases
* 类型别名
* 类型别名可为Java类型设置一个缩写名字。它仅用于XML配置，意在降低冗余的全限定类名书写。例如:
    ```xml
    <typeAliases>
        <typeAlias alias="Author" type="domain.blog.Author"/>
        <typeAlias alias="Blog" type="domain.blog.Blog"/>
        <typeAlias alias="Comment" type="domain.blog.Comment"/>
        <typeAlias alias="Post" type="domain.blog.Post"/>
        <typeAlias alias="Section" type="domain.blog.Section"/>
        <typeAlias alias="Tag" type="domain.blog.Tag"/>
    </typeAliases>
    ```
* 当这样配置时，Blog可以用在任何使用`domain.blog.Blog`的地方，也可以指定一个包名，MyBatis会在包名下面搜索需要的Java Bean，比如:
    ```xml
    <typeAliases>
        <package name="domain.blog"/>
    </typeAliases>
    ```
    * 每一个在包`domain.blog`中的Java Bean，在没有注解的情况下，会使用Bean的首字母小写的非限定类名来作为它的别名。比如`domain.blog.Author`的别名为`author`;若有注解，则别名为其注解值。
    ```java
    @Alias("author")
    public class Author {
        // ...
    }
    ```

##### typeHandlers
* 类型处理器
* MyBatis在设置预处理语句(`PreparedStatement`)中的参数或从结果集中取出一个值时，都会用类型处理器将获取到的值以合适的方式转换成Java类型。
* 你可以重写已有的类型处理器或创建你自己的类型处理器来处理不支持的或非标准的类型。具体做法为:实现`org.apache.ibatis.type.TypeHandler`接口，或继承一个很便利的类`org.apache.ibatis.type.BaseTypeHandler`，并且可以(可选地)将它映射到一个JDBC类型。比如:
    ```java
    @MappedJdbcTypes(JdbcType.VARCHAR)
    public class ExampleTypeHandler extends BaseTypeHandler<String> {

        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
            ps.setString(i, parameter);
        }

        @Override
        public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
            return rs.getString(columnName);
        }

        @Override
        public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getString(columnIndex);
        }

        @Override
        public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
            return cs.getString(columnIndex);
        }
    }
    ```
    ```xml
    <typeHandlers>
        <typeHandler handler="org.mybatis.example.ExampleTypeHandler"/>
    </typeHandlers>
    ```
    * 使用上述的类型处理器将会覆盖已有的处理Java String类型的属性以及VARCHAR类型的参数和结果的类型处理器。要注意MyBatis不会通过检测数据库元信息来决定使用哪种类型，所以你必须在参数和结果映射中指明字段是VARCHAR类型，以使其能够绑定到正确的类型处理器上。这是因为MyBatis直到语句被执行时才清楚数据类型。
* 通过类型处理器的泛型，MyBatis可以得知该类型处理器处理的Java类型，不过这种行为可以通过两种方法改变:
    * 在类型处理器的配置元素(`typeHandler`元素)上增加一个`javaType`属性(比如:javaType="String")；
    * 在类型处理器的类上增加一个`@MappedTypes`注解指定与其关联的Java类型列表。如果在`javaType`属性中也同时指定，则注解上的配置将被忽略。
* 可以通过两种方式来指定关联的JDBC类型:
    * 在类型处理器的配置元素上增加一个`jdbcType`属性(比如:`jdbcType="VARCHAR"`)；
    * 在类型处理器的类上增加一个`@MappedJdbcTypes`注解指定与其关联的JDBC类型列表。 如果在`jdbcType`属性中也同时指定，则注解上的配置将被忽略。
* 当在`ResultMap`中使用`javaType=[Java类型],jdbcType=null`的组合来选择一个类型处理器。这意味着使用`@MappedJdbcTypes`注解可以限制类型处理器的作用范围，并且可以确保，除非显式地设置，否则类型处理器在`ResultMap`中将不会生效。 如果希望能在`ResultMap`中隐式地使用类型处理器，那么设置`@MappedJdbcTypes`注解的`includeNullJdbcType=true`即可。 然而从Mybatis3.4.0 开始，如果某个Java类型只有一个注册的类型处理器，即使没有设置`includeNullJdbcType=true`，那么这个类型处理器也会是`ResultMap`使用Java类型时的默认处理器。

##### objectFactory
* 对象工厂
* 每次MyBatis创建结果对象的新实例时，它都会使用一个对象工厂(`ObjectFactory`)实例来完成实例化工作。
* 默认的对象工厂需要做的仅仅是实例化目标类，要么通过默认无参构造方法，要么通过存在的参数映射来调用带有参数的构造方法。如果想覆盖对象工厂的默认行为，可以通过创建自己的对象工厂来实现。
    * `ExampleObjectFactory.java`
        ```java
        public class ExampleObjectFactory extends DefaultObjectFactory {
            public Object create(Class type) {
                return super.create(type);
            }
            public Object create(Class type, List<Class> constructorArgTypes, List<Object> constructorArgs) {
                return super.create(type, constructorArgTypes, constructorArgs);
            }
            public void setProperties(Properties properties) {
                super.setProperties(properties);
            }
            public <T> boolean isCollection(Class<T> type) {
                return Collection.class.isAssignableFrom(type);
            }
        }
        ```
    * `mybatis-config.xml`
        ```xml
        <objectFactory type="org.mybatis.example.ExampleObjectFactory">
            <property name="someProperty" value="100"/>
        </objectFactory>
        ```
* `ObjectFactory`接口很简单，它包含两个创建实例用的方法，一个是处理默认无参构造方法的，另外一个是处理带参数的构造方法的。另外，`setProperties`方法可以被用来配置`ObjectFactory`，在初始化你的`ObjectFactory`实例后，`objectFactory`元素体中定义的属性会被传递给`setProperties`方法。

##### plugins
* 插件
* MyBatis允许你在映射语句执行过程中的某一点进行拦截调用。默认情况下，MyBatis允许使用插件来拦截的方法调用包括:
    * `Executor(update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)`
    * `ParameterHandler(getParameterObject, setParameters)`
    * `ResultSetHandler(handleResultSets, handleOutputParameters)`
    * `StatementHandler(prepare, parameterize, batch, update, query)`
* 这些类中方法的细节可以通过查看每个方法的签名来发现，或者直接查看MyBatis发行包中的源代码。如果你想做的不仅仅是监控方法的调用，那么你最好相当了解要重写的方法的行为。因为在试图修改或重写已有方法的行为时，很可能会破坏MyBatis的核心模块。这些都是更底层的类和方法，所以使用插件的时候要特别当心。
* 通过MyBatis提供的强大机制，使用插件是非常简单的，只需实现`Interceptor`接口，并指定想要拦截的方法签名即可。
    * `ExamplePlugin.java`
        ```java
        @Intercepts({@Signature(type= Executor.class, method = "update", args = {MappedStatement.class,Object.class})})
        public class ExamplePlugin implements Interceptor {
            private Properties properties = new Properties();
            public Object intercept(Invocation invocation) throws Throwable {
                // implement pre processing if need
                Object returnObject = invocation.proceed();
                // implement post processing if need
                return returnObject;
            }
            public void setProperties(Properties properties) {
                this.properties = properties;
            }
        }
        ```
    * `mybatis-config.xml`
* 上面的插件将会拦截在`Executor`实例中所有的`update`方法调用，这里的`Executor`是负责执行底层映射语句的内部对象。
* 覆盖配置类
    * 除了用插件来修改MyBatis核心行为以外，还可以通过完全覆盖配置类来达到目的。只需继承配置类后覆盖其中的某个方法，再把它传递到`SqlSessionFactoryBuilder.build(myConfig)`方法即可。

##### environments
* 环境配置
* MyBatis可以配置成适应多种环境，这种机制有助于将SQL映射应用于多种数据库之中，现实情况下有多种理由需要这么做。例如，开发、测试和生产环境需要有不同的配置；或者想在具有相同Schema的多个生产数据库中使用相同的SQL映射。
* 尽管可以配置多个环境，但每个`SqlSessionFactory`实例只能选择一种环境。所以，如果你想连接两个数据库，就需要创建两个`SqlSessionFactory`实例，每个数据库对应一个。
* 每个数据库对应一个`SqlSessionFactory`实例
    * 为了指定创建哪种环境，只要将它作为可选的参数传递给`SqlSessionFactoryBuilder`即可。可以接受环境配置的两个方法签名是:
        ```java
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment, properties);
        ```
    * 如果忽略了环境参数，那么将会加载默认环境，如下所示:
        ```java
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, properties);
        ```
* `environments`元素定义了如何配置环境。
    ```xml
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC">
                <property name="..." value="..."/>
            </transactionManager>
            <dataSource type="POOLED">
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>
    ```
    * 注意一些关键点:
        * 默认使用的环境`id`(比如:`default="development"`)。
        * 每个`environment`元素定义的环境`id`(比如:`id="development"`)。
        * 事务管理器的配置(比如:`type="JDBC"`)。
        * 数据源的配置(比如:`type="POOLED"`)。

##### environment

##### transactionManager
* 事务管理器
* 在MyBatis中有两种类型的事务管理器。
    * JDBC: 这个配置直接使用了JDBC的提交和回滚设施，它依赖从数据源获得的连接来管理事务作用域。
    * MANAGED: 这个配置几乎没做什么。它从不提交或回滚一个连接，而是让容器来管理事务的整个生命周期(比如JEE应用服务器的上下文)。默认情况下它会关闭连接。然而一些容器并不希望连接被关闭，因此需要将`closeConnection`属性设置为false来阻止默认的关闭行为。
        ```xml
        <transactionManager type="MANAGED">
            <property name="closeConnection" value="false"/>
        </transactionManager>
        ```
* 这两种事务管理器类型都不需要设置任何属性。它们其实是类型别名，换句话说，你可以用`TransactionFactory`接口实现类的全限定名或类型别名代替它们。
    ```java
    public interface TransactionFactory {
        default void setProperties(Properties props) { // 从 3.5.2 开始，该方法为默认方法
        // 空实现
        }
        Transaction newTransaction(Connection conn);
        Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);
    }
    ```
    * 在事务管理器实例化后，所有在XML中配置的属性将会被传递给`setProperties()`方法。你的实现还需要创建一个`Transaction`接口的实现类，这个接口也很简单:
        ```java
        public interface Transaction {
            Connection getConnection() throws SQLException;
            void commit() throws SQLException;
            void rollback() throws SQLException;
            void close() throws SQLException;
            Integer getTimeout() throws SQLException;
        }
        ```
    * 使用这两个接口，你可以完全自定义MyBatis对事务的处理。

##### dataSource
* 数据源
* `dataSource`元素使用标准的JDBC数据源接口来配置JDBC连接对象的资源。
    * 大多数 MyBatis 应用程序会按示例中的例子来配置数据源。虽然数据源配置是可选的，但如果要启用延迟加载特性，就必须配置数据源。
* 有三种内建的数据源类型
    * `UNPOOLED`: 这个数据源的实现会每次请求时打开和关闭连接。虽然有点慢，但对那些数据库连接可用性要求不高的简单应用程序来说，是一个很好的选择。性能表现则依赖于使用的数据库，对某些数据库来说，使用连接池并不重要，这个配置就很适合这种情形。`UNPOOLED`类型的数据源仅仅需要配置以下5种属性:
        * `driver`: 这是JDBC驱动的Java类全限定名(并不是 JDBC 驱动中可能包含的数据源类)。
        * `url`: 这是数据库的JDBC URL地址。
        * `username`: 登录数据库的用户名。
        * `password`: 登录数据库的密码。
        * `defaultTransactionIsolationLevel`: 默认的连接事务隔离级别。
        * `defaultNetworkTimeout`: 等待数据库操作完成的默认网络超时时间(单位:毫秒)。
    * `POOLED`: 这种数据源的实现利用“池”的概念将JDBC连接对象组织起来，避免了创建新的连接实例时所必需的初始化和认证时间。这种处理方式很流行，能使并发Web应用快速响应请求。除了上述提到`UNPOOLED`下的属性外，还有更多属性用来配置`POOLED`的数据源:
        * `poolMaximumActiveConnections`: 在任意时间可存在的活动(正在使用)连接数量，默认值:10
        * `poolMaximumIdleConnections`: 任意时间可能存在的空闲连接数。
        * `poolMaximumCheckoutTime`: 在被强制返回之前，池中连接被检出(checked out)时间，默认值:20000毫秒(即20秒)
        * `poolTimeToWait`: 这是一个底层设置，如果获取连接花费了相当长的时间，连接池会打印状态日志并重新尝试获取一个连接(避免在误配置的情况下一直失败且不打印日志)，默认值:20000毫秒(即20秒)。
        * `poolMaximumLocalBadConnectionTolerance`: 这是一个关于坏连接容忍度的底层设置，作用于每一个尝试从缓存池获取连接的线程。如果这个线程获取到的是一个坏的连接，那么这个数据源允许这个线程尝试重新获取一个新的连接，但是这个重新尝试的次数不应该超过`poolMaximumIdleConnections`与`poolMaximumLocalBadConnectionTolerance`之和。默认值:3(新增于3.4.5)
        * `poolPingQuery`: 发送到数据库的侦测查询，用来检验连接是否正常工作并准备接受请求。默认是`NO PING QUERY SET`，这会导致多数数据库驱动出错时返回恰当的错误消息。
        * `poolPingEnabled`: 是否启用侦测查询。若开启，需要设置`poolPingQuery`属性为一个可执行的SQL语句(最好是一个速度非常快的SQL语句)，默认值:false。
        * `poolPingConnectionsNotUsedFor`: 配置`poolPingQuery`的频率。可以被设置为和数据库连接超时时间一样，来避免不必要的侦测，默认值:0
    * `JNDI`: 这个数据源实现是为了能在如EJB或应用服务器这类容器中使用，容器可以集中或在外部配置数据源，然后放置一个JNDI上下文的数据源引用。这种数据源配置只需要两个属性:
        * `initial_context`: 这个属性用来在`InitialContext`中寻找上下文(即，`initialContext.lookup(initial_context)`)。这是个可选属性，如果忽略，那么将会直接从`InitialContext`中寻找 data_source 属性。
        * `data_source`: 这是引用数据源实例位置的上下文路径。提供了`initial_context`配置时会在其返回的上下文中进行查找，没有提供时则直接在`InitialContext`中查找。

##### databaseIdProvider
* 数据库厂商标识
* MyBatis可以根据不同的数据库厂商执行不同的语句，这种多厂商的支持是基于映射语句中的`databaseId`属性。MyBatis会加载带有匹配当前数据库`databaseId`属性和所有不带`databaseId`属性的语句。如果同时找到带有`databaseId`和不带`databaseId`的相同语句，则后者会被舍弃。为支持多厂商特性，只要像下面这样在`mybatis-config.xml`文件中加入`databaseIdProvider`即可:
    ```xml
    <databaseIdProvider type="DB_VENDOR" />
    ```
* `databaseIdProvider`对应的`DB_VENDOR`实现会将`databaseId`设置为`DatabaseMetaData#getDatabaseProductName()`返回的字符串。由于通常情况下这些字符串都非常长，而且相同产品的不同版本会返回不同的值，你可能想通过设置属性别名来使其变短。
    ```xml
    <databaseIdProvider type="DB_VENDOR">
        <property name="SQL Server" value="sqlserver"/>
        <property name="DB2" value="db2"/>
        <property name="Oracle" value="oracle" />
    </databaseIdProvider>
    ```
* 在提供了属性别名时，`databaseIdProvider`的`DB_VENDOR`实现会将`databaseId`设置为数据库产品名与属性中的名称第一个相匹配的值，如果没有匹配的属性，将会设置为`null`。在这个例子中，如果`getDatabaseProductName()`返回`Oracle(DataDirect)`，`databaseId`将被设置为`oracle`。
* 你可以通过实现接口`org.apache.ibatis.mapping.DatabaseIdProvider`并在`mybatis-config.xml`中注册来构建自己的`DatabaseIdProvider`:
    ```java
    public interface DatabaseIdProvider {
        default void setProperties(Properties p) { // 从 3.5.2 开始，该方法为默认方法
            // 空实现
        }
        String getDatabaseId(DataSource dataSource) throws SQLException;
    }
    ```

##### mappers
* 映射器
* 既然MyBatis的行为已经由上述元素配置完了，我们现在就要来定义SQL映射语句了。但首先，我们需要告诉MyBatis到哪里去找到这些语句。在自动查找资源方面，Java并没有提供一个很好的解决方案，所以最好的办法是直接告诉MyBatis到哪里去找映射文件。你可以使用相对于类路径的资源引用，或完全限定资源定位符(包括`file:///`形式的`URL`)，或类名和包名等。
    ```xml
    <!-- 使用相对于类路径的资源引用 -->
    <mappers>
        <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
        <mapper resource="org/mybatis/builder/BlogMapper.xml"/>
        <mapper resource="org/mybatis/builder/PostMapper.xml"/>
    </mappers>
    ```
    ```xml
    <!-- 使用完全限定资源定位符(URL) -->
    <mappers>
        <mapper url="file:///var/mappers/AuthorMapper.xml"/>
        <mapper url="file:///var/mappers/BlogMapper.xml"/>
        <mapper url="file:///var/mappers/PostMapper.xml"/>
    </mappers>
    ```
    ```xml
    <!-- 使用映射器接口实现类的完全限定类名 -->
    <mappers>
        <mapper class="org.mybatis.builder.AuthorMapper"/>
        <mapper class="org.mybatis.builder.BlogMapper"/>
        <mapper class="org.mybatis.builder.PostMapper"/>
    </mappers>
    ```
    ```xml
    <!-- 将包内的映射器接口实现全部注册为映射器 -->
    <mappers>
        <package name="org.mybatis.builder"/>
    </mappers>
    ```

### XML映射器
#### select
* MyBatis的基本原则之一是: 在每个插入、更新或删除操作之间，通常会执行多个查询操作。
* 一个简单查询的`select`元素是非常简单的
    ```xml
    <select id="selectPerson" parameterType="int" resultType="hashmap">
        SELECT * FROM PERSON WHERE ID = #{id}
    </select>
    ```
    * `#{id}`: 这就告诉MyBatis创建一个预处理语句(PreparedStatement)参数，在JDBC中，这样的一个参数在SQL中会由一个`?`来标识，并被传递到一个新的预处理语句中。
* 使用JDBC就意味着使用更多的代码，以便提取结果并将它们映射到对象实例中，而这就是MyBatis的拿手好戏。
* Select元素的属性

| 属性 | 描述 |
| --- | --- |
| `id` | 在命名空间中唯一的标识符，可以被用来引用这条语句。 |
| `parameterType` | 将会传入这条语句的参数的类全限定名或别名。这个属性是可选的，因为 MyBatis 可以通过类型处理器(TypeHandler)推断出具体传入语句的参数，默认值为未设置(unset)。 |
| `resultType` | 期望从这条语句中返回结果的类全限定名或别名。 注意，如果返回的是集合，那应该设置为集合包含的类型，而不是集合本身的类型。 resultType 和 resultMap 之间只能同时使用一个。 |
| `resultMap` | 对外部 resultMap 的命名引用。结果映射是 MyBatis 最强大的特性，如果你对其理解透彻，许多复杂的映射问题都能迎刃而解。 resultType 和 resultMap 之间只能同时使用一个。 |
| `flushCache` | 将其设置为 true 后，只要语句被调用，都会导致本地缓存和二级缓存被清空，默认值:false。 |
| `useCache` | 将其设置为 true 后，将会导致本条语句的结果被二级缓存缓存起来，默认值:对 select 元素为 true。 |
| `timeout` | 这个设置是在抛出异常之前，驱动程序等待数据库返回请求结果的秒数。默认值为未设置(unset)(依赖数据库驱动)。 |
| `fetchSize` | 这是一个给驱动的建议值，尝试让驱动程序每次批量返回的结果行数等于这个设置值。 默认值为未设置(unset)(依赖驱动)。 |
| `statementType` | 可选 STATEMENT，PREPARED 或 CALLABLE。这会让 MyBatis 分别使用 Statement，PreparedStatement 或 CallableStatement，默认值:PREPARED。 |
| `resultSetType` | FORWARD_ONLY，SCROLL_SENSITIVE, SCROLL_INSENSITIVE 或 DEFAULT(等价于 unset) 中的一个，默认值为 unset (依赖数据库驱动)。 |
| `databaseId` | 如果配置了数据库厂商标识(databaseIdProvider)，MyBatis 会加载所有不带 databaseId 或匹配当前 databaseId 的语句；如果带和不带的语句都有，则不带的会被忽略。 |
| `resultOrdered` | 这个设置仅针对嵌套结果 select 语句:如果为 true，将会假设包含了嵌套结果集或是分组，当返回一个主结果行时，就不会产生对前面结果集的引用。 这就使得在获取嵌套结果集的时候不至于内存不够用。默认值:false。 |
| `resultSets` | 这个设置仅适用于多结果集的情况。它将列出语句执行后返回的结果集并赋予每个结果集一个名称，多个名称之间以逗号分隔。 |

#### insert,update和delete
* 例子:
    ```xml
    <insert id="insertAuthor"
            parameterType="domain.blog.Author"
            flushCache="true"
            statementType="PREPARED"
            keyProperty=""
            keyColumn=""
            useGeneratedKeys=""
            timeout="20">

    <update id="updateAuthor"
            parameterType="domain.blog.Author"
            flushCache="true"
            statementType="PREPARED"
            timeout="20">

    <delete id="deleteAuthor"
            parameterType="domain.blog.Author"
            flushCache="true"
            statementType="PREPARED"
            timeout="20">
    ```
* Insert,Update,Delete元素的属性

| 属性 | 描述 |
| --- | --- |
| `id` | 在命名空间中唯一的标识符，可以被用来引用这条语句。 |
| `parameterType` | 将会传入这条语句的参数的类全限定名或别名。这个属性是可选的，因为 MyBatis 可以通过类型处理器(TypeHandler)推断出具体传入语句的参数，默认值为未设置(unset)。 |
| `flushCache` | 将其设置为 true 后，只要语句被调用，都会导致本地缓存和二级缓存被清空，默认值:(对 insert、update 和 delete 语句)true。 |
| `timeout` | 这个设置是在抛出异常之前，驱动程序等待数据库返回请求结果的秒数。默认值为未设置(unset)(依赖数据库驱动)。 |
| `statementType` | 可选 STATEMENT，PREPARED 或 CALLABLE。这会让 MyBatis 分别使用 Statement，PreparedStatement 或 CallableStatement，默认值:PREPARED。 |
| `useGeneratedKeys` | (仅适用于 insert 和 update)这会令 MyBatis 使用 JDBC 的 getGeneratedKeys 方法来取出由数据库内部生成的主键(比如:像 MySQL 和 SQL Server 这样的关系型数据库管理系统的自动递增字段)，默认值:false。 |
| `keyProperty` | (仅适用于 insert 和 update)指定能够唯一识别对象的属性，MyBatis 会使用 getGeneratedKeys 的返回值或 insert 语句的 selectKey 子元素设置它的值，默认值:未设置(unset)。如果生成列不止一个，可以用逗号分隔多个属性名称。 |
| `keyColumn` | (仅适用于 insert 和 update)设置生成键值在表中的列名，在某些数据库(像 PostgreSQL)中，当主键列不是表中的第一列的时候，是必须设置的。如果生成列不止一个，可以用逗号分隔多个属性名称。 |
| `databaseId` | 如果配置了数据库厂商标识(databaseIdProvider)，MyBatis 会加载所有不带 databaseId 或匹配当前 databaseId 的语句；如果带和不带的语句都有，则不带的会被忽略。 |

* insert示例
    ```xml
    <insert id="insertAuthor" useGeneratedKeys="true" keyProperty="id">
        insert into Author (id,username,password,email,bio)
        values (#{id},#{username},#{password},#{email},#{bio})
    </insert>
    ```
    * 如果数据库支持自动生成主键的字段，设置`useGeneratedKeys=”true”`，然后再把`keyProperty`设置为目标属性就OK了。
    * 如果你的数据库还支持多行插入, 你也可以传入一个`Author`数组或集合，并返回自动生成的主键。
    ```xml
    <insert id="insertAuthor" useGeneratedKeys="true" keyProperty="id">
        insert into Author (username, password, email, bio) values
        <foreach item="item" collection="list" separator=",">
            (#{item.username}, #{item.password}, #{item.email}, #{item.bio})
        </foreach>
    </insert>
    ```
* update示例
    ```xml
    <update id="updateAuthor">
        update Author set
            username = #{username},
            password = #{password},
            email = #{email},
            bio = #{bio}
        where id = #{id}
    </update>
    ```
* delete示例
    ```xml
    <delete id="deleteAuthor">
        delete from Author where id = #{id}
    </delete>
    ```

##### sql
* 这个元素可以用来定义可重用的SQL代码片段，以便在其它语句中使用。
* 参数可以静态地(在加载的时候)确定下来，并且可以在不同的`include`元素中定义不同的参数值
    ```xml
    <sql id="userColumns"> ${alias}.id,${alias}.username,${alias}.password </sql>
    ```
* 这个SQL片段可以在其它语句中使用
    ```xml
    <select id="selectUsers" resultType="map">
        select
            <include refid="userColumns"><property name="alias" value="t1"/></include>,
            <include refid="userColumns"><property name="alias" value="t2"/></include>
        from some_table t1 cross join some_table t2
    </select>
    ```
* 也可以在`include`元素的`refid`属性或内部语句中使用属性值
    ```xml
    <sql id="sometable">
        ${prefix}Table
    </sql>

    <sql id="someinclude">
        from
            <include refid="${include_target}"/>
    </sql>

    <select id="select" resultType="map">
        select field1, field2, field3
            <include refid="someinclude">
                <property name="prefix" value="Some"/>
                <property name="include_target" value="sometable"/>
            </include>
    </select>
    ```

#### 参数
* 之前见到的所有语句都使用了简单的参数形式，你都不需要使用复杂的参数。
    ```xml
    <select id="selectUsers" resultType="User">
        select id, username, password
        from users
        where id = #{id}
    </select>
    ```
    * 参数类型会被自动设置为int，这个参数可以随意命名。
* 如果传入一个复杂的对象
    ```xml
    <insert id="insertUser" parameterType="User">
        insert into users (id, username, password)
        values (#{id}, #{username}, #{password})
    </insert>
    ```
    * 如果`User`类型的参数对象传递到了语句中，会查找`id`、`username`和`password`属性，然后将它们的值传入预处理语句的参数中。

#### 结果映射
#### 自动映射
#### 缓存

### mybatis-spring