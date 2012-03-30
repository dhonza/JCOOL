/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.benchmark.function;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.FunctionBounds;
import cz.cvut.felk.cig.jcool.core.Point;
import org.ytoh.configurations.annotations.Component;

import static java.lang.Math.cos;
import static java.lang.Math.pow;
/**
 *
 * @author ytoh
 */
@Component(name="Levy 5")
public class Levy5 implements Function, FunctionBounds {

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

        return part1 * part2 + pow(coordinates[0] + 1.42513, 2) + pow(coordinates[1] + 0.80032,2);
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
}
