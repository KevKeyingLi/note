@Counter(count = 1)
public class Main {

    public static void main(String[] args) throws Exception{
        Counter counter = Main.class.getAnnotation(Counter.class);
        System.out.println(counter.count());
    }
}