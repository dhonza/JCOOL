/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.FunctionGradient;
import cz.cvut.felk.cig.jcool.core.FunctionHessian;
import cz.cvut.felk.cig.jcool.core.Gradient;
import cz.cvut.felk.cig.jcool.core.Hessian;
import cz.cvut.felk.cig.jcool.core.Point;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * @author ytoh
 */
@Component(name="Rosenbrock's Function")
public class RosenbrockFunction implements Function, FunctionGradient, FunctionHessian {

  @Property(name = "Number of variables n =")
  @Range(from = 2, to = Integer.MAX_VALUE)
  private int n = 2;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    /*double t1 = ax[1] - ax[0] * ax[0];
    double t2 = 1.0 - ax[0];
    return 100 * t1 * t1 + t2 * t2;*/

    double sum = 0.0;

    for (int i = 0; i < n - 1; i++)
      sum += 100 * (ax[i + 1] - ax[i] * ax[i]) * (ax[i + 1] - ax[i] * ax[i]) + (1 - ax[i]) * (1 - ax[i]);

    return sum;
  }

  public int getDimension() {
    return n;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];

    /*double t1 = ax[1] - ax[0] * ax[0];
    gradient[0] = -400.0 * ax[0] * t1 - 2 * (1 - ax[0]);
    gradient[1] = 200 * t1;*/

    gradient[0] = -2 * (1 - ax[0]) - 400 * ax[0] * (ax[1] - ax[0] * ax[0]);

    if (n > 2)
      for (int i = 1; i < n - 1; i++)
        gradient[i] = -2 * (1 - ax[i]) + 200 * (ax[i] - ax[i - 1] * ax[i - 1]) - 400 * ax[i] * (ax[i + 1] - ax[i] * ax[i]);

    gradient[n - 1] = 200 * (ax[n - 1] - ax[n - 2] * ax[n - 2]);

    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];
    /*hessian[0][0] = 1200.0 * ax[0] * ax[0] - 400.0 * ax[1] + 2;
    hessian[0][1] = -400.0 * ax[0];
    hessian[1][0] = hessian[0][1];
    hessian[1][1] = 200.0;*/

    hessian[0][0] = 2 + 1200 * ax[0] * ax[0] - 400 * ax[1];
    hessian[0][1] = -400 * ax[0];

    if (n > 2)
      for (int i = 1; i < n - 1; i++) {
        hessian[i][i - 1] = -400 * ax[i - 1];
        hessian[i][i] = 202 + 1200 * ax[i] * ax[i] - 400 * ax[i + 1];
        hessian[i][i + 1] = -400 * ax[i];
      }

    hessian[n - 1][n - 2] = -400 * ax[n - 2];
    hessian[n - 1][n - 1] = 200;

    return Hessian.valueOf(hessian);
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
  }
}
