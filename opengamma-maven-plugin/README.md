
OpenGamma plugin for Apache Maven
---------------------------------

This project provides a [Maven](https://maven.apache.org/) plugin
for [OpenGamma](http://developers.opengamma.com/).


#### generate-scripts

Phase: prepare-package

Properties:
- skip: Set to true to skip all processing, default false
- outputDir: Where the scripts should be generated, default ${project.build.directory}/scripts
- type: The type to generate.
 This is a shortcut, allowing all the files stored in the scripts project
 to be accessed without needing to specify lots of config everywhere.
 The only recognized value at present is 'tool'.
 If this is set, then the unixTemplate and windowsTemplate fields will be
 set, and a standard set of additional scripts added.
 Use the 'unix' and 'windows' boolean flags to control which is output.
 Property = "opengamma.generate.scripts.type"
- unix: True to generate unix scripts, default true. Used to switch Unix scripts off.
 Property = "opengamma.generate.scripts.unix"
- unixTemplate: The basic template file name on Unix.
 This is used as the default template file name.
- windows: True to generate windows scripts, default true. Used to switch Windows scripts off.
 Property = "opengamma.generate.scripts.windows"
- windowsTemplate: The basic template file name on Windows.
 This is used as the default template file name.
- additionalScripts: List of additional scripts to copy unchanged.

#### Trademarks

Maven, Apache and Apache Maven are trademarks of The Apache Software Foundation.
