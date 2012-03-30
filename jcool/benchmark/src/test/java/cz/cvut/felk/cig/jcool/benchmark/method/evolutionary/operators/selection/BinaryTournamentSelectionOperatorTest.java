package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.population.SimplePopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations.SimplePopulation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.util.SimpleRandomGenerator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 22.2.2011
 * Time: 16:26
 * Tests minimum input Individual count and whether the fittest Individual wins.
 */
public class BinaryTournamentSelectionOperatorTest extends TestCase {

    protected BinaryTournamentSelectionOperator operator;

    @Before
    public void setUp() throws Exception {
        this.operator = new BinaryTournamentSelectionOperator();
        this.operator.setPopulationFactory(new SimplePopulationFactory());
        this.operator.setRandomGenerator(new SimpleRandomGenerator());
    }

    @Test
    // test whether majority of selected Individuals is the fittest
    public void testSelect1() throws Exception {
        Individual ind1 = new SimpleIndividual(0, 0.0, new SimplePhenotypeRepresentation());
        ind1.setFitness(10.0);
        Individual ind2 = new SimpleIndividual(0, 0.0, new SimplePhenotypeRepresentation());
        ind1.setFitness(1.0);
        Population popIn1 = new SimplePopulation(new Individual[]{ind2, ind1});
        this.operator.setInputArity(1);
        this.operator.setIndividualsPerPopulation(100);
        this.operator.setOutputArity(1);
        Population[] popsOut = this.operator.select(new Population[]{popIn1});

        assertTrue(popsOut != null);
        assertTrue(popsOut.length == 1);
        Population outPop = popsOut[0];
        assertTrue(outPop.getIndividuals().length == 100);
        int fittestCount = 0;
        for (Individual ind : outPop.getIndividuals()){
            if (ind == ind1){
                fittestCount++;
            }
        }
        assertTrue(((double)fittestCount/100) > 0.65);
    }

    @Test
    public void testGetResultsSizes1() throws Exception {
        this.operator.setInputArity(3);
        this.operator.setIndividualsPerPopulation(10);
        this.operator.setOutputArity(2);

        int sizes[] = new int[]{1, 1, 1};

        int[] outSizes = this.operator.getResultsSizes(sizes);

        assertTrue(outSizes != null);
        assertTrue(outSizes.length == 2);
        assertTrue(outSizes[0] == 10);
        assertTrue(outSizes[1] == 10);
    }

    @Test
    // test if throws exception if input Individuals are less than two
    public void testGetResultsSizes2() throws Exception {
        this.operator.setInputArity(1);
        this.operator.setIndividualsPerPopulation(10);
        this.operator.setOutputArity(2);

        int sizes[] = new int[]{1};

        try{
            int[] outSizes = this.operator.getResultsSizes(sizes);
            throw new AssertionError(); // if get here, throw unexpected exception type
        } catch (Exception e){
            assertTrue(e instanceof OptimizationException);
        }
    }
}
