* `Throwable`是所以exception的超类
* `Throwable`有两个子类: `Error`和`Exception`
* `Throwable`包含了其线程创建时线程执行堆栈的快照，它提供了`printStackTrace()`等接口用于获取堆栈跟踪数据等信息
* `Error`类及其子类为程序中无法处理的严重错误
* `Error`类及其子类为不受检异常
* `Exception`为程序本身可以捕获并且可以处理的异常
* `Exception`这种异常又分为两类: `运行时异常`和`编译时异常`
* `RuntimeException`类及其子类，表示JVM在运行期间可能出现的异常
* Java编译器不会检查`RuntimeException`，即使存在还是会编译通过
* `RuntimeException`异常会由Java虚拟机自动抛出并自动捕获
* 编译时异常是`Exception`中除`RuntimeException`及其子类之外的异常
* 编译时异常要么通过`throws`进行声明抛出，要么通过`try-catch`进行捕获处理，否则不能通过编译
* 通常不会自定义编译时异常
* Java异常分为受检异常(checked exception)和非受检异常(unchecked exception)
* 编译器要求必须处理受检异常，包括编译时异常
* 非受检异常不要求必须处理，包括`RuntimeException`与`Error`与他们的子类
* `try`用于监听代码，当try语句块中发生异常，异常就会被抛出
* `catch`用于捕获异常，提供处理指定异常的代码
* `finally`总是会被执行，通常用于关闭或回收资源
* `throw`用于抛出异常
* `throws`用在方法签名中，用于声明该方法可能抛出的异常
