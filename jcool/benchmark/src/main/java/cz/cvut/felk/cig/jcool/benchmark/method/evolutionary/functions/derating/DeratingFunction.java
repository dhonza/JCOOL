package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.derating;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 16.4.2011
 * Time: 18:53
 * Interface common for all functions that derate fitness coefficient.
 */
public interface DeratingFunction {

    /**
     * Returns derating multiplier for individual's fitness value, which is in given distance from derating point.
     * @param distance - distance of individual from derating point.
     * @return value in range <0.0, 1.0>. Value of 1.0 if individual in given distance in not affected by derating point. Value less than 1.0 if the individual is closer than threshold and his fitness will be derated with given returned multiplier.
     */
    public double getDeratedMultiplier(double distance);
}
