package cz.cvut.felk.cig.jcool.benchmark.util;

import cz.cvut.felk.cig.jcool.core.OptimizationException;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 14.2.2011
 * Time: 15:13:29
 */
public class MathUtils {

    /**
     * Counts sum of sizes given in input array.
     * @param sizes - array of non-negative sizes.
     * @return sum of numbers in sizes array.
     */
    public static int sumSizes(int[] sizes){
        if (sizes == null){
            throw new OptimizationException("array of sizes cannot be null");
        }
        return sumSizes(sizes, sizes.length);
    }

    /**
     * Counts sum of sizes given in input array restricted by given index.
     * @param sizes - array of non-negative sizes.
     * @param maxIndexExclusive - one index behind last index in "sizes" that will be taken into account.
     * @return sum of numbers in sizes array restricted by given index.
     */
    public static int sumSizes(int[] sizes, int maxIndexExclusive){
        if (sizes == null){
            throw new OptimizationException("array of sizes cannot be null");
        }
        if (maxIndexExclusive < 0){
            throw new OptimizationException("maxIndexExclusive has to be non-negative");
        }
        if (maxIndexExclusive > sizes.length){
            maxIndexExclusive = sizes.length;
        }
        int sum = 0;
        for (int i = 0; i < maxIndexExclusive; i++){
            if (sizes[i] < 0){
                throw new OptimizationException("sizes elements has to be non-negative");
            }
            sum += sizes[i];
        }
        return sum;
    }
}
