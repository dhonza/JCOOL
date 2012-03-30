/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.benchmark.method.genetic.sade;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.Consumer;
import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.core.OptimizationMethod;
import cz.cvut.felk.cig.jcool.core.Point;
import cz.cvut.felk.cig.jcool.core.SingleSolution;
import cz.cvut.felk.cig.jcool.core.Solution;
import cz.cvut.felk.cig.jcool.core.StopCondition;
import cz.cvut.felk.cig.jcool.core.ValuePoint;
import cz.cvut.felk.cig.jcool.core.ValuePointListTelemetry;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import cz.cvut.felk.cig.jcool.utils.RND;

import java.util.Arrays;

import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * @author drchaj1
 *         TODO gradientLineSearchMutate z DGartou
 *         This class implements the SADE genetic algorithm. The SADE operates
 *         on real domains (the chromosomes are represented by real number vectors).
 *         The scheme of SADE:
 *         Tournament selection is used. The best half of population is preserved to the next generation.
 *         TODO odkaz, prepsat dokumentaci
 *         TODO check low and high for parameter ranges
 */
@Component(name = "SADE genetic algorithm")
public class SADEMethod implements OptimizationMethod<ValuePointListTelemetry> {

  private ObjectiveFunction function;
  private SimpleStopCondition stopCondition;

  private ValuePointListTelemetry telemetry;
  private Consumer<? super ValuePointListTelemetry> consumer;

  @Property(name = "Pool rate")
  @Range(from = 1, to = Integer.MAX_VALUE)
  private int poolRate = 100; //Determines the size of the pool.

  @Property(name = "Radioactivity")
  @Range(from = 0.0, to = 1.0)
  private double radioactivity = 0.0; //The probability of the MUTATION.

  @Property(name = "Local radioactivity")
  @Range(from = 0.0, to = 1.0)
  private double localRadioactivity = 0.2; //The probability of the LOCAL_MUTATION.

  @Property(name = "Mutation rate")
  @Range(from = 0.0, to = 1.0)
  private double mutationRate = 0.5; //Determines the amount of MUTATION.

  @Property(name = "Mutagen rate")
  @Range(from = 0.0, to = 1000.0)
  private double mutagenRate = 1000.0; //Determines the amount of LOCAL_MUTATION.

  @Property(name = "DE rate")
  @Range(from = 0.0, to = 1.0)
  private double deRate = 0.1; //Amount of the crossDE.

  @Property(name = "Min")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double min = -10.0;

  @Property(name = "Max")
  @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
  private double max = 10.0;

  //TODO vyresit jinak s bounds
  private double maxInitMutagen = 0.0;

  private int actualSize;
  private int poolSize;
  private int selectedSize;

  private int n; //dimensions
  private double[] mutagen;
  private ValuePoint[] genotypes;
  private ValuePoint bestSoFar;
  private ValuePoint bestThisGeneration;

  public SADEMethod() {
    this.stopCondition = new SimpleStopCondition();
    this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
    this.telemetry = new ValuePointListTelemetry();
  }

  public int getPoolRate() {
    return poolRate;
  }

  public void setPoolRate(int poolRate) {
    this.poolRate = poolRate;
  }

  public double getRadioactivity() {
    return radioactivity;
  }

  public void setRadioactivity(double radioactivity) {
    this.radioactivity = radioactivity;
  }

  public double getLocalRadioactivity() {
    return localRadioactivity;
  }

  public void setLocalRadioactivity(double localRadioactivity) {
    this.localRadioactivity = localRadioactivity;
  }

  public double getMutationRate() {
    return mutationRate;
  }

  public void setMutationRate(double mutationRate) {
    this.mutationRate = mutationRate;
  }

  public double getMutagenRate() {
    return mutagenRate;
  }

  public void setMutagenRate(double mutagenRate) {
    this.mutagenRate = mutagenRate;
  }

  public double getDeRate() {
    return deRate;
  }

  public void setDeRate(double deRate) {
    this.deRate = deRate;
  }

  public void init(ObjectiveFunction function) {
    this.function = function;

    this.n = function.getDimension();

    min = Math.max(min, function.getMinimum()[0]);
    max = Math.min(max, function.getMaximum()[0]);

    configuration();
    firstGeneration();
    stopCondition.setInitialValue(Double.POSITIVE_INFINITY);
  }

  public StopCondition[] getStopConditions() {
    return new StopCondition[]{stopCondition};
  }

  public void optimize() throws OptimizationException {
    mutate();
    localMutate();
    crossDE();
    evaluatePopulation(false);
    select();

    telemetry = new ValuePointListTelemetry(Arrays.asList(genotypes));
    if (consumer != null)
      consumer.notifyOf(this);

    //test for convergence
    stopCondition.setValue(bestThisGeneration.getValue());
  }

