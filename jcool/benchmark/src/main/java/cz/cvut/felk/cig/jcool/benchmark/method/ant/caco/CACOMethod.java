package cz.cvut.felk.cig.jcool.benchmark.method.ant.caco;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;
import java.util.Random;

/**
 * CACO optimization method class.
 */
@Component(name = "CACO: Continuous Ant Colony Optimization")
public class CACOMethod implements OptimizationMethod<ValuePointListTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  private ValuePointListTelemetry telemetry;
  private Consumer<? super ValuePointListTelemetry> consumer;

  /**
   * Number of ants (directions).
   */
  @Property(name = "Number Of Directions To Search")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int directionsCount = 20;     // number of ants

  /**
   * Local search radius for each direction.
   */
  @Property(name = "Search Radius")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double searchRadius = 50.0;        // initial search radius

  /**
   * Radius multiplier.
   */
  @Property(name = "Search Radius Decrease Speed")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double radiusMultiplier = 0.8;    // search radius decrease speed

  /**
   * Number of generations (iterations) before decreasing the local search radius.
   */
  @Property(name = "Generations Before Decrease")
  @Range(from = 0, to = Integer.MAX_VALUE)
  private int radiusGeneration = 30;    // generations befora decrease

  /**
   * Initial pheromone level.
   */
  @Property(name = "Initial Pheromone Amount")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double startingPheromone = 0.1;   // initial pheromone amount

  /**
   * Minimal pheromone level.
   */
  @Property(name = "Minimal Pheromone Amount")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double minimumPheromone = 0.1;    // minimum pheromone amount

  /**
   * Pheromone amount to add.
   */
  @Property(name = "Pheromone Amount To Add")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double addPheromone = 0.3;        // pheromone amount to add

  /**
   * Evaporation factor.
   */
  @Property(name = "Pheromone Evaporation")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double evaporation = 0.3;

  @Property(name = "Parameter Minimum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;          // parameter minimum

  @Property(name = "Parameter Maximum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;           // parameter maximum

  @Property(name = "Gradient Heuristic Impact")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double gradientWeight = 0.0;     // gradient heuristic impact

  private Random generator;

  /**
   * Iteration counter. Used for evaporation and local search radius decrease.
   */
  private int iteration;              // current iteration

  /**
   * Globally best solution cadnidate.
   */
  private ValuePoint globalBest;     // global best

  /**
   * Directions (ants) in the colony.
   */
  private Direction[] directions;     // directions (positions, pheromone)

  public CACOMethod() {
    this.stopCondition = new SimpleStopCondition();
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointListTelemetry();
  }

  public void init(ObjectiveFunction function) {

    generator = new Random();

    this.function = function;

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    directions = new Direction[directionsCount];

    double[] nest = Point.random(function.getDimension(), min, max).toArray();

    for (int i = 0; i < directionsCount; i++)
      directions[i] = new Direction(nest, startingPheromone, minimumPheromone, addPheromone, gradientWeight, function, min / 10, max / 10);

    globalBest = ValuePoint.at(Point.getDefault(), Double.POSITIVE_INFINITY);
    stopCondition.setInitialValue(Double.POSITIVE_INFINITY);

    iteration = 1;
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {
    int vecNum;
    double rand;
    double sum = 0;

    for (int d = 0; d < directionsCount; d++)
      sum += directions[d].getPheromone();

    rand = generator.nextDouble() * sum;
    vecNum = 0;
    sum = 0;
    while (sum < rand)
      sum += directions[vecNum++].getPheromone();
    vecNum--;

    directions[vecNum].explore(searchRadius, function, min, max);

    if (iteration % radiusGeneration == 0) {
      searchRadius *= radiusMultiplier;
      for (int i = 0; i < directionsCount; i++)
        directions[i].evaporatePheromone(evaporation);
    }

    // find direction with best error
    for (int d = 0; d < directionsCount; d++)
      if (directions[d].getGlobalBest().getValue() < globalBest.getValue())
        globalBest = directions[d].getGlobalBest();

    // distribute best error
    for (int d = 0; d < directionsCount; d++)
      directions[d].setGlobalBest(globalBest);

    iteration++;

    telemetry = new ValuePointListTelemetry(Arrays.asList(getData()));
    if (consumer != null)
      consumer.notifyOf(this);

    stopCondition.setValue(globalBest.getValue());
  }

  private ValuePoint[] getData() {
    ValuePoint[] tmp = new ValuePoint[directions.length];

    for (int i = 0; i < tmp.length; i++)
      tmp[i] = directions[i].getPresent();

    return tmp;
  }

  public void addConsumer(Consumer<? super ValuePointListTelemetry> consumer) {
    this.consumer = consumer;
  }

  public ValuePointListTelemetry getValue() {
    return telemetry;
  }

  // --------------------------------------------------------------------------------------

  public int getDirectionsCount() {
    return directionsCount;
  }

  public void setDirectionsCount(int directionsCount) {
    this.directionsCount = directionsCount;
  }

  public double getSearchRadius() {
    return searchRadius;
  }

  public void setSearchRadius(double searchRadius) {
    this.searchRadius = searchRadius;
  }

  public double getRadiusMultiplier() {
    return radiusMultiplier;
  }

  public void setRadiusMultiplier(double radiusMultiplier) {
    this.radiusMultiplier = radiusMultiplier;
  }

  public int getRadiusGeneration() {
    return radiusGeneration;
  }

  public void setRadiusGeneration(int radiusGeneration) {
    this.radiusGeneration = radiusGeneration;
  }

  public double getStartingPheromone() {
    return startingPheromone;
  }

  public void setStartingPheromone(double startingPheromone) {
    this.startingPheromone = startingPheromone;
  }

  public double getMinimumPheromone() {
    return minimumPheromone;
  }

  public void setMinimumPheromone(double minimumPheromone) {
    this.minimumPheromone = minimumPheromone;
  }

  public double getAddPheromone() {
    return addPheromone;
  }

  public void setAddPheromone(double addPheromone) {
    this.addPheromone = addPheromone;
  }

  public double getEvaporation() {
    return evaporation;
  }

  public void setEvaporation(double evaporation) {
    this.evaporation = evaporation;
  }

  public double getGradientWeight() {
    return gradientWeight;
  }

  public void setGradientWeight(double gradientWeight) {
    this.gradientWeight = gradientWeight;
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