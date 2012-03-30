package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.FunctionBounds;
import cz.cvut.felk.cig.jcool.core.Point;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;

import java.util.Arrays;

/**
 * Number of variables: n = 3; n = 6.
 * Search domain: 0 < xi < 1, i = 1, 2, 3.; 0 < xi < 1, i = 1, 2, . . . , 6.
 * Number of local minima: 4 local minima.; 6 local minima.
 * The global minima: x* = (0.114614, 0.555649, 0.852547), f(x*) = - 3.86278.
 *                    x* = (0.20169, 0.150011, 0.476874, 0.275332, 0.311652, 0.6573), f(x*) = - 3.32237.                    
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page1488.htm
 */
@Component(name="Hartmann Function")
public class HartmannFunction implements Function, FunctionBounds {

  public enum FunctionType {
    H34("H_3,4"), H64("H_6,4");
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
  private FunctionType functionType = FunctionType.H34;

  private static double[] alpha = {1.0, 1.2, 3.0, 3.2};
  
  private static double[][] A = {{3.0, 10.0, 30.0},
                                 {0.1, 10.0, 35.0},
                                 {3.0, 10.0, 30.0},
                                 {0.1, 10.0, 35.0}};
  private static double[][] P = {{0.6890, 0.1170, 0.2673},
                                 {0.4699, 0.4387, 0.7470},
                                 {0.1091, 0.8732, 0.5547},
                                 {0.0381, 0.5743, 0.8828}};

  private static double[][] B = {{10.0,  3.0, 17.0, 3.05,  1.7,  8.0},
                                 {0.05, 10.0, 17.0,  0.1,  8.0, 14.0},
                                 { 3.0,  3.5,  1.7, 10.0, 17.0,  8.0},
                                 {17.0,  8.0, 0.05, 10.0,  0.1, 14.0}};
  private static double[][] Q = {{0.1312, 0.1696, 0.5569, 0.0124, 0.8283, 0.5886},
                                 {0.2329, 0.4135, 0.8307, 0.3736, 0.1004, 0.9991},
                                 {0.2348, 0.1451, 0.3522, 0.2883, 0.3047, 0.6650},
                                 {0.4047, 0.8828, 0.8732, 0.5743, 0.1091, 0.0381}};


  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum1 = 0.0;
    double sum2;

    switch (functionType) {
      case H34:
        for (int i = 0; i < 4; i++) {
          sum2 = 0.0;
          for (int j = 0; j < 3; j++) {
            sum2 += A[i][j] * (ax[j] - P[i][j]) * (ax[j] - P[i][j]);
          }
          sum1 += alpha[i] * Math.exp(-sum2); 
        }
        break;
      case H64:
        for (int i = 0; i < 4; i++) {
          sum2 = 0.0;
          for (int j = 0; j < 6; j++) {
            sum2 += B[i][j] * (ax[j] - Q[i][j]) * (ax[j] - Q[i][j]);
          }
          sum1 += alpha[i] * Math.exp(-sum2);
        } 
        break;
    }

    return -sum1;
  }

  public int getDimension() {
    switch (functionType) {
      case H34:
        return 3;
      case H64:
        return 6;
    }
    return -1;
  }

  public double[] getMinimum() {
    double[] min;
    switch (functionType) {
      case H34:
        min = new double[3];
        Arrays.fill(min, 0.0);
        return min;
      case H64:
        min = new double[6];
        Arrays.fill(min, 0.0);
        return min;
    }

    return new double[]{};
  }

  public double[] getMaximum() {
    double[] max;
    switch (functionType) {
      case H34:
        max = new double[3];
        Arrays.fill(max, 1.0);
        return max;
      case H64:
        max = new double[6];
        Arrays.fill(max, 1.0);
        return max;
    }

    return new double[]{};
  }

   public FunctionType getFunctionType() {
    return functionType;
  }

  public void setFunctionType(FunctionType functionType) {
    this.functionType = functionType;
  } 
}