package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.population.SimplePopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations.SimplePopulation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 14.2.2011
 * Time: 15:46:21
 * Tests for TruncationSelectionOperator.
 */
public class TruncationSelectionOperatorTest extends TestCase {
    
    protected TruncationSelectionOperator operator;

    @Before
    protected void setUp(){
        this.operator = new TruncationSelectionOperator();
        this.operator.setPopulationFactory(new SimplePopulationFactory());
    }

    @Test
    // tests result size smaller than input size
    public void testGetResultsSizes1() throws Exception {
        int[] sizes = new int[]{1, 3, 8, 14};
        operator.setInputArity(4);
        operator.setOutputArity(1);
        operator.setIndividualsPerPopulation(5);
        int[] output = operator.getResultsSizes(sizes);
        assertTrue(output != null);
        assertTrue(output.length == 1);
        assertTrue(output[0] == 5);
    }

    @Test
    // test if result size bigger than input sizes sum then exception is thrown
    public void testGetResultsSizes2() throws Exception {
        int[] sizes = new int[]{1, 3, 8, 14};
        operator.setInputArity(3);
        operator.setOutputArity(1);
        operator.setIndividualsPerPopulation(1 + 3 + 8 + 1);
        try{
            int[] output = operator.getResultsSizes(sizes);
            throw new AssertionError(); // if get here, throw unexpected exception type
        } catch (Exception e){
            assertTrue(e instanceof OptimizationException);
        }
    }

    @Test
    // test result size equal to input size
    public void testGetResultsSizes3() throws Exception {
        int[] sizes = new int[]{1, 3, 8, 14};
        operator.setInputArity(4);
        operator.setOutputArity(1);
        operator.setIndividualsPerPopulation(1 + 3 + 8 + 14);
        int[] output = operator.getResultsSizes(sizes);
        assertTrue(output != null);
        assertTrue(output.length == 1);
        assertTrue(output[0] == (1 + 3 + 8 + 14));
    }

    @Test
    // test if exception is thrown when arguments are illegal.
    public void testGetResultsSizes4() throws Exception {
        int[] sizes = new int[]{1, 3, -8, 14};
        operator.setInputArity(4);
        operator.setOutputArity(1);
        operator.setIndividualsPerPopulation(2);
        try {
            int[] output = operator.getResultsSizes(sizes);
            assertTrue(output != null);
            assertTrue(output.length == 1);
            assertTrue(output[0] == (2));
        } catch (Exception e){
            assertTrue(e instanceof OptimizationException);
        }
    }

    @Test
    // test if selects correct number and order of individuals
    public void testSelect1() throws Exception{
        SimplePopulation popIn = new SimplePopulation();
        Individual[] individuals = new SimpleIndividual[5];
        for (int i = 0; i < individuals.length; i++){
            individuals[i] = new SimpleIndividual(0, 0.0, new SimplePhenotypeRepresentation());
            individuals[i].setFitness((i-1)*(i-1)); // shifted parabola with prescription y = x^2 and bottom at position (x = -1). 
        }
        popIn.setIndividuals(individuals);
        this.operator.setInputArity(1);
        this.operator.setOutputArity(1);
        this.operator.setIndividualsPerPopulation(5);
        Population[] popsOut = this.operator.select(new SimplePopulation[]{popIn});
        Individual ind = popsOut[0].getIndividuals()[0];
        assertTrue(ind.getFitness() == 9.0);
        ind = popsOut[0].getIndividuals()[1];
        assertTrue(ind.getFitness() == 4.0);
        ind = popsOut[0].getIndividuals()[2];
        assertTrue(ind.getFitness() == 1.0);
        ind = popsOut[0].getIndividuals()[3];
        assertTrue(ind.getFitness() == 1.0);
        ind = popsOut[0].getIndividuals()[4];
        assertTrue(ind.getFitness() == 0.0);
    }

    @Test
    // test if selects correct number and order of individuals; 2 populations at input and output
    public void testSelect2() throws Exception{
        SimplePopulation popIn1 = new SimplePopulation();
        SimplePopulation popIn2 = new SimplePopulation();
        Individual[] individuals = new SimpleIndividual[5];
        for (int i = 0; i < individuals.length; i++){
            individuals[i] = new SimpleIndividual(0, 0.0, new SimplePhenotypeRepresentation());
            individuals[i].setFitness((i-1)*(i-1)); // shifted parabola with prescription y = x^2 and bottom at position (x = -1).
        }
        Individual[] ind1 = new Individual[]{individuals[0], individuals[2], individuals[4]};
        Individual[] ind2 = new Individual[]{individuals[1], individuals[3]};
        popIn1.setIndividuals(ind1);
        popIn2.setIndividuals(ind2);
        this.operator.setInputArity(2);
        this.operator.setOutputArity(2);
        this.operator.setIndividualsPerPopulation(2);
        Population[] popsOut = this.operator.select(new SimplePopulation[]{popIn1, popIn2});

        assertTrue(popsOut.length == 2);
        assertTrue(popsOut[0].getIndividuals().length == 2);
        assertTrue(popsOut[1].getIndividuals().length == 2);

        Individual indOut = popsOut[0].getIndividuals()[0];
        assertTrue(indOut.getFitness() == 9.0);
        indOut = popsOut[1].getIndividuals()[0];
        assertTrue(indOut.getFitness() == 4.0);
        indOut = popsOut[0].getIndividuals()[1];
        assertTrue(indOut.getFitness() == 1.0);
        indOut = popsOut[1].getIndividuals()[1];
        assertTrue(indOut.getFitness() == 1.0);
    }
}
