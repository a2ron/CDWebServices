package cd.modular;

// Title:       Multi Step Greedy Algorithm (MSG)
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import noesis.AttributeNetwork;
import noesis.Link;

/**
 * Multi Step Greedy Algorithm (MSG)
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("MSG")
@Description("MSG")
public class MSG extends Modular {

    private QMatrix qm;

    @Override
    protected void preProcess() {
        
        //remove all links and save in an array 
        links = new ArrayList<>();
        for (int node = 0; node < dn.size(); node++) {
            int _links[] = dn.outLinks(node);
            if (_links != null) {
                for (int link = 0; link < _links.length; link++) {
                    links.add(new Link(node, _links[link], 0.0));
                    dn.remove(node, _links[link]);
                }
            }
        }

        //create QMatrix
        qm = new QMatrix(links.size());
    }

    //PriorityQueue to implement QMatrix
    private class QMatrix extends PriorityQueue<Link<Double>> {

        public QMatrix(int size) {
            super(size, (Link<Double> p1, Link<Double> p2) -> {
                Double a = p1.getContent();
                Double b = p2.getContent();
                int res = Double.compare(b, a);
                if (res == 0) {
                    res = Integer.compare(p1.getSource(), p2.getSource());
                    if (res == 0) {
                        res = Integer.compare(p1.getDestination(), p2.getDestination());
                    }
                }
                return res;
            });
        }
    }

    public MSG(AttributeNetwork network) {
        super(network);
    }

    /**
     * Calculate the actual QMatrix to apply the algorithm
     */
    private void recalculateQMatrix() {

        qm.clear();
        double modularity = this.computeModularity();

        /*for each remaining link*/
        for (int i = 0; i < links.size(); ++i) {
            int source = links.get(i).getSource();
            int destination = links.get(i).getDestination();

            dn.add(source, destination);
            //calculate increment of modularity
            double m = this.computeModularity() - modularity;
            if (m > 0) {
                qm.add(new Link(source, destination, m));
            }

            dn.remove(source, destination);

        }
    }

    /**
     * nodes that it's not possible to add links in each iteration
     */
    private ArrayList<Integer> nodesTouch = new ArrayList<>();

    /**
     * Add a link (if appropriate according to "nodesTouch") and save the "nodesTouch" to don't add links in the iteration
     * @param link 
     */
    private void addLinkThatImproves(Link link) {
        int s = link.getSource();
        int d = link.getDestination();
        if (!nodesTouch.contains(s) && !nodesTouch.contains(d)) {
            dn.add(s, d);
            links.remove(link);
            nodesTouch.add(s);
            nodesTouch.add(d);
        }
    }

    @Override
    protected Boolean improveModularity() {

        Boolean ok = false;

        this.recalculateQMatrix();
        nodesTouch.clear();

        Iterator it = qm.iterator();
        Boolean salir = false;
        //adding links that improve modularity while it's possible
        while (it.hasNext() && !salir) {
            Link<Double> l = (Link<Double>) it.next();
            if (l.getContent() > 0) { //improve modularity?
                ok = true;
                this.addLinkThatImproves(l);
            } else {
                salir = true;//no more improvements, exit
            }
        }

        return ok;

    }

}
