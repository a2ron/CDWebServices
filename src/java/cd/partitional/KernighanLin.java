package cd.partitional;

// Title:       Bi-Partitioning Kernighan-Lin Algorithm
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import cd.CDAlgorithm;
import static cd.spectral.Spectral.AdjacencyMatrix;
import ikor.collection.List;
import ikor.collection.util.Pair;
import ikor.math.DenseMatrix;
import ikor.math.DenseVector;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import noesis.AttributeNetwork;
import noesis.CollectionFactory;

/**
 * Bi-Partitioning Kernighan-Lin Algorithm
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("KernighanLin")
@Description("KernighanLin")
public class KernighanLin extends CDAlgorithm {

    /**
     * Constructor.
     *
     * @param network Network to apply Community detecction
     */
    public KernighanLin(AttributeNetwork network) {
        super(network);
    }

    @Override
    public void compute() {

        // --- Initialization ---
        // 2 Subgraphs with n/2 nodes
        List<Integer> A = CollectionFactory.createList();
        List<Integer> B = CollectionFactory.createList();
        // Backup subgraphs
        List<Integer> AA = CollectionFactory.createList();
        List<Integer> BB = CollectionFactory.createList();
        // Create Subgraphs
        for (int node = 0; node < an.nodes(); ++node) {
            if (node % 2 == 0) {
                A.add(node);
                AA.add(node);
            } else {
                B.add(node);
                BB.add(node);
            }
        }

        // Cost reduction for moving node i
        DenseVector D = new DenseVector(an.nodes());
        // Node i is locked
        List<Boolean> L = CollectionFactory.createList();
        // Reset other parameter
        for (int node = 0; node < an.nodes(); ++node) {
            D.set(node,0.0);
            L.add(false);
        }

        // Costs matrix (adjacency matrix)
        DenseMatrix C = AdjacencyMatrix(an);

        // Max Gains list
        List<Pair<Pair<Integer, Integer>, Double>> G = CollectionFactory
                .createList();
        double gmax;

        // --- Algorithm ---
        do {

            // --- Calculates D for all nodes ---
            // A nodes
            ComputeD(A, B, L, C, D);
            // B nodes
            ComputeD(B, A, L, C, D);

            // --- Iterate ---
            int locks = 0;
            do {

// for (int i = 0; i< D.size(); ++i)
// System.out.println("Node "+i+": "+D.get(i));
                // --- Find maximun gain ---
                int namg = -1, nbmg = -1;
                double mg = Double.NEGATIVE_INFINITY;
                // For all A nodes
                for (int i = 0; i < A.size(); ++i) {
                    // A node
                    int na = A.get(i);
                    // If no lock
                    if (!L.get(na)) {
                        // For all B nodes
                        for (int j = 0; j < B.size(); ++j) {
                            // B node
                            int nb = B.get(j);
                            // If no lock
                            if (!L.get(nb)) {
                                // Gain exchange na and nb
                                double g = D.get(na) + D.get(nb) - 2
                                        * C.get(na,nb);
                                if (g > mg) {
                                    mg = g;
                                    namg = i;
                                    nbmg = j;
//System.out.println("Gain [" + na + "," + nb + "] = " + g);
                                }
                            }
                        }
                    }
                }

                // --- Exchange nodes with maximun gain ---
                int temp = A.get(namg);
                A.set(namg, B.get(nbmg));
                B.set(nbmg, temp);
                // Lock nodes
                L.set(A.get(namg), true);
                L.set(B.get(nbmg), true);
                locks += 2;
                // Save gain
                Pair<Integer, Integer> p = new Pair(B.get(nbmg), A.get(namg));
                G.add(new Pair(p, mg));

//System.out.println("Lock [" + B.get(nbmg) + "," + A.get(namg) + "], Gain = " + mg);
                // --- Update D values ---
                // A nodes
                UpdateD(A, B.get(nbmg), A.get(namg), L, C, D);
                // B nodes
                UpdateD(B, A.get(namg), B.get(nbmg), L, C, D);

            } while (locks < an.nodes() - 1);

            // --- Select k pair with gain g1+...+gk is maximized ---
            int k = 0;
            double g = 0;
            gmax = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < G.size(); ++i) {
                g += G.get(i).second();
                if (g > gmax) {
                    k = i + 1;
                    gmax = g;
//System.out.println(k + " pairs [" + G.get(i).first().first() + "," + G.get(i).first().second()+ "], Gain = " + g);
                }
            }

            // --- If gain is positive swap k nodes  ---
            if (gmax > 0) {

                // Swap k first pairs of nodes
                Pair<Integer, Integer> p;
                for (int i = 0; i < k; ++i) {
                    // Get pair
                    p = G.get(i).first();
                    int pos1 = AA.index(p.first());
                    int pos2 = BB.index(p.second());
                    // Swap
                    int temp = AA.get(pos1);
                    AA.set(pos1, BB.get(pos2));
                    BB.set(pos2, temp);
                }

                // Restore backup
                A.clear();
                for (int i = 0; i < AA.size(); ++i) {
                    A.add(AA.get(i));
                }
                B.clear();
                for (int i = 0; i < BB.size(); ++i) {
                    B.add(BB.get(i));
                }

            }

            // Unlock nodes and reset D and G
            for (int i = 0; i < L.size(); ++i) {
                L.set(i, false);
                D.set(i, 0.0);
            }
            G.clear();

        } while (gmax > 0);

        // A nodes
        for (int i = 0; i < AA.size(); ++i) {
            // Node orig
            int no = AA.get(i);
            results.set(0, no, 1);
        }
        // B nodes
        for (int i = 0; i < BB.size(); ++i) {
            // Node orig
            int no = BB.get(i);
            results.set(0, no, 2);
        }

    }

    /**
     * Computing cost reduction (minimal cut) for moving each node of IG to EG 
     * if isn't lock
     * 
     * @pre IG union EG = V, IG intersec EG = 0
     * @param IG Group of nodes 
     * @param EG Group of nodes
     * @param L L[i] = true if node i is lock
     * @param C cost associate to the links
     * @param D Cost to compute
     *
     */
    private void ComputeD(List<Integer> IG, List<Integer> EG, List<Boolean> L, DenseMatrix C, DenseVector D) {
        for (int i = 0; i < IG.size(); ++i) {
            double ic = 0.0, ec = 0.0;
            // Node orig
            int no = IG.get(i);

            // If no lock
            if (!L.get(no)) {
                // Internal cost
                for (int j = 0; j < IG.size(); ++j) {
                    ic += C.get(no,IG.get(j));
                }
                // External cost
                for (int j = 0; j < EG.size(); ++j) {
                    ec += C.get(no,EG.get(j));
                }
                // Cost reduction for moving node 'no'
                D.set(no, ec - ic);
            }
        }
    }


    /**
     * Updating cost reduction (minimal cut) for removing node 'in' from G and
     *  add node 'ex' to G if isn't lock
     * 
     * @param G Group of nodes 
     * @param in node of G to move to other group
     * @param en node of other group to move to G
     * @param L L[i] = true if node i is lock
     * @param C cost associate to the links
     * @param D Cost to update
     *
     */
    private void UpdateD(List<Integer> G, int in, int en, List<Boolean> L, DenseMatrix C, DenseVector D) {
        for (int i = 0; i < G.size(); ++i) {
            int node = G.get(i);
            // If no lock
            if (!L.get(node)) {
                double d = D.get(node);
                // Update Di value
                d += 2 * C.get(node,in) - 2 * C.get(node,en);
                // Cost reduction for moving node 'node'
                D.set(node, d);
            }
        }
    }

}
