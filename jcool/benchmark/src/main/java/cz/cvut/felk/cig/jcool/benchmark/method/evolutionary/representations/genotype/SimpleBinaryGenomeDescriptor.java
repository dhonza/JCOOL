package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 11.3.2011
 * Time: 13:10
 * Class containing all necessary information for BinaryRepresentation instance to be able to decode and encode its holding value.
 * Does not contain encoded information and thus can be shared among multiple instances of BinaryRepresentations created by the same Factory for the same function.
 */
public class SimpleBinaryGenomeDescriptor {
    protected int numVariables = 0;
    protected int[] variablesLengths = new int[0];
    protected double[] variablesLowerBounds = new double[0];
    protected double[] variablesPrecisions = new double[0];

    public SimpleBinaryGenomeDescriptor(){}

    public void setVariablesLengths(int[] variablesLengths) {
        this.variablesLengths = variablesLengths;
    }

    public void setNumVariables(int numVariables) {
        this.numVariables = numVariables;
    }

    public void setVariablesLowerBounds(double[] variablesLowerBounds) {
        this.variablesLowerBounds = variablesLowerBounds;
    }

    public void setVariablesPrecisions(double[] variablesPrecisions) {
        this.variablesPrecisions = variablesPrecisions;
    }

    public int getNumVariables() {
        return numVariables;
    }

    public int[] getVariablesLengths() {
        return variablesLengths;
    }

    public double[] getVariablesLowerBounds() {
        return variablesLowerBounds;
    }

    public double[] getVariablesPrecisions() {
        return variablesPrecisions;
    }
}
