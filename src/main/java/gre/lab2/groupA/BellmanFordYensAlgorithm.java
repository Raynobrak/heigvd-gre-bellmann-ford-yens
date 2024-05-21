package gre.lab2.groupA;

import gre.lab2.graph.BFYResult;
import gre.lab2.graph.IBellmanFordYensAlgorithm;
import gre.lab2.graph.WeightedDigraph;

import java.util.*;

public final class BellmanFordYensAlgorithm implements IBellmanFordYensAlgorithm {
    private static final int INFINITY = Integer.MAX_VALUE;

    private LinkedList<Integer> queue;
    private HashSet<Integer> inQueue;

    void addToQueue(Integer vertex) {
        queue.addLast(vertex);
        inQueue.add(vertex);
    }

    void addToQueueIfNotAlreadyIn(Integer vertex) {
        if(!inQueue.contains(vertex))
            addToQueue(vertex);
    }

    Integer nextFromQueue() {
        var item = queue.removeFirst();
        inQueue.remove(item);
        return item;
    }

    @Override
    public BFYResult compute(WeightedDigraph graph, int from) {
        final int N = graph.getNVertices();

        int[] pred = new int[N];
        Arrays.fill(pred, BFYResult.UNREACHABLE);

        int[] d = new int[N];
        Arrays.fill(d, INFINITY);

        d[from] = 0;

        int k = 0;

        final int sentinel = N;

        // File FIFO
        queue = new LinkedList<Integer>();
        inQueue = new HashSet<Integer>();

        addToQueue(from);
        addToQueue(sentinel);

        while(!queue.isEmpty()) {
            int current = nextFromQueue();
            if(current == sentinel) {
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
                                do
                                    circuit.add(vertex);
                                while((vertex = pred[vertex]) != circuit_begin);

                                var circuitInCorrectOrder = circuit.reversed();
                                return new BFYResult.NegativeCycle(circuitInCorrectOrder, circuit.size());
                            }
                        }
                        throw new RuntimeException("absorbing circuit should have been found");
                    }
                    else {
                        addToQueue(sentinel);
                    }
                }
            }
            else {
                var outgoingEdges = graph.getOutgoingEdges(current);
                // todo : continuer algo
                for(var outgoingEdge : outgoingEdges) {
                    int successor = outgoingEdge.to();
                    // λj > λi + cij
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
