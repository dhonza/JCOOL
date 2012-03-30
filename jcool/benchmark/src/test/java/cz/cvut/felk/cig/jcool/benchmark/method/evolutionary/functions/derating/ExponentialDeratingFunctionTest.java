package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.derating;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 16.4.2011
 * Time: 19:48
 * Tests basic settings of derating function.
 */
public class ExponentialDeratingFunctionTest extends TestCase {

    @Test
    public void testGetDeratedMultiplier() throws Exception {
        ExponentialDeratingFunction function = new ExponentialDeratingFunction();
        double minimumDeratingValue = 0.01;
        double radius = 1.0;
        function.setMinimumDeratingValue(minimumDeratingValue);
        function.setRadius(radius);

        assertEquals(minimumDeratingValue, function.getDeratedMultiplier(0.0), 10E-10);
        assertEquals(0.0158, function.getDeratedMultiplier(0.1), 10E-5);
        assertEquals(0.0398, function.getDeratedMultiplier(0.3), 10E-5);
        assertEquals(0.1, function.getDeratedMultiplier(0.5), 10E-5);
        assertEquals(0.251, function.getDeratedMultiplier(0.7), 10E-4);
        assertEquals(0.631, function.getDeratedMultiplier(0.9), 10E-5);
        assertEquals(1.0, function.getDeratedMultiplier(radius), 0.0);
        assertEquals(1.0, function.getDeratedMultiplier(2 * radius), 0.0);
    }
}
