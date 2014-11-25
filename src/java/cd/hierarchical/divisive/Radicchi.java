package cd.hierarchical.divisive;

// Title:       Radicchi Algorithm
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import cd.score.LinkClusteringCoefficient;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import java.util.ArrayList;
import java.util.PriorityQueue;
import noesis.AttributeNetwork;
import noesis.Link;
import noesis.analysis.LinkScore;
import noesis.analysis.LinkScoreTask;

/**
 * Radicchi Algorithm
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("Radicchi")
@Description("Radicchi")
public class Radicchi extends Divisive {

    private Queue queue = null;

    //PriorityQueue to implement Queue
    private class Queue extends PriorityQueue<Link<Double>> {

        public Queue(int size) {
            super(size, (Link<Double> l1, Link<Double> l2) -> {
                return Double.compare(l1.getContent(), l2.getContent());
            });
        }
    }

    public Radicchi(AttributeNetwork network) {
        super(network);

        //create a queue for recalculate measures faster
        queue = new Queue(dn.links());

        //init the queue
        LinkScoreTask task = new LinkClusteringCoefficient(dn);
        LinkScore measure = task.call();
        for (int node = 0; node < dn.size(); node++) {
            for (int link = 0; link < dn.outDegree(node); link++) {
                double m = measure.get(node, dn.outLink(node, link));
                queue.add(new Link<>(node, dn.outLink(node, link), m));
            }
        }
    }

    private void recalculate(Link<Double> linkRemoved) {

        //foreach link in the queue, update (if necessary) 
        LinkScoreTask task = new LinkClusteringCoefficient(dn);
        ArrayList<Link<Double>> delete = new ArrayList();
        ArrayList<Link<Double>> add = new ArrayList();

        queue.stream().filter((l) -> (l.getDestination() == linkRemoved.getSource() || l.getSource() == linkRemoved.getDestination())).map((l) -> {
            delete.add(l);
            return l;
        }).filter((l) -> (l.getDestination() != linkRemoved.getSource() || l.getSource() != linkRemoved.getDestination())).forEach((l) -> {
            Double newScore = task.compute(l.getSource(), l.getDestination());
            add.add(new Link(l.getSource(), l.getDestination(), newScore));
        });

        queue.removeAll(delete);
        queue.addAll(add);
    }

    @Override
    protected void removeBestLink() {

        //determinate the best link to remove
        Link<Double> l = queue.poll();

        //remove the link (both directions)
        dn.remove(l.getSource(), l.getDestination());
        if (dn.get(l.getDestination(), l.getSource()) != null) {
            dn.remove(l.getDestination(), l.getSource());
        }

        //recalculate LinkClusteringCoefficient
        this.recalculate(l);

    }
}
