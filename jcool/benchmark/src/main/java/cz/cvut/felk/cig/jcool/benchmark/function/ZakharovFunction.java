package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;

/**
 * Number of variables: n.
 * Search domain: -5 <= xi <= 10, i = 1, 2, . . . , n.
 * Number of local minima: no local minimum except the global one.
 * The global minima: x* =  (0, ..., 0), Zn(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page3088.htm
 */
@Component(name="Zakharov Function")
public class ZakharovFunction implements Function, FunctionBounds, FunctionGradient, FunctionHessian {

  @Property(name = "Number of variables n =")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int n = 2;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum1 = 0.0;
    double sum2 = 0.0;

    for (int i = 0; i < n; i++) {
      sum1 += ax[i] * ax[i];
      sum2 += 0.5 * (i + 1) * ax[i];
    }

    return sum1 + sum2 * sum2 * (1 + sum2 * sum2);
  }

  public int getDimension() {
    return n;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[n];

    double sum = 0.0;
    for (int i = 0; i < n; i++)
      sum += (i + 1) * 0.5 * ax[i];

    for (int i = 0; i < n; i++)
      gradient[i] = 2 * ax[i] + (i + 1) * sum + (i + 1) * 2 * sum * sum * sum;

    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];

    double sum = 0.0;
    for (int i = 0; i < n; i++)
      sum += (i + 1) * 0.5 * ax[i];

    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++) {
        hessian[i][j] = 0.5 * (i + 1) * (j + 1) + 3 * (i + 1) * (j + 1) * sum * sum;
        if (i == j)
          hessian[i][j] += 2;
      }

    return Hessian.valueOf(hessian);
  }

  public double[] getMinimum() {
    double[] min = new double[n];
    Arrays.fill(min, -5.0);
    return min;
  }

  public double[] getMaximum() {
    double[] max = new double[n];
    Arrays.fill(max, 10.0);
    return max;
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
  }
}