package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.util.MathUtils;
import cz.cvut.felk.cig.jcool.benchmark.util.PopulationUtils;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.core.RandomGenerator;
import org.ytoh.configurations.annotations.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 22.2.2011
 * Time: 11:42
 * Uniform deterministic selection operator. Each Individual in input population is chosen exactly n-times. Output population count and sizes have to be integral multiple of input Individuals count.
 * Output arity different from 1 does not have much sense since this operator only expands each Individual by fixed times. However the individuals are mapped by columns.
 * Individuals per population will be dynamically determined depending on input Individuals count, output arity and broodSize.
 */
@Component(name = "Uniform deterministic selection operator", description = "Chooses each Individual in input Populations exactly n-times")
public class UniformDeterministicSelectionOperator extends AbstractSelectionOperator{

    //brood size is for internal purposes only
    protected int broodSize;

    public UniformDeterministicSelectionOperator(){
        this.setIndividualsPerPopulation(1);
    }

    public Population[] select(Population[] populations) {
        this.checkConsistency(populations);

        // unify populations for easy linear access to all input Individuals
        Individual[] inputIndividuals = this.concatenateIndividualsFromPopulations(populations, this.inputArity);

        int totalOutput = inputIndividuals.length * this.broodSize;
        List<Individual> outputList = new ArrayList<Individual>();
        // each Individual will be selected exactly broodSize times
        for (Individual individual : inputIndividuals){
            for (int i = 0; i < this.broodSize; i++){
                outputList.add(individual);
            }
        }
        // preparation of output
        this.individualsPerPopulation = totalOutput / this.outputArity;
        Population[] outputPopulations = this.createOutputPopulations();
        Individual[][] outputIndividuals = this.createOutputIndividuals();
        this.mapByColumns(outputList, outputIndividuals);
        this.assignIndividualsToPopulations(outputIndividuals, outputPopulations);

        return outputPopulations;
    }

    @Override
    public void checkConsistency(Population[] inputPopulations) throws OptimizationException {
        super.checkConsistency(inputPopulations);
        
        checkAlignmentPredicate(PopulationUtils.sumSizes(inputPopulations, this.inputArity));
    }

    /**
     * Executes check for correct output population size alignment.
     * @param inputIndividuals - total count of input Individuals.
     */
    protected void checkAlignmentPredicate(int inputIndividuals){
        int totalOutput = individualsPerPopulation * this.outputArity;
        if (totalOutput % inputIndividuals != 0){
            String msg = this.getClass().getSimpleName() + ": input individuals sum has to divide output arity multiplied with individuals per population. " + inputIndividuals + " does not divide " + totalOutput + ".";
            throw new OptimizationException(msg);
        }
        this.broodSize = totalOutput / inputIndividuals;
    }

    protected int[] getResultsSizesCore(int[] parentPopulationSizes){
        int sumSizes = MathUtils.sumSizes(parentPopulationSizes, this.getInputArity());
        checkAlignmentPredicate(sumSizes);
        int totalOutput = sumSizes * this.broodSize;
        // set appropriate individuals per population and call super which will create output array
        this.individualsPerPopulation = totalOutput / this.outputArity;
        return super.getResultsSizesCore(parentPopulationSizes);
    }

    public void setRandomGenerator(RandomGenerator randomGenerator) {
    }

    public void resetGenerationCount() {
    }

    public void nextGeneration() {
    }

    public void setGeneration(int currentGeneration) {
    }
}
