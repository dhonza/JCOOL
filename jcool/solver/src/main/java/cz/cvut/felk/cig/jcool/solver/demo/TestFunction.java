/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.solver.demo;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.FunctionGradient;
import cz.cvut.felk.cig.jcool.core.FunctionHessian;
import cz.cvut.felk.cig.jcool.core.Gradient;
import cz.cvut.felk.cig.jcool.core.Hessian;
import cz.cvut.felk.cig.jcool.core.Point;
import java.util.Random;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.OneOf;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Simple test function without analytical gradient or hessian
 *
 * @author ytoh
 */
@Component(
    name="Test function",
    description="This is test function long description.",
    shortDescription="This is test function short description."
)
public class TestFunction implements Function, FunctionGradient, FunctionHessian {

    @Property
    @Range(from=0,to=10)
    private int xxx = 1;

    @Property
    private boolean hasXxx = true;

    @Property
    @OneOf(value={"a","b","c"})
    private String xxxIndex = "";

    private int value = 100;

    public void setXxxIndex(String xxxIndex) {
        this.xxxIndex = xxxIndex;
    }

    public void setHasXxx(boolean hasXxx) {
        this.hasXxx = hasXxx;
    }

    public void setXxx(int xxx) {
        this.xxx = xxx;
    }

    public boolean getHasXxx() {
        return hasXxx;
    }

    public boolean isHasXxx() {
        return hasXxx;
    }

    public String getXxxIndex() {
        return xxxIndex;
    }

    public int getXxx() {
        return xxx;
    }

    public double valueAt(Point point) {
        Random random = new Random();
        if(random.nextDouble() > 0.4) {
            return value -= random.nextDouble() * random.nextInt(5);
        } else {
            return value += random.nextDouble() * random.nextInt(4);
        }
        
    }

    public int getDimension() {
        return 1;
    }

    public double[] getMinimum() {
        return new double[] { 0.0 };
    }

    public double[] getMaximum() {
        return new double[] { 1.0 };
    }

    public Gradient gradientAt(Point point) {
        return Gradient.valueOf(new double[] {});
    }

    public Hessian hessianAt(Point point) {
        return Hessian.valueOf(new double[][] {});
    }
}
