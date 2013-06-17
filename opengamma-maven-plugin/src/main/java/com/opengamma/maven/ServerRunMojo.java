/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.maven;

//CSOFF for Javadoc tags
/**
 * Runs an OpenGamma component server.
 * 
 * @goal server-run
 * @requiresDependencyResolution compile+runtime
 */
// CSON
public class ServerRunMojo extends AbstractServerMojo {

  @Override
  protected boolean isSpawn() {
    return false;
  }

}
