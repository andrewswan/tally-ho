/*
 * Created on 21/08/2004
 */
package tallyho.model.tile;

import tallyho.model.Team;
import junit.framework.TestCase;

/**
 * Tests the Lumberjack tile
 */
public class LumberjackTest extends TestCase {

  // Fixture
  private Bear bear;
  private Duck duck;
  private Fox fox;
  private Hunter hunter;
  private Lumberjack lumberjack;
  private Pheasant pheasant;
  private Tree tree;
  
  /*
   * @see TestCase#setUp()
   */
  protected void setUp() {
    bear = new Bear();
    duck = new Duck();
    fox = new Fox();
    hunter = new Hunter();
    lumberjack = new Lumberjack();
    pheasant = new Pheasant();
    tree = new Tree();
  }

  /**
   * Tests that lumberjacks have the correct name
   */
  public void testGetName() {
    assertEquals("Lumberjack", lumberjack.getName());
  }
  
  /**
   * Tests that lumberjacks have the correct range
   */
  public void testGetRange() {
    assertEquals(1, lumberjack.getRange());
  }

  /**
   * Tests that lumberjacks have the correct prey
   */
  public void testGetPrey() {
    Class[] prey = lumberjack.getPrey();
    assertNotNull("Array of prey shouldn't be null", prey);
    assertEquals(1, prey.length);
    assertEquals(Tree.class, prey[0]);
  }

  /**
   * Tests that lumberjacks have the correct prey
   */
  public void testCanCapture() {
    assertFalse("Shouldn't be able to capture a null Tile", lumberjack.canCapture(null));
    assertFalse("Shouldn't be able to capture a bear", lumberjack.canCapture(bear));
    assertFalse("Shouldn't be able to capture a duck", lumberjack.canCapture(duck));
    assertFalse("Shouldn't be able to capture a fox", lumberjack.canCapture(fox));
    assertFalse("Shouldn't be able to capture a hunter", lumberjack.canCapture(hunter));
    assertFalse("Shouldn't be able to capture a lumberjack",
        lumberjack.canCapture(new Lumberjack()));
    assertFalse("Shouldn't be able to capture a pheasant", lumberjack.canCapture(pheasant));
    assertTrue("Should be able to capture a tree", lumberjack.canCapture(tree));
  }
  
  /**
   * Tests that lumberjacks are on the correct team
   */
  public void testGetTeam() {
    assertEquals(Team.HUMANS, lumberjack.getTeam());
  }
}