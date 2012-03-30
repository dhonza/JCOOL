package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 7.2.2011
 * Time: 15:00:13
 * Function that computes distance between two given individuals.
 */
public interface DistanceFunction {

    /**
     * Calculates distance between two given individuals. Distance is assumed to be symmetric relation but does not have to be.
     * @param firstIndividual - first individual (can be taken as source).
     * @param secondIndividual - second individual (can be taken as destination).
     * @return -
     */
    public double distance(Individual firstIndividual, Individual secondIndividual);

    /**
     * Returns acceptable Representation type that this function can work with.
     * @return - Representation interface subclass that this function can work with.
     */
    public Class<? extends Representation> getAcceptableType();
}
