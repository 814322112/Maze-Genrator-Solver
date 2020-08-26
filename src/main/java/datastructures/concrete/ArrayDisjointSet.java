package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;


/**
 * @see IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private IDictionary<T, Integer> map;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        this.pointers = new int[8];
        map = new ChainedHashDictionary<>();
    }

    @Override
    public void makeSet(T item) {
        if (map.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        int size = map.size();
        map.put(item, size);
        if (map.size() >= pointers.length) {
            int[] newPointer = new int[pointers.length*2];
            System.arraycopy(pointers, 0, newPointer, 0, pointers.length);
            pointers = newPointer;
        }
        pointers[size] = -1;
    }

    @Override
    public int findSet(T item) {
        if (!map.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        int i = map.get(item);
        int start = i;
        while (pointers[i] >= 0) {
            i = pointers[i];
        }
        while (pointers[start] >= 0) {
            int temp = pointers[start];
            pointers[start] = i;
            start = temp;
        }
        return i;
    }

    @Override
    public void union(T item1, T item2) {
        int root1 = findSet(item1);
        int root2 = findSet(item2);
        int rank1 = -1 * (pointers[root1] + 1);
        int rank2 = -1 * (pointers[root2] + 1);
        if (root1 != root2) {
            if (rank1 > rank2) {
                pointers[root2] = root1;
            } else if (rank1 == rank2){
                pointers[root1] = root2;
                pointers[root2]--;
            } else {
                pointers[root1] = root2;
            }
        }
    }
}
