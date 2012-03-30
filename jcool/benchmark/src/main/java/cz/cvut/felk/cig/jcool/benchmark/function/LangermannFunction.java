/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Number of variables: 1 <= n <= 10.
 * Search domain: 0 <= xi <= 10, i = 1, . . . , n.
 * f(*x) = -1.0809383
 * http://www.it.lut.fi/ip/evo/functions/node15.html
 */
@Component(name = "Langermann's Function")
public class LangermannFunction implements Function {

  @Property(name = "Number of variables n =")
  @Range(from = 1, to = 10)
  private int n = 2;

  @Property(name = "Parameter m =")
  @Range(from = 1, to = 5)
  private int m = 5;

  private double[][] A = {{9.681, 0.667, 4.783, 9.095, 3.517, 9.325, 6.544, 0.211, 5.122, 2.020},
                          {9.400, 2.041, 3.788, 7.931, 2.882, 2.672, 3.568, 1.284, 7.033, 7.374},
                          {8.025, 9.152, 5.114, 7.621, 4.564, 4.711, 2.996, 6.126, 0.734, 4.982},
                          {2.196, 0.415, 5.649, 6.979, 9.510, 9.166, 6.304, 6.054, 9.377, 1.426},
                          {8.074, 8.777, 3.467, 1.863, 6.708, 6.349, 4.534, 0.276, 7.633, 1.567}};

  private double[] c = {0.806, 0.517, 0.100, 0.908, 0.965};

  public double valueAt(Point point) {
    double[] ax = point.toArray();

    double sum1 = 0.0;
    double sum2;

    for (int i = 0; i < m; i++) {
      sum2 = 0.0;

      for (int j = 0; j < n; j++)
        sum2 += (ax[j] - A[i][j]) * (ax[j] - A[i][j]);

      sum1 += c[i] * Math.exp(- 1.0 / Math.PI * sum2) * Math.cos(Math.PI * sum2);
    }

    return -sum1;
  }

  public int getDimension() {
    return n;
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
  }

  public int getM() {
    return m;
  }

  public void setM(int m) {
    this.m = m;
  }
}