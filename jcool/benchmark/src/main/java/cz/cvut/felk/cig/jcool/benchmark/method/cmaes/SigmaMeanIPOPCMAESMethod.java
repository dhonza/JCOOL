package cz.cvut.felk.cig.jcool.benchmark.method.cmaes;

import org.apache.commons.math.linear.MatrixUtils;
import org.ytoh.configurations.annotations.Component;

/**
 * @author sulcanto
 */
@Component(name = "History IPOP-CMA-ES: Increasing Population Covariance Matrix Adaptation Evolution Strategy")
public class SigmaMeanIPOPCMAESMethod extends IPOPCMAESMethod{

    @Override
    public void restart(){
        super.restart();
        double bestWorstDist =
                MatrixUtils.createRealVector(this.theBestPoint.getPoint().toArray()).getDistance(this.theWorstPoint.getPoint().toArray());
        super.xmean = MatrixUtils.createRealVector(super.theBestPoint.getPoint().toArray()).subtract(super.theWorstPoint.getPoint().toArray()).mapDivide(2);
        this.sigma = bestWorstDist/2;
    }
}
