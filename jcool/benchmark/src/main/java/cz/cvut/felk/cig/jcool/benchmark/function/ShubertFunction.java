package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Number of variables: n = 2.
 * Search domain: -10 <= xi <= 10, i = 1, 2.
 * Number of local minima: several local minima.
 * The global minima: 18 global minima  f(x*) = -186.7309.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page1882.htm
 */
@Component(name = "Shubert Function")
public class ShubertFunction implements Function, FunctionBounds, FunctionGradient, FunctionHessian {

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum1 = 0.0;
    double sum2 = 0.0;

    for (int i = 1; i <= 5; i++) {
      sum1 += i * Math.cos((i + 1) * ax[0] + i);
      sum2 += i * Math.cos((i + 1) * ax[1] + i);
    }


    return sum1 * sum2;
  }

  public int getDimension() {
    return 2;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];
    gradient[0] = -2 * (Math.cos(1 + 2 * ax[1]) + 2 * Math.cos(2 + 3 * ax[1]) + 3 * Math.cos(3 + 4 * ax[1]) +
                  4 * Math.cos(4 + 5 * ax[1]) + 5 * Math.cos(5 + 6 * ax[1])) * (Math.sin(1 + 2 * ax[0]) +
                  3 * Math.sin(2 + 3 * ax[0]) + 6 * Math.sin(3 + 4 * ax[0]) + 10 * Math.sin(4 + 5 * ax[0]) +
                  15 * Math.sin(5 + 6 * ax[0]));
    gradient[1] = -2 * (Math.cos(1 + 2 * ax[0]) + 2 * Math.cos(2 + 3 * ax[0]) +
                  3 * Math.cos(3 + 4 * ax[0]) + 4 * Math.cos(4 + 5 * ax[0]) + 5 * Math.cos(5 + 6 * ax[0])) *
                  (Math.sin(1 + 2 * ax[1]) + 3 * Math.sin(2 + 3 * ax[1]) + 6 * Math.sin(3 + 4 * ax[1]) +
                  10 * Math.sin(4 + 5 * ax[1]) + 15 * Math.sin(5 + 6 * ax[1]));
    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];
    hessian[0][0] = -2 * (2 * Math.cos(1 + 2 * ax[0]) + 9 * Math.cos(2 + 3 * ax[0]) + 24 * Math.cos(3 + 4 * ax[0]) +
                    50 * Math.cos(4 + 5 * ax[0]) + 90 * Math.cos(5 + 6 * ax[0])) * (Math.cos(1 + 2 * ax[1]) +
                    2 * Math.cos(2 + 3 * ax[1]) + 3 * Math.cos(3 + 4 * ax[1]) +
                    4 * Math.cos(4 + 5 * ax[1]) + 5 * Math.cos(5 + 6 * ax[1]));
    hessian[0][1] = 4 * (Math.sin(1 + 2 * ax[0]) + 3 * Math.sin(2 + 3 * ax[0]) + 6 * Math.sin(3 + 4 * ax[0]) +
                    10 * Math.sin(4 + 5 * ax[0]) + 15 * Math.sin(5 + 6 * ax[0])) *
                    (Math.sin(1 + 2 * ax[1]) + 3 * Math.sin(2 + 3 * ax[1]) + 6 * Math.sin(3 + 4 * ax[1]) +
                    10 * Math.sin(4 + 5 * ax[1]) + 15 * Math.sin(5 + 6 * ax[1]));
    hessian[1][0] = hessian[0][1];
    hessian[1][1] = -2 * (Math.cos(1 + 2 * ax[0]) + 2 * Math.cos(2 + 3 * ax[0]) + 3 * Math.cos(3 + 4 * ax[0]) +
                    4 * Math.cos(4 + 5 * ax[0]) + 5 * Math.cos(5 + 6 * ax[0])) *
                    (2 * Math.cos(1 + 2 * ax[1]) + 9 * Math.cos(2 + 3 * ax[1]) + 24 * Math.cos(3 + 4 * ax[1]) +
                    50 * Math.cos(4 + 5 * ax[1]) + 90 * Math.cos(5 + 6 * ax[1]));
    return Hessian.valueOf(hessian);
  }

  public double[] getMinimum() {
    return new double[]{-10, -10};
  }

  public double[] getMaximum() {
    return new double[]{10, 10};
  }
}