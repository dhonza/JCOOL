package cz.cvut.felk.cig.jcool.benchmark.method.cmaesold;

import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;

import java.util.*;

/*
    Copyright 1996, 2003, 2005, 2007 Nikolaus Hansen
    e-mail: hansen .AT. bionik.tu-berlin.de
            hansen .AT. lri.fr

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License, version 3,
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 log of changes:
     o init() cannot be called twice anymore, it's saver like this
     o warning() and error() print also to display-file
     o checkEigenSystem() call is now an option, gives warnings, not
       errors, and has its precision criteria adapted to Java.
     o 06/08 fix: error for negative eigenvalues did not show up
     o 09/08: diagonal option included
     o updateDistribution(double[][], double[]) is available, which
       implements an interface, independent of samplePopulation().

 WISH LIST:
     o save all input parameters as output-properties file
     o implement updateDistribution(ISolutionPoint[] pop)
     o explicit control of data writing behavior in terms of iterations
       to wait until the next writing?
     o clean up sorting of eigenvalues/vectors which is done repeatedly
     o implement a good boundary handling
     o check Java random number generator and/or implement a private one.
	 o implement a general initialize_with_evaluated_points method, which
	   estimates a good mean and covariance matrix either from all points
	   or only from the lambda best points (actually mu best points then).
	   cave about outlier points.
     o implement a CMA-ES-specific feed points method for initialization. It should
       accept a population of evaluated points iteratively. It
       just needs to call updateDistribution with a population as input.
	 o save z instead of recomputing it?
     o improve error management to reasonable standard
     o provide output writing for given evaluation numbers and/or given fitness values
     o better use the class java.lang.Object.Date to handle elapsed times?

 */

/**
 * implements the Covariance Matrix Adaptation Evolution Strategy (CMA-ES)
 * for non-linear, non-convex, non-smooth, global function minimization. The CMA-Evolution Strategy
 * (CMA-ES) is a reliable stochastic optimization method which should be applied,
 * if derivative based methods, e.g. quasi-Newton BFGS or conjugate
 * gradient, fail due to a rugged search landscape (e.g. noise, local
 * optima, outlier, etc.)  of the objective function. Like a
 * quasi-Newton method the CMA-ES learns and applies a variable metric
 * of the underlying search space. Unlike a quasi-Newton method the
 * CMA-ES does neither estimate nor use gradients, making it considerably more
 * reliable in terms of finding a good, or even close to optimal, solution, finally.
 * <p/>
 * <p>In general, on smooth objective functions the CMA-ES is roughly ten times
 * slower than BFGS (counting objective function evaluations, no gradients provided).
 * For up to <math>N=10</math> variables also the derivative-free simplex
 * direct search method (Nelder & Mead) can be faster, but it is
 * far less reliable than CMA-ES.
 * <p/>
 * <p>The CMA-ES is particularly well suited for non-separable
 * and/or badly conditioned problems.
 * To observe the advantage of CMA compared to a conventional
 * evolution strategy, it will usually take about 30&#215;<math>N</math> function
 * evaluations. On difficult problems the complete
 * optimization (a single run) is expected to take <em>roughly</em>  between
 * <math>30&#215;N</math> and <math>300&#215;N<sup>2</sup></math>
 * function evaluations.
 * <p/>
 * <p>The main functionality is provided by the methods <code>double[][] {@link #samplePopulation()}</code> and
 * <code>{@link #updateDistribution(double[])}</code> or <code>{@link #updateDistribution(double[][], double[])}</code>.
 * Here is an example code snippet, see file
 * <tt>CMAExample1.java</tt> for a similar example, and
 * <tt>CMAExample2.java</tt> for a more extended example with multi-starts implemented.
 * <pre>
 * // new a CMA-ES and set some initial values
 * CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
 * cma.readProperties(); // read options, see file CMAEvolutionStrategy.properties
 * cma.setDimension(10); // overwrite some loaded properties
 * cma.setTypicalX(0.5); // in each dimension, setInitialX can be used as well
 * cma.setInitialStandardDeviation(0.2); // also a mandatory setting
 * cma.opts.stopFitness = 1e-9;          // optional setting
 * <p/>
 * // initialize cma and get fitness array to fill in later
 * double[] fitness = cma.init();  // new double[cma.parameters.getPopulationSize()];
 * <p/>
 * // initial output to files
 * cma.writeToDefaultFilesHeaders(0); // 0 == overwrites old files
 * <p/>
 * // iteration loop
 * while(cma.stopConditions.getNumber() == 0) {
 * <p/>
 * // core iteration step
 * double[][] pop = cma.samplePopulation(); // get a new population of solutions
 * for(int i = 0; i < pop.length; ++i) {    // for each candidate solution i
 * fitness[i] = fitfun.valueOf(pop[i]); //    compute fitness value, where fitfun
 * }                                        //    is the function to be minimized
 * cma.updateDistribution(fitness);         // use fitness array to update search distribution
 * <p/>
 * // output to files
 * cma.writeToDefaultFiles();
 * ...in case, print output to console, eg. cma.println(),
 * or process best found solution, getBestSolution()...
 * } // while
 * <p/>
 * // evaluate mean value as it is the best estimator for the optimum
 * cma.setFitnessOfMeanX(fitfun.valueOf(cma.getMeanX())); // updates the best ever solution
 * ...retrieve best solution, termination criterion via stopConditions etc...
 * <p/>
 * return cma.getBestX(); // best evaluated search point
 * <p/>
 *   </pre>
 * <P> The implementation follows very closely <a name=HK2004>[3]</a>. It supports small and large
 * population sizes, the latter by using the rank-&micro;-update [2],
 * together with weighted recombination for the covariance matrix, an
 * improved parameter setting for large populations [3] and an (initially) diagonal covariance matrix [5].
 * The latter is particularly useful for large dimension, e.g. larger 100.
 * The default population size is small [1].</P>
 * <p/>
 * <P><B>Remark</B>: In order to solve an optimization problem in reasonable time it needs to be
 * reasonably encoded. In particular the domain width of variables should be
 * similar for all objective variables (decision variables),
 * such that the initial standard deviation can be chosen the same
 * for each variable. For example, an affine-linear transformation could be applied to
 * each variable, such that its typical domain becomes the interval [0,1].
 * For positive variables a log-encoding or a square-encoding
 * should be considered, to avoid the need to set a hard boundary at zero.
 * </P>
 * <p/>
 * <P><B>References</B>
 * <UL>
 * <LI>[1] Hansen, N. and A. Ostermeier (2001). Completely
 * Derandomized Self-Adaptation in Evolution Strategies. <I>Evolutionary
 * Computation</I>, 9(2), pp. 159-195.
 * </LI>
 * <LI>[2] Hansen, N., S.D. M&uuml;ller and
 * P. Koumoutsakos (2003). Reducing the Time Complexity of the
 * Derandomized Evolution Strategy with Covariance Matrix Adaptation
 * (CMA-ES). <I>Evolutionary Computation</I>, 11(1), pp. 1-18.
 * <p/>
 * <LI>[3] Hansen and Kern (2004). Evaluating the CMA Evolution
 * Strategy on Multimodal Test Functions. In <I> Eighth International
 * Conference on Parallel Problem Solving from Nature PPSN VIII,
 * Proceedings</I>, pp. 282-291, Berlin: Springer.
 * </LI>
 * <LI>[4]
 * Auger, A, and Hansen, N. (2005). A Restart CMA Evolution Strategy
 * With Increasing Population Size.</A> In <I>Proceedings of the IEEE
 * Congress on Evolutionary Computation, CEC 2005</I>, pp.1769-1776.
 * </LI>
 * <LI>[5]
 * Ros, R. and N. Hansen (2008). A Simple
 * Modification in CMA-ES Achieving Linear Time and Space Complexity.
 * In Rudolph et al. (eds.) <I>Parallel Problem Solving from Nature, PPSN X,
 * Proceedings</I>, pp. 296-305, Springer.
 * </LI>
 * </UL>
 * </P>
 *
 * @author Nikolaus Hansen, 1996, 2003, 2005, 2007
 * @see #samplePopulation()
 * @see #updateDistribution(double[])
 */
