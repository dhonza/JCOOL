package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.FunctionBounds;
import cz.cvut.felk.cig.jcool.core.Point;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.1.2011
 * Time: 10:30:28
 * To change this template use File | Settings | File Templates.
 * Parabolic function from DeJong's Evolutionary Computation book.
 */
@Component(name = "Parabolic function from DeJong's Evolutionary Computation book")
public class DeJongParabolaFunction implements Function, FunctionBounds {

    @Property(name = "Number of dimensions")
    @Range(from = 1, to = Integer.MAX_VALUE)
    private int dimension = 2;

    @Property(name = "Invert function")
    private boolean inverted = false;

    @Property(name = "Result value offset")
    @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
    private double offset = 0.0;

    @Property(name = "Lower bound")
    private double lowerBound = -4.0;

    @Property(name = "Upper bound")
    private double upperBound = 4.0;

    public double valueAt(Point point) {
        double values[] = point.toArray();
        double value = 0;
        for (double i : values){
            value += i * i;
        }
        if (this.inverted) value = -value;
        
        return (value + this.offset);
    }

    public int getDimension() {
        return dimension;
    }

    public boolean isInverted() {
        return inverted;
    }

    public double getOffset() {
        return offset;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public double[] getMinimum() {
        double[] bounds = new double[this.dimension];
        Arrays.fill(bounds, this.lowerBound);
        return bounds;
    }

    public double[] getMaximum() {
        double[] bounds = new double[this.dimension];
        Arrays.fill(bounds, this.upperBound);
        return bounds;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }
}
