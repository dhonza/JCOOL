package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.core.RandomGenerator;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 23.2.2011
 * Time: 12:16
 * Abstract ancestor for all proportionate selection operators. Defines two methods that descendants have to implement.
 */
public abstract class AbstractProportionateSelectionOperator extends AbstractSelectionOperator{

    protected RandomGenerator randomGenerator;

    public Population[] select(Population[] populations) {
        checkConsistency(populations);

        Individual[] inputIndividuals = this.concatenateIndividualsFromPopulations(populations, this.getInputArity());
        double[] accumulatedProbabilities = this.makeAccumulatedProbabilities(inputIndividuals);
        List<Individual> resultingIndividuals = this.selectWinners(inputIndividuals, accumulatedProbabilities);

        // making resulting array
        Population[] outputPopulations = this.createOutputPopulations();
        Individual[][] outputIndividuals = this.createOutputIndividuals();
        this.assignIndividualsToPopulations(outputIndividuals, outputPopulations);
        this.mapByColumns(resultingIndividuals, outputIndividuals);

        return outputPopulations;
    }

    /**
     * Returns array of accumulated probabilities for given Individuals. Individuals and probabilities are bind through same index in both arrays.
     * I.e. [<0.0, 0.1>, (0.1, 0.12>, ..., (0.95, 1.0>]. Parameter by which the probabilities are counted depends on subclass implementation.
     * @param inputIndividuals - Individual for which to create accumulated probabilities.
     * @return array of accumulated probabilities.
     */
    abstract protected double[] makeAccumulatedProbabilities(Individual[] inputIndividuals);

    /**
     * Selects winning Individuals depending on random generator and provided accumulated probabilities.
     * @param inputIndividuals - Individuals to select from.
     * @param accumulatedProbabilities - appropriate accumulated probabilities array.
     * @return List<Individuals> that had been selected.
     */
    abstract protected List<Individual> selectWinners(Individual[] inputIndividuals, double[] accumulatedProbabilities);

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
