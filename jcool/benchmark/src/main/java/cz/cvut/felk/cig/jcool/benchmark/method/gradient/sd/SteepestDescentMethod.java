/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.benchmark.method.gradient.sd;

import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearch;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearchBrentNoDerivatives;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearchBrentWithDerivatives;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.PALStopCondition;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.Consumer;
import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.OptimizationMethod;
import cz.cvut.felk.cig.jcool.core.Point;
import cz.cvut.felk.cig.jcool.core.SingleSolution;
import cz.cvut.felk.cig.jcool.core.Solution;
import cz.cvut.felk.cig.jcool.core.StopCondition;
import cz.cvut.felk.cig.jcool.core.ValuePoint;
import cz.cvut.felk.cig.jcool.core.ValuePointTelemetry;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Steepest descent optimization method.
 *
 * @author ytoh
 */
@Component(name = "Steepest descent optimization")
public class SteepestDescentMethod implements OptimizationMethod<ValuePointTelemetry> {

  @Property(name = "Select line search")
  private LineSearchType lineSearch = LineSearchType.BRENT_WITHOUT;

  @Property(name = "Parameter Minimum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;          // parameter minimum

  @Property(name = "Parameter Maximum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;           // parameter maximum

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

  // function to optimize
  private ObjectiveFunction function;

  private ValuePointTelemetry telemetry;

  // values to work with
  private double[] gradient;
  private double[] x;
  private double[] p;
  private double fx = 0;
  // stop when the optimized value is not getting any better
  private SimpleStopCondition stopCondition;
  // remember Consumers interested in my telemetry
  private Consumer<? super ValuePointTelemetry> consumer;
  // subroutine to use
  private LineSearch lineSearchMethod;

  public SteepestDescentMethod() {
    this.stopCondition = new SimpleStopCondition();
    // initial dummy value
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 2);
    this.telemetry = new ValuePointTelemetry();
  }

  public LineSearchType getLineSearch() {
    return lineSearch;
  }

  public void setLineSearch(LineSearchType lineSearch) {
    this.lineSearch = lineSearch;
  }

  public void init(ObjectiveFunction function) {
    this.function = function;

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    gradient = new double[function.getDimension()];
    x = new double[function.getDimension()];
    p = new double[function.getDimension()];

    switch (lineSearch) {
      case BRENT_WITHOUT:
        this.lineSearchMethod = new LineSearchBrentNoDerivatives(function);
        break;
      case BRENT_WITH:
        this.lineSearchMethod = new LineSearchBrentWithDerivatives(function);
        break;
      default:
        throw new IllegalStateException("Unknown linesearch method");
    }

    p = new double[function.getDimension()];
    Point startingPoint = Point.random(function.getDimension(), min, max);
    this.fx = function.valueAt(startingPoint);
    this.gradient = function.gradientAt(startingPoint).toArray();
    this.x = startingPoint.toArray();
    // now we can set initial value to correct value
    this.stopCondition.setInitialValue(function.valueAt(startingPoint));
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {
    for (int i = 0; i < p.length; i++) {
      p[i] = -gradient[i];
    }

    //Line search
    fx = lineSearchMethod.minimize(x, p, fx, gradient);

    telemetry = new ValuePointTelemetry(ValuePoint.at(Point.at(x), fx));

    if (consumer != null)
      consumer.notifyOf(this);

    //test for convergence
    stopCondition.setValue(fx);
  }

  public Solution finish() {
    return new SingleSolution(Point.at(x), fx);
  }

  public void addConsumer(Consumer<? super ValuePointTelemetry> consumer) {
    this.consumer = consumer;
  }

  public ValuePointTelemetry getValue() {
    return telemetry;
  }


}
