package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Number of variables: n = 4.
 * Search domain: -10 <= xi <= 10, i = 1, . . . , 4.
 * The global minima: x* =  (1, ..., 1), f(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page1016.htm
 */
@Component(name = "Colville Function")
public class ColvilleFunction implements Function, FunctionBounds, FunctionGradient, FunctionHessian {

  public double valueAt(Point point) {
    double[] ax = point.toArray();
    return 100 * (ax[0] * ax[0] - ax[1]) * (ax[0] * ax[0] - ax[1]) + (ax[0] - 1) * (ax[0] - 1) + (ax[2] - 1) * (ax[2] - 1) + 90 * (ax[2] * ax[2] - ax[3]) * (ax[2] * ax[2] - ax[3]) + 10.1 * ((ax[1] - 1) * (ax[1] - 1) + (ax[3] - 1) * (ax[3] - 1)) + 19.8 * (ax[1] - 1) * (ax[3] - 1);
  }

  public int getDimension() {
    return 4;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];

    gradient[0] = 2 * (-1 + ax[0] + 200. * ax[0] * (ax[0] * ax[0] - ax[1]));
    gradient[1] = -40. - 200 * ax[0] * ax[0] + 220.2 * ax[1] + 19.8 * ax[3];
    gradient[2] = 2 * (-1 + ax[2] + 180 * ax[2] * (ax[2] * ax[2] - ax[3]));
    gradient[3] = -40. + 19.8 * ax[1] - 180. * ax[2] * ax[2] + 200.2 * ax[3];

    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];
    
    hessian[0][0] = 2 + 1200 * ax[0] * ax[0] - 400 * ax[1];
    hessian[0][1] = -400 * ax[0];
    hessian[0][2] = 0;
    hessian[0][3] = 0;
    hessian[1][0] = -400 * ax[0];
    hessian[1][1] = 220.2;
    hessian[1][2] = 0;
    hessian[1][3] = 19.8;
    hessian[2][0] = 0;
    hessian[2][1] = 0;
    hessian[2][2] = 2 + 1080 * ax[2] * ax[2] - 360 * ax[3];
    hessian[2][3] = -360 * ax[2];
    hessian[3][0] = 0;
    hessian[3][1] = 19.8;
    hessian[3][2] = -360 * ax[2];
    hessian[3][3] = 200.2;
    return Hessian.valueOf(hessian);
  }

  public double[] getMinimum() {
    return new double[]{-10.0, -10.0, -10.0, -10.0};
  }

  public double[] getMaximum() {
    return new double[]{10.0, 10.0, 10.0, 10.0};
  }
}