public class CMAEvolutionStrategy {

  void testAndCorrectNumerics() {
    /* Flat Fitness, Test if function values are identical */
    if (getCountIter() > 1 || (getCountIter() == 1 && state >= 3))
      if (fit.fitness[0].val == fit.fitness[Math.min(lambda - 1, lambda / 2 + 1) - 1].val) {
        //System.out.println("flat fitness landscape, consider reformulation of fitness, step-size increased");
        sigma *= Math.exp(0.2 + cs / damps);
      }

    double fac = 1;
    if (math.max(diagD) < 1e-6)
      fac = 1. / math.max(diagD);
    else if (math.min(diagD) > 1e4)
      fac = 1. / math.min(diagD);

    if (fac != 1.) {
      sigma /= fac;
      for (int i = 0; i < N; ++i) {
        pc[i] *= fac;
        diagD[i] *= fac;
        for (int j = 0; j <= i; ++j)
          C[i][j] *= fac * fac;
      }
    }
  }

  /**
   * options that can be changed (fields can be assigned) at any time to control
   * the running behavior
   */
  public CMAOptions options = new CMAOptions();

  int N;
  long seed = System.currentTimeMillis();
  Random rand = new Random(seed);

  final MyMath math = new MyMath();
  double axisratio;
  long counteval;
  long countiter;

  long bestever_eval;
  double[] bestever_x;
  double bestever_fit = Double.NaN;

  double sigma = 0.0;
  double[] typicalX;
  double[] initialX;
  double[] LBound, UBound;
  double[] xmean;
  double xmean_fit = Double.NaN;
  double[] pc;
  double[] ps;
  double[][] C;
  double maxsqrtdiagC;
  double minsqrtdiagC;
  double[][] B;
  double[] diagD;
  boolean flgdiag;

  double[] startsigma;
  double maxstartsigma;
  double minstartsigma;

  boolean iniphase;

  /**
   * state (postconditions):
   * -1 not yet initialized
   * 0 initialized init()
   * 0.5 reSizePopulation
   * 1 samplePopulation, sampleSingle, reSampleSingle
   * 2.5 updateSingle
   * 3 updateDistribution
   */
  double state = -1;
  int lockDimension = 0;


  //////////////////////////////////////////////


  int lambda;          /* -> mu, <- N */
  int mu;              /* -> w, (lambda) */
  double mucov;        /* -> ccov */
  double muEff;        /* <- w */
  double[] w;    /* <- mu, -> muEff, mucov, ccov */
  double damps;        /* <- cs, maxeval, lambda */
  double cs;           /* -> damp, <- N */
  double cc;           /* <- N */
  double ccov;         /* <- mucov, <- N, <- diagonalcov */
  double ccovsep;      /* <- ccov */
  double chiN;


  //////////////////////////////////////////////


  long countCupdatesSinceEigenupdate;

  /**
   * Fitness information class.
   */
  class FitnessCollector {
    double history[];
    /**
     * Int holds index for respective arx
     */
    IntDouble[] fitness;
    /**
     * Sorted differently than fitness.
     */
    IntDouble[] raw;

    /**
     * History of delta fitness / sigma^2. Here delta fitness is the minimum of
     * fitness value differences with distance lambda/2 in the ranking.
     */
    double[] deltaFitHist = new double[5];
    int idxDeltaFitHist = 0;
  }

  protected FitnessCollector fit = new FitnessCollector();

  double recentFunctionValue;
  double recentMaxFunctionValue;
  double recentMinFunctionValue;
  int idxRecentOffspring;

  double[][] arx;

  /**
   * Recent population.
   */
  public double[][] population;
  double[] xold;

  double[] BDz;
  double[] artmp;

  /**
   * postpones most initialization. For initialization use setInitial...
   * methods or set up a properties file, see file "CMAEvolutionStrategy.properties".
   */
  public CMAEvolutionStrategy() {
    state = -1;
  }

  /**
   * @param dimension search space dimension, dimension of the
   *                  objective functions preimage, number of variables
   */
  public CMAEvolutionStrategy(int dimension) {
    setDimension(dimension);
    state = -1;
  }

  /**
   * initialization providing all mandatory input arguments at once. The following two
   * is equivalent
   * <PRE>
   * cma.init(N, X, SD);
   * </PRE> and
   * <PRE>
   * cma.setInitalX(X);  //
   * cma.setInitialStandardDeviations(SD);
   * cma.init(N);
   * </PRE>
   * <p/>
   * The call to <code>init</code> is a point of no return for parameter
   * settings, and demands all mandatory input be set. <code>init</code> then forces the
   * setting up of everything and calls
   * <code>parameters.supplementRemainders()</code>. If <code>init</code> was not called before, it is called once in
   * <code>samplePopulation()</code>. The return value is only provided for sake of convenience.
   *
   * @param dimension
   * @param initialX                  double[] can be of size one, where all variables are set to the
   *                                  same value, or of size dimension
   * @param initialStandardDeviations can be of size one, where all standard
   *                                  deviations are set to the same value, or of size dimension
   * @return <code>double[] fitness</code> of length population size lambda to assign and pass
   *         objective function values to <code>{@link #updateDistribution(double[])}</code>
   * @see #init()
   * @see #init(int)
   * @see #setInitialX(double[])
   * @see #setTypicalX(double[])
   * @see #setInitialStandardDeviations(double[])
   * @see #samplePopulation()
   */
  public double[] init(int dimension, double[] initialX, double[] initialStandardDeviations) {
    setInitialX(initialX);
    setInitialStandardDeviations(initialStandardDeviations);
    return init(dimension);
  }

  private double[] getArrayOf(double x, int dim) {
    double[] res = new double[dim];
    for (int i = 0; i < dim; ++i)
      res[i] = x;
    return res;
  }

