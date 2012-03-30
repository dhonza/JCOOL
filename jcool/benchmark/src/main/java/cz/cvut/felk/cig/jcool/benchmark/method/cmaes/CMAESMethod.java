package cz.cvut.felk.cig.jcool.benchmark.method.cmaes;

import cz.cvut.felk.cig.jcool.benchmark.stopcondition.CMAESStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import org.apache.commons.math.linear.*;
import org.apache.commons.math.random.CorrelatedRandomVectorGenerator;
import org.apache.commons.math.random.GaussianRandomGenerator;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.MersenneTwister;
import org.apache.commons.math.util.MathUtils;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Arrays;

/**
 * @author sulcanto
 */
public abstract class CMAESMethod implements OptimizationMethod<ValuePointListTelemetry> {

    protected ValuePoint theBestPoint;
    protected ValuePoint theWorstPoint;

    protected ObjectiveFunction function;



    private ValuePointListTelemetry telemetry;
    private Consumer<? super ValuePointListTelemetry> consumer;

    @Property(name = "Min")
    @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
    private double min = -10.0;

    @Property(name = "Max")
    @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
    private double max = 10.0;

    @Property(name = "InitialSigma")
    @Range(from = 0, to = Double.MAX_VALUE)
    protected double initialSigma = 0.5;


    /**
     * coordinate wise standard deviation (step size)
     */
    protected double sigma;
    /**
     * best individual fitness in current generation
     */
    private double bestValueInCurrentGeneration;

    /**
     * current generation
     */
    protected int currentGeneration;
    /**
     * number of variables to optimize
     */
    protected int N;
    /**
     * objective variables initial point
     */
    protected RealVector xmean;
    /**
     * population size, offspring number
     */
    protected int lambda;
    /**
     * number of parents/points for recombination
     */
    protected int mu;
    /**
     * array for weighted recombination
     */
    protected RealVector weights;
    /**
     * variance-effectiveness of sum w_i x_i
     */
    protected double muEff;

    /**
     * time constant for cumulation for C
     */
    protected double cCumulation;

    /**
     * t-const for cumulation for sigma control
     */
    protected double cSigma;

    /**
     * learning rate for rank-one update of C
     */
    protected double cRank1;

    /**
     * learning rate for rank-mu update
     */
    protected double cRankMu;

    /**
     * damping for sigma
     */
    protected double dampingForSigma;

    /**
     * evolution paths for C and sigma
     */
    protected RealVector pCumulation, pSigma;

    /**
     * B defines the coordinate system
     */
    protected RealMatrix B;

    /**
     * diagonal D defines the scaling
     */
    protected RealVector D;

    /**
     * covariance matrix C
     */
    protected RealMatrix C;

    /**
     * inverse and square of matrix inverseSqrtC=C^-1/2
     */
    protected RealMatrix inverseAndSqrtC;

    protected int eigenEval;

    /**
     * expectation of ||N(0,I)|| == norm(randn(N,1))
     */
    protected double chiN;

    /**
     * gaussian random generator
     */
    protected GaussianRandomGenerator gaussianGenerator;

    protected static final double GENERATOR_DEPENDENCY_THRESHOLD_MULTIPLE = 1.0e-12;

    protected int countEval;

    public CMAESMethod() {
        this.telemetry = new ValuePointListTelemetry();
    }

    protected void initializeDefaultParameters(){
        //
        // User defined input parameters (need to be edited)
        //
        this.xmean = new ArrayRealVector(N);
        for (int n = 0; n < this.N; n++) {
            this.xmean.setEntry(n, (this.max - this.min) * (Math.random() - 0.5));
        }

        this.sigma = this.initialSigma;

        //
        // Strategy parameter setting: Selection
        //
        this.lambda = (int) (4 + Math.floor(3 * Math.log(this.N)));
        this.mu = (int) Math.floor(this.lambda / 2);

        // weights initiation
        this.weights = new ArrayRealVector(this.mu);
        for (int w = 0; w < this.mu; w++) {
            this.weights.setEntry(w, w+1);
        }
        this.weights = this.weights.mapLog().mapMultiply(-1).mapAdd(Math.log(this.mu+0.5));

        double l1Norm = this.weights.getL1Norm();
        // normalization of weights
        this.weights.mapDivideToSelf(l1Norm);
        // sum of normalised weights
        l1Norm = this.weights.getL1Norm();

        // this.muEff=sum(weights)^2/sum(weights.^2);
        this.muEff = Math.pow(l1Norm, 2) / this.weights.mapPow(2).getL1Norm();

        //
        // Strategy parameter setting: Adaptation
        //

        this.cCumulation = (4 + this.muEff / this.N) / (this.N + 4 + 2 * this.muEff / 2);
        this.cSigma = (this.muEff + 2) / (this.N + this.muEff + 5);
        this.cRank1 = 2 / (Math.pow(N + 1.3, 2) + this.muEff);
        this.cRankMu = 2 * (this.muEff - 2 + 1 / this.muEff) / (Math.pow(this.N + 2, 2) + this.muEff);
        this.dampingForSigma = 1 + 2 * Math.max(0, Math.sqrt((this.muEff - 1) / (N + 1)) - 1) + this.cSigma;

        //
        // Initialize dynamic (internal) strategy parameters and constants
        //
        this.pCumulation = new ArrayRealVector(this.N);
        this.pSigma = new ArrayRealVector(this.N);


        this.B = MatrixUtils.createRealIdentityMatrix(this.N); // defines the coordinate system
        this.D = new ArrayRealVector(this.N, 1);
        this.C = B.multiply(MatrixUtils.createRealDiagonalMatrix(this.D.mapPow(2).toArray())).multiply(this.B.transpose());

        this.inverseAndSqrtC =
                this.B.multiply(MatrixUtils.createRealDiagonalMatrix(this.D.toArray())).multiply(
                        this.B.transpose());


        this.chiN = Math.sqrt(N) * ((1 - ((float) 1 / (4 * N))) + ((float) 1 / (21 * Math.pow(N, 2))));

    }

