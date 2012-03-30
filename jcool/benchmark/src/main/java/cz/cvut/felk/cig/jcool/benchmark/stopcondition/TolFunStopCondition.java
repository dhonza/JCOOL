package cz.cvut.felk.cig.jcool.benchmark.stopcondition;

import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sulcanto
 */
@Component(
        name = "TolFun stop condition",
        description = "Test whether history of last 10+ceil(30*N/lambda) generations is not below TolFun"
)
public class TolFunStopCondition implements CMAESStopCondition {

    @Property(name = "Use")
    private boolean use = true;

    @Property(name = "TolFun")
    double tolFun = 1e-3;


    List<Double> history;
    int historyDepth;
    int currentGeneration;
    double currentSumOfHisotry;

    public void init(int N, int lambda) {
        this.historyDepth = 10 + (int) Math.ceil(30 * N / lambda);
        this.history = new ArrayList<Double>(historyDepth);
        this.currentGeneration = 0;
        this.currentSumOfHisotry = 0;
    }

    public boolean isConditionMet() {
        if (use == false)
            return false;
        if (currentGeneration >= historyDepth) {
            for (int i = 0; i < historyDepth; i++) {
                if (history.get(i) < tolFun)
                    return true;
            }
        }
        return false;
    }

    public void pushToHistory(Double currentBestFitness) {
        currentGeneration++;

        if (currentGeneration <= historyDepth) { // short history
            history.add(currentBestFitness);
            currentSumOfHisotry += currentBestFitness;

        } else {
            currentSumOfHisotry += currentBestFitness - history.get((currentGeneration - 1) % historyDepth);
            history.set(currentGeneration % historyDepth, currentBestFitness);
        }
    }


    /////
    public String toString() {
        return "Last " + historyDepth + " generations has flat fitness";
    }

    public double getTolFun() {
        return tolFun;
    }

    public void setTolFun(double tolFun) {
        this.tolFun = tolFun;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }
}
