package cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch;

import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.ObjectiveFunctions;
import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.Point;

/**
 * User: drchaj1
 * Date: 17.2.2007
 * Time: 20:09:59
 */
public class LineSearchBrentNoDerivatives extends LineSearchBrent {
    // Brent's method
    final private double brentGoldRatio = 0.3819660;

    public LineSearchBrentNoDerivatives(ObjectiveFunction afunc) {
        super(afunc);
    }

    //Brent method does not compute gradient in f(alpha) so we must get it separately
    public double[] getGAlpha() {
        return func.gradientAt(Point.at(xAlpha)).toArray();
    }

    public double minimize(double ax0[], double[] adir, final double afx0, double[] agx0) throws LineSearchException {
        double ret = minimize(ax0, adir, afx0);
        double[] grad = func.gradientAt(Point.at(ax0)).toArray();
        System.arraycopy(grad, 0, agx0, 0, grad.length);
        return ret;
    }

    @Override
    public double minimize(double ax0[], double[] adir, final double afx0) throws LineSearchException {
        xAlpha = ax0;
        ax = 0.0;
        bx = initAlpha;
        minimumBracketing(ax0, adir, afx0);

        brentMethod(ax0, adir);

        //TODO make better (recomputation of xAlpha)
        for (int i = 0; i < n; i++) {
            xAlpha[i] = ax0[i] + alpha * adir[i];
        }
        return fAlpha;
    }

    @Override
    public double minimize(double ax0[], double[] adir) throws LineSearchException {
        return minimize(ax0, adir, func.valueAt(Point.at(ax0)));
    }

    private void brentMethod(double[] ax0, double[] adir) throws LineSearchException {
        double a, b, etemp, fu, fv, fw, fx, p1, q1, r1, tol1, tol2, u, v, w, x, xm;
        double e = 0.0;
        double d = 0.0;

        a = (ax < cx ? ax : cx);
        b = (ax > cx ? ax : cx);
        x = w = v = bx;
        fw = fv = fx = ObjectiveFunctions.evaluateFunctionAlongDirection(func, ax0, adir, x);
        for (int iter = 0; iter < brentMaxIterations; iter++) {
            xm = 0.5 * (a + b);
            tol2 = 2.0 * (tol1 = tolerance * Math.abs(x) + brentEpsilon);
            if (Math.abs(x - xm) <= (tol2 - 0.5 * (b - a))) {
                alpha = x;
                fAlpha = fx;
                return;
            }

            if (Math.abs(e) > tol1) {
                r1 = (x - w) * (fx - fv);
                q1 = (x - v) * (fx - fw);
                p1 = (x - v) * q1 - (x - w) * r1;
                q1 = 2.0 * (q1 - r1);
                if (q1 > 0.0) p1 = -p1;
                q1 = Math.abs(q1);
                etemp = e;
                e = d;
                if (Math.abs(p1) >= Math.abs(0.5 * q1 * etemp) || p1 <= q1 * (a - x) || p1 >= q1 * (b - x)) {
                    d = brentGoldRatio * (e = (x >= xm ? a - x : b - x));
                } else {
                    d = p1 / q1;
                    u = x + d;
                    if (u - a < tol2 || b - u < tol2) {
                        if (xm >= x) {
                            d = Math.abs(tol1);
                        } else {
                            d = -Math.abs(tol1);
                        }
                    }
                }
            } else {
                d = brentGoldRatio * (e = (x >= xm ? a - x : b - x));
            }
            if (d >= 0.0) {
                u = (Math.abs(d) >= tol1 ? x + d : x + Math.abs(tol1));
            } else {
                u = (Math.abs(d) >= tol1 ? x + d : x - Math.abs(tol1));
            }

            fu = ObjectiveFunctions.evaluateFunctionAlongDirection(func, ax0, adir, u);
            if (fu <= fx) {
                if (u >= x) {
                    a = x;
                } else {
                    b = x;
                }
                v = w;
                w = x;
                x = u;

                fv = fw;
                fw = fx;
                fx = fu;
            } else {
                if (u < x) {
                    a = u;
                } else {
                    b = u;
                }
                if (fu <= fw || w == x) {
                    v = w;
                    w = u;
                    fv = fw;
                    fw = fu;
                } else if (fu <= fv || v == x || v == w) {
                    v = u;
                    fv = fu;
                }
            }
        }
        throw new LineSearchException("brentMethod: Too many iterations.");
    }
}
