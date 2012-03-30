package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import org.ytoh.configurations.annotations.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.2.2011
 * Time: 14:18
 * Stochastic Universal Sampling (SUS) selection operator that divides roulette wheel into n equal parts and generates only one random number in range <0.0, 1.0/n).
 * Generates n winners in one step. Every winner is selected from i-th part where its interval is in range < i/n, (i+1)/n ), where i {0,... n-1}
 */
@Component(name = "Stochastic Universal Sampling (SUS) selection operator", description = "Divides roulette wheel into n equal parts and generates only one random number in range <0.0, 1.0/n). Winner are selected from i-th part where its interval is in range < i/n, (i+1)/n ), where i {0,... n-1}")
public class StochasticUniversalSamplingSelectionOperator extends FitnessProportionateSelectionOperator{

    @Override
    protected List<Individual> selectWinners(Individual[] inputIndividuals, double[] accumulatedProbabilities) {
        int resultSize = this.outputArity * this.individualsPerPopulation;
        List<Individual> resultingIndividuals = new ArrayList<Individual>(resultSize);
        double stepSize = 1.0 / resultSize;
        double fraction = this.randomGenerator.nextDouble(0.0, stepSize);
        int probabilitiesIndex = 0;
        for (int currentStep = 0; currentStep < resultSize; currentStep++){
            double currentProbability = stepSize * currentStep + fraction;
            // select first individual which accumulated probability is bigger or equal to currentProbability
            while (true){
                if (accumulatedProbabilities[probabilitiesIndex] >= currentProbability){
                    resultingIndividuals.add(inputIndividuals[probabilitiesIndex]);
                    break;
                } else {
                    probabilitiesIndex++; // increase only if not selected - to prevent bad behaviour when Individuals probability overlaps more than one interval
                }
            }
        }
        return resultingIndividuals;
    }
}
