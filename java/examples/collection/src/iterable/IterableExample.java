package iterable;

import java.util.Iterator;

public class IterableExample {

    public static void main(String[] args) {
        Iterable iterable1 = new Iterable() {
            @Override
            public Iterator iterator() {
                return null;
            }
        };
    }
}
