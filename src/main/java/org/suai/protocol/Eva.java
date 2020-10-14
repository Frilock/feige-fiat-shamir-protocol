package org.suai.protocol;

import lombok.Getter;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static java.lang.System.out;

public class Eva extends TrustCenter {
    private final Random random;
    private final BigInteger n;
    private final BigInteger[] v;
    private final int k;
    private final ArrayList<int[]> e;
    @Getter
    private BigInteger r;
    private int count = 0;

    Eva(BigInteger n, int k) {
        this.n = n;
        this.k = k;
        this.v = new BigInteger[k];
        this.e = getE();
        this.random = new Random();

        String inputFile = "openParametersSideA";
        File file = new File(inputFile);
        byte[] bytes = Utils.readFile(file);
        String[] parameters = new String(bytes).split("\n");

        for (int i = 0; i < k; i++) {
            v[i] = new BigInteger(parameters[i]);
        }
    }

    public BigInteger[] getX() {
        r = new BigInteger(128, random).mod(n).abs();
        BigInteger[] arrayX = new BigInteger[2];

        int[] tryE = e.get(count);
        out.println("На данной итерации выбрали вектор е: " + Arrays.toString(tryE));
        BigInteger temp = v[0].pow(tryE[0]);

        for (int i = 1; i < k; i++) {
            temp = temp.multiply(v[i]).pow(tryE[i]);
        }
        count++;

        BigInteger x1 = r.pow(2).multiply(temp).mod(n);
        BigInteger x2 = x1.negate().mod(n);

        arrayX[0] = x1;
        arrayX[1] = x2;
        return arrayX;
    }

    private ArrayList<int[]> getE() {
        ArrayList<int[]> list = new ArrayList<>();
        for (int i = 0; i < Math.pow(2, k); i++) {
            list.add(generateArray(i));
        }
        return list;
    }

    private int[] generateArray(int i) {
        int j = 0;
        int[] mas = new int[k];
        while (i != 0) {
            if (i % 2 == 1) {
                mas[j] = 1;
            } else {
                mas[j] = 0;
            }
            j++;
            i /= 2;
        }
        for (int z = j; z < k; z++)
            mas[z] = 0;
        return mas;
    }
}
