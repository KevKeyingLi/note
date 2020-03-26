# Grokking the System Design Interview

- [Key Characteristics of Distributed Systems](#Key-Characteristics-of-Distributed-Systems)
    * [Scalability](#Scalability)
    * [Reliability](#Reliability)
    * [Availability](#Availability)
    * [Reliability Vs. Availability](#Reliability-Vs-Availability)
    * [Efficiency](#Efficiency)
    * [Serviceability or Manageability](#Serviceability-or-Manageability)
- [Load Balancing](#Load-Balancing)
    * [Benefits of Load Balancing](#Benefits-of-Load-Balancing)
    * [Load Balancing Algorithms](#Load-Balancing-Algorithms)
- [Caching](#Caching)
    * [Application server cache](#Application-server-cache)
    * [Content Distribution Network (CDN)](#Content-Distribution-Network)
    * [Cache Invalidation](#Cache-Invalidation)
    * [Cache eviction policies](#Cache-eviction-policies)
- [Data Partitioning](#Data-Partitioning)
    * [Partitioning Methods](#Partitioning-Methods)
        * [Horizontal partitioning](#Horizontal-partitioning)
        * [Vertical Partitioning](#Vertical-Partitioning)
        * [Directory Based Partitioning](#Directory-Based-Partitioning)
    * [Partitioning Criteria](#Partitioning-Criteria)
    * [Common Problems of Data Partitioning](#Common-Problems-of-Data-Partitioning)
- [Indexes](#Indexes)
    * [Index Example](#Example-A-library-catalog)
    * [How do Indexes decrease write performance?](#How-do-Indexes-decrease-write-performance)
- [Proxies](#Proxies)
    * [Proxy Server Types](#Proxy-Server-Types)
- [Redundancy and Replication](#Redundancy-and-Replication)
- [SQL vs. NoSQL](#SQL-vs-NoSQL)
    * [SQL](#SQL)
    * [NoSQL](#NoSQL)
    * [SQL VS. NoSQL - Which one to use?](SQL-VS-NoSQL)
- [CAP Theorem](#CAP-Theorem)
- [Consistent Hashing](#Consistent-Hashing)
    * [What is Consistent Hashing?](#What-is-Consistent-Hashing?)
    * [How does Consistent Hashing work?](#How-does-Consistent-Hashing-work?)

## Key Characteristics of Distributed Systems
### Scalability
* Scalability is the capability of a system, process, or a network to grow and manage increased demand. 
    * reason for limiting the speed-up obtained by distribution
        * network speed may become slower because machines tend to be far apart from one another. 
        * some tasks may not be distributed, either because of their inherent atomic nature or because of some flaw in the system design.
    * Horizontal vs. Vertical Scaling
        * Horizontal scaling means that you scale by adding more servers into your pool of resources.
            * often easier to scale dynamically by adding more machines into the existing pool;
        * Vertical scaling means that you scale by adding more power (CPU, RAM, Storage, etc.) to an existing server.
            * usually limited to the capacity of a single server and scaling beyond that capacity often involves downtime and comes with an upper limit.

### Reliability
* By definition, reliability is the probability a system will fail in a given period. In simple terms, a distributed system is considered reliable if it keeps delivering its services even when one or several of its software or hardware components fail. 
    * A reliable distributed system achieves this through redundancy of both the software components and data.

### Availability
* By definition, availability is the time a system remains operational to perform its required function in a specific period. It is a simple measure of the percentage of time that a system, service, or a machine remains operational under normal conditions.
    * Reliability is availability over time considering the full range of possible real-world conditions that can occur. 

#### Reliability Vs. Availability
* If a system is reliable, it is available. 
* If it is available, it is not necessarily reliable.
* In other words, high reliability contributes to high availability, but it is possible to achieve a high availability even with an unreliable product by minimizing repair time and ensuring that spares are always available when they are needed. 

### Efficiency
* Two standard measures of its efficiency are the response time (or latency) that denotes the delay to obtain the first item and the throughput (or bandwidth) which denotes the number of items delivered in a given time unit (e.g., a second). The two measures correspond to the following unit costs:
    * Number of messages globally sent by the nodes of the system regardless of the message size.
    * Size of messages representing the volume of data exchanges.

### Serviceability or Manageability
* Serviceability or manageability is the simplicity and speed with which a system can be repaired or maintained.
* Things to consider for manageability are the ease of diagnosing and understanding problems when they occur, ease of making updates or modifications, and how simple the system is to operate.

## Load Balancing
* Typically a load balancer sits between the client and the server accepting incoming network and application traffic and distributing the traffic across multiple backend servers using various algorithms.
    * By balancing application requests across multiple servers, a load balancer reduces individual server load and prevents any one application server from becoming a single point of failure, thus improving overall application availability and responsiveness.
    * To utilize full scalability and redundancy, we can try to balance the load at each layer of the system. 
        * Between the user and the web server
        * Between web servers and an internal platform layer, like application servers or cache servers
        * Between internal platform layer and database.

### Benefits of Load Balancing
* Users experience faster, uninterrupted service. Users won’t have to wait for a single struggling server to finish its previous tasks. Instead, their requests are immediately passed on to a more readily available resource.
* Service providers experience less downtime and higher throughput. Even a full server failure won’t affect the end user experience as the load balancer will simply route around it to a healthy server.
* Load balancing makes it easier for system administrators to handle incoming requests while decreasing wait time for users.
* Smart load balancers provide benefits like predictive analytics that determine traffic bottlenecks before they happen. As a result, the smart load balancer gives an organization actionable insights. These are key to automation and can help drive business decisions.
* System administrators experience fewer failed or stressed components. Instead of a single device performing a lot of work, load balancing has several devices perform a little bit of work.

### Load Balancing Algorithms
* Load balancers consider two factors before forwarding a request to a backend server. 
    * They will first ensure that the server they choose is actually responding appropriately to requests
    * then use a pre-configured algorithm to select one from the set of healthy servers. 
* Health Checks
    * regularly attempt to connect to backend servers to ensure that servers are listening.
    * If a server fails a health check, it is automatically removed from the pool, and traffic will not be forwarded to it until it responds to the health checks again.
* algorithms:
    * Least Connection Method - This method directs traffic to the server with the fewest active connections. This approach is quite useful when there are a large number of persistent client connections which are unevenly distributed between the servers.
    * Least Response Time Method — This algorithm directs traffic to the server with the fewest active connections and the lowest average response time.
    * Least Bandwidth Method - This method selects the server that is currently serving the least amount of traffic measured in megabits per second (Mbps).
    * Round Robin Method — This method cycles through a list of servers and sends each new request to the next server. When it reaches the end of the list, it starts over at the beginning. It is most useful when the servers are of equal specification and there are not many persistent connections.
    * Weighted Round Robin Method — The weighted round-robin scheduling is designed to better handle servers with different processing capacities. Each server is assigned a weight (an integer value that indicates the processing capacity). Servers with higher weights receive new connections before those with less weights and servers with higher weights get more connections than those with less weights.
    * IP Hash — Under this method, a hash of the IP address of the client is calculated to redirect the request to a server.

## Caching
* caching will enable you to make vastly better use of the resources you already have as well as making otherwise unattainable product requirements feasible.
* Caches take advantage of the locality of reference principle: recently requested data is likely to be requested again. 
* They are used in almost every layer of computing: hardware, operating systems, web browsers, web applications, and more. 
* A cache is like short-term memory: it has a limited amount of space, but is typically faster than the original data source and contains the most recently accessed items.

### Application server cache
* Placing a cache directly on a request layer node enables the local storage of response data. Each time a request is made to the service, the node will quickly return local cached data if it exists. If it is not in the cache, the requesting node will query the data from disk. 
* If the request layer is expanded to multiple nodes, it’s still quite possible to have each node host its own cache. If your load balancer randomly distributes requests across the nodes, the same request will go to different nodes, thus increasing cache misses. Two choices for overcoming this hurdle are global caches and distributed caches.

### Content Distribution Network
* CDNs are a kind of cache that comes into play for sites serving large amounts of static media. In a typical CDN setup, a request will first ask the CDN for a piece of static media; the CDN will serve that content if it has it locally available. If it isn’t available, the CDN will query the back-end servers for the file, cache it locally, and serve it to the requesting user.
* If the system we are building isn’t yet large enough to have its own CDN, we can ease a future transition by serving the static media off a separate subdomain using a lightweight HTTP server like Nginx, and cut-over the DNS from your servers to a CDN later.

### Cache Invalidation
* Data in the caches should be invalidated, after the data modified in the database.
* There are three cache invalidation schemes that are used:
    * Write-through cache: data is written into the cache and the corresponding database at the same time.  The cached data allows for fast retrieval and, since the same data gets written in the permanent storage, we will have complete data consistency between the cache and the storage. Also, this scheme ensures that nothing will get lost in case of a crash, power failure, or other system disruptions. Although, write through minimizes the risk of data loss, since every write operation must be done twice before returning success to the client, this scheme has the disadvantage of higher latency for write operations.
    * Write-around cache: This technique is similar to write through cache, but data is written directly to permanent storage, bypassing the cache. This can reduce the cache being flooded with write operations that will not subsequently be re-read, but has the disadvantage that a read request for recently written data will create a “cache miss” and must be read from slower back-end storage and experience higher latency.
    * Write-back cache: Under this scheme, data is written to cache alone and completion is immediately confirmed to the client. The write to the permanent storage is done after specified intervals or under certain conditions. This results in low latency and high throughput for write-intensive applications, however, this speed comes with the risk of data loss in case of a crash or other adverse event because the only copy of the written data is in the cache.

### Cache eviction policies
* common cache eviction policies:
    * First In First Out (FIFO): The cache evicts the first block accessed first without any regard to how often or how many times it was accessed before.
    * Last In First Out (LIFO): The cache evicts the block accessed most recently first without any regard to how often or how many times it was accessed before.
    * Least Recently Used (LRU): Discards the least recently used items first.
    * Most Recently Used (MRU): Discards, in contrast to LRU, the most recently used items first.
    * Least Frequently Used (LFU): Counts how often an item is needed. Those that are used least often are discarded first.
    * Random Replacement (RR): Randomly selects a candidate item and discards it to make space when necessary.

## Data Partitioning
* Data partitioning is a technique to break up a big database (DB) into many smaller parts. It is the process of splitting up a DB/table across multiple machines to improve the manageability, performance, availability, and load balancing of an application. 

### Partitioning Methods
#### Horizontal partitioning
* In this scheme, we put different rows into different tables.
* Horizontal partitioning is also called as Data Sharding.
* The key problem with this approach is that if the value whose range is used for partitioning isn’t chosen carefully, then the partitioning scheme will lead to unbalanced servers.
* Example:
    * If we are storing different places in a table, we can decide that locations with ZIP codes less than 10000 are stored in one table and places with ZIP codes greater than 10000 are stored in a separate table. 

#### Vertical Partitioning
* In this scheme, we divide our data to store tables related to a specific feature in their own server.
* Vertical partitioning is straightforward to implement and has a low impact on the application.
* The main problem with this approach is that if our application experiences additional growth, then it may be necessary to further partition a feature specific DB across various servers.
* Example:
    * If we are building Instagram like application - where we need to store data related to users, photos they upload, and people they follow - we can decide to place user profile information on one DB server, friend lists on another, and photos on a third server.

#### Directory Based Partitioning
* A loosely coupled approach to work around issues mentioned in the above schemes is to create a lookup service which knows your current partitioning scheme and abstracts it away from the DB access code. So, to find out where a particular data entity resides, we query the directory server that holds the mapping between each tuple key to its DB server.
* This loosely coupled approach means we can perform tasks like adding servers to the DB pool or changing our partitioning scheme without having an impact on the application.

### Partitioning Criteria
* Key or Hash-based partitioning
    * Under this scheme, we apply a hash function to some key attributes of the entity we are storing; that yields the partition number.
    * This approach should ensure a uniform allocation of data among servers.
    * The fundamental problem with this approach is that it effectively fixes the total number of DB servers, since adding new servers means changing the hash function which would require redistribution of data and downtime for the service.
        * A workaround for this problem is to use [Consistent Hashing](#Consistent-Hashing).
    * Example:
        * if we have 100 DB servers and our ID is a numeric value that gets incremented by one each time a new record is inserted. In this example, the hash function could be ‘ID % 100’, which will give us the server number where we can store/read that record.
* List partitioning
    * In this scheme, each partition is assigned a list of values, so whenever we want to insert a new record, we will see which partition contains our key and then store it there.
    * Example:
        * we can decide all users living in Iceland, Norway, Sweden, Finland, or Denmark will be stored in a partition for the Nordic countries.
* Round-robin partitioning
    * This is a very simple strategy that ensures uniform data distribution. With ‘n’ partitions, the ‘i’ tuple is assigned to partition (i mod n).
* Composite partitioning
    * Under this scheme, we combine any of the above partitioning schemes to devise a new scheme.
    * Example:
        * first applying a list partitioning scheme and then a hash based partitioning. Consistent hashing could be considered a composite of hash and list partitioning where the hash reduces the key space to a size that can be listed.

### Common Problems of Data Partitioning
* On a partitioned database, there are certain extra constraints on the different operations that can be performed. Most of these constraints are due to the fact that operations across multiple tables or multiple rows in the same table will no longer run on the same server. Below are some of the constraints and additional complexities introduced by partitioning:
    * Joins and Denormalization: Performing joins on a database which is running on one server is straightforward, but once a database is partitioned and spread across multiple machines it is often not feasible to perform joins that span database partitions. Such joins will not be performance efficient since data has to be compiled from multiple servers. A common workaround for this problem is to denormalize the database so that queries that previously required joins can be performed from a single table. Of course, the service now has to deal with all the perils of denormalization such as data inconsistency.
    * Referential integrity: As we saw that performing a cross-partition query on a partitioned database is not feasible, similarly, trying to enforce data integrity constraints such as foreign keys in a partitioned database can be extremely difficult. Most of RDBMS do not support foreign keys constraints across databases on different database servers. Which means that applications that require referential integrity on partitioned databases often have to enforce it in application code. Often in such cases, applications have to run regular SQL jobs to clean up dangling references.
    * Rebalancing: There could be many reasons we have to change our partitioning scheme:
        * The data distribution is not uniform, e.g., there are a lot of places for a particular ZIP code that cannot fit into one database partition.
        * There is a lot of load on a partition, e.g., there are too many requests being handled by the DB partition dedicated to user photos.
        * In such cases, either we have to create more DB partitions or have to rebalance existing partitions, which means the partitioning scheme changed and all existing data moved to new locations. Doing this without incurring downtime is extremely difficult. Using a scheme like directory based partitioning does make rebalancing a more palatable experience at the cost of increasing the complexity of the system and creating a new single point of failure (i.e. the lookup service/database).

## Indexes
* Indexes are well known when it comes to databases. Sooner or later there comes a time when database performance is no longer satisfactory. One of the very first things you should turn to when that happens is database indexing.
* The goal of creating an index on a particular table in a database is to make it faster to search through the table and find the row or rows that we want. Indexes can be created using one or more columns of a database table, providing the basis for both rapid random lookups and efficient access of ordered records.

### Example: A library catalog
* A library catalog is a register that contains the list of books found in a library. The catalog is organized like a database table generally with four columns: book title, writer, subject, and date of publication. There are usually two such catalogs: one sorted by the book title and one sorted by the writer name. That way, you can either think of a writer you want to read and then look through their books or look up a specific book title you know you want to read in case you don’t know the writer’s name. These catalogs are like indexes for the database of books. They provide a sorted list of data that is easily searchable by relevant information.
* A library catalog is a register that contains the list of books found in a library. The catalog is organized like a database table generally with four columns: book title, writer, subject, and date of publication. There are usually two such catalogs: one sorted by the book title and one sorted by the writer name. That way, you can either think of a writer you want to read and then look through their books or look up a specific book title you know you want to read in case you don’t know the writer’s name. These catalogs are like indexes for the database of books. They provide a sorted list of data that is easily searchable by relevant information.
* Just like a traditional relational data store, we can also apply this concept to larger datasets. The trick with indexes is that we must carefully consider how users will access the data. In the case of data sets that are many terabytes in size, but have very small payloads (e.g., 1 KB), indexes are a necessity for optimizing data access. Finding a small payload in such a large dataset can be a real challenge, since we can’t possibly iterate over that much data in any reasonable time. Furthermore, it is very likely that such a large data set is spread over several physical devices—this means we need some way to find the correct physical location of the desired data. Indexes are the best way to do this.

### How do Indexes decrease write performance?
* When adding rows or making updates to existing rows for a table with an active index, we not only have to write the data but also have to update the index. This performance degradation applies to all insert, update, and delete operations for the table. For this reason, adding unnecessary indexes on tables should be avoided and indexes that are no longer used should be removed. To reiterate, adding indexes is about improving the performance of search queries. If the goal of the database is to provide a data store that is often written to and rarely read from, in that case, decreasing the performance of the more common operation, which is writing, is probably not worth the increase in performance we get from reading.

## Proxies
* A proxy server is an intermediate server between the client and the back-end server. Clients connect to proxy servers to make a request for a service like a web page, file, connection, etc. In short, a proxy server is a piece of software or hardware that acts as an intermediary for requests from clients seeking resources from other servers.
* Typically, proxies are used to filter requests, log requests, or sometimes transform requests (by adding/removing headers, encrypting/decrypting, or compressing a resource). Another advantage of a proxy server is that its cache can serve a lot of requests. If multiple clients access a particular resource, the proxy server can cache it and serve it to all the clients without going to the remote server.

### Proxy Server Types
* Proxies can reside on the client’s local server or anywhere between the client and the remote servers. Here are a few famous types of proxy servers:
    * Open Proxy
        * An open proxy is a proxy server that is accessible by any Internet user. Generally, a proxy server only allows users within a network group (i.e. a closed proxy) to store and forward Internet services such as DNS or web pages to reduce and control the bandwidth used by the group. With an open proxy, however, any user on the Internet is able to use this forwarding service. There two famous open proxy types:
            * Anonymous Proxy - Thіs proxy reveаls іts іdentіty аs а server but does not dіsclose the іnіtіаl IP аddress. Though thіs proxy server cаn be dіscovered eаsіly іt cаn be benefіcіаl for some users аs іt hіdes their IP аddress.
            * Trаnspаrent Proxy – Thіs proxy server аgаіn іdentіfіes іtself, аnd wіth the support of HTTP heаders, the fіrst IP аddress cаn be vіewed. The mаіn benefіt of usіng thіs sort of server іs іts аbіlіty to cаche the websіtes.
    * Reverse Proxy
        * A reverse proxy retrieves resources on behalf of a client from one or more servers. These resources are then returned to the client, appearing as if they originated from the proxy server itself
    
## Redundancy and Replication
* Redundancy is the duplication of critical components or functions of a system with the intention of increasing the reliability of the system, usually in the form of a backup or fail-safe, or to improve actual system performance. Redundancy plays a key role in removing the single points of failure in the system and provides backups if needed in a crisis. 
* Replication means sharing information to ensure consistency between redundant resources, such as software or hardware components, to improve reliability, fault-tolerance, or accessibility. Replication is widely used in many database management systems (DBMS), usually with a master-slave relationship between the original and the copies. The master gets all the updates, which then ripple through to the slaves. Each slave outputs a message stating that it has received the update successfully, thus allowing the sending of subsequent updates.

## SQL vs. NoSQL
### SQL
* Relational databases store data in rows and columns. Each row contains all the information about one entity and each column contains all the separate data points. 

### NoSQL
* types of NoSQL:
    * Key-Value Stores: Data is stored in an array of key-value pairs.
        * well known products: Redis, Voldemort, and Dynamo
    * Document Databases: In these databases, data is stored in documents (instead of rows and columns in a table) and these documents are grouped together in collections. Each document can have an entirely different structure.
        * well known products: CouchDB and MongoDB
    * Wide-Column Databases: in columnar databases we have column families, which are containers for rows. we don’t need to know all the columns up front and each row doesn’t have to have the same number of columns. 
        * well known products: Cassandra and HBase
    * Graph Databases: These databases are used to store data whose relations are best represented in a graph. Data is saved in graph structures with nodes (entities), properties (information about the entities), and lines (connections between the entities).
        * well known products: Neo4J and InfiniteGraph

### SQL VS. NoSQL
* Even as NoSQL databases are gaining popularity for their speed and scalability, there are still situations where a highly structured SQL database may perform better; choosing the right technology hinges on the use case.
    * Reasons to use SQL database
        * We need to ensure ACID compliance.
        * Your data is structured and unchanging. 
    * Reasons to use NoSQL database
        * When all the other components of our application are fast and seamless, NoSQL databases prevent data from being the bottleneck. 
            * Storing large volumes of data that often have little to no structure. A NoSQL database sets no limits on the types of data we can store together and allows us to add new types as the need changes. With document-based databases, you can store data in one place without having to define what “types” of data those are in advance.
            * Making the most of cloud computing and storage. Cloud-based storage is an excellent cost-saving solution but requires data to be easily spread across multiple servers to scale up. Using commodity (affordable, smaller) hardware on-site or in the cloud saves you the hassle of additional software and NoSQL databases like Cassandra are designed to be scaled across multiple data centers out of the box, without a lot of headaches.
            * Rapid development. NoSQL is extremely useful for rapid development as it doesn’t need to be prepped ahead of time. If you’re working on quick iterations of your system which require making frequent updates to the data structure without a lot of downtime between versions, a relational database will slow you down.

## CAP Theorem
* When we design a distributed system, trading off among CAP is almost the first thing we want to consider. CAP theorem says while designing a distributed system we can pick only two of the following three options:
    * Consistency: All nodes see the same data at the same time. Consistency is achieved by updating several nodes before allowing further reads.
    *   Availability: Every request gets a response on success/failure. Availability is achieved by replicating the data across different servers.
    * Partition tolerance: The system continues to work despite message loss or partial failure. A system that is partition-tolerant can sustain any amount of network failure that doesn’t result in a failure of the entire network. Data is sufficiently replicated across combinations of nodes and networks to keep the system up through intermittent outages.
* We can only build a system that has any two of these three properties.    
    * to be consistent, all nodes should see the same set of updates in the same order. But if the network suffers a partition, updates in one partition might not make it to the other partitions before a client reads from the out-of-date partition after having read from the up-to-date one. The only thing that can be done to cope with this possibility is to stop serving requests from the out-of-date partition, but then the service is no longer 100% available.

## Consistent Hashing
* Distributed Hash Table (DHT) is one of the fundamental components used in distributed scalable systems.
    * Suppose we are designing a distributed caching system. Given ‘n’ cache servers, an intuitive hash function would be ‘key % n’. It is simple and commonly used. But it has two major drawbacks:
        * It is NOT horizontally scalable. Whenever a new cache host is added to the system, all existing mappings are broken.
        * It may NOT be load balanced, especially for non-uniformly distributed data.

### What is Consistent Hashing?
* Consistent hashing is a very useful strategy for distributed caching system and DHTs. It allows us to distribute data across a cluster in such a way that will minimize reorganization when nodes are added or removed. Hence, the caching system will be easier to scale up or scale down.

### How does Consistent Hashing work?
* As a typical hash function, consistent hashing maps a key to an integer. Suppose the output of the hash function is in the range of [0, 256). Imagine that the integers in the range are placed on a ring such that the values are wrapped around.
* Here’s how consistent hashing works:
    1. Given a list of cache servers, hash them to integers in the range.
    2. To map a key to a server:
        * Hash it to a single integer.
        * Move clockwise on the ring until finding the first cache it encounters.
        * That cache is the one that contains the key. See animation below as an example: key1 maps to cache A; key2 maps to cache C
```
C-----256|0-----A
|               |
+-------B-------+
```
* To add a new server, say D, keys that were originally residing at C will be split. Some of them will be shifted to D, while other keys will not be touched.
* To remove a cache or, if a cache fails, say A, all keys that were originally mapped to A will fall into B, and only those keys need to be moved to B; other keys will not be affected.
* For load balancing, as we discussed in the beginning, the real data is essentially randomly distributed and thus may not be uniform. It may make the keys on caches unbalanced.
* To handle this issue, we add “virtual replicas” for caches. Instead of mapping each cache to a single point on the ring, we map it to multiple points on the ring, i.e. replicas. This way, each cache is associated with multiple portions of the ring.
* If the hash function “mixes well,” as the number of replicas increases, the keys will be more balanced.