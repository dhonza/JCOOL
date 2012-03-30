package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

import cz.cvut.felk.cig.jcool.core.Function;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 14.2.2011
 * Time: 21:26:16
 * Evaluates function values for all Individuals in given Populations.
 * Can have many implementations, such as sequential, parallel, adaptive parallel,...
 */
public interface FunctionEvaluator {

    /**
     * Evaluates all Individuals in all Populations according to given function.
     * @param populations - populations to be evaluated.
     * @param function - function according to which the Individuals position will be evaluated. 
     */
    public void evaluate(Population[] populations, Function function);
}
