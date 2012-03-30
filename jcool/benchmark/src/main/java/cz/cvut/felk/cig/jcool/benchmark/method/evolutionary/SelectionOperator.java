package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 30.1.2011
 * Time: 19:24:29
 * Executes selection upon given arrays of individuals.
 * Usage of this operator depends on whether it is survival selection operator or parent selection operator.
 * Selection operator is from its nature representation independent.
 * Survival selection returns maximally two populations (chosen one and the rest).
 * Parent selection returns so many populations, how big is arity of corresponding breed operator.
 * Selected Individuals ARE NOT new Individuals.
 */
public interface SelectionOperator extends EvolutionaryOperator{

    /**
     * Performs selection upon given array of populations.
     * @param populations - array of Populations from which the selection is being performed.
     * @return - array of Individuals[] which has been chosen and maybe the rest, which has not been chosen.
     */
    Population[] select(Population[] populations);

    /**
     * Sets required input arity or throws exception if arity of given value cannot be set.
     * @param arity - demanded input arity.
     */
    public void setInputArity(int arity);

    /**
     * Sets required output arity or throws exception if arity of given value cannot be set.
     * @param arity - demanded output arity.
     */
    public void setOutputArity(int arity);

    /**
     * Sets how many individuals should be produced in every resulting population.
     * @param populationSize - size of every resulting population.
     */
    void setIndividualsPerPopulation(int populationSize);
}
