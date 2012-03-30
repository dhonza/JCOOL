package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.evaluators;

import cz.cvut.felk.cig.jcool.benchmark.function.DeJongParabolaFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations.SimplePopulation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 20.2.2011
 * Time: 16:26
 * Tests whether evaluation truly works
 */
public class SimpleFunctionEvaluatorTest extends TestCase{

    @Before
    protected void setUp(){
    }

    @Test
    public void testEvaluate1() throws Exception {
        DeJongParabolaFunction function = new DeJongParabolaFunction();
        function.setDimension(2);
        function.setOffset(0.0);
        function.setInverted(false);

        SimpleIndividual ind1 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{1.0, 2.0}));
        SimpleIndividual ind2 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{-2.0, 2.0}));
        SimpleIndividual ind3 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{-3.0, -2.0}));
        SimpleIndividual ind4 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{1.0, -3.0}));
        ind1.setValue(0.0); ind2.setValue(0.0); ind3.setValue(0.0); ind4.setValue(0.0);

        Individual[] individuals = new Individual[]{ind1, ind2, ind3, ind4};
        Population[] populations = new Population[]{new SimplePopulation(individuals)};

        SimpleFunctionEvaluator evaluator = new SimpleFunctionEvaluator();

        evaluator.evaluate(populations, function);

        assertEquals(5.0, ind1.getValue(), 0.0);
        assertEquals(8.0, ind2.getValue(), 0.0);
        assertEquals(13.0, ind3.getValue(), 0.0);
        assertEquals(10.0, ind4.getValue(), 0.0);
    }
}