  public Solution finish() {
    return new SingleSolution(bestSoFar.getPoint(), bestSoFar.getValue());
  }

  //-------------------------------------------------------------
  // creates a new random point

  private void configuration() {
    poolSize = 2 * poolRate * n;
    selectedSize = poolRate * n;
    mutagen = new double[n];
    double size = max - min;
    for (int i = 0; i < n; i++) {
      mutagen[i] = size / mutagenRate;
      if (maxInitMutagen > mutagen[i]) {
        mutagen[i] = maxInitMutagen;
      }
    }
  }

  private void firstGeneration() {
    genotypes = new ValuePoint[poolSize];
    for (int i = 0; i < poolSize; i++) {
      genotypes[i] = ValuePoint.at(Point.random(n, min, max), function);
    }
    actualSize = poolSize;
    evaluatePopulation(true);
    select();
  }

  private void evaluatePopulation(boolean firstGeneration) {
    int start;
    if (firstGeneration) {
      start = 0;
      bestThisGeneration = genotypes[0];
      bestSoFar = bestThisGeneration;
    } else {
      start = 1;
    }
    for (int i = start * selectedSize + 1; i < actualSize; i++) {
      if (genotypes[i].getValue() < bestThisGeneration.getValue()) {
        bestThisGeneration = genotypes[i];
      }
    }
    if (bestThisGeneration.getValue() < bestSoFar.getValue()) {
      bestSoFar = bestThisGeneration;
    }
  }

  private void select() {
    //tournament
    while (actualSize > selectedSize) {
      //choose two random organisms
      int i1 = RND.getInt(0, actualSize - 1);
      int i2 = RND.getInt(1, actualSize - 1);
      //not the same twice
      if (i1 == i2) {
        i2--;
      }
      //higher weakness dies
      int dead;
      if (genotypes[i1].getValue() < genotypes[i2].getValue()) {
        dead = i2;
      } else {
        dead = i1;
      }
      //the dead one will not undergo selection again
      int last = actualSize - 1;

      ValuePoint swap = genotypes[last];
      genotypes[last] = genotypes[dead];
      genotypes[dead] = swap;

      actualSize--;
    }
  }

  private void mutate() {
    for (int i = 0; i < selectedSize; i++) {
      if (actualSize == poolSize) {
        break;
      }
      double p = RND.getDouble(0, 1);
      if (p <= radioactivity) {
        int index = RND.getInt(0, selectedSize - 1);
        //mutationRate = RND.getDouble(0, 1); // TODO should not we use the input as a value?!?
        double[] x = Point.random(n, min, max).toArray();
        double[] g = genotypes[index].getPoint().toArray();
        double[] m = new double[n];
        for (int j = 0; j < n; j++) {
          m[j] = g[j] + mutationRate * (x[j] - g[j]);
        }
        Point at = Point.at(m);
        genotypes[actualSize] = ValuePoint.at(at, function.valueAt(at));
        actualSize++;
      }
    }
  }

  private void localMutate() {
    for (int i = 0; i < selectedSize; i++) {
      if (actualSize == poolSize) {
        break;
      }
      double p = RND.getDouble(0, 1);
      if (p <= localRadioactivity) {
        int index = RND.getInt(0, selectedSize - 1);
        double[] g = genotypes[index].getPoint().toArray();
        double[] m = new double[n];
        for (int j = 0; j < n; j++) {
          m[j] = g[j] + RND.getDouble(-mutagen[j], mutagen[j]);
        }
        Point at = Point.at(m);
        genotypes[actualSize] = ValuePoint.at(at, function.valueAt(at));
        actualSize++;
      }
    }
  }

  private void crossDE() {
    while (actualSize < poolSize) {
      int i1 = RND.getInt(0, selectedSize - 1);
      int i2 = RND.getInt(1, selectedSize - 1);
      if (i1 == i2) {
        i2--;
      }
      int i3 = RND.getInt(0, selectedSize - 1);
      double[] g1 = genotypes[i1].getPoint().toArray();
      double[] g2 = genotypes[i2].getPoint().toArray();
      double[] g3 = genotypes[i3].getPoint().toArray();
      double[] c = new double[n];
      for (int j = 0; j < n; j++) {
        c[j] = g3[j] + deRate * (g2[j] - g1[j]);
      }
      Point at = Point.at(c);
      genotypes[actualSize] = ValuePoint.at(at, function.valueAt(at));
      actualSize++;
    }
  }

  public void addConsumer(Consumer<? super ValuePointListTelemetry> consumer) {
    this.consumer = consumer;
  }

  public ValuePointListTelemetry getValue() {
    return telemetry;
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