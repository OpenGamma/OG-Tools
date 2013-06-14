
OpenGamma Script utilities
--------------------------

This project provides tools for managing scripts.
It works in association with the opengamma-maven-plugin.

The concept is that the @Scriptable annotation is added to all suitable tool classes.
The ScriptGenerator can then be used to produce a set of script files based on templates.

This is a fairly typical configuration using the scripts i this project:

```
  <plugin>
    <groupId>com.opengamma.tools</groupId>
    <artifactId>opengamma-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
      <execution>
        <goals>
          <goal>generate-scripts</goal>
        </goals>
      </execution>
    </executions>
    <configuration>
      <outputDir>${project.build.directory}/scripts</outputDir>
      <unixTemplate>com/opengamma/scripts/templates/tool-template-unix.ftl</unixTemplate>
      <windowsTemplate>com/opengamma/scripts/templates/tool-template-unix.ftl</windowsTemplate>
      <additionalScripts>
        <param>com/opengamma/scripts/run-tool.sh</param>
        <param>com/opengamma/scripts/run-tool.bat</param>
        <param>com/opengamma/scripts/run-tool-deprecated.bat</param>
        <param>com/opengamma/scripts/java-utils.sh</param>
        <param>com/opengamma/scripts/componentserver-init-utils.sh</param>
      </additionalScripts>
    </configuration>
  </plugin>
```

[![OpenGamma](http://developers.opengamma.com/res/display/default/chrome/masthead_logo.png "OpenGamma")](http://developers.opengamma.com)
