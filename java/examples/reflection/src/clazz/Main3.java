package clazz;

public class Main3 {

    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("club.throwable.reflect.Main3$Supper");
        Supper supper = (Supper) clazz.newInstance();
        System.out.println(supper.sayHello("throwable"));
    }

    public static class Supper {

        public String sayHello(String name) {
            return String.format("%s say hello!", name);
        }
    }
}