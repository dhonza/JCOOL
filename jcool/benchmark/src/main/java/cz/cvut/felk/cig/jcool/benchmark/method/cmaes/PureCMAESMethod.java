package cz.cvut.felk.cig.jcool.benchmark.method.cmaes;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.*;
import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.StopCondition;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;

/**
 * @author sulcanto
 */
@Component(name = "Pure CMA-ES: Pure Covariance Matrix Adaptation Evolution Strategy")
public class PureCMAESMethod extends CMAESMethod {

    private SimpleStopCondition fitnessStopCondition;
    private ConditionCovStopCondition conditionCovStopCondition;
    private TolFunStopCondition tolFunStopCondition;
    private NoEffectAxisStopCondition noEffectToAxisStopCondition;
    private NoEffectCoordStopCondition noEffectCoordStopCondition;
    private EqualFunValuesStopCondition equalFunValuesStopCondition;
    private TolXStopCondition tolXStopCondition;


    public PureCMAESMethod()
    {

        this.fitnessStopCondition = new SimpleStopCondition();
        this.conditionCovStopCondition = new ConditionCovStopCondition();
        this.tolFunStopCondition = new TolFunStopCondition();
        this.noEffectToAxisStopCondition = new NoEffectAxisStopCondition();
        this.noEffectCoordStopCondition = new NoEffectCoordStopCondition();
        this.equalFunValuesStopCondition = new EqualFunValuesStopCondition();
        this.tolXStopCondition = new TolXStopCondition();

    }

    public void init(ObjectiveFunction function)
    {

        super.init(function);

        // stop conditions
        this.fitnessStopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
        this.conditionCovStopCondition.init(1e7);
        this.tolFunStopCondition.init(super.N, super.lambda);
        this.noEffectToAxisStopCondition.init(super.N);
        this.noEffectCoordStopCondition.init(super.N);
        this.equalFunValuesStopCondition.init(N,lambda);
        this.tolXStopCondition.init(N);


    }

    public void optimize(){
        super.optimize();
        this.setStopConditionParameters();


    }

    public void setStopConditionParameters(){
        fitnessStopCondition.setValue(getBestValueInCurrentGeneration());
        conditionCovStopCondition.setEigenvalues(this.D.getData());
        tolFunStopCondition.pushToHistory(getBestValueInCurrentGeneration());
        noEffectToAxisStopCondition.setValues(D, B, xmean, getCurrentGeneration());
        noEffectCoordStopCondition.setValues(D,xmean,sigma);
        equalFunValuesStopCondition.pushToHistory(getBestValueInCurrentGeneration());
        tolXStopCondition.setValues(pCumulation, C, sigma);
    }

    public StopCondition[] getStopConditions() {
        StopCondition [] predecessorStopConditions = super.getStopConditions();
        StopCondition [] thisStopConditions = new StopCondition[]{
                this.fitnessStopCondition,
                this.conditionCovStopCondition,
                this.tolFunStopCondition,
                this.noEffectToAxisStopCondition,
                this.noEffectCoordStopCondition,
                this.equalFunValuesStopCondition,
                this.tolXStopCondition,
        };
        StopCondition [] mergedStopConditions = new StopCondition[predecessorStopConditions.length+thisStopConditions.length];

        int i = 0;

        for(int j = 0; j < predecessorStopConditions.length; j++)
            mergedStopConditions[i++]=predecessorStopConditions[j];
        for(int j = 0; j < thisStopConditions.length; j++)
            mergedStopConditions[i++]=thisStopConditions[j];

        return mergedStopConditions;

    }



}