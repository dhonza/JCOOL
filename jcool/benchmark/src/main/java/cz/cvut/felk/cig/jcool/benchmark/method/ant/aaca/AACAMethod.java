package cz.cvut.felk.cig.jcool.benchmark.method.ant.aaca;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * AACA optimization method class.
 */
@Component(name = "AACA: Adaptive Ant Colony Algorithm")
public class AACAMethod implements OptimizationMethod<ValuePointListTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  private ValuePointListTelemetry telemetry;
  private Consumer<? super ValuePointListTelemetry> consumer;

  /**
   * Number of ants.
   */
  @Property(name = "Population Size")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int populationSize = 20;     // number of ants

  /**
   * Number of bits used for encoding.
   */
  @Property(name = "Number of Bits in Searching Variables")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int encodingLength = 8;      // number of bits in searching variables

  /**
   * Evaporation factor Lambda = <0, 1>.
   */
  @Property(name = "Evaporation Factor - Lambda")
  @Range(from = 0.0, to = 1.0)
  private double evaporationFactor = 0.8; // lambda <0,1>

  /**
   * Pheromone index Beta >= 0.
   */
  @Property(name = "Pheromone Index: Beta - Ant-like Behaviour")
  @Range(from = 0.0, to = 1.0)
  private double pheromoneIndex = 0.1; // beta >= 0

  /**
   * Cost index Delta >= 0.
   */
  @Property(name = "Cost Index: Delta - Heuristic Behaviour")
  @Range(from = 0.0, to = 10.0)
  private double costIndex = 0.0;      // delta >= 0

  @Property(name = "Parameter Minimum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;          // parameter minimum

  @Property(name = "Parameter Maximum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;           // parameter maximum

  @Property(name = "Gradient Heuristic Weight - Contribution of the Gradient of the Error Surface")
  @Range(from = 0.0, to = 1.0)
  private double gradientWeight = 0.0; // gradient heuristic impact

  private int dimension;         // number of variables to optimize

  /**
   * Value points representing individual ants in the colony.
   */
  private ValuePoint[] colony;   // ants in colony

  /**
   * Globally best value point.
   */
  private ValuePoint globalBest; // globally best variables vector

  /**
   * Path of the current ant generated according to pheromone levels.
   */
  private int[][] path;

  /**
   * Cost of the newly generated solution candidate.
   */
  private double solutionCandidateCost;

  /**
   * Newly generated solution candidate.
   */
  private double[] solutionCandidateVector;

  /**
   * Total amount of added pheromone.
   */
  private double[] pheromoneSum;

  /**
   * Actual level of pheromone.
   */
  private PheromoneTable pheromone;

  /**
   * Amount of pheromone to be added.
   */
  private PheromoneTable pheromoneAdd;

  /**
   * Ctor.
   */
  public AACAMethod() {
    this.stopCondition = new SimpleStopCondition();
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointListTelemetry();
  }

  /**
   * Initializes the method and its variables.
   * @param function Objective function to be minimized.
   */
  public void init(ObjectiveFunction function) {

    this.function = function;
    dimension = function.getDimension();

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    colony = new ValuePoint[populationSize];
    for (int i = 0; i < populationSize; i++) {
      colony[i] = ValuePoint.at(Point.random(dimension, min, max), function);
    }

    pheromone = new PheromoneTable(dimension, encodingLength);
    pheromone.init();
    pheromoneAdd = new PheromoneTable(dimension, encodingLength);

    path = new int[dimension][encodingLength];
    solutionCandidateVector = new double[dimension];
    pheromoneSum = new double[dimension];

    globalBest = ValuePoint.at(Point.getDefault(), Double.POSITIVE_INFINITY);
    stopCondition.setInitialValue(Double.POSITIVE_INFINITY);
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[] {stopCondition};
  }

  /**
   * One iteration of AACA optimization.
   */
  public void optimize() {

    pheromoneAdd.clear();

    for (int d = 0; d < dimension; d++)
      pheromoneSum[d] = 0.0;

    for (int ant = 0; ant < populationSize; ant++) {
      generate();
      evaluate(ant);
      layPheromone();
    }

    evaporate();

    ArrayList<ValuePoint> tmp = new ArrayList<ValuePoint>(Arrays.asList(colony));
    if (!tmp.contains(globalBest))
      tmp.add(globalBest);

    telemetry = new ValuePointListTelemetry(tmp);
    if (consumer != null)
      consumer.notifyOf(this);

    stopCondition.setValue(globalBest.getValue());
  }

  public void addConsumer(Consumer<? super ValuePointListTelemetry> consumer) {
    this.consumer = consumer;
  }

  public ValuePointListTelemetry getValue() {
    return telemetry;
  }

  /**
   *  Generates new path stochastically from the pheromone levels.
   */
  private void generate() {

    double total, ran;
    int last, newLast;

    for (int d = 0; d < dimension; d++) {
      total = pheromone.pheromoneFirst[d][0] + pheromone.pheromoneFirst[d][1];
      newLast = 0;
      ran = Math.random() * total;

      if (pheromone.pheromoneFirst[d][0] < ran)
        newLast++;

      last = newLast;
      path[d][0] = last;
      for (int i = 0; i < encodingLength - 1; i++) {
        total = 0.0;

        for (int j = 0; j < 2; j++)
          total += pheromone.pheromoneOther[d][i][last][j];

        newLast = 0;
        ran = Math.random() * total;

        if (pheromone.pheromoneOther[d][i][last][0] < ran)
          newLast++;

        last = newLast;
        path[d][i + 1] = last;
      }
    }
  }

  /**
   * Computes cost of current solution candidate and adds it to the population.
   * Updates global best value if a new global best value is found.
   */
  private void evaluate(int ant) {
    for (int d = 0; d < dimension; d++) {
      double x = 0;
      long power = 0;
      for (int bit = encodingLength - 1; bit >= 0; bit--) {
        x += path[d][bit] * Math.pow(2, power);
        power++;
      }

      solutionCandidateVector[d] = (x / (Math.pow(2, power) - 1)) * (max - min) + min;
    }

    // pheromone & gradient heuristic
    if (gradientWeight != 0.0)
      addGradient(solutionCandidateVector);

    solutionCandidateCost = function.valueAt(Point.at(solutionCandidateVector));

    colony[ant] = ValuePoint.at(Point.at(solutionCandidateVector), solutionCandidateCost);

    if (solutionCandidateCost <= globalBest.getValue()) {
      globalBest = colony[ant];
    }
  }

  /**
   * Modifies solution using gradient information if required.
   * @param solution actual solution to modify by gradient
   */
  private void addGradient(double[] solution) {
    double[] gradient = function.gradientAt(Point.at(solution)).toArray();

    for (int d = 0; d < dimension; d++)
      solution[d] -= gradientWeight * gradient[d];
  }

  /**
   * Lays pheromone on the path depending on the value of the current candidate solution.  
   */
  private void layPheromone() {
    double add;
    for (int d = 0; d < dimension; d++) {
      add = 1 / (1 + Math.exp(pheromoneIndex * (encodingLength + 1) * (((solutionCandidateCost - globalBest.getValue()) / (globalBest.getValue() / 100)) - costIndex)));
      pheromoneAdd.pheromoneFirst[d][path[d][0]] += add;   
      pheromoneSum[d] += add;

      for (int bit = 1; bit < encodingLength; bit++) {
        //5 / (1 + Math.exp(pheromoneIndex * (encodingLength + 1 - bit) * (((solutionCandidateCost - globalBest.getValue()) / (globalBest.getValue() / 100)) - costIndex)));
        add = 1 / (1 + Math.exp(pheromoneIndex * (encodingLength + 1 - bit) * (((solutionCandidateCost - globalBest.getValue()) / (globalBest.getValue() / 100)) - costIndex)));
        pheromoneAdd.pheromoneOther[d][bit - 1][path[d][bit - 1]][path[d][bit]] += add;
        pheromoneSum[d] += add;
      }
    }
  }

  /**
   * Increases and evaporates pheromone.
   */
  private void evaporate() {
    /* pheromone addition */
    for (int d = 0; d < dimension; d++) {
      if (pheromoneSum[d] > 0) {
        pheromone.pheromoneFirst[d][0] += pheromoneAdd.pheromoneFirst[d][0] / pheromoneSum[d];
        pheromone.pheromoneFirst[d][1] += pheromoneAdd.pheromoneFirst[d][1] / pheromoneSum[d];
        for (int bit = 0; bit < encodingLength - 1; bit++) {
          pheromone.pheromoneOther[d][bit][0][0] += pheromoneAdd.pheromoneOther[d][bit][0][0] / pheromoneSum[d];
          pheromone.pheromoneOther[d][bit][0][1] += pheromoneAdd.pheromoneOther[d][bit][0][1] / pheromoneSum[d];
          pheromone.pheromoneOther[d][bit][1][0] += pheromoneAdd.pheromoneOther[d][bit][1][0] / pheromoneSum[d];
          pheromone.pheromoneOther[d][bit][1][1] += pheromoneAdd.pheromoneOther[d][bit][1][1] / pheromoneSum[d];
        }
      }
    }

    /* pheromone evaporation */
    for (int d = 0; d < dimension; d++) {
      pheromone.pheromoneFirst[d][0] = evaporationFactor * pheromone.pheromoneFirst[d][0] + (1 - evaporationFactor) * pheromoneAdd.pheromoneFirst[d][0];
      pheromone.pheromoneFirst[d][1] = evaporationFactor * pheromone.pheromoneFirst[d][1] + (1 - evaporationFactor) * pheromoneAdd.pheromoneFirst[d][1];
      for (int bit = 0; bit < encodingLength - 1; bit++) {
        pheromone.pheromoneOther[d][bit][0][0] = evaporationFactor * pheromone.pheromoneOther[d][bit][0][0] + (1 - evaporationFactor) * pheromoneAdd.pheromoneOther[d][bit][0][0];
        pheromone.pheromoneOther[d][bit][0][1] = evaporationFactor * pheromone.pheromoneOther[d][bit][0][1] + (1 - evaporationFactor) * pheromoneAdd.pheromoneOther[d][bit][0][1];
        pheromone.pheromoneOther[d][bit][1][0] = evaporationFactor * pheromone.pheromoneOther[d][bit][1][0] + (1 - evaporationFactor) * pheromoneAdd.pheromoneOther[d][bit][1][0];
        pheromone.pheromoneOther[d][bit][1][1] = evaporationFactor * pheromone.pheromoneOther[d][bit][1][1] + (1 - evaporationFactor) * pheromoneAdd.pheromoneOther[d][bit][1][1];
      }
    }

    pheromone.setMinimum();
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  public int getEncodingLength() {
    return encodingLength;
  }

  public void setEncodingLength(int encodingLength) {
    this.encodingLength = encodingLength;
  }

  public double getEvaporationFactor() {
    return evaporationFactor;
  }

  public void setEvaporationFactor(double evaporationFactor) {
    this.evaporationFactor = evaporationFactor;
  }

  public double getPheromoneIndex() {
    return pheromoneIndex;
  }

  public void setPheromoneIndex(double pheromoneIndex) {
    this.pheromoneIndex = pheromoneIndex;
  }

  public double getCostIndex() {
    return costIndex;
  }

  public void setCostIndex(double costIndex) {
    this.costIndex = costIndex;
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

  public double getGradientWeight() {
    return gradientWeight;
  }

  public void setGradientWeight(double gradientWeight) {
    this.gradientWeight = gradientWeight;
  }
}