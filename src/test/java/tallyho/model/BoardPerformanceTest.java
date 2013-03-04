/*
 * Created on 5/07/2005
 */
package tallyho.model;

import com.clarkware.junitperf.TimedTest;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * Tests the performance of the BoardImpl class
 */
public class BoardPerformanceTest extends TestCase {
  
  // Constants
  private static final long MAX_SETUP_TIME = 60; // ms

  /**
   * @return the test to be run
   */
  public static Test suite() {
    return new TimedTest(new BoardTest("testSetUpTiles"), MAX_SETUP_TIME);
  }
}
