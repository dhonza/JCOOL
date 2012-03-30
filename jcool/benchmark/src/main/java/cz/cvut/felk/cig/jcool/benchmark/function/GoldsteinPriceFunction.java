package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Number of variables: n = 2.
 * Search domain: -2 <= xi <= 2, i = 1, 2.
 * Number of local minima: several local minima.
 * The global minima: x* = (0, -1), f(x*) = 3
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page1760.htm
 */
@Component(name = "Goldstein & Price Function")
public class GoldsteinPriceFunction implements Function, FunctionBounds, FunctionGradient, FunctionHessian {

  public double valueAt(Point point) {
    double[] ax = point.toArray();
    return (1 + (ax[0] + ax[1] + 1) * (ax[0] + ax[1] + 1) * (19 - 14 * ax[0] + 3 * ax[0] * ax[0] - 14 * ax[1] + 6 * ax[0] * ax[1] + 3 * ax[1] * ax[1])) *
           (30 + (2 * ax[0] - 3 * ax[1]) * (2 * ax[0] - 3 * ax[1]) * (18 - 32 * ax[0] + 12 * ax[0] * ax[0] + 48 * ax[1] - 36 * ax[0] * ax[1] + 27 * ax[1] * ax[1]));
  }

  public int getDimension() {
    return 2;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];

    gradient[0] = 24 * (8 * ax[0] * ax[0] * ax[0] - 4 * ax[0] * ax[0] * (4 + 9 * ax[1]) - 9 * ax[1] * (1 + 4 * ax[1] + 3 * ax[1] * ax[1]) +
        6 * ax[0] * (1 + 8 * ax[1] + 9 * ax[1] * ax[1])) * (1 + (1 + ax[0] + ax[1]) * (1 + ax[0] + ax[1]) * (19 + 3 * ax[0] * ax[0] -
        14 * ax[1] + 3 * ax[1] * ax[1] + 2 * ax[0] * (-7 + 3 * ax[1]))) +
        12 * (2 + ax[0] * ax[0] * ax[0] - ax[1] - 2 * ax[1] * ax[1] + ax[1] * ax[1] * ax[1] + ax[0] * ax[0] * (-2 + 3 * ax[1]) +
            ax[0] * (-1 - 4 * ax[1] + 3 * ax[1] * ax[1])) * (30 + (2 * ax[0] - 3 * ax[1]) * (2 * ax[0] - 3 * ax[1]) * (12 * ax[0] * ax[0] -
            4 * ax[0] * (8 + 9 * ax[1]) + 3 * (6 + 16 * ax[1] + 9 * ax[1] * ax[1])));

    gradient[1] = -36 * (8 * ax[0] * ax[0] * ax[0] - 4 * ax[0] * ax[0] * (4 + 9 * ax[1]) - 9 * ax[1] * (1 + 4 * ax[1] + 3 * ax[1] * ax[1]) +
        6 * ax[0] * (1 + 8 * ax[1] + 9 * ax[1] * ax[1])) * (1 + (1 + ax[0] + ax[1]) * (1 + ax[0] + ax[1]) * (19 + 3 * ax[0] * ax[0] -
        14 * ax[1] + 3 * ax[1] * ax[1] + 2 * ax[0] * (-7 + 3 * ax[1]))) +
        12 * (2 + ax[0] * ax[0] * ax[0] - ax[1] - 2 * ax[1] * ax[1] + ax[1] * ax[1] * ax[1] + ax[0] * ax[0] * (-2 + 3 * ax[1]) +
            ax[0] * (-1 - 4 * ax[1] + 3 * ax[1] * ax[1])) * (30 + (2 * ax[0] - 3 * ax[1]) * (2 * ax[0] - 3 * ax[1]) * (12 * ax[0] * ax[0] -
            4 * ax[0] * (8 + 9 * ax[1]) + 3 * (6 + 16 * ax[1] + 9 * ax[1] * ax[1])));
    
