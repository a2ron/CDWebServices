package cd.score;

// Title:       Task for computing node scores using clusters assignment
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import ikor.collection.List;
import ikor.math.DenseVector;
import ikor.model.data.DataModel;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import java.util.HashMap;
import noesis.CollectionFactory;
import noesis.Network;
import noesis.analysis.NodeScore;
import noesis.analysis.NodeScoreTask;

/**
 * Task for computing node scores using clusters assignment.
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("ClusterScoreTask")
@Description("ClustersScoreTask")
public abstract class ClusterScoreTask extends NodeScoreTask {

    private HashMap<Integer, List<Integer>> clusters;
    private DenseVector assignment;

    /**
     *
     * @param model
     * @param network
     * @param assignment
     */
    public ClusterScoreTask(DataModel model, Network network, DenseVector assignment) {
        super(model,network);
        this.assignment = assignment;
        clusters = new HashMap<>();
        
        // Create a list of nodes for each cluster
        for (int i = 0; i < assignment.size(); ++i) {
            // Cluster
            int c = (int) assignment.get(i);
            // First time
            if (!clusters.containsKey(c)) {
                clusters.put(c,CollectionFactory.createList());
            }
            // Add node to cluster c
            clusters.get(c).add(i);
        }
        
    }

    /**
     *
     * @param network
     * @param assignment
     */
    public ClusterScoreTask(Network network, DenseVector assignment) {
        this(NodeScore.REAL_MODEL, network, assignment);
    }

    
    /**
     *
     * @return
     */
    public final HashMap<Integer, List<Integer>> getClusters() {
        return clusters;
    }

    /**
     *
     * @return
     */
    public DenseVector getAssignment() {
        return assignment;
    }
    
    /**
     *
     * @return
     */
    public int clusters() {
        return getClusters().keySet().size();
    }
    
    /**
     *
     * @param node Node ID
     * @return Value ​​of node
     */
    public double nodeValue(int node)
    {
        return getResult(node);
    }
    
    /**
     *
     * @param cluster Cluster ID
     * @return Combine the values ​​of each node of cluster 
     */
    public abstract double clusterValue(int cluster);
    
    /**
     *
     * @return Combine the values ​​of each node to calculate the overall result
     */
    public abstract double overallValue();
}
