package cz.cvut.felk.cig.jcool.benchmark.method.ant.api;

/**
 * API hunting site class.
 */
class HuntingSite {
  // main
  public double position[];
  private int lastSuccess;
  public double fitness;

  /**
   * Ctor.
   *
   * @param radius       Hunting site local search radius.
   * @param dimension    Number of dimensions.
   * @param nestPosition Current nest position.
   */
  public HuntingSite(double radius, int dimension, double[] nestPosition) {

    /**
     * Position of local best value point.
     */
    position = new double[dimension];

    /**
     * Starvation counter.
     */
    lastSuccess = 1;

    /**
     * Current best fitness of the hunting site found.
     */
    fitness = Double.MAX_VALUE;

    // generate random position near HS
    if (dimension > 1) {
      // count shift vector using n-dimensional spherical coordinates
      double angles[] = new double[dimension - 1];
      double distance = Math.random() * radius;

      // angles
      for (int i = 0; i < dimension - 2; i++)
        angles[i] = Math.random() * Math.PI;
      angles[dimension - 2] = Math.random() * (Math.PI * 2);

      // vector <0, dimension - 2>
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
        position[i] += nestPosition[i];
    } else {
      // for 1D
      position[0] = nestPosition[0] + Math.random() * radius;
    }
  }

  /**
   * Sets the current fitness and resets the starvation counter.
   *
   * @param pos Position of the new local best value point.
   * @param fit Fitness of the new local best value point. 
   */
  public void success(double[] pos, double fit) {
    fitness = fit;
    lastSuccess = 0;
    System.arraycopy(pos, 0, position, 0, position.length);
  }

  /**
   * Increases starvation counter.
   */
  public void noSuccess() {
    lastSuccess++;
  }

  public double getPosition(int i) {
    return position[i];
  }

  /**
   * Returns whether this hunting site lacks food (i.e. is starving)  
   * @param starvation Starvation limit
   * @return hunting site starvation
   */
  public boolean isStarving(int starvation) {
    return (lastSuccess > starvation);
  }
}
