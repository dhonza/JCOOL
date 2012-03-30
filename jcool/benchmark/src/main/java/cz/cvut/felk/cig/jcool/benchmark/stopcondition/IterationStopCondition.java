package cz.cvut.felk.cig.jcool.benchmark.stopcondition;

import cz.cvut.felk.cig.jcool.core.StopCondition;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 16.4.2011
 * Time: 11:19
 * Stop condition that is met after given number of iterations.
 * Count starts at zero, so that the condition is met when value (numIterations-1) is reached;
 */
@Component(name = "Iteration stop condition" , description = "Stop condition that is met after given number of iterations")
public class IterationStopCondition implements StopCondition{

    @Property(name = "number of iterations", description = "sets number of iterations that will be executed before condition is met")
    @Range(from = 1, to = Integer.MAX_VALUE)
    protected int numIterations = 10;

    protected int iteration;
    boolean isMet;

    public void init(int numIterations) {
        this.numIterations = numIterations;
        this.iteration = 0;
        this.isMet = false;
    }


    public void init(){
        this.iteration = 0;
        this.isMet = false;
    }

    public void nextIteration() {
        this.isMet = (++iteration) >= numIterations;
    }

    public boolean isConditionMet() {
        return this.isMet;
    }

    @Override
    public String toString() {
        return "Performed " + this.numIterations + " iterations";
    }


    public int getNumIterations() {
        return numIterations;
    }

    public void setNumIterations(int numIterations) {
        this.numIterations = numIterations;
    }
}
