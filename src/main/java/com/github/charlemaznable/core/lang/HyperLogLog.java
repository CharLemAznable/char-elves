package com.github.charlemaznable.core.lang;

import com.github.charlemaznable.core.codec.MurmurHash;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNullElseGet;

/*
 * Copyright (C) 2012 Clearspring Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Java implementation of HyperLogLog (HLL) algorithm from this paper:
 * <p/>
 * <a href="http://algo.inria.fr/flajolet/Publications/FlFuGaMe07.pdf">FlFuGaMe07.pdf</a>
 * <p/>
 * HLL is an improved version of LogLog that is capable of estimating
 * the cardinality of a set with accuracy = 1.04/sqrt(m) where
 * m = 2^b.  So we can control accuracy vs space usage by increasing
 * or decreasing b.
 * <p/>
 * The main benefit of using HLL over LL is that it only requires 64%
 * of the space that LL does to get the same accuracy.
 * <p/>
 * This implementation implements a single counter.  If a large (millions)
 * number of counters are required you may want to refer to:
 * <p/>
 * http://dsiutils.di.unimi.it/
 * <p/>
 * It has a more complex implementation of HLL that supports multiple counters
 * in a single object, drastically reducing the java overhead from creating
 * a large number of objects.
 * <p/>
 * This implementation leveraged a javascript implementation that Yammer has
 * been working on:
 * <p/>
 * https://github.com/yammer/probablyjs
 * <p>
 * Note that this implementation does not include the long range correction function
 * defined in the original paper.  Empirical evidence shows that the correction
 * function causes more harm than good.
 * </p>
 * <p/>
 * <p>
 * Users have different motivations to use different types of hashing functions.
 * Rather than try to keep up with all available hash functions and to remove
 * the concern of causing future binary incompatibilities this class allows clients
 * to offer the value in hashed int or long form.  This way clients are free
 * to change their hash function on their own time line.  We recommend using Google's
 * Guava Murmur3_128 implementation as it provides good performance and speed when
 * high precision is required.  In our tests the 32bit MurmurHash function included
 * in this project is faster and produces better results than the 32 bit murmur3
 * implementation google provides.
 * </p>
 */
public final class HyperLogLog {

    private final int log2m;
    private final double alphaMM;
    private final RegisterSet registerSet;

    /**
     * m = (1.04/rsd)^2
     *
     * @param rsd relative standard deviation
     * @return log2m, bucket count m = 1 << log2m
     */
    public static int log2m(double rsd) {
        return (int) (log((1.106 / rsd) * (1.106 / rsd)) / log(2));
    }

    /**
     * rsd = 1.04/sqrt(m)
     *
     * @param log2m log2m, bucket count m = 1 << log2m
     * @return relative standard deviation
     */
    public static double rsd(int log2m) {
        return 1.106 / sqrt(exp(log2m * log(2)));
    }

    /**
     * Create a new HyperLogLog instance using the specified relative standard deviation.
     *
     * @param rsd - the relative standard deviation for the counter.
     *            smaller values create counters that require more space.
     */
    public HyperLogLog(double rsd) {
        this(log2m(rsd));
    }

    /**
     * Create a new HyperLogLog instance.  The log2m parameter defines the accuracy of
     * the counter.  The larger the log2m the better the accuracy.
     * <p/>
     * accuracy = 1 - 1.04/sqrt(2^log2m)
     *
     * @param log2m - the number of bits to use as the basis for the HLL instance
     */
    public HyperLogLog(int log2m) {
        int m = caculateM(log2m);
        this.log2m = log2m;
        this.alphaMM = getAlphaMM(log2m, m);
        this.registerSet = new RegisterSet(m);
    }

    private static int caculateM(int log2m) {
        if (log2m < 0 || log2m > 30) {
            throw new IllegalArgumentException("log2m argument is "
                    + log2m + " and is outside the range [0, 30]");
        }
        return 1 << log2m;
    }

    private static double getAlphaMM(final int log2m, final int m) {
        // See the paper.
        return switch (log2m) {
            case 4 -> 0.673 * m * m;
            case 5 -> 0.697 * m * m;
            case 6 -> 0.709 * m * m;
            default -> (0.7213 / (1 + 1.079 / m)) * m * m;
        };
    }

