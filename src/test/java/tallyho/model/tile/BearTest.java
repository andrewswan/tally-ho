/*
 * Created on 21/08/2004
 */
package tallyho.model.tile;

import tallyho.model.Team;
import junit.framework.TestCase;

/**
 * Tests the Bear tile
 */
public class BearTest extends TestCase {

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
   * Tests that a bear can capture exactly the right types of tiles
   */
  public void testCanCapture() {
    assertFalse("Shouldn't be able to capture a null Tile", bear.canCapture(null));
    assertFalse("Shouldn't be able to capture a bear", bear.canCapture(new Bear()));
    assertFalse("Shouldn't be able to capture a duck", bear.canCapture(duck));
    assertFalse("Shouldn't be able to capture a fox", bear.canCapture(fox));
    assertTrue("Should be able to capture a hunter", bear.canCapture(hunter));
    assertTrue("Should be able to capture a lumberjack", bear.canCapture(lumberjack));
    assertFalse("Shouldn't be able to capture a pheasant", bear.canCapture(pheasant));
    assertFalse("Shouldn't be able to capture a tree", bear.canCapture(tree));
  }

  /**
   * Tests that a bear knows its name
   */
  public void testGetName() {
    assertEquals("Bear", bear.getName());
  }

  /**
   * Tests that a bear has the correct range
   */
  public void testGetRange() {
    assertEquals(1, bear.getRange());
  }

  /**
   * Tests that bears are on the right team
   */
  public void testGetTeam() {
    assertEquals(Team.PREDATORS, bear.getTeam());
  }
}