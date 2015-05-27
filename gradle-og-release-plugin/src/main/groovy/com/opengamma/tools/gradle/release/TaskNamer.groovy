/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.tools.gradle.release

trait TaskNamer
{
    String taskNameFor(String baseName)
    {
        return "${baseName}${this.project.name.capitalize()}"
    }
}
