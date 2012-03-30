/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.benchmark.method.gradient.cg;

import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearch;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearchBrentNoDerivatives;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch.LineSearchBrentWithDerivatives;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.PALStopCondition;
import cz.cvut.felk.cig.jcool.core.Consumer;
import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.core.OptimizationMethod;
import cz.cvut.felk.cig.jcool.core.Point;
import cz.cvut.felk.cig.jcool.core.StopCondition;
import cz.cvut.felk.cig.jcool.core.ValuePoint;
import cz.cvut.felk.cig.jcool.core.ValuePointTelemetry;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import cz.cvut.felk.cig.jcool.utils.VectorAlgebra;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * @author drchaj1
 *         Method types are described in:
 *         [1] Nocedal, J. and Wright, S. J. (1999) Numerical Optimization. Springer.
 */
@Component(name = "CGM")
public class ConjugateGradientMethod implements OptimizationMethod<ValuePointTelemetry> {

  /**
   * Number of dimensions.
   */
  private int n; //dimension

  @Property(name = "Parameter Minimum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;          // parameter minimum

  @Property(name = "Parameter Maximum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;           // parameter maximum

  /**
   * Gradient vector.
   */
  private double[] g = new double[0];

  /**
   * Current point.
   */
  private double[] x = new double[0];

  /**
   * Conjugate search direction.
   */
  private double[] p = new double[0];

  /**
   * Previous gradient vector.
   */
  private double[] gold;

  /**
   * Old conjugate direction.
   */
  private double[] pold;

  private PALStopCondition stopCondition;

  /**
   * Current function value.
   */
  private double fx;

  private Consumer<? super ValuePointTelemetry> consumer;

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

  public LineSearchType getLineSearch() {
    return lineSearch;
  }

  public void setLineSearch(LineSearchType lineSearch) {
    this.lineSearch = lineSearch;
  }

  private LineSearch lineSearchMethod;

  public enum UpdateMethod {
    FLETCHER_REEVES,
    POLAK_RIBIERE,
    BEALE_SORENSON_HESTENES_STIEFEL
  }

  /**
   * Update formula type.
   */
  @Property
  private UpdateMethod updateMethod = UpdateMethod.BEALE_SORENSON_HESTENES_STIEFEL;

  public UpdateMethod getUpdateMethod() {
    return updateMethod;
  }

  public void setUpdateMethod(UpdateMethod updateMethod) {
    this.updateMethod = updateMethod;
  }

  @Property(name = "method Plus")
  private boolean usePlusMethod = false;

  public ConjugateGradientMethod() {
    this.stopCondition = new PALStopCondition(MachineAccuracy.EPSILON, MachineAccuracy.EPSILON, 200);
  }

  public void setUsePlusMethod(boolean usePlusMethod) {
    this.usePlusMethod = usePlusMethod;
  }

  public boolean isUsePlusMethod() {
    return usePlusMethod;
  }

  public boolean getUsePlusMethod() {
    return usePlusMethod;
  }

  public void init(ObjectiveFunction function) {
    this.n = function.getDimension();
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

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    p = new double[n];
    gold = new double[n];
    pold = new double[n];

    Point startingPoint = Point.random(n, min, max);
    this.fx = function.valueAt(startingPoint);
    this.g = function.gradientAt(startingPoint).toArray();
    this.x = startingPoint.toArray();

    this.stopCondition.init(fx, startingPoint);

    //set p0 to steepest descent direction p0 = -g0
    for (int i = 0; i < p.length; i++) {
      p[i] = -g[i];
    }
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() throws OptimizationException {
    double beta; //see [1]
    double gg, dgg; //temporary for computation of a new direction
    double slope;
    double orthoTest; //orthogonality test

    //save old gradient
    System.arraycopy(g, 0, gold, 0, n);

    //perform line search
    fx = lineSearchMethod.minimize(x, p, fx, g);

    Point currentPoint = Point.at(x);
    //test for convergence
    stopCondition.setValues(fx, currentPoint);
    if (stopCondition.isConditionMet()) {
      return;
    }

    //we have a new solution, now we have to compute a direction for the next iteration
    if (consumer != null)
      consumer.notifyOf(this);

    //compute beta for given method
    gg = 0.0;
    dgg = 0.0;
    for (int i = 0; i < n; i++) {
      switch (updateMethod) {
        case BEALE_SORENSON_HESTENES_STIEFEL:
          dgg += g[i] * (g[i] - gold[i]);
          gg += p[i] * (g[i] - gold[i]);
          break;
        case POLAK_RIBIERE:
          dgg += g[i] * (g[i] - gold[i]);
          gg += gold[i] * gold[i];
          break;
        case FLETCHER_REEVES:
          dgg += g[i] * g[i];
          gg += gold[i] * gold[i];
          break;
      }
    }

    //gradient ortogonality test
    orthoTest = Math.abs(VectorAlgebra.dotProduct(g, gold)) / VectorAlgebra.dotProduct(g, g);

    if (orthoTest >= 0.1) {
//                beta = 0.0;
      beta = dgg / gg;
    } else {
      beta = dgg / gg; //now we have beta
    }

    if (Double.isNaN(beta))
      return;

    if (usePlusMethod && beta < 0) {
//                Nocedal, J. and Wright, S. J. (1999) Numerical Optimization. Springer.
//                TODO check - it's not clear if it is appropriate to all methods
//                they mention only Polak-Ribiere
      beta = 0.0;
    }

    //compute a new conjugate direction p(k+1) = -g(k+1) + beta(k+1)*p(k)
    System.arraycopy(p, 0, pold, 0, n); //but save the previous
    for (int i = 0; i < n; i++) {
      p[i] = -g[i] + beta * pold[i];
    }

    //compute slope
    slope = VectorAlgebra.dotProduct(p, g);
    if (slope >= 0.0) {//if the slope is positive reset to the steepest descent direction
      //TODO check this...
      //logger.fatal("slope >= 0: implement!!");
                for (int i = 0; i < n; i++) {
                    p[i] = -g[i];
                }
    }
  }

  public void addConsumer(Consumer<? super ValuePointTelemetry> consumer) {
    this.consumer = consumer;
  }

  public ValuePointTelemetry getValue() {
    return new ValuePointTelemetry(ValuePoint.at(Point.at(x), fx));
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