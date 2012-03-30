package cz.cvut.felk.cig.jcool.benchmark.method.genetic.de;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import cz.cvut.felk.cig.jcool.utils.MersenneTwisterFast;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.*;


@Component(name = "PAL: Differential Evolution")
public class PALDifferentialEvolutionMethod implements OptimizationMethod<ValuePointListTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  private ValuePointListTelemetry telemetry;
  private Consumer<? super ValuePointListTelemetry> consumer;

  @Property(name = "Population Size")
  @Range(from = 4, to = Integer.MAX_VALUE)
  private int populationSize = 10; // recommended <4, 100>; optimum 10+

  @Property(name = "Weight Factor 1")
  @Range(from = 0.0, to = 2.0)
  private double WF1 = 0.7; // recommended <0,2>; optimum <0.3, 0.9>

  @Property(name = "Weight Factor 2")
  @Range(from = 0.0, to = 2.0)
  private double WF2 = 0.7; // recommended <0,2>; optimum <0.3, 0.9>

  @Property(name = "Crossover Rating")
  @Range(from = 0.0, to = 1.0)
  private double CR = 0.9; // recommended <0,1>; optimum <0.8, 0.9>

  @Property(name="Update Method")
  private UpdateMethod updateMethod = UpdateMethod.RAND_TO_BEST_1_BIN;

  @Property(name = "Min Function Arg")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;

  @Property(name = "Max Function Arg")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;

  public enum UpdateMethod {
    RAND_TO_BEST_1_BIN("Random to Best - WF1, WF2 used", 2), RAND_TO_BEST_2_BIN("Random to Best - WF1, K = WF2 = random used", 2), BEST_2_BIN("Best - 2", 4);
    private String NAME;
    private int NUM_R;

    private UpdateMethod(String name, int numR) {
      NAME = name;
      NUM_R = numR;
    }

    @Override
    public String toString() {
      return NAME;
    }

    public int getNumR() {
      return NUM_R; 
    }
  }

  private ValuePoint[] currentPopulation;
  private ValuePoint[] nextPopulation;
  private ValuePoint best;

  private int dimension;

  private Random rng;
  private int numR;

  public PALDifferentialEvolutionMethod() {
    this.stopCondition = new SimpleStopCondition();
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointListTelemetry();
  }

  public void init(ObjectiveFunction function) {
    rng = new Random();

    this.function = function;
    dimension = function.getDimension();

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    currentPopulation = new ValuePoint[populationSize];
    nextPopulation = new ValuePoint[populationSize];

    best = ValuePoint.at(Point.getDefault(), Double.POSITIVE_INFINITY);
    stopCondition.setInitialValue(Double.POSITIVE_INFINITY);

    numR = updateMethod.getNumR();

    firstGeneration();
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {

    nextGeneration();

    telemetry = new ValuePointListTelemetry(Arrays.asList(currentPopulation));
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

  // --------------------------------------------------------------------------------------

  private void firstGeneration() {
    for (int i = 0; i < populationSize; i++) {
      currentPopulation[i] = ValuePoint.at(Point.random(dimension, min, max), function);
      if (currentPopulation[i].getValue() < best.getValue())
        best = currentPopulation[i];
    }
  }

  private void nextGeneration() {

    for (int r0 = 0; r0 < populationSize; r0++) {
      int R = rng.nextInt(dimension);

      double[] child = currentPopulation[r0].getPoint().toArray();

      Integer[] indexes = getIndexes(r0);

      double[] r1 = currentPopulation[indexes[0]].getPoint().toArray();
      double[] r2 = currentPopulation[indexes[1]].getPoint().toArray();
      double[] r3 = null;
      double[] r4 = null;

      if (updateMethod == UpdateMethod.BEST_2_BIN) {
        r3 = currentPopulation[indexes[2]].getPoint().toArray();
        r4 = currentPopulation[indexes[3]].getPoint().toArray();
      }

      for (int i = 0; i < dimension; i++) {
        if (rng.nextDouble() < CR || i == R)
          switch (updateMethod) {
            case RAND_TO_BEST_1_BIN:
              child[i] += WF1 * (r1[i] - r2[i]) + WF2 * (best.getPoint().toArray()[i] - child[i]);
            break;

            case RAND_TO_BEST_2_BIN:
              double K = rng.nextDouble();
              child[i] += WF1 * (r1[i] - r2[i]) + K * (best.getPoint().toArray()[i] - child[i]);
            break;

            case BEST_2_BIN:
              child[i] = best.getPoint().toArray()[i] + WF1 * (r1[i] + r2[i] - r3[i] - r4[i]);
            break;
          }
      }

      if (function.valueAt(Point.at(child)) < currentPopulation[r0].getValue()) {
        nextPopulation[r0] = ValuePoint.at(Point.at(child), function);

        if (nextPopulation[r0].getValue() < best.getValue())
          best = nextPopulation[r0];
      } else
        nextPopulation[r0] = currentPopulation[r0];
    }

    currentPopulation = nextPopulation;
  }

  private Integer[] getIndexes(int i) {
    Set<Integer> result = new TreeSet<Integer>();
    int index;
    do {
      index = rng.nextInt(populationSize);
      if (index != i && !result.contains(i))
        result.add(index);
    } while (result.size() != numR);

    return result.toArray(new Integer[numR]);
  }

  ////

  public int getPopulationSize() {
    return populationSize;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  public double getWF1() {
    return WF1;
  }

  public void setWF1(double WF1) {
    this.WF1 = WF1;
  }

  public double getCR() {
    return CR;
  }

  public void setCR(double CR) {
    this.CR = CR;
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

  public double getWF2() {
    return WF2;
  }

  public void setWF2(double WF2) {
    this.WF2 = WF2;
  }

  public UpdateMethod getUpdateMethod() {
    return updateMethod;
  }

  public void setUpdateMethod(UpdateMethod updateMethod) {
    this.updateMethod = updateMethod;
  }
}