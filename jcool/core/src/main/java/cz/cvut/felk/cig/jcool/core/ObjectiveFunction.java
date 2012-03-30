/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.core;

/**
 * Function able to calculate its value, gradient and hessian at a certain point
 *
 * @author ytoh
 */
public interface ObjectiveFunction extends Function, FunctionGradient, FunctionHessian, FunctionBounds, FunctionDynamics {

    /**
     * @return true if the underlying function is able to calculate its gradient
     */
    boolean hasAnalyticalGradient();

    /**
     * @return true if the underlying function is able to calculate its hessian
     */
    boolean hasAnalyticalHessian();

    /**
     * Indicates whether function value changes through time.
     * @return true if the underlying function changes with time (e.g. generations in evolutionary process)
     */
    boolean isDynamic();

    /**
     * Checks if given position is in function bounds.
     * Point as argument gives extreme inefficiency, but makes evaluation more trustworthy. 
     * @param position - location in n-dimensional space.
     * @return true if given position is within function bounds.
     */
    boolean inBounds(Point position);
}
