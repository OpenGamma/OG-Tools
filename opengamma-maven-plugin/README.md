
OpenGamma plugin for Apache Maven
---------------------------------

This project provides a [Maven](https://maven.apache.org/) plugin
for [OpenGamma](http://developers.opengamma.com/).


#### generate-scripts

Phase: prepare-package

Properties:
- skip
 - Set to true to skip all processing, default false
 - Property = "opengamma.generate.scripts.skip"
- outputDir
 - Where the scripts should be generated, default ${project.build.directory}/scripts
- type
 - The type to generate.
   This is a shortcut, allowing all the files stored in the scripts project
   to be accessed without needing to specify lots of config everywhere.
   The only recognized value at present is 'tool'.
   If this is set, then the unixTemplate and windowsTemplate fields will be
   set, and a standard set of additional scripts added.
   Use the 'unix' and 'windows' boolean flags to control which is output.
 - Property = "opengamma.generate.scripts.type"
- unix
 - Set to false to turn off generation of Unix scripts, default true.
 - Property = "opengamma.generate.scripts.unix"
- unixTemplate
 - The basic template file name on Unix.
   This is used as the default template file name.
- windows
 - Set to false to turn off generation of Windows scripts, default true.
 - Property = "opengamma.generate.scripts.windows"
- windowsTemplate
 - The basic template file name on Windows.
   This is used as the default template file name.
- additionalScripts
 - List of additional scripts to copy unchanged.
- zip
 - Set to false to turn off creation of an attached zip file, default false.
 - Property = "opengamma.generate.scripts.zip"


#### server-run / server-start

These start and stop an OpenGamma component server.
They are intended to be run directly from the command line.

The `server-run` goal will start the server inline so it can be killed by Ctrl+C.

The `server-start` goal will start the server in the background where it must be killed by `server-stop`.

Properties:
- configFile
 - The component server properties or INI file.
   The file or classpath prefix is optional, as it will try both.
 - Command line property = "configFile"
- startupLogging
 - The level of logging during startup - ERROR, WARN, INFO or DEBUG.
   Default WARN.
 - Command line property = "startupLogging"
- serverLogging
 - The level of logging for the server in general - ERROR, WARN, INFO or DEBUG.
   Default WARN.
 - Command line property = "serverLogging"
- vmMemoryArgs
 - The memory arguments for the server.
   Default values are chosen if not set.
 - Command line property = "vmMemoryArgs"
- vmArgs
 - Any additional VM arguments for the server.
 - Command line property = "vmArgs"

Example:

```
 mvn opengamma:server-start -DconfigFile=fullstack/fullstack-example-dev.properties
```


#### server-stop

This stops a server that was started using `server-start`.

The `server-stop` goal takes no properties.

Example:

```
 mvn opengamma:server-stop
```


#### Trademarks

Maven, Apache and Apache Maven are trademarks of The Apache Software Foundation.
