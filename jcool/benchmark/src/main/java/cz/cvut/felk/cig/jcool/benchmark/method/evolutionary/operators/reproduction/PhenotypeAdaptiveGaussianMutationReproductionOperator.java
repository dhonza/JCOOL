package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.4.2011
 * Time: 22:15
 * Adaptive ancestor of PhenotypeGaussianReproductionOperator
 */
@Component(name = "Phenotype adaptive Gaussian mutation operator", description = "Operator adapts average step size depending on success rate")
public class PhenotypeAdaptiveGaussianMutationReproductionOperator extends PhenotypeGaussianMutationReproductionOperator {

    @Property(name = "adaptive step size", description = "fraction of which the average step size should change, i.e. stepSize = (1 +- adaptiveStepSize) * stepSize")
    @Range(from = 0.0, to = 1.0)
    protected double adaptiveStepSize = 0.1;

    @Property(name = "threshold success rate", description = "success rate that determines successful and unsuccessful reproduction compared to fraction of successful mutation to all mutations in current generation.")
    @Range(from = 0.0, to = 1.0)
    protected double thresholdRate = 0.2;

    @Property(name = "neutral area perimeter", description = "specifies distance around threshold success rate in which the step size won't be changed")
    @Range(from = 0.0, to = 1.0)
    protected double epsilon = 0.01;

    // working copies of user input and derived value
    protected double workingStepSize;
    protected double workingDeviation;

    /**
     * Cache of created Individuals kept for evaluation of success rate.
     * Due to nor fitness, nor function value of individuals is known at the birthday and must be evaluated outsize operator.
     */
    protected List<Individual[]> createdIndividuals = new ArrayList<Individual[]>();

    public PhenotypeAdaptiveGaussianMutationReproductionOperator() {
        this.setWorkingStepSize(this.stepSize);
    }

    /**
     * Adds caching of output individuals.
     * @param populations - Populations to breed.
     * @return populations of new individuals.
     */
    @Override
    public Population[] reproduce(Population[] populations) {
        Population[] outputPopulations = super.reproduce(populations);
        // do not call clear() on createdIndividuals -> reproduction can be called multiple times during one generation!!!
        for (Population population : outputPopulations){
            this.createdIndividuals.add(population.getIndividuals());
        }
        return outputPopulations;
    }

    /**
     * Adds evaluation of success rate, changes step size and flushed cache.
     */
    @Override
    public void nextGeneration() {
        super.nextGeneration();

        int successfulBreed = 0;
        int allBreed = 0;
        // proceed only if there is at least one individual, otherwise it would be DivisionByZeroException
        if (this.createdIndividuals.size() > 0 && this.createdIndividuals.get(0).length > 0){
            for (Individual[] individuals : this.createdIndividuals){
                for (Individual individual : individuals){
                    allBreed++;
                    if (individual.getFitness() > individual.getParentFitness()){
                        successfulBreed++;
                    }
                }
            }
            double successRate = (double)successfulBreed / allBreed;
            double newStepSize;
            if (successRate > this.thresholdRate + this.epsilon){
                // success too big -> increase step size
                newStepSize = this.getStepSize() * (1.0 + this.adaptiveStepSize);
                this.setWorkingStepSize(newStepSize);
            } else if (successRate < this.thresholdRate - this.epsilon){
                // success too small -> decrease step size
                newStepSize = this.getStepSize() * (1.0 - this.adaptiveStepSize);
                this.setWorkingStepSize(newStepSize);
            }
        }
        // don't forget to erase cached individuals!
        this.createdIndividuals.clear();
    }

    @Override
    public void resetGenerationCount() {
        super.resetGenerationCount();
        this.createdIndividuals.clear();
        this.setWorkingStepSize(this.stepSize);
    }

    @Override
    public void setGeneration(int currentGeneration) {
        // if someone calls setGeneration instead of nextGeneration, then perform nextGeneration
        if (this.generationNumber + 1 == currentGeneration){
            this.nextGeneration();
        } else {
            super.setGeneration(currentGeneration);
            this.createdIndividuals.clear(); // clear individuals to avoid confusion
        }
    }

    public double getAdaptiveStepSize() {
        return adaptiveStepSize;
    }

    public void setAdaptiveStepSize(double adaptiveStepSize) {
        this.adaptiveStepSize = adaptiveStepSize;
    }

    public double getThresholdRate() {
        return thresholdRate;
    }

    public void setThresholdRate(double thresholdRate) {
        this.thresholdRate = thresholdRate;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public void setStepSize(double stepSize) {
        super.setStepSize(stepSize);
        this.workingStepSize = this.stepSize;
        this.workingDeviation = this.getDeviation(this.workingStepSize);
    }

    public void setWorkingStepSize(double stepSize){
        this.workingStepSize = stepSize;
        this.workingDeviation = this.getDeviation(stepSize);
    }

    @Override
    public double getDeviation() {
        return this.workingDeviation;
    }
}