  /**
   * @param x   null or x.length==1 or x.length==dim, only for the second case x is expanded
   * @param dim
   * @return <code>null</code> or <code>double[] x</code> with <code>x.length==dim</code>
   */
  private double[] expandToDimension(double[] x, int dim) {
    if (x == null)
      return null;
    if (x.length == dim)
      return x;
    if (x.length != 1)
      System.out.println("x must have length one or length dimension");

    return getArrayOf(x[0], dim);
  }

  /**
   * @param dimension search space dimension
   * @see #init(int, double[], double[])
   */
  public double[] init(int dimension) {
    setDimension(dimension);
    return init();
  }

  /**
   * @see #init(int, double[], double[])
   */
  public double[] init() {
    int i;
    if (N <= 0)
      System.out.println("dimension needs to be determined, use eg. setDimension() or setInitialX()");
    if (state >= 0)
      System.out.println("init() cannot be called twice");
    if (state == 0) // less save variant
      return new double[lambda];
    if (state > 0)
      System.out.println("init() cannot be called after the first population was sampled");

    chiN = Math.sqrt(N) * (1.0 - 1.0 / (4.0 * N) + 1.0 / (21.0 * N * N));

    lambda = (int) (4.0 + 3.0 * Math.log(N));
    mu = (int) Math.floor(lambda / 2.);

    w = new double[mu];
    for (i = 0; i < mu; ++i)
      w[i] = (Math.log(mu + 1) - Math.log(i + 1));

    double sum = 0;
    for (double weight : w)
      sum += weight;
    for (i = 0; i < w.length; ++i)
      w[i] /= sum;
    double sum1 = 0;
    double sum2 = 0;
    for (i = 0; i < mu; ++i) {
      sum1 += w[i];
      sum2 += w[i] * w[i];
    }
    muEff = sum1 * sum1 / sum2;

    cs = (muEff + 2) / (N + muEff + 3);
    damps = (1 + 2 * Math.max(0, Math.sqrt((muEff - 1.) / (N + 1.)) - 1)) * Math.max(0.3, 1 - N / (1e-6 + Math.min(Long.MAX_VALUE, 1000 / lambda))) + cs;
    cc = 4.0 / (N + 4.0);
    mucov = muEff;
    ccov = 2.0 / (N + 1.41) / (N + 1.41) / mucov + (1 - (1.0 / mucov)) * Math.min(1, (2 * muEff - 1) / (muEff + (N + 2) * (N + 2)));
    ccovsep = Math.min(1, ccov * (N + 1.5) / 3.0);

    diagD = new double[N];
    for (i = 0; i < N; ++i)
      diagD[i] = 1;

    /* expand Boundaries */
    LBound = expandToDimension(LBound, N);
    if (LBound == null) {
      LBound = new double[N];
      for (i = 0; i < N; ++i)
        LBound[i] = Double.NEGATIVE_INFINITY;
    }

    UBound = expandToDimension(UBound, N);
    if (UBound == null) {
      UBound = new double[N];
      for (i = 0; i < N; ++i)
        UBound[i] = Double.POSITIVE_INFINITY;
    }

    /* Initialization of sigmas */
    if (startsigma != null) { //
      if (startsigma.length == 1) {
        sigma = startsigma[0];
      } else if (startsigma.length == N) {
        sigma = math.max(startsigma);
        if (sigma <= 0)
          System.out.println("initial standard deviation sigma must be positive");
        for (i = 0; i < N; ++i) {
          diagD[i] = startsigma[i] / sigma;
        }
      } else
        assert false;
    } else {
      // we might use boundaries here to find startsigma, but I prefer to have stddevs mandatory
      System.out.println("no initial standard deviation specified, use setInitialStandardDeviations()");
      sigma = 0.5;
    }

    if (sigma <= 0 || math.min(diagD) <= 0) {
      System.out.println("initial standard deviations not specified or non-positive, " +
          "use setInitialStandarddeviations()");
      sigma = 1;
    }
    /* save initial standard deviation */
    if (startsigma == null || startsigma.length == 1) {
      startsigma = new double[N];
      for (i = 0; i < N; ++i) {
        startsigma[i] = sigma * diagD[i];
      }
    }
    maxstartsigma = math.max(startsigma);
    minstartsigma = math.min(startsigma);
    axisratio = maxstartsigma / minstartsigma; // axis parallel distribution

    /* expand typicalX, might still be null afterwards */
    typicalX = expandToDimension(typicalX, N);

    /* Initialization of xmean */
    xmean = expandToDimension(xmean, N);
    if (xmean == null) {
      /* set via typicalX */
      if (typicalX != null) {
        xmean = typicalX.clone();
        for (i = 0; i < N; ++i)
          xmean[i] += sigma * diagD[i] * rand.nextGaussian();
        /* set via boundaries, is depriciated */
      } else if (math.max(UBound) < Double.MAX_VALUE
          && math.min(LBound) > -Double.MAX_VALUE) {
        System.out.println("no initial search point (solution) X or typical X specified");
        xmean = new double[N];
        for (i = 0; i < N; ++i) { /* TODO: reconsider this algorithm to set X0 */
          double offset = sigma * diagD[i];
          double range = (UBound[i] - LBound[i] - 2 * sigma * diagD[i]);
          if (offset > 0.4 * (UBound[i] - LBound[i])) {
            offset = 0.4 * (UBound[i] - LBound[i]);
            range = 0.2 * (UBound[i] - LBound[i]);
          }
          xmean[i] = LBound[i] + offset + rand.nextDouble() * range;
        }
      } else {
        System.out.println("no initial search point (solution) X or typical X specified");
        xmean = new double[N];
        for (i = 0; i < N; ++i)
          xmean[i] = rand.nextDouble();
      }
    }

    /* interpret missing option value */
    if (options.diagonalCovarianceMatrix < 0) // necessary for hello world message
      options.diagonalCovarianceMatrix = 150 * N / lambda; // cave: dublication below

    /* non-settable parameters */
    pc = new double[N];
    ps = new double[N];
    B = new double[N][N];
    C = new double[N][N]; // essentially only i <= j part is used

    xold = new double[N];
    BDz = new double[N];
    bestever_x = xmean.clone();
    // bestever = new CMASolution(xmean);
    artmp = new double[N];


    fit.deltaFitHist = new double[5];
    fit.idxDeltaFitHist = -1;
    for (i = 0; i < fit.deltaFitHist.length; ++i)
      fit.deltaFitHist[i] = 1.;

    // code to be duplicated in reSizeLambda
    fit.fitness = new IntDouble[lambda];   // including penalties, used yet
    fit.raw = new IntDouble[lambda];       // raw function values
    fit.history = new double[10 + 30 * N / lambda];

    arx = new double[lambda][N];
    population = new double[lambda][N];

    for (i = 0; i < lambda; ++i) {
      fit.fitness[i] = new IntDouble();
      fit.raw[i] = new IntDouble();
    }

    // initialization
    for (i = 0; i < N; ++i) {
      pc[i] = 0;
      ps[i] = 0;
      for (int j = 0; j < N; ++j) {
        B[i][j] = 0;
      }
      for (int j = 0; j < i; ++j) {
        C[i][j] = 0;
      }
      B[i][i] = 1;
      C[i][i] = diagD[i] * diagD[i];
    }
    maxsqrtdiagC = Math.sqrt(math.max(math.diag(C)));
    minsqrtdiagC = Math.sqrt(math.min(math.diag(C)));
    countCupdatesSinceEigenupdate = 0;
    iniphase = false; // obsolete

    /* Some consistency check */
    for (i = 0; i < N; ++i) {
      if (LBound[i] > UBound[i])
        System.out.println("lower bound is greater than upper bound");
      if (typicalX != null) {
        if (LBound[i] > typicalX[i])
          System.out.println("lower bound '" + LBound[i] + "'is greater than typicalX" + typicalX[i]);
        if (UBound[i] < typicalX[i])
          System.out.println("upper bound '" + UBound[i] + "' is smaller than typicalX " + typicalX[i]);
      }
    }

    initialX = xmean.clone(); // keep finally chosen initialX
    state = 0;
    return new double[lambda];
  }

