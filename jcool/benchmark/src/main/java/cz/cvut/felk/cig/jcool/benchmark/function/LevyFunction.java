package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;

/**
 * Number of variables: n.
 * Search domain: -10 <= xi <= 10, i = 1, 2, . . . , n.
 * Number of local minima: several local minima.
 * The global minima: x* = (1, ..., 1), f(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page2056.htm
 */
@Component(name="Levy Function")
public class LevyFunction implements Function, FunctionBounds {

  @Property(name = "Number of variables n =")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int n = 2;

  double[] y = new double[n];

  public double valueAt(Point point) {
    double[] ax = point.toArray();


    for (int i = 0; i < n; i++)
      y[i] = 1 + (ax[i] - 1) / 4;

    double sum = 0.0;

    for (int i = 0; i < n - 1; i++)
      sum += (y[i] - 1) * (y[i] - 1) * (1 + 10 * Math.sin(Math.PI * y[i] + 1) * Math.sin(Math.PI * y[i] + 1));

    return Math.sin(Math.PI * y[0]) * Math.sin(Math.PI * y[0]) + sum + (y[n - 1] - 1) * (y[n - 1] - 1) * (1 + 10 * Math.sin(2 * Math.PI * y[n - 1]) * Math.sin(2 * Math.PI * y[n - 1]));
  }

  public int getDimension() {
    return n;
  }

  public double[] getMinimum() {
    double[] min = new double[n];
    Arrays.fill(min, -10.0);
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
    y = new double[n];
  }
}