package cd.score;

// Title:       Cohesion Coefficient, between 0 and inf
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import ikor.collection.List;
import ikor.math.DenseVector;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import noesis.CollectionFactory;
import noesis.Network;
import noesis.analysis.NodeScore;
import noesis.analysis.structure.*;

/**
 * Cohesion Coefficient, between 0 and inf
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("Cohesion Coefficient")
@Description("Cohesion Coefficient")
public class CohesionCoefficient extends ClusterScoreTask {

    private List<NodeScore> paths;

    /**
     *
     * @param network Network to compute the coefficient
     * @param clusters Clusters assignment
     */
    public CohesionCoefficient(Network network, DenseVector clusters) {
        super(network, clusters);

        // Compute all paths
        paths = CollectionFactory.createList();
        // Calculates all path lengths for all nodes
        for (int node = 0; node < network.nodes(); ++node) {
            // Path length from node "node"
            PathLength task = new PathLength(network, node);
            // Compute and add
            paths.add(task.call());
        }
    }

    @Override
    public double compute(int node) {
        // Common cluster
        int cluster = (int) getAssignment().get(node);
        List<Integer> nodes = getClusters().get(cluster);
        int size = nodes.size();
        double sum = 0.0;

        // Sum all distances (node -> intra-cluster nodes)
        for (int i = 0; i < size; ++i) {
            // Length [node,i]
            sum += paths.get(node).get(nodes.get(i));
        }
        
        return sum;
    }

    @Override
    public double overallValue() {
        return getResult().average();
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
            // Average value
            return dv.average();
        }
        else
            return Double.NaN;
    }
}
