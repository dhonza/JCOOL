package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.fitness;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.sharing.SharingFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.FitnessFunction;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 31.3.2011
 * Time: 11:02
 * Interface common for all FitnessFunction suitable for Fitness sharing niching method.
 */
public interface SharingFitnessFunction extends FitnessFunction {

    /**
     * Sets required FitnessFunction responsible for computation of pure fitness of all individuals.
     * @param simpleFitnessFunction - FitnessFunction that simply transforms function value into fitness value.
     */
    public void setRawFitnessFunction(FitnessFunction simpleFitnessFunction);

    /**
     * Sets special SharingFunction that will return special sharing distance between given individuals.
     * @param sharingFunction - distance function responsible for computing the sharing distance.
     */
    public void setSharingFunction(SharingFunction sharingFunction);
}
