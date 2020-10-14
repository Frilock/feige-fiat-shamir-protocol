package org.suai.protocol;

import lombok.Getter;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;

import static java.lang.System.out;

public class Bob extends TrustCenter {
    private final int k;
    private final BigInteger n;
    @Getter
    private int[] e;

    Bob(BigInteger n, int k) {
        this.k = k;
        this.n = n;
    }

    int[] generateVectorE() {
        double temp;
        e = new int[k];
        for (int i = 0; i < k; i++) {
            temp = Math.random();
            if (temp >= 0.5) {
                e[i] = 1;
            } else {
                e[i] = 0;
            }
        }
        return e;
    }

    boolean generateZ(BigInteger y, BigInteger x) {
        BigInteger[] v = new BigInteger[k];
        BigInteger z = BigInteger.ONE;

        String inputFile = "openParametersSideA";
        File file = new File(inputFile);
        byte[] bytes = Utils.readFile(file);
        String[] parameters = new String(bytes).split("\n");

        for (int i = 0; i < k; i++) {
            v[i] = new BigInteger(parameters[i]);
        }

        for (int i = 0; i < k; i++) {
            z = z.multiply(v[i].pow(e[i]));
        }
        z = y.pow(2).multiply(z).mod(n);

        out.println("y: " + y);
        out.println("z: " + z);
        out.println("x: " + x);
        out.println("-x: " + x.negate().mod(n));
        out.println("e: " + Arrays.toString(e));

        return z.compareTo(x) == 0 || z.compareTo(x.negate().mod(n)) == 0;
    }

    public boolean checkZ(BigInteger y, BigInteger[] x) {
        BigInteger[] v = new BigInteger[k];
        String inputFile = "openParametersSideA";
        File file = new File(inputFile);
        byte[] bytes = Utils.readFile(file);
        String[] parameters = new String(bytes).split("\n");

        for (int i = 0; i < k; i++) {
            v[i] = new BigInteger(parameters[i]);
        }

        BigInteger temp = BigInteger.ONE;
        for (int i = 0; i < k; i++) {
            temp = temp.multiply(v[i].pow(e[i]));
        }
        y = y.pow(2).mod(n);
        BigInteger z = y.multiply(temp).mod(n);

        out.println("z: " + z);
        out.println("x: " + x[0]);
        out.println("-x: " + x[1]);
        out.println();

        return z.compareTo(x[0]) == 0 || z.compareTo(x[1]) == 0;
    }
}
