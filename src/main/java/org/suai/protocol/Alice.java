package org.suai.protocol;

import java.io.File;
import java.math.BigInteger;
import java.util.Random;


public class Alice extends TrustCenter {
    private final Random random;
    private final BigInteger n;
    private final int k;
    private final BigInteger[] s;

    Alice(BigInteger n, int k) {
        this.n = n;
        this.k = k;
        this.s = new BigInteger[k];
        random = new Random();
    }

    BigInteger generateY(int[] e, BigInteger r) {
        BigInteger temp = BigInteger.ONE;
        for (int i = 0; i < k; i++) {
            temp = temp.multiply(s[i].pow(e[i]));
        }
        return temp.multiply(r).mod(n);
    }

    public BigInteger generateR() {
        return new BigInteger(60, random);
    }

    public BigInteger calculateX(int b, BigInteger r) {
        return (r.pow(2).multiply(BigInteger.valueOf((long) Math.pow(-1, b)))).mod(n);
    }

    public int generateB() {
        double temp = Math.random();
        if (temp >= 0.5) {
            return 1;
        } else {
            return 0;
        }
    }

    public void generator() {
        int[] b = new int[k];
        BigInteger[] v = new BigInteger[k];
        BigInteger tempV;
        double temp;
        long tempS;

        for (int i = 0; i < k; i++) {
            temp = Math.random();
            if (temp <= 0.5) {
                b[i] = 0;
            } else {
                b[i] = 1;
            }
        }

        for (int i = 0; i < k; i++) {
            tempS = random.nextLong();
            if (tempS < 0) {
                tempS *= -1;
            }

            if (n.compareTo(BigInteger.valueOf(tempS)) > 0) {
                s[i] = BigInteger.valueOf(tempS);
            } else {
                i--;
            }
        }

        for (int i = 0; i < k; i++) {
            tempV = s[i].pow(2);
            tempV = inverse(tempV, n);
            v[i] = (tempV.multiply(BigInteger.valueOf((long) Math.pow((-1), b[i])))).mod(n);
        }

        StringBuilder resultParameters = new StringBuilder();
        for (int i = 0; i < k; i++) {
            resultParameters.append(v[i].toString()).append('\n');
        }

        Utils.writeToFile(new File("openParametersSideA"), resultParameters.toString());
    }


    public BigInteger inverse(BigInteger a, BigInteger b) {
        BigInteger x = a, y = b;
        BigInteger[] qRemainder;
        BigInteger[] result = new BigInteger[3];
        BigInteger x0 = BigInteger.ONE, x1 = BigInteger.ZERO;
        BigInteger y0 = BigInteger.ZERO, y1 = BigInteger.ONE;
        while (true) {
            qRemainder = x.divideAndRemainder(y);
            x = qRemainder[1];
            x0 = x0.subtract(y0.multiply(qRemainder[0]));
            x1 = x1.subtract(y1.multiply(qRemainder[0]));
            if (x.equals(BigInteger.ZERO)) {
                result[0] = y;
                result[1] = y0;
                result[2] = y1;
                return result[1];
            }
            qRemainder = y.divideAndRemainder(x);
            y = qRemainder[1];
            y0 = y0.subtract(x0.multiply(qRemainder[0]));
            y1 = y1.subtract(x1.multiply(qRemainder[0]));
            if (y.equals(BigInteger.ZERO)) {
                result[0] = x;
                result[1] = x0;
                result[2] = x1;
                return result[1];
            }
        }
    }
}
