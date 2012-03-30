package cz.cvut.felk.cig.jcool.benchmark.method.ant.aaca;

/**
 * Support class representing pheromone levels in the paths between individual
 * bit positions.
 */
public class PheromoneTable {

  /**
   * Number of dimensions. For each dimension we keep unique pheromone infromation.
   */
  private int dimension;

  /**
   * Number of bits used for encoding.
   */
  private int encodingLength;

  /**
   * Pheromone levels on the edge from the Start point to the first bits.
   */
  protected double pheromoneFirst[][]; // v_s

  /**
   * Pheromone levels for all edges between all other bit positions. 
   */
  protected double pheromoneOther[][][][]; // (v_i)^0, (v_i)^1

  /* First Other
  *  |    /|\
  *  v   v v v
  *   0---0---0---0---0
  *  / \ / \ / \ / \ /
  * S   X   X   X   X
  *  \ / \ / \ / \ / \
  *   1---1---1---1---1
  */

  /**
   * Ctor.
   * @param dimension Number of dimensions
   * @param encodingLength  Number of bits used for encoding.
   */
  PheromoneTable(int dimension, int encodingLength) {
    this.dimension = dimension;
    this.encodingLength = encodingLength;
    pheromoneFirst = new double[dimension][2];
    pheromoneOther = new double[dimension][encodingLength - 1][2][2];
  }

  /**
   * Clears the pheromone level information.
   */
  public void clear() {
    for (int d = 0; d < dimension; d++) {
      pheromoneFirst[d][0] = 0.0;
      pheromoneFirst[d][1] = 0.0;
      for (int e = 0; e < encodingLength - 1; e++) {
        pheromoneOther[d][e][0][0] = 0.0;
        pheromoneOther[d][e][0][1] = 0.0;
        pheromoneOther[d][e][1][0] = 0.0;
        pheromoneOther[d][e][1][1] = 0.0;
      }
    }
  }

  /**
   * Initializes pheromone levels to their default values.
   */
  public void init() {
    for (int d = 0; d < dimension; d++) {
      pheromoneFirst[d][0] = 1.0;
      pheromoneFirst[d][1] = 1.0;
      for (int e = 0; e < encodingLength - 1; e++) {
        pheromoneOther[d][e][0][0] = 1.0;
        pheromoneOther[d][e][0][1] = 1.0;
        pheromoneOther[d][e][1][0] = 1.0;
        pheromoneOther[d][e][1][1] = 1.0;
      }
    }
  }

  /**
   * Sets the pheromone levels to either minimum level if it is currently lower or leaves it as it is. 
   */
  public void setMinimum() {
    double minimum = 0.0001;

    for (int d = 0; d < dimension; d++) {
      pheromoneFirst[d][0] = Math.max(pheromoneFirst[d][0], minimum);
      pheromoneFirst[d][1] = Math.max(pheromoneFirst[d][1], minimum);
      for (int e = 0; e < encodingLength - 1; e++) {
        pheromoneOther[d][e][0][0] = Math.max(pheromoneOther[d][e][0][0], minimum);
        pheromoneOther[d][e][0][1] = Math.max(pheromoneOther[d][e][0][1], minimum);
        pheromoneOther[d][e][1][0] = Math.max(pheromoneOther[d][e][1][0], minimum);
        pheromoneOther[d][e][1][1] = Math.max(pheromoneOther[d][e][1][1], minimum);
      }
    }
  }
}
