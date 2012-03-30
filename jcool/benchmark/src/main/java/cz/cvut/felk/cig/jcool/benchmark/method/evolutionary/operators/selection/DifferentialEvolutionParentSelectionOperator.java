package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.util.PopulationUtils;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.core.RandomGenerator;
import org.ytoh.configurations.annotations.Component;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 10.4.2011
 * Time: 23:23
 * Special selection operator that has fixed output arity. Input arity is flexible and allows merging of multiple populations.
 */
@Component(name = "Differential evolution parent selection operator", description = "For first individualsPerPopulation individuals picks up 3 breeding individuals and returns 4 populations - one of the individuals in given order, other 3 are selected mating partners.")
public class DifferentialEvolutionParentSelectionOperator extends AbstractSelectionOperator {

    protected RandomGenerator randomGenerator;

    public DifferentialEvolutionParentSelectionOperator(){
        this.inputArity = 1;
        this.outputArity = 4;
    }

    @Override
    public void checkConsistency(Population[] inputPopulations) throws OptimizationException {
        super.checkConsistency(inputPopulations);

        if (this.outputArity != 4){
            throw new OptimizationException(this.getClass().getSimpleName() + ": output arity has to be 4, but selected is " + this.outputArity);
        }
        int sumSizes = PopulationUtils.sumSizes(inputPopulations, this.inputArity);
        if (sumSizes < 4){
            throw new OptimizationException(this.getClass().getSimpleName() + ": minimal number of input individuals is 4, but the given array contains only " + sumSizes);
        }
        if (sumSizes < this.individualsPerPopulation){
            throw new OptimizationException(this.getClass().getSimpleName() + ": sum of individuals in processed populations is smaller than demanded output length. Sum of individuals is " + sumSizes + " but required is " + this.individualsPerPopulation);
        }
    }

    public Population[] select(Population[] populations) {
        checkConsistency(populations);

        Individual[] inputIndividuals = this.concatenateIndividualsFromPopulations(populations, this.inputArity);
        if (inputIndividuals.length > this.individualsPerPopulation){
            inputIndividuals = Arrays.copyOf(inputIndividuals, this.individualsPerPopulation);
        }

        Individual[][] outputIndividuals = this.makeParentQuartets(inputIndividuals);

        Population[] outputPopulations = this.createOutputPopulations();
        this.assignIndividualsToPopulations(outputIndividuals, outputPopulations);

        return outputPopulations;
    }

    /**
     * Expands inputIndividuals into four arrays where first array is inputIndividuals and other three are triplets of randomly chosen individuals distinct from neighboring individual in first array and from each other as well.
     * @param inputIndividuals - array of input individuals to which we need to randomly choose another 3 individuals.
     * @return array of permutations to every input individual.
     */
    protected Individual[][] makeParentQuartets(Individual[] inputIndividuals){
        Individual[][] outputIndividuals = this.createOutputIndividuals();
        outputIndividuals[0] = inputIndividuals;

        // assign other parents
        for (int i = 0; i < this.individualsPerPopulation; i++){
            int[] indexes = this.getOthersIndexes(i);
            outputIndividuals[1][i] = inputIndividuals[indexes[0]];
            outputIndividuals[2][i] = inputIndividuals[indexes[1]];
            outputIndividuals[3][i] = inputIndividuals[indexes[2]];
        }

        return outputIndividuals;
    }

    /**
     * Returns 3 indexes different from each other and from usedIndex.
     * @param usedIndex - currently processed index.
     * @return indexes for given index.
     */
    protected int[] getOthersIndexes(int usedIndex){
        int[] indexes = new int[]{-1, -1, -1};

        int current = 0;
        while (current < 3) {
            int index = this.randomGenerator.nextInt(this.individualsPerPopulation);
            if ((index != usedIndex) && (index < this.individualsPerPopulation) && (index != indexes[0]) && (index != indexes[1]) && (index != indexes[2])) {
                indexes[current] = index;
                current++;
            }
        }
        return indexes;
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
