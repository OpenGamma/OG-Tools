package com.opengamma.tools.gradle.sphinx.task
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.bundling.Zip

class Sphinx extends DefaultTask
{
	public static final String INCLUDE_ALL_DOCS = "**/docs/**"

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
	List<String> includes = null

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

	/**
	 * The bundle configuration that will package this documentation set
	 */
	Zip archive

	public void include(String include)
	{
		if( ! this.@includes) includes = []
		this.@includes << include
	}

	public List<String> getIncludes()
	{
		return this.@includes ?: [ INCLUDE_ALL_DOCS ]
	}
}
