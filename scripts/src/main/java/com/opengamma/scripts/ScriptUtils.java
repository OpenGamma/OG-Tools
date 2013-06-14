/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.scripts;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.scannotation.AnnotationDB;

/**
 * Utilities for managing scripts.
 */
public class ScriptUtils {

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
   * @return the matching class names, not null
   */
  public static Set<String> findClassAnnotation(URL[] classpathUrls, Class<? extends Annotation> annotationClass) {
    Validate.notNull(annotationClass, "annotationClass");
    AnnotationDB annotationDb = getAnnotationDb(classpathUrls);
    Set<String> classNames = annotationDb.getAnnotationIndex().get(annotationClass.getName());
    if (classNames == null) {
      return Collections.emptySet();
    }
    return Collections.unmodifiableSet(classNames);
  }

  /**
   * Gets the annotation database for the specific classpath.
   * 
   * @param classpathUrlArray  the classpath URLs, not null
   * @return the annotation database, not null
   */
  private static AnnotationDB getAnnotationDb(URL[] classpathUrlArray) {
    AnnotationDB annotationDb = new AnnotationDB();
    annotationDb.setScanClassAnnotations(true);
    annotationDb.setScanMethodAnnotations(false);
    annotationDb.setScanFieldAnnotations(false);
    annotationDb.setScanParameterAnnotations(false);
    try {
      annotationDb.scanArchives(classpathUrlArray);
    } catch (IOException ex) {
      throw new RuntimeException("Error scanning for annotations", ex);
    }
    return annotationDb;
  }

}
