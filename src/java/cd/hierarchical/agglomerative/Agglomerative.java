package cd.hierarchical.agglomerative;

// Title:       Generic Agglomerative Algorithm
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import cd.CDAlgorithm;
import ikor.collection.List;
import ikor.math.DenseMatrix;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import noesis.AttributeNetwork;
import noesis.CollectionFactory;
import noesis.DynamicNetwork;
import noesis.algorithms.traversal.ConnectedComponents;
import noesis.analysis.NodeScore;
import noesis.analysis.structure.PathLength;

/**
 * Generic Agglomerative Algorithm
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("Agglomerative Algorithm")
@Description("Agglomerative Algorithm")
public abstract class Agglomerative extends CDAlgorithm {

    private DynamicNetwork dn;

    /**
     * Constructor.
     *
     * @param network Network to apply Community detecction
     */
    public Agglomerative(AttributeNetwork network) {
        super(network);
        dn = new DynamicNetwork();
        results = new DenseMatrix(an.nodes(), an.nodes(), -1);
    }

    @Override
    public void compute() {

        List<NodeScore> lengths = CollectionFactory.createList();
        // Calculates all path lengths for all nodes
        for (int node = 0; node < an.nodes(); ++node) {
            // Path length from node "node"
            PathLength task = new PathLength(an, node);
            // Compute and add
            lengths.add(task.call());
        }

// Distances
//        for (int i = 0; i < lengths.size(); ++i)
//        {
//            for (int j = 0; j < lengths.get(i).size(); ++j)
//                System.out.print(lengths.get(i).get(j)+" ");
//            System.out.println();
//        }
        //---------
        // Inicializate
        // Add only nodes
        dn = new DynamicNetwork();
        for (int node = 0; node < an.nodes(); ++node) {
            dn.add(an.get(node));
            // First assign
            results.set(0, node, node);
        }

        // Assign node i to cluster i
        ConnectedComponents cc = new ConnectedComponents(dn);
        cc.compute();

        // Iterate
        // While not exit
        boolean exit = false;
        int iter = 1;
        while (!exit && cc.components() > 1) {
            // Create list of all clusters nodes
            List<List<Integer>> clusters = CollectionFactory.createList();
            // Create Clusters
            for (int i = 0; i < cc.components(); ++i) {
                clusters.add(CollectionFactory.createList());
            }
            // Add each node to corresponding cluster
            for (int node = 0; node < an.nodes(); ++node) {
                clusters.get(cc.component(node) - 1).add(node);
                // System.out.println("Cluster ["+cc.component(node)+"] <- "+node);
            }

            // Distance between clusters
            int c1 = 0, c2 = 1;
            double d, dmin = Integer.MAX_VALUE;
            // For each couple of cluster, calculate minimum distance
            for (int i = 0; i < cc.components(); ++i) {
                for (int j = i + 1; j < cc.components(); ++j) {
                    // Distance of cluster i to cluster j
                    d = distance(clusters.get(i), clusters.get(j), lengths);

                    // System.out.println("Clusters ["+(i+1)+","+(j+1)+"] = "+d);
                    // If less, keep the position
                    if (d < dmin) {
                        c1 = i;
                        c2 = j;
                        dmin = d;
                    }
                }
            }

            // System.out.println("Distancia minima ["+(c1+1)+","+(c2+1)+"] = "+dmin);
            // Merge clusters with minimal distance
            List<Integer> C1 = clusters.get(c1);
            List<Integer> C2 = clusters.get(c2);
            // Exit if dont merge cluster-i and cluster-j
            exit = true;
            for (int i = 0; i < C1.size(); ++i) {
                for (int j = 0; j < C2.size(); ++j) {
                    // Link node-i to node-j exists
                    if (an.index(C1.get(i), C2.get(j)) != -1) {
                        // Add link
                        dn.add2(C1.get(i), C2.get(j));
                        exit = false;
                    }
                }
            }

            // Add results
            if (!exit) {
                // Compute connected components
                cc = new ConnectedComponents(dn);
                cc.compute();

                // Save results
                for (int i = 0; i < results.columns(); ++i) {
                    results.set(iter, i, cc.component(i));
                }
                iter++;
            }

        }

        // Clean results matrix (remove excess memory)
        DenseMatrix aux = new DenseMatrix(iter, an.nodes());
        for (int i = 0; i < aux.rows(); ++i) {
            for (int j = 0; j < aux.columns(); ++j) {
                aux.set(i, j, results.get(i, j));
            }
        }

        results = new DenseMatrix(aux);

    }

    /**
     * Compute the distance between two clusters.
     *
     * @param c1 List of nodes of cluster 1
     * @param c2 List of nodes of cluster 2
     * @param len Lengths Matrix of the net
     *
     * @return Distance between clusters
     */
    protected abstract double distance(List<Integer> c1, List<Integer> c2,
            List<NodeScore> len);

}
