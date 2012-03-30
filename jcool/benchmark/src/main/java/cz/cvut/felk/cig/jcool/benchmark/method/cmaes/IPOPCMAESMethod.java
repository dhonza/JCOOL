package cz.cvut.felk.cig.jcool.benchmark.method.cmaes;


import cz.cvut.felk.cig.jcool.benchmark.stopcondition.*;
import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;

/**
 * @author sulcanto
 */
@Component(name = "IPOP-CMA-ES: Increasing Population Covariance Matrix Adaptation Evolution Strategy")
public class IPOPCMAESMethod extends RestartCMAESMethod {

    @Property(name = "Multiple of new population from population count before restart")
    int increasePopulationMultiplier = 2;

    private SimpleStopCondition fitnessStopCondition;
    private ConditionCovStopCondition conditionCovStopCondition;
    private TolFunStopCondition tolFunStopCondition;
    private NoEffectAxisStopCondition noEffectToAxisStopCondition;
    private NoEffectCoordStopCondition noEffectCoordStopCondition;
    private EqualFunValuesStopCondition equalFunValuesStopCondition;
    private TolXStopCondition tolXStopCondition;

    public IPOPCMAESMethod(){
        this.fitnessStopCondition = new SimpleStopCondition();
        this.conditionCovStopCondition = new ConditionCovStopCondition();
        this.tolFunStopCondition = new TolFunStopCondition();
        this.noEffectToAxisStopCondition = new NoEffectAxisStopCondition();
        this.noEffectCoordStopCondition = new NoEffectCoordStopCondition();
        this.equalFunValuesStopCondition = new EqualFunValuesStopCondition();
        this.tolXStopCondition = new TolXStopCondition();
    }

    @Override
    public void init(ObjectiveFunction function){

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

    @Override
    protected void setStopConditionParameters() {
        return;
    }

    @Override
    public void optimize(){
        super.optimize();

        fitnessStopCondition.setValue(getBestValueInCurrentGeneration());
        conditionCovStopCondition.setEigenvalues(this.D.getData());
        tolFunStopCondition.pushToHistory(getBestValueInCurrentGeneration());
        noEffectToAxisStopCondition.setValues(D, B, xmean, getCurrentGeneration());
        noEffectCoordStopCondition.setValues(D,xmean,sigma);
        equalFunValuesStopCondition.pushToHistory(getBestValueInCurrentGeneration());
        tolXStopCondition.setValues(pCumulation, C, sigma);

    }

    public boolean isRestartConditionsMet(){
        if(
                fitnessStopCondition.isConditionMet() ||
                conditionCovStopCondition.isConditionMet() ||
                tolFunStopCondition.isConditionMet() ||
                noEffectToAxisStopCondition.isConditionMet() ||
                noEffectCoordStopCondition.isConditionMet() ||
                equalFunValuesStopCondition.isConditionMet() ||
                tolXStopCondition.isConditionMet())
        {
            return true;
        }
        return false;


    }

    public void restart(){
        super.restart();

        this.fitnessStopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
        this.conditionCovStopCondition.init(1e7);
        this.tolFunStopCondition.init(super.N, super.lambda);
        this.noEffectToAxisStopCondition.init(super.N);
        this.noEffectCoordStopCondition.init(super.N);
        this.equalFunValuesStopCondition.init(N,lambda);
        this.tolXStopCondition.init(N);
    }

    protected void calculateLambda() {
        lambda = getRestartCounter()*lambda*increasePopulationMultiplier;
    }


    public int getIncreasePopulationMultiplier() {
        return increasePopulationMultiplier;
    }

    public void setIncreasePopulationMultiplier(int increasePopulationMultiplier) {
        this.increasePopulationMultiplier = increasePopulationMultiplier;
    }
}