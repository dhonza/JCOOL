package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.PopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.SelectionOperator;
import cz.cvut.felk.cig.jcool.benchmark.util.PopulationUtils;
import cz.cvut.felk.cig.jcool.core.*;

import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 21.2.2011
 * Time: 14:19
 * Ancestor of all SelectionOperator implementations.
 * Also provides some auxiliary methods for manipulation and creation of Population[] and Individual[][].
 */
public abstract class AbstractSelectionOperator implements SelectionOperator {

    protected int inputArity = 1;
    protected int outputArity = 1;
    protected int individualsPerPopulation = 1;

    protected PopulationFactory populationFactory;

    public void checkConsistency(Population[] inputPopulations) throws OptimizationException{
        if (this.populationFactory == null) {
            throw new OptimizationException(this.getClass().getSimpleName() + ": populationFactory has not been set");
        }
        if (this.inputArity < 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input arity cannot be less than one, but the value " + this.inputArity + " is");
        }
        if (this.outputArity < 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": output arity cannot be less than one, but the value " + this.outputArity + " is");
        }
        if (individualsPerPopulation < 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": individuals per population cannot be less than one, but the value " + this.individualsPerPopulation + " is");
        }
        if (inputPopulations == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input populations cannot be null");
        }
        if (inputPopulations.length < this.inputArity){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input populations length is cannot be smaller than input arity");
        }
    }

    /**
     * Simple implementation that returns demanded number of individuals and populations no matter the input arguments.
     * @param parentPopulationSizes - sizes of parent populations.
     * @return desired population sizes or throws exception if input parameter is dummy.
     */
    public int[] getResultsSizes(int[] parentPopulationSizes) {
        if (parentPopulationSizes == null || parentPopulationSizes.length < this.getInputArity()){
            throw new OptimizationException(this.getClass().getSimpleName() + ": parent population sizes has to be non-null and at least size of " + this.getInputArity());
        }
        return getResultsSizesCore(parentPopulationSizes);
    }

    /**
     * Core functionality for getResultSizes() method. Contains preparation of return parameters and additional checks executed by children.
     * @param parentPopulationSizes - sizes of parent populations.
     * @return desired population sizes or throws exception if input parameter is dummy.
     */
    protected int[] getResultsSizesCore(int[] parentPopulationSizes) {
        int[] ret = new int[this.outputArity];
        for (int i = 0; i < this.outputArity; i++){
            ret[i] = this.individualsPerPopulation;
        }
        return ret;
    }

    public void setPopulationFactory(PopulationFactory populationFactory) {
        this.populationFactory = populationFactory;
    }

    // AUXILIARY_METHODS

    /**
     * Maps arrays of Individuals to appropriate Populations by index.
     * @param individuals - Individual arrays to be scattered into populations.
     * @param populations - array of Populations to be filled.
     */
    protected void assignIndividualsToPopulations(Individual[][] individuals, Population[] populations){
        for (int i = 0; i < populations.length; i++){
            populations[i].setIndividuals(individuals[i]);
        }
    }

    /**
     * Concatenates Individuals from given Population array and throws exception if resulting Individuals are null.
     * @param populations - populations which Individuals to concatenate
     * @param maxIndexExclusive -
     * @return concatenated array of Individuals from provided
     */
    protected Individual[] concatenateIndividualsFromPopulations(Population[] populations, int maxIndexExclusive){
        // unify populations for easy linear access to all input Individuals
        Individual[] inputIndividuals = PopulationUtils.concatenate(populations, this.populationFactory, maxIndexExclusive).getIndividuals();
        if (inputIndividuals == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": there are no Individuals in given populations");
        }
        return inputIndividuals;
    }

    /**
     * Creates two-dimensional array of empty Individuals to be assigned to output populations.
     * @return array of arrays of Individuals
     */
    protected Individual[][] createOutputIndividuals(){
        Individual[][] outputIndividuals = new Individual[this.outputArity][];
        for (int i = 0; i < this.outputArity; i++){
            outputIndividuals[i] = new Individual[this.individualsPerPopulation];
        }
        return outputIndividuals;
    }

    /**
     * Creates array of empty populations.
     * @return array of empty output populations.
     */
    protected Population[] createOutputPopulations(){
        Population[] outputPopulations = new Population[this.outputArity];
        for (int i = 0; i < this.outputArity; i++){
            outputPopulations[i] = this.populationFactory.createPopulation();
        }
        return outputPopulations;
    }

    /**
     * Maps given list of Individuals into given two-dimensional array by columns.
     * @param individualList - Individuals to be mapped into grid.
     * @param targetArray - grid where Individuals should be mapped by columns.
     */
    protected void mapByColumns(List<Individual> individualList, Individual[][] targetArray){
        if (targetArray.length > 0){
            Iterator<Individual> it = individualList.iterator();
            for (int column = 0; column < targetArray[0].length; column++){
                for (int row = 0; row < targetArray.length; row++){
                    targetArray[row][column] = it.next();
                }
            }
        }
    }

    // INDIVIDUALS_PER_POPULATION
    public int getIndividualsPerPopulation(){
        return this.individualsPerPopulation;
    }

    public void setIndividualsPerPopulation(int individualsPerPopulation) {
        this.individualsPerPopulation = individualsPerPopulation;
    }

    // INPUT_ARITY
    public int getInputArity() {
        return this.inputArity;
    }

    public void setInputArity(int inputArity){
        this.inputArity = inputArity;
    }

    // OUTPUT_ARITY
    public int getOutputArity() {
        return outputArity;
    }

    public void setOutputArity(int outputArity) {
        this.outputArity = outputArity;
    }
}
