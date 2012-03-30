package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

import cz.cvut.felk.cig.jcool.core.Point;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 24.1.2011
 * Time: 20:48:22
 * To change this template use File | Settings | File Templates.
 * Interface describing basic interface for individual in EvolutionaryOptimizationMethod
 */
public interface Individual extends Comparable<Individual>{

    /**
     * Returns generation, in which the individual was 'born', i.e. was created.
     * @return - number of generation in which was created.
     */
    public int getBirthday();
    
    /**
     * Individual's fitness for fitness proportional selection operators.
     * @return individual's fitness.
     */
    public double getFitness();

    /**
     * Method called from FitnessOperator which receives whole population and thus can make much more, e.g. proper fitness scaling operation
     * @param fitness - fitness value to be set.
     */
    public void setFitness(double fitness);

    /**
     * Returns fitness of it's parent. If multiple parents present, then returns the highers fitness.
     * For evaluation of breed success rate and breeding operator auto-tuning. 
     * @return parent's fitness
     */
    public double getParentFitness();

    public void setParentFitness(double parentFitness);

    /**
     * Breeding operators need to copy the individual at first to avoid modification of this one.
     * @param birthday - birthday of newly created individual.
     * @return deep copy of individual including its representation.
     */
    public Individual copy(int birthday);

    /**
     * Returns function value at it's position.
     * @return individual's current function value.
     */
    public double getValue();

    /**
     * Sets value corresponding to position.
     * @param value - the value which corresponds with current position on the landscape.
     */
    public void setValue(double value);



    /**
     * Returns coordinates in n-dimensional space wrapped in immutable Point instance.
     * Coordinates come from representation instance.
     * @return individual's currnent position.
     */
    public Point getCurrentPosition();

    /**
     * Returns instance of concrete Representation for breeding operators to operate on.
     * @return individual's representation.
     */
    public Representation getRepresentation();

}
