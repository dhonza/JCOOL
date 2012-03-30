package cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch;

import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;

/**
 * User: drchaj1
 * Date: 17.2.2007
 * Time: 20:09:59
 */
public abstract class LineSearchBrent extends LineSearch {
    // bracketing
    private double goldRatio = 1.618034;
    private double goldLimit = 1.0e2;
    private double tiny = MachineAccuracy.EPSILON;

    protected double ax;
    protected double bx;
    protected double cx;

    // Brent's method
    protected double brentMaxIterations = 300;
    protected double brentEpsilon = 1.0e-10;

    protected double tolerance = MachineAccuracy.SQRT_EPSILON;

    public LineSearchBrent(ObjectiveFunction afunc) {
        super(afunc);
    }

    protected void minimumBracketing(double ax0[], double[] adir, double afx0) throws LineSearchException {
        double ulim, u, r, q, fu;

        double fb = ObjectiveFunctions.evaluateFunctionAlongDirection(func, ax0, adir, bx);

        if (fb > afx0) {
            double dum = ax;
            ax = bx;
            bx = dum;
            dum = fb;
            fb = afx0;
            afx0 = dum;
        }

        cx = bx + goldRatio * (bx - ax);
        double fc = ObjectiveFunctions.evaluateFunctionAlongDirection(func, ax0, adir, cx);

        double qmr;
        while (fb > fc) {
            r = (bx - ax) * (fb - fc);
            q = (bx - cx) * (fb - afx0);

            qmr = q - r;
            if (qmr >= 0.0) {
                u = bx - ((bx - cx) * q - (bx - ax) * r) / (2.0 * Math.max(Math.abs(qmr), tiny));
            } else {
                u = bx - ((bx - cx) * q - (bx - ax) * r) / (-2.0 * Math.max(Math.abs(qmr), tiny));
            }

            ulim = bx + goldLimit * (cx - bx);
            if ((bx - u) * (u - cx) > 0.0) {
                fu = ObjectiveFunctions.evaluateFunctionAlongDirection(func, ax0, adir, u);
                if (fu < fc) {
                    ax = bx;
                    bx = u;
//                    ofx0 = fb;
//                    fb = fu;
                    return;
                } else if (fu > fb) {
                    cx = u;
//                    fc = fu;
                    return;
                }
                u = cx + goldRatio * (cx - bx);
                fu = ObjectiveFunctions.evaluateFunctionAlongDirection(func, ax0, adir, u);
            } else if ((cx - u) * (u - ulim) > 0.0) {
                fu = ObjectiveFunctions.evaluateFunctionAlongDirection(func, ax0, adir, u);
                if (fu < fc) {
                    bx = cx;
                    cx = u;
                    u = cx + goldRatio * (cx - bx);

                    fb = fc;
                    fc = fu;
                    fu = ObjectiveFunctions.evaluateFunctionAlongDirection(func, ax0, adir, u);
                }
            } else if ((u - ulim) * (ulim - cx) >= 0.0) {
                u = ulim;
                fu = ObjectiveFunctions.evaluateFunctionAlongDirection(func, ax0, adir, u);
            } else {
                u = cx + goldRatio * (cx - bx);
                fu = ObjectiveFunctions.evaluateFunctionAlongDirection(func, ax0, adir, u);
            }
            ax = bx;
            bx = cx;
            cx = u;

            afx0 = fb;
            fb = fc;
            fc = fu;
        }
    }
}
