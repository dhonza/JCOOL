package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;

/**
 * Number of variables: n.
 * Search domain: -n^2 <= xi <= n^2, i = 1, 2, . . . , n.
 * Number of local minima: no local minimum except the global one.
 * The global minima: f(x*) = -2  for n=2,
 *                    f(x*) = -50  for n=6,
 *                    f(x*) = -200 for n=10.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page2904.htm
 */
@Component(name="Trid Function")
public class TridFunction implements Function, FunctionBounds, FunctionGradient, FunctionHessian {

  @Property(name = "Number of variables n =")
  @Range(from = 2, to = Integer.MAX_VALUE)
  private int n = 2;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum1 = 0.0;
    double sum2 = 0.0;

    for (int i = 0; i < n; i++)
      sum1 += (ax[i] - 1) * (ax[i] - 1);

    for (int i = 1; i < n; i++)
      sum2 += ax[i] * ax[i - 1];

    return sum1 - sum2;
  }

  public int getDimension() {
    return n;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[n];

    gradient[0] = -2 + 2 * ax[0] - ax[1];

    if (n > 2)
      for (int i = 1; i < n - 1; i++)
        gradient[i] = -2 - ax[i - 1] + 2 * ax[i] - ax[i + 1];

    gradient[n - 1] = -2 - ax[n - 2] + 2 * ax[n - 1];

    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];

    hessian[0][0] = 2;
    hessian[0][1] = -1;

    if (n > 2)
      for (int i = 1; i < n - 1; i++) {
        hessian[i][i - 1] = -1;
        hessian[i][i] = 2;
        hessian[i][i + 1] = -1;
      }

    hessian[n - 1][n - 2] = -1;
    hessian[n - 1][n - 1] = 2;

    return Hessian.valueOf(hessian);
  }

  public double[] getMinimum() {
    double[] min = new double[n];
    Arrays.fill(min, -n * n);
    return min;
  }

  public double[] getMaximum() {
    double[] max = new double[n];
    Arrays.fill(max, n * n);
    return max;
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
  }
}