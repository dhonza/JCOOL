package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.individual;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.IndividualFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.RepresentationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals.SimpleIndividual;
import cz.cvut.felk.cig.jcool.benchmark.util.SimpleRandomGenerator;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.2.2011
 * Time: 15:21:03
 * Creates SimpleIndividual instances randomly arranged in search space defined by given function. 
 */
@Component(name = "SimpleIndividual factory", description = "Factory responsible for creation of SimpleIndividual instances")
public class SimpleIndividualFactory implements IndividualFactory {

    RandomGenerator randomGenerator;
    RepresentationFactory representationFactory;
    ObjectiveFunction function;

    /**
     * Creates one instance of Simple individual randomly placed in defined search space
     * @param birthday - generation in which the Individuals were born.
     * @param dimension - function dimension.
     * @param minima - function minima.
     * @param maxima - function maxima.
     * @return SimpleIndividual instance.
     */
    protected SimpleIndividual createIndividual(int birthday, int dimension, double[] minima, double[] maxima){
        double[] values = new double[dimension];
        for (int i = 0; i < dimension; i++){
            values[i] = this.randomGenerator.nextDouble(minima[i], maxima[i]);
        }
        Representation representation = this.representationFactory.createRepresentation(values);
        return new SimpleIndividual(birthday, 0.0, representation);
    }

    /**
     * Method responsible for providing demanded number of Individuals.
     * @param count - how many Individuals to create.
     * @param birthday - generation in which the Individuals were born.
     * @return
     */
    public Individual[] createIndividuals(int count, int birthday) {
        if (this.representationFactory == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": representationFactory has not been set!");
        }
        if (this.function == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": function has not been set!");
        }
        if (this.randomGenerator == null){
            this.randomGenerator = new SimpleRandomGenerator();
        }
        if (count > 0){ // parameters are alright, proceed to generation of random individuals.
            return createIndividualsInner(count, birthday);
        }
        return new Individual[0];
    }

    /**
     * Safe part of generation Individuals, all parameter are set correctly.
     * @param count - number of resulting Individuals.
     * @param birthday - generation in which the Individuals were born.
     * @return array of Individuals of given size.
     */
    protected Individual[] createIndividualsInner(int count, int birthday){
        SimpleIndividual[] individuals = new SimpleIndividual[count];
        int dimension = function.getDimension();
        double[] minima = function.getMinimum();
        double[] maxima = function.getMaximum();
        for (int i = 0; i < count; i++){
            individuals[i] = createIndividual(birthday, dimension, minima, maxima);
            individuals[i].setValue(this.function.valueAt(individuals[i].getCurrentPosition()));
        }
        return individuals;
    }

    public void setFunction(ObjectiveFunction objectiveFunction) {
        this.function = objectiveFunction;
    }

    public void setRepresentationFactory(RepresentationFactory representationFactory) {
        this.representationFactory = representationFactory;
    }

    /**
     * Sets optional RandomGenerator instance which will be used for random positions generation.
     * @param randomGenerator - random number generator used for generation of random positions within function bounds, etc.
     */
    public void setRandomGenerator(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }
}
