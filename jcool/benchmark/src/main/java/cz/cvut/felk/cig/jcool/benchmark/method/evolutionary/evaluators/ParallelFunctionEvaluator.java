package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.evaluators;

import cz.cvut.felk.cig.jcool.benchmark.util.PopulationUtils;
import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.FunctionEvaluator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import org.ytoh.configurations.annotations.Component;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 14.2.2011
 * Time: 21:40:02
 * Function evaluator which tries to make more threads according to previously set rules for creation of new threads. 
 */
@Component(name = "Parallel function evaluator", description = "Evaluates Individual function value in parallel depending only on count processor cores")
public class ParallelFunctionEvaluator implements FunctionEvaluator {

    /**
     * Descriptor for individual's position in array of populations.
     */
    public static class PositionDescriptor {

        protected final int populationIndex;
        protected final int individualIndex;

        public PositionDescriptor(int populationIndex, int individualIndex) {
            this.populationIndex = populationIndex;
            this.individualIndex = individualIndex;
        }

        public int getPopulationIndex() {
            return populationIndex;
        }
        
        public int getIndividualIndex() {
            return individualIndex;
        }
    }

    /**
     * Implementation of runnable responsible for evaluation of given populations segment.
     */
    protected static class EvaluatorThread implements Runnable{

        protected CyclicBarrier barrier;
        protected Function function;
        protected Population[] populations;
        protected PositionDescriptor from;
        protected PositionDescriptor to;

        public EvaluatorThread(CyclicBarrier barrier, Function function, Population[] populations, PositionDescriptor from, PositionDescriptor toExclusive){
            this.barrier = barrier;
            this.function = function;
            this.populations = populations;
            this.from = from;
            this.to = toExclusive;
        }

        public void run() {
            int individualIdx; // starting and current index for individual in currently processed population
            int individualIdxEnd; // final exclusive index of individual in current population that won't be processed
            Individual[] currentPopulation;
            Individual currentIndividual;
            for (int populationIdx = this.from.getPopulationIndex(); populationIdx <= this.to.getPopulationIndex(); populationIdx++){
                currentPopulation = populations[populationIdx].getIndividuals();
                // if first population to evaluate, then starting index is not the first index in array
                if (populationIdx == this.from.getPopulationIndex()){
                    individualIdx = this.from.getIndividualIndex();
                } else {
                    individualIdx = 0;
                }
                // if current population is the last, then use exclusive index, otherwise use population length
                if (populationIdx == this.to.getPopulationIndex()){
                    individualIdxEnd = this.to.getIndividualIndex();
                } else {
                    individualIdxEnd = currentPopulation.length;
                }
                // evaluation from start to the end
                while (individualIdx < individualIdxEnd){
                    currentIndividual = currentPopulation[individualIdx];
                    currentIndividual.setValue(this.function.valueAt(currentIndividual.getCurrentPosition()));
                    individualIdx++;
                }
            }
            try {
                this.barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    protected CyclicBarrier barrier;
    protected int numProcessors;

    public ParallelFunctionEvaluator(){
        numProcessors = Runtime.getRuntime().availableProcessors();
        barrier = new CyclicBarrier(numProcessors);
    }

    public ParallelFunctionEvaluator(int numThreads){
        numProcessors = numThreads;
        barrier = new CyclicBarrier(numProcessors);
    }

    public synchronized void evaluate(Population[] populations, Function function) {
        this.barrier.reset();

        int totalIndividuals = PopulationUtils.sumSizes(populations);
        int individualsPerThread = totalIndividuals / numProcessors;
        PositionDescriptor from = new PositionDescriptor(0, 0);
        PositionDescriptor toExclusive;
        if (individualsPerThread > 0){
            int currentIndividualExclusive = individualsPerThread;
            for (int i = 0; i < numProcessors - 1; i++){
                toExclusive = getIndexForIndividual(populations, currentIndividualExclusive);
                new Thread(new EvaluatorThread(this.barrier, function, populations, from, toExclusive)).start();

                from = getIndexForIndividual(populations, currentIndividualExclusive);
                currentIndividualExclusive += individualsPerThread;
            }
        }
        // final worker is this thread
        toExclusive = new PositionDescriptor(populations.length - 1, populations[populations.length-1].getIndividuals().length);
        new EvaluatorThread(this.barrier, function, populations, from, toExclusive).run(); // except of join we rather compute
    }

    /**
     * Returns coordinates in array of populations that corresponds to the exclusive index of individual.
     * Population index is inclusive, individual index is exclusive.
     * If the inclusive index hits the length of current population length, then population.length is returned as exclusive index.
     * @param populations - array in which to move
     * @param individualIdx - searched index inclusive
     * @return returns one index behind searched
     */
    protected PositionDescriptor getIndexBehindIndividual(Population[] populations, int individualIdx){
        for (int i = 0; i < populations.length; i++){
            Individual[] individuals = populations[i].getIndividuals();
            if (individuals.length > individualIdx ){
                return new PositionDescriptor(i, individualIdx+1);
            } else {
                individualIdx -= individuals.length;
            }
        }
        // if individual too far, then return first index ahead
        return new PositionDescriptor(populations.length-1, populations[populations.length-1].getIndividuals().length);
    }

    protected PositionDescriptor getIndexForIndividual(Population[] populations, int individualIdx){
        for (int i = 0; i < populations.length; i++){
            Individual[] individuals = populations[i].getIndividuals();
            if (individuals.length > individualIdx ){
                return new PositionDescriptor(i, individualIdx);
            } else {
                individualIdx -= individuals.length;
            }
        }
        // if individual too far, then return first index ahead
        return new PositionDescriptor(populations.length-1, populations[populations.length-1].getIndividuals().length-1);
    }
}
