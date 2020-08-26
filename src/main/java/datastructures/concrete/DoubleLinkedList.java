package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods:
 * @see datastructures.interfaces.IList
 * (You should be able to control/command+click "IList" above to open the file from IntelliJ.)
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        Node<T> newNode = new Node<>(item);
        if (this.size == 0) {
            front = newNode;
            back = newNode;
        } else {
            this.back.next = newNode;
            newNode.prev = back;
            back = newNode;
        }
        size++;
    }

    private Node<T> getNodeAt(int index) {
        if (index < size / 2) {
            Node<T> check = front;
            for (int i = 0; i < index; i++) {
                check = check.next;
            }
            return check;
        } else {
            Node<T> check = back;
            for (int i = 0; i < size - 1 - index; i++) {
                check = check.prev;
            }
            return check;
        }
    }

    @Override //Edited method remove()
    public T remove() {
        if (this.size == 0) {
            throw new EmptyContainerException();
        }
        return delete(this.size - 1);
    }

    //Edited method get()
    @Override
    public T get(int index) {
        if (index >= this.size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> check = front;
        for (int i = 0; i < index; i++) {
            check = check.next;
        }
        return check.data;
    }

    //Edit method set
    @Override
    public void set(int index, T item) {
        delete(index);
        insert(index, item);
    }

    @Override
    public void insert(int index, T item) {
        if (index < 0 || index >= this.size + 1) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> newNode = new Node<>(item);
        if (this.size == 0) {   // empty list
            this.front = newNode;
            this.back = newNode;
        } else if (index == 0) {   // at front
            newNode.next = front;
            front.prev = newNode;
            front = newNode;
        } else if (index == this.size) {   // at the end
            newNode.prev = this.back;
            back.next = newNode;
            back = newNode;
        } else {   // in the middle
            Node<T> temp = getNodeAt(index);
            Node<T> prevNode = temp.prev;
            prevNode.next = newNode;
            newNode.next = temp;
            temp.prev = newNode;
            newNode.prev = prevNode;
        }
        size++;
    }

    @Override
    public T delete(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            Node<T> deleteItem = this.front;
            if (size == 1) {
                front = null;
                back = null;
            } else {
                this.front = this.front.next;
                front.prev = null;
            }
            this.size--;
            return deleteItem.data;
        } else if (index == this.size - 1) {
            Node<T> deleteItem = this.back;
            this.back = this.back.prev;
            this.back.next = null;
            this.size--;
            return deleteItem.data;
        } else {
            Node<T> deleteItem = getNodeAt(index);
            Node<T> prevNode = deleteItem.prev;
            Node<T> nextNode = deleteItem.next;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
            size--;
            return deleteItem.data;
        }
    }

    //implemented indexOf()
    @Override
    public int indexOf(T item) {
        int index = 0;
        Node<T> check = front;
        while (check != null) {
            if ((check.data != null && check.data.equals(item)) || (check.data == null && item == null)) {
                return index;
            }
            check = check.next;
            index++;
        }
        return -1;
    }

    //Implemented size()
    @Override
    public int size() {
        return this.size;
    }

    //Implemented contains()
    @Override
    public boolean contains(T other) {
        return indexOf(other) >= 0;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }


        //Implemented hasNext()
        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return current != null;
        }

        ///Implemented next()
        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            T temp = current.data;
            current = current.next;
            return temp;
        }
    }
}
