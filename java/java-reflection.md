- [核心类库和方法](#核心类库和方法)
    * [图解反射核心类的体系](#图解反射核心类的体系)

---
* refs:
    * > https://www.cnblogs.com/throwable/p/12272229.html
---

## 核心类库和方法
* 反射(Reflection)是一种可以在运行时检查和动态调用类、构造、方法、属性等等的编程语言的能力，甚至可以不需要在编译期感知类的名称、方法的名称等等。
* 反射的缺点:
    * 性能开销：由于反射涉及动态解析的类型，因此无法执行某些Java虚拟机优化。因此，反射操作的性能低于非反射操作，应避免在性能敏感应用程序中频繁调用反射操作代码片段。
    * 安全限制：反射需要运行时权限，不能在安全管理器(security manager)下进行反射操作。
    * 代码可移植性：反射代码打破了抽象，反射的类库有可能随着平台(JDK)升级发生改变，反射代码中允许执行非反射代码的逻辑例如允许访问私有字段，这些问题都有可能影响到代码的可移植性。
* 反射相关的类库集中在`java.lang.reflect`包和`java.lang`包中。

### 图解反射核心类的体系
* `java.lang.reflect`包反射核心类有核心类`Class`、`Constructor`、`Method`、`Field`、`Parameter`。
    * 共有的父接口是AnnotatedElement。
    * Constructor、Method、Field共有的父类是AnnotatedElement、AccessibleObject和Member。
    * Constructor、Method共有的父类是AnnotatedElement、AccessibleObject、Member、GenericDeclaration和Executable。
* 在Class中，`getXXX()`方法和`getDeclearedXXX()`方法有所区别。注解类型`Annotation`的操作方法例外，因为基于注解的修饰符必定是public的
    * `getDeclaredMethod(s)`: 返回类或接口声明的所有方法，包括公共、保护、默认(包)访问和私有方法，但不包括继承的方法。对于获取Method对象，`Method[] methods = clazz.getDeclaredMethods()`;返回的是clazz本类所有修饰符(public、default、private、protected)的方法数组，但是不包含继承而来的方法。
    * `getMethod(s)`: 返回某个类的所有公用(public)方法包括其继承类的公用方法，当然也包括它所实现接口的方法。对于获取Method对象，`Method[] methods = clazz.getMethods()`;表示返回clazz的父类、父类接口、本类、本类接口中的全部修饰符为public的方法数组。
    * `getDeclaredField(s)`和`getField(s)`、`getDeclaredConstructor(s)`和`getConstructor(s)`同上。
    * `getDeclaredAnnotation(s)`: 返回直接存在于此元素上的所有注解，此方法将忽略继承的注解，准确来说就是忽略`@Inherited`注解的作用。
    * `getAnnotation(s)`: 返回此元素上存在的所有注解，包括继承的所有注解。
















