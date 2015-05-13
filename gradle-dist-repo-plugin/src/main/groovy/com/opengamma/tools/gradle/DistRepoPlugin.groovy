package com.opengamma.tools.gradle

import com.opengamma.tools.gradle.task.DeployLocal
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.plugins.MavenRepositoryHandlerConvention
import org.gradle.api.tasks.Upload
import org.gradle.api.tasks.bundling.Zip


class DistRepoPlugin implements Plugin<Project>
{
    public final static String DIST_LOCAL_TASK_NAME = "deployLocal"
    public final static String ZIP_TASK_NAME = "zipRepoLocal"
    public final static String EXTENSION_NAME = "distRepo"

    Project project

    @Override
    void apply(Project target)
    {
        this.project = target

        applyMavenPlugin()
        addExtension()
        addUploadTask()
        addZipTask()
    }

    private void applyMavenPlugin()
    {
        project.plugins.apply(MavenPlugin)
    }

    private void addExtension()
    {
        project.extensions.create(EXTENSION_NAME, DistRepoExtension)
    }

    private void addUploadTask()
    {
        project.afterEvaluate {

        }
    }

    private void addZipTask()
    {
        project.afterEvaluate {
            DistRepoExtension ext = project.distRepo

            Zip task = project.tasks.create(ZIP_TASK_NAME, Zip)
            task.from project.tasks[DIST_LOCAL_TASK_NAME]
            task.into("${project.name}-${project.version}")
            task.onlyIf { ext.distProject }
        }
    }

//    private void configureForRootIfNecessary()
//    {
//        Map<Project, Upload> distLocals = [:]
//
//        if(project.rootProject == project)
//            project.subprojects.findAll { Project p ->
//                if(p.plugins.findPlugin(DistRepoPlugin))
//                    distLocals[p] = p.tasks[DIST_LOCAL_TASK_NAME]
//            }
//
//        distLocals.each { p, t ->
//
//        }
//    }
}
