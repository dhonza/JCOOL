package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.FunctionBounds;
import cz.cvut.felk.cig.jcool.core.Point;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;

/**
 * Number of variables: n.
 * Search domain: -n <= xi <= n, i = 1, 2, . . . , n.
 * The global minima: x* = (1, 2, ..., n), f(x*) = 0.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page2545.htm
 */
@Component(name="Perm Functions")
public class PermFunction implements Function, FunctionBounds {

  @Property(name = "Number of variables n =")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int n = 2;

  @Property(name = "Beta =")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double beta = 10.0;

  public enum FunctionType {
    PNB("P_n, beta"), P0NB("P0_n, beta");
    private String NAME;

    private FunctionType(String name) {
      NAME = name;
    }

    @Override
    public String toString() {
      return NAME;
    }
  }

  @Property(name = "Function type")
  private FunctionType functionType = FunctionType.PNB;

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum1 = 0.0;
    double sum2 = 0.0;

    switch (functionType) {
      case PNB:
        for (int i = 0; i < n; i++) {

          sum2 = 0.0;
          for (int j = 0; j < n; j++)
            sum2 += (Math.pow(j + 1, i + 1) + beta) * (Math.pow(ax[j] / (j + 1), i + 1) - 1);

          sum1 += sum2 * sum2;
        }
        break;

      case P0NB:
        for (int i = 0; i < n; i++) {

          sum2 = 0.0;
          for (int j = 0; j < n; j++)
            sum2 += (j + 1 + beta) * (Math.pow(ax[j], i + 1) - Math.pow(1 / (j + 1), i + 1));

          sum1 += sum2 * sum2;
        }          
        break;
    }

    return sum1;
  }

  public int getDimension() {
    return n;
  }

  public double[] getMinimum() {
    double[] min = new double[n];
    Arrays.fill(min, -n);
    return min;
  }

  public double[] getMaximum() {
    double[] max = new double[n];
    Arrays.fill(max, n);
    return max;
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
  }

  public FunctionType getFunctionType() {
    return functionType;
  }

  public void setFunctionType(FunctionType functionType) {
    this.functionType = functionType;
  }

  public double getBeta() {
    return beta;
  }

  public void setBeta(double beta) {
    this.beta = beta;
  } 
}