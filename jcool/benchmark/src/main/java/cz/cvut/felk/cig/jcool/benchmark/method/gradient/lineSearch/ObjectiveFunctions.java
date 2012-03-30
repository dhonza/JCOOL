package cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch;

import cz.cvut.felk.cig.jcool.core.Gradient;
import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.Point;

/**
 * User: drchaj1
 * Date: 17.2.2007
 * Time: 20:22:03
 */

/**
 * This class contains helper functions for ObjectiveFunction.
 */
public class ObjectiveFunctions {
    //TODO create method which evaluates function and its derivation together

    /**
     * Evaluates a multivariate function along given direction.
     *
     * @param afunc  funcition
     * @param ax     starting point
     * @param adir   direction
     * @param aalpha distance
     * @return functional value along direction
     */
    public static double evaluateFunctionAlongDirection(final ObjectiveFunction afunc, final double[] ax, final double[] adir, final double aalpha) {
        int n = afunc.getDimension();
        double[] argNew = new double[n];
        for (int j = 0; j < n; j++) {
            argNew[j] = ax[j] + aalpha * adir[j];
        }
        return afunc.valueAt(Point.at(argNew));
    }

    /**
     * Evaluates a multivariate function's gradient and derivation along given direction.
     *
     * @param afunc   function
     * @param ax      starting point
     * @param adir    direction
     * @param aalpha  distance
     * @param agAlpha gradient vector along direction
     * @return derivation along direction
     */
    public static double evaluateDerivativeAlongDirection(final ObjectiveFunction afunc, final double[] ax, final double[] adir, final double aalpha, double[] agAlpha) {
        int n = afunc.getDimension();
        int i;
        double df1 = 0.0;
        double[] xt = new double[n];

        for (i = 0; i < n; i++) {
            xt[i] = ax[i] + aalpha * adir[i];
        }

        Gradient g = afunc.gradientAt(Point.at(xt));
        //agAlpha = g.toArray();
        System.arraycopy(g.toArray(), 0, agAlpha, 0 , n);

        for (i = 0; i < n; i++) {
            df1 += agAlpha[i] * adir[i];
        }
        return df1;
    }
}
