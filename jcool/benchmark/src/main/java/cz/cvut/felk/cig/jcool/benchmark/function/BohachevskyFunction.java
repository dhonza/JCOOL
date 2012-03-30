package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;

/**
 * Number of variables: n = 2.
 * Search domain: -100 <= xi <= 100, i = 1, 2.
 * The global minima: x* =  (0, 0), fj(x*) = 0, j = 1, 2, 3.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page595.htm
 */
@Component(name="Bohachevsky Function")
public class BohachevskyFunction implements Function, FunctionGradient, FunctionHessian {

  public enum FunctionType {
    F1("# 1"), F2("# 2"), F3("# 3");
    private String NAME;

    private FunctionType(String name) {
      NAME = name;
    }

    @Override
    public String toString() {
      return NAME;
    }
  }

  @Property(name = "Function definition")
  private FunctionType functionType = FunctionType.F1;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double result = Double.NaN;
    switch (functionType) {
      case F1:
        result = ax[0] * ax[0] + 2 * ax[1] * ax[1] - 0.3 * Math.cos(3 * Math.PI * ax[0]) - 0.4 * Math.cos(4 * Math.PI * ax[1]) + 0.7;
      break;

      case F2:
        result = ax[0] * ax[0] + 2 * ax[1] * ax[1] - 0.3 * Math.cos(3 * Math.PI * ax[0]) * Math.cos(4 * Math.PI * ax[1]) + 0.3;
      break;

      case F3:
        result = ax[0] * ax[0] + 2 * ax[1] * ax[1] - 0.3 * Math.cos(3 * Math.PI * ax[0] + 4 * Math.PI * ax[1]) + 0.3;
      break;
    }

    return result;
  }

  public int getDimension() {
    return 2;
  }

  public Gradient gradientAt(Point point) {
    double[] ax = point.toArray();
    double[] gradient = new double[ax.length];

    switch (functionType) {
      case F1:
        gradient[0] = 2 * ax[0] + 2.82743 * Math.sin(3 * Math.PI *  ax[0]);
        gradient[1] = 4 * ax[1] + 5.02655 * Math.sin(4 * Math.PI *  ax[1]);
      break;

      case F2:
        gradient[0] = 2 * ax[0] + 2.82743 * Math.cos(4 * Math.PI *  ax[1]) * Math.sin(3 * Math.PI *  ax[0]);
        gradient[1] = 4 * ax[1] + 3.76991 * Math.cos(3 * Math.PI *  ax[0]) * Math.sin(4 * Math.PI *  ax[1]);
      break;

      case F3:
        gradient[0] = 2 * ax[0] + 2.82743 * Math.sin(Math.PI * (3 * ax[0] + 4 * ax[1]));
        gradient[1] = 4 * ax[1] + 3.76991 * Math.sin(Math.PI * (3 * ax[0] + 4 * ax[1]));
      break;
    }

    return Gradient.valueOf(gradient);
  }

  public Hessian hessianAt(Point point) {
    double[] ax = point.toArray();
    double[][] hessian = new double[ax.length][ax.length];

        switch (functionType) {
      case F1:
        hessian[0][0] = 2 + 26.6479 * Math.cos(3 * Math.PI *  ax[0]);
        hessian[0][1] = 0;
        hessian[1][0] = hessian[0][1];
        hessian[1][1] = 4 + 63.1655 * Math.cos(4 * Math.PI *  ax[1]);

      case F2:
        hessian[0][0] = 2 + 26.6479 * Math.cos(3 * Math.PI *  ax[0]) * Math.cos(4 * Math.PI *  ax[1]);
        hessian[0][1] = -35.5306 * Math.sin(3 * Math.PI *  ax[0]) * Math.sin(4 * Math.PI *  ax[1]);
        hessian[1][0] = hessian[0][1];
        hessian[1][1] = 4 + 47.3741 * Math.cos(3 * Math.PI *  ax[0]) * Math.cos(4 * Math.PI *  ax[1]);
      break;

      case F3:
        hessian[0][0] = 2 + 26.6479 * Math.cos(Math.PI * (3 * ax[0] + 4 * ax[1]));
        hessian[0][1] = 35.5306 * Math.cos(Math.PI * (3 * ax[0] + 4 * ax[1]));
        hessian[1][0] = hessian[0][1];
        hessian[1][1] = 4 + 47.3741 * Math.cos(Math.PI * (3 * ax[0] + 4 * ax[1]));
    }

    return Hessian.valueOf(hessian);
  }

  public FunctionType getFunctionType() {
    return functionType;
  }

  public void setFunctionType(FunctionType functionType) {
    this.functionType = functionType;
  }
}