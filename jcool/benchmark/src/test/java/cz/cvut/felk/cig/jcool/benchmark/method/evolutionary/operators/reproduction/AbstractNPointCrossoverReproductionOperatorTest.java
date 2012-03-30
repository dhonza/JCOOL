package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.util.SimpleRandomGenerator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.3.2011
 * Time: 20:10
 * Tests makeCutPointFlags
 */
public class AbstractNPointCrossoverReproductionOperatorTest extends TestCase {

    protected AbstractNPointCrossoverReproductionOperator operator;
    
    @Before
    public void setUp() throws Exception {
        this.operator = new AbstractNPointCrossoverReproductionOperator(){
            @Override
            protected void reproduceInternal(Individual[] firstChildren, Individual[] secondChildren) {
            }
            public Class<? extends Representation> getAcceptableType() {
                return null;
            }
        };
        this.operator.setRandomGenerator(new SimpleRandomGenerator());
    }

    @Test
    // test some trivial cases of cut points and function dimension
    public void testMakeCutPointFlags1() throws Exception {
        for (int cutPoints = 1; cutPoints < 10; cutPoints++){
            boolean[] flags = this.operator.makeCutPointFlags(cutPoints,cutPoints+1);
            int flips = countFlips(flags);
            assertTrue(flips == cutPoints);
        }
    }

    @Test
    // test nontrivial cases of cut points and function dimension
    public void testMakeCutPointFlags2() throws Exception {
        for (int cutPoints = 1; cutPoints < 10; cutPoints++){
            int totalLength = cutPoints * 4;
            boolean[] flags = this.operator.makeCutPointFlags(cutPoints, totalLength);
            int flips = countFlips(flags);
            assertEquals(cutPoints, flips);
        }
    }

    /**
     * Counts how many times there is change in given flags array.
     * @param flags - array of true/false flags
     * @return total count of flips
     */
    protected int countFlips(boolean[] flags){
        int flips = 0;
        for (int idx = 1; idx < flags.length; idx++){
            if (flags[idx] != flags[idx-1]){
                flips++;
            }
        }
        return flips;
    }
}
