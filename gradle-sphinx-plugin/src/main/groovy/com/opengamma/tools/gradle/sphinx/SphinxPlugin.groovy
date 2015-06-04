/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.tools.gradle.sphinx

import com.opengamma.tools.gradle.git.task.GitClone
import com.opengamma.tools.gradle.simpleexec.SimpleExec
import com.opengamma.tools.gradle.sphinx.task.CheckDocsEnvironment
import com.opengamma.tools.gradle.sphinx.task.Sphinx
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Zip

class SphinxPlugin implements Plugin<Project>
{
	private final static FETCH_DOCS_COMMON_TASK_NAME = "fetchDocsCommon"
	private final static DOCS_COMMON_REPO = "git@github.com:OpenGamma/London.git"
	private final static DOCS_COMMON_BRANCH = "develop"
	private final static String STAGE_DOCS_COMMON_TASK_BASE_NAME = "stageDocsCommon"
	private final static String STAGE_DOCS_SOURCE_TASK_BASE_NAME = "stageDocsSource"
	private final static String SPHINX_BUILD_TASK_BASE_NAME = "sphinxBuild"
	private final static String COLLECT_SPHINX_OUTPUT_TASK_BASE_NAME = "collectSphinxOutput"
	private final static String BUILD_GUIDE_TASK_BASE_NAME = "buildGuide"
	private final static String PACKAGE_GUIDE_TASK_BASE_NAME = "packageGuide"
	private final static String BUILD_GUIDES_TASK_NAME = "buildSphinx"
	private final static String PACKAGE_GUIDES_TASK_NAME = "packageSphinx"

	private Project project

	@Override
	void apply(Project target)
	{
		this.project = target

		addCheckDocsEnvironmentTask()
		addFetchDocsCommonTask()

		project.afterEvaluate {
			addStageDocsCommonTasks()
			addStageDocsSourceTasks()
			addSphinxBuildTasks()
			addCollectSphinxOutputTasks()
			addBuildGuideTasks()
			addPackageGuideTasks()
			addBuildGuidesTask()
			addPackageGuidesTask()
		}
	}

	private void addCheckDocsEnvironmentTask()
	{
		Task t = project.tasks.create(CheckDocsEnvironment.TASK_NAME, CheckDocsEnvironment)
		t.description = "Check docs environment"
		t.group = "Documentation"
	}

	private void addFetchDocsCommonTask()
	{
		GitClone t = project.tasks.create(FETCH_DOCS_COMMON_TASK_NAME, GitClone)
		t.dependsOn project.tasks[CheckDocsEnvironment.TASK_NAME]
		t.gitRepoURL = DOCS_COMMON_REPO
		t.outputDirectory = new File(project.buildDir, "/tmp/docs-common")
		t.gitBranch = DOCS_COMMON_BRANCH
	}

	private void addStageDocsCommonTasks()
	{
		project.tasks.withType(Sphinx) { s ->
			Copy t = project.tasks.create(task(STAGE_DOCS_COMMON_TASK_BASE_NAME, s), Copy)
			t.from(project.tasks[FETCH_DOCS_COMMON_TASK_NAME]) {
				include "docs/**"
				exclude "**/*.sh", "**/.*", "docs/index.rst"
				eachFile { FileCopyDetails d ->
					d.path = d.path - "docs/"
				}
			}
			t.into(new File(project.buildDir, "/tmp/docs-stage-${s.name}"))
		}
	}

	private void addStageDocsSourceTasks()
	{
		project.tasks.withType(Sphinx) { s ->
			Task t = project.tasks.create(task(STAGE_DOCS_SOURCE_TASK_BASE_NAME, s), DefaultTask)
			t.doLast {
				project.copy {
					from s.index
					rename s.index.name, "index.rst"
					into new File(project.buildDir, "/tmp/docs-stage-${s.name}")
				}

				project.copy {
					from project.projectDir
					into new File(project.buildDir, "/tmp/docs-stage-${s.name}")
					for(String i: s.includes)
						include(i)
					exclude("**/build/")
					exclude("**/tmp/")
					includeEmptyDirs = false
				}
			}
		}
	}

