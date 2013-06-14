/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.maven.scripts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opengamma.util.ClasspathUtils;
import com.opengamma.util.annotation.ClassNameAnnotationScannerUtils;
import com.opengamma.util.generate.scripts.Scriptable;
import com.opengamma.util.generate.scripts.ScriptsGenerator;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Generates scripts from a template based on annotated classes.
 * 
 * @goal generate-scripts
 * @phase prepare-package
 * @requiresDependencyResolution compile
 * @threadSafe
 * @description 
 */
public class ScriptableScriptGeneratorMojo extends AbstractMojo {

  /**
   * Set to true to skip all processing, default false.
   * @parameter alias="skip"
   */
  private boolean _skip;
  /**
   * Where the scripts should be generated.
   * @parameter alias="outputDir" default-value="${project.build.directory}/scripts"
   * @required
   */
  private File _outputDir;
  /**
   * True to generate unix scripts, default true.
   * @parameter alias="unix" default-value="true"
   */
  private boolean _unix;
  /**
   * The basic template file name on Unix.
   * This is used as the default template file name.
   * It effectively maps to 'java.lang.Object' in the unixTemplatesMap.
   * The default is 'com/opengamma/scripts/templates/script-template-unix.ftl'
   * which should be on the classpath.
   * @parameter alias="unixTemplate" default-value="com/opengamma/scripts/templates/script-template-unix.ftl"
   */
  private String _unixTemplate;
  /**
   * A map of class name to template file name on Unix.
   * @parameter alias="baseClassTemplateMap"
   */
  private Map<String, String> _unixTemplatesMap;
  /**
   * True to generate windows scripts, default true.
   * @parameter alias="windows" default-value="true"
   */
  private boolean _windows;
  /**
   * The basic template file name on Windows.
   * This is used as the default template file name.
   * It effectively maps to 'java.lang.Object' in the windowsTemplatesMap.
   * The default is 'com/opengamma/scripts/templates/script-template-windows.ftl'
   * which should be on the classpath.
   * @parameter alias="windowsTemplate" default-value="com/opengamma/scripts/templates/script-template-windows.ftl"
   */
  private String _windowsTemplate;
  /**
   * A map of class name to template file name on Windows.
   * @parameter alias="baseClassTemplateMap"
   */
  private Map<String, String> _windowsTemplatesMap;
  /**
   * Additional scripts to copy unchanged.
   * @parameter alias="additionalScripts"
   */
  private String[] _additionalScripts;
  /**
   * The project base directory.
   * @parameter default-value="${project.basedir}"
   * @required
   */
  private File _baseDir;
  /**
   * @parameter default-value="${project}"
   * @required
   */
  private MavenProject _project;

  //-------------------------------------------------------------------------
  /**
   * Maven plugin for generating scripts to run tools annotated with {@link Scriptable}.
   * <p>
   * Variables available in the Freemarker template are:
   * <ul>
   * <li> className - the fully-qualified Java class name
   * </ul>
   */
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (_skip) {
      return;
    }
    
    // make the output directory
    try {
      FileUtils.forceMkdir(_outputDir);
    } catch (Exception ex) {
      throw new MojoExecutionException("Error creating output directory " + _outputDir.getAbsolutePath(), ex);
    }
    
    // resolve the input
    Map<String, String> unix = buildInputMap(_unixTemplate, _unixTemplatesMap);
    Map<String, String> windows = buildInputMap(_windowsTemplate, _windowsTemplatesMap);
    URL[] classpathUrls = buildProjectClasspath(_project);
    ClassLoader classLoader = new URLClassLoader(classpathUrls, this.getClass().getClassLoader());
    
