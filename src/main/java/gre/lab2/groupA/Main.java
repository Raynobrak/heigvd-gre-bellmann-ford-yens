package gre.lab2.groupA;

import gre.lab2.graph.WeightedDigraph;
import gre.lab2.graph.WeightedDigraphReader;

import java.io.IOException;

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
        var result = algorithm.compute(reseau_5_3, 0);
    }
}