  /**
   * Some simple math utilities.
   */
  class MyMath {
    int itest;

    double square(double d) {
      return d * d;
    }

    double prod(double[] ar) {
      double res = 1.0;
      for (double anAr : ar)
        res *= anAr;
      return res;
    }

    public double median(double ar[]) {
      double[] ar2 = new double[ar.length];
      System.arraycopy(ar, 0, ar2, 0, ar.length);
      Arrays.sort(ar2);
      if (ar2.length % 2 == 0)
        return (ar2[ar.length / 2] + ar2[ar.length / 2 - 1]) / 2.;
      else
        return ar2[ar.length / 2];
    }

    /**
     * @return Maximum value of 1-D double array
     */
    public double max(double ar[]) {
      int i;
      double m;
      m = ar[0];
      for (i = 1; i < ar.length; ++i) {
        if (m < ar[i])
          m = ar[i];
      }
      return m;
    }

    /**
     * sqrt(a^2 + b^2) without under/overflow. *
     */
    public double hypot(double a, double b) {
      double r = 0;
      if (Math.abs(a) > Math.abs(b)) {
        r = b / a;
        r = Math.abs(a) * Math.sqrt(1 + r * r);
      } else if (b != 0) {
        r = a / b;
        r = Math.abs(b) * Math.sqrt(1 + r * r);
      }
      return r;
    }

    /**
     * @return index of minium value of 1-D double array
     */
    public int minidx(double ar[]) {
      return minidx(ar, ar.length - 1);
    }

    /**
     * @param ar     double[]
     * @param maxidx last index to be considered
     * @return index of minium value of 1-D double
     *         array between index 0 and maxidx
     */
    public int minidx(double[] ar, int maxidx) {
      int i, idx;
      idx = 0;
      for (i = 1; i < maxidx; ++i) {
        if (ar[idx] > ar[i])
          idx = i;
      }
      return idx;
    }

    /**
     * @param ar     double[]
     * @param maxidx last index to be considered
     * @return index of minium value of 1-D double
     *         array between index 0 and maxidx
     */
    protected int minidx(IntDouble[] ar, int maxidx) {
      int i, idx;
      idx = 0;
      for (i = 1; i < maxidx; ++i) {
        if (ar[idx].val > ar[i].val)
          idx = i;
      }
      return idx;
    }

    /**
     * @return index of maximum value of 1-D double array
     */
    public int maxidx(double ar[]) {
      int i, idx;
      idx = 0;
      for (i = 1; i < ar.length; ++i) {
        if (ar[idx] < ar[i])
          idx = i;
      }
      return idx;
    }

    /**
     * @return Minimum value of 1-D double array
     */
    public double min(double ar[]) {
      int i;
      double m;
      m = ar[0];
      for (i = 1; i < ar.length; ++i) {
        if (m > ar[i])
          m = ar[i];
      }
      return m;
    }

    /**
     * @return Maximum value of 1-D Object array where the object implements Comparator
     *         Example: max(Double arx, arx[0])
     */
    public Double max(Double ar[], Comparator<Double> c) {
      int i;
      Double m;
      m = ar[0];
      for (i = 1; i < ar.length; ++i) {
        if (c.compare(m, ar[i]) > 0)
          m = ar[i];
      }
      return m;
    }

    /**
     * @return Maximum value of 1-D IntDouble array
     */
    public IntDouble max(IntDouble ar[]) {
      int i;
      IntDouble m;
      m = ar[0];
      for (i = 1; i < ar.length; ++i) {
        if (m.compare(m, ar[i]) < 0)
          m = ar[i];
      }
      return m;
    }

    /**
     * @return Minimum value of 1-D IntDouble array
     */
    public IntDouble min(IntDouble ar[]) {
      int i;
      IntDouble m;
      m = ar[0];
      for (i = 1; i < ar.length; ++i) {
        if (m.compare(m, ar[i]) > 0)
          m = ar[i];
      }
      return m;
    }

    /**
     * @return Minimum value of 1-D Object array defining a Comparator
     */
    public Double min(Double ar[], Comparator<Double> c) {
      int i;
      Double m;
      m = ar[0];
      for (i = 1; i < ar.length; ++i) {
        if (c.compare(m, ar[i]) < 0)
          m = ar[i];
      }
      return m;
    }

    /**
     * @return Diagonal of an 2-D double array
     */
    public double[] diag(double ar[][]) {
      int i;
      double[] diag = new double[ar.length];
      for (i = 0; i < ar.length && i < ar[i].length; ++i)
        diag[i] = ar[i][i];
      return diag;
    }

    /**
     * @return 1-D double array of absolute values of an 1-D double array
     */
    public double[] abs(double v[]) {
      double res[] = new double[v.length];
      for (int i = 0; i < v.length; ++i)
        res[i] = Math.abs(v[i]);
      return res;
    }
  }

  /**
   * Eigen decomposition routine.
   * flgforce == 1 force independent of time measurments,
   * flgforce == 2 force independent of uptodate-status
   */
  void eigendecomposition(int flgforce) {
    /* Update B and D, calculate eigendecomposition */
    int i, j;
    if (countCupdatesSinceEigenupdate == 0 && flgforce < 2)
      return;

    if (flgdiag) {
      for (i = 0; i < N; ++i) {
        diagD[i] = Math.sqrt(C[i][i]);
      }
      countCupdatesSinceEigenupdate = 0;
    } else {
      // set B <- C
      for (i = 0; i < N; ++i)
        for (j = 0; j <= i; ++j)
          B[i][j] = B[j][i] = C[i][j];

      // eigendecomposition
      double[] offdiag = new double[N];
      tred2(N, B, diagD, offdiag);
      tql2(N, diagD, offdiag, B);

      // assign diagD to eigenvalue square roots
      for (i = 0; i < N; ++i) {
        if (diagD[i] < 0) // numerical problem?
          System.out.println("an eigenvalue has become negative");
        diagD[i] = Math.sqrt(diagD[i]);
      }
      countCupdatesSinceEigenupdate = 0;
    } // end Update B and D
    if (math.min(diagD) == 0)
      axisratio = Double.POSITIVE_INFINITY;
    else
      axisratio = math.max(diagD) / math.min(diagD);

  }

