package com.bingo.component

import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

@CompileStatic
@Component
class PluginLoader {
    public List<Class> loadClasses(String directory, String classpath) throws ClassNotFoundException {
        List classes = []
        File pluginsDir = new File(directory)
        for (File jar : pluginsDir.listFiles()) {
            try {
                ClassLoader loader = URLClassLoader.newInstance(
                        [jar.toURI().toURL()] as URL[],
                        getClass().getClassLoader()
                )
                Class<?> clazz = Class.forName(classpath, true, loader)
                // Apparently its bad to use Class.newInstance, so we use
                // newClass.getConstructor() instead
                Constructor constructor = clazz.getConstructor()
                classes.add(constructor.newInstance())

            } catch (ClassNotFoundException e) {
                // There might be multiple JARs in the directory,
                // so keep looking
                continue;
            } catch (MalformedURLException e) {
                e.printStackTrace()
            } catch (NoSuchMethodException e) {
                e.printStackTrace()
            } catch (InvocationTargetException e) {
                e.printStackTrace()
            } catch (IllegalAccessException e) {
                e.printStackTrace()
            } catch (InstantiationException e) {
                e.printStackTrace()
            }
            return classes
        }

//        throw new ClassNotFoundException("Class " + classpath
//                + " wasn't found in directory " + directory)
    }
}
