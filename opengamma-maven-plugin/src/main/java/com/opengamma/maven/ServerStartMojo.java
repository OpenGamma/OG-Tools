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
 * @goal server-start
 * @requiresDependencyResolution compile+runtime
 */
// CSON
public class ServerStartMojo extends AbstractServerMojo {

  @Override
  protected boolean isSpawn() {
    return true;
  }

}
