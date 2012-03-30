package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Number of variables: n.
 * Number of local minima: several local minima.
 * The global minima: x* =  (0, ..., 0), f(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page295.htm
 */
@Component(name="Ackley's Path Function")
public class AckleyFunction implements Function, FunctionGradient {

  @Property(name = "Number of variables n =")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int n = 2;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum1 = 0.0;
    double sum2 = 0.0;

    for (int i = 0; i < n; i++) {
      sum1 += ax[i] * ax[i];
      sum2 += Math.cos(2 * Math.PI * ax[i]);
    }

    return 20 + Math.exp(1) - 20 * Math.exp(- 0.2 * Math.sqrt(sum1 / n)) - Math.exp(sum2 / n);
  }

  public int getDimension() {
    return n;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[n];

    double sqrtSum = 0.0;
    double cosSum = 0.0;
    for (int i = 0; i < n; i++) {
      sqrtSum += ax[i] * ax[i];
      cosSum += Math.cos(2 * Math.PI * ax[i]);
    }

    sqrtSum = Math.sqrt(sqrtSum);
    cosSum /= n;

    for (int i = 0; i < n; i++)
      gradient[i] = (2 * ax[i] * Math.exp(-0.1 * sqrtSum)) / sqrtSum + 0.5 * Math.exp(cosSum) * Math.PI * Math.sin(2 * Math.PI * ax[i]); 

    return Gradient.valueOf(gradient);
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
  }
}