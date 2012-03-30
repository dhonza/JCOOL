package cz.cvut.felk.cig.jcool.benchmark.util;

import cz.cvut.felk.cig.jcool.core.Gradient;
import cz.cvut.felk.cig.jcool.core.Hessian;
import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.Point;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.2.2011
 * Time: 19:39
 * Empty implementation of ObjectiveFunction. Methods necessary for testing will be override.
 */
public class EmptyObjectiveFunction implements ObjectiveFunction {

    public boolean hasAnalyticalGradient() {
        throw new NotImplementedException();
    }

    public boolean hasAnalyticalHessian() {
        throw new NotImplementedException();
    }

    public boolean isDynamic() {
        throw new NotImplementedException();
    }

    public boolean inBounds(Point position) {
        throw new NotImplementedException();
    }

    public double valueAt(Point point) {
        throw new NotImplementedException();
    }

    public int getDimension() {
        throw new NotImplementedException();
    }

    public double[] getMinimum() {
        throw new NotImplementedException();
    }

    public double[] getMaximum() {
        throw new NotImplementedException();
    }

    public void resetGenerationCount() {
        throw new NotImplementedException();
    }

    public void nextGeneration() {
        throw new NotImplementedException();
    }

    public void setGeneration(int currentGeneration) {
        throw new NotImplementedException();
    }

    public Gradient gradientAt(Point point) {
        throw new NotImplementedException();
    }

    public Hessian hessianAt(Point point) {
        throw new NotImplementedException();
    }
}
