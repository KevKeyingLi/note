### Scalability, Availability and Stability

- scalability
  - state
    - distributed caching
      - write-behind
      - write-through
      - eviction polices
        - TTL
        - FIFO
        - LIFO
      - peer-to-peer
    - data grids
      - in-memory storage
      - durable storage
    - service record
      - NoSQL
        - key-value databases
        - document databases
        - graph databases
        - datastructure databases
      - RDBMS
        - sharding
          - partitioning
          - replication
        - denormalization
        - ORM + rich domain model antipattern
    - http caching
      - reserve proxy
      - CDN
    - CAP theorem
      - consistent/atomic data
      - eventually consistent data
    - concurrency
      - message-passing concurrency
      - Software Transactional memory
      - dataflow concurrency
      - shared-state concurrency
    - partitioning
    - replication
  - behavior
    - compute grids
    - event-driven architecture
      - message
        - pushlish-subscribe
        - point-to-point
        - store-forward
        - request-reply
      - actors
        - fire-forget
        - fire-receive-eventually
      - enterprise service bus
      - domain events
      - event stream processing
      - command & query responsibility segregation
    - load-balancing
      - round-robin allocation
      - random allocation
      - weighted allocation
      - dynamic load balancing
        - work-stealling
        - work-donating
        - queue-depth querying
    - parallel computing
      - SPMD pattern
      - master/worker pattern
      - loop parallellsm pattern
      - fork/join pattern
      - mapreduce pattern
- availability
  - replication
    - master-slave
    - tree replication
    - master-master
    - buddy replication
  - fail-over
- stability
  - circuit breaker
  - timeouts
  - let it crash/supervisors
  - crash early
  - steady state (clean up resources)
  - throttling - SEDA

---

### general recommendations

- immutability as the default
- referential transparency (FP)
- laziness
- Think about your data:
  - different data need different garantees

---

### Scalability trade-offs

- Performance vs Scalability
  - if the system is slow for a single user, it has a performance problem
  - if the system is fast for a single user but slow under heavy load, it has a scalability problem
- latency vs throughput
  - we should strive for maximal throughput with acceptable latency
- availability vs consistency

---

### the CAP theorem

- Consistency
- Availability
- Partition tolerance

- Centralized system
  - in a contralized system (RDBMS etc.) we don't have network partitions
  - garantee Availability and Consistency
- Distributed system
  - a distributed system will have network partitions
  - has Partition tolerance
  - pick one from Availability and Consistency, and sacrifice the other

---

### ACID

- Atomic
- Consistent
- Isolated
- Durable

---

### Basically Available Soft state Eventually consistent

---

### Availability patterns

- fail-over
  - fail-over
    1. when failure occurs on primary node, notification will be sent out
    2. when the notification are detected, system activate the passive node
    3. redirect traffic to the passive node when it is activated
  - fall-back
    1. resynchronizing data when primary is restored
    2. redirect traffic to the primary node after resynchronization
- replication
  - active replication: push
  - passive replication: pull
    - data not avaiable, read from peer, then store it locally
    - works well with timeout-based caches
  - pattern
    - master-slave
      - client can only into master, then data be replicated to the slaves
      - client can read from all nodes
    - tree replication
      - multiple master-slave architectures stack together
    - master-master
      - all nodes are master nodes
      - client can read and write to all node, then sync the data to each other
    - buddy replication
      - each node stores its own data, and stores the replication data of other node or nodes

---

### Scalability Patterns

- state
  - distributed caching
    - write-behind
      1. write to cache
      2. add event to queue
      3. return to user
      4. asynchronously: select and execute event
    - write-through
      1. write to cache
      2. store in DB
      3. return to user
    - eviction polices
      - TTL(time to live)
      - FIFO
      - LIFO
      - explicit cache invalidation
    - peer-to-peer
      - decentralized
      - no "special" or "blessed" node
      - nodes can join and leave as they please
    - products
      - EHCache
      - JBoss Cache
      - OSCache
      - memcached
        - very fast
        - simple
        - key-value(string -> binary)
        - clients for most languages
        - distributed
        - not replicated - so I/N chance for local access in cluster
  - data grids
    - in-memory storage
    - durable storage
      - parallel data storage
        - data replication
        - data partitioning
        - continuous availability
        - data invalidation
        - fail-over
        - C + P in CAP
  - service record
    - NoSQL
      - key-value databases
      - document databases
      - graph databases
      - datastructure databases
    - RDBMS
      - scaling reads to a RDBMS is hard
      - scaling writes to a RDBMS is impossible
      - sharding
        - partitioning
        - replication
      - denormalization
      - ORM + rich domain model antipattern
        - attempt: read an object from DB
        - result: you sit with your whole database in your lap
  - http caching
    - reserve proxy
      - Varnish
      - Squid
      - rack-cache
      - Pound
      - Nginx
      - Apache mod_proxy
      - Traffic Server
    - CDN
      - generate and precompute static content
        - Homegrown + cron or Quartz
        - Spring Batch
        - Gearman
        - Hadoop
        - Google Data Protocol
        - Amazon Elastic MapReduce
      - first request
        1. check cache first, data not found, then check backend
        2. after fetching data from backend, return and store to cache, then return to client
      - second request
        1. check cache first, found data
  - CAP theorem
    - consistent/atomic data
    - eventually consistent data
  - concurrency
    - message-passing concurrency
      - actors
        - share nothing
        - isolated lightweight processes
        - communicates through message
        - asychronous and non-blocking
        - no shared state
        - each acter has a mailbox(message queue)
        - easier to reason about
        - raised abstraction level
        - easier to avoid:
          - race conditions
          - deadlocks
          - starvation
          - live locks
    - Software Transactional memory
    - dataflow concurrency
      - declarative
      - no observable non-determinism
      - data-driven - threads block until data is available
      - on-demand, lazy
      - no difference between concurrent and sequential code
      - limitations: can't have side-effects
    - shared-state concurrency
      - Everyone can access anything anytime
      - Totally indeterministic
      - introduce determinisim at well-definied
        places
      - using locks
        - problem with locks:
          - locks do not compose
          - taking too few locks
          - taking too many locks
          - taking the wrong locks
          - taking locks in the wrong order
          - error recovery is hard
      - use java.util.concurrent.\*
  - partitioning
  - replication

---

### Behavior Patterns

- behavior
  - compute grids
  - event-driven architecture
    - message
      - pushlish-subscribe
      - point-to-point
      - store-forward
      - request-reply
    - actors
      - fire-forget
      - fire-receive-eventually
    - enterprise service bus
    - domain events
    - event stream processing
    - event sourcing
      - every state change is materialized in an event
      - all events are sent to an eventprocessor
      - eventprocessor stores all event in an event log
      - system can be reset and event log replayed
      - no need for ORM, just persist the events
      - many different eventlisteners can be added to eventprocessor (or listen directly on the event log)
    - command & query responsibility segregation (CQRS)
      - all state changes are represented by domain events
      - aggregate roots receive command and publish events
      - reporting (query database) is updated as a result of the published events
      - all queries form presentation go directly to reporting and domain is not involved
      - Benefits:
        - fully encapsulated domain that only exposes behavior
        - queries do not use the domain model
        - no object-relational impedance mismatch
        - bullet-proof auditing and historical tracing
        - easy integration with external systems
        - performance and scalability
  - load-balancing
    - round-robin allocation
    - random allocation
    - weighted allocation
    - dynamic load balancing
      - work-stealling
      - work-donating
      - queue-depth querying
  - parallel computing
    - SPMD pattern
    - master/worker pattern
    - loop parallellsm pattern
    - fork/join pattern
    - mapreduce pattern
