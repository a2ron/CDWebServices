package cd.hierarchical.agglomerative;

// Title:       Simple Link Algorithm (SLINK)
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import ikor.collection.List;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import noesis.AttributeNetwork;
import noesis.analysis.NodeScore;

/**
 * Simple Link Algorithm (SLINK)
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("SLINKAlgorithm")
@Description("SLINKAlgorithm")
public class SLink extends Agglomerative {

    public SLink(AttributeNetwork network) {
        super(network);
    }

    @Override
    protected double distance(List<Integer> c1, List<Integer> c2,
            List<NodeScore> len) {
        double d, dmin = Integer.MAX_VALUE;
        int node1, node2;
        // Calculate minimum distance from cluster 1 to cluster 2
        for (int i = 0; i < c1.size(); ++i) {
            for (int j = 0; j < c2.size(); ++j) {
                // Length -> Node i to Node j (if 0.0, no path)
                node1 = c1.get(i);
                node2 = c2.get(j);
                d = len.get(node1).get(node2);
                d = (d <= 0.0) ? Integer.MAX_VALUE : d;
                // If less, keep it
                if (d < dmin) {
                    dmin = d;
                }
            }
        }

        return dmin;
    }

}
