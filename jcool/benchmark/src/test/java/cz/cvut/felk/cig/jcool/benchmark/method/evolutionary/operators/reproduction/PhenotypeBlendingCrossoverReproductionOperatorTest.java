package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.population.SimplePopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations.SimplePopulation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.util.EmptyObjectiveFunction;
import cz.cvut.felk.cig.jcool.benchmark.util.EmptyRandomGenerator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.2.2011
 * Time: 22:30
 * Tests for PhenotypeBlendingCrossoverReproductionOperator.
 */
public class PhenotypeBlendingCrossoverReproductionOperatorTest extends TestCase {

    protected PhenotypeBlendingCrossoverReproductionOperator operator;
    protected Individual[] allIndividuals;

    @Before
    public void setUp() throws Exception {
        this.operator = new PhenotypeBlendingCrossoverReproductionOperator();
        this.operator.setPopulationFactory(new SimplePopulationFactory());
        this.operator.setRandomGenerator(new EmptyRandomGenerator());
        this.operator.setFunction(new EmptyObjectiveFunction());
        Individual ind1 = new SimpleIndividual(1, 0.0, new SimplePhenotypeRepresentation(new double[]{2.0, 1.0}));
        ind1.setFitness(1.0);
        Individual ind2 = new SimpleIndividual(2, 0.0, new SimplePhenotypeRepresentation(new double[]{2.0, 2.0}));
        ind2.setFitness(2.0);
        Individual ind3 = new SimpleIndividual(3, 0.0, new SimplePhenotypeRepresentation(new double[]{-3.0, 2.0}));
        ind3.setFitness(3.0);
        Individual ind4 = new SimpleIndividual(4, 0.0, new SimplePhenotypeRepresentation(new double[]{1.0, -3.0}));
        ind4.setFitness(4.0);
        allIndividuals = new Individual[]{ind1, ind2, ind3, ind4};
    }

    @Test
    //tests creation of one child only
    public void testReproduce1() throws Exception {
        Individual[] par1 = new Individual[]{this.allIndividuals[0], this.allIndividuals[1]};
        Individual[] par2 = new Individual[]{this.allIndividuals[2], this.allIndividuals[3]};
        Population parPop1 = new SimplePopulation(par1);
        Population parPop2 = new SimplePopulation(par2);
        Population[] parentPops = new Population[]{parPop1, parPop2};

        this.operator.setBlendRatio(0.25);
        this.operator.setCreateBothChildren(false);
        Population[] popsOut = this.operator.reproduce(parentPops);
        assertEquals(1, popsOut.length);
        Individual[] children = popsOut[0].getIndividuals();
        assertEquals(2, children.length);
        assertEquals(3.0, children[0].getParentFitness());
        Assert.assertArrayEquals(new double[]{-1.75, 1.75}, children[0].getRepresentation().getDoubleValue(), 0.0);

        assertEquals(4.0, children[1].getParentFitness());
        Assert.assertArrayEquals(new double[]{1.25, -1.75}, children[1].getRepresentation().getDoubleValue(), 0.0);
    }

    @Test
    //tests creation of both children
    public void testReproduce2() throws Exception {
        Individual[] par1 = new Individual[]{this.allIndividuals[0], this.allIndividuals[1]};
        Individual[] par2 = new Individual[]{this.allIndividuals[2], this.allIndividuals[3]};
        Population parPop1 = new SimplePopulation(par1);
        Population parPop2 = new SimplePopulation(par2);
        Population[] parentPops = new Population[]{parPop1, parPop2};

        this.operator.setBlendRatio(0.3);
        this.operator.setCreateBothChildren(true);
        Population[] popsOut = this.operator.reproduce(parentPops);
        assertEquals(2, popsOut.length);
        Individual[] children1 = popsOut[0].getIndividuals();
        Individual[] children2 = popsOut[1].getIndividuals();
        assertEquals(2, children1.length);
        assertEquals(3.0, children1[0].getParentFitness());
        Assert.assertArrayEquals(new double[]{-1.5, 1.7}, children1[0].getRepresentation().getDoubleValue(), 0.0000001);
        assertEquals(4.0, children1[1].getParentFitness());
        Assert.assertArrayEquals(new double[]{1.3, -1.5}, children1[1].getRepresentation().getDoubleValue(), 0.0000001);

        assertEquals(3.0, children2[0].getParentFitness());
        Assert.assertArrayEquals(new double[]{0.5, 1.3}, children2[0].getRepresentation().getDoubleValue(), 0.0000001);
        assertEquals(4.0, children2[1].getParentFitness());
        Assert.assertArrayEquals(new double[]{1.7, 0.5}, children2[1].getRepresentation().getDoubleValue(), 0.0000001);
    }
}