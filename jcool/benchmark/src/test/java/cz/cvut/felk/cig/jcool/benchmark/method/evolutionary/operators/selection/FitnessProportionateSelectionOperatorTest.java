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
 * Date: 23.2.2011
 * Time: 16:46
 * Tests main methods of FitnessProportionateSelectionOperator.
 */
public class FitnessProportionateSelectionOperatorTest extends TestCase {

    protected FitnessProportionateSelectionOperator operator;

    @Before
    public void setUp() throws Exception {
        this.operator = new FitnessProportionateSelectionOperator();
        this.operator.setInputArity(1);
        this.operator.setOutputArity(1);
        this.operator.setIndividualsPerPopulation(10);
    }

    @Test
    // test only makeAccumulatedProbabilities
    public void testMakeAccumulatedProbabilities() throws Exception{
        Individual[] inputIndividuals = new Individual[5];
        double[] fitness = new double[]{2.0, 4.0, 1.0, 5.0, 3.0};
        for (int i = 0; i < 5; i++){
            inputIndividuals[i] = new SimpleIndividual(1, 1.0, new SimplePhenotypeRepresentation());
            inputIndividuals[i].setFitness(fitness[i]);
        }
        double[] accumulatedProbabilities = this.operator.makeAccumulatedProbabilities(inputIndividuals);

        assertTrue(accumulatedProbabilities.length == 5);
        assertTrue(accumulatedProbabilities[0] == (2.0/15));
        assertTrue(accumulatedProbabilities[1] == (6.0/15));
        assertTrue(accumulatedProbabilities[2] == (7.0/15));
        assertTrue(accumulatedProbabilities[3] == (12.0/15));
        assertTrue(accumulatedProbabilities[4] == 1.0);
    }

    @Test
    // tests makeAccumulatedProbabilities in connection to selectWinners
    public void testSelectWinners() throws Exception {
        this.operator.setIndividualsPerPopulation(5);
        this.operator.setOutputArity(1);
        this.operator.setInputArity(1);
        this.operator.setRandomGenerator(new EmptyRandomGenerator(){
            double[] numbers = new double[]{10.0/15, 11.0/15, 7.5/15, 8.0/15, 9.0/15};
            int index = 0;
            @Override
            public double nextRandom() {
                return numbers[index++];
            }
        });
        Individual[] inputIndividuals = new Individual[5];
        double[] fitness = new double[]{2.0, 4.0, 1.0, 5.0, 3.0};
        for (int i = 0; i < 5; i++){
            inputIndividuals[i] = new SimpleIndividual(1, 1.0, new SimplePhenotypeRepresentation());
            inputIndividuals[i].setFitness(fitness[i]);
        }
        double[] accumulatedProbabilities = this.operator.makeAccumulatedProbabilities(inputIndividuals);
        // assert the resulting array of probabilities
        assertTrue(accumulatedProbabilities.length == 5);
        assertTrue(accumulatedProbabilities[0] == (2.0/15));
        assertTrue(accumulatedProbabilities[1] == (6.0/15));
        assertTrue(accumulatedProbabilities[2] == (7.0/15));
        assertTrue(accumulatedProbabilities[3] == (12.0/15));
        assertTrue(accumulatedProbabilities[4] == 1.0);

        List<Individual> winners = this.operator.selectWinners(inputIndividuals, accumulatedProbabilities);
        assertEquals(5, winners.size());
        assertTrue(inputIndividuals[3].getFitness() == 5.0);
        // assert that we have chosen 5 times the fittest individual
        for (Individual individual : winners){
            assertTrue(individual.getFitness() == 5.0);
        }
    }
}
