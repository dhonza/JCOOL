package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Number of variables: n = 2.
 * Search domain: -5 <= xi <= 5, i = 1, 2.
 * Number of local minima: no local minimum except the global ones.
 * The global minima: x* = (0.0898, -0.7126), (-0.0898, 0.7126), f(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page1621.htm
 */
@Component(name = "Hump Function")
public class HumpFunction implements Function, FunctionBounds, FunctionGradient, FunctionHessian {

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    return 1.0316285 + (4 - 2.1 * ax[0] * ax[0] + Math.pow(ax[0], 4.0 / 3.0)) * ax[0] * ax[0] + ax[0] * ax[1] + (-4 + 4 * ax[1] * ax[1]) * ax[1] * ax[1];
  }

  public int getDimension() {
    return 2;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];

    gradient[0] = 8.0 * ax[0] + 10.0 / 3 * Math.pow(ax[0], 7.0 / 3.0) - 8.4 * ax[0] * ax[0] * ax[0] + ax[1];
    gradient[1] = ax[0] - 8 * ax[1] + 16 * ax[1] * ax[1] * ax[1];

    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];

    hessian[0][0] = 8. + 70.0 / 9.0 * Math.pow(ax[0], 4.0 / 3.0) - 25.2 * ax[0] * ax[0];
    hessian[0][1] = 1.0;
    hessian[1][0] = hessian[0][1];
    hessian[1][1] = -8 + 48 * ax[1] * ax[1];

    return Hessian.valueOf(hessian);
  }

  public double[] getMinimum() {
    return new double[]{-5.0, -5.0};
  }

  public double[] getMaximum() {
    return new double[]{5.0, 5.0};
  }
}