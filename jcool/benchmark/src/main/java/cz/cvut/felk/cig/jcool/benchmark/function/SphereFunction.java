package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * De Jong's Function
 * Number of variables: n.
 * Search domain: -5.12 <= xi <= 5.12, i = 1, 2, . . . , n.
 * Number of local minima: no local minimum except the global one.
 * The global minima: x* =  (0, ..., 0), f(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page1113.htm
 */
@Component(name="Sphere Function")
public class SphereFunction implements Function, FunctionGradient, FunctionHessian {

  @Property(name = "Number of variables n =")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int n = 2;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum = 0.0;

    for (int i = 0; i < n; i++)
      sum += ax[i] * ax[i];

    return sum;
  }

  public int getDimension() {
    return n;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[n];

    for (int i = 0; i < n; i++)
      gradient[i] = 2 * ax[i];

    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];

    for (int i = 0; i < n; i++)
      hessian[i][i] = 2;
    return Hessian.valueOf(hessian);
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
  }
}