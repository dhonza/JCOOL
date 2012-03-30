package cz.cvut.felk.cig.jcool.benchmark.method.gradient.qn;

import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearch;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearchBrentNoDerivatives;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearchBrentWithDerivatives;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.PALStopCondition;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Quasi-newton optimization method class.
 */
@Component(name = "Quasi Newton Method")
public class QuasiNewtonMethod implements OptimizationMethod<ValuePointTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  @Property(name = "Parameter Minimum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;          // parameter minimum

  @Property(name = "Parameter Maximum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;           // parameter maximum

  /**
   * Line search type.
   */
  @Property(name = "Line Search Type")
  private LineSearchType lineSearch = LineSearchType.BRENT_WITHOUT;

  /**
   * Update formula type.
   */
  @Property(name = "Update Method")
  private UpdateMethodType updateMethod = UpdateMethodType.BFGS;

  /**
   * Phi for Broyden Family: (1 - phi) * BFGS + phi * DFS").
   */
  @Property(name = "Phi for Broyden Family: (1 - phi) * BFGS + phi * DFS")
  @Range(from = 0.0, to = 1.0)
  private double phi = 0.5;          // parameter minimum

  private LineSearch lineSearchMethod;

  private ValuePointTelemetry telemetry;
  private Consumer<? super ValuePointTelemetry> consumer;

  /**
   * Current solution candidate vector.
   */
  private double[] x;

  /**
   * New solution candidate vector.
   */
  private double[] xNew;

  /**
   * Newton step.
   */
  private double[] xDelta;            // deltaX = xNew - x

  /**
   * Difference of current and old gradient.
   */
  private double[] y;                 // y_k = grad(x_k+1) - grad(x_k)

  /**
   * Gradient vector.
   */
  private double[] g;                 // gradient ~ grad(x_k)

  /**
   * Inverse Hessian matrix approximation * y vector.
   */
  private double[] Hy;                // H * y

  /**
   * x * H
   */
  private double[] xH;                // xH

  /**
   * Inverse Heesian matrix approximaiton.
   */
  private double[][] H;               // B^-1

  double fac, fae, fad;
  double sumY, sumXDelta;

  /**
   * Search direction.
   */
  private double[] direction;

  private ValuePoint solution;        // solution
  private int dimension;              // number of variables to optimize

  public QuasiNewtonMethod() {
    this.stopCondition = new SimpleStopCondition();
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointTelemetry();
  }

  public void init(ObjectiveFunction function) {

    this.function = function;
    dimension = function.getDimension();

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    xNew = new double[dimension];
    xDelta = new double[dimension];
    y = new double[dimension];
    H = new double[dimension][dimension];

    Hy = new double[dimension];
    xH = new double[dimension];

    direction = new double[dimension];

    switch (lineSearch) {
      case BRENT_WITHOUT:
        this.lineSearchMethod = new LineSearchBrentNoDerivatives(function);
        break;
      case BRENT_WITH:
        this.lineSearchMethod = new LineSearchBrentWithDerivatives(function);
        break;
      default:
        throw new IllegalStateException("Unknown line search method");
    }

    x = Point.random(dimension, min, max).toArray();
    solution = ValuePoint.at(Point.at(x), function);
    this.stopCondition.setInitialValue(solution.getValue());

    g = function.gradientAt(Point.at(x)).toArray();

    for (int i = 0; i < dimension; i++) {
      direction[i] = -g[i];
      H[i][i] = 1.0;
      y[i] = -1;
    }
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {

    System.arraycopy(x, 0, xNew, 0, dimension);
    lineSearchMethod.minimize(xNew, direction);

    for (int i = 0; i < dimension; i++)
      xDelta[i] = xNew[i] - x[i];
    
    System.arraycopy(xNew, 0, x, 0, dimension);
    System.arraycopy(g, 0, y, 0, dimension);
    g = function.gradientAt(Point.at(x)).toArray();

    for (int i = 0; i < dimension; i++)
      y[i] = g[i] - y[i];

    for (int i = 0; i < dimension; i++) {
      Hy[i] = 0.0;
      for (int j = 0; j < dimension; j++)
        Hy[i] += H[i][j] * y[j];
    }

    fac = 0.0;
    fae = 0.0;
    sumY = 0.0;
    sumXDelta = 0.0;

    for (int i = 0; i < dimension; i++) {
      fac += y[i] * xDelta[i];
      fae += y[i] * Hy[i];
      sumY += y[i] * y[i];
      sumXDelta += xDelta[i] * xDelta[i];
    }

    if (fac > Math.sqrt(MachineAccuracy.EPSILON * sumY * sumXDelta)) {
      fac = 1.0 / fac;
      fad = 1.0 / fae;

      switch (updateMethod) {
        case DFP:
          for (int i = 0; i < dimension; i++)
            for (int j = i; j < dimension; j++) {
              H[i][j] += fac * xDelta[i] * xDelta[j] - fad * Hy[i] * Hy[j];
              H[j][i] = H[i][j];
            }
        break;

        case BFGS:
          for (int i = 0; i < dimension; i++)
            y[i] = fac * xDelta[i] - fad * Hy[i];

          for (int i = 0; i < dimension; i++)
            for (int j = i; j < dimension; j++) {
              H[i][j] += fac * xDelta[i] * xDelta[j] - fad * Hy[i] * Hy[j] + fae * y[i] * y[j];
              H[j][i] = H[i][j];
            }
        break;

        case BROYDEN_FAMILY:
          for (int i = 0; i < dimension; i++)
            y[i] = fac * xDelta[i] - fad * Hy[i];

          for (int i = 0; i < dimension; i++)
            for (int j = i; j < dimension; j++) {
              H[i][j] += fac * xDelta[i] * xDelta[j] - fad * Hy[i] * Hy[j] + phi * (fae * y[i] * y[j]);
              H[j][i] = H[i][j];
            }
        break;

        case BROYDEN:
          fae = 0.0;
          
          for (int i = 0; i < dimension; i++) {
            xH[i] = 0.0;

            for (int j = 0; j < dimension; j++)
              xH[i] += xDelta[j] * H[j][i];
            
            fae += xDelta[i] * Hy[i];
          }

          fae = 1.0 / fae;

          for (int i = 0; i < dimension; i++) {
            y[i] = xDelta[i] - Hy[i];
            for (int j = i; j < dimension; j++) {
              H[i][j] += fae * y[i] * xH[j];
              H[j][i] = H[i][j];
            }
          }
        break;
      }
    }

    for (int i = 0; i < dimension; i++) {
      direction[i] = 0.0;
      for (int j = 0; j < dimension; j++)
        direction[i] -= H[i][j] * g[j];
    }

    solution = ValuePoint.at(Point.at(x), function);

    telemetry = new ValuePointTelemetry(solution);
    if (consumer != null)
      consumer.notifyOf(this);

    stopCondition.setValue(solution.getValue());
  }

  public void addConsumer(Consumer<? super ValuePointTelemetry> consumer) {
    this.consumer = consumer;
  }

  public ValuePointTelemetry getValue() {
    return telemetry;
  }

  public double getMin() {
    return min;
  }

  public void setMin(double min) {
    this.min = min;
  }

  public double getMax() {
    return max;
  }

  public void setMax(double max) {
    this.max = max;
  }

  public LineSearchType getLineSearch() {
    return lineSearch;
  }

  public void setLineSearch(LineSearchType lineSearch) {
    this.lineSearch = lineSearch;
  }

  public UpdateMethodType getUpdateMethod() {
    return updateMethod;
  }

  public void setUpdateMethod(UpdateMethodType updateMethod) {
    this.updateMethod = updateMethod;
  }

  public double getPhi() {
    return phi;
  }

  public void setPhi(double phi) {
    this.phi = phi;
  }

  public enum LineSearchType {
    BRENT_WITH("Brent with derivatives"), BRENT_WITHOUT("Brent (no derivatives)");
    private String NAME;

    private LineSearchType(String name) {
      NAME = name;
    }

    @Override
    public String toString() {
      return NAME;
    }
  }

  public enum UpdateMethodType {
    DFP("Davidon-Fletcher-Powell"), BFGS("Broyden-Fletcher-Goldfarb-Shanno"),
    BROYDEN("Broyden"), BROYDEN_FAMILY("Broyden Family");
    private String NAME;

    private UpdateMethodType(String name) {
      NAME = name;
    }

    @Override
    public String toString() {
      return NAME;
    }
  }
}