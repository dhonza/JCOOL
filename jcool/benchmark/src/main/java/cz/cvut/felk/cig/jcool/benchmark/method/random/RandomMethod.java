package cz.cvut.felk.cig.jcool.benchmark.method.random;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

@Component(name = "Random Search Algorithm")
public class RandomMethod implements OptimizationMethod<ValuePointTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  private ValuePointTelemetry telemetry;
  private Consumer<? super ValuePointTelemetry> consumer;

  @Property(name = "Min")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;

  @Property(name = "Max")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;

  @Property(name = "Gradient Heuristic Weight")
  @Range(from = 0.0, to = 1.0)
  private double gradientWeight = 0.0;     // gradient heuristic impact

  @Property(name = "Number Of Iterations Before Randomization")
  @Range(from = 0, to = Integer.MAX_VALUE)
  private int cycle = 10;                      // # of iterations before randomization

  private ValuePoint solution;              // solution vector
  private int dimension;              // number of variables to optimize
  private ValuePoint best;      // global best

  private int iteration;

  public RandomMethod() {
    this.stopCondition = new SimpleStopCondition();
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointTelemetry();
  }

  public void init(ObjectiveFunction function) {

    this.function = function;
    dimension = function.getDimension();

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    best = ValuePoint.at(Point.getDefault(), Double.POSITIVE_INFINITY);
    stopCondition.setInitialValue(Double.POSITIVE_INFINITY);

    iteration = 1;
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {

    if ((iteration % cycle) == 1)
      // random solution
      solution = ValuePoint.at(Point.random(dimension, min, max), function);
    else {
      // improve solution using gradient
      double[] gradient = function.gradientAt(solution.getPoint()).toArray();

      double[] point = solution.getPoint().toArray();
      for (int d = 0; d < dimension; d++)
        point[d] -= gradientWeight * gradient[d];

      solution = ValuePoint.at(Point.at(point), function);
    }

    // improvement?
    if (solution.getValue() < best.getValue())
      best = solution;

    telemetry = new ValuePointTelemetry(solution);
    if (consumer != null)
      consumer.notifyOf(this);

    stopCondition.setValue(best.getValue());
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

  public double getGradientWeight() {
    return gradientWeight;
  }

  public void setGradientWeight(double gradientWeight) {
    this.gradientWeight = gradientWeight;
  }

  public int getCycle() {
    return cycle;
  }

  public void setCycle(int cycle) {
    this.cycle = cycle;
  }
}