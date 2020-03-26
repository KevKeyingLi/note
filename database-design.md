# Database Design Fundamentals

- [Fundamental Concept](#Fundamental-Concept)
    * [Database management systems (DBMS)](#Database-management-systems-(DBMS))
    * [Characteristics of the Database Approach](#Characteristics-of-the-Database-Approach)
    * [Benefits of the Database Approach](#Benefits-of-the-Database-Approach)
- [Data Modeling](#Data-Modeling)
    * [Introduction to Data Models](#Introduction-to-Data-Models)
    * [Schemas and Instances](#Schemas-and-Instances)
        * [Database schema](#Database-schema)
        * [Database instance](#Database-instance)
    * [The Three-Schema Architecture](#The-Three-Schema-Architecture)
    * [Data Independence](#Data-Independence)
    * [Classification of Database Management Systems](#Classification-of-Database-Management-Systems)
- [Entity-Relationship Data Model](#Entity-Relationship-Data-Model)
    * [Intro to Entity-Relationship Model](#Intro-to-Entity-Relationship-Model)
    * [Entities, Entity Sets and Entity Types](#Entities-Entity-Sets-and-Entity-Types)
    * [Attributes](#Attributes)
    * [Keys](#Keys)
    * [Relationships, Relationship Sets and Relationship Types](#Relationships-Relationship-Sets-and-Relationship-Types)
    * [Binary Relationship Type Constraints](#Binary-Relationship-Type-Constraints)
    * [Attributes of Relationship Types](#Attributes-of-Relationship-Types)

## Fundamental Concept
### Database management systems (DBMS)
* If an organization wants to adopt the database approach, then it needs a collection of programs that enable the users of the organization to create and maintain databases and control all access to them. This is achieved using a database management system (DBMS). The primary goal of a DBMS is to provide an environment that is both convenient and efficient for users to retrieve and store information.
* Facilities provided by a DBMS:
    * Defining a database involves defining the data types, structures, and constraints of the data to be stored in the database.
    * Constructing the database is the process of storing the data on a storage device that is controlled by the DBMS.
    * Manipulating a database involves querying the database to retrieve specific data, updating the database, etc.
    * Sharing a database allows multiple users and programs to access the database simultaneously.
* Other important terms
    * Furthermore, in order to access the database, we use application programs which send queries (requests) for data to the DBMS. A query causes some data to be retrieved. DBMS software a database system.
    * Example:
        * With the database approach, we can have the traditional banking system as shown in the diagram below:
        ```
        +-----------------------+     +------------------------------------------------------------------------------------------------------------------------+
        | Accounting department |--+  | Database Systems            +------------------------------------------------------------------+                       |
        +-----------------------+  |  |                             | DBMS software                                                    |                       |
        +----------------------+   |  |   +----------------------+  |   +-----------------------------+   +-------------------------+  |   +-----------------+ |
        | Personnel department |---+--+-->| Application programs |--+-->| Software to process queries |-->| Software to access data |--+-->| Stored database | |
        +----------------------+   |  |   +----------------------+  |   +-----------------------------+   +-------------------------+  |   +-----------------+ |
        +-----------------+        |  |                             +------------------------------------------------------------------+                       |
        | Loan department |--------+  |                                                                                                                        |
        +-----------------+           +------------------------------------------------------------------------------------------------------------------------+
        ```
        * The individuals in the different departments use application programs to request the DBMS to retrieve the particular data that each individual is interested in. Only the application programs are visible to the end-user and they allow them to communicate with the DBMS. Then the DBMS, consisting of different software, fetches/stores the data from the database.
            * For example, an accountant in the accounting department wants information regarding the number of outstanding accounts. So he/she will send a request to the DBMS through the application software. After the DBMS retrieves the information from the database, the results will be displayed through the application program.

### Characteristics of the Database Approach
* Several characteristics distinguish the database approach from the file-based system.
    * Self-describing nature of a database system
        * A database system is self-describing because it not only contains the database itself, but also the meta-data which defines and describes the data and relationships between tables in the database. This information is stored in a catalog by the DBMS software. The separation of data and information about the data makes the database system different from the traditional file-based system in which the data definition is part of the application programs.
    * Insulation between program and data
        * In the file-based system, the structure of the data files is defined in the application programs so if a user wants to change the structure of a file, all the programs that access that file might need to be changed as well.
        * In the database approach, the data structure is stored in the system catalog and not in the programs. Therefore, one change is all that is needed to change the structure of a file. This insulation between the programs and data is also called program-data independence.
    * Support for multiple views of data
        *  A view is a subset of the database, which is defined and dedicated for particular users of the system. Multiple users in the system might have different views of the system. Each view might contain only the data of interest to a user or group of users.
    * Sharing data and multiuser system
        * Current database systems are designed for multiple users. That is, they allow many users to access the same database at the same time. This access is achieved through features called concurrency control strategies. These strategies ensure that the data accessed are always correct and that data integrity is maintained. Consider the case when multiple reservation agents try to assign a seat on an airline flight, the DBMS should ensure that each seat can be accessed by only one agent at a time so that the same seat is not assigned to multiple passengers.

### Benefits of the Database Approach
* Control of data redundancy
    * In the database approach, ideally, each data item is stored in only one place in the database. This is known as data normalization, and it ensures consistency and saves storage space. In some cases, data redundancy still exists to improve system performance but it is kept to a minimum.
* Data sharing
    * The integration of all the data for an organization within a database system has many advantages. First, it allows for data sharing among employees and others who have access to the system. Second, it gives users the ability to generate more information from a given amount of data than would be possible without the integration.
* Enforcement of integrity constraints #
    * Database management systems must be able to define and enforce certain constraints to ensure that users enter valid information and maintain data integrity. A database constraint is a restriction or rule that dictates what can be entered or edited in a table, such as adding a valid course name in the Course_Name column in the COURSE table.
    * There are many types of database constraints. One of them is data type, which determines the sort of data that can be entered. For example, integers only. Another restraint is data uniqueness, which specifies that data item values must be unique, such as every record in the STUDENT table must have a unique value for ID.
* Restriction of unauthorized access
    * Not all users of a database system will have the same access privileges. For example, one user might have read-only access, while another might have read and write privileges. For this reason, a database management system should provide a security subsystem to create and control different types of user accounts and restrict unauthorized access.
* Backup and recovery facilities #
    *Backup and recovery are methods that allow you to protect your data from loss. The database system provides a facility for backing up and recovering data. If a hard drive fails and the database stored on the hard drive is not accessible, the only way to recover the database is with a backup.
    * If a computer system fails in the middle of a complex update process, the recovery subsystem is responsible for making sure that the database is restored to its original state.

## Data Modeling
### Introduction to Data Models
* In order to store data in a database system, we need some data-structures. Hence the database systems we use normally include some complex data structures which we normally do not use. To make the system efficient in terms of data retrieval, and reduce complexity in terms of usability, developers use data abstraction i.e., hide irrelevant details from the users. In order to achieve this abstraction, we use data models.
* A data model is a collection of concepts or notations for describing data, data relationships, data semantics, and data constraints.
* Most data models also include a set of basic operations for manipulating data in the database.
* Types of data models
    1. High-level conceptual data models
        * High-level conceptual data models provide a way to present data that is similar to how people perceive data. 
        1. Entity relationship model
            * An entity represents a real-world object such as an employee or a project. The entity has attributes that represent properties such as an employee’s name, address, and birthdate. A relationship represents an association among entities; for example, an employee works on many projects. A relationship exists between the employee and each project.
    2. Record-based logical data models
        * Record-based logical data models provide concepts users can understand but are still similar to the way data is stored on the computer. 
        1. Hierarchical model
            * In a hierarchical model, data is organized into a tree-like structure, implying a single parent for each record. This structure mandates that each child record has only one parent, whereas each parent record can have one or more child records.
        2. Network model
            * The network model expands upon the hierarchical structure, allowing each record to have multiple parent and child records, forming a generalized graph structure. It was the most popular model before being replaced by the relational model.
        3. Relational model
            * The relational model represents data as relations or tables. For example, the university database system contains multiple tables (relations) which in turn have several attributes (columns) and tuples (rows).
    3. Physical data models
        * The physical data model represents how data is stored in computer memory, how it is scattered and ordered in the memory, and how it would be retrieved from memory. Basically physical data model represents each table, its columns, and specifications, etc. It also highlights how tables are built and related to each other in the database. 

### Schemas and Instances
#### Database schema
* A schema is the blueprint of a database. The names of tables, columns of each table, datatype, functions, and other objects are included in the schema.
* We use the schema diagram to display the schema of a database. The schema diagram for the university database can be seen below:
    * Student Table: ID, First_Name, Last_Name, Class, Major
    * Course Table: Course_ID, Course_Name, Course_credits
    * Department Table: Department_Code, Department_Name
    * Instructor Table: Instructor_ID, Instructor_fname, Department_Code
    * Grade Table: ID, Course_ID, Grade
* The schema diagram displays only certain aspects of a schema, such as the names of record types and data items. Other features (although present in the schema) are not specified in the schema diagram; 
    * for example, the above diagram shows neither the data type of each item nor the relationships among the various tables.
* On another note, the schema is not changed frequently, sometimes changes need to be applied to the schema as the requirements of the application change. 
    * fFor example, we may decide to add another data item to each record in a table, such as adding an Address field to the Student schema. This schema modification or alteration is known as schema revolution.

#### Database instance
* An instance is the information collected in a database at some specific moment in time, also known as the database state. It is a snapshot of the current state or occurrence of a database. Each time data is inserted into or deleted from the database, it changes the state of the database. That is the reason why an instance of the database changes more often.
* The starting state of the database is acquired when the database is first loaded with initial data. From then onwards, each time data is updated we get a new database instance. At any point in time, there is a current state associated with a database.

### The Three-Schema Architecture
* The goal of the three-schema architecture is to separate the user applications from the physical database.
    ```
    +----------------------------------------+   +--------------------------------------+   +----------------------------------+   +-----------------+
    | External Level (View 1...n)(End Users) |-->| Conceptual Level (Conceptual Schema) |-->| Internal Level (Internal Schema) |-->| Stored Database |
    +----------------------------------------+   +--------------------------------------+   +----------------------------------+   +-----------------+
    ```
    1. External schema
        * An external schema describes the part of the database that a specific user is interested in. It hides the unrelated details of the database from the user like the exact process of retrieving or storing data from the database. There is a different external view for each user of the database.
        * An external view is just the content of the database as it is seen by one particular user. For example, a user from the sales department will only see sales-related data.
    2. Conceptual schema
        * The conceptual schema describes the database structure of the whole database for the community of users. This schema hides information about the physical storage structures and focuses on describing data types, entities, relationships, etc. Usually, a record-based logical data model is used to describe the conceptual schema when a database system is implemented.
    3. Internal schema
        * The internal schema describes how the database is stored on physical storage devices such as hard drives. The internal schema uses a physical data model and describes the complete details of data storage and access paths for the database.

### Data Independence
* Data independence is defined as a property of the database management system that helps to change the database schema at one level of a database system without changing the schema at the next highest level. The three-schema architecture can be used to further explain the concept of data independence. 
* Types of data independence:
    * Logical data independence
        * The ability to change the conceptual schema without changing the external schema or user view is called logical data independence. For example, the addition or removal of new entities, attributes or relationships to this conceptual schema should be possible without having to change existing external schemas or rewriting existing application programs.
    * Physical data independence
        * Physical data independence helps you to separate the conceptual schema from the internal schema. It allows you to provide a logical description of the database without the need to specify physical structures.
* Does data independence work in reality?
    * Generally, physical data independence exists in most databases and file environments where physical details, such as the exact location of data on disk or the type of storage device, are hidden from the user. On the other hand, logical data independence is harder to achieve because it must accommodate changes in the structure of the database without affecting application programs; which is a much stricter requirement.

### Classification of Database Management Systems
* Classification based on data model
    * The most popular data model in use today is the relational data model.
    * Other traditional models, such as hierarchical data models and network data models, are still used in the industry mainly on mainframe platforms. They are not commonly used due to their complexity.
    * object-oriented data models were introduced. This model is a database management system in which information is represented in the form of objects as used in object-oriented programming. Object-oriented databases are different from relational databases, which are table-oriented. Object-oriented database management systems (OODBMS) combine database capabilities with object-oriented programming language capabilities.
* Classification based on number of users
    * A DBMS can be classified based on the number of users it supports. It can be a single-user database system, which supports one user at a time, or a multi-user database system, which supports multiple users concurrently.
* Classification based on database distribution
    * Centralized systems
        * Within a centralized database system, DBMS and database are central. i.e., stored at a single location and is used by several other systems. 
    * Distributed database system
        * In a distributed database system, the actual database and the DBMS software are distributed across various sites that are connected by a computer network.
        * Homogeneous distributed database systems
            * Homogeneous distributed database systems use the same DBMS software from multiple sites. Data exchanged between these various sites can be handled easily. For example, library information systems by the same vendor, such as Geac Computer Corporation, use the same DBMS software which allows easy data exchange between the various Geac library sites.
        * Heterogeneous distributed database systems
            * In a heterogeneous distributed database system, different sites might use different DBMS software, but there is additional common software to support data exchange between these sites. For example, the various library database systems use the same machine-readable cataloging (MARC) format to support library record data exchange.

## Entity-Relationship Data Model
### Intro to Entity-Relationship Model
* The entity-relationship (ER) data model is a high-level conceptual data model. It is well suited to data modeling for use with databases because it is fairly abstract and is easy to discuss and explain.
* ER modeling is based on two concepts:
    * Entities, defined as tables that hold specific information (data).
    * Relationships, defined as the associations or interactions between entities.

### Entities, Entity Sets and Entity Types
* What is an entity?
    * An entity is an object in the real world with an independent existence that can be differentiated from other objects. 
    * Each entity has attributes which are the particular properties that describe it.
    * The attribute values that describe each entity become a major part of the data stored in the database.
* Entity types and entity sets
    * A database usually contains groups of entities that are similar.
    * An entity type defines a collection (or set) of entities that have the same attributes. Each entity type in the database is described by its name and attributes.
    * The collection of all entities of a particular entity type in the database at any point in time is called an entity set. The entity set is usually referred to using the same name as the entity type, even though they are two separate concepts.

### Attributes
* Each entity is described by a set of attributes. Each attribute has a name, is associated with an entity, and has a range of values it is allowed to take.
* Types of attributes
    * Simple attributes
        * Simple attributes are the atomic value, i.e., they cannot be further divided.
    * Composite attributes
        * Composite attributes can be divided into smaller subparts, which represent more basic attributes with independent meanings. Therefore, composite attributes consist of a hierarchy of attributes.
    * Multivalued attributes
        * Multivalued attributes have a set of values for each entity.
        * An example of a multivalued attribute from the COMPANY database; one employee may not have any college degrees, another may have one, and a third person may have two or more degrees. 
    * Derived attributes
        * Derived attributes are attributes that contain values calculated from other attributes.
            * An example from the COMPANY database is that Age can be derived from the attribute Bdate. 

### Keys
* Key attributes of an entity type
    * An important constraint on the entities of an entity type is the key or uniqueness constraint on attributes. An entity type usually has one or more attributes whose values are distinct for each individual entity in the entity set. Such an attribute is called a key attribute, and its values can be used to identify each entity uniquely.
    * Specifying that an attribute is a key of an entity type means that the preceding uniqueness property must hold for every entity set of the entity type. 
    * This unique attribute is also known as the primary key.
    * Composite keys
        * Sometimes a single attribute is not enough to uniquely identify each entity within an entity set.
        * If a set of attributes possesses this property, the proper way to represent this in the ER model that we describe here is to define a composite attribute and designate it as a key attribute of the entity type. This is called a composite key.

### Relationships, Relationship Sets and Relationship Types
* A relationship is an association between two entities, for example, an entity EMPLOYEE WORKS_ON PROJECT, which is another entity. 
* So we can say that relationship type is simply the relationship that exists between two entities like WORKS_ON. 
* While the set of similar associations at a point of time is called the Relationship Set.

### Degrees of Relationship Types
* Degrees of relationship types
    * The degree of a relationship type is the number of participating entities types. 
    * The Unary (recursive) relationship type
        * The unary relationship type involves only one entity type. However, the same entity type participates in the relationship type in different roles. 
            * For example, The SUPERVISES relationship type relates an employee to a supervisor, where both employee and supervisor entities are members of the same EMPLOYEE entity set. Hence, the EMPLOYEE entity type participates twice in SUPERVISION: once in the role of supervisor, and once in the role of the supervisee.
    * The Binary relationship type
        * This relationship type has two entity types linked together. This is the most common relationship type. 
            * For example, consider a relationship type WORKS_ON between the two entity types EMPLOYEE and PROJECT, which associates each employee with the project he/she is working on.
    * The Ternary relationship type
        * If there are three entity types linked together, the relationship is called a ternary relationship.

### Binary Relationship Type Constraints
* There are two main types of binary relationship constraints: mapping cardinality and participation.
    * Mapping cardinality
        * Mapping cardinality describes the maximum number of entities that a given entity can be associated with via a relationship. 
        * The one to one relationship
        * The one to many relationship
        * The many to many relationship
    * Participation
        * The participation constraint specifies whether the existence of an entity depends on it being related to another entity via the relationship type.
            * Total participation: 
                * This specifies that each entity in the entity set must compulsorily participate in at least one relationship instance in that relationship set. 
            * Partial participation: 
                * This specifies that each entity in the entity set may or may not participate in the relationship instance in that relationship set.

### Attributes of Relationship Types