package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.PhenotypeRepresentation;
//import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 14.2.2011
 * Time: 17:33:02
 * Tests for SimplePhenotypeRepresentation
 */
public class SimplePhenotypeRepresentationTest extends TestCase {

    protected PhenotypeRepresentation representation;

    @Before
    protected void setUp(){
        representation = new SimplePhenotypeRepresentation();
    }

    @Test
    // test that arrays are equal but not the same
    public void testGetDoubleValue() throws Exception {
        double[] input = new double[]{1.2, 4.8, 6.3, 9.9, 10E18};
        this.representation.setDoubleValue(input);
        double[] output = this.representation.getDoubleValue();
        Assert.assertArrayEquals(input, output, 0.0d);
        Assert.assertNotSame(input, output);
    }
}
