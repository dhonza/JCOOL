package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Number of variables: n.
 * The global minima: x* =  (1, ..., 1), f(x*) = 0.
 * http://www.it.lut.fi/ip/evo/functions/node13.html
 */
@Component(name="Whitley's Function")
public class WhitleyFunction implements Function {

  @Property(name = "Number of variables n =")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int n = 2;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum1 = 0.0;
    double sum2;

    for (int i = 0; i < n; i++) {

      sum2 = 0.0;
      for (int j = 0; j < n; j++)
        sum2 += (100 * (ax[i] * ax[i] - ax [j]) * (ax[i] * ax[i] - ax [j]) + (1 - ax[j]) * (1 - ax[j])) *
                (100 * (ax[i] * ax[i] - ax [j]) * (ax[i] * ax[i] - ax [j]) + (1 - ax[j]) * (1 - ax[j])) / 4000.0 -
                Math.cos(100 * (ax[i] * ax[i] - ax [j]) * (ax[i] * ax[i] - ax [j]) + (1 - ax[j]) * (1 - ax[j])) + 1;

      sum1 += sum2;
    }

    return sum1;
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
}