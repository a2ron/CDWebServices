package cd.modular;

// Title:       Generic Modular Algorithm
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import cd.CDAlgorithm;
import cd.score.ModularityCoefficient;
import ikor.math.DenseVector;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import java.util.ArrayList;
import noesis.AttributeNetwork;
import noesis.DynamicNetwork;
import noesis.Link;
import noesis.algorithms.traversal.ConnectedComponents;

/**
 * Generic Modular Algorithm
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("Modular")
@Description("Modular")
public abstract class Modular extends CDAlgorithm {

    /**
     * The remaining links to add in each time
     */
    protected ArrayList<Link<Double>> links;
    protected DynamicNetwork dn;

    public Modular(AttributeNetwork network) {
        super(network);
        dn = new DynamicNetwork(network);
    }

    /**
     * Calculate the modularity of the network
     * @return the modularity value
     */
    protected double computeModularity() {

        ConnectedComponents cc = new ConnectedComponents(dn);
        cc.compute();

        // Get assignment
        DenseVector dv = new DenseVector(an.nodes());
        for (int i = 0; i < results.columns(); ++i) {
            dv.set(i, cc.component(i));
        }

        ModularityCoefficient md = new ModularityCoefficient(an, dv);

        return md.overallValue();
    }

    /**
     * Apply algorithm and save results.
     *
     */
    @Override
    public void compute() {

        preProcess();
                
        while (this.improveModularity()) {

        }

        // Compute connected components
        ConnectedComponents cc = new ConnectedComponents(dn);
        cc.compute();

        // Save results
        for (int i = 0; i < results.columns(); ++i) {
            results.set(0, i, cc.component(i));
        }
    }

    /**
     * @brief Alter links for improve Modularity
     * @return true if improve
     */
    protected abstract Boolean improveModularity();
    
    
    /**
     * Preprocces the net to apply the algorithm
     */
    protected abstract void preProcess();
}
