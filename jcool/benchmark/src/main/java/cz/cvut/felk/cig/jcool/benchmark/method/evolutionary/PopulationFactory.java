package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 3.2.2011
 * Time: 21:27:51
 * Factory responsible for creation of concrete Population implementation.
 */
public interface PopulationFactory {

    /**
     * Creates empty Population instance.
     * @return - empty Population implementation instance. 
     */
    public Population createPopulation();

    /**
     * Creates Population instance which contains individuals.
     * @param individuals - Individual instances to be part of resulting Population.
     * @return Population instance with individuals.
     */
    public Population createPopulation(Individual[] individuals);
}
