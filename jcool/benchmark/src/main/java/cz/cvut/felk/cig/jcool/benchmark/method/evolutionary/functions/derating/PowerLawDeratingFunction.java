package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.derating;

import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 16.4.2011
 * Time: 19:15
 * "Power law Fitness Derating Function" as described in "A sequential niche technique for multimodal function optimization" by Beasley, David and Bull
 */
@Component(name = "Power law derating function", description = "Power law Fitness Derating Function as described in \"A sequential niche technique for multimodal function optimization\" by Beasley, David and Bull")
public class PowerLawDeratingFunction implements DeratingFunction{

    @Property(name = "niche radius", description = "radius in which operates derating function")
    @Range(from = 0.0, to = Double.MAX_VALUE)
    protected double radius = 1.0;

    @Property(name = "alpha", description = "power factor which determines how concave (alpha > 1) or convex (alpha < 1) will be the derating curve")
    @Range(from = 0.0, to = Double.MAX_VALUE)
    protected double alpha = 1.0;

    public double getDeratedMultiplier(double distance) {
        if (distance < this.radius){
            // (d - r) ^ alpha
            return Math.pow( ( distance / radius ), alpha );
        }
        return 1.0;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }
}
