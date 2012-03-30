package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Number of variables: n = 2.
 * Search domain: -5 <= x1 <= 10, 0 <= x2 <= 15.
 * Number of local minima: no local minima except the global ones.
 * The global minima: x* = (-Pi, 12.275), (Pi, 2.275), (9.42478, 2.475), f(x*) = 0.397887.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page913.htm
 */
@Component(name="Branin's rcos Function")
public class BraninFunction implements Function, FunctionBounds, FunctionGradient, FunctionHessian {

  public double valueAt(Point point) {
    double[] ax = point.toArray();
    return (ax[1] - (5.1 / (4 * Math.PI * Math.PI)) * ax[0] * ax[0] + 5 * ax[0] / Math.PI - 6) *
           (ax[1] - (5.1 / (4 * Math.PI * Math.PI)) * ax[0] * ax[0] + 5 * ax[0] / Math.PI - 6) + 10 *
           (1 - 1 / (8 * Math.PI)) * Math.cos(ax[0]) + 10;
  }

  public int getDimension() {
    return 2;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];
    gradient[0] = -19.0986 - 1.23362 * ax[0] * ax[0] + 0.0667545 * ax[0] * ax[0] * ax[0] + ax[0] * (8.16649 - 0.516738 * ax[1]) + 3.1831 * ax[1] - 9.60211 * Math.sin(ax[0]);
    gradient[1] = 2 * (-6 + (5 * ax[0]) / Math.PI - 0.129185 * ax[0] * ax[0] + ax[1]);
    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];
    hessian[0][0] = 8.16649 - 2.46724 * ax[0] + 0.200264 * ax[0] * ax[0] - 0.516738 * ax[1] - 9.60211 * Math.cos(ax[0]);
    hessian[0][1] = 3.1831 - 0.516738 * ax[0];
    hessian[1][0] = hessian[0][1];
    hessian[1][1] = 2;
    return Hessian.valueOf(hessian);
  }

  public double[] getMinimum() {
    return new double[] {-5.0, 0.0};
  }

  public double[] getMaximum() {
    return new double[] {10.0, 15.0};
  }
}