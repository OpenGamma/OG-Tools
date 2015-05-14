package com.opengamma.tools.gradle

import org.gradle.api.Project

class DistRepoExtension
{
    String repoDirectoryName = "m2_dist"
    Project deployInto
    File deployRepo
}
