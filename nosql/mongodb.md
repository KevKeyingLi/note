- [MongoDB简介](#MongoDB简介)
    * [MongoDB架构的关键组件](#MongoDB架构的关键组件)
    * [使用MongoDB的原因](#使用MongoDB的原因)
    * [MongoDB中的数据建模](#MongoDB中的数据建模)
    * [MongoDB和RDBMS之间的区别](#MongoDB和RDBMS之间的区别)
- [CRUD操作](#CRUD操作)
    * [insert文档](#insert文档)
    * [query文档](#query文档)
---
* refs:
    * > https://mongoing.com/mongodb-beginner-tutorial
---

### MongoDB简介
* MongoDB是面向文档的NoSQL数据库
* 每个数据库都包含集合，而集合又包含文档。每个文档可以具有不同数量的字段。每个文档的大小和内容可以互不相同。
* 文档结构更符合开发人员如何使用各自的编程语言构造其类和对象。开发人员经常会说他们的类不是行和列，而是具有键值对的清晰结构。
* 从NoSQL数据库的简介中可以看出，行(或在MongoDB中调用的文档)不需要预先定义架构。相反，可以动态创建字段。
* MongoDB中可用的数据模型使我们可以更轻松地表示层次结构关系，存储数组和其他更复杂的结构。
* 可伸缩性: MongoDB环境具有很高的可伸缩性。全球各地的公司已经定义了自己的集群，其中一些集群运行着100多个节点，数据库中包含大约数百万个文档。

#### MongoDB架构的关键组件
* `_id`: 这是每个MongoDB文档中必填的字段。`_id`字段表示MongoDB文档中的唯一值。`_id`字段类似于文档的主键。如果创建的新文档中没有`_id`字段，MongoDB将自动创建该字段。
* `集合`: 这是MongoDB文档的分组。集合等效于在任何其他RDMS中创建的表。集合存在于单个数据库中。集合不强制执行任何结构。
* `游标`: 这是指向查询结果集的指针。客户可以遍历游标以检索结果。
* `数据库`: 这是像RDMS中那样的集合容器，其中是表的容器。每个数据库在文件系统上都有其自己的文件集。MongoDB服务器可以存储多个数据库。
* `文档`: MongoDB集合中的记录基本上称为文档。文档包含字段名称和值。
* `字段`: 文档中的名称/值对。一个文档具有零个或多个字段。字段类似于关系数据库中的列。
* `JSON`: JavaScript Object Notation是一种轻量级的数据交换格式。是可读性高用于表示结构化数据的纯文本格式，用于表达结构化数据。

#### 使用MongoDB的原因
* `面向文档的`: 由于MongoDB是NoSQL类型的数据库，它不是以关系类型的格式存储数据，而是将数据存储在文档中。这使得MongoDB非常灵活，可以适应实际的业务环境和需求。
* `临时查询`: MongoDB支持按字段，范围查询和正则表达式搜索。可以查询返回文档中的特定字段。
* `索引`: 可以创建索引以提高MongoDB中的搜索性能。MongoDB文档中的任何字段都可以建立索引。
* `复制`: MongoDB可以提供副本集的高可用性。副本集由两个或多个mongo数据库实例组成。每个副本集成员可以随时充当主副本或辅助副本的角色。主副本是与客户端交互并执行所有读/写操作的主服务器。辅助副本使用内置复制维护主数据的副本。当主副本发生故障时，副本集将自动切换到辅助副本，然后它将成为主服务器。
* `负载平衡`: MongoDB使用分片的概念，通过在多个MongoDB实例之间拆分数据来水平扩展。MongoDB可以在多台服务器上运行，以平衡负载或复制数据，以便在硬件出现故障时保持系统正常运行。
 
#### MongoDB中的数据建模
* *应用程序的需求是什么*: 查看应用程序的业务需求，并查看应用程序所需的数据和数据类型。基于此，确保相应地确定文档的结构。
* *什么是数据检索模式*: 如果我们可能使用大量查询操作，则可以考虑在数据模型中使用索引来提高查询效率。
* *数据库中是否频繁发生插入，更新和删除操作*: 如果数据建模设计需要重新考虑使用索引或合并分片，以提高整体MongoDB环境的效率。

#### MongoDB和RDBMS之间的区别

| RDBMS | MongoDB | 区别 |
| --- | --- | --- |
| Table | Collection | 在RDBMS中，表包含用于存储数据的列和行，而在MongoDB中，此相同的结构称为集合。集合包含文档，文档又包含字段，字段又是键值对。 |
| Row | Document | 在RDBMS中，行表示表中单个隐式结构化的数据项。在MongoDB中，数据存储在文档中。 |
| Column | Field | 在RDBMS中，列表示一组数据值。在MongoDB中称为字段。 |
| Joins | Embedded documents | 在RDBMS中，有时会将数据分散在各个表中，并且为了显示所有数据的完整视图，有时会在表之间形成联接以获取数据。在MongoDB中，数据通常存储在单个集合中，但使用嵌入式文档将其分隔开。因此，MongoDB中没有联接的概念。 |


### CRUD操作
#### insert文档
* 如果需要操作的集合不存在，在执行insert操作时，mongoDB会自动创造一个所需collection
* 插入一个文档
    ```java
    Document canvas = new Document("item", "canvas")
            .append("qty", 100)
            .append("tags", singletonList("cotton"));
    Document size = new Document("h", 28)
            .append("w", 35.5)
            .append("uom", "cm");
    canvas.put("size", size);

    collection.insertOne(canvas);
    ```
* 插入多个文档
    ```java
    Document journal = new Document("item", "journal")
            .append("qty", 25)
            .append("tags", asList("blank", "red"));
    Document journalSize = new Document("h", 14)
            .append("w", 21)
            .append("uom", "cm");
    journal.put("size", journalSize);

    Document mat = new Document("item", "mat")
            .append("qty", 85)
            .append("tags", singletonList("gray"));
    Document matSize = new Document("h", 27.9)
            .append("w", 35.5)
            .append("uom", "cm");
    mat.put("size", matSize);

    Document mousePad = new Document("item", "mousePad")
            .append("qty", 25)
            .append("tags", asList("gel", "blue"));
    Document mousePadSize = new Document("h", 19)
            .append("w", 22.85)
            .append("uom", "cm");
    mousePad.put("size", mousePadSize);

    collection.insertMany(asList(journal, mat, mousePad));
    ```

#### query文档
* 查询集合中的所有文档
    ```java
    FindIterable<Document> findIterable = collection.find(new Document());
    ```
* 查询集合中字段满足特定条件的文档
    * 使用[询问运算符](#https://docs.mongodb.com/manual/reference/operator/query/#query-selectors)
        ```java
        gte(<field1>, <value1>)
        lt(<field2>, <value2>)
        eq(<field3>, <value3>)
        ...
        ```
    ```java
    findIterable = collection.find(eq("status", "D"));
    findIterable = collection.find(in("status", "A", "D"));
    ```
    * 多个条件的情况
        ```java
        findIterable = collection.find(and(eq("status", "A"), lt("qty", 30)));
        findIterable = collection.find(or(eq("status", "A"), lt("qty", 30)));
        findIterable = collection.find(and(eq("status", "A"), or(lt("qty", 30), regex("item", "^p"))));
        ```











