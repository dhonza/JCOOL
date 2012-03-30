package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.evaluators;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.FunctionEvaluator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 14.2.2011
 * Time: 21:31:23
 * SimpleFunctionEvaluator which sequentially evaluates all Individuals in given Populations according to given function.
 */
@Component(name = "Simple function evaluator", description = "Sequentially evaluates all Individuals in given Populations")
public class SimpleFunctionEvaluator implements FunctionEvaluator {

    public void evaluate(Population[] populations, Function function) {
        // check the arguments
        if (populations != null && function != null){
            for (Population population : populations){
                if (population.getIndividuals() != null){
                    for (Individual individual : population.getIndividuals()){
                        if (individual.getRepresentation() != null){
                            individual.setValue(function.valueAt(individual.getCurrentPosition()));
                        }
                    }
                }
            }
        }
    }
}
