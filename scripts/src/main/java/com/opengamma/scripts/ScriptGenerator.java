/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.scripts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Generator that can produce command line scripts.
 * <p>
 * Scripts are normally identified based on the {@link Scriptable} annotation.
 * <p>
 * Current scripts use the following template data entries:
 * <ul>
 * <li>className
 * <li>projectName
 * <li>projectJar
 * </ul>
 */
public class ScriptGenerator {

  /**
   * Template key for the class name of the class linked to the script.
   */
  public static final String TEMPLATE_CLASS_NAME = "className";
  /**
   * Template key for the project name.
   */
  public static final String TEMPLATE_PROJECT_NAME = "projectName";
  /**
   * Template key for the project jar file.
   */
  public static final String TEMPLATE_PROJECT_JAR = "projectJar";

  /**
   * Generates the scripts.
   * 
   * @param className  the class name, not null
   * @param projectName  the project name, not null
   * @param scriptDir  the output directory to put the script in, not null
   * @param template  the Freemarker template, not null
   * @param windows  true for Windows, false for Unix
   */
  public static void generate(String className, String projectName, File scriptDir, Template template, boolean windows) {
    Map<String, Object> templateData = new HashMap<>();
    templateData.put(TEMPLATE_CLASS_NAME, className);
    templateData.put(TEMPLATE_PROJECT_NAME, projectName.toLowerCase());
    generate(className, scriptDir, template, templateData, windows);
  }

  /**
   * Generates the scripts.
   * 
   * @param className  the class name, not null
   * @param scriptDir  the output directory to put the script in, not null
   * @param template  the Freemarker template, not null
   * @param templateData  the lookup data injected into the template, not null
   * @param windows  true for Windows, false for Unix
   */
  public static void generate(String className, File scriptDir, Template template, Object templateData, boolean windows) {
    String scriptName = scriptName(className);
    File outputFile;
    if (windows) {
      outputFile = new File(scriptDir, scriptName + ".bat");
    } else {
      outputFile = new File(scriptDir, scriptName + ".sh");
    }
    generate(outputFile, template, templateData);
  }

  /**
   * Calculates the script name.
   * 
   * @param className  the class name, not null
   * @return the script name, not null
   */
  private static String scriptName(String className) {
    className = StringUtils.substringAfterLast(className, ".");  // strip package name
    String[] split = StringUtils.splitByCharacterTypeCamelCase(className);
    return StringUtils.join(split, '-').toLowerCase(Locale.ENGLISH);
  }

  /**
   * Generates and writes the script using the Freemarker template.
   * <p>
   * This is the low-level call to Freemarker.
   * 
   * @param outputFile  the file to write to, not null
   * @param template  the Freemarker template, not null
   * @param templateData  the lookup data injected into the template, not null
   */
  public static void generate(File outputFile, Template template, Object templateData) {
    try {
      PrintWriter writer = new PrintWriter(outputFile);
      template.process(templateData, writer);
      writer.flush();
      writer.close();
      outputFile.setReadable(true, false);
      outputFile.setExecutable(true, false);
    } catch (TemplateException | IOException ex) {
      throw new RuntimeException("Error writing to output file", ex);
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Loads a template either from a file, or from the classpath.
   * 
   * @param baseDir  the base directory for loading the file, not null
   * @param templateFile  the template file, typically a relative path, not null
   * @param classLoader  the class loader for resource loading, not null
   * @return the loaded template, not null
   * @throws RuntimeException if unable to load the template
   */
  public static Template loadTemplate(File baseDir, String templateFile, ClassLoader classLoader) {
    try {
      Configuration cfg = new Configuration();
      cfg.setDefaultEncoding("UTF-8");
      cfg.setTemplateLoader(new FileTemplateLoader(baseDir));
      return cfg.getTemplate(templateFile);
      
    } catch (Exception ex) {
      try (InputStream resourceStream = classLoader.getResourceAsStream(templateFile)) {
        if (resourceStream == null) {
          throw new RuntimeException("Freemarker Template not found as a local file or resource: " + templateFile);
        }
        String templateStr = IOUtils.toString(resourceStream);
        Configuration cfg = new Configuration();
        cfg.setDefaultEncoding("UTF-8");
        return new Template(templateFile, new StringReader(templateStr), cfg);
      } catch (IOException ex2) {
        throw new RuntimeException("Error loading Freemarker template: " + templateFile, ex2);
      }
    }
  }

}
