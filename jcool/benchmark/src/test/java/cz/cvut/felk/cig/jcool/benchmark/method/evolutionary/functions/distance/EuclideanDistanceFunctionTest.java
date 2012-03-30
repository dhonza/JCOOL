package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.distance;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 14.2.2011
 * Time: 21:47:29
 */
public class EuclideanDistanceFunctionTest extends TestCase {

    protected EuclideanDistanceFunction function;

    @Before
    protected void setUp(){
        this.function = new EuclideanDistanceFunction();
    }

    @Test
    // test for correct distance measure and symmetric of relationship.
    public void testDistance() throws Exception {
        Representation repr1 = new SimplePhenotypeRepresentation();
        repr1.setDoubleValue(new double[]{1.0, 2.3, 5.6, 1000.4});
        Individual ind1 = new SimpleIndividual(0, 0.0, repr1);
        Representation repr2 = new SimplePhenotypeRepresentation();
        repr2.setDoubleValue(new double[]{13.6, 10004.5, -11.5, -666.7});
        Individual ind2 = new SimpleIndividual(0, 0.0, repr2);
        double distance1 = this.function.distance(ind1, ind2);
        assertEquals(10140.2011, distance1, 0.0001);
        double distance2 = this.function.distance(ind2, ind1);
        assertEquals(10140.2011, distance2, 0.0001);
        distance1 = this.function.distance(ind1, ind1);
        assertTrue(distance1 == 0.0);
    }
}