    // build the scripts
    if (unix.isEmpty() == false || windows.isEmpty() == false) {
      List<Class<?>> scriptableClasses = buildScriptableClasses(classpathUrls, classLoader);
      getLog().info("Generating " + scriptableClasses.size() + " scripts");
      if (_unix) {
        generateScripts(unix, scriptableClasses, classLoader, false);
      }
      if (_windows) {
        generateScripts(windows, scriptableClasses, classLoader, true);
      }
    }
    copyAdditionalScripts();
  }

  //-------------------------------------------------------------------------
  // merges the input template and templateMap
  private static Map<String, String> buildInputMap(String template, Map<String, String> tempateMap) throws MojoExecutionException {
    Map<String, String> result = Maps.newHashMap();
    if (tempateMap != null) {
      result.putAll(tempateMap);
    }
    if (template != null) {
      if (result.containsKey("java.lang.Object")) {
        throw new MojoExecutionException("Cannot specify template if templateMap contains key 'java.lang.Object'");
      }
      result.put("java.lang.Object", template);
    }
    return result;
  }

  //-------------------------------------------------------------------------
  // extracts the project classpath from Maven
  @SuppressWarnings("unchecked")
  private static URL[] buildProjectClasspath(MavenProject project) throws MojoExecutionException {
    List<String> classpathElementList;
    try {
      classpathElementList = project.getCompileClasspathElements();
    } catch (DependencyResolutionRequiredException ex) {
      throw new MojoExecutionException("Error obtaining dependencies", ex);
    }
    return ClasspathUtils.getClasspathURLs(classpathElementList);
  }

  //-------------------------------------------------------------------------
  // scans for the Scriptable annotation
  private static List<Class<?>> buildScriptableClasses(URL[] classpathUrls, ClassLoader classLoader) throws MojoExecutionException {
    Set<String> scriptableClassNames = ClassNameAnnotationScannerUtils.scan(classpathUrls, Scriptable.class.getName());
    List<Class<?>> result = Lists.newArrayList();
    for (String scriptable : scriptableClassNames) {
      result.add(resolveClass(scriptable, classLoader));
    }
    return result;
  }

  //-------------------------------------------------------------------------
  // generates the scripts
  private void generateScripts(Map<String, String> templates, List<Class<?>> scriptableClasses, ClassLoader classLoader, boolean windows) throws MojoExecutionException {
    if (templates.isEmpty()) {
      return;
    }
    Map<Class<?>, Template> templateMap = resolveTemplateMap(templates, classLoader);
    for (Class<?> scriptableClass : scriptableClasses) {
      Map<String, Object> templateData = new HashMap<String, Object>();
      templateData.put("className", scriptableClass);
      templateData.put("project", _project.getArtifactId());
      Template template = lookupTempate(scriptableClass, templateMap);
      ScriptsGenerator.generate(scriptableClass.getName(), _outputDir, template, templateData, windows);
    }
  }

  //-------------------------------------------------------------------------
  // resolves the template names to templates
  private Map<Class<?>, Template> resolveTemplateMap(Map<String, String> templates, ClassLoader classLoader) throws MojoExecutionException {
    Map<Class<?>, Template> templateMap = Maps.newHashMap();
    for (Map.Entry<String, String> unresolvedEntry : templates.entrySet()) {
      String className = unresolvedEntry.getKey();
      Class<?> clazz = resolveClass(className, classLoader);
      Template template = getTemplate(unresolvedEntry.getValue(), classLoader);
      templateMap.put(clazz, template);
    }
    return templateMap;
  }

  //-------------------------------------------------------------------------
  // load a template
  private Template getTemplate(String templateName, ClassLoader classLoader) throws MojoExecutionException {
    try {
      return getTemplateFromFile(_baseDir, templateName);
    } catch (Exception ex) {
      try (InputStream resourceStream = classLoader.getResourceAsStream(templateName) ) {
        if (resourceStream == null) {
          throw new MojoExecutionException("Freemarker Template not found as a local file or resource: " + templateName);
        }
        String templateStr = IOUtils.toString(resourceStream);
        Configuration cfg = new Configuration();
        cfg.setDefaultEncoding("UTF-8");
        return new Template(templateName, new StringReader(templateStr), cfg);
      } catch (IOException ex2) {
        throw new MojoExecutionException("Error loading Freemarker template: " + templateName, ex2);
      }
    }
  }

  //-------------------------------------------------------------------------
  // loads a template from file
  private static Template getTemplateFromFile(File baseDir, String templateFile) throws IOException {
    Configuration cfg = new Configuration();
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateLoader(new FileTemplateLoader(baseDir));
    return cfg.getTemplate(templateFile);
  }

  //-------------------------------------------------------------------------
  // lookup and find the best matching template
  private static Template lookupTempate(Class<?> scriptableClass, Map<Class<?>, Template> templateMap) throws MojoExecutionException {
    Class<?> clazz = scriptableClass;
    while (clazz != null) {
      Template template = templateMap.get(clazz);
      if (template != null) {
        return template;
      }
      clazz = clazz.getSuperclass();
    }
    throw new MojoExecutionException("No template found: " + scriptableClass);
  }

  //-------------------------------------------------------------------------
  // copies any additional scripts
  private void copyAdditionalScripts() throws MojoExecutionException {
    if (_additionalScripts == null || _additionalScripts.length == 0) {
      return;
    }
    getLog().info("Copying " + _additionalScripts.length + " additional script(s)");
    for (String script : _additionalScripts) {
      File scriptFile = new File(script);
      if (scriptFile.exists() == false) {
        throw new MojoExecutionException("Additional script cannot be found: " + scriptFile);
      }
      if (scriptFile.isFile() == false) {
        throw new MojoExecutionException("Additional script is not a file, directories cannot be copied: " + scriptFile);
      }
      try {
        FileUtils.copyFileToDirectory(scriptFile, _outputDir);
        File copiedFile = new File(_outputDir, scriptFile.getName());
        copiedFile.setReadable(true, false);
        copiedFile.setExecutable(true, false);
      } catch (IOException ex) {
        throw new MojoExecutionException("Unable to copy additional script: " + scriptFile, ex);
      }
    }
  }

  //-------------------------------------------------------------------------
  // loads a class
  private static Class<?> resolveClass(String className, ClassLoader classLoader) throws MojoExecutionException {
    try {
      return classLoader.loadClass(className);
    } catch (ClassNotFoundException e) {
      throw new MojoExecutionException("Unable to resolve class " + className);
    }
  }

}
