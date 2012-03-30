package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.population.SimplePopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations.SimplePopulation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype.SimpleBinaryGenomeDescriptor;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype.SimpleBinaryRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.util.EmptyObjectiveFunction;
import cz.cvut.felk.cig.jcool.benchmark.util.EmptyRandomGenerator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.core.Point;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.3.2011
 * Time: 15:57
 * Test computation of correct mutation probability and bit-flip mutation itself.
 */
public class GenotypeMutationReproductionOperatorTest extends TestCase {

    protected GenotypeMutationReproductionOperator operator;
    protected Population parentPopulation;
    protected boolean[] genomeT;
    protected double[] randomsT;

    @Before
    public void setUp() throws Exception {
        this.operator = new GenotypeMutationReproductionOperator();
        this.operator.setFunction(new EmptyObjectiveFunction(){
            @Override
            public boolean inBounds(Point position) {
                return true;
            }
        });
        this.operator.setPopulationFactory(new SimplePopulationFactory());
        this.genomeT = new boolean[]{true,  true,   true,   true,   true,   false,  false};
        this.randomsT = new double[]{0.1,   1.0/7,  2.0/7,  0.01,   5.0/7,  1.0,    0.05,
                                     0.1,   0.9,    0.8,    0.9,    0.8,    0.01,   0.2};
        SimpleBinaryRepresentation representation = new SimpleBinaryRepresentation(new SimpleBinaryGenomeDescriptor()){
            @Override
            public int getTotalLength() {
                this.genome = genomeT;
                return super.getTotalLength();
            }
        };
        representation.getTotalLength(); // sets genomeT as genome because in call the ancestor is called...
        parentPopulation = new SimplePopulation(new Individual[]{new SimpleIndividual(1, 12.4, representation)});
    }

    @Test
    // test mutation with outputArity 1
    public void testReproduce1() throws Exception {
        this.operator.setProbabilityType(GenotypeMutationReproductionOperator.ProbabilityType.FractionalProbability);
        this.operator.setGenesToMutate(1);
        this.operator.setRandomGenerator(new EmptyRandomGenerator(){
            protected double[] randoms = randomsT;
            protected int randIndex = 0;

            @Override
            public double nextRandom() {
                return this.randoms[randIndex++];
            }
        });
        Population[] childrenPopulations = this.operator.reproduce(new Population[]{parentPopulation});

        assertEquals(1, childrenPopulations.length);
        assertEquals(1, childrenPopulations[0].getIndividuals().length);
        SimpleBinaryRepresentation representation = ((SimpleBinaryRepresentation)childrenPopulations[0].getIndividuals()[0].getRepresentation());
        boolean[] expectedResults = new boolean[]{false, false, true, false, true, false, true};
        assertEquals(expectedResults.length, representation.getTotalLength());
        for (int i = 0; i < expectedResults.length; i++){
            assertEquals("at index " + i, expectedResults[i], representation.getGeneAt(i));
        }
    }

    @Test
    // tests correct mutationProbability value
    public void testComputeMutationProbability1(){
        this.operator.setProbabilityType(GenotypeMutationReproductionOperator.ProbabilityType.FractionalProbability);
        this.operator.setGenesToMutate(1);

        this.operator.computeMutationProbability(parentPopulation.getIndividuals()[0]);
        assertEquals(1.0/7, this.operator.mutationProbability);
    }

    @Test
    // tests correct mutationProbability value if genes to mutate is bigger than genes present
    public void testComputeMutationProbability2(){
        this.operator.setProbabilityType(GenotypeMutationReproductionOperator.ProbabilityType.FractionalProbability);
        this.operator.setMutationProbability(1.0); // dummy value just to make sure
        this.operator.setGenesToMutate(10);

        this.operator.computeMutationProbability(parentPopulation.getIndividuals()[0]);
        assertEquals(1.0, this.operator.mutationProbability);
    }
}
