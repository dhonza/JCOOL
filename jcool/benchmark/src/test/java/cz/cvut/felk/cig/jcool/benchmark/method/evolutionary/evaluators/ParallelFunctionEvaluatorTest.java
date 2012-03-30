package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.evaluators;

import cz.cvut.felk.cig.jcool.benchmark.function.DeJongParabolaFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations.SimplePopulation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.core.Point;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 10.4.2011
 * Time: 16:38
 * To change this template use File | Settings | File Templates.
 */
public class ParallelFunctionEvaluatorTest extends TestCase {

    public static class FunctionWrapper implements Function{

        protected Function function;
        protected int numCalls = 0;
        protected int maxCalls = 0;

        public FunctionWrapper(Function function, int maxCalls){
            this.function = function;
            this.maxCalls = maxCalls;
        }

        public double valueAt(Point point) {
            if (this.numCalls >= maxCalls){
                throw new IllegalArgumentException("valueAt executed too many times!");
            }
            this.numCalls++;
            return this.function.valueAt(point);
        }

        public int getDimension() {
            return this.function.getDimension();
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetIndexBehindIndividual() throws Exception {
        ParallelFunctionEvaluator evaluator = new ParallelFunctionEvaluator();
        Population[] populations = new Population[]{new SimplePopulation(new Individual[6]), new SimplePopulation(new Individual[4]), new SimplePopulation(new Individual[3])};

        ParallelFunctionEvaluator.PositionDescriptor descriptor = evaluator.getIndexBehindIndividual(populations, 0);
        assertEquals(0, descriptor.getPopulationIndex());
        assertEquals(1, descriptor.getIndividualIndex());

        descriptor = evaluator.getIndexBehindIndividual(populations, 6);
        assertEquals(1, descriptor.getPopulationIndex());
        assertEquals(1, descriptor.getIndividualIndex());

        descriptor = evaluator.getIndexBehindIndividual(populations, 1);
        assertEquals(0, descriptor.getPopulationIndex());
        assertEquals(2, descriptor.getIndividualIndex());

        descriptor = evaluator.getIndexBehindIndividual(populations, 5);
        assertEquals(0, descriptor.getPopulationIndex());
        assertEquals(6, descriptor.getIndividualIndex());

        descriptor = evaluator.getIndexBehindIndividual(populations, 9);
        assertEquals(1, descriptor.getPopulationIndex());
        assertEquals(4, descriptor.getIndividualIndex());

        descriptor = evaluator.getIndexBehindIndividual(populations, 10);
        assertEquals(2, descriptor.getPopulationIndex());
        assertEquals(1, descriptor.getIndividualIndex());

        descriptor = evaluator.getIndexBehindIndividual(populations, 12);
        assertEquals(2, descriptor.getPopulationIndex());
        assertEquals(3, descriptor.getIndividualIndex());

        descriptor = evaluator.getIndexBehindIndividual(populations, 13);
        assertEquals(2, descriptor.getPopulationIndex());
        assertEquals(3, descriptor.getIndividualIndex());

        // over-indexed, then return one position after the last individual
        descriptor = evaluator.getIndexBehindIndividual(populations, 14);
        assertEquals(2, descriptor.getPopulationIndex());
        assertEquals(3, descriptor.getIndividualIndex());
    }

    @Test
    public void testGetIndexForIndividual() throws Exception {
        ParallelFunctionEvaluator evaluator = new ParallelFunctionEvaluator();
        Population[] populations = new Population[]{new SimplePopulation(new Individual[6]), new SimplePopulation(new Individual[4]), new SimplePopulation(new Individual[3])};

        ParallelFunctionEvaluator.PositionDescriptor descriptor = evaluator.getIndexForIndividual(populations, 7);
        assertEquals(1, descriptor.getPopulationIndex());
        assertEquals(1, descriptor.getIndividualIndex());

        descriptor = evaluator.getIndexForIndividual(populations, 0);
        assertEquals(0, descriptor.getPopulationIndex());
        assertEquals(0, descriptor.getIndividualIndex());

        descriptor = evaluator.getIndexForIndividual(populations, 1);
        assertEquals(0, descriptor.getPopulationIndex());
        assertEquals(1, descriptor.getIndividualIndex());

        descriptor = evaluator.getIndexForIndividual(populations, 6);
        assertEquals(1, descriptor.getPopulationIndex());
        assertEquals(0, descriptor.getIndividualIndex());

        descriptor = evaluator.getIndexForIndividual(populations, 9);
        assertEquals(1, descriptor.getPopulationIndex());
        assertEquals(3, descriptor.getIndividualIndex());

        descriptor = evaluator.getIndexForIndividual(populations, 10);
        assertEquals(2, descriptor.getPopulationIndex());
        assertEquals(0, descriptor.getIndividualIndex());

        descriptor = evaluator.getIndexForIndividual(populations, 12);
        assertEquals(2, descriptor.getPopulationIndex());
        assertEquals(2, descriptor.getIndividualIndex());

        // over-indexed, then return one position after the last individual
        descriptor = evaluator.getIndexForIndividual(populations, 14);
        assertEquals(2, descriptor.getPopulationIndex());
        assertEquals(2, descriptor.getIndividualIndex());
    }

    @Test
    // test single thread
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

        ParallelFunctionEvaluator evaluator = new ParallelFunctionEvaluator(1);

        evaluator.evaluate(populations, function);

        assertEquals(5.0, ind1.getValue(), 0.0);
        assertEquals(8.0, ind2.getValue(), 0.0);
        assertEquals(13.0, ind3.getValue(), 0.0);
        assertEquals(10.0, ind4.getValue(), 0.0);
    }

