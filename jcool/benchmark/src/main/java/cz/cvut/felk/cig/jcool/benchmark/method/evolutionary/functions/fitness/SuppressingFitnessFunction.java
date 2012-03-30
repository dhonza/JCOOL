package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.fitness;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.FitnessFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.derating.DeratingFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.DistanceFunction;
import cz.cvut.felk.cig.jcool.core.Point;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 15.4.2011
 * Time: 12:42
 * Fitness function that is capable of suppressing fitness value of individual depending on its position.
 * Uses raw fitness function to evaluate individual fitness and after that it recomputes it according to internal suppressing points.
 * Function dynamics should be redirected into rawFitnessFunction.
 */
public interface SuppressingFitnessFunction extends FitnessFunction {

    /**
     * Sets FitnessFunction instance responsible for computation of raw fitness value dependant only on individual function value.
     * @param rawFitnessFunction - FitnessFunction instance responsible for computation of raw fitness.
     */
    public void setRawFitnessFunction(FitnessFunction rawFitnessFunction);

    /**
     * Sets DeratingFunction responsible for computation of derated multiplier for given pair of points.
     * @param deratingFunction - function responsible for computation of derated multiplier.
     */
    public void setDeratingFunction(DeratingFunction deratingFunction);

    /**
     * Sets distance function responsible for measuring distance of given individuals.
     * @param distanceFunction - function responsible for measuring
     */
    public void setDistanceFunction(DistanceFunction distanceFunction);

    /**
     * Adds given point into internal list of suppressing points.
     * @param suppressingPoint - point which surrounding to suppress.
     */
    public void addSuppressingPoint(Point suppressingPoint);

    /**
     * Clears all internal data for suppressing fitness values and starts with clear list again.
     */
    public void resetSuppressingPoints();
}
