package cz.cvut.felk.cig.jcool.benchmark.util;

import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.core.RandomGenerator;
import org.ytoh.configurations.PropertyState;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 13.2.2011
 * Time: 21:12:57
 * Wrap of java.util.Random into JCool. Most range implementations use nextInt and nextDouble methods.
 */
@Component(name = "Simple random generator", description = "Wraps java.util.Random and adds some functionality")
public class SimpleRandomGenerator implements RandomGenerator{

    java.util.Random random;

    @Property(name = "use seed", description = "if checked then Random generator is initialized with given seed")
    boolean useSeed = false;

    @Property(name = "random seed")
    @Range(from = Integer.MIN_VALUE, to = Integer.MAX_VALUE)
    protected int seed;

    public SimpleRandomGenerator(){
        this.random = new Random();
    }

    public SimpleRandomGenerator(long seed){
        this.random = new Random(seed);
    }

    public double nextRandom() {
        return this.random.nextDouble();
    }

    public boolean nextBoolean() {
        return this.random.nextBoolean();
    }
    
    public boolean nextBoolean(double probability) {
        if (probability < 0.0f || probability > 1.0f)
            throw new OptimizationException("probability must be between 0.0 and 1.0 inclusive.");
        if (probability==0.0f) return false;            // fix half-open issues 
        else if (probability==1.0f) return true;        // fix half-open issues
        return nextDouble() < probability;
    }

    public byte nextByte() {
        return (byte) nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1);

    }

    public byte nextByte(byte maxExclusive) {
        return (byte) nextInt(0, maxExclusive);
    }


    public byte nextByte(byte minInclusive, byte maxExclusive) {
        if (minInclusive >= maxExclusive){
            throw new OptimizationException("minInclusive must be smaller than maxExclusive");
        }
        return (byte) nextInt(minInclusive, maxExclusive);
    }

    public int nextInt() {
        return random.nextInt();
    }

    public int nextInt(int maxExclusive) {
        if (maxExclusive <= 0){
            throw new OptimizationException("maxExclusive must be greater than 0");
        }
        return random.nextInt(maxExclusive);
    }

    public int nextInt(int minInclusive, int maxExclusive) {
        if (minInclusive >= maxExclusive){
            throw new OptimizationException("minInclusive must be smaller than maxExclusive");
        }
        int newMax = maxExclusive - minInclusive;
        return random.nextInt(newMax) + minInclusive;
    }

    public long nextLong() {
        return random.nextLong();
    }

    public long nextLong(long maxExclusive) {
        if (maxExclusive <= 0){
            throw new OptimizationException("maxExclusive must be greater than 0");
        }
        return (long)this.nextDouble(maxExclusive);
    }

    public long nextLong(long minInclusive, long maxExclusive) {
        if (minInclusive >= maxExclusive){
            throw new OptimizationException("minInclusive must be smaller than maxExclusive");
        }
        return (long)this.nextDouble(minInclusive, maxExclusive);
    }

    public float nextFloat() {
        return this.random.nextFloat();
    }

    public float nextFloat(float maxExclusive) {
        if (maxExclusive <= 0.0f){
            throw new OptimizationException("maxExclusive must be greater than 0.0");
        }
        return (float)this.nextDouble(maxExclusive);
    }

    public float nextFloat(float minInclusive, float maxExclusive) {
        if (minInclusive >= maxExclusive){
            throw new OptimizationException("minInclusive must be smaller than maxExclusive");
        }
        return (float)nextDouble(minInclusive, maxExclusive);
    }
    
    public double nextDouble() {
        return this.random.nextDouble();
    }

    public double nextDouble(double maxExclusive) {
        if (maxExclusive <= 0.0d){
            throw new OptimizationException("maxExclusive has to be grated than 0.0d");
        }
        return this.nextDouble(0.0d, maxExclusive);
    }

    public double nextDouble(double minInclusive, double maxExclusive) {
		if (!(minInclusive < maxExclusive)){
			throw new OptimizationException("minInclusive has to be smaller than maxExclusive");
        }
		double rnd = nextDouble() - 0.5d;
		double halfRange = maxExclusive * 0.5 - minInclusive * 0.5;
		return minInclusive + halfRange + (2.0 * rnd) * halfRange;
    }

    public double nextGaussian() {
        return random.nextGaussian();
    }
    
    public double nextGaussian(double mean, double standardDeviation) {
        if (standardDeviation < 0.0d){
            throw new OptimizationException("standardDeviation parameter cannot be negative");
        }
        double rnd = this.nextGaussian();
        return rnd * standardDeviation + mean;
    }

    public int getSeed() {
        return this.seed;
    }

    public PropertyState getSeedState(){
        return this.useSeed ? PropertyState.ENABLED : PropertyState.DISABLED ;
    }

    public void setSeed(int seed) {
        this.seed = seed;
        this.random.setSeed(seed);
    }

    public boolean isUseSeed() {
        return useSeed;
    }

    public void setUseSeed(boolean useSeed) {
        if (useSeed){
            this.random.setSeed(this.seed);
        } else {
            this.random = new Random(); // drops seed if previously set
        }
        this.useSeed = useSeed;
    }

}
