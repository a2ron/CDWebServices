package cd.hierarchical.divisive;

// Title:       Generic Divisive Algorithm
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import cd.CDAlgorithm;
import ikor.math.DenseMatrix;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import noesis.AttributeNetwork;
import noesis.DynamicNetwork;
import noesis.algorithms.traversal.ConnectedComponents;

/**
 * Generic Divisive Algorithm
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 */
@Label("Divisive")
@Description("Divisive")
public abstract class Divisive extends CDAlgorithm {

    protected DynamicNetwork dn;

    /**
     * Constructor.
     *
     * @param network Network to apply Community detecction
     */
    public Divisive(AttributeNetwork network) {
        super(network);
        dn = new DynamicNetwork(network);
        results = new DenseMatrix(an.nodes(), an.nodes(), -1);
    }

    /**
     * Apply algorithm and save results.
     *
     */
    @Override
    public void compute() {

        //initialization
        results = new DenseMatrix(an.nodes(), an.nodes(), 1);
        ConnectedComponents cc = new ConnectedComponents(dn);
        cc.compute();
        int n = cc.components();

        int iter = 0;
        while (dn.links() > 0) {
            this.removeBestLink();

            // Compute connected components
            cc = new ConnectedComponents(dn);
            cc.compute();

            // Save results if chage the number of clusters
            if (n != cc.components()) {
                // Save results
                for (int i = 0; i < results.columns(); ++i) {
                    results.set(iter, i, cc.component(i));
                }
                iter++;
                n = cc.components();
            }

        }
    }

    /**
     * @brief Determinate and remove the best candidate link
     * @pre The network must have one links at least
     */
    protected abstract void removeBestLink();
}
