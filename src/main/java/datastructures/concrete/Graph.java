package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IEdge;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.Sorter;
import misc.exceptions.NoPathExistsException;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends IEdge<V> & Comparable<E>> {
    private IList<V> vertices;
    private IList<E> edges;
    private IDictionary<V, ISet<E>> adjList;
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated than usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've constrained Graph
    //   so that E *must* always be an instance of IEdge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the IEdge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    public Graph(IList<V> vertices, IList<E> edges) {
        this.vertices = vertices;
        this.edges = edges;
        adjList = new ChainedHashDictionary<>();
        for (V vertex : this.vertices) {
            if (vertex == null) {
               throw new IllegalArgumentException();
            }
            adjList.put(vertex, new ChainedHashSet<>());
        }

        for (E edge : this.edges) {
            V v1 = edge.getVertex1();
            V v2 = edge.getVertex2();
            if (!adjList.containsKey(v1) || !adjList.containsKey(v2) || edge.getWeight() < 0) {
                throw new IllegalArgumentException();
            }
            ISet<E> neighbor1 = adjList.get(v1);
            ISet<E> neighbor2 = adjList.get(v2);
            neighbor1.add(edge);
            neighbor2.add(edge);
            adjList.put(v1, neighbor1);
            adjList.put(v2, neighbor2);
        }
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        if (set == null) {
            throw new IllegalArgumentException();
        }
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return vertices.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return  edges.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        IList<E> sortedEdges = Sorter.topKSort(numEdges(), edges);
        ISet<E> result = new ChainedHashSet<>();
        IDisjointSet<V> disjset = new ArrayDisjointSet<>();
        for (V vertex : vertices) {
            disjset.makeSet(vertex);
        }
        int index = 0;
        while (result.size() < vertices.size() - 1) {
            E singleEdge = sortedEdges.get(index);
            V v1 = singleEdge.getVertex1();
            V v2 = singleEdge.getVertex2();
            if (disjset.findSet(v1) != disjset.findSet(v2)) {
                disjset.union(v1, v2);
                result.add(singleEdge);
            }
            index++;
        }
        return result;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     * @throws IllegalArgumentException if start or end is null
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        IList<E> result = new DoubleLinkedList<>();
        if (start.equals(end)) {
            return result;
        }
        if (start == null || end == null) {
            throw new IllegalArgumentException();
        }
        IPriorityQueue<Vertex> mpq = new ArrayHeap<>();
        IDictionary<V, Vertex> nodes = new ChainedHashDictionary<>();
        IDictionary<V, Vertex> processed = new ChainedHashDictionary<>();
        for (V vert: vertices) {
            Vertex temp = new Vertex(vert);
            if (vert.equals(start)) {
                temp.cost = 0;
            }
            mpq.insert(temp);
            nodes.put(vert, temp);
        }
        while (processed.size() < vertices.size()) {
            Vertex u = mpq.removeMin();
            if (!processed.containsKey(u.v)) {
                processed.put(u.v, u);
                for (E edge : adjList.get(u.v)) {
                    V neighbor = edge.getOtherVertex(u.v);
                    if (nodes.containsKey(neighbor)) {
                        double oldDist = nodes.get(neighbor).cost;
                        double newDist = u.cost + edge.getWeight();
                        if (newDist < oldDist) {
                            nodes.put(neighbor, new Vertex(neighbor, edge, newDist));
                            mpq.insert(new Vertex(neighbor, edge, newDist));
                        }
                    }
                }
            }
        }
        if (!processed.containsKey(end) || processed.get(end).cost == Double.POSITIVE_INFINITY) {
            throw new NoPathExistsException();
        }
        E edgeBack = processed.get(end).e;
        V otherVectex = end;
        while (!otherVectex.equals(start)) {
            result.insert(0, edgeBack);
            otherVectex = edgeBack.getOtherVertex(otherVectex);
            edgeBack = processed.get(otherVectex).e;
        }
        return  result;
    }

    private class Vertex implements Comparable<Vertex> {
        private V v;
        private E e;
        private double cost;

        public Vertex(V v, E e, double cost) {
            this.v = v;
            this.e = e;
            this.cost = cost;
        }

        public Vertex(V v){
            this.v = v;
            this.cost = Double.POSITIVE_INFINITY;
        }

        @Override
        public int compareTo(Vertex o) {
            return Double.compare(this.cost, o.cost);
        }
    }
}
