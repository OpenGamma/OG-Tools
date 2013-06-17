/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.maven;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.IOException;
import java.io.StringReader;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

//CSOFF for Javadoc tags
/**
 * Runs an OpenGamma component server.
 * 
 * @goal server-stop
 * @requiresDependencyResolution compile+runtime
 */
// CSON
public class ServerStopMojo extends AbstractMojo {

  /**
   * @parameter default-value="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;  // CSIGNORE
  /**
   * The current Maven session.
   *
   * @parameter default-value="${session}"
   * @required
   * @readonly
   */
  private MavenSession mavenSession;  // CSIGNORE
  /**
   * The Maven BuildPluginManager component.
   *
   * @component
   * @required
   */
  private BuildPluginManager mavenPluginManager;  // CSIGNORE

  //-------------------------------------------------------------------------
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    // build the configuration for ant
    // parse XML manually, as per https://github.com/TimMoore/mojo-executor/issues/10
    String fullVmArgs = "-Dcommandmonitor.secret=OpenGammaMojo ";
    String cp = MojoUtils.calculateRuntimeClasspath(project);
    String taskStr = 
        "<configuration>" +
          "<target>" +
            "<java classpath='" + cp + "' classname='com.opengamma.component.OpenGammaComponentServerMonitor' fork='true' outputproperty='jetty.stop.output'>" +
              "<jvmarg line='" + fullVmArgs + "' />" +
              "<arg value='exit' />" +
            "</java>" +
            "<echo>${jetty.stop.output}</echo>" +
          "</target>" +
        "</configuration>";
    Xpp3Dom config;
    try {
      config = Xpp3DomBuilder.build(new StringReader(taskStr));
    } catch (XmlPullParserException | IOException ex) {
      throw new MojoExecutionException("Unable to parse XML configuration: ", ex);
    }
    
    // run the java process using ant
    // uses antrun, as exec-maven-plugin lacks features, eg http://jira.codehaus.org/browse/MEXEC-113
    executeMojo(
      plugin(
        groupId("org.apache.maven.plugins"),
        artifactId("maven-antrun-plugin"),
        version("1.7")
      ),
      goal("run"),
      config,
      executionEnvironment(
        project,
        mavenSession,
        mavenPluginManager
      )
    );
  }

}
