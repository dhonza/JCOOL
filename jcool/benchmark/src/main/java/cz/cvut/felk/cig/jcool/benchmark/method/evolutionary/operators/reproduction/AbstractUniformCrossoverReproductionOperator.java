package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.util.IndividualUtils;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.3.2011
 * Time: 17:59
 * Common ancestor of genotype and phenotype uniform crossover reproduction operator.
 * Children only have to implement reproduceInternal and getAcceptableType methods.
 */
public abstract class AbstractUniformCrossoverReproductionOperator extends AbstractCrossoverReproductionOperator {
    
    @Property(name = "crossover probability", description = "determines the probability of value switch")
    @Range(from = 0.0, to = 1.0)
    protected double crossoverProbability = 0.5;

    @Override
    public void checkConsistency(Population[] populations) throws OptimizationException{
        super.checkConsistency(populations);
        
        if (this.crossoverProbability < 0.0 || this.crossoverProbability > 1.0){
            throw new OptimizationException(this.getClass().getSimpleName() + ": crossoverProbability has to be in range <0.0, 1.0>, but the value " + this.crossoverProbability + " is not");
        }
    }

    public Population[] reproduce(Population[] populations) {
        this.checkConsistency(populations);

        // children initially begin as clones of their parents including Representation
        Individual[] firstChildren = IndividualUtils.makeCopy(populations[0].getIndividuals(), this.generationNumber);
        Individual[] secondChildren = IndividualUtils.makeCopy(populations[1].getIndividuals(), this.generationNumber);

        int retSize = Math.min(populations[0].getIndividuals().length, populations[1].getIndividuals().length);
        this.reproduceInternal(firstChildren, secondChildren, retSize);

        // wrapping Individuals into output array of Population
        Population firstPopulation = this.populationFactory.createPopulation(firstChildren);
        if (this.createBothChildren){
            return new Population[]{firstPopulation, this.populationFactory.createPopulation(secondChildren)};
        } else {
            return new Population[]{firstPopulation};
        }
    }

    /**
     * Performs core crossover functionality over given arrays of Individuals.
     * @param firstChildren - array of first Individuals.
     * @param secondChildren - array of second Individuals.
     * @param retSize - length of returned children Population(s)
     */
    protected abstract void reproduceInternal(Individual[] firstChildren, Individual[] secondChildren, int retSize);

    public double getCrossoverProbability() {
        return this.crossoverProbability;
    }

    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }
}
