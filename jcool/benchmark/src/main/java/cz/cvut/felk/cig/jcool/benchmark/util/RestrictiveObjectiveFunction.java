package cz.cvut.felk.cig.jcool.benchmark.util;

import cz.cvut.felk.cig.jcool.core.*;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 17.4.2011
 * Time: 14:01
 * Decorator that adds additional function search space restrictions for any adapted ObjectiveFunction.
 * Restrictions do not weaken previous, more strict, bounds - result contains the most restrictive bounds from old and new bounds.
 * Decoration is made at construction time.
 */
public class RestrictiveObjectiveFunction implements ObjectiveFunction{

    double minima[];
    double maxima[];
    /**
     * Wrapped/decorated ObjectiveFunction object
     */
    ObjectiveFunction adaptee;

    public RestrictiveObjectiveFunction(ObjectiveFunction function, double minimum, double maximum) {
        this.adaptee = function;

        if (minimum >= maximum){
            throw new OptimizationException(this.getClass().getSimpleName() + ": new minimum has to be smaller than new maximum, but " + minimum + " is not smaller than " + maximum);
        }

        createNewBounds(minimum, maximum);
    }

    /**
     * Creates new instance of FunctionBounds that restricts function bounds to desired interval.
     * @param newMinimum - new minimum for function bound.
     * @param newMaximum - new maximum for function bound.
     */
    protected void createNewBounds(double newMinimum, double newMaximum){
        int dimension = this.adaptee.getDimension();
        double oldMinima[] = this.adaptee.getMinimum();
        double oldMaxima[] = this.adaptee.getMaximum();
        this.minima = new double[dimension];
        this.maxima = new double[dimension];
        // creating new maxima and minima
        for (int i = 0; i < dimension; i++){
            if (newMinimum > oldMaxima[i]){
                throw new OptimizationException(this.getClass().getSimpleName() + ": new minimum in dimension " + i + " is bigger than old maximum, i.e. " + newMinimum + " > " + oldMaxima[i]);
            }
            if (newMaximum < oldMinima[i]){
                throw new OptimizationException(this.getClass().getSimpleName() + ": new maximum in dimension " + i +" is smaller than old minimum, i.e. " + newMaximum + " < " + oldMinima[i]);
            }

            // setting new minimum, that won't stretch minimum more than in old bounds
            if (oldMinima[i] > newMinimum){
                minima[i] = oldMinima[i];
            } else {
                minima[i] = newMinimum;
            }
            // setting new maximum, that won't stretch minimum more than in old bounds
            if (oldMaxima[i] < newMaximum){
                maxima[i] = oldMaxima[i];
            } else {
                maxima[i] = newMaximum;
            }
        }
    }


    public double[] getMinimum() {
        return Arrays.copyOf(minima, minima.length);
    }

    public double[] getMaximum() {
        return Arrays.copyOf(maxima, maxima.length);
    }

    public boolean hasAnalyticalGradient() {
        return this.adaptee.hasAnalyticalGradient();
    }

    public boolean hasAnalyticalHessian() {
        return this.adaptee.hasAnalyticalHessian();
    }

    public boolean isDynamic() {
        return this.adaptee.isDynamic();
    }

    public boolean inBounds(Point position) {
        return this.adaptee.inBounds(position);
    }

    public double valueAt(Point point) {
        return this.adaptee.valueAt(point);
    }

    public int getDimension() {
        return this.adaptee.getDimension();
    }

    public void resetGenerationCount() {
        this.adaptee.resetGenerationCount();
    }

    public void nextGeneration() {
        this.adaptee.resetGenerationCount();
    }

    public void setGeneration(int currentGeneration) {
        this.adaptee.setGeneration(currentGeneration);
    }

    public Gradient gradientAt(Point point) {
        return this.adaptee.gradientAt(point);
    }

    public Hessian hessianAt(Point point) {
        return this.adaptee.hessianAt(point);
    }
}