    public void init(ObjectiveFunction function) {

        this.function = function;
        N = function.getDimension();

        min = Math.max(min, function.getMinimum()[0]);
        max = Math.min(max, function.getMaximum()[0]);

        gaussianGenerator = new GaussianRandomGenerator(new MersenneTwister());

        initializeDefaultParameters();
        this.countEval = 0;

        this.theBestPoint = ValuePoint.at(Point.getDefault(), Double.MAX_VALUE);
        this.theWorstPoint = ValuePoint.at(Point.getDefault(), -Double.MAX_VALUE);
    }

    public StopCondition[] getStopConditions() {
        return new CMAESStopCondition[]{};
    }

    protected ValuePoint[] generatePopulation() {
        CorrelatedRandomVectorGenerator correlatedGenerator = null;
        try {
            correlatedGenerator = new CorrelatedRandomVectorGenerator(
                    this.C, this.C.getNorm() * GENERATOR_DEPENDENCY_THRESHOLD_MULTIPLE, this.gaussianGenerator);
        } catch (NotPositiveDefiniteMatrixException e) {
            e.printStackTrace();
        }

        ValuePoint[] population = new ValuePoint[lambda];

        // Generate and evaluate lambda offspring
        for (int k = 0; k < lambda; k++) {
            Point newIndividual = Point.at((MatrixUtils.createRealVector(correlatedGenerator.nextVector()).mapMultiply(sigma).add(xmean)).toArray());
            population[k] = ValuePoint.at(newIndividual, function.valueAt(newIndividual));
            countEval++;
        }

        return population;
    }

    protected void calculateEigendecomposition(){
        // Update B and D from C
        if (countEval - eigenEval > lambda / (cRank1 + cRankMu) / this.N / 10) { // to achieve O(N^2)
            eigenEval = countEval;
            C = triu(this.C, 0).add(triu(C, 1).transpose());
            EigenDecomposition eig = new EigenDecompositionImpl(C, MathUtils.SAFE_MIN);
            this.D = MatrixUtils.createRealVector(eig.getRealEigenvalues()).mapSqrt();
            this.B = eig.getV();
            this.inverseAndSqrtC =
                    this.B.multiply(MatrixUtils.createRealDiagonalMatrix(this.D.mapPow(-1).getData())).multiply(
                            B.transpose());
        }
    }

    protected void calculateCovariance(final float hSigma, final RealMatrix muDifferenceVectorsFromOldMean){
        this.C = this.C.scalarMultiply(1 - this.cRank1 - this.cRankMu).add(
                (this.C.scalarMultiply(cCumulation * (2 - cCumulation) * (1 - hSigma)).add(
                        pCumulation.outerProduct(pCumulation))).scalarMultiply(cRank1)).add((
                muDifferenceVectorsFromOldMean.multiply(MatrixUtils.createRealDiagonalMatrix(
                        weights.toArray())).multiply(muDifferenceVectorsFromOldMean.transpose())).
                scalarMultiply(cRankMu));
    }

    protected void calculatePSigma(RealVector xold){
        // Cumulation: Update evolution paths
        this.pSigma = inverseAndSqrtC.operate(xmean.subtract(xold)).mapMultiply(
                Math.sqrt(cSigma * (2 - cSigma) * muEff) / sigma).add(
                pSigma.mapMultiply(1 - cSigma)
        );
    }

