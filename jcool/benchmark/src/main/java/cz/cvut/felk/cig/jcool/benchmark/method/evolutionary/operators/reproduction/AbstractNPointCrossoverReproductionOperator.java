package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.util.IndividualUtils;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.3.2011
 * Time: 19:09
 * Common ancestor of all NPointCrossoverReproductionOperators.
 * Provides common functionality and properties.
 */
public abstract class AbstractNPointCrossoverReproductionOperator extends AbstractCrossoverReproductionOperator {

    @Property(name = "number of crossover points")
    @Range(from = 1, to = Integer.MAX_VALUE)
    protected int crossPointCount = 1;

    @Override
    public void checkConsistency(Population[] populations) throws OptimizationException{
        super.checkConsistency(populations);
        
        if (this.crossPointCount < 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": number of cross points cannot be smaller than one");
        }
    }

    public Population[] reproduce(Population[] populations) {
        this.checkConsistency(populations);

        // make copies of parents
        Individual[] firstChildren = IndividualUtils.makeCopy(populations[0].getIndividuals(), this.generationNumber);
        Individual[] secondChildren = IndividualUtils.makeCopy(populations[1].getIndividuals(), this.generationNumber);

        this.reproduceInternal(firstChildren, secondChildren);
        
        // wrapping Individuals into output array of Population
        Population firstPopulation = this.populationFactory.createPopulation(firstChildren);
        if (this.createBothChildren){
            return new Population[]{firstPopulation, this.populationFactory.createPopulation(secondChildren)};
        } else {
            return new Population[]{firstPopulation};
        }
    }

    protected abstract void reproduceInternal(Individual[] firstChildren, Individual[] secondChildren);

    public int getCrossPointCount() {
        return this.crossPointCount;
    }

    public void setCrossPointCount(int crossPointCount) {
        this.crossPointCount = crossPointCount;
    }

    /**
     * Prepares cut point flag array for determination whether to keep value of the first parent or the second.
     * @param numCrossings - number of cut points to generate
     * @param arrayLength - length of output array
     * @return array of flags describing whether the parent should keep his gene or not. (true means to keep it gene, false means to receive gene from second parent)
     */
    protected boolean[] makeCutPointFlags(int numCrossings, int arrayLength){
        int[] indexes = new int[numCrossings];
        // trivial case when numCrossings is by one smaller than dimension count
        if ((numCrossings + 1) == arrayLength){
            for (int i = 0; i < numCrossings; i++){
                indexes[i] = i + 1;
            }
        } else {
            // generating of cross points into set which is ordered and then transform it into int[].
            Set<Integer> indexSet = new TreeSet<Integer>();
            while (indexSet.size() < numCrossings){
                indexSet.add(this.randomGenerator.nextInt(1, arrayLength));
            }

            // probably the best solution without creating temporary Integer[] array from indexSet
            int idx = 0;
            for (Integer integer : indexSet){
                indexes[idx++] = integer;
            }
        }
        // we have generated cross points and now can generate boolean flag array
        boolean[] flagArray = new boolean[arrayLength];
        boolean flag = true;
        int index = 0;
        for (int i = 0; i < arrayLength; i++){
            // if at inverting position then invert and shift index
            if (index < numCrossings && indexes[index] == i){
                flag = !flag;
                index++;
            }
            flagArray[i] = flag;
        }
        return flagArray;
    }
}
