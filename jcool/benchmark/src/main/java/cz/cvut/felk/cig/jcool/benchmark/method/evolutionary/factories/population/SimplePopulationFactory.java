package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.population;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.populations.SimplePopulation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.PopulationFactory;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.2.2011
 * Time: 15:20:27
 * Creates empty instance of SimplePopulation.
 */
@Component(name = "SimplePopulation factory", description = "Factory responsible for creation of SimplePopulation instances")
public class SimplePopulationFactory implements PopulationFactory{
    
    public Population createPopulation() {
        return new SimplePopulation();
    }

    public Population createPopulation(Individual[] individuals) {
        Population population = new SimplePopulation();
        population.setIndividuals(individuals);
        return population;
    }
}
