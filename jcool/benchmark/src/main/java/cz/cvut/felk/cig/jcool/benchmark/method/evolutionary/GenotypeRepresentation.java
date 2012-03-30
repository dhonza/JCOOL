package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 23.1.2011
 * Time: 15:10:23
 * To change this template use File | Settings | File Templates.
 * Basic interface for all genotype representation implementations.
 * All genotype representation implementations shield operators from implementation of genes.
 * Interface only provides necessary length information. The rest (e.g. chromosome descriptor) is left for concrete implementations.
 */
public interface GenotypeRepresentation extends PhenotypeRepresentation{
    
    /**
     * Returns sum of genes in chromosome.
     * @return total gene count.
     */
    int getTotalLength();

    /**
     * Returns array containing number of genes for each coded variable.
     * @return array containing number of genes for each coded variable.
     */
    int[] getValueLengths();

    /**
     * Returns interpreted boolean value matching for given index in chromosome  
     * @param idx - desired position to obtain.
     * @return true, if position in chromosome is chosen, false if position is not chosen (not realised in given Individual). 
     */
    boolean getGeneAt(int idx);

    /**
     * Sets interpreted boolean value for given index in chromosome.
     * @param idx - index in chromosome which is being modified.
     * @param value - interpreted value to be converted to internal representation.
     */
    void setGeneAt(int idx, boolean value);

    /**
     * Inverts bit value at given position.
     * @param idx - position on which to invert the value.
     */
    void invertGeneAt(int idx);

    /**
     * Swaps given bits in chromosomes between current and given genotype
     * @param secondRepresentation - representation od second Individual.
     * @param positions - array of bit positions to swap
     */
    void swapGenes(GenotypeRepresentation secondRepresentation, int[] positions);

    /**
     * Swaps bits in chromosomes between current Genotype and given GenotypeRepresentation.
     * @param secondRepresentation - representation od second Individual.
     * @param from - starting bit index (inclusive)
     * @param to - ending bit index (exclusive)
     */
    void swapGenes(GenotypeRepresentation secondRepresentation, int from, int to);

    /**
     * Current genotype receives bits at given positions from second GenotypeRepresentation.
     * @param secondRepresentation
     * @param positions
     */
    void receiveGenes(GenotypeRepresentation secondRepresentation, int[] positions);
    void receiveGenes(GenotypeRepresentation secondRepresentation, int from, int to);
    void andGenes(GenotypeRepresentation secondRepresentation, int[] positions);
    void andGenes(GenotypeRepresentation secondRepresentation, int from, int to);
    void orGenes(GenotypeRepresentation secondRepresentation, int[] positions);
    void orGenes(GenotypeRepresentation secondRepresentation, int from, int to);
    void xorGenes(GenotypeRepresentation secondRepresentation, int[] positions);
    void xorGenes(GenotypeRepresentation secondRepresentation, int from, int to);
}
