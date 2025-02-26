package org.plugnplay;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DynamicJarLoader {
    private String implementedInferface;

    public DynamicJarLoader(String implementedInferface) {
        this.implementedInferface = implementedInferface;
    }

    public List<Class<?>> load(String classPath) {
        List<Class<?>> loadedClasses = new ArrayList<>();
        JarFile jar;
        try {
            jar = new JarFile(classPath);
        }catch (IOException e) {
            throw new RuntimeException();
        }

        URL pathToFolder;
        try {
            pathToFolder = new URL("jar:file:" + classPath + "!/");
        }catch (IOException e) {
            throw new RuntimeException();
        }
        ClassLoader loader = new URLClassLoader(new URL[] {pathToFolder});

        Enumeration<JarEntry> currEntry = jar.entries();

        while(currEntry.hasMoreElements()){
            JarEntry entry = currEntry.nextElement();
            if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                continue;
            }

            System.out.println(entry.getName());
            String className = entry.getName().replace('/', '.').substring(0, entry.getName().length()-6);//TODO change to method

            Class<?> c;
            try {
                c = loader.loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException();
            }
            if(validateCommand(c)){
                loadedClasses.add(c);
            }
        }

        return loadedClasses;
    }

    private boolean validateCommand(Class<?> c) {
        Class<?>[] interfaces = c.getInterfaces();

        for (Class<?> i : interfaces) {
            if (i.getName().equals(this.implementedInferface)) {
                return true;
            }
        }
        return false;
    }
}
