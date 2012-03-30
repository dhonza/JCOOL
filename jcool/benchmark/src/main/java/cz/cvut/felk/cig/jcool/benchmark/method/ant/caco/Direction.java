package cz.cvut.felk.cig.jcool.benchmark.method.ant.caco;

import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.Point;
import cz.cvut.felk.cig.jcool.core.ValuePoint;

/**
 * Class representing an CACO ant.
 */
class Direction {
  /**
   * No improvement recorded last iteration.
   */
  private static final int NO_IMPROVEMENT     = 0;

  /**
   * Local improvement recorded last iteration.
   */
  private static final int LOCAL_IMPROVEMENT  = 1;

  /**
   * Global improvement recorded last iteration.
   */
  private static final int GLOBAL_IMPROVEMENT = 2;

  /**
   * Global best solution candidate.
   */
  private ValuePoint globalBest;

  /**
   * Local best solution candidate.
   */
  private ValuePoint localBest;

  /**
   * Current solution candidate.
   */
  private ValuePoint present;

  private double minimumPheromone;
  private double addPheromone;

  /**
   * Last iteration update status.
   */
  private int lastErrorCheck;    // result of the last error check
  // (NO_IMPROVEMENT, LOCAL_IMPROVEMENT, GLOBAL_IMPROVEMENT)

  /**
   * Current pheormone level.
   */
  private double pheromone;    // present pheromone for vector
  private double gradientWeight;  // weight of gradient heuristic impact (0.0 = none)

  /**
   * Ctor.
   * @param nest  Nest position.
   * @param startingPheromone Initial pheromone level.
   * @param minimumPheromone Minimum pheromone level.
   * @param addPheromone Pheromone amount to be added.
   * @param gradientWeight
   * @param function
   * @param min
   * @param max
   */
  public Direction(double[] nest, double startingPheromone, double minimumPheromone, double addPheromone, double gradientWeight, ObjectiveFunction function, double min, double max) {

    this.minimumPheromone = minimumPheromone;
    this.addPheromone = addPheromone;

    double[] point = Point.random(function.getDimension(), min, max).toArray();
    for (int i = 0; i < point.length; i++)
      point[i] += nest[i];

    present = localBest = ValuePoint.at(Point.at(point), function);
    globalBest = ValuePoint.at(Point.getDefault(), Double.POSITIVE_INFINITY);

    changePheromone(startingPheromone);

    lastErrorCheck = NO_IMPROVEMENT;

    this.gradientWeight = gradientWeight;
  }

  /**
   * Changes the current pheromone level ensuring minimal value.
   * @param amount Amount to be added or subtracted.
   */
  void changePheromone(double amount) {
    pheromone += amount;
    if (pheromone < minimumPheromone)
      pheromone = minimumPheromone;     
  }

  /**
   * Evaporates the pheromone.
   * @param evaporation Evaporation factor.
   */
  public void evaporatePheromone(double evaporation) {
    changePheromone(-evaporation * pheromone);
  }

  /**
   * Conducts local search in the given radius using random spherical coordinates.
   * @param radius  Local search radius.
   * @param function
   * @param min
   * @param max
   */
  public void explore(double radius, ObjectiveFunction function, double min, double max) {
    int dimension = function.getDimension();

    Point oldVector = present.getPoint();

    if (pheromone == minimumPheromone)
      present = ValuePoint.at(Point.random(dimension, min, max), function);
    else if (dimension > 1) {

      // count shift vector using n-dimensional spherical coordinates
      double vector[] = new double[dimension];
      double angles[] = new double[dimension - 1];
      double distance = Math.random() * radius;

      // angles
      for (int i = 0; i < dimension - 2; i++) angles[i] = Math.random() * Math.PI;
      angles[dimension - 2] = Math.random() * (Math.PI * 2);

      // vector <0, dimension-2>
      for (int i = 0; i < dimension - 1; i++) {
        vector[i] = distance;
        for (int j = 0; j < i; j++)
          vector[i] *= Math.sin(angles[j]);
        vector[i] *= Math.cos(angles[i]);
      }

      // vector [dimension-1]
      vector[dimension - 1] = distance;
      for (int j = 0; j < dimension - 1; j++)
        vector[dimension - 1] *= Math.sin(angles[j]);

      double[] tmp = present.getPoint().toArray();

      // add to pVector
      for (int i = 0; i < dimension; i++)
        tmp[i] += vector[i];

      present = ValuePoint.at(Point.at(tmp), function);

    } else {
      // for 1D
      present = ValuePoint.at(Point.at(present.getPoint().toArray()[0] + (Math.random() * radius * 2) - radius), function);
    }

    if (gradientWeight > 0.0)
      addGradient(function);

    countErrors();

    if (lastErrorCheck == NO_IMPROVEMENT)
      present = ValuePoint.at(oldVector, function);
  }

  private void addGradient(ObjectiveFunction function) {
    double[] gradient = function.gradientAt(present.getPoint()).toArray();

    double[] tmp = present.getPoint().toArray();

    for (int d = 0; d < tmp.length; d++)
      tmp[d] -= gradientWeight * gradient[d];

    present = ValuePoint.at(Point.at(tmp), function);
  }

  /**
   * Counts error for current ant's solution candidate.
   */
  void countErrors() {
    if (present.getValue() < localBest.getValue()) { // local
      localBest = present;
      if (present.getValue() < globalBest.getValue()) { // global
        globalBest = present;
        lastErrorCheck = GLOBAL_IMPROVEMENT;
        changePheromone(addPheromone);
      } else {
        lastErrorCheck = LOCAL_IMPROVEMENT;
        changePheromone(addPheromone);
      }
    } else {
      lastErrorCheck = NO_IMPROVEMENT;
    }
  }

  public double getPheromone() {
    return pheromone;
  }

  public ValuePoint getGlobalBest() {
    return globalBest;
  }

  public void setGlobalBest(ValuePoint globalBest) {
    this.globalBest = globalBest;
  }

  public ValuePoint getPresent() {
    return present;
  }
}
