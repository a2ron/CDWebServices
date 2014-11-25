package cd.score;

// Title:       Link Clustering coefficient
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import noesis.Network;
import noesis.analysis.LinkScoreTask;

/**
 * Link Clustering coefficient
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("Link Clustering coefficient")
@Description("Link Clustering coefficient")
public class LinkClusteringCoefficient extends LinkScoreTask {

    public LinkClusteringCoefficient(Network network) {
        super(network);
    }

    @Override
    public double compute(int source, int destination) {

        Network net = getNetwork();

        if (net.get(source, destination) == null) {
            return 0;
        }

        //triangles where the link is there
        int triangles = 0;
        int links[] = net.outLinks(destination);
        if (links != null) {
            for (int i = 0; i < net.outDegree(destination); i++) {
                if (net.get(links[i], source) != null && source != links[i]) {
                    triangles++;
                }
            }
        }

        //max triangles where the link could be there
        int kj = net.outDegree(destination);
        int ki = net.inDegree(source);
        int denom = Math.max(ki, kj);
        if (denom == ki) {
            denom--;
        } else {
            if (net.get(destination, source) != null) {
                denom--;
            }
        }
        
        Double res = ((double) (triangles + 1)) / denom;

        if (res.isInfinite()) { //denominator could be 0
            res = new Double(0);
        }

        return res;

    }

}
