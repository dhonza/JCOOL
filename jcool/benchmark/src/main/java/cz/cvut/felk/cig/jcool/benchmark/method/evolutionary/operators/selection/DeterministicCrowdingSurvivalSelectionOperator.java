package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.DistanceFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.distance.EuclideanDistanceFunction;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.ui.DynamicDropDown;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 30.3.2011
 * Time: 23:16
 * Survival selection operator suitable for deterministic crowding method.
 * It takes 4 populations at the input, eliminates half of individuals and returns one population with doubled length of the input.
 * That means that individualsPerPopulation has to be even.
 * First two populations are children the other two are parents.
 */
@Component(name = "Deterministic crowding survival selection operator", description = "Survival selection operator which reduces 4 individuals (2 children and 2 parents) into 2 survivors in only one output Population")
public class DeterministicCrowdingSurvivalSelectionOperator extends AbstractSelectionOperator {

    @Property(name = "distance function")
    @DynamicDropDown(key = "distanceFunctions", type = DistanceFunction.class, label = DynamicDropDown.Label.NAME)
    protected DistanceFunction distanceFunction = new EuclideanDistanceFunction();

    public DeterministicCrowdingSurvivalSelectionOperator(){
        this.setInputArity(4);
        this.setOutputArity(1);
        this.setIndividualsPerPopulation(2);
    }

    public Population[] select(Population[] populations) {
        this.checkConsistency(populations);

        Individual[] children1 = populations[0].getIndividuals();
        Individual[] children2 = populations[1].getIndividuals();
        Individual[] parents1 = populations[2].getIndividuals();
        Individual[] parents2 = populations[3].getIndividuals();
        Individual[] outputIndividuals = new Individual[this.getIndividualsPerPopulation()];
        Individual winner1;
        Individual winner2;
        double distance1;
        double distance2;
        int length = this.getIndividualsPerPopulation() / 2;
        for (int i = 0; i < length; i++){
            distance1 = this.distanceFunction.distance(parents1[i], children1[i]) + this.distanceFunction.distance(parents2[i], children2[i]);
            distance2 = this.distanceFunction.distance(parents1[i], children2[i]) + this.distanceFunction.distance(parents2[i], children1[i]);
            winner1 = parents1[i];
            winner2 = parents2[i];
            if (distance1 <= distance2) {
                if (children1[i].getFitness() > parents1[i].getFitness()){
                    winner1 = children1[i];
                }
                if (children2[i].getFitness() > parents2[i].getFitness()){
                    winner2 = children2[i];
                }
            } else {
                if (children2[i].getFitness() > parents1[i].getFitness()){
                    winner1 = children2[i];
                }
                if (children1[i].getFitness() > parents2[i].getFitness()){
                    winner2 = children1[i];
                }
            }
            // linearize the winners into output array
            outputIndividuals[i*2] = winner1;
            outputIndividuals[i*2 + 1] = winner2;
        }

        return new Population[]{this.populationFactory.createPopulation(outputIndividuals)};
    }

    @Override
    public void checkConsistency(Population[] inputPopulations) throws OptimizationException {
        super.checkConsistency(inputPopulations);

        if (this.distanceFunction == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": distance function has not been set");
        }
        if (this.inputArity != 4){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input arity has to be four, the set value is " + this.inputArity);
        }
        if (this.outputArity != 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": output arity has to be one, the set value is " + this.outputArity);
        }
        if (this.individualsPerPopulation % 2 != 0){
            throw new OptimizationException(this.getClass().getSimpleName() + ": individualsPerPopulation has to be even, but the given value " + this.individualsPerPopulation + " is not");
        }
        // we have special demand on input populations length
        if (inputPopulations.length < 4){
            throw new OptimizationException(this.getClass().getSimpleName() + ": count of populations at the input cannot be smaller than 4, but the count of populations at the input is " + inputPopulations.length);
        }
        // we have a special demand on length of input populations
        for (int i = 0; i < 4 ; i++){
            if (inputPopulations[i].getIndividuals().length < (this.getIndividualsPerPopulation() / 2) ){
                throw new OptimizationException(this.getClass().getSimpleName() + ": length of input population cannot be smaller than half of individualsPerPopulation, but the length of " + i + "-th population is " + inputPopulations[i].getIndividuals().length + " and the minimal required number is  " + (this.getIndividualsPerPopulation()/2) );
            }
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

    public DistanceFunction getDistanceFunction() {
        return distanceFunction;
    }

    public void setDistanceFunction(DistanceFunction distanceFunction) {
        this.distanceFunction = distanceFunction;
    }
}
