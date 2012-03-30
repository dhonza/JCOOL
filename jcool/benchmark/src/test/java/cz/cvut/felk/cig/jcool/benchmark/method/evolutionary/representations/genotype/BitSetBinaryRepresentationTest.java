package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype;

import cz.cvut.felk.cig.jcool.benchmark.util.MathUtils;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 14.4.2011
 * Time: 11:22
 * Tests for correct work with BitSet.
 */
public class BitSetBinaryRepresentationTest extends TestCase {

    protected BitSetBinaryRepresentation representation;
    protected SimpleBinaryGenomeDescriptor descriptor;

    @Before
    public void setUp() throws Exception {
        this.descriptor = new SimpleBinaryGenomeDescriptor();
        this.descriptor.setNumVariables(4);
        this.descriptor.setVariablesLengths(new int[]{6, 2, 4, 9});
        this.descriptor.setVariablesLowerBounds(new double[]{-10.0, 0.0, 10E10, 123456});
        this.descriptor.setVariablesPrecisions(new double[]{1.0, 0.5, 123.45, 0.001});
        this.representation = new BitSetBinaryRepresentation(this.descriptor);
    }

    @Test
    // tests decoding of internal information according to descriptor
    public void testGetDoubleValue1(){
        boolean[] boolGenome = new boolean[]{
                false, true, false, true, true, true,
                true, false,
                true, true, true, true,
                true, true, true, true, true, false, true, false, false
        };
        for (int idx = 0; idx < boolGenome.length; idx++){
            this.representation.setGeneAt(idx, boolGenome[idx]);
        }
        double[] expected = new double[]{13.0, 1.0, 1851.75+10e10, 123456.5};
        double[] result = this.representation.getDoubleValue();
        assertEquals(expected.length, result.length);
        Assert.assertArrayEquals(expected, result, 0.0);
    }

    @Test
    // tests encoding of given information according to descriptor
    public void testSetDoubleValue1(){
        this.representation.setDoubleValue(new double[]{13.0, 1.0, 1851.75+10e10, 123456.5});
        boolean[] boolGenome = new boolean[this.representation.genomeLength];
        for (int idx = 0; idx < boolGenome.length; idx++){
            boolGenome[idx] = this.representation.genome.get(idx);
        }
        boolean[] expected = new boolean[]{
                false, true, false, true, true, true,
                true, false,
                true, true, true, true,
                true, true, true, true, true, false, true, false, false
        };

        assertEquals(expected.length, boolGenome.length);
        for (int i = 0; i < expected.length; i++){
            assertEquals(expected[i], boolGenome[i]);
        }
    }

    @Test
    // tests conversion of integer values into binary strings
    public void testIntegerToBinary1(){
        this.representation = new BitSetBinaryRepresentation(this.descriptor);
        this.representation.integerToBinary(new double[] {23, 2, 31, 500}); // 31 tests whether 5.th bit does not overflow and overrides preceding zero
        boolean[] expected = new boolean[]{
                false, true, false, true, true, true,
                true, false,
                true, true, true, true,
                true, true, true, true, true, false, true, false, false
        };
        assertEquals(MathUtils.sumSizes(this.descriptor.variablesLengths), this.representation.genomeLength);
        assertEquals(this.representation.genomeLength, expected.length);
        for (int i = 0; i < expected.length; i++){
            assertEquals(expected[i], this.representation.genome.get(i));
        }
    }

    @Test
    // tests conversion of binary numbers into integers
    public void testBinaryToInteger1(){
        boolean[] boolGenome = new boolean[]{
                false, true, false, true, true, true,
                true, false,
                true, true, true, true,
                true, true, true, true, true, false, true, false, false
        };
        for (int idx = 0; idx < boolGenome.length; idx++){
            this.representation.setGeneAt(idx, boolGenome[idx]);
        }
        double[] expected = new double[] {23, 2, 15, 500};

        double[] result = this.representation.binaryToInteger();

        assertEquals(MathUtils.sumSizes(this.descriptor.variablesLengths), this.representation.genomeLength);
        assertEquals(expected.length, result.length);

        Assert.assertArrayEquals(expected, result, 0.0);
    }
}
