package gre.lab2.groupA;

import gre.lab2.graph.BFYResult;
import gre.lab2.graph.IBellmanFordYensAlgorithm;
import gre.lab2.graph.WeightedDigraph;

import java.util.*;

public final class BellmanFordYensAlgorithm implements IBellmanFordYensAlgorithm {
    private static final int INFINITY = Integer.MAX_VALUE;

    private LinkedList<Integer> queue;
    private boolean[] inQueue;

    void addToQueue(Integer vertex) {
        queue.addLast(vertex);
        inQueue[vertex] = true;
    }

    void addToQueueIfNotAlreadyIn(Integer vertex) {
        if(!inQueue[vertex])
            addToQueue(vertex);
    }

    Integer nextFromQueue() {
        var item = queue.removeFirst();
        inQueue[item] = false;
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
        inQueue = new boolean[N + 1]; // n + 1 for the sentinel

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
