package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Number of variables: n = 4.
 * Search domain: 0 <= xi <= 4, i = 1, 2, . . . , 4.
 * The global minima: x* = (1,2,3,4), f(x*) = 0
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page670.htm
 */
@Component(name="Power Sum Function")
public class PowerSumFunction implements Function, FunctionBounds {

  private double[] b = {8, 18, 44, 114};

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum1 = 0.0;
    double sum2;

    for (int i = 0; i < 4; i ++) {
      sum2 = 0.0;

      for (int j = 0; j < 4; j++)
        sum2 += Math.pow(ax[j], i + 1);  

      sum1 += (sum2 - b[i]) * (sum2 - b[i]);
    }

    return sum1;
  }

  public int getDimension() {
    return 4;
  }

  public double[] getMinimum() {
    return new double[] {0.0, 0.0, 0.0, 0.0};
  }

  public double[] getMaximum() {
    return new double[] {4.0, 4.0, 4.0, 4.0};
  }
}