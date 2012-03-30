package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.FitnessFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.ReproductionOperator;
import cz.cvut.felk.cig.jcool.core.StopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 31.3.2011
 * Time: 14:03
 * Common interface for HillClimbing mutation reproduction operators.
 */
public interface HillClimbingMutationOperator extends ReproductionOperator {

    /**
     * Sets FitnessFunction instance responsible for transformation of function value into fitness.
     * @param fitnessFunction - function responsible for transformation of function value into fitness.
     */
    public void setFitnessFunction(FitnessFunction fitnessFunction);

    /**
     * Returns one or more internal StopConditions that controls mutation process.
     * @return one or more internal StopConditions that controls mutation process.
     */
    public StopCondition[] getStopConditions();
}