  return Gradient.valueOf(gradient);
}

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];
    hessian[0][0] = 12 * (672 * ax[0] * ax[0] * ax[0] * ax[0] * ax[0] * ax[0] - 336 * ax[0] * ax[0] * ax[0] * ax[0] * ax[0] * (8 + 3 * ax[1]) -
        20 * ax[0] * ax[0] * ax[0] * ax[0] * (-119 - 168 * ax[1] + 81 * ax[1] * ax[1]) + 40 * ax[0] * ax[0] * ax[0] * (56 - 7 * ax[1] + 108 * ax[1] * ax[1] + 51 * ax[1] * ax[1] * ax[1]) +
        3 * ax[0] * ax[0] * (-818 - 2560 * ax[1] - 1790 * ax[1] * ax[1] - 1360 * ax[1] * ax[1] * ax[1] + 435 * ax[1] * ax[1] * ax[1] * ax[1]) +
        ax[0] * (-536 + 2892 * ax[1] + 4920 * ax[1] * ax[1] + 620 * ax[1] * ax[1] * ax[1] - 1740 * ax[1] * ax[1] * ax[1] * ax[1] - 918 * ax[1] * ax[1] * ax[1] * ax[1] * ax[1]) +
        3 * (70 + 408 * ax[1] + 432 * ax[1] * ax[1] + 280 * ax[1] * ax[1] * ax[1] + 485 * ax[1] * ax[1] * ax[1] * ax[1] + 204 * ax[1] * ax[1] * ax[1] * ax[1] * ax[1] - 81 * ax[1] * ax[1] * ax[1] * ax[1] * ax[1] * ax[1]));

    hessian[0][1] = -12 * (390 + 168 * ax[0] * ax[0] * ax[0] * ax[0] * ax[0] * ax[0] + 3216 * ax[1] + 5904 * ax[1] * ax[1] + 3960 * ax[1] * ax[1] * ax[1] + 495 * ax[1] * ax[1] * ax[1] * ax[1] - 972 * ax[1] * ax[1] * ax[1] * ax[1] * ax[1] - 567 * ax[1] * ax[1] * ax[1] * ax[1] * ax[1] * ax[1] + 24 * ax[0] * ax[0] * ax[0] * ax[0] * ax[0] * (-28 + 27 * ax[1]) - 10 * ax[0] * ax[0] * ax[0] * ax[0] * (-7 + 216 * ax[1] + 153 * ax[1] * ax[1]) - 20 * ax[0] * ax[0] * ax[0] * (-128 - 179 * ax[1] - 204 * ax[1] * ax[1] + 87 * ax[1] * ax[1] * ax[1]) + 3 * ax[0] * ax[0] * (-482 - 1640 * ax[1] - 310 * ax[1] * ax[1] + 1160 * ax[1] * ax[1] * ax[1] + 765 * ax[1] * ax[1] * ax[1] * ax[1]) + 6 * ax[0] * (-204 - 432 * ax[1] - 420 * ax[1] * ax[1] - 970 * ax[1] * ax[1] * ax[1] - 510 * ax[1] * ax[1] * ax[1] * ax[1] + 243 * ax[1] * ax[1] * ax[1] * ax[1] * ax[1]));
    hessian[1][0] = hessian[0][1];
    hessian[1][1] = -12 * (108 * ax[0] * ax[0] * ax[0] * ax[0] * ax[0] * ax[0] - 36 * ax[0] * ax[0] * ax[0] * ax[0] * ax[0] * (12 + 17 * ax[1]) +
        ax[0] * ax[0] * ax[0] * ax[0] * (895 + 2040 * ax[1] - 1305 * ax[1] * ax[1]) + 20 * ax[0] * ax[0] * ax[0] * (-82 - 31 * ax[1] + 174 * ax[1] * ax[1] + 153 * ax[1] * ax[1] * ax[1]) +
        9 * ax[0] * ax[0] * (-144 - 280 * ax[1] - 970 * ax[1] * ax[1] - 680 * ax[1] * ax[1] * ax[1] + 405 * ax[1] * ax[1] * ax[1] * ax[1]) -
        6 * ax[0] * (-536 - 1968 * ax[1] - 1980 * ax[1] * ax[1] - 330 * ax[1] * ax[1] * ax[1] + 810 * ax[1] * ax[1] * ax[1] * ax[1] + 567 * ax[1] * ax[1] * ax[1] * ax[1] * ax[1]) -
        6 * (85 + 1024 * ax[1] + 2391 * ax[1] * ax[1] + 540 * ax[1] * ax[1] * ax[1] - 1845 * ax[1] * ax[1] * ax[1] * ax[1] - 378 * ax[1] * ax[1] * ax[1] * ax[1] * ax[1] + 567 * ax[1] * ax[1] * ax[1] * ax[1] * ax[1] * ax[1]));
    return Hessian.valueOf(hessian);
  }

  public double[] getMinimum() {
    return new double[]{-2.0, -2.0};
  }

  public double[] getMaximum() {
    return new double[]{2.0, 2.0};
  }
}