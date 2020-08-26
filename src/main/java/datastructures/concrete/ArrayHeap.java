package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;

/**
 * @see IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    private int size;
    private int sizeOfArray;

    // Feel free to add more fields and constants.

    public ArrayHeap() {
        this.size = 0;
        this.sizeOfArray = 20;
        this.heap = makeArrayOfT(sizeOfArray);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int arraySize) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[arraySize]);
    }

    @Override
    public T removeMin() {
        T out;
        if (size != 0) {
            out = heap[0];
            heap[0] = heap[size - 1];
            int min = minChild(heap, 0);
            for (int i = 0; i < size; i = min) {
                if (numberOfChildren(i) != 0) {
                    min = minChild(heap, i);
                    if (heap[min].compareTo(heap[i]) < 0) {
                        T temp = heap[min];
                        heap[min] = heap[i];
                        heap[i] = temp;
                    }
                } else {
                    break;
                }
            }
            size--;
        } else {
            throw new EmptyContainerException();
        }
        return out;
    }

    //returns the index of the smallest children of given node
    private int minChild(T[] heapIn, int index) {
        int minIndex = index * NUM_CHILDREN + 1;
        int numOfChildren = numberOfChildren(index);
        for (int i = 1; i <= numOfChildren; i++) {
            if (heapIn[index * NUM_CHILDREN + i].compareTo(heapIn[minIndex]) < 0) {
                minIndex = index * NUM_CHILDREN + i;
            }
        }
        return minIndex;
    }

    //return the number of children of given node.
    private int numberOfChildren(int index) {
        int count = 0;
        for (int i = 1; i <= NUM_CHILDREN; i++) {
            if (index * NUM_CHILDREN + i < size) {
                count = i;
            }
        }
        return count;
    }

    @Override
    public T peekMin() {
        if (this.size != 0) {
            return heap[0];
        }
        throw new EmptyContainerException();
    }

    @Override
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (size == sizeOfArray) {
            T[] temp = this.heap;
            this.heap = makeArrayOfT(this.sizeOfArray * 2);
            this.sizeOfArray *= 2;
            for (int i = 0; i < temp.length; i++) {
                this.heap[i] = temp[i];
            }
        }
        this.heap[size] = item;
        size++;
        for (int i = size - 1; i > 0; i = (i - 1) / NUM_CHILDREN) {
            int indexOfParent = (i - 1)/NUM_CHILDREN;
            if (heap[indexOfParent].compareTo(heap[i]) > 0) {
                T temp = this.heap[indexOfParent];
                heap[indexOfParent] = heap[i];
                heap[i] = temp;
            }
        }
    }

    @Override
    public int size() {
        return this.size;
    }
}
