package cz.cvut.felk.cig.jcool.utils;

import cz.cvut.felk.cig.jcool.core.ObjectiveFunctionFast;

/**
 * User: drchaj1
 * Date: 23.8.2008
 * Time: 17:44:00
 */

/**
 * This class performs actions connected with numerical differentiation. It implements
 * various methods which compute numerical gradient (central difference and forward difference),
 * and numerical hessian (TODO implement).
 * It also contains methods to check analytic vs. numerical gradient/hessian.
 */
public class NumericalDifferentiation {
    protected static double gradientCDStepMult = Math.pow(MachineAccuracy.EPSILON, 1.0 / 3.0);

    /**
     * Determine gradient numerically using central difference.
     * Needs 2*n function evaluations.
     *
     * @param afunc objective function
     * @param ax    argument vector
     * @param agrad vector for gradientCD
     */
    public static void gradientCD(final ObjectiveFunctionFast afunc, final double[] ax, final double[] agrad) {
        double oldx, h, fxplus, fxminus;
        for (int i = 0; i < afunc.getDim(); i++) {
            oldx = ax[i];
            h = gradientCDStepMult * Math.abs(oldx);
            if (h < gradientCDStepMult) {// TODO revise
                h = gradientCDStepMult;
            }

            ax[i] = oldx + h;
            fxplus = afunc.f(ax);
            ax[i] = oldx - h;
            fxminus = afunc.f(ax);
            ax[i] = oldx;

            // first derivative
            agrad[i] = (fxplus - fxminus) / (2.0 * h);
        }
    }

    /**
     * Determine gradient numerically using forward finite difference.
     * Needs n function evaluations.
     *
     * @param afunc objective function
     * @param ax    argument vector
     * @param afx   evaluated function value afunc(ox)
     * @param agrad vector for gradientCD
     */
    public static void gradientFD(final ObjectiveFunctionFast afunc, final double[] ax, final double afx, double[] agrad) {
        double oldx, h, fxplus;
        for (int i = 0; i < afunc.getDim(); i++) {
            oldx = ax[i];
            h = MachineAccuracy.SQRT_EPSILON * oldx;
            if (h < MachineAccuracy.SQRT_EPSILON) {// TODO revise
                h = MachineAccuracy.SQRT_EPSILON;
            }

            ax[i] = oldx + h;
            fxplus = afunc.f(ax);
            ax[i] = oldx;

            // first derivative
            agrad[i] = (fxplus - afx) / h;
        }
    }

    /**
     * Determine gradient numerically using forward finite difference.
     * Needs n function evaluations. This method computes function value in ax.
     *
     * @param afunc objective function
     * @param ax    argument vector
     * @param agrad vector for gradientCD
     */
    public static void gradientFD(final ObjectiveFunctionFast afunc, final double[] ax, double[] agrad) {
        gradientFD(afunc, ax, afunc.f(ax), agrad);
    }

    public static void hessian(final ObjectiveFunctionFast afunc, final double[] ax, double[][] ahess) {
//        int n = afunc.getDim();
//        double[] tgrad = new double[n];
//        gradientFD(afunc, ax, afunc.f(ax), tgrad);
//        hessian2(afunc, ax, tgrad, ahess);
        double fx = afunc.f(ax);
        hessianCD(afunc, ax, fx, ahess);
    }

    public static void hessian2(final ObjectiveFunctionFast afunc, final double[] ax, final double[] agrad, double[][] ahess) {
        double h, oldx;
        int i, j;
        int n = afunc.getDim();
        double[] tgrad = new double[n];
        for (j = 0; j < n; j++) {
            h = MachineAccuracy.SQRT_EPSILON * (Math.abs(ax[j]) + MachineAccuracy.SQRT_EPSILON);
            oldx = ax[j];
            ax[j] = oldx + h;
//            System.out.println("calling gradient!!!");
            afunc.grad(ax, tgrad);
            ax[j] = oldx;
            for (i = 0; i < n; i++) {
                ahess[i][j] = (tgrad[i] - agrad[i]) / h;
            }
        }
//        for (j = 0; j < n - 1; j++) {
//            for (i = j + 1; i < n; i++) {
//                ahess[i][j] = (ahess[i][j] + ahess[j][i]) / 2.0;
//            }
//        }
    }

