package cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch;

import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.Point;

/**
 * User: drchaj1
 * Date: 3.5.2007
 * Time: 18:11:36
 */
public abstract class LineSearch {
    protected double alpha = 0.0;
    protected double[] xAlpha;
    protected double fAlpha;
    protected double[] gAlpha;

    //initial step size
    protected double initAlpha = 1.0;

    protected ObjectiveFunction func;
    protected int n;

    public LineSearch(ObjectiveFunction afunc) {
        func = afunc;
        n = afunc.getDimension();
    }

    public double getAlpha() {
        return alpha;
    }

    public double getInitAlpha() {
        return initAlpha;
    }

    public void setInitAlpha(double ainitAlpha) {
        this.initAlpha = ainitAlpha;
    }

    public abstract double minimize(double ax0[], double[] adir, final double afx0, double[] agx0) throws LineSearchException;

    public double minimize(double ax0[], double[] adir, final double afx0) throws LineSearchException {
        return minimize(ax0, adir, afx0, func.gradientAt(Point.at(ax0)).toArray());
    }

    public double minimize(double ax0[], double[] adir) throws LineSearchException {
        Point p = Point.at(ax0);
        return minimize(ax0, adir, func.valueAt(p), func.gradientAt(p).toArray());
    }
}
