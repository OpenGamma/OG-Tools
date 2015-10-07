/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.tools.gradle.release


trait SafeClassReference
{
	static Optional<Class> safeGetClass(String className)
	{
		try {
			return Optional.of(Class.forName(className))
		} catch(ClassNotFoundException ignored) {
			return Optional.empty()
		}
	}
}