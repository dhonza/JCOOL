package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype;

import cz.cvut.felk.cig.jcool.benchmark.util.MathUtils;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 12.3.2011
 * Time: 15:30
 * Tests main methods that could have faulty implementation.
 */
public class SimpleBinaryRepresentationTest extends TestCase {

    protected SimpleBinaryRepresentation representation;
    protected SimpleBinaryGenomeDescriptor descriptor;
    
    @Before
    public void setUp() throws Exception {
        this.descriptor = new SimpleBinaryGenomeDescriptor();
        this.descriptor.setNumVariables(4);
        this.descriptor.setVariablesLengths(new int[]{6, 2, 4, 9});
        this.descriptor.setVariablesLowerBounds(new double[]{-10.0, 0.0, 10E10, 123456});
        this.descriptor.setVariablesPrecisions(new double[]{1.0, 0.5, 123.45, 0.001});
        this.representation = new SimpleBinaryRepresentation(this.descriptor);
    }

    @Test
    // tests decoding of internal information according to descriptor
    public void testGetDoubleValue1(){
        this.representation.genome = new boolean[]{
                false, true, false, true, true, true,
                true, false,
                true, true, true, true,
                true, true, true, true, true, false, true, false, false
        };
        double[] expected = new double[]{13.0, 1.0, 1851.75+10e10, 123456.5};
        double[] result = this.representation.getDoubleValue();
        assertEquals(expected.length, result.length);
        Assert.assertArrayEquals(expected, result, 0.0);
    }

    @Test
    // tests encoding of given information according to descriptor
    public void testSetDoubleValue1(){
        this.representation.setDoubleValue(new double[]{13.0, 1.0, 1851.75+10e10, 123456.5});
        boolean[] genome = this.representation.genome;
        boolean[] expected = new boolean[]{
                false, true, false, true, true, true,
                true, false,
                true, true, true, true,
                true, true, true, true, true, false, true, false, false
        };

        assertEquals(expected.length, genome.length);
        for (int i = 0; i < expected.length; i++){
            assertEquals(expected[i], genome[i]);
        }
    }

    @Test
    // tests conversion of integer values into binary strings
    public void testIntegerToBinary1(){
        this.representation.genome = new boolean[MathUtils.sumSizes(this.descriptor.variablesLengths)];
        this.representation.integerToBinary(new double[] {23, 2, 31, 500}); // 31 tests whether 5.th bit does not overflow and overrides preceding zero
        boolean[] expected = new boolean[]{
                false, true, false, true, true, true,
                true, false,
                true, true, true, true,
                true, true, true, true, true, false, true, false, false
        };
        assertEquals(MathUtils.sumSizes(this.descriptor.variablesLengths), this.representation.genome.length);
        assertEquals(this.representation.genome.length, expected.length);
        for (int i = 0; i < expected.length; i++){
            assertEquals(expected[i], this.representation.genome[i]);
        }
    }
    
    @Test
    // tests conversion of binary numbers into integers
    public void testBinaryToInteger1(){
        this.representation.genome = new boolean[]{
                false, true, false, true, true, true,
                true, false,
                true, true, true, true,
                true, true, true, true, true, false, true, false, false
        };
        double[] expected = new double[] {23, 2, 15, 500};

        double[] result = this.representation.binaryToInteger();

        assertEquals(MathUtils.sumSizes(this.descriptor.variablesLengths), this.representation.genome.length);
        assertEquals(expected.length, result.length);

        Assert.assertArrayEquals(expected, result, 0.0);
    }
}
