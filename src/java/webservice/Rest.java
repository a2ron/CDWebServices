package webservice;

// Title:       REST Web Service
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import cd.CDAlgorithm;
import cd.hierarchical.divisive.NewmanGirvan;
import cd.hierarchical.agglomerative.ALink;
import ikor.math.DenseVector;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import noesis.Attribute;
import noesis.AttributeNetwork;

/**
 * REST Web Service
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Stateless
@Path("/cd")
public class Rest {
   
    protected CDAlgorithm cd;
    
    /**
     * Retrieves representation of Attributed network instance of com.ws.Rest
     * @param source Data input
     * @return Clusters assign
     */
    @POST
    @Produces("text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public String post(@FormParam("source") String source) {
    	Date date = new Date();
    	DateFormat format = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    	format.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
    	System.out.println("-------------------------------");
    	System.out.println("Date: "+format.format(date));
    	System.out.println("Method: POST"); 
        System.out.println("Data: "+source);
    	System.out.println("-------------------------------");
        // Preprocess CSV input -> Compute -> Postprocess output
        return postProcessCSV(compute(preProcessCSV(source)));
    }

    /**
     * Retrieves representation of Attributed network instance of com.ws.Rest
     * @param source Data input
     * @return Clusters assign
     */
    @GET
    @Produces("text/plain")
    @Consumes("text/plain")
    public String get(@QueryParam("source") String source) {
    	Date date = new Date();
    	DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	format.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
    	System.out.println("-------------------------------");
    	System.out.println("Date: "+format.format(date));
    	System.out.println("Method: GET");
        System.out.println("Data: "+source);
    	System.out.println("-------------------------------");
        // Preprocess CSV input -> Compute -> Postprocess output
        return postProcessCSV(compute(preProcessCSV(source)));
    }
        

    /**
     * Preprocessing source data to create an attribute network
     * @param source Data input
     * @return attribute network
     */
    private AttributeNetwork preProcessCSV(String source){
        // Split by ;
        String [] s = source.split(";");

        // Create network with 'id' attribute
        AttributeNetwork an = new AttributeNetwork();
        an.addNodeAttribute(new Attribute("id", new ikor.model.data.IntegerModel()));
        
        // Add nodes
        try
        {
            long nodes = Long.parseLong(s[0]);
            for (int i = 0; i<nodes; ++i)
            {
                an.add(i);
                an.setNodeAttribute("id", i, Integer.toString(i));
            }
        } catch (Exception error)
        {
            System.out.println("Error to create network: "+error.toString());
            return new AttributeNetwork();
        } 
        
        // Add links
        try
        {
            for (int i = 1; i<s.length; ++i)
            {
                // Get source and destination
                String [] link = s[i].split("-");
                int ns = Integer.parseInt(link[0]);
                int nd = Integer.parseInt(link[1]);
                // Add
                if (ns >= 0 && nd >= 0)
                    an.add2(ns,nd);
            }
        } catch (Exception error)
        {
            System.out.println("Error to create network: "+error.toString());
            return new AttributeNetwork();
        }
        
        return an;
    }
    
    
    /**
     * Compute 
     * @param source Data input
     * @return attribute network
     */
    private DenseVector compute(AttributeNetwork an)
    {
    	// If there are nodes to cluster
    	if (an.nodes() > 1)
    	{
	        // Inicialize algorithm and compute
    		if (an.nodes() > 200)
    			cd = new ALink(an);
    		else
    			cd = new NewmanGirvan(an);
    		cd.compute();
	        //Return the best assignment of clusters
	        return cd.getBest();
    	}
    	else
    		return new DenseVector(1);
    }
    
    
    /**
     * Convert an assignment of clusters to CSV format
     * @param source Data input
     * @return assignment in csv format
     */
    private String postProcessCSV(DenseVector source)
    {
        String r = "";
        for (int i = 0; i < source.size(); ++i)
        {
            r = r + (int) source.get(i) + ";";
        }
        return r.substring(0, r.length()-1);
    }
}
