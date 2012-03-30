package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.population.SimplePopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations.SimplePopulation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.util.SimpleRandomGenerator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 21.2.2011
 * Time: 21:21
 * Tests correct sizes and non-null instances of Individuals.
 */
public class UniformStochasticSelectionOperatorTest extends TestCase {

    protected UniformStochasticSelectionOperator operator;

    @Before
    protected void setUp(){
        this.operator = new UniformStochasticSelectionOperator();
        this.operator.setPopulationFactory(new SimplePopulationFactory());
        this.operator.setRandomGenerator(new SimpleRandomGenerator());
    }

    @Test
    // tests selection of more Individuals than given from one Population
    public void testSelect1() throws Exception {
        SimplePopulation popIn1 = new SimplePopulation();
        Individual[] individuals1 = new SimpleIndividual[2];
        for (int i = 0; i < individuals1.length; i++){
            individuals1[i] = new SimpleIndividual(0, 0.0, new SimplePhenotypeRepresentation());
            individuals1[i].setFitness((i-1)*(i-1)); // shifted parabola with prescription y = x^2 and bottom at position (x = -1).
        }
        popIn1.setIndividuals(individuals1);
        this.operator.setInputArity(1);
        this.operator.setOutputArity(1);
        this.operator.setIndividualsPerPopulation(4);
        Population[] popsOut = this.operator.select(new SimplePopulation[]{popIn1});
        assertTrue(popsOut.length == 1);
        assertTrue(popsOut[0].getIndividuals().length == 4);
        for (Individual ind : popsOut[0].getIndividuals()){
            assertTrue(ind != null);
        }
    }

    @Test
    // tests selection of less Individuals than given from two Populations
    public void testSelect2() throws Exception {
        SimplePopulation popIn1 = new SimplePopulation();
        SimplePopulation popIn2 = new SimplePopulation();
        Individual[] individuals1 = new SimpleIndividual[2];
        for (int i = 0; i < individuals1.length; i++){
            individuals1[i] = new SimpleIndividual(0, 0.0, new SimplePhenotypeRepresentation());
            individuals1[i].setFitness((i-1)*(i-1)); // shifted parabola with prescription y = x^2 and bottom at position (x = -1).
        }
        Individual[] individuals2 = new SimpleIndividual[2];
        for (int i = 0; i < individuals2.length; i++){
            individuals2[i] = new SimpleIndividual(0, 0.0, new SimplePhenotypeRepresentation());
            individuals2[i].setFitness((i-1)*(i-1)); // shifted parabola with prescription y = x^2 and bottom at position (x = -1).
        }
        popIn1.setIndividuals(individuals1);
        popIn2.setIndividuals(individuals2);
        this.operator.setInputArity(2);
        this.operator.setOutputArity(1);
        this.operator.setIndividualsPerPopulation(2);
        Population[] popsOut = this.operator.select(new SimplePopulation[]{popIn1, popIn2});
        assertTrue(popsOut.length == 1);
        assertTrue(popsOut[0].getIndividuals().length == 2);
        for (Individual ind : popsOut[0].getIndividuals()){
            assertTrue(ind != null);
        }
    }
}

