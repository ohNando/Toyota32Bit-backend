package com.toyotabackend.mainplatform.ClassLoader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class LoadJarFile {
    public static void loadJarFile(String jarPath, String className) throws Exception {
        File jarFile = new File(jarPath);
        URL[] urls = {jarFile.toURI().toURL()};
        URLClassLoader ucl = new URLClassLoader(urls,LoadJarFile.class.getClassLoader());
        Class<?> loadedClass = ucl.loadClass(className);
        loadedClass.getMethod("main",String[].class).invoke(null   ,(Object) new String[]{});
    }
}
