package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

import cz.cvut.felk.cig.jcool.core.FunctionDynamics;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.core.RandomGenerator;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 3.2.2011
 * Time: 20:17:45
 * Common interface for all evolutionary operators, such as SelectionOperator, ReproductionOperator,...
 * Contains all the common and nonfunctional methods, thus only operator input arity and output result sizes and setter for PopulationFactory and RandomGenerator that might be needed.
 */
public interface EvolutionaryOperator extends FunctionDynamics {

    /**
     * Returns arity of input individuals, which means how many populations this evolutionary operator demands.
     * Assumes that Individuals at the same index are parents for resulting child/children.
     * @return nonzero value meaning of how many Populations are taken into account. Any overhung population can be ignored.
     */
    public int getInputArity();

    /**
     * Returns arity of output Populations. The returned value is obligatory, which means that the arity won't be less.
     * However, the output arity can be bigger in some special cases.
     * @return nonzero value meaning minimal populations count that will be on correct output.
     */
    public int getOutputArity();

    /**
     * Returns sizes of resultant populations depending on sizes of input populations.
     * Serves for determination of correctly set values of all operators and parameters in evolutionary algorithm.
     * parameter and resulting values are made arrays for further expansion and "wild" breeding operators.
     * @param parentPopulationSizes - sizes of parent populations.
     * @return array of sizes of children populations.
     */
    public int[] getResultsSizes(int[] parentPopulationSizes);

    /**
     * Every evolutionary operator returns Individuals encapsulated in new Population instance so PopulationFactory is necessary method to be implemented. 
     * @param populationFactory - factory responsible for creation of new empty populations.
     */
    public void setPopulationFactory(PopulationFactory populationFactory);

    /**
     * Evolutionary operators can utilise generator of random numbers and it is more convenient to set it once than every time per main operation call.
     * Some implementations can be empty, but no harm done - than operator either does not uses random numbers at all or has its own specific random number generator. 
     * @param randomGenerator - RandomGenerator instance for optional generation of random numbers.
     */
    public void setRandomGenerator(RandomGenerator randomGenerator);

    /**
     * Checks consistency of internal parameters as well as input populations criteria.
     * @param populations - input populations.
     * @throws cz.cvut.felk.cig.jcool.core.OptimizationException if some parameter is set incorrectly or input populations does not meet required criteria.
     */
    public void checkConsistency(Population[] populations) throws OptimizationException;
}
