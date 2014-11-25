package cd.spectral;

// Title:       Bi-Partitioning Spectral Algorithm
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import ikor.math.DenseMatrix;
import ikor.math.DenseVector;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import noesis.AttributeNetwork;

/**
 * Bi-Partitioning Spectral Algorithm
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("EIG1")
@Description("EIG1")
public class EIG1 extends Spectral {

    public enum ThresholdType {
        // 0.0
        ZERO, 
        // Fiedler Eigenvector average
        AVG, 
        // Fiedler Eigenvector median
        MEDIAN,
        // Max distance of elements in Fiedler Eigenvector
        GAP
    };
    ThresholdType threshold;

    /**
     * @param network
     */
    public EIG1(AttributeNetwork network) {
        super(network);
        threshold = ThresholdType.ZERO;
    }

    /**
     * @param network
     * @param th Type of threshold
     */
    public EIG1(AttributeNetwork network, ThresholdType th) {
        super(network);
        threshold = th;
    }

    /*
     * (sin Javadoc)
     * 
     * @see cd.CDAlgorithm#compute()
     */
    @Override
    public void compute() {

        // Adjacency matrix
        DenseMatrix W = AdjacencyMatrix(an);
        // Diagonal degree matrix (Dii -> degree of node i)
        DenseMatrix D = DegreeMatrix(an);
        // Laplacian matrix no normalized
        DenseMatrix L = LaplacianMatrix(W, D, Normalized.NO);
        // Get Fiedler eigenvector
        DenseVector E = FiedlerEigenvector(L);
        // Partitioning of the net using 2nd eigenvector (Fiedler vector) with threshold th
        int c1 = 0, c2 = 1;
        // Compute real threshold value
        double th = Threshold(E, threshold);
        DenseVector CL = Partitioning(E, c1, c2, th);

        // Save results
        for (int i = 0; i < results.columns(); ++i) {
            results.set(0, i, CL.get(i));
        }
    }

    /**
     * Use the second smallest eigenvector of eigenvectors matrix E, also called
     * Fiedler vector, to partition of the graph by finding the optimal
     * splitting point.
     *
     * @pre Columns(E) >= 2
     * @param E Eigenvectors Matrix
     * @param cluster1 Value of cluster 1
     * @param cluster2 Value of cluster 2
     * @param th Threshold, If value is less than threshold, asign value ->
     * cluster1, else asign -> cluster2
     * @return Vector where position i correspond to cluster of element i
     */
    private static DenseVector Partitioning(DenseVector E, int cluster1, int cluster2, double th) {
        int size = E.size();
        DenseVector CL = new DenseVector(size);
        // If value is less than threshold, asign cluster 1, else asign cluster 2
        for (int i = 0; i < size; ++i) {
            if (E.get(i) < th) {
                CL.set(i, cluster1);
            } else {
                CL.set(i, cluster2);
            }
        }

        return CL;
    }

    /**
     * Compute threshold to use in partitioning.
     *
     * @param E Egenvector
     * @param th Type of threshold
     * @return Threshold value
     */
    private double Threshold(DenseVector E, ThresholdType th) {
        double value = 0.0;

        // Type
        switch (th) {
            // Average value
            case AVG:
                value = E.average();
                break;
            // Median value
            case MEDIAN:
                DenseVector M = new DenseVector(E);
                QuickSort(M);
                value = M.get(Math.round(M.size() / 2));
                break;
            // Max gap value
            case GAP:
                DenseVector G = new DenseVector(E);
                QuickSort(G);
                // Find max gap
                int pos = -1;
                for (int i = 0; i < G.size() - 1; ++i) {
                    if (Math.abs(G.get(i) - G.get(i + 1)) > value) {
                        value = Math.abs(G.get(i) - G.get(i + 1));
                        pos = i + 1;
                    }
                }
                if (pos != -1) {
                    value = G.get(pos);
                }
                break;
            default:
                value = 0.0;
        }

        return value;
    }
}
