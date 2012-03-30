package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.sharing;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.DistanceFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 31.3.2011
 * Time: 11:12
 * Interface common for all SharingFunction suitable as sharing functions for sharing distance niching method.
 */
public interface SharingFunction {

    /**
     * Sets internal DistanceFunction responsible for computation of plain distance between given individuals.
     * @param distanceFunction - DistanceFunction responsible for computation of plain distance as an input value for sharing function.
     */
    public void setDistanceFunction(DistanceFunction distanceFunction);

    /**
     * Computes value of shared function for targetIndividual which is also located in allIndividuals.
     * @param targetIndividual - individual for which the sharing value will be computed.
     * @param secondIndividual - individual in respect to which the sharing value is computed.
     * @return value of sharing function for individual according to secondIndividual.
     */
    public double computeShareValue(Individual targetIndividual, Individual secondIndividual);
}
