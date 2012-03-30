package cz.cvut.felk.cig.jcool.benchmark.method.ant.api;

import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.Point;

import java.util.Random;
import java.util.Vector;

/**
 * Class representing an API ant.
 */
class Ant {
  private int dimension;

  /**
   * Ant's search radius from the nest.
   */
  private double radius;

  /**
   * Ant's local search radius from the hunting site.
   */
  private double localRadius;

  /**
   * Ant's hunting sites.
   */
  private Vector<HuntingSite> huntingSites;

  /**
   * Flag denoting whether a new hunting site was added.
   */
  private boolean huntingSiteAdded;

  /**
   * Index of the last hunting site created.
   */
  private int lastCreatedHS;

  /**
   * Flag denoting whether the last search was successfull.
   */
  private boolean lastSearchSuccessful;

  /**
   * Index of the last hunting site this ant visited.
   */
  private int lastVisitedHS;

  private int starvation;

  /**
   * Current ant position used when exploring the hunting site. 
   */
  public double position[];

  /**
   * Value of current solution candidate.
   */
  public double fitness;

  private Random generator;

  public Ant(int id, int dimension, int populationSize, int starvation) {
    this.dimension = dimension;
    this.starvation = starvation;

    radius = 0.1 * Math.pow(Math.pow(100.0, 1.0 / populationSize), (id + 1));
    localRadius = radius / 10.0;
    fitness = Double.MAX_VALUE;
    lastSearchSuccessful = false;

    position = new double[dimension];
    huntingSites = new Vector<HuntingSite>();

    generator = new Random();
  }

  public boolean huntingSiteAdded() {
    return huntingSiteAdded;
  }

  /**
   * Checks whether the last search was successful or not and adds new hunting site if needed. 
   * @param huntingSitesCount
   * @param nestPosition
   */
  public void checkHuntingSitesQueue(int huntingSitesCount, double[] nestPosition) {
    // if last search successful do nothing
    if (!lastSearchSuccessful) {
      // add site ?
      if (huntingSites.size() < huntingSitesCount) {
        huntingSites.add(new HuntingSite(radius, dimension, nestPosition));
        huntingSiteAdded = true;
        lastCreatedHS = huntingSites.size() - 1;
      } else
        huntingSiteAdded = false;
    }
  }

  public int lastCreatedHS() {
    return lastCreatedHS;
  }

  public boolean lastSearchSuccessful() {
    return lastSearchSuccessful;
  }

  public int lastVisitedHS() {
    return lastVisitedHS;
  }

  public int getRandomHS() {
    return generator.nextInt(huntingSites.size());
  }

  /**
   * Reinitializes ant information. 
   */
  public void forgetAll() {
    fitness = Double.POSITIVE_INFINITY;
    huntingSites.clear();
    lastSearchSuccessful = false;
  }

  /**
   * Computes random spheric coordinates for a point to be explored in a given hunting site within this ant's local search radius.
   * @param HS  Hunting site to be explored.
   * @param function  Function used for solution candidate evaluation.
   */
  public void explore(int HS, ObjectiveFunction function) {
    lastVisitedHS = HS;

    // generate random position near HS
    if (dimension > 1) {
      // count shift vector using n-dimensional spherical coordinates
      double angles[] = new double[dimension - 1];
      double distance = Math.random() * localRadius;

      // angles
      for (int i = 0; i < dimension - 2; i++)
        angles[i] = Math.random() * Math.PI;
      angles[dimension - 2] = Math.random() * (Math.PI * 2);

      // vector <0, dimensions-2>
      for (int i = 0; i < dimension - 1; i++) {
        position[i] = distance;
        for (int j = 0; j < i; j++)
          position[i] *= Math.sin(angles[j]);
        position[i] *= Math.cos(angles[i]);
      }

      // position [dimension - 1]
      position[dimension - 1] = distance;
      for (int j = 0; j < dimension - 1; j++)
        position[dimension - 1] *= Math.sin(angles[j]);

      // add to hunting site position
      for (int i = 0; i < dimension; i++)
        position[i] += huntingSites.elementAt(HS).getPosition(i);
    } else {
      // for 1D
      position[0] = huntingSites.elementAt(HS).getPosition(0) + (Math.random() * radius * 2) - radius;
    }

    double fit = function.valueAt(Point.at(position));

    if (fit < fitness) {
      lastSearchSuccessful = true;
      huntingSites.elementAt(HS).success(position, fit);
      fitness = fit;
    } else {
      lastSearchSuccessful = false;
      huntingSites.elementAt(HS).noSuccess();
      if ((huntingSites.elementAt(HS)).isStarving(starvation))
        huntingSites.remove(HS);
    }
  }

  public double getFitness() {
    return fitness;
  }

  /**
   * Conducts a tandem run with another ant.
   * @param ant Second ant in the tandem run.
   */
  public void tandemRun(Ant ant) {
    /*
    * find best HS for both ant and replace
    */
    double f1, f2;
    int pos1, pos2;
    pos1 = 0;
    pos2 = 0;
    f1 = Double.POSITIVE_INFINITY;
    f2 = Double.POSITIVE_INFINITY;

    for (int i = 0; i < ant.huntingSites.size(); i++)
      if (ant.huntingSites.elementAt(i).fitness < f2) {
        pos2 = i;
        f2 = ant.huntingSites.elementAt(i).fitness;
      }

    for (int i = 0; i < huntingSites.size(); i++)
      if (huntingSites.elementAt(i).fitness < f1) {
        pos1 = i;
        f1 = huntingSites.elementAt(i).fitness;
      }

    HuntingSite HS1 = huntingSites.elementAt(pos1);
    HuntingSite HS2 = ant.huntingSites.elementAt(pos2);

    System.arraycopy(HS1.position, 0, HS2.position, 0, dimension);
    HS2.fitness = HS1.fitness;
  }
}
