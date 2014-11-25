package cd.score;

// Title:       Cohesion Coefficient Test
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
 * Cohesion Coefficient Test
 * 
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
public class CohesionCoefficientTest {
    private double error = 1e-4;
    private CliquesNetwork n3x3;
    private DenseVector a3x3;
    
    public CohesionCoefficientTest() {
        
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
        CohesionCoefficient instance = new CohesionCoefficient(n3x3,a3x3);
        double expResult = 2.0;
        double result = instance.overallValue();
        assertEquals(expResult, result, error);
        System.out.println("Cohesion 3x3 Passed");
    }
    
    
    public static void main(String[] args) throws IOException {
        CohesionCoefficientTest cct = new CohesionCoefficientTest();
        cct.setUp();
        cct.test3x3();        
        System.exit(0);
    }
}
