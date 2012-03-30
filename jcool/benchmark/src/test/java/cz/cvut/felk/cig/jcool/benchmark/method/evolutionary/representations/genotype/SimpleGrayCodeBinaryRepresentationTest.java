package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 12.3.2011
 * Time: 15:30
 * Tests conversion between binary and Gray coding.
 */
public class SimpleGrayCodeBinaryRepresentationTest extends TestCase {

    protected SimpleGrayCodeBinaryRepresentation representation;
    protected SimpleBinaryGenomeDescriptor descriptor;

    @Before
    public void setUp() throws Exception {
        this.descriptor = new SimpleBinaryGenomeDescriptor();
        this.descriptor.setNumVariables(4);
        this.descriptor.setVariablesLengths(new int[]{6, 2, 4, 10}); // 22 values
        this.representation = new SimpleGrayCodeBinaryRepresentation(this.descriptor);
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
        this.representation.genome = gray;
        boolean[] result = this.representation.decodeGenomeToBinary();

        assertEquals((6+2+4+10), result.length);
        boolean[] binary = new boolean[]{
                true, false, true, false, true, false,
                true, false,
                true, true, true, true,
                false, false, false, true, true, false, false, true, true, false
        };
        assertEquals(binary.length, result.length);
        // Assert does not have assertArrayEquals for boolean[]...
        for (int i = 0; i < binary.length; i++){
            Assert.assertEquals("in step " + i, binary[i], result[i]);
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
        this.representation.genome = binary;
        boolean[] gray = new boolean[]{
                true, true, true, true, true, true,
                true, true,
                true, false, false, false,
                false, false, false, true, false, true, false, true, false, true
        };

        this.representation.encodeBinaryGenome();
        boolean[] result = this.representation.genome;

        assertEquals((6+2+4+10), result.length);
        assertEquals(binary.length, result.length);
        assertEquals(binary.length, gray.length);
        // Assert does not have assertArrayEquals for boolean[]...
        for (int i = 0; i < binary.length; i++){
            Assert.assertEquals("in step " + i, gray[i], result[i]);
        }
    }
}