  /**
   * Symmetric Householder reduction to tridiagonal form, taken from JAMA package.
   *
   * @param n
   * @param V
   * @param d
   * @param e
   */
  private void tred2(int n, double V[][], double d[], double e[]) {

    for (int j = 0; j < n; j++) {
      d[j] = V[n - 1][j];
    }

    // Householder reduction to tridiagonal form.
    for (int i = n - 1; i > 0; i--) {

      // Scale to avoid under/overflow.
      double scale = 0.0;
      double h = 0.0;
      for (int k = 0; k < i; k++) {
        scale = scale + Math.abs(d[k]);
      }
      if (scale == 0.0) {
        e[i] = d[i - 1];
        for (int j = 0; j < i; j++) {
          d[j] = V[i - 1][j];
          V[i][j] = 0.0;
          V[j][i] = 0.0;
        }
      } else {

        // Generate Householder vector.
        for (int k = 0; k < i; k++) {
          d[k] /= scale;
          h += d[k] * d[k];
        }
        double f = d[i - 1];
        double g = Math.sqrt(h);
        if (f > 0) {
          g = -g;
        }
        e[i] = scale * g;
        h = h - f * g;
        d[i - 1] = f - g;
        for (int j = 0; j < i; j++) {
          e[j] = 0.0;
        }

        // Apply similarity transformation to remaining columns.
        for (int j = 0; j < i; j++) {
          f = d[j];
          V[j][i] = f;
          g = e[j] + V[j][j] * f;
          for (int k = j + 1; k <= i - 1; k++) {
            g += V[k][j] * d[k];
            e[k] += V[k][j] * f;
          }
          e[j] = g;
        }
        f = 0.0;
        for (int j = 0; j < i; j++) {
          e[j] /= h;
          f += e[j] * d[j];
        }
        double hh = f / (h + h);
        for (int j = 0; j < i; j++) {
          e[j] -= hh * d[j];
        }
        for (int j = 0; j < i; j++) {
          f = d[j];
          g = e[j];
          for (int k = j; k <= i - 1; k++) {
            V[k][j] -= (f * e[k] + g * d[k]);
          }
          d[j] = V[i - 1][j];
          V[i][j] = 0.0;
        }
      }
      d[i] = h;
    }

    // Accumulate transformations.
    for (int i = 0; i < n - 1; i++) {
      V[n - 1][i] = V[i][i];
      V[i][i] = 1.0;
      double h = d[i + 1];
      if (h != 0.0) {
        for (int k = 0; k <= i; k++) {
          d[k] = V[k][i + 1] / h;
        }
        for (int j = 0; j <= i; j++) {
          double g = 0.0;
          for (int k = 0; k <= i; k++) {
            g += V[k][i + 1] * V[k][j];
          }
          for (int k = 0; k <= i; k++) {
            V[k][j] -= g * d[k];
          }
        }
      }
      for (int k = 0; k <= i; k++) {
        V[k][i + 1] = 0.0;
      }
    }
    for (int j = 0; j < n; j++) {
      d[j] = V[n - 1][j];
      V[n - 1][j] = 0.0;
    }
    V[n - 1][n - 1] = 1.0;
    e[0] = 0.0;
  }

  /**
   * Symmetric tridiagonal QL algorithm, taken from JAMA package.
   *
   * @param n
   * @param d
   * @param e
   * @param V
   */
  private void tql2(int n, double d[], double e[], double V[][]) {
    for (int i = 1; i < n; i++) {
      e[i - 1] = e[i];
    }
    e[n - 1] = 0.0;

    double f = 0.0;
    double tst1 = 0.0;
    double eps = MachineAccuracy.EPSILON;//Math.pow(2.0, -52.0);

    for (int l = 0; l < n; l++) {

      // Find small subdiagonal element
      tst1 = Math.max(tst1, Math.abs(d[l]) + Math.abs(e[l]));
      int m = l;
      while (m < n) {
        if (Math.abs(e[m]) <= eps * tst1) {
          break;
        }
        m++;
      }

      // If m == l, d[l] is an eigenvalue,
      // otherwise, iterate.
      if (m > l) {
        int iter = 0;
        do {
          iter = iter + 1;  // (Could check iteration count here.)

          // Compute implicit shift
          double g = d[l];
          double p = (d[l + 1] - g) / (2.0 * e[l]);
          double r = math.hypot(p, 1.0);
          if (p < 0) {
            r = -r;
          }
          d[l] = e[l] / (p + r);
          d[l + 1] = e[l] * (p + r);

          double dl1 = d[l + 1];
          double h = g - d[l];
          for (int i = l + 2; i < n; i++) {
            d[i] -= h;
          }
          f = f + h;

          // Implicit QL transformation.
          p = d[m];
          double c = 1.0;
          double c2 = c;
          double c3 = c;
          double el1 = e[l + 1];
          double s = 0.0;
          double s2 = 0.0;
          for (int i = m - 1; i >= l; i--) {
            c3 = c2;
            c2 = c;
            s2 = s;
            g = c * e[i];
            h = c * p;
            r = math.hypot(p, e[i]);
            e[i + 1] = s * r;
            s = e[i] / r;
            c = p / r;
            p = c * d[i] - s * g;
            d[i + 1] = h + s * (c * g + s * d[i]);

            // Accumulate transformation.
            for (int k = 0; k < n; k++) {
              h = V[k][i + 1];
              V[k][i + 1] = s * V[k][i] + c * h;
              V[k][i] = c * V[k][i] - s * h;
            }
          }
          p = -s * s2 * c3 * el1 * e[l] / dl1;

          e[l] = s * p;
          d[l] = c * p;

          // Check for convergence.
        } while (Math.abs(e[l]) > eps * tst1);
      }
      d[l] = d[l] + f;
      e[l] = 0.0;
    }

    // Sort eigenvalues and corresponding vectors.
    for (int i = 0; i < n - 1; i++) {
      int k = i;
      double p = d[i];
      for (int j = i + 1; j < n; j++) {
        if (d[j] < p) { // NH find smallest k>i
          k = j;
          p = d[j];
        }
      }
      if (k != i) {
        d[k] = d[i]; // swap k and i
        d[i] = p;
        for (int j = 0; j < n; j++) {
          p = V[j][i];
          V[j][i] = V[j][k];
          V[j][k] = p;
        }
      }
    }
  }

  /**
   * clones and copies
   *
   * @param popx genotype
   * @param popy phenotype, repaired
   * @return popy
   */
  double[][] genoPhenoTransformation(double[][] popx, double[][] popy) {
    if (popy == null || popy == popx || popy.length != popx.length)
      popy = new double[popx.length][];

    for (int i = 0; i < popy.length; ++i)
      popy[i] = genoPhenoTransformation(popx[i], popy[i]);

    return popy;
  }