    @CanIgnoreReturnValue
    public boolean offer(Object o) {
        final int x = MurmurHash.hash(o);
        return offerHashed(x);
    }

    @CanIgnoreReturnValue
    public boolean offerHashed(int hashedValue) {
        // j becomes the binary address determined by the first b log2m of x
        // j will be between 0 and 2^log2m
        final int j = hashedValue >>> (Integer.SIZE - log2m);
        final int r = Integer.numberOfLeadingZeros((hashedValue << this.log2m) | (1 << (this.log2m - 1)) + 1) + 1;
        return registerSet.updateIfGreater(j, r);
    }

    public long cardinality() {
        double registerSum = 0;
        int m = registerSet.count;
        double zeros = 0.0;
        for (int j = 0; j < registerSet.count; j++) {
            int val = registerSet.get(j);
            registerSum += 1.0 / (1 << val);
            if (val == 0) {
                zeros++;
            }
        }

        double estimate = alphaMM * (1 / registerSum);

        if (estimate <= (5.0 / 2.0) * m) {
            // Small Range Estimate
            return round(linearCounting(m, zeros));
        } else {
            return round(estimate);
        }
    }

    private static double linearCounting(int m, double zeros) {
        return m * log(m / zeros);
    }

    public HyperLogLog merge(HyperLogLog... hyperLogLogs) {
        HyperLogLog merged = new HyperLogLog(log2m);
        merged.addAll(this);
        
        if (isNull(hyperLogLogs)) return merged;
        for (HyperLogLog hyperLogLog : hyperLogLogs) {
            merged.addAll(hyperLogLog);
        }
        return merged;
    }

    /**
     * Add all the elements of the other set to this set.
     * <p/>
     * This operation does not imply a loss of precision.
     *
     * @param other A compatible Hyperloglog instance (same log2m)
     */
    public void addAll(HyperLogLog other) {
        if (this.size() != other.size()) {
            throw new IllegalArgumentException("Cannot merge estimators of different sizes");
        }
        registerSet.merge(other.registerSet);
    }

    private int size() {
        return registerSet.size * 4;
    }

    /*
     * Copyright (C) 2012 Clearspring Technologies, Inc.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    public static final class RegisterSet {

        public final static int LOG2_BITS_PER_WORD = 6;
        public final static int REGISTER_SIZE = 5;

        public final int count;
        public final int size;

        private final int[] M;

        public RegisterSet(int count) {
            this(count, null);
        }

        public RegisterSet(int count, int[] initialValues) {
            this.count = count;

            this.M = requireNonNullElseGet(initialValues,
                    () -> new int[getSizeForCount(count)]);
            this.size = this.M.length;
        }

        public static int getBits(int count) {
            return count / LOG2_BITS_PER_WORD;
        }

        public static int getSizeForCount(int count) {
            int bits = getBits(count);
            if (bits == 0) {
                return 1;
            } else if (bits % Integer.SIZE == 0) {
                return bits;
            } else {
                return bits + 1;
            }
        }

        public int get(int position) {
            int bucketPos = position / LOG2_BITS_PER_WORD;
            int shift = REGISTER_SIZE * (position - (bucketPos * LOG2_BITS_PER_WORD));
            return (this.M[bucketPos] & (0x1f << shift)) >>> shift;
        }

        public boolean updateIfGreater(int position, int value) {
            int bucket = position / LOG2_BITS_PER_WORD;
            int shift = REGISTER_SIZE * (position - (bucket * LOG2_BITS_PER_WORD));
            int mask = 0x1f << shift;

            // Use long to avoid sign issues with the left-most shift
            long curVal = this.M[bucket] & mask;
            long newVal = (long) value << shift;
            if (curVal < newVal) {
                this.M[bucket] = (int) ((this.M[bucket] & ~mask) | newVal);
                return true;
            } else {
                return false;
            }
        }

        public void merge(RegisterSet that) {
            for (int bucket = 0; bucket < M.length; bucket++) {
                int word = 0;
                for (int j = 0; j < LOG2_BITS_PER_WORD; j++) {
                    int mask = 0x1f << (REGISTER_SIZE * j);

                    int thisVal = (this.M[bucket] & mask);
                    int thatVal = (that.M[bucket] & mask);
                    word |= max(thisVal, thatVal);
                }
                this.M[bucket] = word;
            }
        }
    }
}
