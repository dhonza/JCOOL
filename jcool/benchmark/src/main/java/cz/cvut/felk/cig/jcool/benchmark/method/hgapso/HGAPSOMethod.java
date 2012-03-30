package cz.cvut.felk.cig.jcool.benchmark.method.hgapso;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;
import java.util.Random;

/**
 * HGAPSO optimization method class.
 */
@Component(name = "HGAPSO: Hybrid of the GA and the PSO")
public class HGAPSOMethod implements OptimizationMethod<ValuePointListTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  private ValuePointListTelemetry telemetry;
  private Consumer<? super ValuePointListTelemetry> consumer;

  /**
   * Number of particles/individuals.
   */
  @Property(name = "Number Of Particles")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int nParticles = 80;

  /**
   * Maximal PSO speed of a particle.
   */
  @Property(name = "Max Velocity Component")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double maxVelocityComponent = 2;

  /**
   * Elite ratio of the population.
   */
  @Property(name = "Elite Ratio")
  @Range(from = 0.0, to = 1.0)
  private double eliteRatio = 0.5;

  /**
   * Mutation probability.
   */
  @Property(name = "Mutation Probability")
  @Range(from = 0.0, to = 1.0)
  private double mutationProbability = 0.1;

  /**
   * Cognitive acceleration coefficient Phi1.
   */
  @Property(name = "Phi1")
  @Range(from = 0.0, to = 1.0)
  private double c1 = 1.0;

  /**
   * Social acceleration coefficient Phi2.
   */
  @Property(name = "Phi2")
  @Range(from = 0.0, to = 1.0)
  private double c2 = 1.0;

  /**
   * PSO particle speed damping parameter.
   */
  @Property(name = "Chi")
  @Range(from = 0.0, to = 1.0)
  private double chi = 0.8;

  @Property(name = "Parameter Minimum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;          // parameter minimum

  @Property(name = "Parameter Maximum - Limits the Search Space")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;           // parameter maximum

  /**
   * Particles/individuals.
   */
  private Individual[] particle;

  /**
   * Particle positions and values.
   */
  private ValuePoint[] particleData;

  private int dimension;              // number of variables to optimize

  private ValuePoint globalBest;      // global best

  private Random generator;

  public HGAPSOMethod() {
    this.stopCondition = new SimpleStopCondition();
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointListTelemetry();
  }

  public void init(ObjectiveFunction function) {

    generator = new Random();

    particle = new Individual[nParticles];
    particleData = new ValuePoint[nParticles];

    this.function = function;
    dimension = function.getDimension();

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    globalBest = ValuePoint.at(Point.getDefault(), Double.POSITIVE_INFINITY);
    stopCondition.setInitialValue(Double.POSITIVE_INFINITY);

    createPopulation(); // create a new population of particles
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() {

    calculateErrors(nParticles); // calculate error for each particle's position
    int nElites = extractElites(eliteRatio);
    // sort elites according to their error, discard the worst ones
    enhanceElites(nElites, 5); // enhance the elites using PSO
    completePopulation(nElites);

    for (int i = 0; i < nParticles; i++)
      particleData[i] = particle[i].present;

    telemetry = new ValuePointListTelemetry(Arrays.asList(particleData));
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
   * Creates initial population.
   */
  void createPopulation() {
    for (int i = 0; i < nParticles; i++) {
      particle[i] = new Individual(function);
      particle[i].init(min, max);
      if (particle[i].present.getValue() < globalBest.getValue())
        globalBest = particle[i].present;
    }
  }

  /**
   * Calculates particle's value.
   * @param number Index of the particle.
   */
  void calculateErrors(int number) {
    for (int i = 0; i < number; i++)
      globalBest = particle[i].calculateErrors(globalBest);
  }

  /**
   * Computes number of particles used as elites.
   * @param eliteRatio Elite ratio
   * @return numbber of particles marked as elite.
   */
  int extractElites(double eliteRatio) {
    int nElites = (int) (eliteRatio * nParticles);
    sortParticles(nElites);
    for (int i = nElites; i < nParticles; i++)
      particle[i] = null;
    return nElites;
  }

  /**
   * Sorts first nBest particles. Uses bubble-sort according to their actual value.
   * @param nBest
   */
  void sortParticles(int nBest) {
    Individual aux;
    // Bubble-sort particles (only first nBest ones)
    for (int i = 0; i < nBest; i++) {
      for (int j = i + 1; j < nParticles; j++)
        if (particle[i].present.getValue() > particle[j].present.getValue()) {
          // NB: particles are sorted according to their ACTUAL error
          aux = particle[i];
          particle[i] = particle[j];
          particle[j] = aux;
        }
    }
  }

  /**
   * Applies PSO method on the elites for given number of iterations.
   * @param nElites Number of elite particles.
   * @param maxIterations Number of PSO iterations.
   */
  void enhanceElites(int nElites, int maxIterations) {
    for (int i = 0; i < maxIterations; i++) {
      calculateErrors(nElites);
      for (int j = 0; j < nElites; j++)
        particle[j].newVelocityAndPosition(c1, c2, globalBest.getPoint().toArray(), maxVelocityComponent, chi);
    }
  }

  /**
   * Completes the population by GA algorithm.
   * @param nElites Number of elites. This is how many particles already are in the new population.
   */
  void completePopulation(int nElites) {
    for (int i = nElites; i < nParticles; i += 2) {
      Individual[] twoNewParticles;
      twoNewParticles = selectTwoParticles(nElites);
      twoNewParticles = crossTwoParticles(twoNewParticles);
      twoNewParticles = mutateTwoParticles(twoNewParticles);
      twoNewParticles[0].initAfterCrossover();
      twoNewParticles[1].initAfterCrossover();
      if (nParticles - i > 1) {
        particle[i] = twoNewParticles[0];
        particle[i + 1] = twoNewParticles[1];
      } else
        particle[i] = twoNewParticles[0];
    }
  }

  /**
   * Selects two particles randomly from the non elite population.
   * @param nElites Number of elite particles.
   * @return Two selected individuals.
   */
  Individual[] selectTwoParticles(int nElites) {
    return selectParticles(nElites, 2);
  }

  /**
   * Applies crossover operation on given two particles.
   * @param particles Particles to be crossed over.
   * @return Crossed over (new) particles.
   */
  Individual[] crossTwoParticles(Individual[] particles) {
    double aux;
    int startPos, endPos; // positions at which to start and end the crossover
    startPos = generator.nextInt(dimension);
    do
      endPos = generator.nextInt(dimension);
    while (endPos == startPos);

    if (startPos > endPos) {
      int auxPos = endPos;
      endPos = startPos;
      startPos = auxPos;
    }

    double[] pos1 = particles[0].present.getPoint().toArray();
    double[] pos2 = particles[1].present.getPoint().toArray();

    for (int i = startPos; i < endPos; i++) {
      aux = pos1[i];
      pos1[i] = pos2[i];
      pos2[i] = aux;
    }

    particles[0].present = ValuePoint.at(Point.at(pos1), function);
    particles[1].present = ValuePoint.at(Point.at(pos2), function);

    return particles;
  }

  /**
   * Mutates given two particles.
   * @param particles Particles to be mutated.
   * @return Mutated particles.
   */
  Individual[] mutateTwoParticles(Individual[] particles) {
    particles[0] = mutateParticle(particles[0]);
    particles[1] = mutateParticle(particles[1]);
    return particles;
  }

  /**
   * Selectes particles randomly from the non-elite population.
   * @param nElites Number of elite particles.
   * @param number Number of particles to be selected.
   * @return selected particles.
   */
  Individual[] selectParticles(int nElites, int number) {
    Individual p1, p2;
    Individual[] newParticles = new Individual[number];
    for (int i = 0; i < number; i++) {
      p1 = particle[generator.nextInt(nElites)];
      p2 = particle[generator.nextInt(nElites)];
      if (p1.present.getValue() <= p2.present.getValue())
        newParticles[i] = p1.createCopyBasic();
      else newParticles[i] = p2.createCopyBasic();
    }
    return newParticles;
  }

  /**
   * Mutates given particle.
   * @param p particle to be mutated.
   * @return mutated particle.
   */
  Individual mutateParticle(Individual p) {
    if (Math.random() < mutationProbability)
      p.mutate(generator.nextInt(dimension));
    return p;
  }

  public int getnParticles() {
    return nParticles;
  }

  public void setnParticles(int nParticles) {
    this.nParticles = nParticles;
  }

  public double getMaxVelocityComponent() {
    return maxVelocityComponent;
  }

  public void setMaxVelocityComponent(double maxVelocityComponent) {
    this.maxVelocityComponent = maxVelocityComponent;
  }

  public double getEliteRatio() {
    return eliteRatio;
  }

  public void setEliteRatio(double eliteRatio) {
    this.eliteRatio = eliteRatio;
  }

  public double getC1() {
    return c1;
  }

  public void setC1(double c1) {
    this.c1 = c1;
  }

  public double getC2() {
    return c2;
  }

  public void setC2(double c2) {
    this.c2 = c2;
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

  public double getMutationProbability() {
    return mutationProbability;
  }

  public void setMutationProbability(double mutationProbability) {
    this.mutationProbability = mutationProbability;
  }

  public double getChi() {
    return chi;
  }

  public void setChi(double chi) {
    this.chi = chi;
  }
}