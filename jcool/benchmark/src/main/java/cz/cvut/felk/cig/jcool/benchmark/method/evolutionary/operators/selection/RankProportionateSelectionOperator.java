package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import org.ytoh.configurations.annotations.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 23.2.2011
 * Time: 15:48
 * Ranking selection operator. Best and Worst probabilities are defined as DeJong's work.
 */
@Component(name = "Rank proportionate selection operator", description = "Ranking selection operator. Best and Worst probabilities are defined as DeJong's work")
public class RankProportionateSelectionOperator extends AbstractProportionateSelectionOperator {
    @Override
    protected double[] makeAccumulatedProbabilities(Individual[] inputIndividuals) {
        // sort according to fitness
        Arrays.sort(inputIndividuals, Collections.reverseOrder());
        double worstProbability = 1.0 / (inputIndividuals.length * inputIndividuals.length);
        double bestProbability = (2.0 / inputIndividuals.length) - worstProbability;
        double[] accumulatedProbabilities = new double[inputIndividuals.length];
        double accumulatedProbability = 0.0;
        // make accumulated probabilities
        for (int i = 0; i < inputIndividuals.length; i++){
            accumulatedProbability += bestProbability - ( (bestProbability - worstProbability) * ( (double)i / (inputIndividuals.length - 1) ) );
            accumulatedProbabilities[i] = accumulatedProbability;
        }
        accumulatedProbabilities[inputIndividuals.length-1] = 1.0; // make sure that roulette selection always selects an Individual

        return accumulatedProbabilities;
    }

    @Override
    protected List<Individual> selectWinners(Individual[] inputIndividuals, double[] accumulatedProbabilities) {
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
