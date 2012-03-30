package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Number of variables: n.
 * Search domain: -500 <= xi <= 500, i = 1, 2, . . . , n.
 * Number of local minima: several local minima.
 * The global minima: x* = (420.9687, ..., 420.9687), f(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page2530.htm
 */
@Component(name="Schwefel's Function")
public class SchwefelFunction implements Function {

  @Property(name = "Number of variables n =")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int n = 2;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum = 0.0;

    for (int i = 0; i < n; i++)
      sum += ax[i] * Math.sin(Math.sqrt(Math.abs(ax[i])));

    return 418.9829 * n - sum;
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