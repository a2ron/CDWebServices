package cd;

// Title:       Cliques Network
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import noesis.Attribute;
import noesis.AttributeNetwork;

/**
 * CliquesNetwork
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("CliquesNetwork")
@Description("CliquesNetwork")
public class CliquesNetwork extends AttributeNetwork {

    public CliquesNetwork(int numCliques, int nodesPerClique) {
        super();
        // Node Attribute ID
        addNodeAttribute(new Attribute("id", new ikor.model.data.IntegerModel()));
        //add nodes
        for (int i = 0; i < numCliques; ++i)//foreach clique
        {
            for (int j = 0; j < nodesPerClique; ++j)//foreach node
            {
                this.add(i * nodesPerClique + j);
                setNodeAttribute("id", i * nodesPerClique + j, Integer.toString(i * nodesPerClique + j));
            }
        }
        //add links
        int mod = numCliques * nodesPerClique;
        int plus = 0;
        if (nodesPerClique > 1) {
            plus = 1;
        }
        for (int i = 0; i < numCliques; ++i)//foreach clique
        {
            for (int j = 0; j < nodesPerClique; ++j)//foreach node
            {
                int source = i * nodesPerClique + j;
                for (int k = 1; k < nodesPerClique - j; ++k) //link between same clique nodes
                {
                    int destination = source + k;
                    //System.out.println(source + "-" + destination);
                    this.add2(source, destination);
                }
            }
            //link between different cliques nodes
            if (numCliques > 2 || (numCliques == 2 && (nodesPerClique >= 2 || i == 0))) {
                int source = i * nodesPerClique;
                int destination = ((i + 1) * nodesPerClique + plus) % mod;
                //System.out.println("Between cliques: " + source + "-" + destination);
                this.add2(source, destination);
            }

        }
    }
}
