package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see IDictionary and the assignment page for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;
    private int chainSize;
    private int load;

    // You're encouraged to add extra fields (and helper methods) though!

    public ChainedHashDictionary() {
        this.chainSize = 8;
        this.load = 0;
        this.chains = makeArrayOfChains(this.chainSize);
        for (int i = 0; i < this.chainSize; i++) {
            this.chains[i] = new ArrayDictionary<>();
        }
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    private int keyHashIndex(K key, int size) {
        if (key == null) {
            return 0;
        }
        return Math.abs(key.hashCode() % size);
    }

    @Override
    public V get(K key) {
        if (!containsKey(key))      {
            throw new NoSuchKeyException("No such key!");
        }
        int hashedIndex = keyHashIndex(key, this.chainSize);
        IDictionary<K, V>  target = this.chains[hashedIndex];
        return target.get(key);
    }

    @Override
    public void put(K key, V value) {
        int hashedIndex = keyHashIndex(key, this.chainSize);
        if (!containsKey(key)) {
            this.load++;
        }
        this.chains[hashedIndex].put(key, value);
        rehash();
    }

    private void rehash() {
        double loadFactor = (double) this.load / this.chainSize;
        if (loadFactor >= 1.0) {
            IDictionary<K, V>[] newList = this.chains;
            this.chains = makeArrayOfChains(this.chainSize * 2);
            this.chainSize *= 2;
            this.load = 0;
            for (int i = 0; i < this.chainSize; i++) {
                this.chains[i] = new ArrayDictionary<>();
            }
            for (int i = 0; i < newList.length; i++){
                if (newList[i].size() != 0) {
                    for (KVPair<K, V> pairs : newList[i]) {
                        put(pairs.getKey(), pairs.getValue());
                    }
                }
            }
        }
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)) {
            throw new NoSuchKeyException("No such key!");
        }
        int hashedIndex = keyHashIndex(key, this.chainSize);
        load--;
        return chains[hashedIndex].remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        int hashedIndex = keyHashIndex(key, this.chainSize);
        if (chains[hashedIndex] != null) {
            return chains[hashedIndex].containsKey(key);
        }
        return false;
    }

    @Override
    public int size() {
        return this.load;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be 
     *    true once the constructor is done setting up the class AND 
     *    must *always* be true both before and after you call any 
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int indexVer;
        private Iterator<KVPair<K, V>> itr;


        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.indexVer = 0;
            this.itr = chains[indexVer].iterator();

            while (indexVer < this.chains.length-1  && !itr.hasNext()) {   // not null
                indexVer++;
                itr = this.chains[indexVer].iterator();
            }
        }

        @Override
        public boolean hasNext() {
            if (itr.hasNext()) {
                return  true;
            } else {
                while ((indexVer < chains.length - 1) && !itr.hasNext()) {   // not null
                    indexVer++;
                    itr = this.chains[indexVer].iterator();
                }
                return itr.hasNext();
            }
        }


        @Override
        public KVPair<K, V> next() {
            if (itr.hasNext()) {
                return itr.next();
            } else {
                while (indexVer < chains.length - 1 && !itr.hasNext()) {   // not null
                    indexVer++;
                    itr = chains[indexVer].iterator();
                }
                if (itr.hasNext()) {
                    return itr.next();
                } else {
                    throw new NoSuchElementException();
                }
            }
        }
    }
}
