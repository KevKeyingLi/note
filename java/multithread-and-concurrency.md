- [并发编程的优缺点](#并发编程的优缺点)
    * [并发的缺点](#并发的缺点)
        * [频繁的上下文切换](#频繁的上下文切换)
        * [线程相关概念](#线程相关概念)
            * [同步与异步](#同步与异步)
            * [并发与并行](#并发与并行)
            * [阻塞与非阻塞](#阻塞与非阻塞)
            * [临界区](#临界区)
- [线程状态转换以及基本操作](#线程状态转换以及基本操作)
    * [新建线程](#新建线程)
    * [线程状态转换](#线程状态转换)
    * [线程状态的基本操作](#线程状态的基本操作)
        * [interrupted](#interrupted)
        * [join](#join)
        * [sleep](#sleep)
        * [yield](#yield)
- [Java内存模型以及happens-before](#Java内存模型以及happens-before)
    * [内存模型抽象结构](#内存模型抽象结构)
        * [共享变量](#共享变量)
        * [JMM抽象结构模型](#JMM抽象结构模型)
    * [重排序](#重排序)
        * [as-if-serial](#as-if-serial)
    * [happens-before规则](#happens-before规则)
        * [happens-before定义](#happens-before定义)
        * [具体规则](#具体规则)
- [关键字synchronized](#关键字synchronized)
    * [synchronized实现原理](#synchronized实现原理)
        * [对象锁monitor机制](#对象锁monitor机制)
        * [synchronized的happens-before关系](#synchronized的happens-before关系)
        * [锁获取和锁释放的内存语义](#锁获取和锁释放的内存语义)
    * [synchronized优化](#synchronized优化)
        * [CAS操作](#CAS操作)
            * [CAS的操作过程](#CAS的操作过程)
            * [CAS的问题](#CAS的问题)
        * [Java对象头](#Java对象头)
        * [偏向锁](#偏向锁)
        * [轻量级锁](#轻量级锁)

---
* refs:
    * > https://github.com/zjcscut/Java-concurrency
---

### 并发编程的优缺点
* 并发编程的形式可以将多核CPU的计算能力发挥到极致，性能得到提升。
* 面对复杂业务模型，并行程序会比串行程序更适应业务需求，而并发编程更能吻合这种业务拆分 。

#### 并发的缺点
##### 频繁的上下文切换
* 时间片是CPU分配给各个线程的时间，因为时间非常短，所以CPU不断通过切换线程，让我们觉得多个线程是同时执行的，时间片一般是几十毫秒。而每次切换时，需要保存当前的状态起来，以便能够进行恢复先前状态，而这个切换时非常损耗性能，过于频繁反而无法发挥出多线程编程的优势。通常减少上下文切换可以采用`无锁并发编程`，`CAS算法`，`使用最少的线程`和`使用协程`。
    * `无锁并发编程`: 可以参照concurrentHashMap锁分段的思想，不同的线程处理不同段的数据，这样在多线程竞争的条件下，可以减少上下文切换的时间。
    * `CAS算法`: 利用Atomic下使用CAS算法来更新数据，使用了乐观锁，可以有效的减少一部分不必要的锁竞争带来的上下文切换
    * `使用最少线程`: 避免创建不需要的线程，比如任务很少，但是创建了很多的线程，这样会造成大量的线程都处于等待状态
    * `协程`: 在单线程里实现多任务的调度，并在单线程里维持多个任务间的切换

##### 线程安全
* 多线程编程中最难以把握的就是临界区线程安全问题，稍微不注意就会出现死锁的情况，一旦产生死锁就会造成系统功能不可用。
* 避免死锁的方式: 
    * 避免一个线程同时获得多个锁；
    * 避免一个线程在锁内部占有多个资源，尽量保证每个锁只占用一个资源；
    * 尝试使用定时锁，使用`lock.tryLock(timeOut)`，当超时等待时当前线程不会阻塞；
    * 对于数据库锁，加锁和解锁必须在一个数据库连接里，否则会出现解锁失败的情况
* 所以，如何正确的使用多线程编程技术有很大的学问，比如如何保证线程安全，如何正确理解由于JMM内存模型在原子性，有序性，可见性带来的问题，比如数据脏读，DCL等这些问题。

##### 线程相关概念
###### 同步与异步
* 同步方法调用一开始，调用者必须等待被调用的方法结束后，调用者后面的代码才能执行。
* 异步调用，指的是，调用者不用管被调用方法是否完成，都会继续执行后面的代码，当被调用的方法完成后会通知调用者。

###### 并发与并行
* 并发指的是多个任务交替进行，实际上，如果系统内只有一个CPU，而使用多线程时，那么真实系统环境下不能并行，只能通过切换时间片的方式交替进行，而成为并发执行任务。
* 并行则是指真正意义上的“同时进行”。只能出现在拥有多个CPU的系统中。

###### 阻塞与非阻塞
* 阻塞和非阻塞通常用来形容多线程间的相互影响，比如一个线程占有了临界区资源，那么其他线程需要这个资源就必须进行等待该资源的释放，会导致等待的线程挂起，这种情况就是阻塞，而非阻塞就恰好相反，它强调没有一个线程可以阻塞其他线程，所有的线程都会尝试地往前运行。

###### 临界区
* 临界区用来表示一种公共资源或者说是共享数据，可以被多个线程使用。但是每个线程使用时，一旦临界区资源被一个线程占有，那么其他线程必须等待。


### 线程状态转换以及基本操作
#### 新建线程
* 一个java程序从`main()`方法开始执行，然后按照既定的代码逻辑执行，看似没有其他线程参与，但实际上java程序天生就是一个多线程程序，包含了:
    * 通过继承`Thread`类，重写`run`方法
        ```java
        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println("继承Thread");
                super.run();
            }
        };
        thread.start();
        ```
    * 通过实现`runable`接口
        ```java
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("实现runable接口");
            }
        });
        thread1.start();
        ```
    * 通过实现`callable`接口
        ```java
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = service.submit(new Callable() {
            @Override
            public String call() throws Exception {
                return "通过实现Callable接口";
            }
        });
        try {
            String result = future.get();
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ```
* 三种新建线程的方式具体看以上注释，需要主要的是:
    * 由于java不能多继承可以实现多个接口，因此，在创建线程的时候尽量多考虑采用实现接口的形式；
    * 实现`callable`接口，提交给`ExecutorService`返回的是异步执行的结果，另外，通常也可以利用`FutureTask(Callable callable)`将`callable`进行包装然后`FeatureTask`提交给`ExecutorsService`。
* 另外由于`FeatureTask`也实现了`Runable`接口也可以利用上面第二种方式(实现Runable接口)来新建线程；
    * 可以通过`Executors`将`Runable`转换成`Callable`，具体方法是:
        * `Callable callable(Runnable task, T result)`
        * `Callable callable(Runnable task)`

#### 线程状态转换
* 线程状态
    * 初始(new)
    * 等待(waiting)
    * 运行(runnable) (运行中+就绪)
    * 运行中(running)
    * 就绪(ready)
    * 超时等待(timed waiting)
    * 阻塞(blocked)
    * 终止(terminated)
* 线程创建之后调用`start()`方法开始运行
* 当调用`wait()`、`join()`、`LockSupport.lock()`方法线程会进入到`WAITING`状态
* `wait(long timeout)`、`sleep(long)`、`join(long)`、`LockSupport.parkNanos()`、`LockSupport.parkUtil()`增加了超时等待的功能，也就是调用这些方法后线程会进入`TIMED_WAITING`状态，当超时等待时间到达后，线程会切换到`Runable`的状态
* `WAITING`和`TIMED _WAITING`状态时可以通过`Object.notify()`,`Object.notifyAll()`方法使线程转换到`Runable`状态
* 当线程出现资源竞争时，即等待获取锁的时候，线程会进入到`BLOCKED`阻塞状态，当线程获取锁时，线程进入到`Runable`状态
* 线程运行结束后，线程进入到`TERMINATED`状态，状态转换可以说是线程的生命周期。
* 当线程进入到`synchronized`方法或者`synchronized`代码块时，线程切换到的是`BLOCKED`状态
* 使用`java.util.concurrent.locks`下`lock`进行加锁的时候线程切换的是`WAITING`或者`TIMED_WAITING`状态，因为`lock`会调用`LockSupport`的方法。

#### 线程状态的基本操作
##### interrupted
* 中断可以理解为线程的一个标志位，它表示了一个运行中的线程是否被其他线程进行了中断操作。中断好比其他线程对该线程打了一个招呼。其他线程可以调用该线程的`interrupt()`方法对其进行中断操作，同时该线程可以调用`isInterrupted()`来感知其他线程对其自身的中断操作，从而做出响应。另外，同样可以调用Thread的静态方法`interrupted()`对当前线程进行中断操作，该方法会清除中断标志位。需要注意的是，当抛出`InterruptedException`时候，会清除中断标志位，也就是说在调用`isInterrupted`会返回false。
    ```java
    public class InterruptDemo {
        public static void main(String[] args) throws InterruptedException {
            //sleepThread睡眠1000ms
            final Thread sleepThread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    super.run();
                }
            };
            //busyThread一直执行死循环
            Thread busyThread = new Thread() {
                @Override
                public void run() {
                    while (true) ;
                }
            };
            sleepThread.start();
            busyThread.start();
            sleepThread.interrupt();
            busyThread.interrupt();
            while (sleepThread.isInterrupted()) ;
            System.out.println("sleepThread isInterrupted: " + sleepThread.isInterrupted());
            System.out.println("busyThread isInterrupted: " + busyThread.isInterrupted());
        }
    }
    ```
    * 开启了两个线程分别为`sleepThread`和`BusyThread`, `sleepThread`睡眠1s，`BusyThread`执行死循环。然后分别对着两个线程进行中断操作，可以看出`sleepThread`抛出`InterruptedException`后清除标志位，而`busyThread`就不会清除标志位。
    * 另外，同样可以通过中断的方式实现线程间的简单交互，`while(sleepThread.isInterrupted())`表示在Main中会持续监测`sleepThread`，一旦`sleepThread`的中断标志位清零，即`sleepThread.isInterrupted()`返回为false时才会继续Main线程才会继续往下执行。因此，中断操作可以看做线程间一种简便的交互方式。一般在结束线程时通过中断标志位或者标志位的方式可以有机会去清理资源，相对于武断而直接的结束线程，这种方式要优雅和安全。

##### join
* 如果一个线程实例`A`执行了`threadB.join()`,其含义是：当前线程`A`会等待`threadB`线程终止后`threadA`才会继续执行。
* `join`方法:
    * `public final synchronized void join(long millis)`
    * `public final synchronized void join(long millis, int nanos)` 
    * `public final void join() throws InterruptedException`
* `Thread`类除了提供`join()`方法外，另外还提供了超时等待的方法，如果线程`threadB`在等待的时间内还没有结束的话，`threadA`会在超时之后继续执行。`join`方法源码关键:
    ```java
    while (isAlive()) {
        wait(0);
    }
    ```
    * 可以看出来当前等待对象`threadA`会一直阻塞，直到被等待对象`threadB`结束后即`isAlive()`返回false的时候才会结束`while`循环，当`threadB`退出时会调用`notifyAll()`方法通知所有的等待线程。
        ```java
        public class JoinDemo {
            public static void main(String[] args) {
                Thread previousThread = Thread.currentThread();
                for (int i = 1; i <= 10; i++) {
                    Thread curThread = new JoinThread(previousThread);
                    curThread.start();
                    previousThread = curThread;
                }
            }

            static class JoinThread extends Thread {
                private Thread thread;

                public JoinThread(Thread thread) {
                    this.thread = thread;
                }

                @Override
                public void run() {
                    try {
                        thread.join();
                        System.out.println(thread.getName() + " terminated.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        ```
        * 在上面的例子中一个创建了10个线程，每个线程都会等待前一个线程结束才会继续运行。可以通俗的理解成接力，前一个线程将接力棒传给下一个线程，然后又传给下一个线程

##### sleep
* `public static native void sleep(long millis)`方法显然是`Thread`的静态方法，很显然它是让当前线程按照指定的时间休眠，其休眠时间的精度取决于处理器的计时器和调度器。需要注意的是如果当前线程获得了锁，`sleep`方法并不会失去锁。
* `sleep()` VS `wait()`
* 两者主要的区别:
    * `sleep()`方法是`Thread`的静态方法，而`wait`是`Object`实例方法
    * `wait()`方法必须要在同步方法或者同步块中调用，也就是必须已经获得对象锁。而`sleep()`方法没有这个限制可以在任何地方种使用。另外，`wait()`方法会释放占有的对象锁，使得该线程进入等待池中，等待下一次获取资源。而`sleep()`方法只是会让出CPU并不会释放掉对象锁；
    * `sleep()`方法在休眠时间达到后如果再次获得CPU时间片就会继续执行，而`wait()`方法必须等待`Object.notift/Object.notifyAll`通知后，才会离开等待池，并且再次获得CPU时间片才会继续执行。

##### yield
* `public static native void yield()`;这是一个静态方法，一旦执行，它会是当前线程让出CPU，但是，需要注意的是，让出的CPU并不是代表当前线程不再运行了，如果在下一次竞争中，又获得了CPU时间片当前线程依然会继续运行。另外，让出的时间片只会分配给当前线程相同优先级的线程。
* 现代操作系统基本采用时分的形式调度运行的线程，操作系统会分出一个个时间片，线程会分配到若干时间片，当前时间片用完后就会发生线程调度，并等待这下次分配。线程分配到的时间多少也就决定了线程使用处理器资源的多少，而线程优先级就是决定线程需要或多或少分配一些处理器资源的线程属性。
* 在Java程序中，通过一个整型成员变量Priority来控制优先级，优先级的范围从1~10.在构建线程的时候可以通过`setPriority(int)`方法进行设置，默认优先级为5，优先级高的线程相较于优先级低的线程优先获得处理器时间片。需要注意的是在不同JVM以及操作系统上，线程规划存在差异，有些操作系统甚至会忽略线程优先级的设定。
* 另外需要注意的是，`sleep()`和`yield()`方法，同样都是当前线程会交出处理器资源，而它们不同的是，`sleep()`交出来的时间片其他线程都可以去竞争，也就是说都有机会获得当前线程让出的时间片。而`yield()`方法只允许与当前线程具有相同优先级的线程能够获得释放出来的CPU时间片。

##### 守护线程Daemon
* 守护线程是一种特殊的线程，就和它的名字一样，它是系统的守护者，在后台默默地守护一些系统服务，比如垃圾回收线程，JIT线程就可以理解守护线程。
* 与之对应的就是用户线程，用户线程就可以认为是系统的工作线程，它会完成整个系统的业务操作。
* 用户线程完全结束后就意味着整个系统的业务任务全部结束了，因此系统就没有对象需要守护的了，守护线程自然而然就会退。
* 当一个Java应用，只有守护线程的时候，虚拟机就会自然退出。
    ```java
    public class DaemonDemo {
        public static void main(String[] args) {
            Thread daemonThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            System.out.println("i am alive");
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            System.out.println("finally block");
                        }
                    }
                }
            });
            daemonThread.setDaemon(true);
            daemonThread.start();
            //确保main线程结束前能给daemonThread能够分到时间片
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    ```
    * 输出结果为: `i am alive finally block i am alive`
    * 上面的例子中`daemodThread`的`run`方法中是一个while死循环，会一直打印,但是当`main`线程结束后`daemonThread`就会退出所以不会出现死循环的情况。
    * `main`线程先睡眠800ms保证`daemonThread`能够拥有一次时间片的机会，也就是说可以正常执行一次打印“i am alive”操作和一次finally块中"finally block"操作。
    * 紧接着`main`线程结束后，`daemonThread`退出，这个时候只打印了"i am alive"并没有打印finnal块中的。
* 因此，这里需要注意的是守护线程在退出的时候并不会执行`finnaly`块中的代码，所以将释放资源等操作不要放在`finnaly`块中执行，这种操作是不安全的
* 线程可以通过`setDaemon(true)`的方法将线程设置为守护线程。并且需要注意的是设置守护线程要先于`start()`方法，否则会报。这样的异常，但是该线程还是会执行，只不过会当做正常的用户线程执行。


### Java内存模型以及happens-before
* 当多个线程访问同一个对象时，如果不用考虑这些线程在运行时环境下的调度和交替运行，也不需要进行额外的同步，或者在调用方进行任何其他的协调操作，调用这个对象的行为都可以获取正确的结果，那这个对象是线程安全的。
* 出现线程安全的问题一般是因为主内存和工作内存数据不一致性和重排序导致的，而解决线程安全的问题最重要的就是理解这两种问题是怎么来的，那么，理解它们的核心在于理解java内存模型(JMM)。
* 在多线程条件下，多个线程肯定会相互协作完成一件事情，一般来说就会涉及到多个线程间相互通信告知彼此的状态以及当前的执行结果等，另外，为了性能优化，还会涉及到编译器指令重排序和处理器指令重排序。

#### 内存模型抽象结构
* 并发编程中主要需要解决两个问题:
    * 线程之间如何通信
    * 线程之间如何完成同步(这里的线程指的是并发执行的活动实体)
* 通信是指线程之间以何种机制来交换信息，主要有两种: `共享内存`和`消息传递`
* java内存模型是共享内存的并发模型，线程之间主要通过读-写共享变量来完成隐式通信。

##### 共享变量
* 在java程序中所有实例域，静态域和数组元素都是放在堆内存中(所有线程均可访问到，是可以共享的)，而局部变量，方法定义参数和异常处理器参数不会在线程间共享。共享数据会出现线程安全的问题，而非共享数据不会出现线程安全的问题。

##### JMM抽象结构模型
* CPU的处理速度和主存的读写速度不是一个量级的，为了平衡这种巨大的差距，每个CPU都会有缓存。因此，共享变量会先放在主存中，每个线程都有属于自己的工作内存，并且会把位于主存中的共享变量拷贝到自己的工作内存，之后的读写操作均使用位于工作内存的变量副本，并在某个时刻将工作内存的变量副本写回到主存中去。JMM就从抽象层次定义了这种方式，并且JMM决定了一个线程对共享变量的写入何时对其他线程是可见的。
    * 线程A和线程B之间要完成通信的话，要经历如下两步: 
        * 线程A从主内存中将共享变量读入线程A的工作内存后并进行操作，之后将数据重新写回到主内存中
        * 线程B从主存中读取最新的共享变量
    * 如果线程A更新后数据并没有及时写回到主存，而此时线程B读到的是过期的数据，这就出现了“脏读”现象。可以通过同步机制(控制不同线程间操作发生的相对顺序)来解决或者通过`volatile`关键字使得每次`volatile`变量都能够强制刷新到主存，从而对每个线程都是可见的。

#### 重排序
* 一个好的内存模型实际上会放松对处理器和编译器规则的束缚，也就是说软件技术和硬件技术都为同一个目标而进行奋斗: 在不改变程序执行结果的前提下，尽可能提高并行度。
* JMM对底层尽量减少约束，使其能够发挥自身优势。因此，在执行程序时，为了提高性能，编译器和处理器常常会对指令进行重排序。一般重排序可以分为如下三种:
    * `源代码` -> `编译器优化重排序` -> `指令级并行重排序` -> `内存系统重排序` -> `最终执行的指令序列`
        * `编译器优化重排序`: 编译器在不改变单线程程序语义的前提下，可以重新安排语句的执行顺序；
        * `指令级并行重排序`: 现代处理器采用了指令级并行技术来将多条指令重叠执行。如果不存在数据依赖性，处理器可以改变语句对应机器指令的执行顺序；
        * `内存系统重排序`: 由于处理器使用缓存和读写缓冲区，这使得加载和存储操作看上去可能是在乱序执行的。
* 这些重排序会导致线程安全的问题，这个在以后的文章中会具体去聊。针对编译器重排序，JMM的编译器重排序规则会禁止一些特定类型的编译器重排序；针对处理器重排序，编译器在生成指令序列的时候会通过插入内存屏障指令来禁止某些特殊的处理器重排序。
* 如果两个操作访问同一个变量，且这两个操作有一个为写操作，此时这两个操作就存在数据依赖性这里就存在三种情况:
    * 读后写
    * 写后写
    * 写后读
* 者三种操作都是存在数据依赖性的，如果重排序会对最终执行结果会存在影响。编译器和处理器在重排序时，会遵守数据依赖性，编译器和处理器不会改变存在数据依赖性关系的两个操作的执行顺序。

##### as-if-serial
* 不管怎么重排序(编译器和处理器为了提供并行度)，(单线程)程序的执行结果不能被改变。编译器，`runtime`和处理器都必须遵守`as-if-serial`语义。`as-if-serial`语义把单线程程序保护了起来，遵守`as-if-serial`语义的编译器，`runtime`和处理器共同为编写单线程程序的程序员创建了一个幻觉: 单线程程序是按程序的顺序来执行的。在单线程中，会让人感觉代码是一行一行顺序执行上，实际上A,B两行不存在数据依赖性可能会进行重排序，即A，B不是顺序执行的。`as-if-serial`语义使程序员不必担心单线程中重排序的问题干扰他们，也无需担心内存可见性问题。

#### happens-before规则
##### happens-before定义
* 由于这两个操作可以在一个线程之内，也可以是在不同线程之间。因此，JMM可以通过`happens-before`关系向程序员提供跨线程的内存可见性保证(如果A线程的写操作a与B线程的读操作b之间存在`happens-before`关系，尽管a操作和b操作在不同的线程中执行，但JMM向程序员保证a操作将对b操作可见)。
* 具体的定义为:
    * 如果一个操作`happens-before`另一个操作，那么第一个操作的执行结果将对第二个操作可见，而且第一个操作的执行顺序排在第二个操作之前。
    * 两个操作之间存在`happens-before`关系，并不意味着Java平台的具体实现必须要按照`happens-before`关系指定的顺序来执行。如果重排序之后的执行结果，与按`happens-before`关系来执行的结果一致，那么这种重排序并不非法(也就是说，JMM允许这种重排序)。
* `as-if-serial`VS`happens-before`
    * `as-if-serial`语义保证单线程内程序的执行结果不被改变，`happens-before`关系保证正确同步的多线程程序的执行结果不被改变。
    * `as-if-serial`语义给编写单线程程序的程序员创造了一个幻境: 单线程程序是按程序的顺序来执行的。`happens-before`关系给编写正确同步的多线程程序的程序员创造了一个幻境: 正确同步的多线程程序是按`happens-before`指定的顺序来执行的。
    * `as-if-serial`语义和`happens-before`这么做的目的，都是为了在不改变程序执行结果的前提下，尽可能地提高程序执行的并行度。

##### 具体规则
* 程序顺序规则: 一个线程中的每个操作，`happens-before`于该线程中的任意后续操作。
* 监视器锁规则: 对一个锁的解锁，`happens-before`于随后对这个锁的加锁。
* `volatile`变量规则: 对一个`volatile`域的写，`happens-before`于任意后续对这个`volatile`域的读。
* 传递性: 如果`A happens-before B`，且`B happens-before C`，那么`A happens-before C`。
* `start()`规则: 如果线程A执行操作`ThreadB.start()`(启动线程B)，那么A线程的`ThreadB.start()`操作`happens-before`于线程B中的任意操作。
* `join()`规则: 如果线程A执行操作`ThreadB.join()`并成功返回，那么线程B中的任意操作`happens-before`于线程A从`ThreadB.join()`操作成功返回。
* 程序中断规则: 对线程`interrupted()`方法的调用先行于被中断线程的代码检测到中断时间的发生。
* 对象`finalize`规则: 一个对象的初始化完成(构造函数执行结束)先行于发生它的`finalize()`方法的开始。


### 关键字synchronized
* 一个现象:
    ```java
    public class SynchronizedDemo implements Runnable {
        private static int count = 0;

        public static void main(String[] args) {
            for (int i = 0; i < 10; i++) {
                Thread thread = new Thread(new SynchronizedDemo());
                thread.start();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("result: " + count);
        }

        @Override
        public void run() {
            for (int i = 0; i < 1000000; i++)
                count++;
        }
    }
    ```
    * 开启了10个线程，每个线程都累加了1000000次，如果结果正确的话自然而然总数就应该是10 * 1000000 = 10000000。可就运行多次结果都不是这个数，而且每次运行结果都不一样。

#### synchronized实现原理
* 在java代码中使用`synchronized`可是使用在代码块和方法中，根据`Synchronized`用的位置可以有这些使用场景:
    * 方法
        * 实例方法
            ```java
            // 锁住的是该类的实例对象
            public synchronized void method() {}
            ```
        * 静态方法
            ```java
            // 锁住的是类对象
            public static synchronized void method() {}
            ```
    * 代码块
        * 实例对象
            ```java
            // 锁住的是该类的实例对象
            synchronized(this) {}
            ```
        * class对象
            ```java
            // 锁住的是类对象
            synchronized(SynchronizedDemo.class) {}
            ```
        * 任意实例对象Object
            ```java
            // 实例对象Object
            // String对象作为锁
            String lock = "";
            synchronized(lock) {}
            ```
* 如果锁的是类对象的话，尽管`new`多个实例对象，但他们仍然是属于同一个类依然会被锁住，即线程之间保证同步关系。

##### 对象锁monitor机制
* * 使用`Synchronized`进行同步，其关键就是必须要对对象的监视器`monitor`进行获取，当线程获取`monitor`后才能继续往下执行，否则就只能等待。而这个获取的过程是`互斥`的，即同一时刻只有一个线程能够获取到`monitor`。
* 现在我们来看看`synchronized`的具体底层实现。先写一个简单的demo:
    ```java
    public class SynchronizedDemo {
        public static void main(String[] args) {
            synchronized (SynchronizedDemo.class) {
            }
            method();
        }

        private static void method() {}
    }
    ```
    * 上面的代码中有一个同步代码块，锁住的是类对象，并且还有一个同步静态方法，锁住的依然是该类的类对象。编译之后，切换到`SynchronizedDemo.class`的同级目录之后，然后用`javap -v SynchronizedDemo.class`查看字节码文件: 
        * 添`Synchronized`关键字之后独有指令: 
            * 退出的时候`monitorexit`指令，第7、13行
            * 执行同步代码块后首先要先执行`monitorenter`指令，第5行
    * 上面的demo中在执行完同步代码块之后紧接着再会去执行一个静态同步方法，而这个方法锁的对象依然就这个类对象，那么这个正在执行的线程还需要获取该锁吗？答案是不必的，执行静态同步方法的时候就只有一条`monitorexit`指令，并没有`monitorenter`获取锁的指令。
* 锁的重入性，即在同一锁程中，线程不需要再次获取同一把锁。`Synchronized`先天具有重入性。每个对象拥有一个计数器，当线程获取该对象锁后，计数器就会加一，释放锁后就会将计数器减一。
* 任意一个对象都拥有自己的监视器，当这个对象由同步块或者这个对象的同步方法调用时，执行方法的线程必须先获取该对象的监视器才能进入同步块和同步方法，如果没有获取到监视器的线程将会被阻塞在同步块和同步方法的入口处，进入到`BLOCKED`状态
* 任意线程对Object的访问，首先要获得Object的监视器，如果获取失败，该线程就进入同步状态，线程状态变为`BLOCKED`，当Object的监视器占有者释放后，在同步队列中得线程就会有机会重新获取该监视器。

##### synchronized的happens-before关系
```java
public class MonitorDemo {
    private int a = 0;

    public synchronized void writer() {     // 1
        a++;                                // 2
    }                                       // 3

    public synchronized void reader() {    // 4
        int i = a;                         // 5
    }                                      // 6
}
```
* 该代码的`happens-before`关系:
    * 线程A
        1. 获取锁
        2. 执行临界区代码
        3. 释放锁
    * 线程B
        4. 获取锁
        5. 执行临界区代码
        6. 释放锁
    * 线程A释放锁`happens-before`线程获取锁
    * 根据`happens-before`的定义中的一条: 如果`A happens-before B`，则A的执行结果对B可见，并且A的执行顺序先于B。线程A先对共享变量A进行加一，由`2 happens-before 5`关系可知线程A的执行结果对线程B可见即线程B所读取到的a的值为1。

##### 锁获取和锁释放的内存语义
* Step1:
    * 线程A:
        * 本地内存A: `a = 1`
        * 操作: 写
    * 主内存:
        `a = 1`
    * 线程B:
        * 本地内存B: `a = 0`
        * 操作: 无
* 线程A会首先先从主内存中读取共享变量a=0的值然后将该变量拷贝到自己的本地内存，进行加一操作后，再将该值刷新到主内存，整个过程即为: `线程A加锁` -> `执行临界区代码` ->`释放锁`相对应的内存语义。
* Step2:
    * 线程A:
        * 本地内存A: `a = 1`
        * 操作: 写
    * 主内存:
        `a = 1`
    * 线程B:
        * 本地内存B: `a = 1`
        * 操作: 读
    * 线程A隐式向线程B发消息
* 线程B获取锁的时候同样会从主内存中共享变量a的值，这个时候就是最新的值1,然后将该值拷贝到线程B的工作内存中去，释放锁的时候同样会重写到主内存中。
* 从整体上来看，线程A的执行结果(`a=1`)对线程B是可见的，实现原理为: 释放锁的时候会将值刷新到主内存中，其他线程获取锁时会强制从主内存中获取最新的值。另外也验证了`2 happens-before 5`，2的执行结果对5是可见的。
* 从横向来看，这就像线程A通过主内存中的共享变量和线程B进行通信，A告诉B我们俩的共享数据现在为1啦，这种线程间的通信机制正好吻合java的内存模型正好是共享内存的并发模型结构。

#### synchronized优化
* `Synchronized`应该有所印象了，它最大的特征就是在同一时刻只有一个线程能够获得对象的监视器，从而进入到同步代码块或者同步方法之中，即表现为`互斥性(排它性)`，这种方式肯定效率低下。
* 优化锁需要的两个知识点:
    * CAS操作
    * Java对象头

##### CAS操作
* 使用锁时，线程获取锁是一种`悲观锁`策略，即假设每一次执行临界区代码都会产生冲突，所以当前线程获取到锁的时候同时也会阻塞其他线程获取该锁。
* 而CAS操作(又称为无锁操作)是一种`乐观锁`策略，它假设所有线程访问共享资源的时候不会出现冲突，既然不会出现冲突自然而然就不会阻塞其他线程的操作。
* CAS(compare and swap): 又叫做比较交换来鉴别线程是否出现冲突，出现冲突就重试当前操作直到没有冲突为止。

###### CAS的操作过程
* CAS比较交换的过程可以通俗的理解为`V,O,N`，包含三个值分别为:
    * V内存地址存放的实际值
    * O预期的值(旧值)
    * N更新的新值
* 当V和O相同时，也就是说旧值和内存中实际的值相同表明该值没有被其他线程更改过，即该旧值O就是目前来说最新的值了，自然而然可以将新值N赋值给V。
* V和O不相同，表明该值已经被其他线程改过了则该旧值O不是最新版本的值了，所以不能将新值N赋给V，返回V即可。
* 当多个线程使用CAS操作一个变量是，只有一个线程会成功，并成功更新，其余会失败。失败的线程会重新尝试，当然也可以选择挂起线程。
* CAS的实现需要硬件指令集的支撑，在JDK1.5后虚拟机才可以使用处理器提供的CMPXCHG指令实现。
* `Synchronized` VS `CAS`
    * 元老级的`Synchronized`(未优化前)最主要的问题是: 
        * 在存在线程竞争的情况下会出现线程阻塞和唤醒锁带来的性能问题，因为这是一种`互斥同步(阻塞同步)`。
        * 而CAS并不是武断的间线程挂起，当CAS操作失败后会进行一定的尝试，而非进行耗时的挂起唤醒的操作，因此也叫做`非阻塞同步`。

###### CAS的问题
* ABA问题 
    * 因为CAS会检查旧值有没有变化，这里存在这样一个有意思的问题。比如一个旧值A变为了成B，然后再变成A，刚好在做CAS时检查发现旧值并没有变化依然为A，但是实际上的确发生了变化。解决方案可以沿袭数据库中常用的乐观锁方式，添加一个版本号可以解决。原来的变化路径`A->B->A`就变成了1A->2B->3C`
* 自旋时间过长
    * 使用CAS时非阻塞同步，也就是说不会将线程挂起，会自旋(无非就是一个死循环)进行下一次尝试，如果这里自旋时间过长对性能是很大的消耗。如果JVM能支持处理器提供的pause指令，那么在效率上会有一定的提升。
* 只能保证一个共享变量的原子操作
    * 当对一个共享变量执行操作时CAS能保证其原子性，如果对多个共享变量进行操作，CAS就不能保证其原子性。有一个解决方案是利用对象整合多个共享变量，即一个类中的成员变量就是这几个共享变量。然后将这个对象做CAS操作就可以保证其原子性。atomic中提供了AtomicReference来保证引用对象之间的原子性。

##### Java对象头
* 在同步的时候是获取对象的`monitor`，即获取到对象的锁。那么对象的锁怎么理解？无非就是类似对对象的一个标志，那么这个标志就是存放在Java对象的对象头。
* Java对象头里的Mark Word里默认的存放的对象的Hashcode、分代年龄、是否是偏向锁和锁标记位。
* 锁一共有4种状态，级别从低到高依，随着竞争情况逐渐升级: 
    * 无锁状态
    * 偏向锁状态
    * 轻量级锁状态
    * 重量级锁状态
* 锁可以升级但不能降级，意味着偏向锁升级成轻量级锁后不能降级成偏向锁。
* 这种锁升级却不能降级的策略，目的是为了提高获得锁和释放锁的效率。

##### 偏向锁
* 大多数情况下，锁不仅不存在多线程竞争，而且总是由同一线程多次获得，为了让线程获得锁的代价更低而引入了偏向锁。
* 偏向锁的获取
    * 当一个线程访问同步块并获取锁时，会在对象头和栈帧中的锁记录里存储锁偏向的线程ID，以后该线程在进入和退出同步块时不需要进行CAS操作来加锁和解锁，只需简单地测试一下对象头的Mark Word里是否存储着指向当前线程的偏向锁。
        * 如果测试成功，表示线程已经获得了锁。
        * 如果测试失败，则需要再测试一下Mark Word中偏向锁的标识是否设置成1(表示当前是偏向锁):
            * 如果没有设置，则使用CAS竞争锁；
            * 如果设置了，则尝试使用CAS将对象头的偏向锁指向当前线程
* 偏向锁的撤销
    * 偏向锁使用了一种等到竞争出现才释放锁的机制，所以当其他线程尝试竞争偏向锁时，持有偏向锁的线程才会释放锁。
    * 偏向锁的撤销，需要等待全局安全点(在这个时间点上没有正在执行的字节码)。
    * 它会首先暂停拥有偏向锁的线程，然后检查持有偏向锁的线程是否活着:
        * 如果线程不处于活动状态，则将对象头设置成无锁状态；
        * 如果线程仍然活着，拥有偏向锁的栈会被执行，遍历偏向对象的锁记录，栈中的锁记录和对象头的Mark Word要么重新偏向于其他线程，要么恢复到无锁或者标记对象不适合作为偏向锁，最后唤醒暂停的线程。

##### 轻量级锁
* 加锁
    * 线程在执行同步块之前，JVM会先在当前线程的栈桢中创建用于存储锁记录的空间，并将对象头中的Mark Word复制到锁记录中，官方称为Displaced Mark Word。然后线程尝试使用CAS将对象头中的Mark Word替换为指向锁记录的指针。如果成功，当前线程获得锁，如果失败，表示其他线程竞争锁，当前线程便尝试使用自旋来获取锁。
* 解锁
    * 轻量级解锁时，会使用原子的CAS操作将Displaced Mark Word替换回到对象头，如果成功，则表示没有竞争发生。如果失败，表示当前锁存在竞争，锁就会膨胀成重量级锁。
* 因为自旋会消耗CPU，为了避免无用的自旋(比如获得锁的线程被阻塞住了)，一旦锁升级成重量级锁，就不会再恢复到轻量级锁状态。当锁处于这个状态下，其他线程试图获取锁时，都会被阻塞住，当持有锁的线程释放锁之后会唤醒这些线程，被唤醒的线程就会进行新一轮的夺锁之争。