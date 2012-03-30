package cz.cvut.felk.cig.jcool.benchmark.method.powell;

import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearch;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearchBrentNoDerivatives;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearchBrentWithDerivatives;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;

/**
 * Powell's optimization method class.
 */
@Component(name = "Powell")
public class PowellMethod implements OptimizationMethod<ValuePointTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  private ValuePointTelemetry telemetry;
  private Consumer<? super ValuePointTelemetry> consumer;

  @Property(name = "Parameter Minimum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;          // parameter minimum

  @Property(name = "Parameter Maximum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;           // parameter maximum

  /**
   * Line search type.
   */
  @Property(name = "Select line search")
  private LineSearchType lineSearch = LineSearchType.BRENT_WITHOUT;

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

  private LineSearch lineSearchMethod;

  private int dimension;        // number of variables to optimize
  private ValuePoint solution;

  /**
   * Initial point at the beginning of each iteration.
   */
  private double[] p0;          // p_0

  /**
   * Point iteratively updated during one iteration.
   */
  private double[] pI;          // p_i; i = 1 ... N

  /**
   * Search directions.
   */
  private Point[] u;            // search directions

  /**
   * New search direction if conditions allow.
   */
  private double[] uNew;        // new search direction u_new = pN - p0

  /**
   * Function value for P0.
   */
  private double f0;            // f_0 = F(p_0)

  /**
   * Function value for PN.
   */
  private double fN;            // f_N = F(p_N)

  /**
   * Function value for (2 * PN - P0)
   */
  private double fE;            // f_E = F(2 * p_N - p_0) = F(p_E)

  /**
   * PE = 2 * PN - P0
   */
  private double[] pE;          // p_E = 2 * p_N - p_0

  /**
   * Stores magnitude of the largest decrease along one particular search direction of the present iteration.
   */
  private double maxDeltaF;     // magnitude of the largest decrease along one particular search direction
                                // of the present iteration

  /**
   * Index of the search direction corresponding to maxDeltaF.
   */
  private int indexU;
  
  double val;
  double deltaF;

  ///////////////////////////////////////////

  public PowellMethod() {
    this.stopCondition = new SimpleStopCondition();
    // initial value is dummy for now
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 200);
    this.telemetry = new ValuePointTelemetry();
  }

  public void init(ObjectiveFunction function) {

    this.function = function;
    dimension = function.getDimension();

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    switch (lineSearch) {
      case BRENT_WITHOUT:
        this.lineSearchMethod = new LineSearchBrentNoDerivatives(function);
        break;
      case BRENT_WITH:
        this.lineSearchMethod = new LineSearchBrentWithDerivatives(function);
        break;
      default:
        throw new IllegalStateException("Unknown line search method.");
    }

    pI = new double[dimension];
    pE = new double[dimension];
    u = new Point[dimension];
    uNew = new double[dimension];    

    for (int i = 0; i < dimension; i++) {
      double[] point = new double[dimension];
      Arrays.fill(point, 0);
      point[i] = 1.0;
      u[i] = Point.at(point);
    }

    p0 = Point.random(dimension, min, max).toArray();
    solution = ValuePoint.at(Point.at(p0), function);
    // now we can set initial value correctly
    this.stopCondition.setInitialValue(solution.getValue());
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {

    System.arraycopy(p0, 0, pI, 0, dimension);

    f0 = function.valueAt(Point.at(p0));

    maxDeltaF = 0.0;
    for (int i = 0; i < dimension; i++) {
      val = function.valueAt(Point.at(pI));
      deltaF = val - lineSearchMethod.minimize(pI, u[i].toArray());
      if (deltaF >= maxDeltaF) {
        maxDeltaF = deltaF;
        indexU = i;
      }
    }

    //pI ~ pN
    for (int i = 0; i < dimension; i++)
      pE[i] = 2 * pI[i] - p0[i];


    fN = function.valueAt(Point.at(pI));
    fE = function.valueAt(Point.at(pE));

    if ((fE < f0) && (2 * (f0 - 2 * fN + fE) * (f0 - fN - maxDeltaF) * (f0 - fN - maxDeltaF) -
                      maxDeltaF * (f0 - fE) * (f0 - fE)) < 0) {
      for (int i = 0; i < dimension; i++)
        uNew[i] = pI[i] - p0[i];
      u[indexU] = Point.at(uNew);
      lineSearchMethod.minimize(pI, uNew);
    }


    System.arraycopy(pI, 0, p0, 0, dimension);

    solution = ValuePoint.at(Point.at(p0), function);

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

  ////

  public LineSearchType getLineSearch() {
    return lineSearch;
  }

  public void setLineSearch(LineSearchType lineSearch) {
    this.lineSearch = lineSearch;
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
}