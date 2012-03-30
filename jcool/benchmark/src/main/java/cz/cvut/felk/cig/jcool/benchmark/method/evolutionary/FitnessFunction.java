package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

import cz.cvut.felk.cig.jcool.core.FunctionDynamics;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 3.2.2011
 * Time: 21:53:56
 * Function responsible for converting Individual's function value into fitness value.
 * Fitness can vary during one generation on between generations.
 */
public interface FitnessFunction extends FunctionDynamics {

    /**
     * Computes fitness for each individual depending on function value, optionally on position and other Individual or Population properties.
     * @param populations - array of population for which the fitness value should be computed. If FitnessFunction isDynamicWithinGeneration(), then fitness evaluation should be called on all Individuals at the same time.   
     */
    public void computeFitness(Population[] populations);

    /**
     * Flag that indicates that fitness value is dependant on population composition and should be evaluated every time the given populations' individuals may compete with each other - either in survival selection or parent selection.
     * @return true if fitness value depends on populations' composition, not just function value itself.
     */
    public boolean isDependantOnPopulationComposition();

    /**
     * Flag that indicates if concrete FitnessFunction truly implements FunctionDynamics and fitness value has to be reevaluated in every generation.
     * @return - true if fitness value has to be reevaluated in every generation. 
     */
    public boolean isDynamic();
}
