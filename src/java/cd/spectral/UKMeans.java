package cd.spectral;

// Title:       Spectral + Kmeans algorithm
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
 * Spectral + kmeans algorithm
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("UKMeans")
@Description("UKMeans")
public class UKMeans extends Spectral {

    // K Clusters
    private int K;

    /**
     * @param network
     * @param clusters
     */
    public UKMeans(AttributeNetwork network, int clusters) {
        super(network);
        K = clusters > 0 ? clusters : 1;
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
        // Create matrix with k first Eigenvectors of Laplacian matrix --- 
        DenseMatrix E = EigenvectorsMatrix(L, K);

        // K-means algorithm 
        DenseVector CL = KMeans(E, K, 1e10);

        // Save results
        for (int i = 0; i < results.columns(); ++i) {
            results.set(0, i, CL.get(i));
        }
    }

}
