package gre.lab2.groupA;

import gre.lab2.graph.BFYResult;
import gre.lab2.graph.IBellmanFordYensAlgorithm;
import gre.lab2.graph.WeightedDigraph;

import java.util.*;

/**
 * Class used to perform the Yen's improvement of the Bellman-Ford algorithm
 * @author Lucas Charbonnier
 * @author Michael Strefeler
 */
public final class BellmanFordYensAlgorithm implements IBellmanFordYensAlgorithm {
    private static final int INFINITY = Integer.MAX_VALUE;

    private LinkedList<Integer> queue;

    /**
     * Array of booleans used to check if a vertex is in the queue or not
     */
    private boolean[] inQueue;

    /**
     * Add a vertex to the queue
     * @param vertex vertex to add
     */
    void addToQueue(Integer vertex) {
        queue.addLast(vertex);
        inQueue[vertex] = true;
    }

    /**
     * Add a vertex to the queue if it's not already in said queue
     * @param vertex vertex to add
     */
    void addToQueueIfNotAlreadyIn(Integer vertex) {
        if(!inQueue[vertex])
            addToQueue(vertex);
    }

    /**
     * Get the next vertex from the FIFO queue
     * @return the number of that vertex
     */
    Integer nextFromQueue() {
        var item = queue.removeFirst();
        inQueue[item] = false;
        return item;
    }

    /**
     *
     * @param graph the graph that we want to perform the algorithm on
     * @param from the vertex from where the algorithm needs to start (source)
     * @return the result of the algorithm as an instance of the {@link BFYResult} class
     */
    @Override
    public BFYResult compute(WeightedDigraph graph, int from) {
        final int N = graph.getNVertices(); // Number of vertices

        int[] pred = new int[N]; // Array of predecessors
        Arrays.fill(pred, BFYResult.UNREACHABLE);

        int[] d = new int[N]; // Array of distances from the source
        Arrays.fill(d, INFINITY);

        d[from] = 0;

        int k = 0; // Number of iterations

        // FIFO queue
        queue = new LinkedList<Integer>();
        inQueue = new boolean[N + 1]; // n + 1 for the sentinel

        addToQueue(from);
        addToQueue(N);

        while(!queue.isEmpty()) {
            int current = nextFromQueue();
            if(current == N) {
                if(!queue.isEmpty()) {
                    k++;
                    if(k == N) {
                        // looking for a vertex with a negative distance from source s
                        for(int vertex = 0; vertex < d.length; ++vertex) {
                            if(d[vertex] < 0) {
                                var circuit = new ArrayList<Integer>();

                                // 'vertex' is part of a negative cost circuit
                                int circuit_begin = vertex;
                                // reverse-exploring the circuit to find all vertices
                                do {
                                    if(d[vertex] < 0)
                                        System.out.println(vertex + " " + circuit_begin);
                                    circuit.add(vertex);
                                }
                                while((vertex = pred[vertex]) != circuit_begin);

                                var circuitInCorrectOrder = circuit.reversed();

                                int circuitLength = 0;
                                for(int i = 0; i < circuitInCorrectOrder.size(); ++i) {
                                    var v = circuitInCorrectOrder.get(i);

                                    if(i+1 < circuitInCorrectOrder.size()) {
                                        var next = circuitInCorrectOrder.get(i+1);

                                        for(var edge : graph.getOutgoingEdges(v)) {
                                            if(edge.to() == next) {
                                                circuitLength += edge.weight();
                                                break;
                                            }
                                        }
                                    }
                                }
                                return new BFYResult.NegativeCycle(circuitInCorrectOrder, circuitLength);
                            }
                        }
                        throw new RuntimeException("Un circuit absorbant aurait dû être trouvé!");
                    }
                    else {
                        addToQueue(N); // Add the sentinel back to the queue
                    }
                }
            }
            else {
                var outgoingEdges = graph.getOutgoingEdges(current);
                for(var outgoingEdge : outgoingEdges) {
                    int successor = outgoingEdge.to();
                    // λj > λi + cij --> update distance and predecessor of the 'successor' vertex
                    if(d[successor] > d[current] + outgoingEdge.weight()){
                        d[successor] = d[current] + outgoingEdge.weight();
                        pred[successor] = current;
                        addToQueueIfNotAlreadyIn(successor);
                    }
                }
            }
        }
        return new BFYResult.ShortestPathTree(d, pred);
    }
}
