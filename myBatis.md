- [mybatis](#mybatis)
    * [从XML中构建SqlSessionFactory](#从XML中构建SqlSessionFactory)
    * [不使用XML构建SqlSessionFactory](#不使用XML构建SqlSessionFactory)
    * [从SqlSessionFactory中获取SqlSession](#从SqlSessionFactory中获取SqlSession)
- [mybatis-spring](#mybatis-spring)

### mybatis
* 每个基于MyBatis的应用都是以一个`SqlSessionFactory`的实例为核心的。
* `SqlSessionFactory`的实例可以通过`SqlSessionFactoryBuilder`获得。
* 而`SqlSessionFactoryBuilder`则可以从XML配置文件或一个预先配置的Configuration实例来构建出`SqlSessionFactory`实例。

#### 从XML中构建SqlSessionFactory
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

#### 不使用XML构建SqlSessionFactory
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

#### 从SqlSessionFactory中获取SqlSession

### mybatis-spring