package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Number of variables: n = 2.
 * Search domain: -10 <= xi <= 10, i = 1, 2.
 * Number of local minima: no local minima except the global one.
 * The global minima: x* = (0, 0), f(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page2213.htm
 */
@Component(name = "Matyas Function")
public class MatyasFunction implements Function, FunctionBounds, FunctionGradient, FunctionHessian {

  public double valueAt(Point point) {
    double[] ax = point.toArray();
    return 0.26 * (ax[0] * ax[0] + ax[1] * ax[1]) - 0.48 * ax[0] * ax[1];
  }

  public int getDimension() {
    return 2;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];
    gradient[0] = 0.52 * ax[0] - 0.48 * ax[1];
    gradient[1] = -0.48 * ax[0] + 0.52 * ax[1];
    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];
    hessian[0][0] = 0.52;
    hessian[0][1] = -0.48;
    hessian[1][0] = hessian[0][1];
    hessian[1][1] = hessian[0][0];
    return Hessian.valueOf(hessian);
  }

  public double[] getMinimum() {
    return new double[]{-10, -10};
  }

  public double[] getMaximum() {
    return new double[]{10, 10};
  }
}