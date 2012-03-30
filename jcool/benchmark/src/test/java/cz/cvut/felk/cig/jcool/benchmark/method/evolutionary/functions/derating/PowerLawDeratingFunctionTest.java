package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.derating;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 16.4.2011
 * Time: 19:47
 * Tests basic settings of derating function.
 */
public class PowerLawDeratingFunctionTest extends TestCase {

    @Test
    public void testGetDeratedMultiplier1() throws Exception {
        PowerLawDeratingFunction function = new PowerLawDeratingFunction();
        double radius = 1.0;
        double alpha = 2;
        function.setRadius(radius);
        function.setAlpha(alpha);

        assertEquals(0.0, function.getDeratedMultiplier(0.0));
        assertEquals(1.0, function.getDeratedMultiplier(radius));
        assertEquals(0.09, function.getDeratedMultiplier(0.3));
        assertEquals(0.25, function.getDeratedMultiplier(0.5));
        assertEquals(0.49, function.getDeratedMultiplier(0.7), 10E-3);
        assertEquals(0.81, function.getDeratedMultiplier(0.9));
    }
}
