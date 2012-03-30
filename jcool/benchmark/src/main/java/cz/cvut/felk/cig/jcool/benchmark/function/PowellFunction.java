package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.FunctionBounds;
import cz.cvut.felk.cig.jcool.core.Point;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;

/**
 * Number of variables: n.
 * Search domain: -4 <= xi <= 5, i = 1, 2, . . . , n.
 * The global minima: x* = (1, 2, ..., n), f(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page2545.htm
 */
@Component(name="Powell Function")
public class PowellFunction implements Function, FunctionBounds {

  @Property(name = "Number of variables n =")
  @Range(from = 4, to = Integer.MAX_VALUE)
  private int n = 4;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum = 0.0;

    for (int i = 1; i <= n / 4; i++) {
      sum += (ax[4 * i - 4] + 10 * ax[4 * i - 3]) * (ax[4 * i - 4] + 10 * ax[4 * i - 3]) +
             5 * (ax[4 * i - 2] - ax[4 * i - 1]) * (ax[4 * i - 2] - ax[4 * i - 1]) +
             (ax[4 * i - 3] - ax[4 * i - 2]) * (ax[4 * i - 3] - ax[4 * i - 2]) * (ax[4 * i - 3] - ax[4 * i - 2]) * (ax[4 * i - 3] - ax[4 * i - 2]) +
             10 * (ax[4 * i - 4] - ax[4 * i - 1]) * (ax[4 * i - 4] - ax[4 * i - 1]) * (ax[4 * i - 4] - ax[4 * i - 1]) * (ax[4 * i - 4] - ax[4 * i - 1]);
    }

    return sum;
  }

  public int getDimension() {
    return n;
  }

  public double[] getMinimum() {
    double[] min = new double[n];
    Arrays.fill(min, -4.0);
    return min;
  }

  public double[] getMaximum() {
    double[] max = new double[n];
    Arrays.fill(max, 5.0);
    return max;
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
  }
}