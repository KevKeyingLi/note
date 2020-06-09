package reflection.proxypatternexample;

public class ProxySubject implements Subject {

    private final Subject subject;

    public ProxySubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public void doSomething() {
        subject.doSomething();
        doOtherThing();
    }

    private void doOtherThing() {
        System.out.println("ProxySubject doOtherThing...");
    }
}
