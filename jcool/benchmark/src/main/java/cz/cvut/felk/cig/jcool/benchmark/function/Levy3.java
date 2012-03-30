package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.FunctionBounds;
import cz.cvut.felk.cig.jcool.core.FunctionGradient;
import cz.cvut.felk.cig.jcool.core.Gradient;
import cz.cvut.felk.cig.jcool.core.Point;

import org.ytoh.configurations.annotations.Component;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 *
 * @author ytoh
 */
@Component(name="Levy 3")
public class Levy3 implements Function, FunctionBounds, FunctionGradient {

    public double valueAt(Point point) {
        double[] coordinates = point.toArray();

        double part1 = 0;
        for (int i = 0; i < 5; i++) {
            part1 += (i * cos((i + 1) * coordinates[0] + i));
        }

        double part2 = 0;
        for (int i = 0; i < 5; i++) {
            part2 += (i * cos((i + 1) * coordinates[1]) + i);
        }

        return part1 * part2;
    }

    public int getDimension() {
        return 2;
    }

    public double[] getMinimum() {
        return new double[] { -10, -10 };
    }

    public double[] getMaximum() {
        return new double[] { 10, 10 };
    }

    public Gradient gradientAt(Point point) {
        double[] coordinates = point.toArray();

        double dx = 0;
        for (int i = 0; i < 5; i++) {
            dx += (-i * (i + 1) * sin((i + 1) * coordinates[0] + i));
        }

        double dy = 0;
        for (int i = 0; i < 5; i++) {
            dy += (-i * (i + 1) * sin((i + 1) * coordinates[1]) + i);
        }

        double x = 0;
        for (int i = 0; i < 5; i++) {
            x += (i * cos((i + 1) * coordinates[0] + i));
        }

        double y = 0;
        for (int i = 0; i < 5; i++) {
            y += (i * cos((i + 1) * coordinates[1]) + i);
        }

        return Gradient.valueOf(dx * y, dy * x);
    }
}
