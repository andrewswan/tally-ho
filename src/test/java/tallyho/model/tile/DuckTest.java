/*
 * Created on 21/08/2004
 */
package tallyho.model.tile;

import tallyho.model.Team;
import junit.framework.TestCase;


/**
 * Tests the duck model
 */
public class DuckTest extends TestCase {

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
   * Tests that a duck has the correct range
   */
  public void testGetRange() {
    assertEquals(Integer.MAX_VALUE, duck.getRange());
  }

  /**
   * Makes sure a duck can capture the correct tiles
   */
  public void testCanCapture() {
    assertFalse("Shouldn't be able to capture a null Tile", duck.canCapture(null));
    assertFalse("Shouldn't be able to capture a bear", duck.canCapture(bear));
    assertFalse("Shouldn't be able to capture a duck", duck.canCapture(new Duck()));
    assertFalse("Shouldn't be able to capture a fox", duck.canCapture(fox));
    assertFalse("Shouldn't be able to capture a hunter", duck.canCapture(hunter));
    assertFalse("Shouldn't be able to capture a lumberjack", duck.canCapture(lumberjack));
    assertFalse("Shouldn't be able to capture a pheasant", duck.canCapture(pheasant));
    assertFalse("Shouldn't be able to capture a tree", duck.canCapture(tree));
  }

  /**
   * Tests that ducks have the correct name
   */
  public void testGetName() {
    assertEquals("Duck", duck.getName());
  }

  /**
   * Tests that ducks report the correct prey types
   */
  public void testGetPrey() {
    Class[] prey = duck.getPrey();
    assertNotNull("Prey array shouldn't be null", prey);
    assertEquals(0, prey.length);
  }

  /**
   * Tests that ducks are on the right team
   */
  public void testGetTeam() {
    assertEquals(Team.NEUTRAL, duck.getTeam());
  }
}