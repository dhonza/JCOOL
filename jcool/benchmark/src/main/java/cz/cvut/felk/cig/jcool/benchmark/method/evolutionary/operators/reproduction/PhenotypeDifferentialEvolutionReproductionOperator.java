package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.*;
import cz.cvut.felk.cig.jcool.benchmark.util.IndividualUtils;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 11.4.2011
 * Time: 0:10
 * Special crossover reproduction operator for phenotype differential evolution algorithm.
 */
@Component(name = "Phenotype differential evolution reproduction operator", description = "Special crossover/mutation reproduction operator that produces one offspring out of 4 parents.")
public class PhenotypeDifferentialEvolutionReproductionOperator implements ReproductionOperator {

    @Property(name = "crossover probability", description = "determines the probability with which the child will accept mutated values from its three parents")
    @Range(from = 0.0, to = 1.0)
    protected double crossoverProbability = 0.85;

    @Property(name = "mutation constant", description = "determines how much of difference of parent2 and parent3 will receive the child")
    @Range(from = 0.0, to = 2.0)
    protected double mutationConstant = 0.6;

    int generationNumber = 0;
    ObjectiveFunction function;
    PopulationFactory populationFactory;
    RandomGenerator randomGenerator;

    public Population[] reproduce(Population[] populations) {
        checkConsistency(populations);

        Individual[] parents0 = populations[0].getIndividuals();
        Individual[] parents1 = populations[1].getIndividuals();
        Individual[] parents2 = populations[2].getIndividuals();
        Individual[] parents3 = populations[3].getIndividuals();

        Individual[] children = IndividualUtils.makeCopy(parents0, this.generationNumber);

        for (int index = 0; index < children.length; index++){

            double[] values0 = parents0[index].getRepresentation().getDoubleValue();
            double[] values1 = parents1[index].getRepresentation().getDoubleValue();
            double[] values2 = parents2[index].getRepresentation().getDoubleValue();
            double[] values3 = parents3[index].getRepresentation().getDoubleValue();
            double[] valuesCh = children[index].getRepresentation().getDoubleValue();
            double parentFitness = Math.max(
                    Math.max(parents0[index].getFitness(), parents1[index].getFitness()),
                    Math.max(parents2[index].getFitness(), parents3[index].getFitness())
                    );
            children[index].setParentFitness(parentFitness);

            // creating mutation vector v = (r1 - r2)*F + r3
            int R = this.randomGenerator.nextInt(valuesCh.length); // magic constant
            for (int dimension = 0; dimension < valuesCh.length; dimension++){
                if (this.randomGenerator.nextDouble() <= this.crossoverProbability || dimension == R){
                    valuesCh[dimension] = values3[dimension] + this.mutationConstant * (values1[dimension] - values2[dimension]);
                    // if new individual not in bounds, then revert last change
                    if (!this.function.inBounds(Point.at(valuesCh))){
                        valuesCh[dimension] = values0[dimension];
                    }
                }
            }
        }

        return new Population[]{this.populationFactory.createPopulation(children)};
    }

    public void checkConsistency(Population[] populations) throws OptimizationException{
        if (this.function == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": function has not been set");
        }
        if (this.randomGenerator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": randomGenerator has not been set");
        }
        if (this.populationFactory == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": populationFactory has not been set");
        }
        if (populations == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input Population[] cannot be null");
        }
        if (populations.length < this.getInputArity()){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input Population[] has to contain at least " + this.getInputArity() + " populations");
        }
    }

    public void setFunction(ObjectiveFunction objectiveFunction) {
        this.function = objectiveFunction;
    }

    public Class<? extends Representation> getAcceptableType() {
        return PhenotypeRepresentation.class;
    }

    public int getInputArity() {
        return 4;
    }

    public int getOutputArity() {
        return 1;
    }

    public int[] getResultsSizes(int[] parentPopulationSizes) {
        if (parentPopulationSizes == null || parentPopulationSizes.length < this.getInputArity()){
            throw new OptimizationException(this.getClass().getSimpleName() + ": parent population sizes has to be non-null and at least size of " + this.getInputArity());
        }
        int retSize = Math.min(parentPopulationSizes[0], parentPopulationSizes[1]);
        return new int[]{retSize};
    }

    public void setPopulationFactory(PopulationFactory populationFactory) {
        this.populationFactory = populationFactory;
    }

    public void setRandomGenerator(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    public void resetGenerationCount() {
        this.generationNumber = 0;
    }

    public void nextGeneration() {
        this.generationNumber++;
    }

    public void setGeneration(int currentGeneration) {
        this.generationNumber = currentGeneration;
    }

    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    public double getMutationConstant() {
        return mutationConstant;
    }

    public void setMutationConstant(double mutationConstant) {
        this.mutationConstant = mutationConstant;
    }
}
