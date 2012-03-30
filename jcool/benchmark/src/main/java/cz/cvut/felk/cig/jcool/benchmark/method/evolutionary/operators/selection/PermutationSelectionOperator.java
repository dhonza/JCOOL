package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.util.MathUtils;
import cz.cvut.felk.cig.jcool.benchmark.util.PopulationUtils;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.core.RandomGenerator;
import org.ytoh.configurations.annotations.Component;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 8.5.2011
 * Time: 1:17
 * Selection operator primary for Deterministic Crowding where each individual is chosen exactly once.
 */
@Component(name = "Permutation selection operator", description = "Permutes input individuals and makes desired n-tuples from them. Every individual is chosen exactly one time. Selection for Deterministic Crowding method")
public class PermutationSelectionOperator extends AbstractSelectionOperator{

    protected RandomGenerator randomGenerator;

    public PermutationSelectionOperator(){
        super();
        this.setInputArity(1);
        this.setOutputArity(2);
    }

    @Override
    public void checkConsistency(Population[] inputPopulations) throws OptimizationException{
        super.checkConsistency(inputPopulations);
        if (this.randomGenerator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": randomGenerator has not been set");
        }
        checkInputIndividualsPredicate(PopulationUtils.sumSizes(inputPopulations, this.getInputArity()));
    }

    public Population[] select(Population[] populations) {
        this.checkConsistency(populations);

        Individual[] inputIndividuals = this.concatenateIndividualsFromPopulations(populations, this.inputArity);
        int resultSize = this.individualsPerPopulation * this.outputArity;

        // individuals to swap for perfect permutation
        Individual individual1;
        int idx2; // index of second individual
        // swapping random individuals
        for (int i = 0; i < resultSize; i++){
            individual1 = inputIndividuals[i];
            idx2 = this.randomGenerator.nextInt(inputIndividuals.length);
            inputIndividuals[i] = inputIndividuals[idx2];
            inputIndividuals[idx2] = individual1;
        }

        // making resulting array
        Population[] outputPopulations = this.createOutputPopulations();
        Individual[][] outputIndividuals = this.createOutputIndividuals();
        this.assignIndividualsToPopulations(outputIndividuals, outputPopulations);
        this.mapByColumns(Arrays.asList(inputIndividuals), outputIndividuals);

        return outputPopulations;
    }

    /**
     * Checks if sum of input individuals is product of output arity and individuals per population.
     * @param sumSizes - sum of individuals in input.
     */
    protected void checkInputIndividualsPredicate(int sumSizes){
        if (sumSizes != this.getOutputArity() * this.getIndividualsPerPopulation()){
            throw new OptimizationException(this.getClass().getSimpleName() + ": sum of Individuals in input Populations has to be same as product of output arity and individuals per population, but the value " + sumSizes + " is not product of " + this.getOutputArity() + " and " + this.getIndividualsPerPopulation());
        }
    }
    
    @Override
    protected int[] getResultsSizesCore(int[] parentPopulationSizes) {
        this.checkInputIndividualsPredicate(MathUtils.sumSizes(parentPopulationSizes));
        return super.getResultsSizesCore(parentPopulationSizes);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setRandomGenerator(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    public void resetGenerationCount() {
    }

    public void nextGeneration() {
    }

    public void setGeneration(int currentGeneration) {
    }
}
