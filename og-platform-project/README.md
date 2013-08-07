OpenGamma Platform Project Parent
---------------------------------
This is the parent pom for OpenGamma client integrations.

It is intended to inherit maven version numbers from the corporate parent to lock down the specific version of the platform to be used as well as house the standard boilerplate configs such as:
* The version of corporate-parent
* resources processing configs
* jar plugin
* surefire plugin
* checkstyle plugin
* script generation
* web unpacks
* assembly plugin and the assembly-server.xml
* dependecies
* profiles and execution configs
* basic properties such as og.version

This allows the actual client integration projects to start off as little more than a pom, properties and ini file. Updating the version of the parent pom allows for easy upgrade/downgrade. It also clarifies what is local customization to a particular integration project so it does not become lost in the boilerplate. 

NOTE: This project is publicly available.

[![OpenGamma](http://developers.opengamma.com/res/display/default/chrome/masthead_logo.png "OpenGamma")](http://developers.opengamma.com)
