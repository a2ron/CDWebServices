package cd.score;

// Title:       Silhouette Coefficient, between -1 and 1
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
import noesis.analysis.structure.PathLength;

/**
 * Silhouette Coefficient, between -1 and 1
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("Silhouette")
@Description("Silhouette")
public class SilhouetteCoefficient extends ClusterScoreTask {

    private List<NodeScore> paths;

    /**
     *
     * @param network Network to compute the coefficient
     * @param clusters Clusters assignment
     */
    public SilhouetteCoefficient(Network network, DenseVector clusters) {
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
        double distin = Double.MAX_VALUE, distout = Double.MAX_VALUE;

        // Foreach cluster
        Iterator it = getClusters().keySet().iterator();
        while (it.hasNext()) {
            // Cluster
            int c = (int) it.next();
            double sum = 0.0, reacheables = 0.0;

            // List of nodes
            List<Integer> no = getClusters().get(c);

            // Sum distances [node, All other nodes]
            for (int i = 0; i < no.size(); ++i)
            {
                double l = paths.get(node).get(no.get(i));
                if (l > 0.0)
                {
                    reacheables++;
                    sum += l;
                }
            }
            
            // Average
            sum = reacheables > 0.0 ? sum / reacheables : 0.0;

            // If the clusters are differents
            if (c != cluster) {
                // Save external distance if it's lesser
                if (sum < distout) {
                    distout = sum;
                }
            } // if the cluster is same
            else {
                // Save internal distance
                distin = sum;
            }
        }
        
        // If only has 1 cluster
        distout = (distout == Double.MAX_VALUE) ? 0 : distout;
        
        return (distout - distin) / Math.max(distin, distout);
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
