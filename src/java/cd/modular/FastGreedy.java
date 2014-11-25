package cd.modular;

// Title:       Fast Greedy Algorithm
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com
import cd.partitional.KMeans;
import ikor.math.DenseVector;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import java.util.ArrayList;
import java.util.Random;
import noesis.AttributeNetwork;
import noesis.Link;

/**
 * Fast Greedy Algorithm
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("FastGreedy")
@Description("FastGreedy")
public class FastGreedy extends Modular {

    double actualModularity;

    public FastGreedy(AttributeNetwork network) {
        super(network);
    }

    /**
     * Looking for a link that modularity improves when this is added to the
     * network (null if not exists)
     *
     * @return
     */
    private Link<Double> getGoodLink() {

        Link link = null;
        Random r = new Random();
        ArrayList<Link> remainingLinks = new ArrayList<>(this.links);
        //while remaining links
        while (remainingLinks.size() > 0 && link == null) {
            //choose a random link
            int i = r.nextInt(remainingLinks.size());
            int source = remainingLinks.get(i).getSource();
            int destination = remainingLinks.get(i).getDestination();
            dn.add(source, destination);

            //if the link improves the modularity, save it
            double modu = this.computeModularity();
            if (modu > actualModularity) {
                /* save the link that improves to return to improveModularity*/
                link = new Link(source, destination, modu);
                this.links.set(i, link);//to remove it after (see improveModularity)
            }

            dn.remove(source, destination);
            remainingLinks.remove(i);
        }

        return link; //return the link that improves modularity
    }

    @Override
    protected Boolean improveModularity() {

        Boolean ok = false;
        if (dn.links() > 0) {

            Link<Double> link = getGoodLink();
            if (link != null) {
                ok = true;
                dn.add(link.getSource(), link.getDestination());
                actualModularity = link.getContent();
                links.remove(link);
            }

        }
        return ok;
    }

    @Override
    protected void preProcess() {

        //remove links and detect cliques
        int cliques = (int) (an.nodes() * 0.5);//TODO ¿cuantos cliques iniciales?
        // Apply kmeans algorithm
        KMeans k = new KMeans(an, cliques);
        k.compute();
        DenseVector cl = new DenseVector(k.getResults().getRow(0));

        //init the network and "links" according to the cliques detected
        ArrayList<Integer> dealed = new ArrayList<>();
        links = new ArrayList<>();
        for (int node = 0; node < dn.size(); node++) {
            int _links[] = dn.outLinks(node);
            if (_links != null) {
                for (int link = 0; link < _links.length; link++) {
                    if (dealed.contains(node) || cl.get(node) != cl.get(_links[link])) {
                        links.add(new Link(node, _links[link], 0.0));
                        dn.remove(node, _links[link]);
                    } else {
                        dealed.add(node);
                    }
                }
            }
        }
        //save the actual modularity
        actualModularity = this.computeModularity();
    }

}
