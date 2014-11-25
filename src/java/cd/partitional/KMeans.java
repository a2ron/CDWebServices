package cd.partitional;

// Title:       K-Means Algorithm
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import static cd.spectral.Spectral.KMeans;
import ikor.math.DenseMatrix;
import ikor.math.DenseVector;
import ikor.math.random.Random;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import noesis.AttributeNetwork;
import noesis.algorithms.visualization.FruchtermanReingoldLayout;
import static noesis.algorithms.visualization.NetworkLayout.MARGIN;

/**
 * K-Means Algorithm
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("KMeansAlgorithm")
@Description("KMeansAlgorithm")
public class KMeans extends Partitional {

    private int K;

    public KMeans(AttributeNetwork network) {
        super(network);
        K = 3;
    }

    public KMeans(AttributeNetwork network, int K) {
        super(network);
        this.K = K;
    }

    @Override
    public void compute() {

        // Init with FruchtermanReingold for distribute the nodes in the space
        for (int i = 0; i < an.nodes(); ++i) {
            double x = MARGIN + (1 - 2 * MARGIN) * Random.random();
            double y = MARGIN + (1 - 2 * MARGIN) * Random.random();
            an.setNodeAttribute("x", i, x + "");
            an.setNodeAttribute("y", i, y + "");
        }
        FruchtermanReingoldLayout frl = new FruchtermanReingoldLayout();
        frl.layout(an, an.getNodeAttribute("x"), an.getNodeAttribute("y"));

        // Create matrix with results of Fruchterman Reingold Layout
        DenseMatrix F = new DenseMatrix(an.nodes(), 2);
        for (int i = 0; i < F.rows(); ++i) {
            F.set(i, 0, (double) an.getNodeAttribute("x").get(i));
            F.set(i, 1, (double) an.getNodeAttribute("y").get(i));
        }

        // K-means algorithm 
        DenseVector CL = KMeans(F, K, 1e10);

        // Save results
        for (int i = 0; i < results.columns(); ++i) {
            results.set(0, i, CL.get(i));
        }
    }

}
