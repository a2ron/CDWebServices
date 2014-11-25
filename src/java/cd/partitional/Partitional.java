package cd.partitional;

// Title:       Generic Partitional Algorithm
// Version:     1.0
// Copyright:   2014
// Author:      Fco Javier Gijon - Aaron Rosas
// E-mail:      fcojaviergijon@gmail.com - aarr90@gmail.com

import cd.CDAlgorithm;
import ikor.model.data.annotations.Description;
import ikor.model.data.annotations.Label;
import noesis.AttributeNetwork;

/**
 * Generic Partitional Algorithm
 *
 * @author Aaron Rosas (aarr90@gmail.com) , Fco Javier Gijon
 * (fcojaviergijon@gmail.com)
 *
 */
@Label("PartitionalAlgorithm")
@Description("PartitionalAlgorithm")
public abstract class Partitional extends CDAlgorithm {

    public Partitional(AttributeNetwork network) {
        super(network);
    }

}
