/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.tools.gradle.release

import com.opengamma.tools.gradle.release.task.UpdateVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class AutoVersionPlugin implements Plugin<Project>
{
    private final static String UPDATE_VERSION_TASK_NAME = "updateVersion"

    Project project

    @Override
    void apply(Project target)
    {
        this.project = target
        project.extensions.create("release", ReleaseExtension)

        addUpdateVersionTask()
    }

    private Task addUpdateVersionTask()
    {
        Task t = projects.tasks.create(UPDATE_VERSION_TASK_NAME, UpdateVersion)
        // TODO t.mustRunAfter "checkReleaseEnvironment"

        // TODO THIS HAS NO BUSINESS BEING HERE
//        t.configure {
//            project.gradle.taskGraph.whenReady { taskGraph ->
//            if(project.tasks[ReleasePlugin.RELEASE_TASK_NAME])
//                project.release.releaseBuild = true
//            }
//        }

        t.doFirst {
            Project rootProject = project.rootProject
            rootProject.allprojects { p ->
                p.version = rootProject.release.releaseBuild ?
                        rootProject.release.releaseVersion.toString() : getDerivedSnapshotVersion().toString()
            }
        }
    }
}
