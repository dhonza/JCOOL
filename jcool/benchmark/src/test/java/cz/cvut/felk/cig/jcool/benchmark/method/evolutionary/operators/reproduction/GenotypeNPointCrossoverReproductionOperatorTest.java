package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.GenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype.SimpleBinaryGenomeDescriptor;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype.SimpleBinaryRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.util.EmptyRandomGenerator;
import cz.cvut.felk.cig.jcool.core.RandomGenerator;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.3.2011
 * Time: 20:04
 * Tests reproduceInternal method.
 */
public class GenotypeNPointCrossoverReproductionOperatorTest extends TestCase {

    protected GenotypeNPointCrossoverReproductionOperator operator;
    protected RandomGenerator randomGenerator;
    protected boolean[] genome1;
    protected boolean[] genome2;
    protected boolean[] expectedGenome1;
    protected boolean[] expectedGenome2;

    @Before
    public void setUp(){
        this.operator = new GenotypeNPointCrossoverReproductionOperator();
        this.operator.setRandomGenerator(new EmptyRandomGenerator(){
            protected int[] ints = new int[]{8, 12, 7, 2};
            protected int intIdx = 0;
            @Override
            public int nextInt(int minInclusive, int maxExclusive) {
                return this.ints[intIdx++];
            }
        });
        //                             0     1       2       3       4       5       6       7       8
        this.genome1 = new boolean[]{false, true,   false,  false,  true,   true,   false,  true,   true,
                                     true,  false,  true,   false,  false,  true,   true,   false,  false};
        this.genome2 = new boolean[]{true,  true,   false,  false,  false,  false,  true,   false,  true,
                                     false, false,  false,  true,   false,  false,  true,   false,  true};
        this.expectedGenome1 = new boolean[]
                                    {false, true,   false,  false,  false,  false,  true,   true,   true,
                                     false, false,  false,  false,  false,  true,   true,   false,  false};
        this.expectedGenome2 = new boolean[]
                                    {true,  true,   false,  false,  true,   true,   false,  false,  true,
                                     true,  false,  true,   true,   false,  false,  true,   false,  true};
    }

    @Test
    public void testReproduceInternal1() throws Exception {
        this.operator.setCreateBothChildren(true);
        this.operator.setCrossPointCount(4);
        SimpleBinaryRepresentation repr1 = new SimpleBinaryRepresentation(new SimpleBinaryGenomeDescriptor()){
            @Override
            public int getTotalLength() {
                this.genome = genome1;
                return super.getTotalLength();
            }
        };
        repr1.getTotalLength();
        Individual first = new SimpleIndividual(1, 0.0, repr1);
        SimpleBinaryRepresentation repr2 = new SimpleBinaryRepresentation(new SimpleBinaryGenomeDescriptor()){
            @Override
            public int getTotalLength() {
                this.genome = genome2;
                return super.getTotalLength();
            }
        };
        repr2.getTotalLength();
        Individual second = new SimpleIndividual(1, 0.0, repr2);
        this.operator.reproduceInternal(new Individual[]{first}, new Individual[]{second});
        GenotypeRepresentation result1 = ((GenotypeRepresentation)first.getRepresentation());
        GenotypeRepresentation result2 = ((GenotypeRepresentation)second.getRepresentation());
        assertEquals(this.expectedGenome1.length, result1.getTotalLength());
        assertEquals(this.expectedGenome2.length, result2.getTotalLength());
        for (int i = 0; i < this.expectedGenome1.length; i++){
            assertEquals("at index " + i, expectedGenome1[i], result1.getGeneAt(i));
        }
        for (int i = 0; i < this.expectedGenome2.length; i++){
            assertEquals("at index " + i, expectedGenome2[i], result2.getGeneAt(i));
        }
    }
}
