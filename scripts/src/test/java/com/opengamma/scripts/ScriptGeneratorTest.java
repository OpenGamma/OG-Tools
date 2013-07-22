/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.scripts;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class ScriptGeneratorTest {

  @DataProvider(name = "lineEndings")
  Object[][] data_lineEndings() {
    return new Object[][] {
        {"", "file.txt", "\n" },
        {"a", "file.txt", "a\n" },

        {"a\r\n b\r c\n d\n\r e", "file.txt", "a\n b\n c\n d\n e\n" },
        {"a\r\n b\r c\n d\n\r e", "file.bat", "a\r\n b\r\n c\r\n d\r\n e\r\n" },
        {"a\r\n b\r c\n d\n\r e", "file.sh", "a\n b\n c\n d\n e\n" },
    };
  }

  @Test(dataProvider = "lineEndings")
  public void test_fixLineEndings(String input, String fileName, String expected) {
    assertEquals(expected, ScriptGenerator.fixLineEndings(input, fileName));
  }

}
