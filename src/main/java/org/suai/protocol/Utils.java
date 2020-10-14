package org.suai.protocol;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

public class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class.getName());

    Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static byte[] readFile(File file) {
        byte[] data = null;
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return data;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void writeToFile(File file, String data) {
        if (file.exists()) {
            file.delete();
        }
        try {
            Files.write(file.toPath(), data.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }
}
