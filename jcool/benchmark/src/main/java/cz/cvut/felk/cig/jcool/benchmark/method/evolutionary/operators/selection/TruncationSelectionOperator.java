package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.util.MathUtils;
import cz.cvut.felk.cig.jcool.benchmark.util.PopulationUtils;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.2.2011
 * Time: 15:16:17
 * Selection operator that selects n best Individuals. Thus cannot select more Individuals than provided.
 */
@Component(name="Truncation selection operator", description = "Selects n best Individuals. Thus cannot select more Individuals than provided.")
public class TruncationSelectionOperator extends AbstractSelectionOperator {

    /**
     * Selects n fittest Individuals from all given Populations
     * @param populations - array of Populations from which the selection is being performed.
     * @return Population containing n fittest Individuals.
     */
    public Population[] select(Population[] populations) {
        checkConsistency(populations);

        List<Individual> allIndividuals = Arrays.asList(this.concatenateIndividualsFromPopulations(populations, this.inputArity));
        // sorting according to fitness in reversed order -> fittest first
        Collections.sort(allIndividuals, Collections.reverseOrder());

        // prepare result and map selected Individuals into resulting arrays
        int resultSize = this.individualsPerPopulation * this.outputArity;
        List<Individual> resultingIndividuals = allIndividuals.subList(0, resultSize);

        // making resulting array
        Population[] outputPopulations = this.createOutputPopulations();
        Individual[][] outputIndividuals = this.createOutputIndividuals();
        this.assignIndividualsToPopulations(outputIndividuals, outputPopulations);
        this.mapByColumns(resultingIndividuals, outputIndividuals);

        return outputPopulations;
    }

    @Override
    public void checkConsistency(Population[] populations) throws OptimizationException{
        super.checkConsistency(populations);
        
        checkMinIndividualsPredicate(PopulationUtils.sumSizes(populations, this.inputArity));
    }

    /**
     * Checks whether input Populations contain at least that much Individuals as required on output.
     * @param sumSizes - total count of Individuals to process.
     */
    protected void checkMinIndividualsPredicate(int sumSizes){
        if (sumSizes < this.outputArity * this.individualsPerPopulation){
            String msg = this.getClass().getSimpleName() + ": sum of individuals in input populations is smaller than number of output populations multiplied with individuals per output populations, i.e " + sumSizes + " < " + this.outputArity + " * " + this.individualsPerPopulation;
            throw new OptimizationException(msg);
        }
    }

    /**
     * Returns minimum from selected individualsPerPopulation and sizes of all populations.
     * @param parentPopulationSizes - sizes of parent populations.
     * @return minimum from selected individualsPerPopulation and sizes of all populations.
     */
    protected int[] getResultsSizesCore(int[] parentPopulationSizes) {
        checkMinIndividualsPredicate(MathUtils.sumSizes(parentPopulationSizes, this.inputArity));
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
