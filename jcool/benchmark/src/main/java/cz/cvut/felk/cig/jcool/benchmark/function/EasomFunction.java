package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Number of variables: n = 2.
 * Search domain: -100 <= xi <= 100, i = 1, 2.
 * Number of local minima: several local minima.
 * The global minima: x* = (Pi, Pi), f(x*) = - 1.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page1361.htm
 */
@Component(name = "Easom Function")
public class EasomFunction implements Function, FunctionGradient, FunctionHessian {

  public double valueAt(Point point) {
    double[] ax = point.toArray();
    return -Math.cos(ax[0]) * Math.cos(ax[1]) * Math.exp(-((ax[0] - Math.PI) * (ax[0] - Math.PI)) - (ax[1] - Math.PI) * (ax[1] - Math.PI));
  }

  public int getDimension() {
    return 2;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];

    gradient[0] = Math.exp(-(ax[0] - Math.PI) * (ax[0] - Math.PI) - (ax[1] - Math.PI) * (ax[1] - Math.PI)) * Math.cos(ax[1]) * (-2 * (Math.PI - ax[0]) * Math.cos(ax[0]) + Math.sin(ax[0]));
    gradient[1] = Math.exp(-(ax[0] - Math.PI) * (ax[0] - Math.PI) - (ax[1] - Math.PI) * (ax[1] - Math.PI)) * Math.cos(ax[0]) * (-2 * (Math.PI - ax[1]) * Math.cos(ax[1]) + Math.sin(ax[1]));
    
    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];
    hessian[0][0] = -Math.exp(-(ax[0] - Math.PI) * (ax[0] - Math.PI) - (ax[1] - Math.PI) * (ax[1] - Math.PI)) * Math.cos(ax[1]) * ((-3 + 4 * Math.PI * Math.PI - 8 * Math.PI * ax[0] + 4 * ax[0] * ax[0]) * Math.cos(ax[0]) + 4 * (-Math.PI + ax[0]) * Math.sin(ax[0]));
    hessian[0][1] = -Math.exp(-(ax[0] - Math.PI) * (ax[0] - Math.PI) - (ax[1] - Math.PI) * (ax[1] - Math.PI)) * (2 * (Math.PI - ax[0]) * Math.cos(ax[0]) - Math.sin(ax[0])) * (2 * (Math.PI - ax[1]) * Math.cos(ax[1]) - Math.sin(ax[1]));
    hessian[1][0] = hessian[0][1];
    hessian[1][1] = -Math.exp(-(ax[0] - Math.PI) * (ax[0] - Math.PI) - (ax[1] - Math.PI) * (ax[1] - Math.PI)) * Math.cos(ax[0]) * ((-3 + 4 * Math.PI * Math.PI - 8 * Math.PI * ax[1] + 4 * ax[1] * ax[1]) * Math.cos(ax[1]) + 4 * (-Math.PI + ax[1]) * Math.sin(ax[1]));
    return Hessian.valueOf(hessian);
  }
}