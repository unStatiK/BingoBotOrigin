package com.bingo.component

import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import java.util.jar.JarEntry
import java.util.jar.JarFile

@CompileStatic
@Component
class PluginLoader {
    public List<Class> loadClasses(String directory, String classpath) {
        List classes = []
        File pluginsDir = new File(directory)
        for (File jar : pluginsDir.listFiles()) {
            ClassLoader loader = URLClassLoader.newInstance(
                    [jar.toURI().toURL()] as URL[],
                    getClass().getClassLoader()
            )

            classes.addAll(findClasses(jar, classpath))
        }
        return classes
    }

    private static List<Class> findClasses(File pluginFile, String packageName) throws ClassNotFoundException {
        List<Class> foundedClasses = []
        if (pluginFile.isFile() && pluginFile.getName().endsWith(".jar")) {
            JarFile jarFile = new JarFile(pluginFile)
            Enumeration<JarEntry> e = jarFile.entries()
            URL[] urls = [pluginFile.toURI().toURL()] as URL[]
            URLClassLoader cl = URLClassLoader.newInstance(urls)

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement()
                if (je.isDirectory() || !je.getName().endsWith(".class") || je.getName().indexOf('$') != -1) {
                    continue
                }
                // -6 because of .class
                String className = je.getName().substring(0, je.getName().length() - 6)
                className = className.replace('/', '.')
                Class c = cl.loadClass(className)
                foundedClasses << c
            }
        }
        foundedClasses
    }

}
