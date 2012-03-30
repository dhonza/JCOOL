package cz.cvut.felk.cig.jcool.benchmark.util;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 18.2.2011
 * Time: 17:04
 * Provides common operations executed on Individuals.
 */
public class IndividualUtils {

    /**
     * Makes deep copy of given array of Individuals. Copies only non-null Individuals.
     * @param sourceIndividuals - Individuals to be deep copied.
     * @param birthday - birthdays if newly created individuals.
     * @return array of deep copies of source individuals.
     */
    public static Individual[] makeCopy(Individual[] sourceIndividuals, int birthday){
        if (sourceIndividuals == null){
            return null;
        }
        Individual[] copies = new Individual[sourceIndividuals.length];
        for (int i = 0; i < sourceIndividuals.length; i++){
            if (sourceIndividuals[i] != null){ // in the future, there might be null individuals in the Population
                copies[i] = sourceIndividuals[i].copy(birthday);
            }
        }
        return copies;
    }

    /**
     * Returns the best individual from all given populations.
     * The best means with the lowest function value.
     * @param populations - populations in which to find the best individual
     * @return the best individual according to lowest function value or null, if population are empty. 
     */
    public static Individual getBestIndividual(Population[] populations){
        Individual globalBest = null;
        Individual currentBest;

        if (populations != null){
            for (Population population : populations){
                currentBest = population.getBestIndividual();
                // initialize globalBest if null, else compare non-null currentBest
                if (globalBest == null){
                    globalBest = currentBest;
                } else if (currentBest != null && currentBest.getValue() < globalBest.getValue()){
                    globalBest = currentBest;
                }
            }
        }
        return globalBest;
    }

    /**
     * Extracts all individuals from given populations array.
     * @param populations - array of Population from which the individuals should be extracted.
     * @return all individuals contained in given populations.
     */
    public static Individual[] getAllIndividuals(Population[] populations){
        if (populations == null){
            throw new OptimizationException("input Population[] cannot be null");
        }
        return getIndividuals(populations, populations.length);
    }

    /**
     * Extract individuals from given populations array restricted with maxIndexExclusive index in populations array.
     * @param populations - array of Population from which the individuals should be extracted.
     * @param maxIndexExclusive - index in populations array from which (including this index) the populations should be ignored. Can be bigger than actual length of input populations array.
     * @return individuals contained in given populations array limited with maxIndexExclusive in populations array.
     */
    public static Individual[] getIndividuals(Population[] populations, int maxIndexExclusive){
        if (populations == null){
            throw new OptimizationException("input Population[] cannot be null");
        }
        if (maxIndexExclusive < 0){
            throw new OptimizationException("maxIndexExclusive has to be non-negative integer");
        }
        if (populations.length < maxIndexExclusive){
            maxIndexExclusive = populations.length;
        }

        if (populations.length > 0){
            int size = 0;
            for (int i = 0; i < maxIndexExclusive; i++){
                if (populations[i].getIndividuals() != null){
                    size += populations[i].getIndividuals().length;
                }
            }
            if (size > 0){
                int freeIndex = 0;
                Individual[] outputIndividuals;
                // first Population can be copied using java.util.Arrays
                if (populations[0].getIndividuals() != null){
                    outputIndividuals = Arrays.copyOf(populations[0].getIndividuals(), size);
                    freeIndex = populations[0].getIndividuals().length;
                } else {
                    outputIndividuals = new Individual[size];
                }
                // the rest has to be copied manually
                for (int i = 1; i < maxIndexExclusive; i++){ // skip population without Individuals
                    if (populations[i].getIndividuals() == null){
                        continue;
                    }
                    for (Individual individual : populations[i].getIndividuals()){
                        outputIndividuals[freeIndex++] = individual;
                    }
                }
                return outputIndividuals;
            }
        }
        return new Individual[0];
    }
}
