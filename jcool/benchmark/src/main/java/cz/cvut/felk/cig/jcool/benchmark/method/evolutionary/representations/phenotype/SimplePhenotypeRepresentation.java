package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.PhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.2.2011
 * Time: 15:41:14
 * SimplePhenotypeRepresentation simply copies given array into private property.
 */
public class SimplePhenotypeRepresentation implements PhenotypeRepresentation{

    private double[] representation;

    public SimplePhenotypeRepresentation(){
    }

    public SimplePhenotypeRepresentation(double[] phenotypeValue){
        this.setDoubleValue(phenotypeValue);
    }

    public SimplePhenotypeRepresentation(SimplePhenotypeRepresentation second){
        this.setDoubleValue(second.getDoubleValue()); // safe copy of null value.
    }
    
    public double[] getDoubleValue() {
        if (this.representation != null){
            return Arrays.copyOf(this.representation, this.representation.length);
        } else {
            return new double[0];
        }

    }

    public void setDoubleValue(double[] phenotypeValue) {
        if (phenotypeValue != null){
            if (this.representation != null && this.representation.length == phenotypeValue.length){
                System.arraycopy(phenotypeValue, 0, this.representation, 0, this.representation.length);
            } else {
                this.representation = Arrays.copyOf(phenotypeValue, phenotypeValue.length);
            }
        } else {
            this.representation = new double[0]; // intention to put null array falls in creation of empty array.
        }

    }

    public Representation copy() {
        return new SimplePhenotypeRepresentation(this);
    }
}
