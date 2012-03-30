package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype;

import cz.cvut.felk.cig.jcool.benchmark.util.MathUtils;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.BitSet;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 14.4.2011
 * Time: 11:52
 * Tests conversion between binary and Gray code and adds consistency test that sets double value and reads it afterwards.
 */
public class BitSetGrayCodeBinaryRepresentationTest extends TestCase {

    protected BitSetGrayCodeBinaryRepresentation representation;
    protected SimpleBinaryGenomeDescriptor descriptor;

    @Before
    public void setUp() throws Exception {
        this.descriptor = new SimpleBinaryGenomeDescriptor();
        this.descriptor.setNumVariables(4);
        this.descriptor.setVariablesLengths(new int[]{6, 2, 4, 10}); // 22 values
        this.representation = new BitSetGrayCodeBinaryRepresentation(this.descriptor);
    }

    @Test
    //tests correct conversion of multiple gray variables into binary notation
    public void testDecodeGenomeToBinary1(){
        boolean[] gray = new boolean[]{
                true, true, true, true, true, true,
                true, true,
                true, false, false, false,
                false, false, false, true, false, true, false, true, false, true
        };
        assertEquals((6+2+4+10), gray.length);
        for (int idx = 0; idx < gray.length; idx++){
            this.representation.setGeneAt(idx, gray[idx]);
        }
        assertEquals(gray.length, this.representation.genomeLength);
        BitSet result = this.representation.decodeGenomeToBinary();

        assertTrue((6+2+4+10) >=  result.length());
        boolean[] binary = new boolean[]{
                true, false, true, false, true, false,
                true, false,
                true, true, true, true,
                false, false, false, true, true, false, false, true, true, false
        };
        assertTrue(binary.length >=result.length());
        // Assert does not have assertArrayEquals for boolean[]...
        for (int i = 0; i < binary.length; i++){
            Assert.assertEquals("in step " + i, binary[i], result.get(i) );
        }
    }

    @Test
    public void testEncodeBinaryGenome1(){
        boolean[] binary = new boolean[]{
                true, false, true, false, true, false,
                true, false,
                true, true, true, true,
                false, false, false, true, true, false, false, true, true, false
        };
        for (int idx = 0; idx < binary.length; idx++){
            this.representation.setGeneAt(idx, binary[idx]);
        }
        boolean[] gray = new boolean[]{
                true, true, true, true, true, true,
                true, true,
                true, false, false, false,
                false, false, false, true, false, true, false, true, false, true
        };

        this.representation.encodeBinaryGenome();
        BitSet result = this.representation.genome;

        assertTrue((6+2+4+10) >= result.length());
        assertEquals(binary.length, gray.length);
        // Assert does not have assertArrayEquals for boolean[]...
        for (int i = 0; i < binary.length; i++){
            Assert.assertEquals("in step " + i, gray[i], result.get(i) );
        }
    }

    @Test
    // tests setting double value and reading it afterwards
    public void testGetDoubleValue1(){
        this.descriptor = new SimpleBinaryGenomeDescriptor();
        this.descriptor.setNumVariables(4);
        this.descriptor.setVariablesLengths(new int[]{6, 2, 4, 9});
        this.descriptor.setVariablesLowerBounds(new double[]{-10.0, 0.0, 10E10, 123456});
        this.descriptor.setVariablesPrecisions(new double[]{1.0, 0.5, 123.45, 0.001});
        this.representation = new BitSetGrayCodeBinaryRepresentation(this.descriptor);
        this.representation.setDoubleValue(new double[]{13.0, 1.0, 1851.75+10e10, 123456.5});

        double[] expected = new double[]{13.0, 1.0, 1851.75+10e10, 123456.5};
        double[] result = this.representation.getDoubleValue();
        assertEquals(expected.length, result.length);
        assertNotSame(expected, result);
        Assert.assertArrayEquals(expected, result, 0.0);
    }
}
