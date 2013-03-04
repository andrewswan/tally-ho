/*
 * Created on 25/09/2004
 */
package tallyho.model.player.ai;

import junit.framework.TestCase;
import tallyho.model.Team;
import tallyho.model.turn.Pass;

/**
 * Test the Path class
 */
public class PathTest extends TestCase {

  /**
   * Tests the clone() method
   * 
   * @throws CloneNotSupportedException
   */
  public void testClone() throws CloneNotSupportedException {
    // Check that the cloning succeeds
    Path original = new Path(new Pass(Team.HUMANS));
    Path clone = (Path) original.clone();
    assertNotSame(original, clone);
    
    // Check that the lists are indeed different
    original.addTurn(new Pass(Team.PREDATORS));
    assertEquals(1, clone.getLength());
    assertEquals(2, original.getLength());
  }
}
