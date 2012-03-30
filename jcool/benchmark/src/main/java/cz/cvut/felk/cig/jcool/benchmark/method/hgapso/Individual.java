package cz.cvut.felk.cig.jcool.benchmark.method.hgapso;

import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.Point;
import cz.cvut.felk.cig.jcool.core.ValuePoint;

import java.util.Arrays;

/**
 * Class representing HGAPSO individual.
 */
public class Individual {

  /**
   * Local best solution candidate.
   */
  public ValuePoint best;

  /**
   * Current solution candidate.
   */
  public ValuePoint present;

  /**
   * Current particle velocity.
   */
  private double velocity[];

  private ObjectiveFunction function;

  public Individual(ObjectiveFunction function) {
    velocity = new double[function.getDimension()];
    this.function = function;
  }

  /**
   * Generates randomly positioned particle within the given bounds. Sets zero velocity.
   * @param min Lower limit on particle's position.
   * @param max Upper limit on particle's position.
   */
  public void init(double min, double max) {
    Arrays.fill(velocity, 0);
    best = present = ValuePoint.at(Point.random(function.getDimension(), min, max), function);
  }

  public Individual createCopy() {
    Individual newParticle = new Individual(function);

    newParticle.present = present;
    newParticle.best = best;
    Arrays.fill(newParticle.velocity, 0);

    return newParticle;
  }

  public Individual createCopyBasic() {
    Individual newParticle = new Individual(function);
    newParticle.present = present;
    return newParticle;
  }

  /**
   * Initializes particle data after a crossover operation.
   */
  public void initAfterCrossover() {
    Arrays.fill(velocity, 0);
    best = present;
  }

  /**
   * Updates the global best values if needed.
   * @param globalBest Current global best value.
   * @return either the supplied of its own best value.
   */
  public ValuePoint calculateErrors(ValuePoint globalBest) {
    if (present.getValue() < best.getValue()) {
      best = present;
      if (best.getValue() < globalBest.getValue())
        globalBest = best;
    }
    return globalBest;
  }

  /**
   * Applies the velocity update on the particle's position.
   * @param c1 Cognitive acceleration coefficient.
   * @param c2 Social acceleration coefficient.
   * @param globalBest Global best solution.
   * @param maxVelocity Maximal allowed particle velocity.
   * @param chi Particle velocity damping parameter.
   */
  public void newVelocityAndPosition(double c1, double c2, double[] globalBest, double maxVelocity, double chi) {
    double phi1 = Math.random();
    double phi2 = Math.random();

    double[] bestPosition = best.getPoint().toArray();
    double[] position = present.getPoint().toArray();

    for (int i = 0; i < function.getDimension(); i++) {
      velocity[i] = chi * (velocity[i] + c1 * phi1 * (bestPosition[i] - position[i]) + c2 * phi2 * (globalBest[i] - position[i]));
      trimVelocityComponent(i, maxVelocity);
      position[i] += velocity[i];
    }
    
    present = ValuePoint.at(Point.at(position), function);
  }

  /**
   * Trims particle velocity if exceeding the limit.
   * @param d
   * @param maxVelocity
   */
  void trimVelocityComponent(int d, double maxVelocity) {
    velocity[d] = Math.min(Math.max(velocity[d], -maxVelocity), maxVelocity);
  }

  /**
   * Mutates the particle. The new (mutated) value is drawn uniformly from the interval [-2x, 2x), where x is the current value. 
   * @param dimension
   */
  public void mutate(int dimension) {
    double[] position = present.getPoint().toArray();
    position[dimension] *= Math.random() * 4 - 2;
    present = ValuePoint.at(Point.at(position), function);
  }
}
