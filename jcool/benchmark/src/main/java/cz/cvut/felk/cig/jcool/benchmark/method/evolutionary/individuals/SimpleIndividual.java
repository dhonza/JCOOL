package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.individuals;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.core.Point;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.2.2011
 * Time: 22:13:56
 * Simple Individual implementation, does not do any additional operations. 
 */
@Component(name = "Simple Individual", description = "Basic and trivial implementation of Individual interface without any additional functionality")
public class SimpleIndividual implements Individual{

    private double value;
    private int birthday = 0;
    private double fitness = 0.0;
    private double parentFitness = 0.0;
    private Representation representation;
    
    public SimpleIndividual(int birthday, double parentFitness, Representation representation){
        this.birthday = birthday;
        this.parentFitness = parentFitness;
        this.representation = representation;
    }

    /**
     * Deep copy constructor.
     * @param second - SimpleIndividual to be copied.
     * @param birthday - birthday of newly created individual.
     */
    public SimpleIndividual(SimpleIndividual second, int birthday){
        this.value = second.value;
        this.birthday = birthday;
        this.fitness = second.fitness;
        this.parentFitness = second.parentFitness;
        if (second.representation != null)
            this.representation = second.representation.copy();
    }

    public int getBirthday() {
        return this.birthday;
    }
    
    public double getFitness() {
        return this.fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getParentFitness() {
        return this.parentFitness;
    }

    public void setParentFitness(double parentFitness) {
        this.parentFitness = parentFitness;
    }

    public Individual copy(int birthday) {
        return new SimpleIndividual(this, birthday);
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Point getCurrentPosition() {
        return Point.at(this.representation.getDoubleValue());
    }

    public Representation getRepresentation() {
        return this.representation;
    }

    public int compareTo(Individual second) {
        if (this.fitness < second.getFitness()){
            return -1;
        }
        if (this.fitness > second.getFitness()){
            return 1;
        }
        return 0;
    }
}
