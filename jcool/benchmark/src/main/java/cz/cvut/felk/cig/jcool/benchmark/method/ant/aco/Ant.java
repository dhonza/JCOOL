package cz.cvut.felk.cig.jcool.benchmark.method.ant.aco;

import cz.cvut.felk.cig.jcool.core.Point;
import cz.cvut.felk.cig.jcool.core.ValuePoint;

/**
 * Class representing an ACO* ant.
 */
public class Ant implements Comparable<Ant> {

  /**
   * Value point representing solution candidate (ant).
   */
  private ValuePoint data;

  /**
   * Weight of this ant in the overall sum.
   */
  public double gradientWeight;  // Gauss curves weight

  public Ant(ValuePoint data) {
    this(data, 0.0);
  }

  public Ant(ValuePoint data, double gradientWeight) {
    this.data = data;
    this.gradientWeight = gradientWeight;
  }

  public double getValue() {
    return data.getValue();
  }

  public Point getPoint() {
    return data.getPoint();
  }

  public ValuePoint getData() {
    return data;
  }

  public void setData(ValuePoint data) {
    this.data = data;
  }

  public int compareTo(Ant ant) {
		if (this.data.getValue() < ant.getValue())
      return -1;

		if (this.data.getValue() > ant.getValue())
      return 1;

		return 0;
  }
}
