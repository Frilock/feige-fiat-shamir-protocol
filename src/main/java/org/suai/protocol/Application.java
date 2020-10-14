package org.suai.protocol;


import java.math.BigInteger;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Сторона A доказывает своё знание секрета s стороне B в течение t раундов,
 * не раскрывая при этом ни одного бита самого секрета
 */
public class Application {
    private static final Logger logger = Logger.getLogger(Application.class.getName());
    private static final TrustCenter trustCenter = new TrustCenter();
    private static final Authentication identification = new Authentication();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        logger.info("Enter the number of rounds: ");
        int t = scanner.nextInt();

        logger.info("Enter the number k: ");
        int k = scanner.nextInt();

        authentication(t, k);
        falseAuthentication(k);
    }

    private static void authentication(int t, int k) {
        BigInteger n = trustCenter.generateN();
        logger.log(Level.INFO,() -> "n: " + n);

        if (identification.authentication(t, n, k)){
            logger.info("authentication successful");
        } else {
            logger.info("authentication not successful");
        }
    }

    private static void falseAuthentication(int k) {
        BigInteger n = trustCenter.generateN();
        logger.log(Level.INFO,() -> "n: " + n);

        if (identification.falseAuthentication(n, k)) {
            logger.info("authentication successful");
        } else {
            logger.info("authentication not successful");
        }
    }

}
