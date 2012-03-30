package cz.cvut.felk.cig.jcool.benchmark.method.gradient.lm;

import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearch;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearchBrentNoDerivatives;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearchBrentWithDerivatives;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.PALStopCondition;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import cz.cvut.felk.cig.jcool.utils.VectorAlgebra;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Random;

/**
 * Levenberg-Marquardt optimization method class. 
 */
@Component(name = "Levenberg-Marquardt")
public class LevenbergMarquardtMethod implements OptimizationMethod<ValuePointTelemetry> {

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
   * Current solution candidate.
   */
  private ValuePoint solution;              // solution vector
  private int dimension;              // number of variables to optimize

  /**
   * Current point coordinates.
   */
  double[] a;

  /**
   * New point coordinates.
   */
  double[] newA;

  /**
   * Hessian matrix.
   */
  double[][] H;

  /**
   * Gradient vector.
   */
  double[] g;

  /**
   * Search direction/step.
   */
  double[] delta;

  /**
   * Multiplier constant.
   */
  double lambda;

  ////////

  public LevenbergMarquardtMethod() {
    this.stopCondition = new SimpleStopCondition();
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointTelemetry();
  }

  public void init(ObjectiveFunction function) {
    Random generator = new Random();
    this.function = function;
    dimension = function.getDimension();

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    a = new double[dimension];

    for (int i = 0; i < dimension; i++)
      a[i] = generator.nextDouble() * (max - min) + min;

    newA = new double[dimension];
    H = new double[dimension][dimension];
    g = new double[dimension];
    delta = new double[dimension];

    lambda = 0.00001;

    solution = ValuePoint.at(Point.at(a), Double.POSITIVE_INFINITY);
    stopCondition.setInitialValue(Double.POSITIVE_INFINITY);
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {

    H = function.hessianAt(Point.at(a)).toArray();

    for (int i = 0; i < dimension; i++) {
      for (int j = 0; j < dimension; j++)
        H[i][j] *= 0.5;
      H[i][i] *= (1.0 + lambda);
    }

    g = function.gradientAt(Point.at(a)).toArray();

    for (int i = 0; i < dimension; i++)
	    g[i] *= -0.5;

    try {
      delta = VectorAlgebra.GaussJordanElimination(H, g, dimension);
    } catch (Exception e) {
      System.err.print(e.getMessage());
      e.printStackTrace();
    }

    for (int i = 0; i < dimension; i++)
      newA[i] = a[i] + delta[i];

    if (function.valueAt(Point.at(newA)) < solution.getValue()) {
      lambda *= 0.1;
      System.arraycopy(newA, 0, a, 0, dimension);
      solution = ValuePoint.at(Point.at(a), function);
    } else 
      lambda *= 10;



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

  //////

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