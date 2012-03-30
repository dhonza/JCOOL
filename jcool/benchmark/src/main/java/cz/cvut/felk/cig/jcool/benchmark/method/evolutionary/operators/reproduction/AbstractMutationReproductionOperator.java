package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.PopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.ReproductionOperator;
import cz.cvut.felk.cig.jcool.benchmark.util.IndividualUtils;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.3.2011
 * Time: 18:24
 * Common ancestor for all mutation operators providing the basic functionality.
 */
public abstract class AbstractMutationReproductionOperator implements ReproductionOperator {

    @Property(name = "mutation probability", description = "Probability of mutation of one value")
    @Range(from = 0.0, to = 1.0)
    protected double mutationProbability = 0.1;

    ObjectiveFunction function;
    PopulationFactory populationFactory;
    RandomGenerator randomGenerator;
    int generationNumber = 0;

    public void checkConsistency(Population[] populations) throws OptimizationException{
        if (this.function == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": function has not been set");
        }
        if (this.randomGenerator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": randomGenerator has not been set");
        }
        if (this.populationFactory == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": populationFactory has not been set");
        }
        if (this.mutationProbability < 0.0 || this.mutationProbability > 1.0){
            throw new OptimizationException(this.getClass().getSimpleName() + ": mutationProbability has to be in range <0.0, 1.0>");
        }
        if (populations == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input Population[] cannot be null");
        }
        if (populations.length < this.getInputArity()){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input Population[] has to be at least size of " + this.getInputArity());
        }
    }

    public Population[] reproduce(Population[] populations) {
        this.checkConsistency(populations);

        Individual[] parents = populations[0].getIndividuals();
        Individual[][] children = new Individual[this.getOutputArity()][];
        for (int i = 0; i < this.getOutputArity(); i++){
            children[i] = IndividualUtils.makeCopy(parents, this.generationNumber);
            this.reproduceInternal(children[i], parents);
        }

        // finally pack Individuals into Population container and return it
        Population[] childrenPopulation = new Population[this.getOutputArity()];
        for (int i = 0; i < getOutputArity(); i++){
            childrenPopulation[i] = this.populationFactory.createPopulation(children[i]);
        }
        return childrenPopulation;
    }

    /**
     * Executes the core reproduction activity, including setting parent fitness!
     * @param children - deep copy of parents that will be mutated.
     * @param parents - parents from which the children originate.
     */
    protected abstract void reproduceInternal(Individual[] children, Individual[] parents);

    public void setFunction(ObjectiveFunction objectiveFunction) {
        this.function = objectiveFunction;
    }

    /**
     * Each Individual has only one parent.
     * @return 1 means that there is only one parent for new Individual
     */
    public int getInputArity() {
        return 1;
    }

    public int getOutputArity() {
        return 1;
    }

    /**
     * Returns size of the first Population in input parameter
     *
     * @param parentPopulationSizes - sizes of parent populations.
     * @return size of first Population.
     */
    public int[] getResultsSizes(int[] parentPopulationSizes) {
        if (parentPopulationSizes == null || parentPopulationSizes.length < this.getInputArity()){
            throw new OptimizationException(this.getClass().getSimpleName() + ": parentPopulationSizes has to be non-null and at least size of " + this.getInputArity());
        }
        return new int[]{parentPopulationSizes[0]};
    }

    public void setPopulationFactory(PopulationFactory populationFactory) {
        this.populationFactory = populationFactory;
    }

    public void setRandomGenerator(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    public void resetGenerationCount() {
        this.generationNumber = 0;
    }

    public void nextGeneration() {
        this.generationNumber++;
    }

    public void setGeneration(int currentGeneration) {
        this.generationNumber = currentGeneration;
    }

    public double getMutationProbability() {
        return this.mutationProbability;
    }

    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }
}
