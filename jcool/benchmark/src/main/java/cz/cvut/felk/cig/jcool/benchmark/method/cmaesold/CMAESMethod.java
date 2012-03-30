package cz.cvut.felk.cig.jcool.benchmark.method.cmaesold;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;

/**
 * CMA-ES optimizaiton method class.
 */
@Component(name = "CMA-ES: Covariance Matrix Adaptation Evolution Strategy")
public class CMAESMethod implements OptimizationMethod<ValuePointListTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  @Property(name = "Parameter Minimum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;          // parameter minimum

  @Property(name = "Parameter Maximum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;           // parameter maximum

  /**
   * Initial standard deviation Sigma.
   */
  @Property(name = "Initial standard deviation")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double sigma = 0.2;          // parameter minimum

  private ValuePointListTelemetry telemetry;
  private Consumer<? super ValuePointListTelemetry> consumer;

  /**
   * Main algorithm class.
   */
  private CMAEvolutionStrategy cma;

  /**
   * Fitness values for the population.
   */
  private double[] fitness;

  /**
   * Population of solution candidates.
   */
  double[][] pop;

  public CMAESMethod() {
    this.stopCondition = new SimpleStopCondition();
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointListTelemetry();
  }

  public void init(ObjectiveFunction function) {

    this.function = function;

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    cma = new CMAEvolutionStrategy();
    cma.setDimension(function.getDimension());
    cma.setInitialX(min, max);
    cma.setInitialStandardDeviation(sigma);

    stopCondition.setInitialValue(Double.POSITIVE_INFINITY);

    fitness = cma.init();
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {

    pop = cma.samplePopulation(); // get a new population of solutions
    for (int i = 0; i < pop.length; ++i) {    // for each candidate solution i
      fitness[i] = function.valueAt(Point.at(pop[i])); // fitfun.valueOf() is to be minimized
    }
    cma.updateDistribution(fitness);

    telemetry = new ValuePointListTelemetry(Arrays.asList(getPoints()));
    if (consumer != null)
      consumer.notifyOf(this);

    stopCondition.setValue(cma.getBestFunctionValue());
  }

  private ValuePoint[] getPoints() {
    ValuePoint[] result = new ValuePoint[pop.length];

    for (int i = 0; i < result.length; i++)
      result[i] = ValuePoint.at(Point.at(pop[i]), function);

    return result;
  }

  public void addConsumer(Consumer<? super ValuePointListTelemetry> consumer) {
    this.consumer = consumer;
  }

  public ValuePointListTelemetry getValue() {
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

  public double getSigma() {
    return sigma;
  }

  public void setSigma(double sigma) {
    this.sigma = sigma;
  }
}