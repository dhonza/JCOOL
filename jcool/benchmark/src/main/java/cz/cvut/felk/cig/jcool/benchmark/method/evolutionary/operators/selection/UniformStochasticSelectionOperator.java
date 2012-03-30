package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.core.RandomGenerator;
import org.ytoh.configurations.annotations.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 21.2.2011
 * Time: 20:12
 * Selects n = outputArity * individualsPerPopulation Individuals from given Populations depending only on random choice.
 * Can select more Individuals than provided.
 */
@Component(name = "Uniform stochastic selection operator", description = "Selects desired count of Individuals depending only on random choice")
public class UniformStochasticSelectionOperator extends AbstractSelectionOperator {

    protected RandomGenerator randomGenerator;

    public Population[] select(Population[] populations) {
        checkConsistency(populations);

        // unify populations for easy linear access to all input Individuals
        Individual[] inputIndividuals = this.concatenateIndividualsFromPopulations(populations, this.inputArity);
        int inputSize = inputIndividuals.length;
        int resultSize = this.getOutputArity() * this.getIndividualsPerPopulation();
        List<Individual> selectedIndividuals = new ArrayList<Individual>(resultSize);
        // random selection of Individuals from input
        for (int i = 0; i < resultSize; i++){
            selectedIndividuals.add(inputIndividuals[this.randomGenerator.nextInt(inputSize)]);
        }

        // wrapping result into return values
        Population[] outputPopulations = this.createOutputPopulations();
        Individual[][] outputIndividuals = this.createOutputIndividuals();
        this.mapByColumns(selectedIndividuals, outputIndividuals);
        this.assignIndividualsToPopulations(outputIndividuals, outputPopulations);

        return outputPopulations;
    }

    @Override
    public void checkConsistency(Population[] inputPopulations) throws OptimizationException{
        super.checkConsistency(inputPopulations);
        
        if (this.randomGenerator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": randomGenerator has not been set");
        }
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
