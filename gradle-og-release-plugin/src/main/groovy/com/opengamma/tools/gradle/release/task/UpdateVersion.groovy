/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.tools.gradle.release.task

import com.opengamma.tools.gradle.release.AutoVersionPlugin
import com.opengamma.tools.gradle.release.SnapshotVersionDeriver
import com.opengamma.tools.gradle.simpleexec.SimpleExec
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class UpdateVersion extends DefaultTask
{
    @TaskAction
    void updateProjectVersion()
    {
	    String template = project.release.releaseTagTemplate
	    SimpleExec baseDescTask = project.tasks[AutoVersionPlugin.DESCRIBE_TAG_TASK_NAME]
	    SimpleExec commitDescTask = project.tasks[AutoVersionPlugin.DESCRIBE_COMMIT_TASK_NAME]
	    String baseDesc, commitDesc
	    if(baseDescTask.output.exitCode == 0 && commitDescTask.output.exitCode == 0)
	    {
		    baseDesc = baseDescTask.output.stdOut
		    commitDesc = commitDescTask.output.stdOut
	    }
	    else
	    {
		    String fallbackVersion =
				    project.version && project.version != "unspecified" ? project.version.toString() - "-SNAPSHOT" : "0.1.0"
		    String desc = template.replaceAll("@version@", fallbackVersion)
		    baseDesc = desc
		    commitDesc = desc
	    }
	    Integer buildNumber = System.getenv("BUILD_NUMBER") as Integer
	    SnapshotVersionDeriver versionDeriver = new SnapshotVersionDeriver(baseDesc, commitDesc, template, buildNumber)
	    String newVersion = versionDeriver.deriveSnapshot().toString()
	    project.allprojects { p ->
		    p.version = newVersion
	    }
    }
}
