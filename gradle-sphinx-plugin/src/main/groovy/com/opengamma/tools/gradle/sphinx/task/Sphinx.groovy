package com.opengamma.tools.gradle.sphinx.task
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class Sphinx extends DefaultTask
{
	/**
	 * The index file to use for this doc set
	 */
	@Input
	File index

	/**
	 * The name of the archive that the User Guide should be packaged into
	 */
	@Input
	String userGuideArchiveName

	/**
	 * OPTIONAL
	 * Whether or not warnings from the Sphinx compiler should fail the build
	 *
	 * Defaults to true
	 */
	@Input @Optional
	boolean ignoreSphinxWarnings = true

	/**
	 * OPTIONAL
	 * The include spec to pass to the Copy task for staging the docs source
	 *
	 * Defaults to include("**&#47;docs/**")
	 */
	@Input @Optional
	Closure includes = {
		include("**/docs/**")
	}

	/**
	 * OPTIONAL
	 * The required Python packages for building the User Guide.
	 *
	 * Defaults to the OpenGamma Sphinx/RST stack.
	 */
	@Input @Optional
	Set<String> requiredPackages =  [
			"numpy", "numpydoc", "rst2pdf", "pygments", "sphinx", "sphinx-rtd-theme"
	].toSet()
}
