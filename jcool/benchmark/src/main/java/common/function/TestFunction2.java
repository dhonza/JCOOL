package common.function;

/**
 * User: honza
 * Date: 17.2.2007
 * Time: 22:11:38
 */
public class TestFunction2 extends BasicObjectiveFunction {
    public int getNumArguments() {
        return 2;
    }

    public double evaluate(double[] oargument) {
        //(3x-1)^2 + (y+1)^2 + 3.
        numEvaluateCalls++;
        return (3.0 * oargument[0] - 1.0) * (3.0 * oargument[0] - 1.0) + (oargument[1] + 1.0)
                * (oargument[1] + 1.0) + 3.0;
    }

    public void gradient(double[] oargument, double[] ogradient) {
        //[6*(3x-1) 2*(y+1)]
        numGradientCalls++;
        ogradient[0] = 6.0 * (3.0 * oargument[0] - 1.0);
        ogradient[1] = 2.0 * (oargument[1] + 1.0);
    }


    public void hessian(double[] oargument, double[][] ohessian) {
        // |18  0|
        // | 0  2|
        numHessianCalls++;
        ohessian[0][0] = 18.0;
        ohessian[0][1] = 0.0;
        ohessian[1][0] = 0.0;
        ohessian[1][1] = 2.0;
    }

    public boolean isAnalyticGradient() {
        return true;
    }

    public boolean isAnalyticHessian() {
        return true;
    }
}
