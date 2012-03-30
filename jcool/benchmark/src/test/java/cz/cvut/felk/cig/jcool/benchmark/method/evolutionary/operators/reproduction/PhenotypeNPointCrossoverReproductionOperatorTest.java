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
 * Time: 21:41
 * Tests makeCutPointFlags and reproduce.
 */
public class PhenotypeNPointCrossoverReproductionOperatorTest extends TestCase{

    protected DeJongParabolaFunction function;
    protected PhenotypeNPointCrossoverReproductionOperator operator;
    protected Population[] parents;

    @Before
    public void setUp(){
        this.function = new DeJongParabolaFunction();
        this.function.setDimension(2);
        this.function.setOffset(0.0);
        this.function.setInverted(false);

        this.operator = new PhenotypeNPointCrossoverReproductionOperator();
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
    // test simple case of dimension 2, crossover 1 and 1 child
    public void testReproduce1() throws Exception {
        this.operator.setCrossPointCount(1);
        this.operator.setCreateBothChildren(false);

        Population[] childPops = this.operator.reproduce(this.parents);
        assertTrue(childPops.length == 1);
        Individual[] parents1 = this.parents[0].getIndividuals();
        Individual[] parents2 = this.parents[1].getIndividuals();
        Individual[] children = childPops[0].getIndividuals();
        assertTrue(children.length == 2);
        Assert.assertTrue(parents1[0].getRepresentation().getDoubleValue()[0] == children[0].getRepresentation().getDoubleValue()[0]);
        Assert.assertTrue(parents2[0].getRepresentation().getDoubleValue()[1] == children[0].getRepresentation().getDoubleValue()[1]);
        Assert.assertTrue(parents1[1].getRepresentation().getDoubleValue()[0] == children[1].getRepresentation().getDoubleValue()[0]);
        Assert.assertTrue(parents2[1].getRepresentation().getDoubleValue()[1] == children[1].getRepresentation().getDoubleValue()[1]);
    }
}
