package cd.score;

// Title:       Silhouette Coefficient Test
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import cd.CliquesNetwork;
import ikor.math.DenseVector;
import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Silhouette Coefficient Test
 * 
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
public class SilhouetteCoefficientTest {
    private double error = 1e-4;
    private CliquesNetwork n3x3;
    private DenseVector a3x3;
    
    public SilhouetteCoefficientTest() {
        
        n3x3 = new CliquesNetwork(3,3);
        a3x3 = new DenseVector(3*3);
    }
    
    @Before
    public void setUp() {
        for (int i = 0; i < a3x3.size(); ++i)
            a3x3.set(i, (i/3)+1);
    }
    

    @Test
    public void test3x3() {
        SilhouetteCoefficient instance = new SilhouetteCoefficient(n3x3,a3x3);
        double expResult = 0.475;
        double result = instance.overallValue();
        assertEquals(expResult, result, error);
        System.out.println("Silhouette 3x3 Passed");
    }
    
    
    public static void main(String[] args) throws IOException {
        SilhouetteCoefficientTest cct = new SilhouetteCoefficientTest();
        cct.setUp();
        cct.test3x3();        
        System.exit(0);
    }
}
