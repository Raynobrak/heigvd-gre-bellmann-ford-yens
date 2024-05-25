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

    /**
     * Queue containing the vertices that have yet to be processed
     */
    private ArrayDeque<Integer> queue;

    /**
     * Array of booleans used to check if a vertex is in the queue or not
     */
    private boolean[] isInQueue;

    /**
     * Add a vertex to the queue and updates "isInQueue" array
     * @param vertex vertex to add
     */
    void addToQueue(Integer vertex) {
        queue.addLast(vertex);
        isInQueue[vertex] = true;
    }

    /**
     * Add a vertex to the queue if it's not already in said queue
     * @param vertex vertex to add
     */
    void addToQueueIfNotAlreadyIn(Integer vertex) {
        if(!isInQueue[vertex])
            addToQueue(vertex);
    }

    /**
     * Removes and returns the next vertex from the FIFO queue and updates the "isInQueue" array
     * @return the removed vertex
     */
    Integer removeNextFromQueue() {
        var item = queue.removeFirst();
        isInQueue[item] = false;
        return item;
    }

    /**
     * Runs the algorithm on a given graph, from a given source
     * @param graph the graph that we want to perform the algorithm on
     * @param from the vertex from where the algorithm needs to start (source)
     * @return the result of the algorithm as an instance of the {@link BFYResult} class
     */
    @Override
    public BFYResult compute(WeightedDigraph graph, int from) {
        final int verticesCount = graph.getNVertices(); // Number of vertices

        int[] predecessors = new int[verticesCount]; // Array of best predecessors, all unreachable by default
        Arrays.fill(predecessors, BFYResult.UNREACHABLE);

        int[] distancesFromSource = new int[verticesCount]; // Array of distances from the source, all equal to infinity by default
        Arrays.fill(distancesFromSource, INFINITY);

        distancesFromSource[from] = 0; // source is at a distance zero from the source

        int iterationNumber = 0; // Number of iterations

        // Queue containing the vertices to process
        queue = new ArrayDeque<Integer>();
        isInQueue = new boolean[verticesCount + 1]; // +1 is to be able to track the sentinel (N) too

        addToQueue(from);
        addToQueue(verticesCount);

        while(!queue.isEmpty()) {
            int current = removeNextFromQueue();
            if(current == verticesCount) {
                if(!queue.isEmpty()) {
                    iterationNumber++;
                    if(iterationNumber == verticesCount) {
                        // we're looking for a negative cost cycle
                        // searching for a vertex with a negative distance from source s

                        for(int vertex = 0; vertex < verticesCount; ++vertex) {
                            if(distancesFromSource[vertex] < 0) {
                                var circuit = new ArrayList<Integer>();
                                int circuitWeight = 0;

                                // reverse-exploring the circuit to find all vertices
                                var verticesOccurrences = new int[verticesCount];
                                do {
                                    if(++verticesOccurrences[vertex] == 2) { // found a circuit
                                        circuit.addFirst(vertex);

                                        // update circuit weight
                                        for(var edge : graph.getOutgoingEdges(predecessors[vertex])) {
                                            if (edge.to() == vertex) {
                                                circuitWeight += edge.weight();
                                                break;
                                            }
                                        }
                                    }
                                } while(verticesOccurrences[vertex = predecessors[vertex]] < 3);

                                return new BFYResult.NegativeCycle(circuit, circuitWeight);
                            }
                        }
                        throw new RuntimeException("a negative cost circuit should have been found");
                    }
                    else {
                        addToQueue(verticesCount); // Add the sentinel back to the queue
                    }
                }
            }
            else {
                for(var outgoingEdge : graph.getOutgoingEdges(current)) {
                    int successor = outgoingEdge.to();
                    // if λj > λi + cij --> update distance and predecessor of the 'successor' vertex
                    if(distancesFromSource[successor] > distancesFromSource[current] + outgoingEdge.weight()){
                        distancesFromSource[successor] = distancesFromSource[current] + outgoingEdge.weight();
                        predecessors[successor] = current;
                        addToQueueIfNotAlreadyIn(successor);
                    }
                }
            }
        }
        return new BFYResult.ShortestPathTree(distancesFromSource, predecessors);
    }
}
