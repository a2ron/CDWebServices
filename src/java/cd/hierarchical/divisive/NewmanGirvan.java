package cd.hierarchical.divisive;

// Title:       Newman & Girman's Algorithm
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import noesis.AttributeNetwork;
import noesis.analysis.structure.LinkBetweenness;
import noesis.analysis.LinkScore;

/**
 * Newman & Girman's Algorithm
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("NewmanGirman")
@Description("NewmanGirman")
public class NewmanGirvan extends Divisive {

    public NewmanGirvan(AttributeNetwork network) {
        super(network);
    }

    @Override
    protected void removeBestLink() {

        //determinate the best link to remove
        LinkBetweenness task = new LinkBetweenness(dn);
        LinkScore linkScore = task.call();
        int source = -1;
        int destination = -1;
        double bestScore = Double.MIN_VALUE;
        for (int node = 0; node < dn.size(); node++) {
            for (int link = 0; link < dn.outDegree(node); link++) {
                double m = linkScore.get(node, dn.outLink(node, link));
                if (m > bestScore) {
                    bestScore = m;
                    source = node;
                    destination = dn.outLink(node, link);
                }
            }
        }
        //remove the link (both directions)
        dn.remove(source, destination);
        if (dn.get(destination, source) != null) {
            dn.remove(destination, source);
        }
    }
}
