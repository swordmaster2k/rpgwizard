/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.utilities;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

/**
 *
 * @author Joshua Michael Daly
 */
public class FileToolsTest {
  
  public FileToolsTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of createDirectoryStructure method, of class FileTools.
   */
  @Test
  public void testCreateDirectoryStructure() {
    System.out.println("createDirectoryStructure");
    String path = System.getProperty("user.home");
    String project = "Test";
    
    boolean expResult = true;
    boolean result = FileTools.createDirectoryStructure(path, project);
    
    assertEquals(expResult, result);
  }

  /**
   * Test of doChoosePath method, of class FileTools.
   */
  @Test
  @Ignore
  public void testDoChoosePath() {
    System.out.println("doChoosePath");
    File expResult = null;
    File result = FileTools.doChoosePath();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of doChooseFile method, of class FileTools.
   */
  @Test
  @Ignore
  public void testDoChooseFile() {
    System.out.println("doChooseFile");
    String extension = "";
    String directory = "";
    String type = "";
    File expResult = null;
    File result = FileTools.doChooseFile(extension, directory, type);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
}
