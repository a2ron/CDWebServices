package cd.score;

// Title:       Separation Coefficient Test
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
 * Separation Coefficient Test
 * 
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
public class SeparationCoefficientTest {
    private double error = 1e-4;
    private CliquesNetwork n3x3;
    private DenseVector a3x3;
    
    public SeparationCoefficientTest() {
        
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
        SeparationCoefficient instance = new SeparationCoefficient(n3x3,a3x3);
        double expResult = 14.0;
        double result = instance.overallValue();
        assertEquals(expResult, result, error);
        System.out.println("Separation 3x3 Passed");
    }
    
    
    public static void main(String[] args) throws IOException {
        SeparationCoefficientTest cct = new SeparationCoefficientTest();
        cct.setUp();
        cct.test3x3();        
        System.exit(0);
    }
}
