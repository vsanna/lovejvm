package java.util;

public class ArrayList<E> implements List<E> {
    private Object[] elementData; // non-private to simplify nested class access
    private int size = 0;

    public ArrayList() {
        this.elementData = new Object[0];
    }

    public ArrayList(int capacity) {
        this.elementData = new Object[capacity];
    }

    public int size(){
        return size;
    }

    public boolean isEmpty() {
        return elementData.length == 0;
    }

    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    public int indexOf(Object o) {
        return indexOfRange(o, 0, size);
    }

    int indexOfRange(Object o, int start, int end) {
        Object[] es = elementData;
        if (o == null) {
            for (int i = start; i < end; i++) {
                if (es[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                if (o.equals(es[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean add(E e) {
        if (size == elementData.length) {
            elementData = grow();
        }
        elementData[size] = e;
        size = size + 1;

        return true;
    }

    public E get(int index) {
        return (E) elementData[index];
    }

    public E set(int index, E element) {
        elementData[index] = element;
        return element;
    }

    private Object[] grow() {
        return grow(size + 1);
    }
    private Object[] grow(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity * 2;

        Object[] newElementData = new Object[newCapacity];
        for (int i = 0; i < elementData.length; i++) {
            newElementData[i] = elementData[i];
        }

        return newElementData;
    }
}
