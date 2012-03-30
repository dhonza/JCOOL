package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Number of variables: n.
 * Several local minima.
 * The global minima: x* = (0, ..., 0), f(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page1240.htm
 */
@Component(name="Griewangk Function")
public class GriewangkFunction implements Function, FunctionGradient, FunctionHessian {

  @Property(name = "Number of variables n =")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int n = 2;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum = 0.0;
    for (int i = 0; i < n; i++)
      sum += ax[i] * ax[i];

    double product = 1.0;
    for (int i = 0; i < n; i++)
      product *= Math.cos(ax[i] / Math.sqrt(i + 1));

    return sum / 4000.0 - product + 1;
  }

  public int getDimension() {
    return n;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];

    double prod;
    for (int i = 0; i < n; i++) {
      gradient[i] = ax[i] / 2000;
      prod = 1.0 / Math.sqrt(i + 1);
      
      for (int j = 0; j < n; j++)
        if (j != i)
          prod *= Math.cos(ax[j] / Math.sqrt(j + 1));
        else
          prod *= Math.sin(ax[j] / Math.sqrt(j + 1));

      gradient[i] += prod;
    }

    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];

    double prod;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (i == j) {
          hessian[i][j] = 1.0 / 2000;

          prod = 1.0 / (i + 1);
          for (int k = 0; k < n; k++)
            prod *= Math.cos(ax[j] / Math.sqrt(j + 1));
          hessian[i][j] += prod;

        }
        else {
          hessian[i][j] = - Math.sin(ax[i] / Math.sqrt(i + 1)) / Math.sqrt((i + 1) * (j + 1));

          for (int k = 0; k < n; k++) {
            hessian[i][j] *= Math.sin(ax[j] / Math.sqrt(j + 1));
            if (k != i && k != j)
              hessian[i][j] *= Math.cos(ax[k] / Math.sqrt(k + 1));
          }
        }
      }
    }

    return Hessian.valueOf(hessian);
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
  }
}