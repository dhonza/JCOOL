package cz.cvut.felk.cig.jcool.benchmark.method.ant.daco;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;
import java.util.Random;

/**
 * DACO optimization method class.
 */
@Component(name = "DACO: Direct Ant Colony Algorithm")
public class DACOMethod implements OptimizationMethod<ValuePointListTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  private ValuePointListTelemetry telemetry;
  private Consumer<? super ValuePointListTelemetry> consumer;

  /**
   * Number of ants.
   */
  @Property(name = "Number Of Ants In Population")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int populationSize = 100;             // number of ants in population

  /**
   * Evaporation factor Lambda.
   */
  @Property(name = "Evaporation Factor - Lambda")
  @Range(from = 0.0, to = 1.0)
  private double evaporationFactor = 0.2;       // lambda <0,1>

  @Property(name = "Parameter Minimum")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;                     // parameter minimum

  @Property(name = "Parameter Maximum")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;                     // parameter maximum

  @Property(name = "Gradient Heuristic Impact")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double gradientWeight = 0.0;     // gradient heuristic impact

  private int dimension;              // number of variables to optimize

  /**
   * Globally best solution candidate.
   */
  private ValuePoint best;      // global best

  /**
   * Ants' population means.
   */
  private double[] means;

  /**
   * Ants' population devations.
   */
  private double[] deviations;

  /**
   * Ants' paths.
   */
  private ValuePoint[] paths;

  private Random generator;

  public DACOMethod() {
    this.stopCondition = new SimpleStopCondition();
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointListTelemetry();
  }

  public void init(ObjectiveFunction function) {

    generator = new Random();

    this.function = function;
    dimension = function.getDimension();

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    means = new double[dimension];
    deviations = new double[dimension];
    paths = new ValuePoint[populationSize];

    best = ValuePoint.at(Point.getDefault(), Double.POSITIVE_INFINITY);
    stopCondition.setInitialValue(Double.POSITIVE_INFINITY);

    for (int i = 0; i < dimension; i++) {
      means[i] = (generator.nextDouble() * (max - min)) + min;
      deviations[i] = (max - min) / 2;
    }
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {

    for (int ant = 0; ant < populationSize; ant++) {

      // generate solution
      double[] newPoint = new double[dimension];
      for (int d = 0; d < dimension; d++)
        newPoint[d] = (generator.nextGaussian() * deviations[d]) + means[d];

      // improve solution using gradient
      if (gradientWeight != 0.0) {
        double[] gradient = function.gradientAt(Point.at(newPoint)).toArray();
        for (int d = 0; d < dimension; d++)
          newPoint[d] -= gradientWeight * gradient[d];
      }

      // get solution error (& update best solution
      double error = function.valueAt(Point.at(newPoint));
      paths[ant] = ValuePoint.at(Point.at(newPoint), error);

      if (error < best.getValue())
        best = paths[ant]; //ValuePoint.at(Point.at(newPoint), error);
    }

    // update pheromone
    double[] bestVect = best.getPoint().toArray();
    for (int i = 0; i < dimension; i++) {
      deviations[i] = (1 - evaporationFactor) * deviations[i] + evaporationFactor * Math.abs(bestVect[i] - means[i]);
      means[i] = (1 - evaporationFactor) * means[i] + evaporationFactor * bestVect[i];
    }

    telemetry = new ValuePointListTelemetry(Arrays.asList(paths));
    if (consumer != null)
      consumer.notifyOf(this);

    stopCondition.setValue(best.getValue());
  }

  public void addConsumer(Consumer<? super ValuePointListTelemetry> consumer) {
    this.consumer = consumer;
  }

  public ValuePointListTelemetry getValue() {
    return telemetry;
  }

  public double getGradientWeight() {
    return gradientWeight;
  }

  public void setGradientWeight(double gradientWeight) {
    this.gradientWeight = gradientWeight;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  public double getEvaporationFactor() {
    return evaporationFactor;
  }

  public void setEvaporationFactor(double evaporationFactor) {
    this.evaporationFactor = evaporationFactor;
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