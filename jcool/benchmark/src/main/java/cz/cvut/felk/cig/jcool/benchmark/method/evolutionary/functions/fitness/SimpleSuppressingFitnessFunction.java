package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.fitness;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.DistanceFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.FitnessFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.derating.DeratingFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.derating.PowerLawDeratingFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.distance.EuclideanDistanceFunction;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.ui.DynamicDropDown;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 15.4.2011
 * Time: 12:50
 * Fitness function that suppresses individual raw fitness value depending on its position, given suppressing points and used derating function.
 * Methods nextGeneration, setGeneration and resetGeneration are redirected to underlying fitness function. Method resetSuppressingPoints is used for blanking suppressing points.
 */
@Component(name = "Suppressing fitness function", description = "Lowers individual fitness value if it is in range of previously set point(s). Used for Sequential niching optimization method")
public class SimpleSuppressingFitnessFunction implements SuppressingFitnessFunction{

    @Property(name = "raw fitness function", description = "fitness function responsible for computation of fitness depending only on individual function value")
    @DynamicDropDown(key = "fitnessFunctions", type = FitnessFunction.class, label = DynamicDropDown.Label.NAME)
    protected FitnessFunction rawFitnessFunction = new SimpleFitnessFunction();

    @Property(name = "derating function", description = "function for computation of derated fitness multiplier for every suppressing point and individual")
    @DynamicDropDown(key = "deratingFunctions", type = DeratingFunction.class, label = DynamicDropDown.Label.NAME)
    protected DeratingFunction deratingFunction = new PowerLawDeratingFunction();

    @Property(name = "distance function", description = "distance function responsible for computation of distance between individual and suppressing point")
    @DynamicDropDown(key = "distanceFunctions", type = DistanceFunction.class, label = DynamicDropDown.Label.NAME)
    protected DistanceFunction distanceFunction = new EuclideanDistanceFunction();

    // individual for encapsulation of suppressing point
    protected Individual temporaryIndividual;
    // suppressing points stored as arrays of double
    protected List<double[]> suppressingPoints;

    public SimpleSuppressingFitnessFunction(){
        this.suppressingPoints = new ArrayList<double[]>();
    }

    public void addSuppressingPoint(Point point) {
        this.suppressingPoints.add(point.toArray());
    }

    public void resetSuppressingPoints() {
        this.suppressingPoints.clear();
    }

    protected void checkConsistency(Population[] populations){
        if (this.rawFitnessFunction == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": raw fitness function has not been set");
        }
        if (this.deratingFunction == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": derating function has not been set");
        }
        if (this.distanceFunction == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": distance function has not been set");
        }
        if (populations == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input Population[] cannot be null");
        }
    }

    public void computeFitness(Population[] populations) {
        this.checkConsistency(populations);

        this.temporaryIndividual = null;
        this.rawFitnessFunction.computeFitness(populations);
        for (Population population : populations){
            for (Individual individual : population.getIndividuals()){
                this.computeSuppressedFitness(individual);
            }
        }
    }

    /**
     * Inner method responsible for computation of suppressed fitness for given individual.
     * @param individual - individual for which the suppressed fitness should be computed. 
     */
    protected void computeSuppressedFitness(Individual individual){
        double rawFitness = individual.getFitness(); // can be positive or negative as well

        if (this.temporaryIndividual == null){
            this.temporaryIndividual = individual.copy(0); // making temporary individual with representation of the same type as has given population - no need of factory
        }
        double deratingMultiplier = 1.0;
        // for all suppressing points compute distance between them and current individual, use that value as input for given derating function and multiply current fitness value with returned multiplier
        for (double[] point : this.suppressingPoints){
            this.temporaryIndividual.getRepresentation().setDoubleValue(point);
            deratingMultiplier *= this.deratingFunction.getDeratedMultiplier(this.distanceFunction.distance(individual, this.temporaryIndividual));
        }
        double suppressedFitness;
        if (rawFitness > 0){
            suppressedFitness = rawFitness * deratingMultiplier; // positive values are multiplied, i.e 10 -> 8
        } else {
            suppressedFitness = rawFitness / deratingMultiplier; // negative values are divided, i.e -10 -> -12.5
        }
        // don't forget to set suppressed fitness to the individual
        individual.setFitness(suppressedFitness);
    }

    public boolean isDependantOnPopulationComposition() {
        return this.rawFitnessFunction.isDependantOnPopulationComposition();
    }

    public boolean isDynamic() {
        return this.rawFitnessFunction.isDynamic();
    }

    public void resetGenerationCount() {
        this.rawFitnessFunction.resetGenerationCount();
    }

    public void nextGeneration() {
        this.rawFitnessFunction.nextGeneration();
    }

    public void setGeneration(int currentGeneration) {
        this.rawFitnessFunction.setGeneration(currentGeneration);
    }

    public FitnessFunction getRawFitnessFunction() {
        return rawFitnessFunction;
    }

    public void setRawFitnessFunction(FitnessFunction rawFitnessFunction) {
        this.rawFitnessFunction = rawFitnessFunction;
    }

    public DistanceFunction getDistanceFunction() {
        return distanceFunction;
    }

    public void setDistanceFunction(DistanceFunction distanceFunction) {
        this.distanceFunction = distanceFunction;
    }

    public DeratingFunction getDeratingFunction() {
        return deratingFunction;
    }

    public void setDeratingFunction(DeratingFunction deratingFunction) {
        this.deratingFunction = deratingFunction;
    }
}
