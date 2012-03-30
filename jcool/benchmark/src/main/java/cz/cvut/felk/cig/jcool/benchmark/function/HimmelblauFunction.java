package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * De Jong's Function
 * Number of variables: n.
 * Search domain: -6 <= xi <= 6, i = 1, 2.
 * The global minima: f(x*) = 0,
 * x* = (3, 2)
 * x* = (-2.805118, 3.131312)
 * x* = (-3.779310, -3.283185)
 * x* = (3.584428, -1.848126)
 * http://www.it.lut.fi/ip/evo/functions/node25.html
 */
@Component(name = "Himmelblau Function")
public class HimmelblauFunction implements Function, FunctionBounds, FunctionGradient, FunctionHessian {

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    return (ax[0] * ax[0] + ax[1] - 11) * (ax[0] * ax[0] + ax[1] - 11) +
        (ax[0] + ax[1] * ax[1] - 7) * (ax[0] + ax[1] * ax[1] - 7);
  }

  public int getDimension() {
    return 2;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];

    gradient[0] = 2 * (-7 + ax[0] + ax[1] * ax[1] + 2 * ax[0] * (-11 + ax[0] * ax[0] + ax[1]));
    gradient[1] = 2 * (-11 + ax[0] * ax[0] + ax[1] + 2 * ax[1] * (-7 + ax[0] + ax[1] * ax[1]));

    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];

    hessian[0][0] = -42 + 12 * ax[0] * ax[0] + 4 * ax[1];
    hessian[0][1] = 4 * (ax[0] + ax[1]);
    hessian[1][0] = hessian[0][1];
    hessian[1][1] = -26 + 4 * ax[0] + 12 * ax[1] * ax[1];
    return Hessian.valueOf(hessian);
  }

  public double[] getMinimum() {
    return new double[]{-6.0, -6.0};
  }

  public double[] getMaximum() {
    return new double[]{6.0, 6.0};
  }
}