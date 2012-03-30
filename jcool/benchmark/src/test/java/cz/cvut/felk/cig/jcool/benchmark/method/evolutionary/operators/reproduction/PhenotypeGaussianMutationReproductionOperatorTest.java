package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.population.SimplePopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations.SimplePopulation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.util.EmptyObjectiveFunction;
import cz.cvut.felk.cig.jcool.benchmark.util.EmptyRandomGenerator;
import cz.cvut.felk.cig.jcool.core.*;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.2.2011
 * Time: 19:38
 * Tests for PhenotypeGaussianOperator.
 */
public class PhenotypeGaussianMutationReproductionOperatorTest extends TestCase {

    protected PhenotypeGaussianMutationReproductionOperator operator;
    protected RandomGenerator randomGenerator;
    protected ObjectiveFunction function;

    @Test
    // makes Individuals and mutates some of their values
    public void testReproduce() throws Exception {
        this.operator = new PhenotypeGaussianMutationReproductionOperator();

        this.randomGenerator = new EmptyRandomGenerator(){
            protected double[] randoms = new double[]{0.8, 0.3, 0.4, 0.5};
            protected int randomIdx = 0;
            protected double[] gaussians = new double[]{1.0, 0.5, -0.75};
            protected int gaussIdx = 0;

            @Override
            public double nextRandom() {
                return randoms[randomIdx++];
            }
            @Override
            public double nextGaussian(double mean, double standardDeviation) {
                return gaussians[gaussIdx++];
            }
        };
        this.operator.setRandomGenerator(this.randomGenerator);
        // { <-3.0, 1.0> , <-2.0, 1.0>}
        this.function = new EmptyObjectiveFunction(){
            @Override
            public boolean inBounds(Point position) {
                double[] pos = position.toArray();
                return (pos[0] > -3.0 && pos[0] < 1.0 && pos[1] > -2.0 && pos[1] < 1.0);
            }
            @Override
            public int getDimension() {
                return 2;
            }
        };
        this.operator.setFunction(this.function);
        this.operator.setPopulationFactory(new SimplePopulationFactory());
        this.operator.setMutationProbability(0.5);
        this.operator.setStepSize(1.0);
        double[] positionsX = new double[]{0.0, -2.0};
        double[] positionsY = new double[]{0.5, -1.0};
        Individual[] parents = new Individual[2];
        for (int i = 0; i < 2; i++){
            parents[i] = new SimpleIndividual(1, 1.0, new SimplePhenotypeRepresentation(new double[]{positionsX[i], positionsY[i]}));
        }

        Population[] childrenPops = this.operator.reproduce(new Population[]{new SimplePopulation(parents)});
        assertTrue(childrenPops.length == 1);
        Individual[] children = childrenPops[0].getIndividuals();
        assertTrue(children.length == 2);
        // first individual without change due to inBounds error
        assertEquals(0.0, children[0].getRepresentation().getDoubleValue()[0]);
        assertEquals(0.5, children[0].getRepresentation().getDoubleValue()[1]);
        // second individual changed in both dimensions
        assertEquals(-1.5, children[1].getRepresentation().getDoubleValue()[0]);
        assertEquals(-1.75, children[1].getRepresentation().getDoubleValue()[1]);
    }
}
