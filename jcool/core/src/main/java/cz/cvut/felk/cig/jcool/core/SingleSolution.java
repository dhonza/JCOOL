/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.core;

/**
 *
 * @author ytoh
 */
public class SingleSolution implements Solution {
    private Point point;
    private double value;

    public SingleSolution(Point point, double value) {
        this.point = point;
        this.value = value;
    }

    public Point getPoint() {
        return point;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("Solution with value: %e at point: %s", value, point);
    }
}
