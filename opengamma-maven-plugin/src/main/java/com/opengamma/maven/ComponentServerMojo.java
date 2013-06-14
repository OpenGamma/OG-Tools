/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Runs an OpenGamma component server.
 * 
 * @ goal component-server
 * @ requiresDependencyResolution runtime
 * @ description Runs a component server.
 */
public class ComponentServerMojo extends AbstractMojo {

  /**
   * @parameter default-value="${project}"
   * @required
   */
  private MavenProject _project;

  //-------------------------------------------------------------------------
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("TODO: Run server: " + _project.getArtifactId());
  }

}
