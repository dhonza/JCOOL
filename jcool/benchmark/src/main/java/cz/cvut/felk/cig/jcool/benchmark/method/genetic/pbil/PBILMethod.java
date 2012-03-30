package cz.cvut.felk.cig.jcool.benchmark.method.genetic.pbil;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;
import java.util.Random;

/**
 * PBIL optimization method class.
 */
@Component(name = "PBIL: Population-Based Incremental Learning")
public class PBILMethod implements OptimizationMethod<ValuePointListTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  private ValuePointListTelemetry telemetry;
  private Consumer<? super ValuePointListTelemetry> consumer;

  /**
   * Number of bits used for encoding.
   */
  @Property(name = "Bits Per Variable")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int bitsPerVariable = 8;

  /**
   * Population size (number of individuals generated from the probability vector).
   */
  @Property(name = "Population Size")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int populationSize = 50;

  /**
   * Positive learning rate.
   */
  @Property(name = "Learn Rate")
  @Range(from = 0.0, to = 1.0)
  private double learnRate = 0.5;

  /**
   * Negative learning rate.
   */
  @Property(name = "Negative Learn Rate")
  @Range(from = 0.0, to = 1.0)
  private double negLearnRate = 0.3;

  /**
   * Mutation probability.
   */
  @Property(name = "Mutation Probability")
  @Range(from = 0.0, to = 1.0)
  private double mutProb = 0.3;

  /**
   * Mutation shift.
   */
  @Property(name = "Mutation shift")
  @Range(from = 0.0, to = 1.0)
  private double mutShift = 0.5;

  @Property(name = "Min")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10;

  @Property(name = "Max")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10;

  private int dimension;        // number of variables to optimize

  /**
   * Global best solution candidate.
   */
  private ValuePoint best;

  /**
   * Population of solution candidates. 
   */
  private ValuePoint[] population;
  private Random generator;

  /**
   * Total number of bits used per one solution candidate = (number of bits) * (number of dimensions)
   */
  private int totalBits;

  /**
   * The probability vector.
   */
  private double[] probabilityVector;

  /**
   * Encoded individuals.
   */
  private boolean[][] genes;

  /**
   * Costs of individuals.
   */
  private double[] costs;

  /**
   * Population's gene having lowest value.
   */
  boolean[] minGene;

  /**
   * Population's gene having highest value.
   */
  boolean[] maxGene;

  /**
   * Population's lowest value.
   */
  double minCost;

  /**
   * Population's highest value.
   */
  double maxCost;

  public PBILMethod() {
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

    totalBits = bitsPerVariable * dimension;

    probabilityVector = new double[totalBits];
    Arrays.fill(probabilityVector, 0.5);

    genes = new boolean[populationSize][totalBits];
    costs = new double[populationSize];

    population = new ValuePoint[populationSize];

    best = ValuePoint.at(Point.getDefault(), Double.POSITIVE_INFINITY);
    stopCondition.setInitialValue(Double.POSITIVE_INFINITY);
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {

    generatePopulation();
    updateProbabilityVector();
    mutate();

    telemetry = new ValuePointListTelemetry(Arrays.asList(population));
    if (consumer != null)
      consumer.notifyOf(this);

    stopCondition.setValue(best.getValue());
  }

  /**
   * Mutates the probability vector.
   */
  private void mutate() {
    for (int i = 0; i < totalBits; i++)
      if (generator.nextDouble() < mutProb)
        probabilityVector[i] = probabilityVector[i] * (1 - mutShift) + (generator.nextBoolean() ? 1 : 0) * mutShift;
  }

  /**
   * Updates the probability vector using min/max values and pos/neg learning rates.
   */
  private void updateProbabilityVector() {
    for (int i = 0; i < totalBits; i++)
      if (minGene[i] == maxGene[i])
        probabilityVector[i] = probabilityVector[i] * (1 - learnRate) + (minGene[i] ? 1 : 0) * learnRate;
      else {
        double learnRate2 = learnRate + negLearnRate;
        probabilityVector[i] = probabilityVector[i] * (1 - learnRate2) + (minGene[i] ? 1 : 0) * learnRate2;
      }
  }

  /**
   * Generates new population from the probability vector.
   */
  private void generatePopulation() {
    minCost = Double.POSITIVE_INFINITY;
    maxCost = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < populationSize; i++) {
      for (int j = 0; j < totalBits; j++)
        genes[i][j] = (generator.nextDouble() < probabilityVector[j]);
      evaluate(i);
    }
  }

  /**
   * Evaluates individual with the given index. Updates global best, min and max values accordingly.
   * @param index Index of evaluated individual.
   */
  private void evaluate(int index) {
    double[] point = new double[dimension];
    double x;
    long power;
    for (int i = 0; i < dimension; i++) {
      x = 0;
      power = 0;
      for (int j = totalBits - (i * bitsPerVariable) - 1; j >= totalBits - ((i + 1) * bitsPerVariable); j--) {
        x += (genes[index][j] ? 1 : 0) * Math.pow(2, power);
        power++;
      }
      point[i] = (x / Math.pow(2, power - 1)) * (max - min) + min;
    }

    costs[index] = function.valueAt(Point.at(point));

    population[index] = ValuePoint.at(Point.at(point), costs[index]);

    if (costs[index] < best.getValue())
      best = ValuePoint.at(Point.at(point), costs[index]);

    if (costs[index] < minCost) {
      minCost = costs[index];
      minGene = genes[index];
    } else if (costs[index] > maxCost) {
      maxCost = costs[index];
      maxGene = genes[index];
    }
  }

  public void addConsumer(Consumer<? super ValuePointListTelemetry> consumer) {
    this.consumer = consumer;
  }

  public ValuePointListTelemetry getValue() {
    return telemetry;
  }

  public int getBitsPerVariable() {
    return bitsPerVariable;
  }

  public void setBitsPerVariable(int bitsPerVariable) {
    this.bitsPerVariable = bitsPerVariable;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  public double getLearnRate() {
    return learnRate;
  }

  public void setLearnRate(double learnRate) {
    this.learnRate = learnRate;
  }

  public double getNegLearnRate() {
    return negLearnRate;
  }

  public void setNegLearnRate(double negLearnRate) {
    this.negLearnRate = negLearnRate;
  }

  public double getMutProb() {
    return mutProb;
  }

  public void setMutProb(double mutProb) {
    this.mutProb = mutProb;
  }

  public double getMutShift() {
    return mutShift;
  }

  public void setMutShift(double mutShift) {
    this.mutShift = mutShift;
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