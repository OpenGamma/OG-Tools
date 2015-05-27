/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.tools.gradle.distrepo.task

import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.Upload

class DeployLocal extends Upload
{
	@OutputDirectory
	File deployIntoDir
}
