package cz.cvut.felk.cig.jcool.benchmark.util;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.population.SimplePopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations.SimplePopulation;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 20.2.2011
 * Time: 12:05
 * Tests for PopulationUtils.
 */
public class PopulationUtilsTest extends TestCase {
    @Test
    // tests concatenation and ordering of given Populations
    public void testConcatenate1() throws Exception {
        SimpleIndividual[] ind1 = new SimpleIndividual[] {new SimpleIndividual(1, 1.0, null), new SimpleIndividual(2, 3.0, null), new SimpleIndividual(5, 3.0, null)};
        SimpleIndividual[] ind2 = new SimpleIndividual[] {new SimpleIndividual(10, 0.0, null), new SimpleIndividual(2, 2.0, null)};

        Individual[] indOut = PopulationUtils.concatenate(new Population[]{new SimplePopulation(ind1), new SimplePopulation(ind2), new SimplePopulation(ind1)}, new SimplePopulationFactory()).getIndividuals();

        assertTrue(indOut[0] == ind1[0]);
        assertTrue(indOut[1] == ind1[1]);
        assertTrue(indOut[2] == ind1[2]);
        assertTrue(indOut[3] == ind2[0]);
        assertTrue(indOut[4] == ind2[1]);
        assertTrue(indOut[5] == ind1[0]);
        assertTrue(indOut[6] == ind1[1]);
        assertTrue(indOut[7] == ind1[2]);
        assertTrue(indOut.length == ind1.length + ind1.length + ind2.length);
    }

    @Test
    // tests concatenation and ordering of given Populations, limited by maxIndexExclusive
    public void testConcatenate2() throws Exception {
        SimpleIndividual[] ind1 = new SimpleIndividual[] {new SimpleIndividual(1, 1.0, null), new SimpleIndividual(2, 3.0, null), new SimpleIndividual(5, 3.0, null)};
        SimpleIndividual[] ind2 = new SimpleIndividual[] {new SimpleIndividual(10, 0.0, null), new SimpleIndividual(2, 2.0, null)};

        Individual[] indOut = PopulationUtils.concatenate(new Population[]{new SimplePopulation(ind1), new SimplePopulation(ind2), new SimplePopulation(ind1)}, new SimplePopulationFactory(), 2).getIndividuals();

        assertTrue(indOut[0] == ind1[0]);
        assertTrue(indOut[1] == ind1[1]);
        assertTrue(indOut[2] == ind1[2]);
        assertTrue(indOut[3] == ind2[0]);
        assertTrue(indOut[4] == ind2[1]);
        assertTrue(indOut.length == ind1.length + ind2.length);
    }

    @Test
    // tests concatenation of single Population - creation of new array of Individual
    public void testConcatenate3() throws Exception {
        SimpleIndividual[] ind1 = new SimpleIndividual[] {new SimpleIndividual(1, 1.0, null), new SimpleIndividual(2, 3.0, null), new SimpleIndividual(5, 3.0, null)};
        SimplePopulation pop1 = new SimplePopulation(ind1);

        Individual[] indOut = PopulationUtils.concatenate(new Population[]{pop1}, new SimplePopulationFactory()).getIndividuals();

        assertTrue(indOut[0] == ind1[0]);
        assertTrue(indOut[1] == ind1[1]);
        assertTrue(indOut[2] == ind1[2]);
        assertNotSame(pop1.getIndividuals(), indOut);
        assertNotSame(ind1, indOut);
        assertTrue(indOut.length == ind1.length);
    }

    @Test
    // tests concatenation of single EMPTY Population
    public void testConcatenate4() throws Exception {
        SimplePopulation pop1 = new SimplePopulation();

        Individual[] indOut = PopulationUtils.concatenate(new Population[]{pop1}, new SimplePopulationFactory()).getIndividuals();

        assertTrue(pop1.getIndividuals() == indOut || indOut.length == 0);
    }

    @Test
    // tests concatenation of EMPTY Population and filled one.
    public void testConcatenate5() throws Exception {
        SimpleIndividual[] ind1 = new SimpleIndividual[] {new SimpleIndividual(1, 1.0, null), new SimpleIndividual(2, 3.0, null), new SimpleIndividual(5, 3.0, null)};
        SimplePopulation pop1 = new SimplePopulation(ind1);

        Individual[] indOut = PopulationUtils.concatenate(new Population[]{new SimplePopulation(), pop1}, new SimplePopulationFactory()).getIndividuals();

        assertTrue(indOut[0] == ind1[0]);
        assertTrue(indOut[1] == ind1[1]);
        assertTrue(indOut[2] == ind1[2]);
        assertNotSame(pop1.getIndividuals(), indOut);
        assertNotSame(ind1, indOut);
        assertTrue(indOut.length == ind1.length);
    }

    @Test
    // tests appending at the end of the population
    public void testAppendPopulation1(){
        Population[] inputPopulations = new Population[]{new SimplePopulation(), new SimplePopulation(), new SimplePopulation() };
        Population populationToAppend = new SimplePopulation();
        Population[] results = PopulationUtils.appendPopulation(inputPopulations, populationToAppend, true);
        assertEquals(4, results.length);
        assertSame(inputPopulations[0], results[0]);
        assertSame(inputPopulations[1], results[1]);
        assertSame(inputPopulations[2], results[2]);
        assertSame(populationToAppend, results[3]);
    }

    @Test
    // tests appending at the beginning of the population
    public void testAppendPopulation2(){
        Population[] inputPopulations = new Population[]{new SimplePopulation(), new SimplePopulation(), new SimplePopulation() };
        Population populationToAppend = new SimplePopulation();
        Population[] results = PopulationUtils.appendPopulation(inputPopulations, populationToAppend, false);
        assertEquals(4, results.length);
        assertSame(populationToAppend, results[0]);
        assertSame(inputPopulations[0], results[1]);
        assertSame(inputPopulations[1], results[2]);
        assertSame(inputPopulations[2], results[3]);
    }

    @Test
    // tests appending at the end of the empty populations
    public void testAppendPopulation3(){
        Population populationToAppend = new SimplePopulation();
        Population[] results = PopulationUtils.appendPopulation(null, populationToAppend, true);
        assertEquals(1, results.length);
        assertSame(populationToAppend, results[0]);
    }

    @Test
    // tests appending at the beginning of the empty populations
    public void testAppendPopulation4(){
        Population populationToAppend = new SimplePopulation();
        Population[] results = PopulationUtils.appendPopulation(null, populationToAppend, false);
        assertEquals(1, results.length);
        assertSame(populationToAppend, results[0]);
    }

    @Test
    // tests appending empty population at the beginning of the empty populations
    public void testAppendPopulation5(){
        Population[] results = PopulationUtils.appendPopulation(null, null, true);
        assertEquals(null, results);
    }
}
