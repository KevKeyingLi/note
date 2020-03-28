package Classes.NestedClasses.MemberInnerClass;

public class Outer {

    String name = "MemberInnerClass";

    public void print() {
        PrivateInnerClass privateInnerClass = new PrivateInnerClass();
        DefaultInnerClass defaultInnerClass = new DefaultInnerClass();
        PublicInnerClass publicInnerClass = new PublicInnerClass();
        privateInnerClass.print();
        defaultInnerClass.print();
        publicInnerClass.print();
    }

    private class PrivateInnerClass {
        String name = "PrivateInnerClass";
        public void print() {
            System.out.println(Outer.this.name + "." + name);
        }
    }

    class DefaultInnerClass {
        String name = "DefaultInnerClass";
        public void print() {
            System.out.println(Outer.this.name + "." + name);
        }
    }

    public class PublicInnerClass {
        String name = "PublicInnerClass";
        public void print() {
            System.out.println(Outer.this.name + "." + name);
        }
    }

}