  /**
   * just clones and copies
   *
   * @param popx genotype
   * @param popy phenotype, repaired
   * @return popy
   */
  double[][] phenoGenoTransformation(double[][] popx, double[][] popy) {
    if (popy == null || popy == popx || popy.length != popx.length)
      popy = new double[popx.length][];

    for (int i = 0; i < popy.length; ++i)
      popy[i] = phenoGenoTransformation(popx[i], popy[i]);

    return popy;
  }

  /**
   * ust clones and copies
   *
   * @param x genotype
   * @param y phenotype
   * @return y
   */
  double[] genoPhenoTransformation(double[] x, double[] y) {
    if (y == null || y == x || y.length != x.length) {
      y = x.clone();
      return y;
    }
    System.arraycopy(x, 0, y, 0, N);
    return y;
  }

  /**
   * just clones and copies
   *
   * @param x genotype
   * @param y phenotype
   * @return y
   */
  double[] phenoGenoTransformation(double[] x, double[] y) {
    if (y == null || y == x || y.length != x.length) {
      y = x.clone();
      return y;
    }
    System.arraycopy(x, 0, y, 0, N);
    return y;
  }

  /**
   * Samples the recent search distribution lambda times
   *
   * @return double[][] population, lambda times dimension array of sampled solutions,
   *         where <code>lambda == parameters.getPopulationSize()</code>
   * @see #resampleSingle(int)
   * @see #updateDistribution(double[])
   */
  public double[][] samplePopulation() {
    int i, j, iNk;
    double sum;

    if (state < 0)
      init();
    else if (state < 3 && state > 2)
      System.out.println("mixing of calls to updateSingle() and samplePopulation() is not possible");
    else
      eigendecomposition(0); // latest possibility to generate B and diagD

    if (state != 1)
      ++countiter;
    state = 1; // can be repeatedly called without problem
    idxRecentOffspring = lambda - 1; // not really necessary at the moment


    // ensure maximal and minimal standard deviations
    if (options.lowerStandardDeviations != null && options.lowerStandardDeviations.length > 0)
      for (i = 0; i < N; ++i) {
        double d = options.lowerStandardDeviations[Math.min(i, options.lowerStandardDeviations.length - 1)];
        if (d > sigma * minsqrtdiagC)
          sigma = d / minsqrtdiagC;
      }
    if (options.upperStandardDeviations != null && options.upperStandardDeviations.length > 0)
      for (i = 0; i < N; ++i) {
        double d = options.upperStandardDeviations[Math.min(i, options.upperStandardDeviations.length - 1)];
        if (d < sigma * maxsqrtdiagC)
          sigma = d / maxsqrtdiagC;
      }

    testAndCorrectNumerics();

    /* sample the distribution */
    for (iNk = 0; iNk < lambda; ++iNk) { /*
        * generate scaled
        * random vector (D * z)
        */
      for (i = 0; i < N; ++i) { // code duplication from resampleSingle
        if (flgdiag)
          arx[iNk][i] = xmean[i] + sigma * diagD[i] * rand.nextGaussian();
        else
          artmp[i] = diagD[i] * rand.nextGaussian();
      }
      /* add mutation (sigma * B * (D*z)) */
      if (!flgdiag)
        for (i = 0; i < N; ++i) {
          for (j = 0, sum = 0; j < N; ++j)
            sum += B[i][j] * artmp[j];
          arx[iNk][i] = xmean[i] + sigma * sum;
        }
    }

    return population = genoPhenoTransformation(arx, population);

  }

  /**
   * re-generate the <code>index</code>-th solution. After getting lambda
   * solution points with samplePopulation() the i-th point,
   * i=0...lambda-1, can be sampled anew by resampleSingle(i).
   * <p/>
   * <PRE>
   * double[][] pop = cma.samplePopulation();
   * // check some stuff, i-th solution went wrong, therefore
   * pop[i] = cma.resampleSingle(i); // assignment to keep the population consistent
   * for (i = 0,...)
   * fitness[i] = fitfun.valueof(pop[i]);
   * </PRE>
   *
   * @see #samplePopulation()
   */
  public double[] resampleSingle(int index) {
    int i, j;
    double sum;
    if (state != 1)
      System.out.println("call samplePopulation before calling resampleSingle(int index)");

    /* sample the distribution */
    /* generate scaled random vector (D * z) */
    for (i = 0; i < N; ++i) {
      if (flgdiag)
        arx[index][i] = xmean[i] + sigma * diagD[i] * rand.nextGaussian();
      else
        artmp[i] = diagD[i] * rand.nextGaussian();
    }
    /* add mutation (sigma * B * (D*z)) */
    if (!flgdiag)
      for (i = 0; i < N; ++i) {
        for (j = 0, sum = 0.0; j < N; ++j)
          sum += B[i][j] * artmp[j];
        arx[index][i] = xmean[i] + sigma * sum;
      }
    return population[index] = genoPhenoTransformation(arx[index], population[index]);
  }

  /**
   * update of the search distribution from a population and its
   * function values. functionValues establishes an ordering
   * of the elements in population. A failure is
   * likely if population is not originated from #samplePopulation().
   *
   * @see #samplePopulation()
   */
  public void updateDistribution(double[][] population, double[] functionValues) {
    arx = phenoGenoTransformation(population, null);
    updateDistribution(functionValues);
  }

  /**
   * update of the search distribution after samplePopulation(). functionValues
   * determines the selection order (ranking) for the solutions in the previously sampled
   * population.
   *
   * @see #samplePopulation()
   */
  public void updateDistribution(double[] functionValues) {
    if (state == 3) {
      System.out.println("updateDistribution() was already called");
    }
    if (functionValues.length != lambda)
      System.out.println("argument double[] funcionValues.length=" + functionValues.length + "!=" + "lambda=" + lambda);

    /* pass input argument */
    for (int i = 0; i < lambda; ++i) {
      fit.raw[i].val = functionValues[i];
      fit.raw[i].i = i;
    }

    counteval += lambda;
    recentFunctionValue = math.min(fit.raw).val;
    recentMaxFunctionValue = math.max(fit.raw).val;
    recentMinFunctionValue = math.min(fit.raw).val;
    updateDistribution();
  }

