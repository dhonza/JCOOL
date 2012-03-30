package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.util.EmptyRandomGenerator;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.2.2011
 * Time: 15:04
 * Tests right selection when randomGenerator is previously given.
 */
public class StochasticUniversalSamplingSelectionOperatorTest extends TestCase {

    protected StochasticUniversalSamplingSelectionOperator operator;
    Individual[] inputIndividuals;

    @Before
    public void setUp() throws Exception {
        this.operator = new StochasticUniversalSamplingSelectionOperator();
        this.operator.setRandomGenerator(new EmptyRandomGenerator(){
            @Override
            public double nextDouble(double minInclusive, double maxExclusive) {
                double ret = 1.5/15;
                assertTrue(ret >= minInclusive);
                assertTrue(ret < maxExclusive);
                return ret;
            }
        });
        this.operator.setInputArity(1);
        this.operator.setOutputArity(1);
        this.operator.setIndividualsPerPopulation(10);
        
        this.inputIndividuals = new Individual[5];
        double[] fitness = new double[]{2.0, 4.0, 1.0, 5.0, 3.0};
        for (int i = 0; i < 5; i++){
            inputIndividuals[i] = new SimpleIndividual(1, 1.0, new SimplePhenotypeRepresentation());
            inputIndividuals[i].setFitness(fitness[i]);
        }
    }

    @Test
    // selects 3 Individuals, test whether it hits inputIndividual[2] whose probability is very low 1/15 th
    public void testSelectWinners1() throws Exception {
        this.operator.setIndividualsPerPopulation(3);
        double[] accumulatedProbabilities = this.operator.makeAccumulatedProbabilities(this.inputIndividuals);

        List<Individual> winners = this.operator.selectWinners(this.inputIndividuals, accumulatedProbabilities);
        assertEquals(3, winners.size());
        assertSame(this.inputIndividuals[0], winners.get(0));
        assertSame(this.inputIndividuals[2], winners.get(1));
        assertSame(this.inputIndividuals[3], winners.get(2));
    }

    @Test
    // selects 5 Individuals, test whether it hits inputIndividual[3] twice
    public void testSelectWinners2() throws Exception {
        this.operator.setIndividualsPerPopulation(5);
        double[] accumulatedProbabilities = this.operator.makeAccumulatedProbabilities(this.inputIndividuals);

        List<Individual> winners = this.operator.selectWinners(this.inputIndividuals, accumulatedProbabilities);
        assertEquals(5, winners.size());
        assertSame(this.inputIndividuals[0], winners.get(0));
        assertSame(this.inputIndividuals[1], winners.get(1));
        assertSame(this.inputIndividuals[3], winners.get(2));
        assertSame(this.inputIndividuals[3], winners.get(3));
        assertSame(this.inputIndividuals[4], winners.get(4));
    }
}
