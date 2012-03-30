package cz.cvut.felk.cig.jcool.benchmark.util;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.PopulationFactory;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 19.2.2011
 * Time: 17:24
 * Provides operation sometimes required for Population manipulations.
 */
public class PopulationUtils {

    public static Population concatenate(Population[] populations, PopulationFactory populationFactory){
        if (populations == null){
            throw new OptimizationException("input Population[] cannot be null");
        }
        return concatenate(populations, populationFactory, populations.length);
    }

    /**
     * Concatenates given Populations into one resulting Population. Concatenated Populations are restricted by maxIndexExclusive.
     * @param populations - Populations which Individuals to be concatenated.
     * @param populationFactory - populationFactory responsible for creation of resulting Population.
     * @param maxIndexExclusive - restriction parameter for concatenation. If bigger than array length, then set to array length.
     * @return Population which Individuals are created by all intended Individuals from given populations.
     */
    public static Population concatenate(Population[] populations, PopulationFactory populationFactory, int maxIndexExclusive){
        if (populations == null){
            throw new OptimizationException("input Population[] cannot be null");
        }
        if (populationFactory == null){
            throw new OptimizationException("input PopulationFactory cannot be null");
        }
        if (populations.length < maxIndexExclusive){
            maxIndexExclusive = populations.length;
        }

        return populationFactory.createPopulation(IndividualUtils.getIndividuals(populations, maxIndexExclusive));
    }

    /**
     * Appends populationToAppend at the end or the beginning of the given populations array.
     * @param populations - populations array to which the population should be append.
     * @param populationToAppend - population to be append.
     * @param appendAtTheEnd - true, if population should be append at the end of array, false if at the beginning.
     * @return array of populations with appended given population.
     */
    public static Population[] appendPopulation(Population[] populations, Population populationToAppend, boolean appendAtTheEnd){
        Population[] result = null;
        if (populations != null){ // if there are populations to which to append
            if (populationToAppend != null){
                result = new Population[populations.length + 1];
                int startIndex = 0;
                if (appendAtTheEnd){
                    startIndex = 0;
                    result[result.length - 1] = populationToAppend;
                } else {
                    startIndex = 1;
                    result[0] = populationToAppend;
                }
                for (Population population : populations){
                    result[startIndex++] = population;
                }
            } else { // return original populations
                result = populations;
            }
        } else if (populationToAppend != null) { // return new array just of given populationToAppend
            result = new Population[]{populationToAppend};
        }
        return result;
    }

    /**
     * Returns sum of Individuals in all given Populations.
     * @param populations - populations which Individual sizes to count.
     * @return sum of Individuals in all given Populations.
     */
    public static int sumSizes(Population[] populations){
        if (populations == null){
            throw new OptimizationException("input populations cannot be null");
        }
        return sumSizes(populations, populations.length);
    }

    /**
     * Returns sum of Individuals in all given Populations.
     * @param populations - populations which Individual sizes to count.
     * @param maxIndexExclusive - index of first Population which size won't be included in resulting sum.
     * @return sum of Individuals in all given Populations.
     */
    public static int sumSizes(Population[] populations, int maxIndexExclusive){
        if (populations == null){
            throw new OptimizationException("input populations cannot be null");
        }
        if (populations.length < maxIndexExclusive){
            maxIndexExclusive = populations.length;
        }
        int sum = 0;
        for (int i = 0; i < maxIndexExclusive; i++){
            if (populations[i].getIndividuals() != null){
                sum += populations[i].getIndividuals().length;
            }
        }
        return sum;
    }
}