    protected void calculatePCumulation(float hSigma, RealVector xold){
        this.pCumulation = pCumulation.mapMultiply(1 - cCumulation).add(xmean.subtract(xold).mapMultiply(hSigma * Math.sqrt(cCumulation * (2 - cCumulation) * muEff) / sigma));
    }

    protected float calculateHSigma(){
        return pSigma.mapPow(2).getL1Norm() / (1 - Math.pow(1 - cSigma, 2 * (float) countEval / lambda)) / N <
                        2 + 4 / (N + 1) ? 1 : 0;
    }

    protected void calculateSigma(){
        // Adapt step size sigma
        this.sigma = this.sigma * Math.exp((cSigma / dampingForSigma) * (pSigma.getNorm() / chiN - 1));
    }

    protected void storeTheBestAndWorst(ValuePoint [] population){
        if(theBestPoint.compareTo(population[0])==1)
            theBestPoint = population[0];
        if(theWorstPoint.compareTo(population[population.length-1])==-1)
            theWorstPoint = population[population.length-1];

    }

    protected abstract void setStopConditionParameters();

    public void optimize() {
        ValuePoint[] population = generatePopulation();
        // Sort by fitness and compute weighted mean into xmean
        RealVector xold = xmean; // sore old mean
        Arrays.sort(population); // sort population

        storeTheBestAndWorst(population);

        RealMatrix muBestIndividuals = createMatrixFromPopulation(population, this.mu, this.N);
        this.xmean = muBestIndividuals.operate(weights);// computeMeanFromMuIndividuals(population);

        calculatePSigma(xold);

        float hSigma = calculateHSigma();

        calculatePCumulation(hSigma,xold);

        // rank-mu-update y calculation
        RealMatrix muDifferenceVectorsFromOldMean = calculateMuDifferenceFromOldMean(muBestIndividuals,xold, N, mu, sigma);
        // Adapt covariance matrix C
        calculateCovariance(hSigma,muDifferenceVectorsFromOldMean);

        calculateSigma();

        calculateEigendecomposition();

        this.currentGeneration++;
        bestValueInCurrentGeneration = population[0].getValue();

        telemetry = new ValuePointListTelemetry(Arrays.asList(population));
        if (consumer != null)
            consumer.notifyOf(this);


    }

    /**
     * y variable for rank-mu-update
     * @param muBestIndividuals
     * @param xold
     * @return
     */
    protected static RealMatrix calculateMuDifferenceFromOldMean(RealMatrix muBestIndividuals, RealVector xold,
                                                                 int N, int mu, double sigma){
        RealMatrix muDifferenceVectorsFromOldMean = MatrixUtils.createRealMatrix(N, mu);
        for (int i = 0; i < mu; i++) {
            muDifferenceVectorsFromOldMean.setColumnVector(i,
                    muBestIndividuals.getColumnVector(i).subtract(xold).mapMultiply(1 / sigma));
        }
        return muDifferenceVectorsFromOldMean;

    }

    /**
     * generate matrix from population where each column is one indivudual
     *
     * @param population
     * @return matrix of population
     */
    protected static RealMatrix createMatrixFromPopulation(ValuePoint[] population, int toIndividual, int N) {
        RealMatrix matrix = MatrixUtils.createRealMatrix(N, toIndividual);
        for (int column = 0; column < toIndividual; column++) {
            matrix.setColumn(column, population[column].getPoint().toArray());
        }
        return matrix;
    }

    /**
     * Getting upper triangular part of matrix
     * @param toTriu matrix to be triangulated
     * @param k k = 0 is the main diagonal, k > 0 is above the main diagonal, and k < 0 is below the main diagonal.
     * @return upper triangular matrix
     */
    protected static RealMatrix triu(RealMatrix toTriu, int k) {
        RealMatrix X = toTriu.copy();
        for (int row = 0; row < X.getRowDimension(); row++) {
            for (int column = 0; column < row + k; column++) {
                X.setEntry(row, column, 0.0d);
            }
        }
        return X;
    }

    public void addConsumer(Consumer<? super ValuePointListTelemetry> consumer) {
        this.consumer = consumer;
    }

    public ValuePointListTelemetry getValue() {
        return telemetry;
    }

    protected double getBestValueInCurrentGeneration() {
        return bestValueInCurrentGeneration;
    }

    protected void setBestValueInCurrentGeneration(double theBest){
        this.bestValueInCurrentGeneration = theBest;
    }

    protected int getCurrentGeneration() {
        return currentGeneration;
    }

    protected void setTelemetry(ValuePointListTelemetry telemetry) {
        this.telemetry = telemetry;
    }

    public Consumer<? super ValuePointListTelemetry> getConsumer() {
        return consumer;
    }

    //////

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getInitialSigma() {
        return initialSigma;
    }

    public void setInitialSigma(double initialSigma) {
        this.initialSigma = initialSigma;
    }


}
