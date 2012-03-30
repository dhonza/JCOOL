package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;

/**
 * Number of variables: n.
 * Search domain: -10 <= xi <= 10, i = 1, 2, . . . , n.
 * The global minima: f(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page1240.htm
 */
@Component(name="Dixon & Price Function")
public class DixonPriceFunction implements Function, FunctionBounds {

  @Property(name = "Number of variables n =")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int n = 2;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum = 0.0;

    for (int i = 1; i < n; i++)
      sum += (i + 1) * (2 * ax[i] * ax[i] - ax[i - 1]) * (2 * ax[i] * ax[i] - ax[i - 1]); 

    return (ax[0] - 1) * (ax[0] - 1) + sum;
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
  }
}