    public static void hessianCD(final ObjectiveFunctionFast afunc, final double[] ax, final double afx, double[][] ahess) {
        int n = afunc.getDim();
        double[] h = new double[n];
        double xh, oldx, oldy;
        int i, j;
        double tol = Math.pow(MachineAccuracy.EPSILON, 0.25);//TODO revise maybe 1/3
        double[] fplus = new double[n];
        double[] fminus = new double[n];

        double fxx;

        for (i = 0; i < n; i++) {
            h[i] = tol * (Math.abs(ax[i]) + MachineAccuracy.EPSILON);
            xh = ax[i] + h[i];
            h[i] = xh - ax[i];
            oldx = ax[i];
            ax[i] = oldx + h[i];
            fplus[i] = afunc.f(ax);
            ax[i] = oldx - h[i];
            fminus[i] = afunc.f(ax);
            ax[i] = oldx;
        }

        double tH;
        for (i = 0; i < n; i++) {
            ahess[i][i] = h[i] * h[i];
            for (j = i + 1; j < n; j++) {
                tH = h[i] * h[j];
                ahess[i][j] = tH;
                ahess[j][i] = tH;
            }
        }

        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                if (i == j) {
                    ahess[i][j] = (fplus[i] + fminus[j] - 2 * afx) / ahess[i][j];
                } else {
                    oldx = ax[i];
                    oldy = ax[j];
                    ax[i] = oldx + h[i];
                    ax[j] = oldy - h[j];
                    fxx = afunc.f(ax);
                    ax[i] = oldx;
                    ax[j] = oldy;
                    ahess[i][j] = (fplus[i] + fminus[j] - afx - fxx) / ahess[i][j];
                }
            }
        }
        //H=(H+H')/2
        for (i = 0; i < n; i++) {
            for (j = i + 1; j < n; j++) {
                tH = (ahess[i][j] + ahess[j][i]) / 2.0;
                ahess[i][j] = tH;
                ahess[j][i] = tH;
            }
        }
    }

    /**
     * Checks if the analytic gradient corresponds with the numeric.
     * TODO not complete - implement thresholds etc.
     *
     * @param afunc objective function to check
     * @param ax    argument vector
     * @return true for correct analytic gradient, false for wrong
     */
    public static boolean checkAnalyticGradient(final ObjectiveFunctionFast afunc, final double[] ax) {
        if (!afunc.hasAnalyticGradient()) {
            throw new IllegalArgumentException("Function has no analytic gradient to check");
        }
        double[] ngrad = new double[afunc.getDim()];
        double[] ngrad2 = new double[afunc.getDim()];
        double[] agrad = new double[afunc.getDim()];

        gradientFD(afunc, ax, ngrad);
        gradientCD(afunc, ax, ngrad2);
        afunc.grad(ax, agrad);

        for (int i = 0; i < afunc.getDim(); i++) {
            System.out.println(i + ": " + " " + agrad[i] + " "
                    + ngrad[i] + " " + Math.abs(ngrad[i] - agrad[i]) + " "
                    + ngrad2[i] + " " + Math.abs(ngrad2[i] - agrad[i]));
        }

        boolean isOk = true;
        for (int i = 0; i < afunc.getDim(); i++) {
            if (Math.abs(agrad[i] - ngrad[i]) > 1) {
                isOk = false;
            }

        }
        return isOk;
    }

    /**
     * Checks if the analytic hessian corresponds with the numeric.
     * TODO not complete - implement thresholds etc.
     * @param afunc objective function to check
     * @param ax argument vector
     * @return true for correct analytic gradient, false for wrong
     */
    public static boolean checkAnalyticHessian(final ObjectiveFunctionFast afunc, final double[] ax) {
        if (!afunc.hasAnalyticHessian()) {
            throw new IllegalArgumentException("Function has no analytic hessian to check");
        }
        int n = afunc.getDim();
        double[][] nhess = new double[n][n];
        double[][] ahess = new double[n][n];
        hessian(afunc, ax, nhess);
        afunc.hess(ax, ahess);
        System.out.println("analytic hessian:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(ahess[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("numerical hessian:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(nhess[i][j] + " ");
            }
            System.out.println("");
        }
        return true;
    }

}
