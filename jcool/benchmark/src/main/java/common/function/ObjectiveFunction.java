package common.function;

/**
 * User: honza
 * Date: 17.2.2007
 * Time: 19:43:19
 */
public interface ObjectiveFunction {
    int getNumArguments();

    double evaluate(double[] oargument);

    double evaluate(double[] oargument, double[] ogradient);

    double evaluate(double[] oargument, double[] ogradient, double[][] ohessian);

    void gradient(double[] oargument, double[] ogradient);

    void hessian(double[] oargument, double[][] ohessian);

    boolean isAnalyticGradient();

    boolean isAnalyticHessian();
}
