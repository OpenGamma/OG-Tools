package com.opengamma.tools.gradle.task

import com.opengamma.tools.gradle.DistRepoExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.plugins.MavenRepositoryHandlerConvention
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Upload

class DeployLocal extends Upload
{
	@Input @Optional
	Project deployInto

	@Input @Optional
	String repoDirectoryName = "m2_dist"

	@OutputDirectory
	File deployRepo

	DeployLocal()
	{
		super()
	}


	@TaskAction
	void makeItSo()
	{
		if( ! deployInto) deployInto = project

		File deployRepo = new File(deployInto.buildDir, repoDirectoryName).canonicalFile
		final String m2RepoURL = "file://${deployRepo}"

		Configuration configuration = project.configurations.getByName(Dependency.ARCHIVES_CONFIGURATION)
		this.configuration = configuration

		MavenRepositoryHandlerConvention repositories =
				new DslObject(repositories).convention.getPlugin(MavenRepositoryHandlerConvention.class)

		outputs.dir deployRepo
		repositories.mavenDeployer() {
			repository(url: m2RepoURL)
		}
	}
}
