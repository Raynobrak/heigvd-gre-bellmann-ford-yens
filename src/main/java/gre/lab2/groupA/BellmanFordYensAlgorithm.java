package gre.lab2.groupA;

import gre.lab2.graph.BFYResult;
import gre.lab2.graph.IBellmanFordYensAlgorithm;
import gre.lab2.graph.WeightedDigraph;

import java.util.ArrayDeque;
import java.util.Arrays;

public final class BellmanFordYensAlgorithm implements IBellmanFordYensAlgorithm {
    private static final int INFINITY = Integer.MAX_VALUE;
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
        var Q = new ArrayDeque<Integer>();

        Q.push(from);
        Q.push(sentinel);

        while(!Q.isEmpty()) {
            int current = Q.removeFirst();
            if(current == sentinel) {
                if(!Q.isEmpty()) {
                    k++;
                    if(k == N) {
                        // todo : circuit absorbant trouvé
                        //return new BFYResult.NegativeCycle();
                        System.out.println("R contient un circuit absorbant accessible depuis s");
                    }
                    else {
                        Q.push(sentinel);
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
                        // TODO ne pas utiliser contains
                        if(!Q.contains(successor)) {
                            Q.push(successor);
                        }
                    }
                }
            }
        }
        return new BFYResult.ShortestPathTree(d, pred);
    }
}
