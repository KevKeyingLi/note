package proxypattern;

public class Client {

    public static void main(String[] args) throws Exception {
        Subject subject = new RealSubject();
        ProxySubject proxySubject = new ProxySubject(subject);
        proxySubject.doSomething();
    }
}

// RealSubject doSomething...
// ProxySubject doOtherThing...