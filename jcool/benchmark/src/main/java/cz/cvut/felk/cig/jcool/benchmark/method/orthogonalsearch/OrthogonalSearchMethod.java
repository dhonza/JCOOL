package cz.cvut.felk.cig.jcool.benchmark.method.orthogonalsearch;

import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearch;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearchBrentNoDerivatives;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearchBrentWithDerivatives;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.PALStopCondition;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import cz.cvut.felk.cig.jcool.utils.MersenneTwisterFast;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;

/**
 * Orthogonal (stochastic) search optimization method class.
 */
@Component(name = "Orthogonal Search")
public class OrthogonalSearchMethod implements OptimizationMethod<ValuePointTelemetry> {

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
   * Flag whether to use sequential or stochastic search.
   */
  @Property(name = "Use Stochastic Search")
  private boolean useStochastic = false;

  /**
   * Line serach method type.
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

  private int dimension;        // Number of variables to optimize
  private ValuePoint solution;

  /**
   * Current point coordinates.
   */
  private double[] point;

  /**
   * Search directions. Unit vectors.
   */
  private Point[] direction;    // Search directions

  /**
   * Ordering of the search directions.
   */
  private int[] order;          // Order of search directions

  private MersenneTwisterFast generator;

  public OrthogonalSearchMethod() {
    this.stopCondition = new SimpleStopCondition();
    // dummy initial value
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointTelemetry();
  }

  public void init(ObjectiveFunction function) {

    generator = new MersenneTwisterFast();

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

    order = new int[dimension];
    direction = new Point[dimension];
    point = new double[dimension];

    double[] dir = new double[dimension];
    for (int i = 0; i < dimension; i++) {
      order[i] = i;
      Arrays.fill(dir, 0);
      dir[i] = 1.0;
      direction[i] = Point.at(dir);
    }

    solution = ValuePoint.at(Point.random(dimension, min, max), function);
    // now we can set initial value correctly
    this.stopCondition.setInitialValue(solution.getValue());
    point = solution.getPoint().toArray();
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {

    if (useStochastic)
      generator.shuffle(order);

    double val = Double.POSITIVE_INFINITY;
    for (int i = 0; i < dimension; i++)
      val = lineSearchMethod.minimize(point, direction[order[i]].toArray());

    solution = ValuePoint.at(Point.at(point), val);

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

  public boolean isUseStochastic() {
    return useStochastic;
  }

  public void setUseStochastic(boolean useStochastic) {
    this.useStochastic = useStochastic;
  }
}