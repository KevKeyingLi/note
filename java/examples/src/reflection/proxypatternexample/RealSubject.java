package reflection.proxypatternexample;

public class RealSubject implements Subject {

    @Override
    public void doSomething() {
        System.out.println("RealSubject doSomething...");
    }
}