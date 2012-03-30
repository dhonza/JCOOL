package cz.cvut.felk.cig.jcool.benchmark.util;

import cz.cvut.felk.cig.jcool.core.RandomGenerator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 23.2.2011
 * Time: 17:04
 * RandomGenerator providing empty implementation. Suitable for testing when ancestor overrides only necesary methods.
 */
public class EmptyRandomGenerator implements RandomGenerator{
    public void setSeed(int seed) {
        throw new NotImplementedException();
    }

    public double nextRandom() {
        throw new NotImplementedException();
    }

    public boolean nextBoolean() {
        throw new NotImplementedException();
    }

    public boolean nextBoolean(double probability) {
        throw new NotImplementedException();
    }

    public byte nextByte() {
        throw new NotImplementedException();
    }

    public byte nextByte(byte maxExclusive) {
        throw new NotImplementedException();
    }

    public byte nextByte(byte minInclusive, byte maxExclusive) {
        throw new NotImplementedException();
    }

    public int nextInt() {
        throw new NotImplementedException();
    }

    public int nextInt(int maxExclusive) {
        throw new NotImplementedException();
    }

    public int nextInt(int minInclusive, int maxExclusive) {
        throw new NotImplementedException();
    }

    public long nextLong() {
        throw new NotImplementedException();
    }

    public long nextLong(long maxExclusive) {
        throw new NotImplementedException();
    }

    public long nextLong(long minInclusive, long maxExclusive) {
        throw new NotImplementedException();
    }

    public float nextFloat() {
        throw new NotImplementedException();
    }

    public float nextFloat(float maxExclusive) {
        throw new NotImplementedException();
    }

    public float nextFloat(float minInclusive, float maxExclusive) {
        throw new NotImplementedException();
    }

    public double nextDouble() {
        throw new NotImplementedException();
    }

    public double nextDouble(double maxExclusive) {
        throw new NotImplementedException();
    }

    public double nextDouble(double minInclusive, double maxExclusive) {
        throw new NotImplementedException();
    }

    public double nextGaussian() {
        throw new NotImplementedException();
    }

    public double nextGaussian(double mean, double standardDeviation) {
        throw new NotImplementedException();
    }
}
