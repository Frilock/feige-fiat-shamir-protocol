package org.suai.protocol;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.math.BigInteger.*;

/**
 * Доверенный центр T публикует большое число n=pq, где p и q — простые числа, которые держатся в секрете.
 * Также выбираются целые числа k и t - параметры безопасности.
 */
public class TrustCenter {
    private static final Logger logger = Logger.getLogger(TrustCenter.class.getName());

    private static final int P_LENGTH = 128;
    private static final int Q_LENGTH = 32;
    private static final int SEED_LENGTH = 128;

    public BigInteger generateN() {
        BigInteger n = ONE;
        try {
            BigInteger[] primes = generatePrimes(P_LENGTH, Q_LENGTH, SEED_LENGTH);
            BigInteger p = primes[0];
            BigInteger q = primes[1];
            n = ONE.multiply(q).multiply(p);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception: ", e);
        }
        return n;
    }

    private BigInteger[] generatePrimes(int pLength, int qLength, int seedLength) throws NoSuchAlgorithmException {
        if (seedLength < qLength) {
            throw new IllegalArgumentException("Error");
        }
        int outLength = 128;
        int n = (int) (Math.ceil(pLength / outLength) - 1);
        int b = pLength - 1 - (n * outLength);
        do {
            BigInteger domainParameterSeed = new BigInteger(seedLength, new Random());
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] hash = messageDigest.digest(domainParameterSeed.toByteArray());
            BigInteger u = new BigInteger(hash).mod(TWO.pow(qLength - 1));
            BigInteger q = TWO.pow(qLength - 1).add(u).add(ONE).subtract(u.mod(TWO));
            if (!millerRabinTest(q, 5)) {
                continue;
            }
            int offset = 1;
            for (int i = 0; i < 4 * pLength - 1; i++) {
                BigInteger[] v = new BigInteger[n + 1];
                for (int j = 0; j <= n; j++) {
                    BigInteger value = domainParameterSeed
                            .add(valueOf(offset))
                            .add(valueOf(j))
                            .mod(TWO.pow(seedLength));

                    hash = messageDigest.digest(value.toByteArray());
                    v[j] = new BigInteger(hash);
                }
                BigInteger w = BigInteger.ZERO;
                for (int k = 0; k < n; k++) {
                    w = w.add(v[k].multiply(TWO.pow(k * outLength)));
                }
                w = w.add(v[v.length - 1]
                        .mod(TWO.pow(b))
                        .multiply(TWO.pow(n * outLength)));
                BigInteger x = w.add(TWO.pow(pLength - 1));
                BigInteger c = x.mod(TWO.multiply(q));
                BigInteger p = x.subtract(c.subtract(ONE));
                if (p.compareTo(TWO.pow(pLength - 1)) >= 0) {
                    if (millerRabinTest(p, 5)) {
                        return new BigInteger[]{p, q};
                    }
                }
                offset = offset + n + 1;
            }
        } while (true);
    }

    private boolean millerRabinTest(BigInteger n, int iteration) {
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3)))
            return true;
        if (n.compareTo(BigInteger.TWO) < 0 || n.remainder(BigInteger.TWO).equals(BigInteger.ZERO))
            return false;
        BigInteger t = n.subtract(BigInteger.ONE);
        int s1 = 0;

        while (Objects.equals(n.remainder(TWO), ZERO)) {
            t = t.divide(BigInteger.TWO);
            s1 += 1;
        }

        for (int i = 0; i < iteration; i++) {
            BigInteger a;
            do {
                a = new BigInteger(n.bitLength(), new Random());
            } while (a.compareTo(BigInteger.TWO) < 0 || a.compareTo(n.subtract(BigInteger.TWO)) >= 0);
            BigInteger bigIntegerX = modExp(a, t, n);
            if (bigIntegerX.equals(BigInteger.ONE) || bigIntegerX.equals(n.subtract(BigInteger.ONE)))
                continue;

            for (int j = 1; j < s1; j++) {
                bigIntegerX = modExp(bigIntegerX, BigInteger.TWO, n);
                if (bigIntegerX.equals(BigInteger.ONE))
                    return false;
                if (bigIntegerX.equals(n.subtract(BigInteger.ONE)))
                    break;
            }
            if (!bigIntegerX.equals(n.subtract(BigInteger.ONE)))
                return false;
        }
        return true;
    }

    private BigInteger modExp(BigInteger base, BigInteger exponent, BigInteger module) {
        int trailingZeroBitsCount = exponent.getLowestSetBit();
        if (trailingZeroBitsCount == -1) {
            return BigInteger.ONE.mod(module);
        }
        BigInteger z = modExp(base, exponent.shiftRight(1), module);
        BigInteger result = z.pow(2).mod(module);
        if (trailingZeroBitsCount != 0) {
            return result;
        }
        return result.multiply(base).mod(module);
    }
}