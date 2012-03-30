package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Number of variables: n = 2.
 * Search domain: -4.5 <= xi <= 4.5, i = 1, 2.
 * The global minimum: x* = (3, 0.5), f(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page288.htm
 */
@Component(name="Beale Function")
public class BealeFunction implements Function, FunctionBounds, FunctionGradient, FunctionHessian {

  public double valueAt(Point point) {
    double[] ax = point.toArray();
    return (1.5 - ax[0] * (1 - ax[1])) * (1.5 - ax[0] * (1 - ax[1])) +
           (2.25 - ax[0] * (1 - ax[1] * ax[1])) * (2.25 - ax[0] * (1 - ax[1] * ax[1])) +
           (2.625 - ax[0] * (1 - ax[1] * ax[1] * ax[1])) * (2.625 - ax[0] * (1 - ax[1] * ax[1] * ax[1]));
  }

  public int getDimension() {
    return 2;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];
    gradient[0] = (-1. + ax[1]) * (12.75 + 9.75 * ax[1] + 5.25 * ax[1] * ax[1] + 2. * ax[0] * (-1. + ax[1]) * (2.09007 + 0.275136 * ax[1] + ax[1] * ax[1]) * (1.43536 + 1.72486 * ax[1] + ax[1] * ax[1]));
    gradient[1] = ax[0] * (3. + 9. * ax[1] + 15.75 * ax[1] * ax[1] + ax[0] * (-2 - 2 * ax[1] - 6 * ax[1] * ax[1] + 4 * ax[1] * ax[1] * ax[1] + 6 * ax[1] * ax[1] * ax[1] * ax[1] * ax[1]));
    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];
    hessian[0][0] = 2 * (3 - 2 * ax[1] - ax[1] * ax[1] - 2 * ax[1] * ax[1] * ax[1] + ax[1] * ax[1] * ax[1] * ax[1] + ax[1] * ax[1] * ax[1] * ax[1] * ax[1] * ax[1]);
    hessian[0][1] = 3. + 9. * ax[1] + 15.75 * ax[1] * ax[1] + ax[0] * (-4. - 4. * ax[1] - 12. * ax[1] * ax[1] + 8. * ax[1] * ax[1] * ax[1] + 12. * ax[1] * ax[1] * ax[1] * ax[1] * ax[1]);
    hessian[1][0] = hessian[0][1];
    hessian[1][1] = ax[0] * (9. + 31.5 * ax[1] + ax[0] * (-2. - 12. * ax[1] + 12. * ax[1] * ax[1] + 30. * ax[1] * ax[1] * ax[1] * ax[1]));
    return Hessian.valueOf(hessian);
  }

  public double[] getMinimum() {
    return new double[] {-4.5, -4.5};
  }

  public double[] getMaximum() {
    return new double[] {4.5, 4.5};
  }
}