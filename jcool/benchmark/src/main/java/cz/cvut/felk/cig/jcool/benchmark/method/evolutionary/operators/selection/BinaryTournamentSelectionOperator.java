package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.util.MathUtils;
import cz.cvut.felk.cig.jcool.benchmark.util.PopulationUtils;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.core.RandomGenerator;
import org.ytoh.configurations.annotations.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 22.2.2011
 * Time: 15:48
 * Executes binary tournament selection so many times, util the output is satisfied.
 */
@Component(name = "Binary tournament selection operator", description = "executes binary tournament between randomly chosen couples. Wins Individual with higher fitness or the first one, if fitness values are equal")
public class BinaryTournamentSelectionOperator extends AbstractSelectionOperator {

    protected RandomGenerator randomGenerator;

    public Population[] select(Population[] populations) {
        checkConsistency(populations);

        Individual[] inputIndividuals = this.concatenateIndividualsFromPopulations(populations, this.inputArity);
        int resultSize = this.individualsPerPopulation * this.outputArity;
        List<Individual> resultingIndividuals = new ArrayList<Individual>(resultSize);

        // competing couple
        Individual individual1;
        Individual individual2;
        // competition between randomly chosen couples
        for (int i = 0; i < resultSize; i++){
            individual1 = inputIndividuals[this.randomGenerator.nextInt(inputIndividuals.length)];
            individual2 = inputIndividuals[this.randomGenerator.nextInt(inputIndividuals.length)];
            resultingIndividuals.add(individual1.getFitness() > individual2.getFitness() ? individual1 : individual2);
        }

        // making resulting array
        Population[] outputPopulations = this.createOutputPopulations();
        Individual[][] outputIndividuals = this.createOutputIndividuals();
        this.assignIndividualsToPopulations(outputIndividuals, outputPopulations);
        this.mapByColumns(resultingIndividuals, outputIndividuals);

        return outputPopulations;
    }

    @Override
    public void checkConsistency(Population[] inputPopulations) throws OptimizationException {
        super.checkConsistency(inputPopulations);
        
        checkMinIndividualsPredicate(PopulationUtils.sumSizes(inputPopulations, this.getInputArity()));
    }

    /**
     * Checks whether at least two Individuals are present in given populations.
     * @param sumSizes - total count of Individuals to process.
     */
    protected void checkMinIndividualsPredicate(int sumSizes){
        if (sumSizes < 2){
            throw new OptimizationException(this.getClass().getSimpleName() + ": sum of Individuals in input Populations has to be at least 2, but the value " + sumSizes + " is smaller");
        }
    }

    @Override
    protected int[] getResultsSizesCore(int[] parentPopulationSizes) {
        checkMinIndividualsPredicate(MathUtils.sumSizes(parentPopulationSizes));
        return super.getResultsSizesCore(parentPopulationSizes);
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
