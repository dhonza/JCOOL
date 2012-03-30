package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.population.SimplePopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations.SimplePopulation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.util.SimpleRandomGenerator;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 22.2.2011
 * Time: 14:55
 * Tests correct output sizes and correct composition (mapping)
 */
public class UniformDeterministicSelectionOperatorTest extends TestCase {

    protected UniformDeterministicSelectionOperator operator;

    @Before
    protected void setUp(){
        this.operator = new UniformDeterministicSelectionOperator();
        this.operator.setRandomGenerator(new SimpleRandomGenerator());
        this.operator.setPopulationFactory(new SimplePopulationFactory());
    }

    @Test
    public void testSelect1() throws Exception {
        SimplePopulation popIn1 = new SimplePopulation();
        SimplePopulation popIn2 = new SimplePopulation();
        Individual[] allIndividuals = new SimpleIndividual[10];
        for (int i = 0; i < allIndividuals.length; i++){
            allIndividuals[i] = new SimpleIndividual(0, i, new SimplePhenotypeRepresentation());
        }
        Individual[] ind1 = new Individual[]{allIndividuals[0], allIndividuals[1], allIndividuals[2]};
        popIn1.setIndividuals(ind1);
        Individual[] ind2 = new Individual[]{allIndividuals[3], allIndividuals[4]};
        popIn2.setIndividuals(ind2);

        Population[] popsIn = new Population[]{popIn1, popIn2};

        this.operator.setInputArity(1);
        this.operator.setIndividualsPerPopulation(6);
        this.operator.setOutputArity(1);

        Population[] popsOut = operator.select(popsIn);

        assertTrue(popsOut != null);
        assertTrue(popsOut.length == 1);
        Individual[] indOut1 = popsOut[0].getIndividuals();
        assertTrue(indOut1.length == 6);
        assertSame(allIndividuals[0], indOut1[0]);
        assertSame(allIndividuals[0], indOut1[1]);
        assertSame(allIndividuals[1], indOut1[2]);
        assertSame(allIndividuals[1], indOut1[3]);
        assertSame(allIndividuals[2], indOut1[4]);
        assertSame(allIndividuals[2], indOut1[5]);
    }

    @Test
    public void testSelect2() throws Exception {
        SimplePopulation popIn1 = new SimplePopulation();
        SimplePopulation popIn2 = new SimplePopulation();
        SimplePopulation popIn3 = new SimplePopulation();
        Individual[] allIndividuals = new SimpleIndividual[10];
        for (int i = 0; i < allIndividuals.length; i++){
            allIndividuals[i] = new SimpleIndividual(0, i, new SimplePhenotypeRepresentation());
        }
        Individual[] ind1 = new Individual[]{allIndividuals[0], allIndividuals[1], allIndividuals[2], allIndividuals[3]};
        popIn1.setIndividuals(ind1);
        Individual[] ind2 = new Individual[]{allIndividuals[4], allIndividuals[5]};
        popIn2.setIndividuals(ind2);
        Individual[] ind3 = new Individual[]{allIndividuals[6], allIndividuals[7]};
        popIn3.setIndividuals(ind3);

        Population[] popsIn = new Population[]{popIn1, popIn2, popIn3};

        this.operator.setInputArity(2);
        this.operator.setIndividualsPerPopulation(9);
        this.operator.setOutputArity(2);

        Population[] popsOut = operator.select(popsIn);

        assertTrue(popsOut != null);
        assertTrue(popsOut.length == 2);
        Individual[] indOut1 = popsOut[0].getIndividuals();
        Individual[] indOut2 = popsOut[1].getIndividuals();
        assertTrue(indOut1.length == 9);
        assertSame(allIndividuals[0], indOut1[0]);
        assertSame(allIndividuals[1], indOut2[1]);
        assertSame(allIndividuals[1], indOut2[2]);
        assertSame(allIndividuals[2], indOut1[4]);
        assertSame(allIndividuals[3], indOut2[5]);
        assertSame(allIndividuals[4], indOut1[6]);
        assertSame(allIndividuals[5], indOut2[7]);
    }

    @Test
    // test maxIndexExclusive and arity
    public void testGetResultsSizes1() throws Exception {
        int[] sizes = new int[]{1, 4, 8, 3};
        this.operator.setInputArity(2);
        this.operator.setIndividualsPerPopulation(10);
        this.operator.setOutputArity(1);

        int[] outputs = this.operator.getResultsSizes(sizes);

        assertTrue(outputs != null);
        assertTrue(outputs.length == 1);
        assertTrue(outputs[0] == 10);
    }

    @Test
    // test correct alignment
    public void testGetResultsSizes2() throws Exception {
        int[] sizes = new int[]{1, 4, 8, 3};
        this.operator.setInputArity(3);
        this.operator.setIndividualsPerPopulation(10);
        this.operator.setOutputArity(2);

        try {
            int[] outputs = this.operator.getResultsSizes(sizes);
            throw new AssertionError(); // if get here, throw unexpected exception type
        } catch (Exception e){
            assertTrue(e instanceof OptimizationException);
        }
    }

    @Test
    // test output arity more than one
    public void testGetResultsSizes3() throws Exception {
        int[] sizes = new int[]{1, 4, 7, 3};
        this.operator.setInputArity(3);
        this.operator.setIndividualsPerPopulation(8);
        this.operator.setOutputArity(3);

        int[] outputs = this.operator.getResultsSizes(sizes);
        assertTrue(outputs != null);
        assertTrue(outputs.length == 3);
        assertTrue(outputs[0] == 8);
        assertTrue(outputs[1] == 8);
        assertTrue(outputs[2] == 8);
    }
}
