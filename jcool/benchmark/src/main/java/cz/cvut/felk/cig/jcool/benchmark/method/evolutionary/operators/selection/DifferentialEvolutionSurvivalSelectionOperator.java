package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.core.RandomGenerator;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 11.4.2011
 * Time: 0:13
 * Survival selection operator for differential evolution algorithm. From two individuals at the same index in input arrays picks up the fittest.
 * population[0] are children, population[1] are parents.
 */
@Component(name = "Differential evolution survival selection operator", description = "From two individuals at the same index in input arrays picks up the fittest. Index 0 is for children, index 1 is for parents.")
public class DifferentialEvolutionSurvivalSelectionOperator extends AbstractSelectionOperator{

    public DifferentialEvolutionSurvivalSelectionOperator(){
        this.inputArity = 2;
        this.outputArity = 1;
    }

    public Population[] select(Population[] populations) {
        this.checkConsistency(populations);

        Individual[] inputChildren = populations[0].getIndividuals();
        Individual[] inputParents = populations[1].getIndividuals();

        Individual[] outputIndividuals = new Individual[this.individualsPerPopulation];
        for (int i = 0; i < this.individualsPerPopulation; i++){
            outputIndividuals[i] = inputChildren[i].getFitness() > inputParents[i].getFitness() ? inputChildren[i] : inputParents[i];
        }

        return new Population[]{this.populationFactory.createPopulation(outputIndividuals)};
    }

    @Override
    public void checkConsistency(Population[] inputPopulations) throws OptimizationException {
        super.checkConsistency(inputPopulations);

        if (this.inputArity != 2){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input arity has to be 2, but selected is " + this.inputArity);
        }
        if (this.outputArity != 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": output arity has to be 1, but selected is " + this.outputArity);
        }
        if (inputPopulations[0].getIndividuals().length < this.individualsPerPopulation){
            throw new OptimizationException(this.getClass().getSimpleName() + ": length of input populations has to be at least of size of individuals per population. Given length is " + this.individualsPerPopulation + ", but required individuals per population is " + this.individualsPerPopulation);
        }
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
