package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.core.OptimizationMethod;
import cz.cvut.felk.cig.jcool.core.RandomGenerator;
import cz.cvut.felk.cig.jcool.core.ValuePointListTelemetry;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 23.1.2011
 * Time: 14:30:20
 * Root interface for all EvolutionaryOptimizationMethods.
 * Contains declarations of all necessary methods that define basic evolutionary optimization methods.
 * Responsibility of method is to distribute set factories, generators and functions into underlying operators and other components.
 * Also has to set correct bindings between operators (arity, individualsPerPopulation) and to take care of correct population size.
 */
public interface EvolutionaryOptimizationMethod<T extends ValuePointListTelemetry> extends OptimizationMethod<T> {

    /**
     * Returns population size at the beginning of first generation.
     * @return population size at the beginning of first generation.
     */
    public int getPopulationSize();

    /**
     * Sets population size at the beginning of first generation.
     * @param size - intended population size at the beginning of first generation.
     */
    public void setPopulationSize(int size);

    /**
     * Validates settings of this method before first optimisation step is executed Should be called from init() method.
     * @throws cz.cvut.felk.cig.jcool.core.OptimizationException if some parameter is set incorrectly or not at all.
     */
    public void validateConfiguration() throws OptimizationException;

    /**
     * Sets FunctionEvaluator instance responsible for evaluation of Individuals function value.
     * @param functionEvaluator - instance of FunctionEvaluator responsible for evaluation of Individuals function value.
     */
    public void setFunctionEvaluator(FunctionEvaluator functionEvaluator);

    /**
     * Returns current Representation that this method uses.
     * Left for responsibility of the method, because not every method has to have fully implement setRepresentationFactory - can use only one pre-specified.
     * @return currently used Representation subclass class or Representation.class if representation factory not set
     */
    public Class<? extends Representation> getRepresentationType();

    /**
     * Sets RandomGenerator instance that one or more of underlying components might use
     * @param randomGenerator - instance of RandomGenerator that will be used for generating random numbers.
     */
    public void setRandomGenerator(RandomGenerator randomGenerator);

    /**
     * Sets RepresentationFactory instance that will be responsible creation or Representation instances for Individuals.
     * @param representationFactory - instance of RepresentationFactory that will be responsible for creation of correctly encoded representations.
     */
    public void setRepresentationFactory(RepresentationFactory representationFactory);

    /**
     * Sets PopulationFactory instance that will be used for creation of new Population instances during the optimization run.
     * @param populationFactory - instance of PopulationFactory responsible for creation of Population instances.
     */
    public void setPopulationFactory(PopulationFactory populationFactory);

    /**
     * Sets IndividualFactory instance that will be used for creation of new Individual instances during the optimization run.
     * @param individualFactory - instance of IndividualFactory responsible for creation of Individual instances.
     */
    public void setIndividualFactory(IndividualFactory individualFactory);

    /**
     * Sets FitnessFunction instance that will transform function value into fitness.
     * @param fitnessFunction - instance that will compute individual fitness.
     */
    public void setFitnessFunction(FitnessFunction fitnessFunction);
}
