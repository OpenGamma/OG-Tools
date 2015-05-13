/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.tools.gradle.release

import com.opengamma.tools.gradle.release.task.CheckReleaseEnvironment
import com.opengamma.tools.gradle.release.task.UpdateVersion
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.tasks.bundling.AbstractArchiveTask

class ReleasePlugin implements Plugin<Project>
{
    public final static String RELEASE_TASK_NAME = "release"
    public final static String CHECK_RELEASE_ENVIRONMENT_TASK_NAME = "checkReleaseEnvironment"
	public final static Optional<Class> DEPLOY_LOCAL_TASK_TYPE = safeGetClass("com.opengamma.tools.gradle.task.DeployLocal")

	Project project

    @Override
    void apply(Project target)
    {
	    this.project = target

	    addPackageTask()

	    addCheckReleaseEnvironmentTask()
	    addReleaseTask()
    }

	private Task addCheckReleaseEnvironmentTask()
	{
		Task t = project.tasks.create(CHECK_RELEASE_ENVIRONMENT_TASK_NAME, CheckReleaseEnvironment)
		return t
	}

	private Task addReleaseTask()
	{
		Task t = project.tasks.create(RELEASE_TASK_NAME, DefaultTask)
		t.configure {
			project.gradle.taskGraph.whenReady { TaskExecutionGraph taskGraph ->
				if(taskGraph.hasTask(project.tasks[ReleasePlugin.RELEASE_TASK_NAME]))
				{
					project.release.releaseBuild = true
					reconfigureVersion()
				}
			}
		}
		t.dependsOn project.tasks[CHECK_RELEASE_ENVIRONMENT_TASK_NAME]
		return t
	}

	private void reconfigureVersion()
	{
		def setVersion = {
			project.allprojects*.version = project.release.releaseVersion.toString()
		}
		if(project.plugins.hasPlugin(AutoVersionPlugin))
			project.tasks[AutoVersionPlugin.UPDATE_VERSION_TASK_NAME].doLast setVersion
		else
			setVersion()
	}

	private void addPackageTask()
	{
		Task t = project.tasks.create("package", DefaultTask)
		t.dependsOn project.rootProject.getTasksByName("build", true)
		project.rootProject.tasks.withType(AbstractArchiveTask) { at ->
			t.dependsOn at
		}
		DEPLOY_LOCAL_TASK_TYPE.ifPresent { taskType ->
			project.rootProject.tasks.withType(taskType) { dl ->
				t.dependsOn dl
			}
		}
	}

	private static Optional<Class> safeGetClass(String className)
	{
		try {
			return Optional.of(Class.forName(className))
		} catch(ClassNotFoundException ignored) {
			return Optional.empty()
		}
	}
}
