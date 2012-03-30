package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.FitnessFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.PopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.util.IndividualUtils;
import cz.cvut.felk.cig.jcool.core.*;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 14.4.2011
 * Time: 14:38
 * Common ancestor for Genotype and Phenotype HillClimbing mutation operators.
 * Ancestors have to implement reproduceInternal and getRepresentationType methods.
 * Also can extend checkConsistency and generational methods.
 */
public abstract class AbstractHillClimbingMutationOperator implements HillClimbingMutationOperator{

    protected ObjectiveFunction function;
    protected FitnessFunction fitnessFunction;
    protected PopulationFactory populationFactory;
    protected RandomGenerator randomGenerator;
    protected int generationNumber;

    public AbstractHillClimbingMutationOperator(){
        this.generationNumber = 0;
    }

    public void checkConsistency(Population[] populations) throws OptimizationException{
        if (this.function == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": function has not been set");
        }
        if (this.randomGenerator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": random generator has not been set");
        }
        if (this.populationFactory == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": population factory has not been set");
        }
        if (populations == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input Population[] cannot be null");
        }
        if (populations.length < this.getInputArity()){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input Population[] has to be at least size of " + this.getInputArity());
        }
        if (this.fitnessFunction == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": fitness function has not been set");
        }
        if (this.fitnessFunction.isDependantOnPopulationComposition()){
            throw new OptimizationException(this.getClass().getSimpleName() + ": fitness function cannot be dependant on population composition, but given " + this.fitnessFunction.getClass().getSimpleName() + " is");
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
        for (int i = 0; i < this.getOutputArity(); i++){
            childrenPopulation[i] = this.populationFactory.createPopulation(children[i]);
        }
        return childrenPopulation;
    }

    protected abstract void reproduceInternal(Individual[] children, Individual[] parents);

    public void setFunction(ObjectiveFunction objectiveFunction) {
        this.function = objectiveFunction;
    }

    public void setFitnessFunction(FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public int getInputArity() {
        return 1;
    }

    public int getOutputArity() {
        return 1;
    }

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
        if (currentGeneration < 0){
            throw new OptimizationException(this.getClass().getSimpleName() + ": generation cannot be negative, but given generation number is " + currentGeneration);
        }
        this.generationNumber = currentGeneration;
    }
}
