package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Number of variables: n.
 * Search domain: 0 <= xi <= Pi, i = 1, 2, . . . , n.
 * Number of local minima: several local minima.
 * The global minima:
 *                    at n=2, f(x*) = -1.8013.
 *                    at n=5, f(x*) = -4.687658.
 *                    at n=10, f(x*) = -9.66015.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page2376.htm
 */
@Component(name="Michalewicz's Function")
public class MichalewiczFunction implements Function, FunctionGradient, FunctionHessian {

  @Property(name = "Number of variables n =")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int n = 2;

  @Property(name = "Parameter m =")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double m = 10.0;

  public double valueAt(Point point) {
    double[] ax = point.toArray();
    double sum = 0.0;

    for (int i = 0; i < n; i++)
      sum += Math.sin(ax[i]) * Math.pow(Math.sin((i + 1) * ax[i] * ax[i] / Math.PI), 2 * m); 

    return -sum;
  }

  public int getDimension() {

    return n;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];

    for (int i = 0; i < n; i++) {
      gradient[i] = -Math.cos(ax[i]) * Math.pow(Math.sin((i + 1) * ax[i] * ax[i] / Math.PI), 2 * m) - (1.0 / Math.PI) *
          (i + 1) * 4 * ax[i] * m * Math.cos((i + 1) * ax[i] * ax[i] / Math.PI) * Math.sin(ax[i]) * Math.pow(Math.sin((i + 1) * ax[i] * ax[i] / Math.PI), - 1 + 2 * m);
    }

    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];

    for (int i = 0; i < n; i++)
      hessian[i][i] = (Math.pow(Math.sin((i + 1) * ax[i] * ax[i] / Math.PI), - 2 + 2 * m) * (- (8 * (i + 1) * (i + 1) * ax[i] * ax[i] * m *
      (- 1 + 2 * m) * Math.cos((i + 1) * ax[i] * ax[i] / Math.PI) * Math.cos((i + 1) * ax[i] * ax[i] / Math.PI) *
      Math.sin(ax[i]) - (i + 1) * 4 * ax[i] * m * Math.PI * Math.cos(ax[i]) * Math.sin(2 * (i + 1) * ax[i] * ax[i] / Math.PI) +
      Math.sin(ax[i]) * ((8 * (i + 1) * (i + 1) * ax[i] * ax[i] * m + Math.PI * Math.PI) *
      Math.sin((i + 1) * ax[i] * ax[i] / Math.PI) * Math.sin((i + 1) * ax[i] * ax[i] / Math.PI) -
      2 * (i + 1) * m * Math.PI * Math.sin(2 * (i + 1) * ax[i] * ax[i] / Math.PI)))) 
      ) / (Math.PI * Math.PI);

    return Hessian.valueOf(hessian);
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
  }

  public double getM() {
    return m;
  }

  public void setM(double m) {
    this.m = m;
  }
}