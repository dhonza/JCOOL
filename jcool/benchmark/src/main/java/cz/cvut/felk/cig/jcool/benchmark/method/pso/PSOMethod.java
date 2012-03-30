package cz.cvut.felk.cig.jcool.benchmark.method.pso;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * PSO optimization method class.
 */
@Component(name = "PSO: Particle Swarm Optimization")
public class PSOMethod implements OptimizationMethod<ValuePointListTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  private ValuePointListTelemetry telemetry;
  private Consumer<? super ValuePointListTelemetry> consumer;

  public enum VelocityUpdateType {
    SIW("Stochastic Inertia Weight"), N("Normal"), FI("Fully Informed"), C("Canonical");
    private String NAME;

    private VelocityUpdateType(String name) {
      NAME = name;
    }

    @Override
    public String toString() {
      return NAME;
    }
  }

  /**
   * Particle velocity update formula type.
   */
  @Property(name = "Update Method")
  private VelocityUpdateType method = VelocityUpdateType.N;

  /**
   * Number of particles.
   */
  @Property(name = "Number Of Particles")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int populationSize = 20;

  @Property(name = "Init Max")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double initMax = 10.0;

  @Property(name = "Init Min")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double initMin = -10.0;

  /**
   * Cognitive acceleration coefficient Phi 1.
   */
  @Property(name = "Cognitive Acceleration Coefficient")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double phi1 = 0.5; //cognitive acceleration coefficient

  /**
   * Social acceleration coefficient Phi 2.
   */
  @Property(name = "Social Acceleration Coefficient")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double phi2 = 0.5; //social acceleration coefficient

  /**
   * Maximal allowed particle speed.
   */
  @Property(name = "Maximal Allowed Particle Speed")
  @Range(from = 0.0, to = Double.MAX_VALUE)
  private double max_v = 0.5;

  /**
   * Neighbourhood distance for FI update formula.
   */
  @Property(name = "Neighbourhood Distance")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int nd = 2;

  /**
   * Constant used to compute Chi for C update formula. 
   */
  @Property(name = "Constant Used To Compute Chi")
  @Range(from = 0.0, to = 1.0)
  private double k = 1.0;             // constant used to compute chi

  /**
   * Auxiliary constant defined by variables phi 1, phi 2 and k
   */
  double chi;                         // auxillary constant, is defined by phi1, phi2 and k variables

  /**
   * PSO particles.
   */
  private ValuePoint[] particles;

  /**
   * Local best values for each particle.
   */
  private ValuePoint[] localBest;

  /**
   * Particles' velocities.
   */
  private Point[] velocity;

  private int dimension;              // number of variables to optimize

  /**
   * Common global best value point. 
   */
  private ValuePoint globalBest;      // global best
  private Random generator;

  public PSOMethod() {
    this.stopCondition = new SimpleStopCondition();
    // first set initial value with dummy value
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointListTelemetry();
  }

  public void init(ObjectiveFunction function) {

    generator = new Random();

    particles = new ValuePoint[populationSize];
    localBest = new ValuePoint[populationSize];
    velocity = new Point[populationSize];

    this.function = function;
    dimension = function.getDimension();

    initMin = Math.max(initMin, function.getMinimum()[0]);
    initMax = Math.min(initMax, function.getMaximum()[0]);

    double[] point;
    double[] velocityPoint;

    globalBest = ValuePoint.at(Point.getDefault(), Double.POSITIVE_INFINITY);
    
    for (int i = 0; i < populationSize; i++) {
      point = new double[dimension];
      velocityPoint = new double[dimension];
      for (int d = 0; d < dimension; d++) {
        point[d] = generator.nextDouble() * (initMax - initMin) + initMin;
        velocityPoint[d] = 2 * generator.nextDouble() * max_v - max_v; //TODO check this
      }
      particles[i] = ValuePoint.at(Point.at(point), this.function);
      localBest[i] = ValuePoint.at(Point.at(point), this.function);
      velocity[i] = Point.at(velocityPoint);

      if (particles[i].getValue() < globalBest.getValue())
        globalBest = particles[i];
    }

    // now we can set initial value correctly
    this.stopCondition.setInitialValue(globalBest.getValue());

    computeChi();
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }


  public void optimize() {
    evaluate();

    ArrayList<ValuePoint> tmp = new ArrayList<ValuePoint>(Arrays.asList(particles));
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
   * Computes Chi damping parameter for C update formula.
   */
  private void computeChi() {
      double phi = phi1 + phi2;
    if (phi > 4) {
      double abs = 2 - phi - Math.sqrt((phi * phi) - (4 * phi));  // auxiliary variables used for the sake of legibility
      chi = 2 * k / Math.abs(abs);
    } else
      chi = k;
  }

  /**
   * Updates global and local best values for all particles if needed.
   */
  protected void evaluate() {
    for (int i = 0; i < populationSize; i++) {
      // evaluate error
      if (particles[i].getValue() < localBest[i].getValue()) {
        // particle's best solution
        localBest[i] = particles[i];
        if (particles[i].getValue() < globalBest.getValue()) {
          // swarm best solution
          globalBest = particles[i];
        }
      }
      updateParticles(i);
    }
  }

  /**
   * Updates given particle's velocity according to selected update formula.
   * @param i Index of updated particle.
   */
  protected void updateParticles(int i) {
    double[] point = particles[i].getPoint().toArray();
    double[] localBestPoint = localBest[i].getPoint().toArray();
    double[] globalBestPoint = globalBest.getPoint().toArray();
    double[] velocityPoint = velocity[i].toArray();

    for (int d = 0; d < dimension; d++) {
      // velocity

      switch (method) {
        case N:
          velocityPoint[d] += phi1 * generator.nextDouble() * (localBestPoint[d] - point[d]) + phi2 * generator.nextDouble() * (globalBestPoint[d] - point[d]);
          break;
        case SIW:
          velocityPoint[d] = ((0.5 + (generator.nextDouble() * 0.5)) * velocityPoint[d]) + (phi1 * generator.nextDouble() * (localBestPoint[d] - point[d])) + (phi2 * generator.nextDouble() * (globalBestPoint[d] - point[d]));
          break;
        case FI:
          velocityPoint[d] = ((0.5 + (generator.nextDouble() * 0.5)) * velocityPoint[d]) + phi1 * generator.nextDouble() * (computeNeighbourhood(i, d));
          break;
        case C:
          velocityPoint[d] = chi * (velocityPoint[d] + (phi1 * generator.nextDouble() * (localBestPoint[d] - point[d])) + (phi2 * generator.nextDouble() * (globalBestPoint[d] - point[d])));
          break;
      }

      // velocity limit
      if (velocityPoint[d] > max_v)
        velocityPoint[d] = max_v;
      else if (velocityPoint[d] < -max_v)
        velocityPoint[d] = -max_v;

      // position
      point[d] += velocityPoint[d];
    }

    velocity[i] = Point.at(velocityPoint);
    particles[i] = ValuePoint.at(Point.at(point), function);
  }

  /**
   * Computes the global attraction value for given data.
   * @param i Index of particle for which the value is computed.
   * @param d Dimension in which the value is computed.
   * @return global attraction value
   */
  protected double computeNeighbourhood(int i, int d) {
    int j;
    double aux = 0.0;

    for (int m = 1; m <= nd; m++ ) {
      j = i - m;                      //calculates position of neighbour
      if (j < 0)
        j = particles.length + j;       //neighbour is on the other side of array, modify j
      else if (j >= particles.length)
        j = j - particles.length;

      double[] localBestPoint = localBest[j].getPoint().toArray();
      double[] point = particles[i].getPoint().toArray();
      aux += this.generator.nextDouble() * (localBestPoint[d] - point[d]);
    }
    aux /= nd;                       //computes average value
    return aux;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  public double getInitMax() {
    return initMax;
  }

  public void setInitMax(double initMax) {
    this.initMax = initMax;
  }

  public double getInitMin() {
    return initMin;
  }

  public void setInitMin(double initMin) {
    this.initMin = initMin;
  }

  public double getPhi1() {
    return phi1;
  }

  public void setPhi1(double phi1) {
    this.phi1 = phi1;
  }

  public double getPhi2() {
    return phi2;
  }

  public void setPhi2(double phi2) {
    this.phi2 = phi2;
  }

  public double getMax_v() {
    return max_v;
  }

  public void setMax_v(double max_v) {
    this.max_v = max_v;
  }

  public VelocityUpdateType getMethod() {
    return method;
  }

  public void setMethod(VelocityUpdateType method) {
    this.method = method;
  }

  public int getNd() {
    return nd;
  }

  public void setNd(int nd) {
    this.nd = nd;
  }

  public double getK() {
    return k;
  }

  public void setK(double k) {
    this.k = k;
  }
}