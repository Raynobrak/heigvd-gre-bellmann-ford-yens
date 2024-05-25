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

    private ArrayDeque<Integer> queue;

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

        int[] predecessors = new int[N]; // Array of best predecessors, all unreachable by default
        Arrays.fill(predecessors, BFYResult.UNREACHABLE);

        int[] d = new int[N]; // Array of distances from the source, all equal to infinity by default
        Arrays.fill(d, INFINITY);

        d[from] = 0; // source is at a distance zero from the source

        int iterationNumber = 0; // Number of iterations

        // Queue containing the vertices to process
        queue = new ArrayDeque<Integer>();
        inQueue = new boolean[N + 1]; // n + 1 for the sentinel

        addToQueue(from);
        addToQueue(N);

        while(!queue.isEmpty()) {
            int current = nextFromQueue();
            if(current == N) {
                if(!queue.isEmpty()) {
                    iterationNumber++;
                    if(iterationNumber == N) {
                        // looking for a vertex with a negative distance from source s
                        for(int vertex = 0; vertex < d.length; ++vertex) {
                            if(d[vertex] < 0) {
                                var circuit = new ArrayList<Integer>();

                                var verticesOccurences = new int[N];

                                // 'vertex' is part of a negative cost circuit
                                int circuit_begin = vertex;

                                // reverse-exploring the circuit to find all vertices
                                do {
                                    verticesOccurences[vertex]++;

                                    if(verticesOccurences[vertex] == 2) {
                                        // found a cycle
                                        circuit.addFirst(vertex);
                                    }
                                }
                                while(verticesOccurences[vertex = predecessors[vertex]] != 3);

                                int circuitLength = 0;
                                for(int i = 0; i < circuit.size(); ++i) {
                                    var v = circuit.get(i);

                                    if(i+1 < circuit.size()) {
                                        var next = circuit.get(i+1);

                                        for(var edge : graph.getOutgoingEdges(v)) {
                                            if(edge.to() == next) {
                                                circuitLength += edge.weight();
                                                break;
                                            }
                                        }
                                    }
                                }
                                return new BFYResult.NegativeCycle(circuit, circuitLength);
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
                        predecessors[successor] = current;
                        addToQueueIfNotAlreadyIn(successor);
                    }
                }
            }
        }
        return new BFYResult.ShortestPathTree(d, predecessors);
    }
}
