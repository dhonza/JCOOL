package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 23.1.2011
 * Time: 14:35:18
 * Common interface that defines necessary methods for work with individual's representation.
 * This interface just describes methods for encoding and decoding of double value for continous optimization.
 * Every concrete Representation is responsible for its decode and manipulation. 
 */   
public interface Representation {
    /**
     * Decodes this representation into phenotype values.
     * @return array of double values describing current position in phenotype representation.
     */
    double[] getDoubleValue();

    /**
     * Setter just for completeness and convenience when setting whole new value.
     * @param phenotypeValue - new location in n-dimensional space.
     */
    public void setDoubleValue(double[] phenotypeValue);

    public Representation copy();
}
