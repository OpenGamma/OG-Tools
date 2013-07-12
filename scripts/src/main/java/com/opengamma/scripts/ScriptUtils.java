/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.scripts;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * Utilities for managing scripts.
 */
public final class ScriptUtils {

  /**
   * Restricted constructor.
   */
  private ScriptUtils() {
  }

  /**
   * Obtains an array of URLs from an array of file names.
   * 
   * @param classpath  the classpath, may be null
   * @return an array of URLs, not null
   */
  public static URL[] getClasspathURLs(String[] classpath) {
    Validate.notNull(classpath, "classpath");
    Set<URL> classpathUrls = new HashSet<URL>();
    for (String classpathEntry : classpath) {
      File f = new File(classpathEntry);
      if (f.exists()) {
        try {
          classpathUrls.add(f.toURI().toURL());
        } catch (MalformedURLException ex) {
          throw new RuntimeException("Error interpreting classpath entry as URL: " + classpathEntry, ex);
        }
      }
    }
    return classpathUrls.toArray(new URL[0]);
  }

  /**
   * Obtains an array of URLs from a collection of file names.
   * 
   * @param classpath  the classpath, may be null
   * @return an array of URLs, not null
   */
  public static URL[] getClasspathURLs(Collection<String> classpath) {
    String[] classpathArray = new String[classpath.size()];
    classpathArray = classpath.toArray(classpathArray);
    return getClasspathURLs(classpathArray);
  }

  //-------------------------------------------------------------------------
  /**
   * Scans the specified classpath for an annotation, typically {@code Scriptable}.
   * 
   * @param classpathUrls  the classpath, not null
   * @param annotationClass  the annotation to find, not null
   * @param classLoader  the class loader, not null
   * @return the matching class names, not null
   */
  public static Set<Class<?>> findClassAnnotation(
      URL[] classpathUrls, Class<? extends Annotation> annotationClass, ClassLoader classLoader) {
    Validate.notNull(classpathUrls, "classpathUrls");
    Validate.notNull(annotationClass, "annotationClass");
    Validate.notNull(classLoader, "classLoader");
    Set<URL> initialUrls = new HashSet<>(Arrays.asList(classpathUrls));
    Set<URL> completeUrls = ClasspathHelper.forManifest(initialUrls);
    Configuration config = new ConfigurationBuilder()
      .setUrls(completeUrls)
      .setScanners(new TypeAnnotationsScanner())
      .addClassLoader(classLoader)
      .useParallelExecutor();
    Reflections reflections = new Reflections(config);
    return reflections.getTypesAnnotatedWith(annotationClass);
  }

}
