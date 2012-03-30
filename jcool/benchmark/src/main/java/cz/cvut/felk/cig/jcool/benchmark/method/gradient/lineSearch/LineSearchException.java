package cz.cvut.felk.cig.jcool.benchmark.method.gradient.lineSearch;

import cz.cvut.felk.cig.jcool.core.OptimizationException;

/**
 * User: drchaj1
 * Date: 17.2.2007
 * Time: 21:14:36
 */
public class LineSearchException extends OptimizationException {

    public LineSearchException(String amessage) {
        super(amessage);
    }
}
