### Difference between JDK and JRE and JVM

- JDK, JRE, and JVM are core concepts of Java programming language.
- Although they all look similar and as a programmer, we don’t care about these concepts a lot, but they are different and meant for specific purposes.

#### JDK

- Java Development Kit is the core component of Java Environment and provides all the tools, executables and binaries required to compile, debug and execute a Java Program.
- JDK is a platform-specific software and that’s why we have separate installers for Windows, Mac, and Unix systems.
- We can say that JDK is the superset of JRE since it contains JRE with Java compiler, debugger, and core classes.

#### JVM

- JVM is the heart of Java programming language.
- When we run a program, JVM is responsible for converting Byte code to the machine specific code.
- JVM is also platform dependent and provides core java functions like memory management, garbage collection, security etc.
- JVM is customizable and we can use java options to customize it, for example allocating minimum and maximum memory to JVM.
- JVM is called virtual because it provides an interface that does not depend on the underlying operating system and machine hardware.
- This independence from hardware and the operating system is what makes java program write-once-run-anywhere.

#### JRE

- JRE is the implementation of JVM, it provides a platform to execute java programs.
- JRE consists of JVM and java binaries and other classes to execute any program successfully.
- JRE doesn’t contain any development tools like java compiler, debugger etc.
- If you want to execute any java program, you should have JRE installed but we don’t need JDK for running any java program.

### JDK vs JRE vs JVM

- Let’s look at some of the important difference between JDK, JRE, and JVM.
  - JDK is for development purpose whereas JRE is for running the java programs.
  - JDK and JRE both contains JVM so that we can run our java program.
  - JVM is the heart of java programming language and provides platform independence.

#### Just-in-time Compiler (JIT)

- JIT is part of JVM that optimizes byte code to machine specific language compilation by compiling similar byte codes at the same time, hence reducing overall time taken for the compilation of byte code to machine specific language.
