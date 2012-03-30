package cz.cvut.felk.cig.jcool.benchmark.util;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations.SimplePopulation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 18.2.2011
 * Time: 17:13
 * Simple test cases for IndividualUtils class.
 */
public class IndividualUtilsTest extends TestCase {

    @Test
    // tests null parameter
    public void testMakeCopy1() throws Exception {
        Individual[] copies = IndividualUtils.makeCopy(null, 1);
        assertTrue(copies == null);
    }

    @Test
    // tests zero length
    public void testMakeCopy2() throws Exception {
        Individual[] source = new Individual[0];
        Individual[] copies = IndividualUtils.makeCopy(source, 1);
        assertNotSame(copies, source);
    }

    @Test
    // tests null objects in input
    public void testMakeCopy3() throws Exception {
        Individual[] source = new Individual[5];
        Individual[] copies = IndividualUtils.makeCopy(source, 1);
        assertNotSame(copies, source);
    }

    @Test
    // tests deep copy of non-null objects in input
    public void testMakeCopy4() throws Exception {
        int size = 5;
        Individual[] source = new Individual[size];
        for (int i = 0; i < size; i++){
            if (i == 2 || i == 4){
                source[i] = null;
            } else {
                source[i] = new SimpleIndividual(i, i, new SimplePhenotypeRepresentation(new double[]{1.0, 2.0, 3.0}));
            }
        }
        int birthday = 1;
        Individual[] copies = IndividualUtils.makeCopy(source, birthday);
        for (int i = 0; i < size; i++){
            if (i == 2 || i == 4){
                assertTrue(copies[i] == null);
            } else {
                assertNotSame(copies[i], source[i]);
                assertEquals(birthday, copies[i].getBirthday());
                assertNotSame(copies[i].getRepresentation(), source[i].getRepresentation());
            }
        }

        assertNotSame(copies, source);
    }

    @Test
    // test whether the the function finds best individual in non-first population
    public void testGetBestIndividual(){
        Individual ind1 = new SimpleIndividual(0, 1.0, null);
        ind1.setValue(4.0);
        Individual ind2 = new SimpleIndividual(0, 1.0, null);
        ind2.setValue(1.0);
        Individual ind3 = new SimpleIndividual(0, 1.0, null);
        ind3.setValue(2.0);
        Population[] inputPopulations = new Population[]{new SimplePopulation(new Individual[]{ind1}),
                                                        new SimplePopulation(new Individual[]{ind2}),
                                                        new SimplePopulation(new Individual[]{ind3})};
        Individual bestIndividual = IndividualUtils.getBestIndividual(inputPopulations);
        assertNotNull(bestIndividual);
        assertSame(ind2, bestIndividual);
    }
}
