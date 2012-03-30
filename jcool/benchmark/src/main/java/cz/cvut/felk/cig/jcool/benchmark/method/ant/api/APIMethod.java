package cz.cvut.felk.cig.jcool.benchmark.method.ant.api;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * API optimization method class.
 */
@Component(name = "API")
public class APIMethod implements OptimizationMethod<ValuePointListTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  /**
   * Iteration counter used for moving the nest. 
   */
  private long iteration;

  private ValuePointListTelemetry telemetry;
  private Consumer<? super ValuePointListTelemetry> consumer;

  /**
   * Number of ants.
   */
  @Property(name = "Population Size")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int populationSize = 100;     // number of ants

  /**
   * Number of hunting sites for each ant.
   */
  @Property(name = "Hunting Sites")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int huntingSitesCount = 3;   // number of hunting sites for one ant

  /**
   * Number of generations (iterations) after which the nest is moved to the global best value point.
   */
  @Property(name = "Move Generation")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int moveGeneration = 30;     // number of generations before moving the nest

  /**
   * Number of iterations without solution improvement before removing a hunting site.
   */
  @Property(name = "Starvation")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int starvation = 8;          // number of iterations without solution before removing hunting site

  @Property(name = "Parameter Minimum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;          // parameter minimum

  @Property(name = "Parameter Maximum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;           // parameter maximum

  @Property(name = "Gradient Heuristic Weight")
  @Range(from = 0.0, to = 1.0)
  private double gradientWeight = 0.0; // gradient heuristic impact

  /**
   * Actual nest position.
   */
  private double[] nestPosition;

  /**
   * Support variable used to convert ants' data to telemetry information.
   */
  private ValuePoint[] antData;

  /**
   * Ants in the colony.
   */
  private Ant[] ants;

  private Random generator;

  /**
   * Globally best solution.
   */
  private ValuePoint bestSolution; // globally bestSolution solution

  /**
   * ******************************************************************
   */

  public APIMethod() {
    this.stopCondition = new SimpleStopCondition();
    // dummy value at first
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointListTelemetry();
  }

  public void init(ObjectiveFunction function) {
    this.function = function;

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    ants = new Ant[populationSize];
    antData = new ValuePoint[populationSize];
    for (int i = 0; i < populationSize; i++)
      ants[i] = new Ant(i, function.getDimension(), populationSize, starvation);

    // place nest
    nestPosition = new double[function.getDimension()];
    for (int i = 0; i < function.getDimension(); i++)
      nestPosition[i] = Math.random() * (max - min) + min;

    generator = new Random();
    bestSolution = ValuePoint.at(Point.at(nestPosition), function);
    // initialize with correct value
    this.stopCondition.setInitialValue(function.valueAt(Point.at(nestPosition)));

    iteration = 0;
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {

    iteration++;

    for (int ant = 0; ant < populationSize; ant++) {
      ants[ant].checkHuntingSitesQueue(huntingSitesCount, nestPosition);
      
      if (ants[ant].huntingSiteAdded())
        ants[ant].explore(ants[ant].lastCreatedHS(), function);
      else {
        if (ants[ant].lastSearchSuccessful()) {
          ants[ant].explore(ants[ant].lastVisitedHS(), function);
        } else {
          ants[ant].explore(ants[ant].getRandomHS(), function);
        }
      }

      compareGlobal(ants[ant].fitness, ants[ant].position);

      antData[ant] = ValuePoint.at(Point.at(ants[ant].position), ants[ant].fitness);
    }

    tandemRun();

    if (iteration % moveGeneration == 0)
      moveNest();

        ArrayList<ValuePoint> tmp = new ArrayList<ValuePoint>(Arrays.asList(antData));
    if (!tmp.contains(bestSolution))
      tmp.add(bestSolution);

    telemetry = new ValuePointListTelemetry(tmp);
    
    if (consumer != null)
      consumer.notifyOf(this);

    stopCondition.setValue(bestSolution.getValue());
  }

  public void addConsumer(Consumer<? super ValuePointListTelemetry> consumer) {
    this.consumer = consumer;
  }

  public ValuePointListTelemetry getValue() {
    return telemetry;
  }

  // --------------------------------------------------------------------------------------

  public void compareGlobal(double fitness, double[] position) {
    if (fitness < bestSolution.getValue()) {
      bestSolution = ValuePoint.at(Point.at(position), fitness);
    }
  }

  private void tandemRun() {
    /* 2 ants, copy of the best hunting site */
    int ant1, ant2;

    ant1 = generator.nextInt(populationSize);

    do
      ant2 = generator.nextInt(populationSize);
    while (ant1 == ant2);

    if (ants[ant1].getFitness() > ants[ant2].getFitness()) {
      int tmp = ant1;
      ant1 = ant2;
      ant2 = tmp;
    }

    ants[ant1].tandemRun(ants[ant2]);
  }

  private void moveNest() {
    nestPosition = bestSolution.getPoint().toArray();

    for (int ant = 0; ant < populationSize; ant++) {
      ants[ant].forgetAll();
    }
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  public double getGradientWeight() {
    return gradientWeight;
  }

  public void setGradientWeight(double gradientWeight) {
    this.gradientWeight = gradientWeight;
  }

  public int getHuntingSitesCount() {
    return huntingSitesCount;
  }

  public void setHuntingSitesCount(int huntingSitesCount) {
    this.huntingSitesCount = huntingSitesCount;
  }

  public int getMoveGeneration() {
    return moveGeneration;
  }

  public void setMoveGeneration(int moveGeneration) {
    this.moveGeneration = moveGeneration;
  }

  public int getStarvation() {
    return starvation;
  }

  public void setStarvation(int starvation) {
    this.starvation = starvation;
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