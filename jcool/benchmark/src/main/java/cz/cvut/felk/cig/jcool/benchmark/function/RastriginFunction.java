package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Number of variables: n.
 * Search domain: -5.12 <= xi <= 5.12, i = 1, 2, . . . , n.
 * Number of local minima: several local minima.
 * The global minima: x* = (0, ..., 0), f(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page2607.htm
 */
@Component(name="Rastrigin Function")
public class RastriginFunction implements Function, FunctionGradient, FunctionHessian {

  @Property(name = "Number of variables n =")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int n = 2;

 @Property(name = "Parameter A = ")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double parameterA = 10.0;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum = 0.0;

    for (int i = 0; i < n; i++)
      sum += ax[i] * ax[i] - parameterA * Math.cos(2 * Math.PI * ax[i]);

    return parameterA * n + sum;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];

    for (int i = 0; i < n; i++)
      gradient[i] = 2 * ax[i] + 2 * parameterA * Math.PI * Math.sin(2 * Math.PI * ax[i]);

    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];

    for (int i = 0; i < n; i++)
      hessian[i][i] = 2 + 4 * parameterA * Math.PI * Math.PI * Math.cos(2 * Math.PI * ax[i]);

    return Hessian.valueOf(hessian);
  }

  public int getDimension() {
    return n;
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
  }

  public double getParameterA() {
    return parameterA;
  }

  public void setParameterA(double parameterA) {
    this.parameterA = parameterA;
  }
}