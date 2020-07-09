package list;

import java.util.*;

public class ListExample {

    public static void main(String[] args) {
        System.out.println("ArrayList");
        List<Integer> arrayList1 = new ArrayList<>();
        arrayList1.add(1);
        arrayList1.add(2);
        arrayList1.add(3);
        for(int num : arrayList1) {
            System.out.println(num);
        }

        System.out.println("\n--------------------");
        List<Integer> arrayList2 = new ArrayList<>(2);
        arrayList2.add(4);
        arrayList2.add(5);
        arrayList2.add(6);
        for(int num : arrayList2) {
            System.out.println(num);
        }

        System.out.println("\n--------------------");
        Set<Integer> hashSet1 = new HashSet<>();
        hashSet1.add(7);
        hashSet1.add(8);
        hashSet1.add(9);
        ArrayList<Integer> arrayList3 = new ArrayList<>(hashSet1);
        for(int num : arrayList3) {
            System.out.println(num);
        }

        System.out.println("\n====================");
        System.out.println("void add(int index, E element)");
        arrayList1.add(1, 10);
        for(int num : arrayList1) {
            System.out.println(num);
        }

        System.out.println("\n--------------------");
        System.out.println("boolean addAll(int index, Collection<? extends E> c)");
        arrayList1.addAll(2, arrayList2);
        for(int num : arrayList1) {
            System.out.println(num);
        }

        System.out.println("\n--------------------");
        System.out.println("Object clone()");
        for(int num : arrayList3) {
            System.out.println(num);
        }
        List<Integer> arrayList4 = (ArrayList)arrayList3.clone();
        arrayList4.add(10);
        System.out.println("--------------------");
        for(int num : arrayList3) {
            System.out.println(num);
        }
        System.out.println("--------------------");
        for(int num : arrayList4) {
            System.out.println(num);
        }

        System.out.println("\n--------------------");
        System.out.println("int indexOf(Object o)");
        System.out.println(arrayList2.indexOf(5));

        System.out.println("\n--------------------");
        System.out.println("Iterator<E> iterator()");
        Iterator it1 = arrayList1.iterator();
        while(it1.hasNext()) {
            System.out.println(it1.next());
        }

        System.out.println("\n--------------------");
        System.out.println("E remove(int index)");
        int removed = arrayList1.remove(1);
        System.out.println(removed);
        System.out.println("--------------------");
        for(int num : arrayList1) {
            System.out.println(num);
        }

        System.out.println("\n--------------------");
        System.out.println("boolean remove(Object o)");
        boolean removedSuccess = arrayList1.remove(new Integer(1));
        System.out.println(removedSuccess);
        System.out.println("--------------------");
        for(int num : arrayList1) {
            System.out.println(num);
        }

        System.out.println("\n####################\n");
    }
}
