package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.2.2011
 * Time: 22:13:20
 * Simple Population implementation, does not do any additional operations.
 */
@Component(name = "Simple Population", description = "Basic and trivial implementation of Population interface without any additional functionality")
public class SimplePopulation implements Population{

    /**
     * Stores all Individual instances 
     */
    Individual[] individuals;

    /**
     * Initializes new empty instance.
     */
    public SimplePopulation(){
        this.individuals = new Individual[0];
    }

    /**
     * Initializes new instance with given Individuals.
     * @param individuals - Individuals to be part of the Population.
     */
    public SimplePopulation(Individual[] individuals){
        this.individuals = individuals;
    }

    /**
     * Returns the best individual according to the lowest function value. Function value is assumed to be evaluated before.
     * @return Individual with the lowest function value or null, if individuals size is zero.
     */
    public Individual getBestIndividual() {
        if (this.individuals != null && this.individuals.length > 0){
            Individual best = this.individuals[0];
            for (int i = 1; i < individuals.length; i++){
                if (individuals[i].getValue() < best.getValue()){
                    best = individuals[i];
                }
            }
            return best;
        }
        return null;
    }

    /**
     * Returns the best individual according to the highest fitness value. Fitness is assumed to be evaluated before.
     * @return Individual with the highest fitness or null, if individuals size is zero.
     */
    public Individual getFittestIndividual() {
        if (this.individuals != null && this.individuals.length > 0){
            Individual best = this.individuals[0];
            for (int i = 1; i < individuals.length; i++){
                if (individuals[i].getFitness() > best.getFitness()){
                    best = individuals[i];
                }
            }
            return best;
        }
        return null;
    }

    /**
     * Returns reference to array of contained Individual instances.
     * @return array of Individual instances in current individuals.
     */
    public Individual[] getIndividuals() {
        return this.individuals;
    }

    public void setIndividuals(Individual[] individuals) {
        this.individuals = individuals;
    }
}
