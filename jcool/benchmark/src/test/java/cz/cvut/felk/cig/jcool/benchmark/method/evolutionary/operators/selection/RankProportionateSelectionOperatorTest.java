package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.util.EmptyRandomGenerator;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.2.2011
 * Time: 16:03
 * Tests whether Individuals are selected correctly to previously given "RandomGenerator"
 */
public class RankProportionateSelectionOperatorTest extends TestCase {

    protected RankProportionateSelectionOperator operator;
    Individual[] inputIndividuals;

    @Before
    public void setUp() throws Exception {
        this.operator = new RankProportionateSelectionOperator();

        this.operator.setInputArity(1);
        this.operator.setOutputArity(1);
        this.operator.setIndividualsPerPopulation(10);

        this.inputIndividuals = new Individual[5];
        double[] fitness = new double[]{2.0, 4.0, 1.0, 5.0, 3.0}; // probabilities are 12%, 28%, 4%, 36% and 20%
        for (int i = 0; i < 5; i++){
            inputIndividuals[i] = new SimpleIndividual(1, 1.0, new SimplePhenotypeRepresentation());
            inputIndividuals[i].setFitness(fitness[i]);
        }
    }

    @Test
    // tests some randomly selected values
    public void testSelectWinners1() throws Exception {
        this.operator.setIndividualsPerPopulation(5);
        this.operator.setRandomGenerator(new EmptyRandomGenerator(){
            double[] values = new double[]{0.1, 0.15, 0.4, 0.5, 1.0};
            int index = 0;
            @Override
            public double nextRandom() {
                return values[index++];
            }
        });

        Individual[] inputIndividualsCopy = Arrays.copyOf(this.inputIndividuals, this.inputIndividuals.length);
        double[] accumulatedProbabilities = this.operator.makeAccumulatedProbabilities(inputIndividualsCopy);
//        String msg = "";
//        for (int i = 0; i < accumulatedProbabilities.length; i++){
//            msg += "[" + i + "] = " + accumulatedProbabilities[i] + " ";
//        }
//        for (int i = 0; i < inputIndividualsCopy.length; i++){
//            msg += "[" + i + "] = " + inputIndividualsCopy[i].getFitness() + " ";
//        }
//        throw new Exception(msg);
        List<Individual> winners = operator.selectWinners(inputIndividualsCopy, accumulatedProbabilities);
        assertEquals(5, winners.size());
        assertSame(this.inputIndividuals[3], winners.get(0));
        assertSame(this.inputIndividuals[3], winners.get(1));
        assertSame(this.inputIndividuals[1], winners.get(2));
        assertSame(this.inputIndividuals[1], winners.get(3));
        assertSame(this.inputIndividuals[2], winners.get(4));
    }

    @Test
    //test marginal conditions
    public void testSelectWinners2() throws Exception {
        this.operator.setIndividualsPerPopulation(5);
        this.operator.setRandomGenerator(new EmptyRandomGenerator(){
            double[] values = new double[]{0.36, 0.64, 0.84, 0.96, 1.0};
            int index = 0;
            @Override
            public double nextRandom() {
                return values[index++];
            }
        });

        Individual[] inputIndividualsCopy = Arrays.copyOf(this.inputIndividuals, this.inputIndividuals.length);
        double[] accumulatedProbabilities = this.operator.makeAccumulatedProbabilities(inputIndividualsCopy);

        List<Individual> winners = operator.selectWinners(inputIndividualsCopy, accumulatedProbabilities);
        assertEquals(5, winners.size());
        assertSame(this.inputIndividuals[3], winners.get(0));
        assertSame(this.inputIndividuals[1], winners.get(1));
        assertSame(this.inputIndividuals[4], winners.get(2));
        assertSame(this.inputIndividuals[0], winners.get(3));
        assertSame(this.inputIndividuals[2], winners.get(4));
    }
}
