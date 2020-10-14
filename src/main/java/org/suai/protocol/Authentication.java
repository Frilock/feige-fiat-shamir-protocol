package org.suai.protocol;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.logging.Logger;

import static java.lang.System.out;

public class Authentication {
    private static final Logger logger = Logger.getLogger(Authentication.class.getName());
    private int tempCount = 0;

    /**
     * Участник идентифицирует себя окружающим с помощью значений (v_1, v_2, ..., v_k; n),
     * которые выступают в качестве его открытого ключа,
     * в то время как секретный ключ s = (s_1, s_2, ..., s_k) известен только самому участнику.
     */
    public boolean authentication(int t, BigInteger n, int k) {
        Alice alice = new Alice(n, k);
        Bob bob = new Bob(n, k);

        for (int i = 0; i < t; i++) {
            alice.generator(); // А генерирует параметры
            BigInteger r = alice.generateR(); //А выбирает r
            out.println("r: " + r);
            int b = alice.generateB(); // А выбирает b
            BigInteger x = alice.calculateX(b, r); //А вычисляет Х и отправляет Б
            int[] e = bob.generateVectorE(); // Б генерирует вектор е и отправляет А
            BigInteger y = alice.generateY(e, r); // r * П по s
            boolean flag = bob.generateZ(y, x); // y^2 П
            if (!flag) {
                return false;
            }
        }
        return true;
    }

    public boolean falseAuthentication(BigInteger n, int k) {
        Alice alice = new Alice(n, k);
        alice.generator();
        Bob bob = new Bob(n, k);
        bob.generateVectorE();
        Eva eva = new Eva(n, k);

        out.println("Был использован вектор е: " + Arrays.toString(bob.getE()) + " - попробуем его угадать.");
        int count = 0;
        while (true) {
            BigInteger[] x = eva.getX();
            BigInteger y = eva.getR();
            if (bob.checkZ(y, x)) {
                return true;
            }
            if (count == Math.pow(2, k) - 1) {
                tempCount++;
                falseAuthentication(n, k);
                logger.info(() -> "Превышение количества попыток для компроментации: " + tempCount);
                return true;
            }
            if (tempCount == 10) {
                return false;
            }
            count++;
        }
    }
}