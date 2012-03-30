package cz.cvut.felk.cig.jcool.benchmark.method.cmaes;

import java.util.ArrayList;
import java.util.List;
/**
 * @author sulcanto
 */
public abstract class RestartCMAESMethod extends CMAESMethod {
   // counting how many times restart conditions met
    private int restartCounter;


    abstract protected void calculateLambda();

    abstract public boolean isRestartConditionsMet();

    protected void restart(){
        restartCounter++;
        initializeDefaultParameters();
        calculateLambda();
    }

    @Override
    public void optimize(){
        super.optimize();
        if(isRestartConditionsMet()){
            restart();
        }
    }

    public int getRestartCounter() {
        return restartCounter;
    }
}
