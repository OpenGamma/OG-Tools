/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.maven;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.google.common.base.Joiner;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Utilities for Maven plugins.
 */
public final class MojoUtils {

  /**
   * Hidden constructor.
   */
  private MojoUtils() {
  }

  //-------------------------------------------------------------------------
  /**
   * Gets a file for the given artifact coordinates.
   * 
   * @param resourceArtifact  the artifact coordinates in the form groupId:artifactId[:type[:classifier]], not null
   * @param project  the Maven project instance, not null
   * @return the file corresponding to the given artifact coordinates, not null
   * @throws MojoExecutionException if no artifact exists with the given coordinates
   */
  @SuppressWarnings("unchecked")
  public static File getArtifactFile(String resourceArtifact, MavenProject project) throws MojoExecutionException {
    String[] artifactParts = resourceArtifact.split(":");
    if (artifactParts.length < 2 || artifactParts.length > 4) {
      throw new MojoExecutionException("resourceArtifact must be of the form groupId:artifactId[:type[:classifier]]");
    }
    String groupId = artifactParts[0];
    String artifactId = artifactParts[1];
    String type = artifactParts.length > 2 ? artifactParts[2] : null;
    String classifier = artifactParts.length > 3 ? artifactParts[3] : null;

    File artifactFile = null;
    for (Artifact artifact : (Set<Artifact>) project.getDependencyArtifacts()) {
      if (groupId.equals(artifact.getGroupId()) && artifactId.equals(artifact.getArtifactId()) &&
          (type == null || type.equals(artifact.getType())) && (classifier == null || classifier.equals(artifact.getClassifier()))) {
        artifactFile = artifact.getFile();
        break;
      }
    }
    if (artifactFile == null) {
      throw new MojoExecutionException("Unable to find artifact with coordinates '" + resourceArtifact + "'");
    }
    return artifactFile;
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the String classpath for the project.
   * 
   * @param project  the project, not null
   * @return the classpath string, not null
   */
  public static String calculateRuntimeClasspath(MavenProject project) {
    @SuppressWarnings("unchecked")
    List<Artifact> artifacts = new ArrayList<>(project.getRuntimeArtifacts());
    List<String> cpStrs = new ArrayList<>();
    cpStrs.add(new File(project.getBuild().getOutputDirectory()).getAbsolutePath());
    for (Artifact artifact : artifacts) {
      cpStrs.add(artifact.getFile().getAbsolutePath());
    }
    cpStrs.removeAll(Collections.singleton(""));
    return Joiner.on(File.pathSeparator).join(cpStrs);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the String classpath for the project.
   * 
   * @param project  the project, not null
   * @return the classpath string, not null
   */
  public static ClassLoader calculateRuntimeClassLoader(MavenProject project) {
    try {
      @SuppressWarnings("unchecked")
      List<Artifact> artifacts = new ArrayList<>(project.getRuntimeArtifacts());
      List<URL> urlList = new ArrayList<>();
      urlList.add(new File(project.getBuild().getOutputDirectory()).getAbsoluteFile().toURI().toURL());
      for (Artifact artifact : artifacts) {
        urlList.add(artifact.getFile().getAbsoluteFile().toURI().toURL());
      }
      URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
      return new URLClassLoader(urls);
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

}
