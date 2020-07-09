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
        * [各种锁的比较](#各种锁的比较)
    * [synchronized例子](#synchronized例子)
- [关键字volatile](#关键字volatile)
    * [volatile实现原理](#volatile实现原理)
    * [volatile的内存语义](#volatile的内存语义)
        * [volatile的内存语义实现](#volatile的内存语义实现)
            * [内存屏障](#内存屏障)
    * [volatile例子](#volatile例子)
- [三大性质](#三大性质)
    * [原子性](#原子性)
    * [有序性](#有序性)
    * [可见性](#可见性)
- [lock](#lock)
    * [Lock接口API](#Lock接口API)
- [AbstractQueuedSynchronizer](#AbstractQueuedSynchronizer)
    * [AQS的模板方法设计模式](#AQS的模板方法设计模式)
    * [AQS例子](#AQS例子)
    * [同步队列](#同步队列)
    * [独占锁](#独占锁)
        * [独占锁的获取](#独占锁的获取)
        * [独占锁的释放](#独占锁的释放)
        * [可中断式获取锁](#可中断式获取锁)
        * [超时等待式获取锁](#超时等待式获取锁)
    * [共享锁](#共享锁)
        * [共享锁的获取](#共享锁的获取)
        * [共享锁的释放](#共享锁的释放)
- [ReentrantLock](#ReentrantLock)
    * [重入性的实现原理](#重入性的实现原理)
    * [公平锁与非公平锁](#公平锁与非公平锁)
- [ReentrantReadWriteLock](#ReentrantReadWriteLock)
    * [写锁详解](#写锁详解)

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

##### 各种锁的比较
* 偏向锁
    * 优点: 加锁和解锁不需要额外消耗
    * 缺点: 如果线程之间竞争锁，会带来额外的消耗
    * 适用场景: 只有一个线程的场景
* 轻量级锁
    * 优点: 竞争的线程不会阻塞，提高相应速度
    * 缺点: 竞争不到锁的线程，自旋消耗CPU
    * 适用场景: 追求相应速度的场景
* 重量级锁
    * 优点: 线程竞争不实用自旋，不消耗CPu
    * 缺点: 线程阻塞，相应时间慢
    * 适用场景: 追求吞吐量，同步块执行速度较长

#### synchronized例子
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
        synchronized (SynchronizedDemo.class) {
            for (int i = 0; i < 1000000; i++)
                count++;
        }
    }
}
```
* 最终正确的结果为10X1000000=10000000，这里能够计算出正确的结果是因为在做累加操作时使用了同步代码块，这样就能保证每个线程所获得共享变量的值都是当前最新的值，如果不使用同步的话，就可能会出现A线程累加后，而B线程做累加操作有可能是使用原来的就值，即“脏值”。


### 关键字volatile
* `synchronized`是阻塞式同步，在线程竞争激烈的情况下会升级为重量级锁。而`volatile`就可以说是java虚拟机提供的最轻量级的同步机制。
* 被`volatile`修饰的变量能够保证每个线程能够获取该变量的最新值，从而避免出现数据脏读的现象。

#### volatile实现原理
* 为了提高处理速度，处理器不直接和内存进行通信，而是先将系统内存的数据读到内部缓存(L1，L2或其他)后再进行操作，但操作完不知道何时会写到内存。如果对声明了volatile的变量进行写操作，JVM就会向处理器发送一条Lock前缀的指令，将这个变量所在缓存行的数据写回到系统内存。但是，就算写回到内存，如果其他处理器缓存的值还是旧的，再执行计算操作就会有问题。所以，在多处理器下，为了保证各个处理器的缓存是一致的，就会实现`缓存一致性协议`，每个处理器通过嗅探在总线上传播的数据来检查自己缓存的值是不是过期了，当处理器发现自己缓存行对应的内存地址被修改，就会将当前处理器的缓存行设置成无效状态，当处理器对这个数据进行修改操作的时候，会重新从系统内存中把数据读到处理器缓存里。
    * Lock前缀的指令会引起处理器缓存写回内存
    * 一个处理器的缓存回写到内存会导致其他处理器的缓存失效
    * 当处理器发现本地缓存失效后，就会从内存中重读该变量数据，即可以获取当前最新值

#### volatile的内存语义
```java
public class VolatileExample {
    private int a = 0;
    private volatile boolean flag = false;
    public void writer(){
        a = 1;          //1
        flag = true;   //2
    }
    public void reader(){
        if(flag){      //3
            int i = a; //4
        }
    }
}
```
* 还是以上面的代码为例，假设线程A先执行writer方法，线程B随后执行reader方法，初始时线程的本地内存中flag和a都是初始状态。
* 状态变化:
    * step1:
        * 线程A:
            * 本地内存:
                * flag = true
                * a = 1
            * 操作: 写
        * 主内存:
            * flag = true
            * a = 1
        * 线程B:
            * 本地内存:
                * flag = false
                * a = 0
            * 操作: 无
    * 当`volatile`变量写后，线程中本地内存中共享变量就会置为失效的状态，因此线程B再需要读取从主内存中去读取该变量的最新值。
    * step2:
        * 线程A:
            * 本地内存:
                * flag = true
                * a = 1
            * 操作: 写
        * 主内存:
            * flag = true
            * a = 1
        * 线程B:
            * 本地内存:
                * flag = true
                * a = 1
            * 操作: 读
    * 从横向来看，线程A和线程B之间进行了一次通信，线程A在写`volatile`变量时，实际上就像是给B发送了一个消息告诉线程B你现在的值都是旧的了，然后线程B读这个`volatile`变量时就像是接收了线程A刚刚发送的消息。
        
##### volatile的内存语义实现
* 如果想阻止重排序可以添加内存屏障。

###### 内存屏障
* java编译器会在生成指令系列时在适当的位置会插入内存屏障指令来禁止特定类型的处理器重排序。为了实现`volatile`的内存语义，JMM会限制特定类型的编译器和处理器重排序，JMM会针对编译器制定`volatile`重排序规则表。
* 对于编译器来说，发现一个最优布置来最小化插入屏障的总数几乎是不可能的，为此，JMM采取了保守策略:
    * 在每个volatile写操作的前面插入一个StoreStore屏障
    * 在每个volatile写操作的后面插入一个StoreLoad屏障
    * 在每个volatile读操作的后面插入一个LoadLoad屏障
    * 在每个volatile读操作的后面插入一个LoadStore屏障
* volatile写是在前面和后面分别插入内存屏障，而volatile读操作是在后面插入两个内存屏障
    * StoreStore屏障: 禁止上面的普通写和下面的volatile写重排序；
    * StoreLoad屏障: 防止上面的volatile写与下面可能有的volatile读/写重排序
    * LoadLoad屏障: 禁止下面所有的普通读操作和上面的volatile读重排序
    * LoadStore屏障: 禁止下面所有的普通写操作和上面的volatile读重排序

#### volatile例子
```java
public class VolatileDemo {
    private static volatile boolean isOver = false;

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isOver) ;
            }
        });
        thread.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isOver = true;
    }
}
```


### 三大性质
* 三条性质: 原子性、有序性和可见性

#### 原子性
* 原子性是指一个操作是不可中断的，要么全部执行成功要么全部执行失败，有着“同生共死”的感觉。
* 及时在多个线程一起执行的时候，一个操作一旦开始，就不会被其他线程所干扰。
* java内存模型中定义了8中操作都是原子的。
* 原子性变量操作`read`、`load`、`use`、`assign`、`store`、`write`可以大致认为基本数据类型的访问读写具备原子性。
* jvm没有把`lock`和`unlock`开放给我们使用
* `synchronized`满足原子性。
* `volatile`并不能保证原子性。
* 如果让volatile保证原子性，必须符合以下两条规则: 
    * 运算结果并不依赖于变量的当前值，或者能够确保只有一个线程修改变量的值
    * 变量不需要与其他的状态变量共同参与不变约束

#### 有序性
* `synchronized`语义表示锁在同一时刻只能由一个线程进行获取，当锁被占用后，其他线程只能等待。因此，`synchronized`语义就要求线程在访问读写共享变量时只能“串行”执行，因此`synchronized`具有有序性。
* 在java内存模型中说过，为了性能优化，编译器和处理器会进行指令重排序；也就是说java程序天然的有序性可以总结为: 如果在本线程内观察，所有的操作都是有序的；如果在一个线程观察另一个线程，所有的操作都是无序的。

#### 可见性
* 可见性是指当一个线程修改了共享变量后，其他线程能够立即得知这个修改。
* 通过之前对`synchronzed`内存语义进行了分析，当线程获取锁时会从主内存中获取共享变量的最新值，释放锁的时候会将共享变量同步到主内存中。
* 从而，`synchronized`具有可见性。同样的在`volatile`分析中，会通过在指令中添加lock指令，以实现内存可见性，因此`volatile`具有可见性。


### lock
* 锁是用来控制多个线程访问共享资源的方式。在`lock`接口出现之前，java程序主要是靠`synchronized`关键字实现锁功能的，而java SE5之后，并发包中增加了`lock`接口，它提供了与`synchronized`一样的锁功能。虽然它失去了像`synchronize`关键字隐式加锁解锁的便捷性，但是却拥有了锁获取和释放的可操作性，可中断的获取锁以及超时获取锁等多种`synchronized`关键字所不具备的同步特性。
* 通常使用显示使用lock的形式如下:
    ```java
    Lock lock = new ReentrantLock();
    lock.lock();
    try{
        // .......
    }finally{
        lock.unlock();
    }
    ```
* 需要注意的是`synchronized`同步块执行完成或者遇到异常是锁会自动释放，而`lock`必须调用`unlock()`方法释放锁。

#### Lock接口API
* lock接口定义的方法:
    * `void lock();` //获取锁
    * `void lockInterruptibly() throws InterruptedException;` //获取锁的过程能够响应中断
    * `boolean tryLock();` //非阻塞式响应中断能立即返回，获取锁放回true反之返回fasle
    * `boolean tryLock(long time, TimeUnit unit) throws InterruptedException;` //超时获取锁，在超时内或者未中断的情况下能够获取锁
    * `Condition newCondition();` //获取与lock绑定的等待通知组件，当前线程必须获得了锁才能进行等待，进行等待时会先释放锁，当再次获取锁时才能从等待中返回
* `ReentrantLock`实现了`lock`接口，其中并没有多少代码，有一个很明显的特点是: 基本上所有的方法的实现实际上都是调用了其静态内存类`Sync`中的方法，而`Sync`类继承了`AbstractQueuedSynchronizer`(AQS)。
    ```java
    public class ReentrantLock implements Lock, java.io.Serializable
    ```

### AbstractQueuedSynchronizer
* ReentrantLock关键核心在于对队列同步器(`AbstractQueuedSynchronizer(AQS)`)
* 同步器是用来构建锁和其他同步组件的基础框架，它的实现主要依赖一个int成员变量来表示同步状态以及通过一个FIFO队列构成等待队列。它的子类必须重写`AQS`的几个`protected`修饰的用来改变同步状态的方法，其他方法主要是实现了排队和阻塞机制。状态的更新使用`getState`，`setState`以及`compareAndSetState`这三个方法。
* 子类被推荐定义为自定义同步组件的静态内部类，同步器自身没有实现任何同步接口，它仅仅是定义了若干同步状态的获取和释放方法来供自定义同步组件的使用，同步器既支持独占式获取同步状态，也可以支持共享式获取同步状态，这样就可以方便的实现不同类型的同步组件。
* 同步器是实现锁(也可以是任意同步组件)的关键，在锁的实现中聚合同步器，利用同步器实现锁的语义。可以这样理解二者的关系:
    * 锁是面向使用者，它定义了使用者与锁交互的接口，隐藏了实现细节
    * 同步器是面向锁的实现者，它简化了锁的实现方式，屏蔽了同步状态的管理，线程的排队，等待和唤醒等底层操作。
    * 锁和同步器很好的隔离了使用者和实现者所需关注的领域。

#### AQS的模板方法设计模式
* AQS的设计是使用模板方法设计模式，它将一些方法开放给子类进行重写，而同步器给同步组件所提供模板方法又会重新调用被子类所重写的方法。
* 举个例子，AQS中需要重写的方法`tryAcquire`:
    ```java
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }
    ```
* `ReentrantLock`中`NonfairSync`(继承AQS)会重写该方法为:
    ```java
    protected final boolean tryAcquire(int acquires) {
        return nonfairTryAcquire(acquires);
    }
    ```
* 而AQS中的模板方法`acquire()`:
    ```java
    public final void acquire(int arg) {
            if (!tryAcquire(arg) &&
                acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
                selfInterrupt();
    }
    ```
    * 会调用`tryAcquire`方法，而此时当继承AQS的`NonfairSync`调用模板方法`acquire`时就会调用已经被`NonfairSync`重写的`tryAcquire`方法。
* AQS提供的模板方法可以分为3类:
    * 独占式获取与释放同步状态
    * 共享式获取与释放同步状态
    * 查询同步队列中等待线程情况

#### AQS例子
```java
class Mutex implements Lock, java.io.Serializable {
    // Our internal helper class
    // 继承AQS的静态内存类
    // 重写方法
    private static class Sync extends AbstractQueuedSynchronizer {
        // Reports whether in locked state
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        // Acquires the lock if state is zero
        public boolean tryAcquire(int acquires) {
            assert acquires == 1; // Otherwise unused
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        // Releases the lock by setting state to zero
        protected boolean tryRelease(int releases) {
            assert releases == 1; // Otherwise unused
            if (getState() == 0) throw new IllegalMonitorStateException();
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        // Provides a Condition
        Condition newCondition() {
            return new ConditionObject();
        }

        // Deserializes properly
        private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            setState(0); // reset to unlocked state
        }
    }

    // The sync object does all the hard work. We just forward to it.
    private final Sync sync = new Sync();
    //使用同步器的模板方法实现自己的同步语义
    public void lock() { sync.acquire(1); }

    public boolean tryLock() { return sync.tryAcquire(1); }

    public void unlock() { sync.release(1); }

    public Condition newCondition() { return sync.newCondition(); }

    public boolean isLocked() { return sync.isHeldExclusively(); }

    public boolean hasQueuedThreads() { return sync.hasQueuedThreads(); }

    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }
}
```
```java
public class MutextDemo {
    private static Mutex mutex = new Mutex();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                mutex.lock();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mutex.unlock();
                }
            });
            thread.start();
        }
    }
}
```
* 上面的这个例子实现了独占锁的语义，在同一个时刻只允许一个线程占有锁。
* `Mutex`定义了一个继承AQS的静态内部类`Sync`,并且重写了AQS的`tryAcquire`等等方法，而对state的更新也是利用了`setState()`、`getState()`、`compareAndSetState()`这三个方法。
* 在实现实现`lock`接口中的方法也只是调用了AQS提供的模板方法。从这个例子就可以很清楚的看出来，在同步组件的实现上主要是利用了AQS，而AQS“屏蔽”了同步状态的修改，线程排队等底层实现，通过AQS的模板方法可以很方便的给同步组件的实现者进行调用。
* 而针对用户来说，只需要调用同步组件提供的方法来实现并发编程即可。同时在新建一个同步组件时需要把握的两个关键点是:
    * 实现同步组件时推荐定义继承AQS的静态内存类，并重写需要的`protected`修饰的方法；
    * 同步组件语义的实现依赖于AQS的模板方法，而AQS模板方法又依赖于被AQS的子类所重写的方法。
* 因为AQS整体设计思路采用模板方法设计模式，同步组件以及AQS的功能实际上别切分成各自的两部分:
    * 同步组件实现者的角度:
        * 通过可重写的方法:
            * 独占式: `tryAcquire()`、`tryRelease()`
            * 共享式: `tryAcquireShared()`、`tryReleaseShared()`
        * 同步组件专注于对当前同步状态的逻辑判断，从而实现自己的同步语义。
    * AQS的角度:
        * 只需要同步组件返回的true和false即可，因为AQS会对true和false会有不同的操作，true会认为当前线程获取同步组件成功直接返回，而false的话就AQS也会将当前线程插入同步队列等一系列的方法。

#### 同步队列
* 当共享资源被某个线程占有，其他请求该资源的线程将会阻塞并进入同步队列。
* AQS中的同步队列则是通过链式方式进行实现。
* 在AQS有一个静态内部类Node，其中有这样一些属性:
    * `volatile int waitStatus;` // 节点状态
    * `volatile Node prev;` // 当前节点/线程的前驱节点
    * `volatile Node next;` //当前节点/线程的后继节点 
    * `volatile Thread thread;` // 加入同步队列的线程引用
    * `Node nextWaiter;` // 等待队列中的下一个节点
* 节点的状态有以下这些:
    * `int CANCELLED = 1;` // 节点从同步队列中取消 
    * `int SIGNAL = -1;` // 后继节点的线程处于等待状态，如果当前节点释放同步状态会通知后继节点，使得后继节点的线程能够运行；
    * `int CONDITION = -2;` // 当前节点进入等待队列中 
    * `int PROPAGATE = -3;` // 表示下一次共享式同步状态获取将会无条件传播下去 
    * `int INITIAL = 0;` // 初始状态
* lock例子
    ```java
    public class LockDemo {
        private static ReentrantLock lock = new ReentrantLock();

        public static void main(String[] args) {
            for (int i = 0; i < 5; i++) {
                Thread thread = new Thread(() -> {
                    lock.lock();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                });
                thread.start();
            }
        }
    }
    ```
    * `Thread-0`先获得锁后进行睡眠，其他线程（`Thread-1`、`Thread-2`、`Thread-3`、`Thread-4`）获取锁失败进入同步队列，同时也可以很清楚的看出来每个节点有两个域: prev(前驱)和next(后继)，并且每个节点用来保存获取同步状态失败的线程引用以及等待状态等信息。
* AQS中有两个重要的成员变量:
    ```java
    private transient volatile Node head;
    private transient volatile Node tail;
    ```
    * AQS实际上通过头尾指针来管理同步队列

#### 独占锁
##### 独占锁的获取
* 我们继续通过看源码和debug的方式来看，还是以上面的demo为例，调用`lock()`方法是获取独占式锁，获取失败就将当前线程加入同步队列，成功则线程执行。
* 而`lock()`方法实际上会调用AQS的`acquire()`方法，源码如下:
    ```java
    public final void acquire(int arg) {
        if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
    ```
    * `acquire`根据当前获得同步状态成功与否做了两件事情:
        * 成功，则方法结束返回
        * 失败，则先调用`addWaiter()`然后在调用`acquireQueued()`方法。
* `addWaiter()`源码如下:
    ```java
    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        Node pred = tail;
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }
    ```
    * 当前同步队列的尾节点为null，调用方法`enq()`插入
    * 当前队列的尾节点不为null，则采用尾插入(`compareAndSetTail()`方法)的方式入队
    * 如果`if(compareAndSetTail(pred, node))`为false，则会继续执行到`enq()`方法
    * `enq()`方法可能承担两个任务: 
        * 处理当前同步队列尾节点为null时进行入队操作
        * 如果CAS尾插入节点失败后负责自旋进行尝试
* `enq()`源码如下:
    ```java
    private Node enq(final Node node) {
            for (;;) {
                Node t = tail;
                if (t == null) {
                    if (compareAndSetHead(new Node()))
                        tail = head;
                } else {
                    node.prev = t;
                    if (compareAndSetTail(t, node)) {
                        t.next = node;
                        return t;
                    }
                }
            }
    }
    ```
    * 同步队列是带头结点的链式存储结构，带头节点的队列初始化时机是在`tail`为null时，即当前线程是第一次插入同步队列。
    * `compareAndSetTail(t, node)`方法会利用CAS操作设置尾节点，如果CAS操作失败会在`for(;;)`死循环中不断尝试，直至成功return返回为止。
* `acquireQueued()`源码如下:
    ```java
    final boolean acquireQueued(final Node node, int arg) {
            boolean failed = true;
            try {
                boolean interrupted = false;
                for (;;) {
                    final Node p = node.predecessor(); 
                    if (p == head && tryAcquire(arg)) {
                        setHead(node);
                        p.next = null;
                        failed = false;
                        return interrupted;
                    }
                    if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                        interrupted = true;
                }
            } finally {
                if (failed)
                    cancelAcquire(node);
            }
    }
    ```
    * `for(;;)`是一个自旋的过程
    * 如果先驱节点是头结点的并且成功获得同步状态的时候，即`if(p == head && tryAcquire(arg))`，当前节点所指向的线程能够获取锁。反之，获取锁失败进入等待状态。
* 出队操作
    ```java
    setHead(node);
    p.next = null;
    failed = false;
    return interrupted;
    ```
* `setHead()`源码如下:
    ```java
    private void setHead(Node node) {
            head = node;
            node.thread = null;
            node.prev = null;
    }
    ```
    * 将当前节点通过`setHead()`方法设置为队列的头结点，然后将之前的头结点的`next`域设置为null并且`pre`域也为null，即与队列断开，无任何引用方便GC时能够将内存进行回收。
* 当获取锁失败的时候会调用`shouldParkAfterFailedAcquire()`方法和`parkAndCheckInterrupt()`方法
    ```java
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL)
            return true;
        if (ws > 0) {
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }
    ```
    * 使用`compareAndSetWaitStatus(pred, ws, Node.SIGNAL)`使用CAS将节点状态由`INITIAL`设置成`SIGNAL`，表示当前线程阻塞。
    ```java
    private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }
    ```
    * 调用`LookSupport.park()`方法，用来阻塞当前线程的。

##### 独占锁的释放
* 独占锁的释放源码
    ```java
    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
    ```
    * 如果同步状态释放成功(`tryRelease`返回true)则会执行if块中的代码，当head指向的头结点不为null，并且该节点的状态值不为0的话才会执行`unparkSuccessor()`方法。
* `unparkSuccessor`方法源码:
    ```java
    private void unparkSuccessor(Node node) {
        int ws = node.waitStatus;
        if (ws < 0)
            compareAndSetWaitStatus(node, ws, 0);
        Node s = node.next;
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)
                    s = t;
        }
        if (s != null)
            LockSupport.unpark(s.thread);
    }
    ```
    * 首先获取头节点的后继节点，当后继节点的时候会调用`LookSupport.unpark()`方法，该方法会唤醒该节点的后继节点所包装的线程。
* 独占式锁的获取和释放的过程以及同步队列的总结:
    * 线程获取锁失败，线程被封装成`Node`进行入队操作，核心方法在于`addWaiter()`和`enq()`，同时`enq()`完成对同步队列的头结点初始化工作以及CAS操作失败的重试
    * 线程获取锁是一个自旋的过程，当且仅当当前节点的前驱节点是头结点并且成功获得同步状态时，节点出队即该节点引用的线程获得锁，否则，当不满足条件时就会调用`LookSupport.park()`方法使得线程阻塞
    * 在释放同步状态时，同步器会调用`unparkSuccessor()`方法唤醒后继节点。

##### 可中断式获取锁
* 我们知道`lock`相较于`synchronized`有一些更方便的特性，比如能响应中断以及超时等待等特性。
* 可响应中断式锁可调用方法`lock.lockInterruptibly()`，而该方法其底层会调用AQS的`acquireInterruptibly`方法。
* 源码:
    ```java
    public final void acquireInterruptibly(int arg) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        if (!tryAcquire(arg))
            doAcquireInterruptibly(arg);
    }
    ```
    * 在获取同步状态失败后就会调用`doAcquireInterruptibly`方法
    ```java
    private void doAcquireInterruptibly(int arg) throws InterruptedException {
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; 
                    failed = false;
                    return;
                }
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
    ```

##### 超时等待式获取锁
* 通过调用`lock.tryLock(timeout,TimeUnit)`方式达到超时等待获取锁的效果，该方法会在三种情况下才会返回:
    * 在超时时间内，当前线程成功获取了锁
    * 当前线程在超时时间内被中断
    * 超时时间结束，仍未获得锁返回false
* AQS的方法`tryAcquireNanos()`的源码:
    ```java
    public final boolean tryAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        return tryAcquire(arg) || doAcquireNanos(arg, nanosTimeout);
    }
    ```
    * 很显然这段源码最终是靠`doAcquireNanos`方法实现超时等待的效果，该方法源码如下:
    ```java
    private boolean doAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
        if (nanosTimeout <= 0L)
            return false;
        final long deadline = System.nanoTime() + nanosTimeout;
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null;
                    failed = false;
                    return true;
                }
                nanosTimeout = deadline - System.nanoTime();
                if (nanosTimeout <= 0L)
                    return false;
                if (shouldParkAfterFailedAcquire(p, node) &&
                    nanosTimeout > spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                if (Thread.interrupted())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
    ```
* 程序逻辑同独占锁可响应中断式获取基本一致，唯一的不同在于获取锁失败后，对超时时间的处理上，在第1步会先计算出按照现在时间和超时时间计算出理论上的截止时间，比如当前时间是8h10min,超时时间是10min，那么根据`deadline = System.nanoTime() + nanosTimeout`计算出刚好达到超时时间时的系统时间就是8h10min + 10min = 8h20min。
* 然后根据`deadline - System.nanoTime()`就可以判断是否已经超时了，比如，当前系统时间是8h 30min很明显已经超过了理论上的系统时间8h20min，`deadline - System.nanoTime()`计算出来就是一个负数，自然而然会在3.2步中的If判断之间返回false。

#### 共享锁
##### 共享锁的获取
* `acquireShared`的源码:
    ```java
    public final void acquireShared(int arg) {
        if (tryAcquireShared(arg) < 0)
            doAcquireShared(arg);
    }
    ```
    * `tryAcquireShared`返回值是一个int类型，当返回值为大于等于0的时候方法结束说明获得成功获取锁，否则，表明获取同步状态失败即所引用的线程获取锁失败，会执行`doAcquireShared`方法，该方法的源码为:
    ```java
    private void doAcquireShared(int arg) {
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                if (p == head) {
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        p.next = null;
                        if (interrupted)
                            selfInterrupt();
                        failed = false;
                        return;
                    }
                }
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
    ```

##### 共享锁的释放
* 共享锁的释放在AQS中会调用方法`releaseShared`:
    ```java
    public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            doReleaseShared();
            return true;
        }
        return false;
    }
    ```
    * 当成功释放同步状态之后即`tryReleaseShared`会继续执行`doReleaseShared`方法
    ```java
    private void doReleaseShared() {
        for (;;) {
            Node h = head;
            if (h != null && h != tail) {
                int ws = h.waitStatus;
                if (ws == Node.SIGNAL) {
                    if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                        continue;         
                    unparkSuccessor(h);
                }
                else if (ws == 0 && !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
                    continue;
            }
            if (h == head)
                break;
        }
    }
    ```
    * 这段方法跟独占式锁释放过程有点点不同，在共享式锁的释放过程中，对于能够支持多个线程同时访问的并发组件，必须保证多个线程能够安全的释放同步状态，这里采用的CAS保证，当CAS操作失败continue，在下一次循环中进行重试。


### ReentrantLock
* `ReentrantLock`重入锁，是实现`Lock`接口的一个类，也是在实际编程中使用频率很高的一个锁，支持重入性，表示能够对共享资源能够重复加锁，即当前线程获取该锁再次获取不会被阻塞。
* 在java关键字`synchronized`隐式支持重入性，`synchronized`通过获取自增，释放自减的方式实现重入。
* `ReentrantLock`还支持`公平锁`和`非公平锁`两种方式。

#### 重入性的实现原理
* 要想支持重入性，就要解决两个问题:
    * 在线程获取锁的时候，如果已经获取锁的线程是当前线程的话则直接再次获取成功；
    * 由于锁会被获取n次，那么只有锁在被释放同样的n次之后，该锁才算是完全释放成功。
* 针对第一个问题，我们来看看`ReentrantLock`是怎样实现的，以非公平锁为例，判断当前线程能否获得锁为例，核心方法为`nonfairTryAcquire`:
    ```java
    final boolean nonfairTryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
            if (compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        } else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0)
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }
    ```
* 为了支持重入性，在第二步增加了处理逻辑，如果该锁已经被线程所占有了，会继续检查占有线程是否为当前线程，如果是的话，同步状态加1返回true，表示可以再次获取成功。
* 释放重入锁，核心方法为`tryRelease`:
    ```java
    protected final boolean tryRelease(int releases) {
        int c = getState() - releases;
        if (Thread.currentThread() != getExclusiveOwnerThread())
            throw new IllegalMonitorStateException();
        boolean free = false;
        if (c == 0) {
            free = true;
            setExclusiveOwnerThread(null);
        }
        setState(c);
        return free;
    }
    ```
    * 重入锁的释放必须得等到同步状态为0时锁才算成功释放，否则锁仍未释放。
    * 如果锁被获取n次，释放了n-1次，该锁未完全释放返回false，只有被释放n次才算成功释放，返回true。

#### 公平锁与非公平锁
* ReentrantLock支持两种锁:
    * `公平锁`和`非公平锁`。
* 何谓公平性，是针对获取锁而言的，如果一个锁是公平的，那么锁的获取顺序就应该符合请求上的绝对时间顺序，满足FIFO。
* `ReentrantLock`的构造方法无参时是构造非公平锁，源码为:
    ```java
    public ReentrantLock() {
        sync = new NonfairSync();
    }
    ```
* 另外还提供了另外一种方式，可传入一个boolean值，true时为公平锁，false时为非公平锁，源码为:
    ```java
    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }
    ```
* 在上面非公平锁获取时(`nonfairTryAcquire`方法)只是简单的获取了一下当前状态做了一些逻辑处理，并没有考虑到当前同步队列中线程等待的情况。我们来看看公平锁的处理逻辑是怎样的，核心方法为:
    ```java
    protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
            if (!hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        } else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0)
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }
    ```
    * 这段代码的逻辑与`nonfairTryAcquire`基本上一直，唯一的不同在于增加了`hasQueuedPredecessors`的逻辑判断，方法名就可知道该方法用来判断当前节点在同步队列中是否有前驱节点的判断，如果有前驱节点说明有线程比当前线程更早的请求资源，根据公平性，当前线程请求资源失败。
    * 如果当前节点没有前驱节点的话，再才有做后面的逻辑判断的必要性。
* `公平锁`每次都是从同步队列中的第一个节点获取到锁，而`非公平性`锁则不一定，有可能刚释放锁的线程能再次获取到锁。
* `公平锁`VS`非公平锁`
    * `公平锁`每次获取到锁为同步队列中的第一个节点，保证请求资源时间上的绝对顺序，而`非公平锁`有可能刚释放锁的线程下次继续获取该锁，则有可能导致其他线程永远无法获取到锁，造成“饥饿”现象。
    * `公平锁`为了保证时间上的绝对顺序，需要频繁的上下文切换，而`非公平锁`会降低一定的上下文切换，降低性能开销。因此，`ReentrantLock`默认选择的是`非公平锁`，则是为了减少一部分上下文切换，保证了系统更大的吞吐量。

#### ReentrantReadWriteLock
* 大部分只是读数据，写数据很少，使用独占锁的话，很显然这将是出现性能瓶颈的地方。
* 针对这种读多写少的情况，java还提供了另外一个实现`Lock`接口的`ReentrantReadWriteLock`(读写锁)。读写所允许同一时刻被多个读线程访问，但是在写线程访问时，所有的读线程和其他的写线程都会被阻塞。
* 归纳总结:
    * 公平性选择: 支持非公平性(默认)和公平的锁获取方式，吞吐量还是非公平优于公平；
    * 重入性: 支持重入，读锁获取后能再次获取，写锁获取之后能够再次获取写锁，同时也能够获取读锁；
    * 锁降级: 遵循获取写锁，获取读锁再释放写锁的次序，写锁能够降级成为读锁

##### 写锁详解
* 在同一时刻写锁是不能被多个线程所获取，很显然写锁是独占式锁，而实现写锁的同步语义是通过重写AQS中的tryAcquire方法实现的。
* 源码:
    ```java
    protected final boolean tryAcquire(int acquires) {
        Thread current = Thread.currentThread();
        int c = getState();
        int w = exclusiveCount(c);
        if (c != 0) {
            if (w == 0 || current != getExclusiveOwnerThread())
                return false;
            if (w + exclusiveCount(acquires) > MAX_COUNT)
                throw new Error("Maximum lock count exceeded");
            setState(c + acquires);
            return true;
        }
        if (writerShouldBlock() || !compareAndSetState(c, c + acquires))
            return false;
        setExclusiveOwnerThread(current);
        return true;
    }
    ```
    * `exclusiveCount(c)`方法，该方法源码为:
    ```java
    static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }
    ```
* 其中`EXCLUSIVE_MASK`为: `static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;` 
* `EXCLUSIVE_MASK`为1左移16位然后减1，即为0x0000FFFF。
* 而`exclusiveCount`方法是将同步状态(state为int类型)与`0x0000FFFF`相与，即取同步状态的低16位。那么低16位代表什么呢？根据`exclusiveCount`方法的注释为独占式获取的次数即写锁被获取的次数，现在就可以得出来一个结论同步状态的低16位用来表示写锁的获取次数。
* 同时还有一个方法值得我们注意:
    ```java
    static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
    ```