  private void updateDistribution() {

    int i, j, k, iNk, hsig;
    double sum, tfac;
    double psxps;

    if (state == 3) {
      System.out.println("updateDistribution() was already called");
    }

    /* sort function values */
    Arrays.sort(fit.raw, fit.raw[0]);

    for (iNk = 0; iNk < lambda; ++iNk) {
      fit.fitness[iNk].val = fit.raw[iNk].val; // superfluous at time
      fit.fitness[iNk].i = fit.raw[iNk].i;
    }

    /* update fitness history */
    for (i = fit.history.length - 1; i > 0; --i)
      fit.history[i] = fit.history[i - 1];
    fit.history[0] = fit.raw[0].val;

    /* save/update bestever-value */
    updateBestEver(arx[fit.raw[0].i], fit.raw[0].val,
        counteval - lambda + fit.raw[0].i + 1);

    /* re-calculate diagonal flag */
    flgdiag = (options.diagonalCovarianceMatrix == 1 || options.diagonalCovarianceMatrix >= countiter);
    if (options.diagonalCovarianceMatrix == -1) // options might have been re-read
      flgdiag = (countiter <= 150 * N / lambda);

    /* calculate xmean and BDz~N(0,C) */
    for (i = 0; i < N; ++i) {
      xold[i] = xmean[i];
      xmean[i] = 0.;
      for (iNk = 0; iNk < mu; ++iNk)
        xmean[i] += w[iNk] * arx[fit.fitness[iNk].i][i];
      BDz[i] = Math.sqrt(muEff) * (xmean[i] - xold[i]) / sigma;
    }

    if (flgdiag) {
      /* cumulation for sigma (ps) using B*z = z = D^-1 BDz given B=I */
      for (i = 0; i < N; ++i) {
        ps[i] = (1. - cs) * ps[i]
            + Math.sqrt(cs * (2. - cs))
            * BDz[i] / diagD[i];
      }
    } else {
      /* calculate z := D^(-1) * B^(-1) * BDz into artmp, we could have stored z instead */
      for (i = 0; i < N; ++i) {
        for (j = 0, sum = 0.; j < N; ++j)
          sum += B[j][i] * BDz[j];
        artmp[i] = sum / diagD[i];
      }
      /* cumulation for sigma (ps) using B*z */
      for (i = 0; i < N; ++i) {
        for (j = 0, sum = 0.; j < N; ++j)
          sum += B[i][j] * artmp[j];
        ps[i] = (1. - cs) * ps[i]
            + Math.sqrt(cs * (2. - cs)) * sum;
      }
    }
    /* calculate norm(ps)^2 */
    psxps = 0;
    for (i = 0; i < N; ++i)
      psxps += ps[i] * ps[i];

    /* cumulation for covariance matrix (pc) using B*D*z~N(0,C) */
    hsig = 0;
    if (Math.sqrt(psxps)
        / Math.sqrt(1. - Math.pow(1. - cs, 2. * countiter))
        / chiN < 1.4 + 2. / (N + 1.)) {
      hsig = 1;
    }
    for (i = 0; i < N; ++i) {
      pc[i] = (1. - cc) * pc[i] + hsig
          * Math.sqrt(cc * (2. - cc)) * BDz[i];
    }

    /* stop initial phase, not in use anymore as hsig does the job */
    if (iniphase
        && countiter > Math.min(1 / cs, 1 + N / mucov))
      if (psxps / damps
          / (1. - Math.pow((1. - cs), countiter)) < N * 1.05)
        iniphase = false;

    /* this, it is harmful in a dynamic environment
* remove momentum in ps, if ps is large and fitness is getting worse */
    if (11 < 3 && psxps / N > 1.5 + 10 * Math.sqrt(2. / N)
        && fit.history[0] > fit.history[1] && fit.history[0] > fit.history[2]) {

      System.out.println(countiter + ": remove momentum " + psxps / N + " "
          + ps[0] + " " + sigma);

      tfac = Math.sqrt((1 + Math.max(0, Math.log(psxps / N))) * N / psxps);
      for (i = 0; i < N; ++i) {
        ps[i] *= tfac;
      }
      psxps *= tfac * tfac;
    }

    /* update of C */
    if (ccov > 0 && !iniphase) {

      ++countCupdatesSinceEigenupdate;

      /* update covariance matrix */
      for (i = 0; i < N; ++i)
        for (j = (flgdiag ? i : 0);
             j <= i; ++j) {
          C[i][j] = (1 - ccovsep)
              * C[i][j]
              + ccovsep
              * (1. / mucov)
              * (pc[i] * pc[j] + (1 - hsig) * cc
              * (2. - cc) * C[i][j]);
          for (k = 0; k < mu; ++k) { /*
                    * additional rank mu
                    * update
                    */
            C[i][j] += ccovsep * (1 - 1. / mucov)
                * w[k]
                * (arx[fit.fitness[k].i][i] - xold[i])
                * (arx[fit.fitness[k].i][j] - xold[j]) / sigma
                / sigma;
          }
        }
      maxsqrtdiagC = Math.sqrt(math.max(math.diag(C)));
      minsqrtdiagC = Math.sqrt(math.min(math.diag(C)));
    } // update of C

    /* update of sigma */
    sigma *= Math.exp(((Math.sqrt(psxps) / chiN) - 1) * cs
        / damps);

    state = 3;

  }

  /**
   * assigns lhs to a different instance with the same values,
   * sort of smart clone
   *
   * @param rhs
   * @param lhs
   * @return 
   */
  double[] assignNew(double[] rhs, double[] lhs) {
    assert rhs != null;
    if (lhs != null && lhs != rhs && lhs.length == rhs.length)
      System.arraycopy(rhs, 0, lhs, 0, lhs.length);
    else
      lhs = rhs.clone();
    return lhs;
  }

  void updateBestEver(double[] x, double fitness, long eval) {
    if (countiter == 1 || fitness < bestever_fit) {
      bestever_fit = fitness;
      bestever_eval = eval;
      bestever_x = assignNew(x, bestever_x);
    }
  }

  /**
   * ratio between length of longest and shortest axis
   * of the distribution ellipsoid, which is the square root
   * of the largest divided by the smallest eigenvalue of the covariance matrix
   */
  public double getAxisRatio() {
    return axisratio;
  }

  /**
   * get best evaluated search point found so far.
   * Remark that the distribution mean was not evaluated
   * but is expected to have an even better function value.
   *
   * @return best search point found so far as double[]
   * @see #getMeanX()
   */
  public double[] getBestX() {
    if (state < 0)
      return null;
    return bestever_x.clone();
  }

  /**
   * objective function value of best solution found so far.
   *
   * @return objective function value of best solution found so far
   */
  public double getBestFunctionValue() {
    if (state < 0)
      return Double.NaN;
    return bestever_fit;
  }

  /**
   * evaluation count when the best solution was found
   */
  public long getBestEvaluationNumber() {
    return bestever_eval;
  }

  /**
   * best search point of the recent iteration.
   *
   * @return Returns the recentFunctionValue.
   * @see #getBestRecentFunctionValue()
   */
  public double[] getBestRecentX() {
    return genoPhenoTransformation(arx[math.minidx(fit.raw, idxRecentOffspring)], null);
  }

  /**
   * objective function value of the,
   * best solution in the
   * recent iteration (population)
   *
   * @return Returns the recentFunctionValue.
   * @see #getBestEvaluationNumber()
   * @see #getBestFunctionValue()
   */
  public double getBestRecentFunctionValue() {
    return recentMinFunctionValue;
  }

  /**
   * objective function value of the,
   * worst solution of the recent iteration.
   *
   * @return Returns the recentMaxFunctionValue.
   */
  public double getWorstRecentFunctionValue() {
    return recentMaxFunctionValue;
  }

