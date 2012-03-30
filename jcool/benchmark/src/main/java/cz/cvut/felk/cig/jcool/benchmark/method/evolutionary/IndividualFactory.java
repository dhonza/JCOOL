package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.RandomGenerator;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 28.1.2011
 * Time: 20:52:42
 * Graduates optimization method from complex process of creation of new Individual with its Representation.
 * Can make a lot of 'magic' while generation of points belonging to given ObjectiveFunction.
 * Resulting Individuals also have to have set their Representation properly and that's where RepresentationFactory comes to place.  
 * Does not need to bother with RandomGenerator either, because it is provided as well.
 */
public interface IndividualFactory {

    /**
     * Creates new Individuals according to inside logic and provided function, factory and random generator. 
     * @param count - how many Individuals to create.
     * @param birthday - generation in which the Individuals were born.
     * @return array of newly created Individuals with Representation defined by representationFactory.
     */
    Individual[] createIndividuals(int count, int birthday);

    /**
     * Sets ObjectiveFunction for which the Individuals will be created.
     * @param objectiveFunction - function for which the Individuals will be created. 
     */
    public void setFunction(ObjectiveFunction objectiveFunction);

    /**
     * Sets factory responsible for generation of appropriate representations for Individuals.
     * @param representationFactory - factory generating proper Representation.
     */
    public void setRepresentationFactory(RepresentationFactory representationFactory);

    /**
     * Sets optional RandomGenerator instance which will be used for random positions generation. 
     * @param randomGenerator - random number generator used for generation of random positions within function bounds, etc.
     */
    public void setRandomGenerator(RandomGenerator randomGenerator);
}
