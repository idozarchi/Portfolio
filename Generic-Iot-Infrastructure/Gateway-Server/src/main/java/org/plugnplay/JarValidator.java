package org.plugnplay;

import java.io.IOException;
import java.util.jar.JarFile;

public class JarValidator {
    public static boolean validate(String filePath) {
        return filePath.endsWith(".jar");
    }
}