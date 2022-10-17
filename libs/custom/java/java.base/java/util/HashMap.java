package java.util;

public class HashMap<K,V> implements Map<K,V> 
{
    private int capacity;

    /**
     * [
     *   [[k, v], [k, v], ....],
     *   [[k, v], [k, v], ....],
     *   [[k, v], [k, v], ....],
     * ]
     * */
    private final Object[][][] table;

    public HashMap() {
        this(10);
    }

    public HashMap(int capacity) {
        this.capacity = capacity;
        this.table = new Object[capacity][10][2];
    }

    public V get(Object key) {
        int index = key.hashCode()%capacity;
        for (int i = 0; i < table[index].length; i++) {
            Object[] kv = table[index][i];
            if(key.equals(kv[0])) {
                return (V) kv[1];
            }
        }

        return null;
    }

    public V put(K key, V value) {
        int index = key.hashCode()%capacity;
        for (int i = 0; i < table[index].length; i++) {
            Object[] kv = table[index][i];
            if(key.equals(kv[0])) {
                table[index][i][1] = value;
                return value;
            }
            if(kv[0] == null) {
                table[index][i] = new Object[]{key, value};
                return value;
            }
        }
        throw new RuntimeException("capacity is over");
    }

    public V remove(Object key){
        int index = key.hashCode()%capacity;
        for (int i = 0; i < table[index].length; i++) {
            Object[] kv = table[index][i];
            if(kv[0] == key) {
                table[index][i] = new Object[]{null, null};
                return (V) kv[1];
            }
        }
        return null;
    }

    public int size() {
        throw new UnsupportedOperationException("");
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsKey(Object key) {
        int index = key.hashCode()%capacity;
        for (int i = 0; i < table[index].length; i++) {
            Object[] kv = table[index][i];
            if(kv[0] == key) {
                return true;
            }
        }
        return false;
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("");
    }

    // TODO: probably default impl doesn't work now?
    public V getOrDefault(Object key, V defaultValue) {
        V v;
        return (((v = get(key)) != null) || containsKey(key))
            ? v
            : defaultValue;
    }
}
