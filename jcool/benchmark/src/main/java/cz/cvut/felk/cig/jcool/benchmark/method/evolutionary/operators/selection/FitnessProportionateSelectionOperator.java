package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import org.ytoh.configurations.annotations.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 23.2.2011
 * Time: 10:49
 * Executes fitness proportionate selection over given Populations. "Roulette" is executed for each output Individual.
 * Fitness values has to be non-negative.
 */
@Component(name = "Fitness proportionate selection operator", description = "Executes fitness proportionate selection over given Populations. \"Roulette\" is executed for each output Individual")
public class FitnessProportionateSelectionOperator extends AbstractProportionateSelectionOperator{
    
    /**
     * Returns array of accumulated probabilities for given Individuals. Individuals and probabilities are bind through same index in both arrays.
     * I.e. [<0.0, 0.1>, (0.1, 0.12>, ..., (0.95, 1.0>]
     * @param inputIndividuals - Individual for which to create accumulated probabilities.
     * @return array of accumulated probabilities.
     */
    protected double[] makeAccumulatedProbabilities(Individual[] inputIndividuals){
        double[] accumulatedFitnessValues = new double[inputIndividuals.length];
        double[] accumulatedProbabilities = new double[inputIndividuals.length];
        double totalFitness = 0.0; // actual accumulated fitness value
        // make accumulated fitness values
        for (int i = 0; i < inputIndividuals.length; i++){
            if (inputIndividuals[i].getFitness() >= 0.0){
                accumulatedFitnessValues[i] = totalFitness + inputIndividuals[i].getFitness();
                totalFitness = accumulatedFitnessValues[i];
            } else {
                throw new OptimizationException(this.getClass().getSimpleName() + ": individual fitness value cannot be negative but found value of " + inputIndividuals[i].getFitness());
            }
        }
        // make accumulated probabilities
        for (int i = 0; i < inputIndividuals.length; i++){
            accumulatedProbabilities[i] = accumulatedFitnessValues[i] / totalFitness;
        }
        accumulatedProbabilities[inputIndividuals.length-1] = 1.0; // make sure that roulette selection always selects an Individual
        
        return accumulatedProbabilities;
    }

    /**
     * Trivial implementation of looking for winner - iteration.
     * @param inputIndividuals - individual to select from.
     * @param accumulatedProbabilities - appropriate accumulated probabilities array.
     * @return List<Individuals> that had been selected.
     */
    protected List<Individual> selectWinners(Individual[] inputIndividuals, double[] accumulatedProbabilities){
        int resultSize = this.outputArity * this.individualsPerPopulation;
        List<Individual> resultingIndividuals = new ArrayList<Individual>(resultSize);
        for (int i = 0; i < resultSize; i++){
            double probability = this.randomGenerator.nextRandom();
            // select first individual which accumulated probability is bigger or equal to current random number
            for (int j = 0; j < inputIndividuals.length; j++){
                if (accumulatedProbabilities[j] >= probability){
                    resultingIndividuals.add(inputIndividuals[j]);
                    break;
                }
            }
        }
        return resultingIndividuals;
    }
}
