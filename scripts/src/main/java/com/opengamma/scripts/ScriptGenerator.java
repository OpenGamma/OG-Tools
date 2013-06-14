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
 */
public class ScriptGenerator {

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
    templateData.put("className", className);
    templateData.put("project", projectName.toLowerCase());
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
    writeScriptFile(outputFile, template, templateData);
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
   * Writes the script using the Freemarker template.
   * 
   * @param outputFile  the file to write to, not null
   * @param template  the Freemarker template, not null
   * @param templateData  the lookup data injected into the template, not null
   */
  private static void writeScriptFile(File outputFile, Template template, Object templateData) {
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
