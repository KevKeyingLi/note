- [集合容器概述](#集合容器概述)
    * [集合和数组的区别](#集合和数组的区别)
    * [使用集合框架的好处](#使用集合框架的好处)
- [集合容器接口](#集合容器接口)
    * [Collection接口](#Collection接口)
        * [List接口](#List接口)
            * [ArrayList](#ArrayList)
            * [LinkedList](#LinkedList)
            * [Vector](#Vector)
        * [Set接口](#Set接口)
            * [HashSet](#HashSet)
            * [TreeSet](#TreeSet)
        * [Queue接口](#Queue接口)
            * [ArrayDeque](#ArrayDeque)
    * [Map接口](#Map接口)
        * [HashMap](#HashMap)
        * [TreeMap](#TreeMap)
    * [Iterable接口](#Iterable接口)
        * [Iterator接口](#Iterator接口)
    * [Collections接口](#Collections接口)
    * [Arrays接口](#Arrays接口)
- [集合的琐事](#集合的琐事)
    * [线程安全的集合类](#线程安全的集合类)
    * [集合的快速失败机制fail-fast](#集合的快速失败机制fail-fast)
    * [Iterator和ListIterator的区别](#Iterator和ListIterator的区别)
    * [遍历一个List的方式](#遍历一个List的方式)
    * [ArrayList的优缺点](#ArrayList的优缺点)
    * [数组和List之间的转换](#数组和List之间的转换)
    * [ArrayList和LinkedList的区别](#ArrayList和LinkedList的区别)
    * [ArrayList和Vector的区别](#ArrayList和Vector的区别)
    * [多线程场景下的ArrayList](#多线程场景下的ArrayList)
    * [ArrayList的elementData上的transient](#ArrayList的elementData上的transient)
    * [List和Set的区别](#List和Set的区别)
    * [HashSet的实现原理](#HashSet的实现原理)
    * [hashCode()与equals()](#hashCode()与equals())
    * [==与equals的区别](#==与equals的区别)
    * [HashMap的实现原理](#HashMap的实现原理)

---
* refs:
    * > https://thinkwon.blog.csdn.net/article/details/104588551
    * > https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/package-summary.html
---

### 集合容器概述
* 用于存储数据的容器。

#### 集合和数组的区别
* 数组是固定长度的;集合可变长度的。
* 数组可以存储基本数据类型，也可以存储引用数据类型;集合只能存储引用数据类型。
* 数组存储的元素必须是同一个数据类型;集合存储的对象可以是不同数据类型。

#### 使用集合框架的好处
* 容量自增长;
* 提供了高性能的数据结构和算法，使编码更轻松，提高了程序速度和质量;
* 允许不同API之间的互操作，API之间可以来回传递集合;
* 可以方便地扩展或改写集合，提高代码复用性和可操作性。
* 通过使用JDK自带的集合类，可以降低代码维护和学习新API成本。


### 集合容器接口
#### Collection接口

#### List接口
* 一个有序容器，元素可以重复，可以插入多个null元素，元素都有索引。

##### ArrayList
* Object数组
* 数据结构
    ```java
    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_ELEMENTDATA = {};
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {}; 
    transient Object[] elementData; 
    private int size;
    protected transient int modCount = 0;

    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
        }
    }

    public boolean add(E e) {
        modCount++;
        add(e, elementData, size);
        return true;
    }

    private void add(E e, Object[] elementData, int s) {
        if (s == elementData.length)
            elementData = grow();
        elementData[s] = e;
        size = s + 1;
    }

    private Object[] grow() {
        return grow(size + 1);
    }

    private Object[] grow(int minCapacity) {
        return elementData = Arrays.copyOf(elementData, newCapacity(minCapacity));
    }

    private int newCapacity(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity <= 0) {
            if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
                return Math.max(DEFAULT_CAPACITY, minCapacity);
            if (minCapacity < 0) 
                throw new OutOfMemoryError();
            return minCapacity;
        }
        return (newCapacity - MAX_ARRAY_SIZE <= 0) ? newCapacity : hugeCapacity(minCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0)
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    public void add(int index, E element) {
        rangeCheckForAdd(index);
        modCount++;
        final int s;
        Object[] elementData;
        if ((s = size) == (elementData = this.elementData).length)
            elementData = grow();
        System.arraycopy(elementData, index, elementData, index + 1, s - index);
        elementData[index] = element;
        size = s + 1;
    }

    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    public E remove(int index) {
        Objects.checkIndex(index, size);
        final Object[] es = elementData;
        @SuppressWarnings("unchecked") E oldValue = (E) es[index];
        fastRemove(es, index);
        return oldValue;
    }

    public boolean remove(Object o) {
        final Object[] es = elementData;
        final int size = this.size;
        int i = 0;
        found: {
            if (o == null) {
                for (; i < size; i++)
                    if (es[i] == null)
                        break found;
            } else {
                for (; i < size; i++)
                    if (o.equals(es[i]))
                        break found;
            }
            return false;
        }
        fastRemove(es, i);
        return true;
    }

    private void fastRemove(Object[] es, int i) {
        modCount++;
        final int newSize;
        if ((newSize = size - 1) > i)
            System.arraycopy(es, i + 1, es, i, newSize - i);
        es[size = newSize] = null;
    }
    ```

##### LinkedList
* 双向循环链表
* 数据结构
    ```java
    transient int size = 0;
    transient Node<E> first;
    transient Node<E> last;

    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    public LinkedList() {}

    public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }

    public void add(int index, E element) {
        checkPositionIndex(index);
        if (index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));
    }

    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    void linkBefore(E e, Node<E> succ) {
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode;
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        size++;
        modCount++;
    }

    public E remove(int index) {
        checkElementIndex(index);
        return unlink(node(index));
    }

    private void checkElementIndex(int index) {
        if (!isElementIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }

    Node<E> node(int index) {
        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    E unlink(Node<E> x) {
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;
        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }
        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }
        x.item = null;
        size--;
        modCount++;
        return element;
    }

    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
    ```

##### Vector
* Object数组
* 数据结构
    ```java
    protected Object[] elementData;
    protected int elementCount;
    protected int capacityIncrement;

    public Vector(int initialCapacity, int capacityIncrement) {
        super();
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
        this.elementData = new Object[initialCapacity];
        this.capacityIncrement = capacityIncrement;
    }

    public Vector(int initialCapacity) {
        this(initialCapacity, 0);
    }

    public Vector() {
        this(10);
    }

    public synchronized boolean add(E e) {
        modCount++;
        add(e, elementData, elementCount);
        return true;
    }

    private void add(E e, Object[] elementData, int s) {
        if (s == elementData.length)
            elementData = grow();
        elementData[s] = e;
        elementCount = s + 1;
    }

    public void add(int index, E element) {
        insertElementAt(element, index);
    }

    public synchronized void insertElementAt(E obj, int index) {
        if (index > elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " > " + elementCount);
        }
        modCount++;
        final int s = elementCount;
        Object[] elementData = this.elementData;
        if (s == elementData.length)
            elementData = grow();
        System.arraycopy(elementData, index, elementData, index + 1, s - index);
        elementData[index] = obj;
        elementCount = s + 1;
    }

    private Object[] grow() {
        return grow(elementCount + 1);
    }

    private Object[] grow(int minCapacity) {
        return elementData = Arrays.copyOf(elementData, newCapacity(minCapacity));
    }

    private int newCapacity(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + ((capacityIncrement > 0) ? capacityIncrement : oldCapacity);
        if (newCapacity - minCapacity <= 0) {
            if (minCapacity < 0)
                throw new OutOfMemoryError();
            return minCapacity;
        }
        return (newCapacity - MAX_ARRAY_SIZE <= 0) ? newCapacity : hugeCapacity(minCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    public boolean remove(Object o) {
        return removeElement(o);
    }

    public synchronized boolean removeElement(Object obj) {
        modCount++;
        int i = indexOf(obj);
        if (i >= 0) {
            removeElementAt(i);
            return true;
        }
        return false;
    }

    public int indexOf(Object o) {
        return indexOf(o, 0);
    }

    public synchronized int indexOf(Object o, int index) {
        if (o == null) {
            for (int i = index ; i < elementCount ; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = index ; i < elementCount ; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    public synchronized void removeElementAt(int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
        } else if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int j = elementCount - index - 1;
        if (j > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, j);
        }
        modCount++;
        elementCount--;
        elementData[elementCount] = null; /* to let gc do its work */
    }
    ```

#### Set接口
* 一个无序容器，不可以存储重复元素，只允许存入一个null元素，必须保证元素唯一性。

##### HashSet
* 无序，唯一;
* 基于HashMap实现的，底层采用HashMap来保存元素
* 数据结构
    ```java
    private transient HashMap<E,Object> map;
    private static final Object PRESENT = new Object();

    public HashSet() {
        map = new HashMap<>();
    }

    public boolean add(E e) {
        return map.put(e, PRESENT)==null;
    }

    public boolean remove(Object o) {
        return map.remove(o)==PRESENT;
    }
    ```

##### TreeSet
* 有序，唯一; 
* 红黑树(自平衡的排序二叉树);
* 数据结构
    ```java
    private transient NavigableMap<E,Object> m;
    private static final Object PRESENT = new Object();

    public TreeSet() {
        this(new TreeMap<>());
    }

    TreeSet(NavigableMap<E,Object> m) {
        this.m = m;
    }

    public boolean add(E e) {
        return m.put(e, PRESENT)==null;
    }

    public E first() {
        return m.firstKey();
    }

    public boolean remove(Object o) {
        return m.remove(o)==PRESENT;
    }

    public E last() {
        return m.lastKey();
    }

    public E floor(E e) {
        return m.floorKey(e);
    }

    public E lower(E e) {
        return m.lowerKey(e);
    }

    public E pollFirst() {
        Map.Entry<E,?> e = m.pollFirstEntry();
        return (e == null) ? null : e.getKey();
    }

    public E pollLast() {
        Map.Entry<E,?> e = m.pollLastEntry();
        return (e == null) ? null : e.getKey();
    }
    ```

#### Queue接口
##### ArrayDeque
* 数据结构
    ```java
    transient Object[] elements;
    transient int head;
    transient int tail;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    public ArrayDeque() {
        elements = new Object[16];
    }

    public boolean add(E e) {
        addLast(e);
        return true;
    }

    public void addFirst(E e) {
        if (e == null)
            throw new NullPointerException();
        final Object[] es = elements;
        es[head = dec(head, es.length)] = e;
        if (head == tail)
            grow(1);
    }

    static final int dec(int i, int modulus) {
        if (--i < 0) 
            i = modulus - 1;
        return i;
    }

    public void addLast(E e) {
        if (e == null)
            throw new NullPointerException();
        final Object[] es = elements;
        es[tail] = e;
        if (head == (tail = inc(tail, es.length)))
            grow(1);
    }

    static final int inc(int i, int modulus) {
        if (++i >= modulus) 
            i = 0;
        return i;
    }

    public boolean offer(E e) {
        return offerLast(e);
    }

    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    public E poll() {
        return pollFirst();
    }

    public E pollFirst() {
        final Object[] es;
        final int h;
        E e = elementAt(es = elements, h = head);
        if (e != null) {
            es[h] = null;
            head = inc(h, es.length);
        }
        return e;
    }

    public E pollLast() {
        final Object[] es;
        final int t;
        E e = elementAt(es = elements, t = dec(tail, es.length));
        if (e != null)
            es[tail = t] = null;
        return e;
    }
    ```

#### Map接口
* Map是一个键值对集合，存储键、值和之间的映射
* Key无序，唯一
* value 不要求有序，允许重复
* Map没有继承于Collection接口，从Map集合中检索元素时，只要给出键对象，就会返回对应的值对象

##### HashMap
* 数组 + 链表 + 红黑树
* 数据结构
    ```java
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    static final int MAXIMUM_CAPACITY = 1 << 30;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int TREEIFY_THRESHOLD = 8;
    static final int UNTREEIFY_THRESHOLD = 6;
    static final int MIN_TREEIFY_CAPACITY = 64;

    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;

        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }

    transient Node<K,V>[] table;
    transient Set<Map.Entry<K,V>> entrySet;
    transient int size;
    transient int modCount;
    int threshold;
    final float loadFactor;

    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }

    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
        Node<K,V>[] tab; 
        Node<K,V> p; 
        int n, i;
        if ((tab = table) == null || (n = tab.length) == 0) // 表为空或长度为0
            n = (tab = resize()).length;
        if ((p = tab[i = (n - 1) & hash]) == null) // 要插入新Node的未知没东西，为null
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K,V> e; 
            K k;
            if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }

    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY && oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap]; // 先创建rawtype数组，再cast
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

    Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) {
        return new Node<>(hash, key, value, next);
    }

    final void treeifyBin(Node<K,V>[] tab, int hash) {
        int n, index; 
        Node<K,V> e;
        if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
            resize();
        else if ((e = tab[index = (n - 1) & hash]) != null) {
            TreeNode<K,V> hd = null, tl = null;
            do {
                TreeNode<K,V> p = replacementTreeNode(e, null);
                if (tl == null)
                    hd = p;
                else {
                    p.prev = tl;
                    tl.next = p;
                }
                tl = p;
            } while ((e = e.next) != null);
            if ((tab[index] = hd) != null)
                hd.treeify(tab);
        }
    }

    TreeNode<K,V> replacementTreeNode(Node<K,V> p, Node<K,V> next) {
        return new TreeNode<>(p.hash, p.key, p.value, next);
    }

    void afterNodeAccess(Node<K,V> p) { }
    void afterNodeInsertion(boolean evict) { }
    ```

##### TreeMap
* * 红黑树
* 数据结构
    ```java
    private final Comparator<? super K> comparator;
    private transient Entry<K,V> root;
    private transient int size = 0;
    private transient int modCount = 0;

    static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;
        Entry<K,V> left;
        Entry<K,V> right;
        Entry<K,V> parent;
        boolean color = BLACK;

        Entry(K key, V value, Entry<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;

            return valEquals(key,e.getKey()) && valEquals(value,e.getValue());
        }

        public int hashCode() {
            int keyHash = (key==null ? 0 : key.hashCode());
            int valueHash = (value==null ? 0 : value.hashCode());
            return keyHash ^ valueHash;
        }

        public String toString() {
            return key + "=" + value;
        }
    }

    public TreeMap() {
        comparator = null;
    }

    public V put(K key, V value) {
        Entry<K,V> t = root;
        if (t == null) {
            compare(key, key); // type (and possibly null) check

            root = new Entry<>(key, value, null);
            size = 1;
            modCount++;
            return null;
        }
        int cmp;
        Entry<K,V> parent;
        // split comparator and comparable paths
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            do {
                parent = t;
                cmp = cpr.compare(key, t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        else {
            if (key == null)
                throw new NullPointerException();
            @SuppressWarnings("unchecked")
                Comparable<? super K> k = (Comparable<? super K>) key;
            do {
                parent = t;
                cmp = k.compareTo(t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        Entry<K,V> e = new Entry<>(key, value, parent);
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
        fixAfterInsertion(e);
        size++;
        modCount++;
        return null;
    }

    @SuppressWarnings("unchecked")
    final int compare(Object k1, Object k2) {
        return comparator==null ? ((Comparable<? super K>)k1).compareTo((K)k2) : comparator.compare((K)k1, (K)k2);
    }  
    ```

#### Iterable接口
* 数据结构
    ```java
    Iterator<T> iterator();

    default void forEach(Consumer<? super T> action) {
            Objects.requireNonNull(action);
            for (T t : this) {
                action.accept(t);
            }
        }

    default Spliterator<T> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), 0);
    }
    ```

#### Iterator接口
* `Iterator`接口提供遍历任何`Collection`的接口。
* 我们可以从一个`Collection`中使用迭代器方法来获取迭代器实例。
* 迭代器取代了Java集合框架中的`Enumeration`，迭代器允许调用者在迭代过程中移除元素，使用`Iterator.remove()`方法。
* `Iterator`的特点是只能单向遍历，但是更加安全，因为它可以确保，在当前遍历的集合元素被更改的时候，就会抛出`ConcurrentModificationException`异常。
* 当使用`foreach`语句时，会自动生成一个`iterator`来遍历该list。
* 数据结构
    ```java
    boolean hasNext();
    E next();

    default void remove() {
        throw new UnsupportedOperationException("remove");
    }

    default void forEachRemaining(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        while (hasNext())
            action.accept(next());
    }
    ```

#### Collections接口
#### Arrays接口

### 集合的琐事
#### 线程安全的集合类
* vector: 就比arraylist多了个同步化机制(线程安全)，因为效率较低，现在已经不太建议使用。在web应用中，特别是前台页面，往往效率(页面响应速度)是优先考虑的。
* statck: 堆栈类，先进后出。vecter的子类。
* hashtable: 就比hashmap多了个线程安全。
* enumeration: 枚举，相当于迭代器。

#### 集合的快速失败机制fail-fast
* java集合的一种错误检测机制，当多个线程对集合进行结构上的改变的操作时，有可能会触发fail-fast机制。
* 假设存在两个线程(线程1、线程2)，线程1通过Iterator在遍历集合A中的元素，在某个时候线程2修改了集合A的结构(是结构上面的修改，而不是简单的修改集合元素的内容)，那么这个时候程序就会抛出`ConcurrentModificationException`异常，从而产生fail-fast机制。
* 迭代器在遍历时直接访问集合中的内容，并且在遍历过程中使用一个`modCount`变量。集合在被遍历期间如果内容发生变化，就会改变`modCount`的值。每当迭代器使用`hashNext()/next()`遍历下一个元素之前，都会检测`modCount`变量是否为`expectedmodCount`值，是的话就返回遍历；否则抛出异常，终止遍历。

#### Iterator和ListIterator的区别
* `Iterator`可以遍历`Set`和`List`集合，而`ListIterator`只能遍历`List`。
* `Iterator`只能单向遍历，而`ListIterator`可以双向遍历(向前/后遍历)。
* `ListIterator`实现`Iterator`接口，然后添加了一些额外的功能，比如添加一个元素、替换一个元素、获取前面或后面元素的索引位置。

#### 遍历一个List的方式
* `for`循环遍历，基于计数器。在集合外部维护一个计数器，然后依次读取每一个位置的元素，当读取到最后一个元素后停止。
* 迭代器遍历，`Iterator`。`Iterator`是面向对象的一个设计模式，目的是屏蔽不同数据集合的特点，统一遍历集合的接口。Java在`Collections`中支持了`Iterator`模式。
* `foreach`循环遍历。`foreach`内部也是采用了`Iterator`的方式实现，使用时不需要显式声明 Iterator 或计数器。优点是代码简洁，不易出错；缺点是只能做简单的遍历，不能在遍历过程中操作数据集合，例如删除、替换。
* 最佳实践:
    * Java Collections框架中提供了一个`RandomAccess`接口，用来标记`List`实现是否支持`RandomAccess`。
    * 如果一个数据集合实现了该接口，就意味着它支持`RandomAccess`，按位置读取元素的平均时间复杂度为`O(1)`，如`ArrayList`。
    * 如果没有实现该接口，表示不支持`RandomAccess`，如`LinkedList`。
    * 推荐的做法就是，支持`RandomAccess`的列表可用`for`循环遍历，否则建议用`Iterator`或`foreach`遍历。

#### ArrayList的优缺点
* 优点:
    * `ArrayList`底层以数组实现，是一种随机访问模式。`ArrayList`实现了`RandomAccess`接口，因此查找的时候非常快。
    * `ArrayList`在顺序添加一个元素的时候非常方便。
* 缺点:
    * 删除元素的时候，需要做一次元素复制操作。如果要复制的元素很多，那么就会比较耗费性能。
    * 插入元素的时候，也需要做一次元素复制操作，缺点同上。

#### 数组和List之间的转换
* 数组转`List`: 使用`Arrays.asList(array)`进行转换。
* `List`转数组: 使用`List`自带的`toArray()`方法。

#### ArrayList和LinkedList的区别
* 数据结构实现: `ArrayList`是动态数组的数据结构实现，而`LinkedList`是双向链表的数据结构实现。
* 随机访问效率: `ArrayList`比`LinkedList`在随机访问的时候效率要高，因为`LinkedList`是线性的数据存储方式，所以需要移动指针从前往后依次查找。
* 增加和删除效率: 在非首尾的增加和删除操作，`LinkedList`要比`ArrayList`效率要高，因为`ArrayList`增删操作要影响数组内的其他数据的下标。
* 内存空间占用: `LinkedList`比`ArrayList`更占内存，因为`LinkedList`的节点除了存储数据，还存储了两个引用，一个指向前一个元素，一个指向后一个元素。
* 线程安全: `ArrayList`和`LinkedList`都是不同步的，也就是不保证线程安全；
* 综合来说，在需要频繁读取集合中的元素时，更推荐使用`ArrayList`，而在插入和删除操作较多时，更推荐使用`LinkedList`。

#### ArrayList和Vector的区别
* 线程安全: `Vector`使用了`Synchronized`来实现线程同步，是线程安全的，而`ArrayList`是非线程安全的。
* 性能: `ArrayList`在性能方面要优于`Vector`。
* 扩容: `ArrayList`和`Vector`都会根据实际的需要动态的调整容量，只不过在`Vector`扩容每次会增加1倍，而`ArrayList`只会增加50%。

#### 多线程场景下的ArrayList
* `ArrayList`不是线程安全的，如果遇到多线程场景，可以通过`Collections`的`synchronizedList`方法将其转换成线程安全的容器后再使用。
    ```java
    List<String> synchronizedList = Collections.synchronizedList(list);
    synchronizedList.add("aaa");
    synchronizedList.add("bbb");
    for (int i = 0; i < synchronizedList.size(); i++) {
        System.out.println(synchronizedList.get(i));
    }
    ```

#### ArrayList的elementData上的transient
* `ArrayList`中的数组定义如下:
    ```java
    private transient Object[] elementData;
    ```
* `ArrayList`的定义:
    ```java
    public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable
    ```
* `ArrayList`实现了`Serializable`接口，这意味着`ArrayList`支持序列化。
* `transient`的作用是说不希望`elementData`数组被序列化，重写了`writeObject`实现:
    ```java
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException{
        int expectedModCount = modCount;
        s.defaultWriteObject();
        s.writeInt(elementData.length);
        for (int i=0; i<size; i++)
            s.writeObject(elementData[i]);
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }   
    }
    ```
    * 每次序列化时，先调用`defaultWriteObject()`方法序列化`ArrayList`中的非`transient`元素，然后遍历`elementData`，只序列化已存入的元素，这样既加快了序列化的速度，又减小了序列化之后的文件大小。

#### List和Set的区别
* `List`特点: 一个有序（元素存入集合的顺序和取出的顺序一致）容器，元素可以重复，可以插入多个null元素，元素都有索引。常用的实现类有 ArrayList、LinkedList 和 Vector。
* `Set`特点: 一个无序（存入和取出顺序有可能不一致）容器，不可以存储重复元素，只允许存入一个null元素，必须保证元素唯一性。Set 接口常用实现类是 HashSet、LinkedHashSet 以及 TreeSet。
* 另外`List`支持`for`循环，也就是通过下标来遍历，也可以用迭代器，但是set只能用迭代，因为他无序，无法用下标来取得想要的值。
* `Set`: 检索元素效率低下，删除和插入效率高，插入和删除不会引起元素位置改变。
* `List`: 和数组类似，`List`可以动态增长，查找元素效率高，插入删除元素效率低，因为会引起其他元素位置改变

#### HashSet的实现原理
* `HashSet`是基于`HashMap`实现的，`HashSet`的值存放于`HashMap`的`key`上，`HashMap`的`value`统一为`PRESENT`，因此`HashSet`的实现比较简单，相关`HashSet`的操作，基本上都是直接调用底层`HashMap`的相关方法来完成，`HashSet`不允许重复的值。

#### hashCode()与equals()
* 如果两个对象相等，则`hashcode`一定也是相同的
* 两个对象相等,对两个`equals`方法返回true
* 两个对象有相同的`hashcode`值，它们也不一定是相等的，因为他们的类加载器可能不一样。
* 综上，`equals`方法被覆盖过，则`hashCode`方法也必须被覆盖
* `hashCode()`的默认行为是对堆上的对象产生独特值。如果没有重写`hashCode()`，则该class的两个对象无论如何都不会相等(即使这两个对象指向相同的数据)。

#### ==与equals的区别
* `==`是判断两个变量或实例是不是指向同一个内存空间`equals`是判断两个变量或实例所指向的内存空间的值是不是相同
* `==`是指对内存地址进行比较`equals()`是对字符串的内容进行比较
* `==`指引用是否相同`equals()`指的是值是否相同

#### HashMap的实现原理
* `HashMap`概述: `HashMap`是基于哈希表的`Map`接口的非同步实现。此实现提供所有可选的映射操作，并允许使用null值和null键。此类不保证映射的顺序，特别是它不保证该顺序恒久不变。
* `HashMap`的数据结构: 数组 + 链表
* `HashMap`基于Hash算法实现的:
    * 当我们往`Hashmap`中`put`元素时，利用`key`的`hashCode`重新hash计算出当前对象的元素在数组中的下标
    * 存储时，如果出现hash值相同的key，此时有两种情况
        * 如果key相同，则覆盖原始值
        * 如果key不同(出现冲突)，则将当前的key-value放入链表中
    * 获取时，直接找到hash值对应的下标，在进一步判断key是否相同，从而找到对应值。
    * 当链表中的节点数据超过八个之后，该链表会转为红黑树来提高查询效率，从原来的`O(n)`到`O(logn)`


