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
 * Search domain: -520 < xi < 520, i = 1, ..., n.
 * The global minima: xi* = -514,041683, i = 1, ..., n. 
 *                    f(x*) = -512,753162426239100568636786193
 * http://www.it.lut.fi/ip/evo/functions/node17.html
 */
@Component(name="Rana's Function")
public class RanaFunction implements Function, FunctionBounds {

  @Property(name = "Number of variables n =")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int n = 2;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum = 0.0;
    int j;
    double pp, pm;

    for (int i = 0; i < n; i++) {

      j = (i + 2) % n;

      pp = Math.sqrt(Math.abs(ax[j] + ax[i] + 1));
      pm = Math.sqrt(Math.abs(ax[j] - ax[i] + 1));

      sum += ax[i] * Math.sin(pm) * Math.cos(pp) + (ax[j] + 1) * Math.cos(pm) * Math.sin(pp);
    }

    return sum / n;
  }

  public int getDimension() {
    return n;
  }

  public double[] getMinimum() {
    double[] min = new double[n];
    Arrays.fill(min, -520.0);
    return min;
  }

  public double[] getMaximum() {
    double[] max = new double[n];
    Arrays.fill(max, 520.0);
    return max;
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
  }
}