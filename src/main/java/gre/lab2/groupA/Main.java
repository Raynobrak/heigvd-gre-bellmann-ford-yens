package gre.lab2.groupA;

import gre.lab2.graph.BFYResult;
import gre.lab2.graph.WeightedDigraph;
import gre.lab2.graph.WeightedDigraphReader;

import java.io.IOException;
import java.util.Arrays;

public final class Main {
    public static void main(String[] args) throws IOException {
        // TODO
        //  - Renommage du package ;
        //  - Écrire le code dans le package de votre groupe et UNIQUEMENT celui-ci ;
        //  - Documentation soignée comprenant :
        //    - la javadoc, avec auteurs et description des implémentations ;
        //    - des commentaires sur les différentes parties de vos algorithmes.
        WeightedDigraph reseau1 = WeightedDigraphReader.fromFile("data/reseau1.txt");
        WeightedDigraph reseau2 = WeightedDigraphReader.fromFile("data/reseau2.txt");
        WeightedDigraph reseau3 = WeightedDigraphReader.fromFile("data/reseau3.txt");
        WeightedDigraph reseau4 = WeightedDigraphReader.fromFile("data/reseau4.txt");

        WeightedDigraph reseau_5_2 = WeightedDigraphReader.fromFile("data/reseau-ex-5_2.txt");
        WeightedDigraph reseau_5_3 = WeightedDigraphReader.fromFile("data/reseau-ex-5_3.txt");

        var algorithm = new BellmanFordYensAlgorithm();



        var result_5_2 = algorithm.compute(reseau_5_2, 0);
        var result_5_3 = algorithm.compute(reseau_5_3, 0);
        printBFYResult(result_5_2, "Réseau exo 5.2");
        printBFYResult(result_5_3, "Réseau exo 5.3");

        var result1 = algorithm.compute(reseau1, 0);
        var result2 = algorithm.compute(reseau2, 0);
        var result3 = algorithm.compute(reseau3, 0);
        var result4 = algorithm.compute(reseau4, 0);
        printBFYResult(result1, "Réseau 1");
        printBFYResult(result2, "Réseau 2");
        printBFYResult(result3, "Réseau 3");
        printBFYResult(result4, "Réseau 4");
    }

    static void printBFYResult(BFYResult result, String name) {
        String bar = "----------[ " + name + " ]----------";
        System.out.println(bar);

        if(result.isNegativeCycle()) {
            System.out.println("> Le graphe contient un circuit absorbant.");
            System.out.println("Circuit trouvé :");
            System.out.println("- poids du circuit : " + result.getNegativeCycle().length());
            System.out.println("- sommets du circuit : " + result.getNegativeCycle().vertices());
        }
        else {
            System.out.println("> Il existe une arborescence de plus courts chemins sans circuit absorbants.");

            var tree = result.getShortestPathTree();
            if(tree.distances().length < 25) {
                System.out.println("Résultat de l'algorithme :");
                System.out.println("- Tableau des distances : " + Arrays.toString(tree.distances()));
                System.out.println("- Tableau des prédécesseurs : " + Arrays.toString(tree.predecessors()));
            }
        }
        System.out.println(new String(new char[bar.length()]).replace('\0', '-'));
    }
}
