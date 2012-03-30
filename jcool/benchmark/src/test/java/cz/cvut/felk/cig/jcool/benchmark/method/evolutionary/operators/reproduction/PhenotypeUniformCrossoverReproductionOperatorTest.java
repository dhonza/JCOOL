package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.function.DeJongParabolaFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.population.SimplePopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations.SimplePopulation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.util.SimpleRandomGenerator;
import cz.cvut.felk.cig.jcool.solver.BaseObjectiveFunction;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 20.2.2011
 * Time: 12:49
 * Tests for successful crossover operation.
 */
public class PhenotypeUniformCrossoverReproductionOperatorTest extends TestCase {

    protected DeJongParabolaFunction function;
    protected PhenotypeUniformCrossoverReproductionOperator operator;
    protected Population[] parents;

    @Before
    protected void setUp(){
        this.function = new DeJongParabolaFunction();
        this.function.setDimension(2);
        this.function.setOffset(0.0);
        this.function.setInverted(false);

        this.operator = new PhenotypeUniformCrossoverReproductionOperator();
        this.operator.setFunction(new BaseObjectiveFunction(this.function));
        this.operator.setRandomGenerator(new SimpleRandomGenerator());
        this.operator.setPopulationFactory(new SimplePopulationFactory());

        this.parents = new Population[2];
        SimpleIndividual ind1 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{1.0, 2.0}));
        SimpleIndividual ind2 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{-2.0, 2.0}));
        SimpleIndividual ind3 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{-3.0, -2.0}));
        SimpleIndividual ind4 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{1.0, -3.0}));

        Individual[] parents1 = new Individual[]{ind1, ind2};
        Individual[] parents2 = new Individual[]{ind3, ind4};

        this.parents[0] = new SimplePopulation(parents1);
        this.parents[1] = new SimplePopulation(parents2);
    }

    @Test
    // tests that no information will be received from second parent
    public void testReproduce1() throws Exception {
        this.operator.setCrossoverProbability(0.0);
        this.operator.setCreateBothChildren(false);
        Population[] childPops = this.operator.reproduce(this.parents);

        assertTrue(childPops.length == 1);
        Individual[] parents = this.parents[0].getIndividuals();
        Individual[] children = childPops[0].getIndividuals();
        assertTrue(children.length == 2);
        Assert.assertArrayEquals(parents[0].getRepresentation().getDoubleValue(), children[0].getRepresentation().getDoubleValue(), 0.0);
        Assert.assertArrayEquals(parents[1].getRepresentation().getDoubleValue(), children[1].getRepresentation().getDoubleValue(), 0.0);
    }

    @Test
    // tests that all information will be received from second parent
    public void testReproduce2() throws Exception {
        this.operator.setCrossoverProbability(1.0);
        this.operator.setCreateBothChildren(false);
        Population[] childPops = this.operator.reproduce(this.parents);

        assertTrue(childPops.length == 1);
        Individual[] parents = this.parents[1].getIndividuals();
        Individual[] children = childPops[0].getIndividuals();
        assertTrue(children.length == 2);
        Assert.assertArrayEquals(parents[0].getRepresentation().getDoubleValue(), children[0].getRepresentation().getDoubleValue(), 0.0);
        Assert.assertArrayEquals(parents[1].getRepresentation().getDoubleValue(), children[1].getRepresentation().getDoubleValue(), 0.0);
    }

    @Test
    // tests that two children are created and that one children has information from only one parent
    public void testReproduce3() throws Exception {
        this.operator.setCrossoverProbability(0.0);
        this.operator.setCreateBothChildren(true);
        Population[] childPops = this.operator.reproduce(this.parents);

        assertTrue(childPops.length == 2);
        Individual[] parents1 = this.parents[0].getIndividuals();
        Individual[] parents2 = this.parents[1].getIndividuals();
        Individual[] children1 = childPops[0].getIndividuals();
        Individual[] children2 = childPops[1].getIndividuals();
        assertTrue(children1.length == 2);
        assertTrue(children2.length == 2);
        Assert.assertArrayEquals(parents1[0].getRepresentation().getDoubleValue(), children1[0].getRepresentation().getDoubleValue(), 0.0);
        Assert.assertArrayEquals(parents1[1].getRepresentation().getDoubleValue(), children1[1].getRepresentation().getDoubleValue(), 0.0);
        Assert.assertArrayEquals(parents2[0].getRepresentation().getDoubleValue(), children2[0].getRepresentation().getDoubleValue(), 0.0);
        Assert.assertArrayEquals(parents2[1].getRepresentation().getDoubleValue(), children2[1].getRepresentation().getDoubleValue(), 0.0);
    }

    @Test
    // tests that two children are created and that one children has information from only one parent
    public void testReproduce4() throws Exception {
        this.operator.setCrossoverProbability(1.0);
        this.operator.setCreateBothChildren(true);
        Population[] childPops = this.operator.reproduce(this.parents);

        assertTrue(childPops.length == 2);
        Individual[] parents1 = this.parents[0].getIndividuals();
        Individual[] parents2 = this.parents[1].getIndividuals();
        Individual[] children1 = childPops[0].getIndividuals();
        Individual[] children2 = childPops[1].getIndividuals();
        assertTrue(children1.length == 2);
        assertTrue(children2.length == 2);
        Assert.assertArrayEquals(parents1[0].getRepresentation().getDoubleValue(), children2[0].getRepresentation().getDoubleValue(), 0.0);
        Assert.assertArrayEquals(parents1[1].getRepresentation().getDoubleValue(), children2[1].getRepresentation().getDoubleValue(), 0.0);
        Assert.assertArrayEquals(parents2[0].getRepresentation().getDoubleValue(), children1[0].getRepresentation().getDoubleValue(), 0.0);
        Assert.assertArrayEquals(parents2[1].getRepresentation().getDoubleValue(), children1[1].getRepresentation().getDoubleValue(), 0.0);
    }
}
