package common.function;

/**
 * Created by IntelliJ IDEA.
 * User: honza
 * Date: 19.2.2007
 * Time: 16:13:41
 * To change this template use File | Settings | File Templates.
 */
public abstract class BasicObjectiveFunction implements ObjectiveFunction {
    //statistics
    protected int numEvaluateCalls = 0;
    protected int numGradientCalls = 0;
    protected int numHessianCalls = 0;


    public int getNumEvaluateCalls() {
        return numEvaluateCalls;
    }

    public int getNumGradientCalls() {
        return numGradientCalls;
    }

    public int getNumHessianCalls() {
        return numHessianCalls;
    }

    public double evaluate(double[] oxvec, double[] ogradient) {
        gradient(oxvec, ogradient);
        return evaluate(oxvec);
    }

    public double evaluate(double[] oxvec, double[] ogradient, double[][] ohessian) {
        hessian(oxvec, ohessian);
        gradient(oxvec, ogradient);
        return evaluate(oxvec);
    }

    public void resetNumberOfCalls() {
        numEvaluateCalls = 0;
        numGradientCalls = 0;
        numHessianCalls = 0;
    }
}
