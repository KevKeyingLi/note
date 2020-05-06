- [XML映射器](#XML映射器)
    * [select](#select)
    * [insert](#insert)
    * [update](#update)
    * [delete](#delete)
    * [sql](#sql)

---
* Refs:
    * > https://mybatis.org/mybatis-3/zh/sqlmap-xml.html#insert_update_and_delete
---

### XML映射器
* SQL映射文件只有很少的几个顶级元素
    * cache – 该命名空间的缓存配置。
    * cache-ref – 引用其它命名空间的缓存配置。
    * resultMap – 描述如何从数据库结果集中加载对象，是最复杂也是最强大的元素。
    * parameterMap – 老式风格的参数映射。此元素已被废弃，并可能在将来被移除！
    * insert – 映射插入语句。
    * update – 映射更新语句。
    * delete – 映射删除语句。
    * select – 映射查询语句。

#### select
* 一个简单查询的select元素
    ```xml
    <select id="selectPerson" parameterType="int" resultType="hashmap">
        SELECT * FROM PERSON WHERE ID = #{id}
    </select>
    ```
    * 接受一个int类型的参数，并返回一个HashMap类型的对象。
    * `#{id}`告诉MyBatis创建一个预处理语句(`PreparedStatement`)参数。
* select元素允许你配置很多属性来配置每条语句的行为细节。
    ```xml
    <select
        id="selectPerson"
        parameterType="int"
        parameterMap="deprecated"
        resultType="hashmap"
        resultMap="personResultMap"
        flushCache="false"
        useCache="true"
        timeout="10"
        fetchSize="256"
        statementType="PREPARED"
        resultSetType="FORWARD_ONLY">
    ```
    * id
        * 在命名空间中唯一的标识符，可以被用来引用这条语句。
    * parameterType
        * 将会传入这条语句的参数的类全限定名或别名。这个属性是可选的，因为MyBatis可以通过类型处理器`TypeHandler`推断出具体传入语句的参数，默认值为未设置(unset)。
    * resultType
        * 期望从这条语句中返回结果的类全限定名或别名。注意，如果返回的是集合，那应该设置为集合包含的类型，而不是集合本身的类型。`resultType`和`resultMap`之间只能同时使用一个。
    * resultMap
        * 对外部`resultMap`的命名引用。结果映射是MyBatis最强大的特性，如果你对其理解透彻，许多复杂的映射问题都能迎刃而解。`resultType`和`resultMap`之间只能同时使用一个。
    * flushCache
        * 将其设置为true后，只要语句被调用，都会导致本地缓存和二级缓存被清空，默认值：false。
    * useCache
        * 将其设置为true后，将会导致本条语句的结果被二级缓存缓存起来，默认值：对select元素为true。
    * timeout
        * 这个设置是在抛出异常之前，驱动程序等待数据库返回请求结果的秒数。默认值为未设置(依赖数据库驱动)。
    * fetchSize	
        * 这是一个给驱动的建议值，尝试让驱动程序每次批量返回的结果行数等于这个设置值。 默认值为未设置(依赖驱动)。
    * statementType
        * 可选`STATEMENT`，`PREPARED`或`CALLABLE`。这会让MyBatis分别使用`Statement`，`PreparedStatement`或`CallableStatement`，默认值：`PREPARED`。
    * resultSetType
        `FORWARD_ONLY`，`SCROLL_SENSITIVE`, `SCROLL_INSENSITIVE`或`DEFAULT`(等价于unset)中的一个，默认值为unset(依赖数据库驱动)。
    * databaseId
        * 如果配置了数据库厂商标识(databaseIdProvider)，MyBatis会加载所有不带`databaseId`或匹配当前`databaseId`的语句；如果带和不带的语句都有，则不带的会被忽略。
    * resultOrdered
        * 这个设置仅针对嵌套结果select语句：如果为true，将会假设包含了嵌套结果集或是分组，当返回一个主结果行时，就不会产生对前面结果集的引用。这就使得在获取嵌套结果集的时候不至于内存不够用。默认值：false。
    * resultSets
        * 这个设置仅适用于多结果集的情况。它将列出语句执行后返回的结果集并赋予每个结果集一个名称，多个名称之间以逗号分隔。


#### insert
* insert元素允许你配置很多属性来配置每条语句的行为细节。
    ```xml
    <insert
        id="insertAuthor"
        parameterType="domain.blog.Author"
        flushCache="true"
        statementType="PREPARED"
        keyProperty=""
        keyColumn=""
        useGeneratedKeys=""
        timeout="20">
    ```
    * id
        * 在命名空间中唯一的标识符，可以被用来引用这条语句。
    * parameterType
        * 将会传入这条语句的参数的类全限定名或别名。这个属性是可选的，因为MyBatis可以通过类型处理器(`TypeHandler`)推断出具体传入语句的参数，默认值为未设置。
    * flushCache
        * 将其设置为true后，只要语句被调用，都会导致本地缓存和二级缓存被清空，默认值：对insert、update和delete语句true。
    * timeout
        * 这个设置是在抛出异常之前，驱动程序等待数据库返回请求结果的秒数。默认值为未设置(依赖数据库驱动)。
    * statementType
        * 可选`STATEMENT`，`PREPARED`或`CALLABLE`。这会让MyBatis分别使用`Statement`，`PreparedStatement`或`CallableStatement`，默认值：`PREPARED`。
    * useGeneratedKeys
        * 这会令MyBatis使用JDBC的`getGeneratedKeys`方法来取出由数据库内部生成的主键(比如：像MySQL和SQL Server这样的关系型数据库管理系统的自动递增字段)，默认值：false。
    * keyProperty
        * 指定能够唯一识别对象的属性，MyBatis会使用`getGeneratedKeys`的返回值或insert语句的`selectKey`子元素设置它的值，默认值：未设置。如果生成列不止一个，可以用逗号分隔多个属性名称。
    * keyColumn
        * 设置生成键值在表中的列名，在某些数据库(像PostgreSQL)中，当主键列不是表中的第一列的时候，是必须设置的。如果生成列不止一个，可以用逗号分隔多个属性名称。
    * databaseId
        * 如果配置了数据库厂商标识(`databaseIdProvider`)，MyBatis 会加载所有不带`databaseId`或匹配当前`databaseId`的语句；如果带和不带的语句都有，则不带的会被忽略。
* insert例子
    ```xml
    <insert id="insertAuthor">
        insert into Author (id,username,password,email,bio)
        values (#{id},#{username},#{password},#{email},#{bio})
    </insert>
    ```
* 在插入语句里面有一些额外的属性和子元素用来处理主键的生成，并且提供了多种生成方式。
    1. 如果你的数据库支持自动生成主键的字段(比如MySQL和SQL Server)，那么你可以设置`useGeneratedKeys=”true”`，然后再把`keyProperty`设置为目标属性就OK 了。
        ```xml
        <insert id="insertAuthor" 
            useGeneratedKeys="true"
            keyProperty="id">
            insert into Author (username,password,email,bio)
            values (#{username},#{password},#{email},#{bio})
        </insert>
        ```
    2. 如果你的数据库还支持多行插入, 你也可以传入一个Author数组或集合，并返回自动生成的主键。
        ```xml
        <insert id="insertAuthor" 
            useGeneratedKeys="true"
            keyProperty="id">
            insert into Author (username, password, email, bio) values
            <foreach item="item" collection="list" separator=",">
                (#{item.username}, #{item.password}, #{item.email}, #{item.bio})
            </foreach>
        </insert>
        ```
    

#### update
* update元素允许你配置很多属性来配置每条语句的行为细节。
    ```xml
    <update
        id="updateAuthor"
        parameterType="domain.blog.Author"
        flushCache="true"
        statementType="PREPARED"
        timeout="20">
    ```
    * id
        * 在命名空间中唯一的标识符，可以被用来引用这条语句。
    * parameterType
        * 将会传入这条语句的参数的类全限定名或别名。这个属性是可选的，因为MyBatis可以通过类型处理器(`TypeHandler`)推断出具体传入语句的参数，默认值为未设置。
    * flushCache
        * 将其设置为true后，只要语句被调用，都会导致本地缓存和二级缓存被清空，默认值：对insert、update和delete语句true。
    * timeout
        * 这个设置是在抛出异常之前，驱动程序等待数据库返回请求结果的秒数。默认值为未设置(依赖数据库驱动)。
    * statementType
        * 可选`STATEMENT`，`PREPARED`或`CALLABLE`。这会让MyBatis分别使用`Statement`，`PreparedStatement`或`CallableStatement`，默认值：`PREPARED`。
    * useGeneratedKeys
        * 这会令MyBatis使用JDBC的`getGeneratedKeys`方法来取出由数据库内部生成的主键(比如：像MySQL和SQL Server这样的关系型数据库管理系统的自动递增字段)，默认值：false。
    * keyProperty
        * 指定能够唯一识别对象的属性，MyBatis会使用`getGeneratedKeys`的返回值或insert语句的`selectKey`子元素设置它的值，默认值：未设置。如果生成列不止一个，可以用逗号分隔多个属性名称。
    * keyColumn
        * 设置生成键值在表中的列名，在某些数据库(像PostgreSQL)中，当主键列不是表中的第一列的时候，是必须设置的。如果生成列不止一个，可以用逗号分隔多个属性名称。
    * databaseId
        * 如果配置了数据库厂商标识(`databaseIdProvider`)，MyBatis 会加载所有不带`databaseId`或匹配当前`databaseId`的语句；如果带和不带的语句都有，则不带的会被忽略。
* update例子
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


#### delete
* delete元素允许你配置很多属性来配置每条语句的行为细节。
    ```xml
    <delete
        id="deleteAuthor"
        parameterType="domain.blog.Author"
        flushCache="true"
        statementType="PREPARED"
        timeout="20">
    ```
    * id
        * 在命名空间中唯一的标识符，可以被用来引用这条语句。
    * parameterType
        * 将会传入这条语句的参数的类全限定名或别名。这个属性是可选的，因为MyBatis可以通过类型处理器(`TypeHandler`)推断出具体传入语句的参数，默认值为未设置。
    * flushCache
        * 将其设置为true后，只要语句被调用，都会导致本地缓存和二级缓存被清空，默认值：对insert、update和delete语句true。
    * timeout
        * 这个设置是在抛出异常之前，驱动程序等待数据库返回请求结果的秒数。默认值为未设置(依赖数据库驱动)。
    * statementType
        * 可选`STATEMENT`，`PREPARED`或`CALLABLE`。这会让MyBatis分别使用`Statement`，`PreparedStatement`或`CallableStatement`，默认值：`PREPARED`。
    * databaseId
        * 如果配置了数据库厂商标识(`databaseIdProvider`)，MyBatis 会加载所有不带`databaseId`或匹配当前`databaseId`的语句；如果带和不带的语句都有，则不带的会被忽略。
* delete例子
    ```xml
    <delete id="deleteAuthor">
        delete from Author where id = #{id}
    </delete>
    ```


#### sql
* 这个元素可以用来定义可重用的SQL代码片段

















