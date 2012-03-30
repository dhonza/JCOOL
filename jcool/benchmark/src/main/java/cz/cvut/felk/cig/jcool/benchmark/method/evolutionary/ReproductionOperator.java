package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 30.1.2011
 * Time: 20:12:50
 * Executes reproduction upon given arrays of individuals.
 * Reproduction operator is representation dependant.
 * Usually returns only one resulting population, but it can vary.
 * Returning Individuals ARE new Individuals.
 */
public interface ReproductionOperator extends EvolutionaryOperator{

    /**
     * Performs reproduction with given array of populations.
     * @param populations - array of Populations from which the reproduction is being performed.
     * @return - array of Population[] containing children Individuals. Size of array depends on how many children are produced for each  parent "chunk" 
     */
    public Population[] reproduce(Population[] populations);

    /**
     * Sets current ObjectiveFunction that Individuals belong to.
     * If individual is created through mutation, then necessary for method inBounds(Point).
     * @param objectiveFunction - function to which definition scope the Individuals has to fit.
     */
    public void setFunction(ObjectiveFunction objectiveFunction);

    /**
     * Returns concrete representation type with which this reproduction operator can manipulate.
     * @return
     */
    public Class<? extends Representation> getAcceptableType();
}
