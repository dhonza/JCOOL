package cz.cvut.felk.cig.jcool.benchmark.method.ant.aco;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;
import java.util.Random;

/**
 * ACO* optimization method class.
 */
@Component(name = "ACO*: Extended Ant Colony Optimization")
public class ACOMethod implements OptimizationMethod<ValuePointListTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  private ValuePointListTelemetry telemetry;
  private Consumer<? super ValuePointListTelemetry> consumer;

  /**
   * Number of ants.
   */
  @Property(name = "Population Size", description = "Number of ants in the population")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int populationSize = 60;     // number of ants

  /**
   * Deviation parameter Omega.
   * Lower value - better solutions are strongly preferred.
   */
  @Property(name = "Deviation Parameter: Omega", description = "Lower value - better solutions are strongly preferred")
  @Range(from = 0.0, to = 1.0)
  private double omega = 0.8;                  // deviation parameter. lower - better solutions are strongly preferred

   /**
   * Deviation parameter Sigma.
   * Lower value - faster convergence.
   */
  @Property(name = "Convergence Parameter: Sigma", description = "Lower value - faster convergence")
  @Range(from = 0.0, to = 1.0)
  private double sigma = 0.4;                  // from (0, 1): speed of convergence parameter. lower - faster convergence

  /**
   * Number of ants to replace in the population each iteration.
   */
  @Property(name = "Number of Replaced Ants", description = "Number of ants to replace in the population")
  @Range(from = 0, to = Integer.MAX_VALUE)
  private int replace = 60;            // number of ants to replace in one iteration

  /**
   * Whether standard deviation is used instead of average value.
   */
  @Property(name = "Use Standard Deviation (otherwise use Average)")
  private boolean standardDeviation = true;  // use standard deviation? otherwise average is used

  /**
   * Limits the neighbourhood for the deviation computation by diversityLimit value.
   */
  @Property(name = "Force Diversity", description = "Limit the neighbourhood for the deviation computation by diversityLimit")
  private boolean forceDiversity = false;     // limit neighbourhood for the deviation computation by diversityLimit

  /**
   * Neighborhood size used for Force diversity.
   */
  @Property(name = "Neighborhood Size for Force Diversity")
  @Range(from = 0.0, to = 1.0)
  private double diversityLimit = 0.1;     // size of neighborhood for forceDiversity

  @Property(name = "Parameter Minimum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;          // parameter minimum

  @Property(name = "Parameter Maximum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;           // parameter maximum

  @Property(name = "Gradient Heuristic Weight")
  @Range(from = 0.0, to = 1.0)
  private double gradientWeight = 0.0;     // gradient heuristic impact

  private int dimension;         // number of variables to optimize

  /**
   * Ants in the colony (population).
   */
  private Ant[] ants;                 // ants in colony

  private Random generator;

  /**
   * Globally best value point.
   */
  private ValuePoint globalBest;  // globaly best variables vector

  /**
   * Locally best value point.
   */
  private ValuePoint localBest;  // local best position

  public ACOMethod() {
    this.stopCondition = new SimpleStopCondition();
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointListTelemetry();
  }

  public void init(ObjectiveFunction function) {

    this.function = function;
    dimension = function.getDimension();

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    ants = new Ant[populationSize + replace];

    for (int i = 0; i < populationSize + replace; i++)
      ants[i] = new Ant(ValuePoint.at(Point.random(dimension, min, max), function));

    generator = new Random();

    globalBest = localBest = ValuePoint.at(Point.getDefault(), Double.POSITIVE_INFINITY);
    stopCondition.setInitialValue(Double.POSITIVE_INFINITY);

    sortAnts();
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {
    createNewAnts();
    sortAnts();

    telemetry = new ValuePointListTelemetry(Arrays.asList(getData()));
    if (consumer != null)
      consumer.notifyOf(this);

    stopCondition.setValue(globalBest.getValue());
  }

  public void addConsumer(Consumer<? super ValuePointListTelemetry> consumer) {
    this.consumer = consumer;
  }

  public ValuePoint[] getData() {
    ValuePoint[] tmp = new ValuePoint[populationSize + replace];

    for (int i = 0; i < populationSize + replace; i++)
      tmp[i] = ants[i].getData();

    return tmp;
  }

  public ValuePointListTelemetry getValue() {
    return telemetry;
  }

  /**
   * Updates global and local best values using the ant's value.
   * @param ant Ant to be used for global/local information update.
   */
  void countErrors(int ant) {

    // local best solution
    if (ants[ant].getValue() < localBest.getValue()) {
      localBest = ants[ant].getData();

      // global best solution
      if (ants[ant].getValue() < globalBest.getValue())
        globalBest = ants[ant].getData();
    }
  }

  /**
   * Sorts the ants according to their cost.
   */
  private void sortAnts() {

    for (int i = 0; i < populationSize + replace; i++)
      countErrors(i);

    Arrays.sort(ants);
  }

  /**
   * Counts sum of weights for all ants.
   */
  private double getWeightsSum() {
    double sumWeights = 0.0;

    for (int i = 0; i < populationSize; i++) {
      ants[i].gradientWeight = ((1 / (omega * populationSize * Math.sqrt(2 * Math.PI))) * Math.exp(-(i * i) / (2 * omega * omega * populationSize * populationSize)));
      sumWeights += ants[i].gradientWeight;
    }

    return sumWeights;
  }

  private void addGradient(double[] solution) {
    double[] gradient = function.gradientAt(Point.at(solution)).toArray();

    for (int d = 0; d < dimension; d++)
      solution[d] -= gradientWeight * gradient[d];
  }

  /**
   * Chooses ant from the population using the sum of weights as a probabilistic measure.
   * @param sumWeights Sum of the ants' wights
   * @return index of chosen ant.
   */
  private int chooseAnt(double sumWeights) {
    double ran = generator.nextDouble() * sumWeights;
    double sum = 0.0;
    int chosen = 0;

    while (sum < ran)
      sum += ants[chosen++].gradientWeight;

    return --chosen;
  }

  /**
   * Computes the standard deviation of the population.
   * @param mean mean valu of the population
   * @param i ith ant to be used for computation of the deviation. 
   * @return standard deviation or average of the populaiton.
   */
  private double getDeviation(double mean, int i) {
    double deviation = 0.0;
    int nearestCount = 0;

    for (int n = 0; n < populationSize; n++) {
      double tmp = (ants[n].getPoint().toArray()[i] - mean);
      if (!standardDeviation) {
        if ((!forceDiversity) || (tmp < diversityLimit)) {
          deviation += Math.abs(tmp);
          nearestCount++;
        }
      } else {
        if ((!forceDiversity) || (tmp < diversityLimit)) {
          deviation += (tmp * tmp);
          nearestCount++;
        }
      }
    }
    if (!standardDeviation)
      deviation /= nearestCount;
    else {
      deviation /= (nearestCount - 1);
      deviation = Math.sqrt(deviation);
    }

    deviation *= sigma;

    return deviation;
  }

  /**
   * Samples the weighted probabilistic funciton and creates new population.
   */
  private void createNewAnts() {
    double sumWeights = getWeightsSum();

    for (int num = populationSize; num < populationSize + replace; num++) {
      int chosen = chooseAnt(sumWeights);

      double mean;
      double deviation;
      double[] newAntPos = new double[dimension];

      for (int i = 0; i < dimension; i++) {
          mean = ants[chosen].getPoint().toArray()[i];
          deviation = getDeviation(mean, i);
          // sample Xi -> new mi & sigma
          double x = (generator.nextGaussian() * deviation) + mean;
          newAntPos[i] = x;
      }

      // pheromone & gradient heuristic
      if (gradientWeight != 0.0)
        addGradient(newAntPos);

      ants[num].setData(ValuePoint.at(Point.at(newAntPos), function));
    }
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  public double getOmega() {
    return omega;
  }

  public void setOmega(double omega) {
    this.omega = omega;
  }

  public double getSigma() {
    return sigma;
  }

  public void setSigma(double sigma) {
    this.sigma = sigma;
  }

  public int getReplace() {
    return replace;
  }

  public void setReplace(int replace) {
    this.replace = replace;
  }

  public boolean isStandardDeviation() {
    return standardDeviation;
  }

  public void setStandardDeviation(boolean standardDeviation) {
    this.standardDeviation = standardDeviation;
  }

  public boolean isForceDiversity() {
    return forceDiversity;
  }

  public void setForceDiversity(boolean forceDiversity) {
    this.forceDiversity = forceDiversity;
  }

  public double getDiversityLimit() {
    return diversityLimit;
  }

  public void setDiversityLimit(double diversityLimit) {
    this.diversityLimit = diversityLimit;
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
