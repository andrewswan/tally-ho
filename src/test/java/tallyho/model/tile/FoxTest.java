/*
 * Created on 21/08/2004
 */
package tallyho.model.tile;

import junit.framework.TestCase;
import tallyho.model.Team;


/**
 * Tests the Fox tile
 */
public class FoxTest extends TestCase {

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
   * Tests that foxes can capture the right types of tiles
   */
  public void testCanCapture() {
    assertFalse("Shouldn't be able to capture a null Tile", fox.canCapture(null));
    assertFalse("Shouldn't be able to capture a bear", fox.canCapture(bear));
    assertTrue("Should be able to capture a duck", fox.canCapture(duck));
    assertFalse("Shouldn't be able to capture a fox", fox.canCapture(new Fox()));
    assertFalse("Shouldn't be able to capture a hunter", fox.canCapture(hunter));
    assertFalse("Shouldn't be able to capture a lumberjack", fox.canCapture(lumberjack));
    assertTrue("Should be able to capture a pheasant", fox.canCapture(pheasant));
    assertFalse("Shouldn't be able to capture a tree", fox.canCapture(tree));
  }

  /**
   * Tests that foxes have the right name
   */
  public void testGetName() {
    assertEquals("Fox", fox.getName());
  }
  
  /**
   * Tests that foxes are on the right team
   */
  public void testGetTeam() {
    assertEquals(Team.PREDATORS, fox.getTeam());
  }
}
