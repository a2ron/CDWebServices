package cd.score;

// Title:       Separation Coefficient, between 0 and inf
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import ikor.collection.List;
import ikor.math.DenseVector;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import java.util.Iterator;
import noesis.CollectionFactory;
import noesis.Network;
import noesis.analysis.NodeScore;
import noesis.analysis.structure.*;

/**
 * Separation Coefficient, between 0 and inf
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("Separation")
@Description("Separation")
public class SeparationCoefficient extends ClusterScoreTask {

    private List<NodeScore> paths;

    /**
     *
     * @param network Network to compute the coefficient
     * @param clusters Clusters assignment
     */
    public SeparationCoefficient(Network network, DenseVector clusters) {
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
        
        int cluster = (int) getAssignment().get(node);
        double sum = 0.0;

        // Sum all distances (node to all outsider nodes)
        Iterator it = getClusters().keySet().iterator();        
        while (it.hasNext()) {
            // Cluster
            int c = (int) it.next();
            if (c != cluster) {
                // Outsider nodes
                List<Integer> no = getClusters().get(c);
                // Sum distances
                for (int i = 0; i < no.size(); ++i) {
                    // Length [node,i]
                    sum += paths.get(node).get(no.get(i));;
                }
            }
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