	private void addSphinxBuildTasks()
	{
		Environment env = new Environment()
		project.tasks.withType(Sphinx) { s ->
			SimpleExec t = project.tasks.create(task(SPHINX_BUILD_TASK_BASE_NAME, s), SimpleExec)
			t.environment = env.pythonEnvironment
			t.command = "make html"
			t.workingDirectory = new File(project.buildDir, "/tmp/docs-stage-${s.name}")
			t.outputs.dir new File(t.workingDirectory, "/_build/html")
			t.dependsOn project.tasks[task(STAGE_DOCS_COMMON_TASK_BASE_NAME, s)]
			t.dependsOn project.tasks[task(STAGE_DOCS_SOURCE_TASK_BASE_NAME, s)]
		}
	}

	private void addCollectSphinxOutputTasks()
	{
		project.tasks.withType(Sphinx) { s ->
			Task t = project.tasks.create(task(COLLECT_SPHINX_OUTPUT_TASK_BASE_NAME, s), DefaultTask)
			SimpleExec sphinxBuild = project.tasks[task(SPHINX_BUILD_TASK_BASE_NAME, s)]
			t.dependsOn sphinxBuild
			t.onlyIf = { sphinxBuild.didWork }
			t.doLast {
				if (sphinxBuild.output?.stdErr?.trim())
				{
					if (project.hasProperty("ignoreSphinxWarnings") || s.ignoreSphinxWarnings)
						sphinxBuild.output.stdErr.eachLine { l ->
							logger.warn "sphinxBuild: ${l}"
						}
					else
						throw new GradleException("""\
Sphinx build failed or unclean.
=== SPHINX OUTPUT ===
${sphinxBuild.output.stdErr}
=== END SPHINX OUTPUT ===
""")
				}
			}

			project.tasks[task(SPHINX_BUILD_TASK_BASE_NAME, s)].finalizedBy t
		}
	}

	private void addBuildGuideTasks()
	{
		project.tasks.withType(Sphinx) { s ->
			Copy t = project.tasks.create(task(BUILD_GUIDE_TASK_BASE_NAME, s), Copy)
			t.dependsOn project.tasks[task(COLLECT_SPHINX_OUTPUT_TASK_BASE_NAME, s)]
			t.configure {
				from(project.tasks[task(SPHINX_BUILD_TASK_BASE_NAME, s)])
				into(new File(project.buildDir, "/docs/guide-${s.name}"))
			}
		}
	}

	private void addPackageGuideTasks()
	{
		project.tasks.withType(Sphinx) { s ->
			Zip t = project.tasks.create(task(PACKAGE_GUIDE_TASK_BASE_NAME, s), Zip)
			t.dependsOn project.tasks[task(BUILD_GUIDE_TASK_BASE_NAME, s)]

			t.from project.tasks[task(BUILD_GUIDE_TASK_BASE_NAME, s)]
			t.into "html"

			t.doFirst {
				String userGuideArchiveName = s.userGuideArchiveName
				if (!userGuideArchiveName?.trim())
					throw new GradleException("userGuideArchiveName is required property")

				archiveName = userGuideArchiveName.replaceAll("@project\\.version@", project.version.toString())
			}
		}
	}

	private void addBuildGuidesTask()
	{
		Task t = project.tasks.create(BUILD_GUIDES_TASK_NAME, DefaultTask)

		t.group = "Documentation"
		t.description = "Run Sphinx against the rst sources to generate the HTML User Guide"

		project.tasks.all {
			if(it.name.startsWith(BUILD_GUIDE_TASK_BASE_NAME))
				t.dependsOn it
		}
	}

	private void addPackageGuidesTask()
	{
		Task t = project.tasks.create(PACKAGE_GUIDES_TASK_NAME, DefaultTask)

		t.group = "Documentation"
		t.description = "Package the HTML User Guide into a Zip archive"

		project.tasks.all {
			if(it.name.startsWith(PACKAGE_GUIDE_TASK_BASE_NAME))
				t.dependsOn it
		}
	}

	private String task(String baseName, Sphinx task)
	{
		return baseName + task.name.capitalize()
	}
}