    @Test
    // test two threads
    public void testEvaluate2() throws Exception {
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

        ParallelFunctionEvaluator evaluator = new ParallelFunctionEvaluator(2);

        evaluator.evaluate(populations, function);

        assertEquals(5.0, ind1.getValue(), 0.0);
        assertEquals(8.0, ind2.getValue(), 0.0);
        assertEquals(13.0, ind3.getValue(), 0.0);
        assertEquals(10.0, ind4.getValue(), 0.0);
    }
    
    @Test
    public void testEvaluate3() throws Exception {
        DeJongParabolaFunction function = new DeJongParabolaFunction();
        Function functionWrapper = new FunctionWrapper(function, 6);
        function.setDimension(2);
        function.setOffset(0.0);
        function.setInverted(false);

        SimpleIndividual ind1 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{1.0, 2.0}));
        SimpleIndividual ind2 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{-2.0, 2.0}));
        SimpleIndividual ind3 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{-3.0, -2.0}));
        SimpleIndividual ind4 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{1.0, -3.0}));
        SimpleIndividual ind5 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{2.0, -1.0}));
        SimpleIndividual ind6 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{4.0, 3.0}));
        ind1.setValue(0.0); ind2.setValue(0.0); ind3.setValue(0.0); ind4.setValue(0.0); ind5.setValue(0.0); ind6.setValue(0.0);

        Individual[] individuals1 = new Individual[]{ind1, ind2};
        Individual[] individuals2 = new Individual[]{ind3};
        Individual[] individuals3 = new Individual[]{ind4, ind5, ind6};
        Population[] populations = new Population[]{new SimplePopulation(individuals1), new SimplePopulation(individuals2), new SimplePopulation(individuals3)};

        ParallelFunctionEvaluator evaluator = new ParallelFunctionEvaluator(5);

        evaluator.evaluate(populations, functionWrapper);

        assertEquals(5.0, ind1.getValue(), 0.0);
        assertEquals(8.0, ind2.getValue(), 0.0);
        assertEquals(13.0, ind3.getValue(), 0.0);
        assertEquals(10.0, ind4.getValue(), 0.0);
        assertEquals(5.0, ind5.getValue(), 0.0);
        assertEquals(25.0, ind6.getValue(), 0.0);
    }
}
