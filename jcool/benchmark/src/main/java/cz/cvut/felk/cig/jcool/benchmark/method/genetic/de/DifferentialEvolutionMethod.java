package cz.cvut.felk.cig.jcool.benchmark.method.genetic.de;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;

/**
 * DE optimization method class.
 */
@Component(name = "Differential Evolution")
public class DifferentialEvolutionMethod implements OptimizationMethod<ValuePointListTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  private ValuePointListTelemetry telemetry;
  private Consumer<? super ValuePointListTelemetry> consumer;

  /**
   * Population size.
   */
  @Property(name = "Population Size")
  @Range(from = 4, to = Integer.MAX_VALUE)
  private int NP = 16; // recommended <4, 100>; optimum 10+

  /**
   * Mutation constant.
   */
  @Property(name = "Mutation Constant")
  @Range(from = 0.0, to = 2.0)
  private double MC = 0.6; // recommended <0,2>; optimum <0.3, 0.9>

  /**
   * Crossover rating.
   */
  @Property(name = "Crossover Rating")
  @Range(from = 0.0, to = 1.0)
  private double CR = 0.85; // recommended <0,1>; optimum <0.8, 0.9>

  @Property(name = "Min")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double MIN = -10.0;

  @Property(name = "Max")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double MAX = 10.0;

  /**
   * Current population of solution candidates.
   */
  private ValuePoint[] population;

  /**
   * Next (new) population.
   */
  private ValuePoint[] nextPopulation;

  /**
   * Global best solution candidate.
   */
  private ValuePoint best;

  private int dimension;         // number of variables to optimize

  public DifferentialEvolutionMethod() {
    this.stopCondition = new SimpleStopCondition();
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointListTelemetry();
  }

  public void init(ObjectiveFunction function) {

    this.function = function;
    dimension = function.getDimension();

    MIN = Math.max(MIN, function.getMinimum()[0]);
    MAX = Math.min(MAX, function.getMaximum()[0]);

    population = new ValuePoint[NP];
    for (int i = 0; i < NP; i++)
      population[i] = ValuePoint.at(Point.random(dimension, MIN, MAX), function);

    nextPopulation = new ValuePoint[NP];

    best = ValuePoint.at(Point.getDefault(), Double.POSITIVE_INFINITY);
    stopCondition.setInitialValue(Double.POSITIVE_INFINITY);
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {

    for (int i = 0; i < NP; i++)
      makeReproduction(i);

    // swap populations
    population = nextPopulation;
    nextPopulation = new ValuePoint[NP];

    telemetry = new ValuePointListTelemetry(Arrays.asList(population));
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

  /**
   * Creates new solution candidate for given (current) individual and replaces the old one if this one is better.
   * @param ind Index of current individual.
   */
  private void makeReproduction(int ind) {
    int R = (int) (Math.random() * dimension); //wiki

    // setting parents
    double[] child = population[ind].getPoint().toArray();

    int index[] = setParentsIndexes(ind);
    double[] r1 = population[index[0]].getPoint().toArray();
    double[] r2 = population[index[1]].getPoint().toArray();
    double[] r3 = population[index[2]].getPoint().toArray();

    // setting mutation vector: v = (r1 - r2)*F + r3
    for (int i = 0; i < dimension; i++)
      if (Math.random() <= CR || i == R)
        child[i] = r3[i] + MC * (r1[i] - r2[i]);

    // test -> is new ValuePoint better then it's parent ...?
    // '<' ... because we want to minimize the function (error in neural network)
    if (function.valueAt(Point.at(child)) < population[ind].getValue()) {
      nextPopulation[ind] = ValuePoint.at(Point.at(child), function); // new solution is better
      if (nextPopulation[ind].getValue() < best.getValue())
        best = nextPopulation[ind];
    }
    else
      nextPopulation[ind] = population[ind]; // old solution is better
  }

  /**
   * Sets parents indexes. Main parent is the i-th one in the population.
   */  
  private int[] setParentsIndexes(int i) {
    // making indexes for other 3 parents
    int index[] = {-1, -1, -1};
    int current = 0;
    while (current < 3) {
      int ind = (int) Math.round(NP * Math.random());
      // we must select 3 differend parrents
      if ((ind != i) && (ind < NP) && (ind != index[0]) && (ind != index[1]) && (ind != index[2])) {
        index[current] = ind;
        current++;
      }
    }
    return index;
  }

  public int getNP() {
    return NP;
  }

  public void setNP(int NP) {
    this.NP = NP;
  }

  public double getMC() {
    return MC;
  }

  public void setMC(double MC) {
    this.MC = MC;
  }

  public double getCR() {
    return CR;
  }

  public void setCR(double CR) {
    this.CR = CR;
  }

  public double getMIN() {
    return MIN;
  }

  public void setMIN(double MIN) {
    this.MIN = MIN;
  }

  public double getMAX() {
    return MAX;
  }

  public void setMAX(double MAX) {
    this.MAX = MAX;
  }
}