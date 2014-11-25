package cd.score;

// Title:       Coverage Coefficient, between 0 and 1
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import ikor.collection.List;
import ikor.math.DenseVector;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import noesis.Network;

/**
 * Coverage Coefficient, between 0 and 1
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("Coverage Coefficient")
@Description("Coverage Coefficient")
public class CoverageCoefficient extends ClusterScoreTask {


    /**
     *
     * @param network Network to compute the coefficient
     * @param clusters Clusters assignment
     */
    public CoverageCoefficient(Network network, DenseVector clusters) {
        super(network, clusters);

    }

    @Override
    public double compute(int node) {
        Network net = getNetwork();
        
        // Common cluster
        int cluster = (int) getAssignment().get(node);
        List<Integer> nodes = getClusters().get(cluster);
        // Undirected network has 2 links per link
        double m = net.links()/2, links = 0;
        int size = nodes.size();
        
        // Count links
        for (int i = 0; i < size; ++i)
            for (int j = i+1; j < size; ++j)
                if (net.contains(nodes.get(i), nodes.get(j)))
                    links++;
        
        return (links/m)/size;
    }

    @Override
    public double overallValue() {        
        return getResult().sum();
    }
    
    @Override
    public double clusterValue(int cluster) {
        // If exists
        if (getClusters().containsKey(cluster))
        {
            checkDone();
            // List nodes of cluster
            List<Integer> nodes = getClusters().get(cluster);
            DenseVector dv = new DenseVector(nodes.size());
            // Add value for each node
            for (int i = 0; i < nodes.size(); ++i)
                dv.set(i, nodeValue(nodes.get(i)));
            // Acumulate value
            return dv.sum();
        }
        else
            return Double.NaN;
    }
}
