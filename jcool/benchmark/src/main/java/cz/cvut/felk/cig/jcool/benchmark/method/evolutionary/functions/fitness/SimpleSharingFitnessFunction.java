package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.fitness;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.FitnessFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.sharing.SharingFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.sharing.SimpleSharingFunction;
import cz.cvut.felk.cig.jcool.benchmark.util.IndividualUtils;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;
import org.ytoh.configurations.ui.DynamicDropDown;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 28.3.2011
 * Time: 21:37
 * Implementation of FitnessFunction suitable for Niching FitnessSharing method.
 */
@Component(name = "Sharing fitness function", description = "Distance function suitable for Niching Fitness sharing method")
public class SimpleSharingFitnessFunction implements SharingFitnessFunction {

    @Property(name = "raw fitness function", description = "Fitness function used to compute raw fitness value of each individual")
    @DynamicDropDown(key = "fitnessFunctions", type = FitnessFunction.class, label = DynamicDropDown.Label.NAME)
    protected FitnessFunction rawFitnessFunction = new SimpleFitnessFunction();

    @Property(name = "raw fitness scaling factor", description = "coefficient that powers the raw fitness value. Sufficiently large beta will smooth the resultant fitness sharing function")
    @Range(from = 1.0, to = Double.MAX_VALUE)
    protected double beta = 1.0;

    @Property(name = "sharing distance function", description = "distance function suitable for niching Fitness sharing method")
    @DynamicDropDown(key = "sharingDistanceFunctions", type = SharingFunction.class, label = DynamicDropDown.Label.NAME)
    protected SharingFunction sharingFunction = new SimpleSharingFunction();

    public void computeFitness(Population[] populations) {
        this.checkConsistency();

        // first cache simple fitness values of each Individual
        this.rawFitnessFunction.computeFitness(populations);

        Individual[] individuals = IndividualUtils.getAllIndividuals(populations);
        // compute sharing fitness of each Individual
        for (Individual individual : individuals){
            individual.setFitness(Math.pow(individual.getFitness(), this.beta) / this.computeSharingDistance(individual, individuals));
        }
    }

    /**
     * Computes sharingFunction between given individual and the other given individuals.
     * @param individual - Individual to which the sharingFunction should be computed.
     * @param others - other Individuals with compare to them the sharingFunction is being computed.
     * @return sharingDistance value between given individual and the other individuals.
     */
    protected double computeSharingDistance(Individual individual, Individual[] others){
        double sharingDistance = 0.0;
        for (Individual other : others){
            sharingDistance += this.sharingFunction.computeShareValue(individual, other);
        }
        return sharingDistance;
    }

    protected void checkConsistency(){
        if (this.rawFitnessFunction == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": raw fitness function has not been set!");
        }
        if (this.sharingFunction == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": sharing function has not been set!");
        }
    }

    public boolean isDependantOnPopulationComposition() {
        return true;
    }

    public boolean isDynamic() {
        return false;
    }

    public void resetGenerationCount() {
    }

    public void nextGeneration() {
    }

    public void setGeneration(int currentGeneration) {
    }

    public FitnessFunction getRawFitnessFunction() {
        return this.rawFitnessFunction;
    }

    public void setRawFitnessFunction(FitnessFunction rawFitnessFunction) {
        if (rawFitnessFunction != this){
            this.rawFitnessFunction = rawFitnessFunction;
        } else {
            this.rawFitnessFunction = null;
        }
    }

    public SharingFunction getSharingFunction() {
        return this.sharingFunction;
    }

    public void setSharingFunction(SharingFunction sharingFunction) {
        this.sharingFunction = sharingFunction;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }
}
