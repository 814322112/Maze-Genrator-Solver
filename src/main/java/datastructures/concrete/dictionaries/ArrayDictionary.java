package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see datastructures.interfaces.IDictionary
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field.
    // We will be inspecting it in our private tests.
    private Pair<K, V>[] pairs;
    private int size;


    // You may add extra fields or helper methods though!

    public ArrayDictionary() {
        this.pairs = makeArrayOfPairs(8);
        this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }

    @Override
    public V get(K key) {
        if (containsKey(key)) {
            for (int i = 0; i < pairs.length; i++) {
                Pair<K, V> temp = pairs[i];
                if ((temp.key != null && temp.key.equals(key)) || (temp.key == null && key == null)) {
                    return temp.value;
                }
            }
        }
        throw new NoSuchKeyException();
    }

    @Override
    public void put(K key, V value) {
        if (!containsKey(key)) {
            if (size == pairs.length) {
                Pair<K, V>[] temp = makeArrayOfPairs(2 * pairs.length);
                for (int i = 0; i < pairs.length; i++) {
                    temp[i] = pairs[i];
                }
                pairs = temp;
            }
            Pair<K, V> temp = new Pair<>(key, value);
            pairs[size] = temp;
            size++;
        } else {


            for (int i = 0; i < this.size; i++) {
                if ((pairs[i].key != null && pairs[i].key.equals(key)) ||
                        (pairs[i].key == null && key == null)) {
                    pairs[i].value = value;
                }
            }
        }
    }

    @Override
    public V remove(K key) {
        if (containsKey(key)) {
            for (int i = 0; i < pairs.length; i++) {
                if ((pairs[i].key != null && pairs[i].key.equals(key)) || (pairs[i].key == null && key == null)) {
                    V temp = pairs[i].value;
                    pairs[i] = pairs[size - 1];
                    pairs[size - 1] = null;
                    size--;
                    return temp;
                }
            }
        }
        throw new NoSuchKeyException();
    }

    @Override
    public boolean containsKey(K key) {
        for (int i = 0; i < size; i++) {
            Pair<K, V> temp = pairs[i];
            if ((temp.key != null && temp.key.equals(key)) || (temp.key == null && key == null)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator(){
        return new ArrayDictionaryIterator<>(this.pairs, this.size);
    }


    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        private Pair<K, V>[] pairs;
        private int index;
        private int size;

        public ArrayDictionaryIterator(Pair<K, V>[] pairs, int size) {
            this.index = 0;
            this.pairs = pairs;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return this.index < size;
        }

        @Override
        public KVPair<K, V> next() {
            if (hasNext()) {
                KVPair<K, V> temp = new KVPair<>(pairs[index].key, pairs[index].value);
                index++;
                return temp;
            }
            throw new NoSuchElementException();
        }
    }
    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }


        public Pair() {
            this.key = null;
            this.value = null;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
