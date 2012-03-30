package cz.cvut.felk.cig.jcool.benchmark.util;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 14.2.2011
 * Time: 16:27:00
 */
public class MathUtilsTest extends TestCase {

    @Test
    // tests sumSizes(int[])
    public void testSumSizes1() throws Exception {
        int[] sizes = new int[]{2, 5, 7, 9, 3};
        int sum = MathUtils.sumSizes(sizes);
        Assert.assertTrue(sum == (2 + 5 + 7 + 9 + 3));
    }

    @Test
    // tests sumSizes(int[], int)
    public void testSumSizes2() throws Exception {
        int[] sizes = new int[]{2, 5, 7, 9, 3};
        int sum = MathUtils.sumSizes(sizes, 2);
        Assert.assertTrue(sum == (2 + 5));
    }
}