  /**
   * Get mean of the current search distribution. The mean should
   * be regarded as the best estimator for the global
   * optimimum at the given iteration.  The return value is
   * <em>not</em> a copy. Therefore do not change it, unless
   * you know what you are doing.
   *
   * @return mean value of the current search distribution
   * @see #getBestX()
   */
  public double[] getMeanX() {
    return xmean.clone();
  }

  public int getDimension() {
    return N;
  }

  /**
   * number of objective function evaluations counted so far
   */
  public long getCountEval() {
    return counteval;
  }

  /**
   * number of iterations conducted so far
   */
  public long getCountIter() {
    return countiter;
  }

  /**
   * the final setting of initial <code>x</code> can
   * be retrieved only after <code>init()</code> was called
   *
   * @return <code>double[] initialX</code> start point chosen for
   *         distribution mean value <code>xmean</code>
   */
  public double[] getInitialX() {
    if (state < 0)
      System.out.println("initiaX not yet available, init() must be called first");
    return initialX.clone();
  }

  /**
   * get used random number generator instance
   */
  public Random getRand() {
    return rand;
  }

  /**
   * @see #setSeed(long)
   */
  public long getSeed() {
    return seed;
  }

  /**
   * number of objective function evaluations counted so far
   */
  public long setCountEval(long c) {
    return counteval = c;
  }

  /**
   * search space dimensions must be set before the optimization is started.
   */
  public void setDimension(int n) {
    if ((lockDimension > 0 || state >= 0) && N != n)
      System.out.println("dimension cannot be changed anymore or contradicts to initialX");
    N = n;
  }

  /**
   * sets typicalX value, the same value in each coordinate
   *
   * @see #setTypicalX(double[])
   */
  public void setTypicalX(double x) {
    if (state >= 0)
      System.out.println("typical x cannot be set anymore");
    typicalX = new double[]{x}; // allows "late binding" of dimension
  }

  /**
   * sets typicalX value, which will be overwritten by initialX setting from properties
   * or {@link #setInitialX(double[])} function call.
   * Otherwise the initialX is sampled normally distributed from typicalX with initialStandardDeviations
   *
   * @see #setTypicalX(double)
   * @see #setInitialX(double[])
   * @see #setInitialStandardDeviations(double[])
   */
  public void setTypicalX(double[] x) {
    if (state >= 0)
      System.out.println("typical x cannot be set anymore");
    if (x.length == 1) {
      setTypicalX(x[0]);
      return;
    }
    if (N < 1)
      setDimension(x.length);
    if (N != x.length)
      System.out.println("dimensions N=" + N + " and input x.length=" + x.length + "do not agree");
    typicalX = new double[N];
    System.arraycopy(x, 0, typicalX, 0, N);
    lockDimension = 1;
  }

  public void setInitialStandardDeviation(double startsigma) {
    if (state >= 0)
      System.out.println("standard deviations cannot be set anymore");
    this.startsigma = new double[]{startsigma};
  }

  public void setInitialStandardDeviations(double[] startsigma) {
    if (state >= 0)
      System.out.println("standard deviations cannot be set anymore");
    if (startsigma.length == 1) { // to make properties work
      setInitialStandardDeviation(startsigma[0]);
      return;
    }
    if (N > 0 && N != startsigma.length)
      System.out.println("dimensions N=" + N + " and input startsigma.length="
          + startsigma.length + "do not agree");
    if (N == 0)
      setDimension(startsigma.length);
    assert N == startsigma.length;
    this.startsigma = startsigma.clone();
    lockDimension = 1;
  }

  /**
   * sets <code>initialX</code> to the same value in each coordinate
   *
   * @param x value
   * @see #setInitialX(double[])
   */
  public void setInitialX(double x) {
    if (state >= 0)
      System.out.println("initial x cannot be set anymore");
    xmean = new double[]{x};
  }

  /**
   * set initial seach point <code>xmean</code> coordinate-wise uniform
   * between <code>l</code> and <code>u</code>,
   * dimension needs to have been set before
   *
   * @param l double lower value
   * @param u double upper value
   * @see #setInitialX(double[])
   * @see #setInitialX(double[], double[])
   */
  public void setInitialX(double l, double u) {
    if (state >= 0)
      System.out.println("initial x cannot be set anymore");
    if (N < 1)
      System.out.println("dimension must have been specified before");
    xmean = new double[N];
    for (int i = 0; i < xmean.length; ++i)
      xmean[i] = l + (u - l) * rand.nextDouble();
    lockDimension = 1;
  }

  /**
   * set initial seach point <code>x</code> coordinate-wise uniform
   * between <code>l</code> and <code>u</code>,
   * dimension needs to have been set before
   *
   * @param l double lower value
   * @param u double upper value
   */
  public void setInitialX(double[] l, double[] u) {
    if (state >= 0)
      System.out.println("initial x cannot be set anymore");
    if (l.length != u.length)
      System.out.println("length of lower and upper values disagree");
    setDimension(l.length);
    xmean = new double[N];
    for (int i = 0; i < xmean.length; ++i)
      xmean[i] = l[i] + (u[i] - l[i]) * rand.nextDouble();
    lockDimension = 1;
  }

  /**
   * set initial search point to input value <code>x</code>. <code>x.length==1</code> is possible, otherwise
   * the search space dimension is set to <code>x.length</code> irrevocably
   *
   * @param x double[] initial point
   * @see #setInitialX(double)
   * @see #setInitialX(double, double)
   */
  public void setInitialX(double[] x) {
    if (state >= 0)
      System.out.println("initial x cannot be set anymore");
    if (x.length == 1) {
      setInitialX(x[0]);
      return;
    }
    if (N > 0 && N != x.length)
      System.out.println("dimensions do not match");
    if (N == 0)
      setDimension(x.length);
    assert N == x.length;
    xmean = new double[N];
    System.arraycopy(x, 0, xmean, 0, N);
    lockDimension = 1;
  }

  public void setRand(Random rand) {
    this.rand = rand;
  }

  /**
   * Setter for the seed for the random number generator
   * java.util.Random(seed). Changing the seed will only take
   * effect before {@link #init()} was called.
   *
   * @param seed a long value to initialize java.util.Random(seed)
   */
  public void setSeed(long seed) {
    if (seed <= 0)
      seed = System.currentTimeMillis();
    this.seed = seed;
    rand.setSeed(seed);
  }
}

class IntDouble implements Comparator<IntDouble> {
  int i;
  double val;

  public IntDouble(double d, int i) {
    this.val = d;
    this.i = i;
  }

  public IntDouble(double d) {
    this.val = d;
  }

  public IntDouble() {
  }

  public int compare(IntDouble o1, IntDouble o2) {
    if (o1.val < o2.val)
      return -1;
    if (o1.val > o2.val)
      return 1;
    if (o1.i < o2.i)
      return -1;
    if (o1.i > o2.i)
      return 1;
    return 0;
  }

  public boolean equals(IntDouble o1, IntDouble o2) {
    return o1.compare(o1, o2) == 0;
  }
}
