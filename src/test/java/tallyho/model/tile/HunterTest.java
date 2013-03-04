/*
 * Created on 21/08/2004
 */
package tallyho.model.tile;

import tallyho.model.Team;
import junit.framework.TestCase;

/**
 * Unit tests for the tallyho.model.tile.Hunter class
 */
public class HunterTest extends TestCase {

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
   * Tests that hunters have the correct range
   */
  public void testGetRange() {
    assertEquals(Integer.MAX_VALUE, hunter.getRange());
  }

  /**
   * Tests that hunters have the correct types of prey
   */
  public void testGetPrey() {
    Class[] prey = hunter.getPrey();
    assertNotNull("Array of prey shouldn't be null", prey);
    assertFalse("Prey should contain some elements", 0 == prey.length);
  }

  /**
   * Tests that hunters can capture the correct types of prey
   */
  public void testCanCapture() {
    assertFalse("Shouldn't be able to capture a null Tile", hunter.canCapture(null));
    assertTrue("Should be able to capture a bear", hunter.canCapture(bear));
    assertTrue("Should be able to capture a duck", hunter.canCapture(duck));
    assertTrue("Should be able to capture a fox", hunter.canCapture(fox));
    assertFalse("Shouldn't be able to capture a hunter", hunter.canCapture(new Hunter()));
    assertFalse("Shouldn't be able to capture a lumberjack", hunter.canCapture(lumberjack));
    assertTrue("Should be able to capture a pheasant", hunter.canCapture(pheasant));
    assertFalse("Shouldn't be able to capture a tree", hunter.canCapture(tree));
  }
  
  /**
   * Tests that the hunter's direction is one of the allowed values
   */
  public void testGetDirection() {
    int hunterDirection = hunter.getDirection();
    for (int i = 0; i < Directional.DIRECTIONS.length; i++) {
      int direction = Directional.DIRECTIONS[i];
      if (direction == hunterDirection) {
        // The hunter's direction is one of the allowed values - success
        return;
      }
    }
    fail("The hunter has an invalid direction: " + hunterDirection);
  }

  /**
   * Tests that hunters have the correct name
   */
  public void testGetName() {
    assertEquals("Hunter", hunter.getName());
  }
  
  /**
   * Tests that hunters are on the correct team
   */
  public void testGetTeam() {
    assertEquals(Team.HUMANS, hunter.getTeam());
  }
}
