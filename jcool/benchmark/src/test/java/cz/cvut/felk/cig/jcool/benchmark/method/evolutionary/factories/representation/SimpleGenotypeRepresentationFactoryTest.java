package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.representation;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype.SimpleBinaryGenomeDescriptor;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.representation.AbstractGenotypeRepresentationFactory.*;
import cz.cvut.felk.cig.jcool.benchmark.util.EmptyObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 11.3.2011
 * Time: 20:42
 * Tests correct fixed length and fixed precision.
 */
public class SimpleGenotypeRepresentationFactoryTest extends TestCase {

    protected ObjectiveFunction function;
    protected SimpleGenotypeRepresentationFactory factory;

    @Before
    public void setUp() throws Exception {
        function = new EmptyObjectiveFunction(){

            @Override
            public int getDimension() {
                return 3;
            }

            @Override
            public double[] getMinimum() {
                return new double[]{-5.0, 0.0, -20e100};
            }

            @Override
            public double[] getMaximum() {
                return new double[]{5.0, 10e10, 50e100};
            }
        };

        this.factory = new SimpleGenotypeRepresentationFactory();
    }

    @Test
    // tests fixed length
    public void testSetFunction1() throws Exception {
        this.factory.setEncodingType(EncodingType.FixedLength);
        this.factory.setFixedPrecision(0.1); // dummy on purpose
        this.factory.setFixedLength(16);
        this.factory.setFunction(this.function);
        SimpleBinaryGenomeDescriptor descriptor = this.factory.descriptor;

        assertEquals(function.getDimension(), descriptor.getNumVariables());
        Assert.assertArrayEquals(function.getMinimum(), descriptor.getVariablesLowerBounds(), 0.0);
        Assert.assertArrayEquals(new int[]{16, 16, 16}, descriptor.getVariablesLengths());
        for (int i = 0; i < 3; i++){
            assertTrue(this.function.getMaximum()[i] <= Math.pow(2, 16)*descriptor.getVariablesPrecisions()[i]);
        }
        assertEquals(0.000152590219, descriptor.getVariablesPrecisions()[0], 0.000000000001);
        assertEquals(1525902.19, descriptor.getVariablesPrecisions()[1], 0.01);
        assertEquals(1.068131E97, descriptor.getVariablesPrecisions()[2], 0.0001E97);
    }

    @Test
    // tests fixed precision
    public void testSetFunction2() throws Exception {
        this.factory.setEncodingType(EncodingType.FixedPrecision);
        this.factory.setFixedPrecision(1.0);
        this.factory.setFixedLength(16); // dummy on purpose
        this.factory.setFunction(this.function);
        SimpleBinaryGenomeDescriptor descriptor = this.factory.descriptor;

        assertEquals(function.getDimension(), descriptor.getNumVariables());
        Assert.assertArrayEquals(function.getMinimum(), descriptor.getVariablesLowerBounds(), 0.0);
        Assert.assertArrayEquals(new int[]{4, 37, 339}, descriptor.getVariablesLengths());
        Assert.assertArrayEquals(new double[]{1, 1, 1}, descriptor.getVariablesPrecisions(), 0.00000000001);
    }
}
