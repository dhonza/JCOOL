package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 24.1.2011
 * Time: 21:10:34
 * Wrapper for array of Individuals that provide some extra functionality as well as convenient manipulation with individuals. 
 */
public interface Population {

    /**
     * Returns the best individual in the population according to the lowest function value. Function value is assumed to be evaluated before.
     * @return Individual with the lowest function value or null, if individuals size is zero.
     */
    public Individual getBestIndividual();

    /**
     * Returns the best individual according to the highest fitness value. Fitness is assumed to be evaluated before.
     * @return Individual with the highest fitness or null, if individuals size is zero.
     */
    public Individual getFittestIndividual();

    /**
     * Returns Individuals in array so that Breeding and Selection operators could manipulate with Individuals directly.
     * @return Individuals in array so that Breeding and Selection operators could manipulate with Individuals directly.
     */
    public Individual[] getIndividuals();

    /**
     * Sets content of this Population
     * @param individuals - Individuals that compose population.
     */
    public void setIndividuals(Individual[] individuals);
}
