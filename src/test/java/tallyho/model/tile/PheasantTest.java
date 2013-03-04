/*
 * Created on 21/08/2004
 */
package tallyho.model.tile;

import tallyho.model.Team;
import junit.framework.TestCase;

/**
 * Tests the Pheasant tile
 */
public class PheasantTest extends TestCase {

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
   * Tests that pheasants have the correct range
   */
  public void testGetRange() {
    assertEquals(Integer.MAX_VALUE, pheasant.getRange());
  }

  /**
   * Tests that pheasants have the correct types of prey
   */
  public void testCanCapture() {
    assertFalse("Shouldn't be able to capture a null Tile", pheasant.canCapture(null));
    assertFalse("Shouldn't be able to capture a bear", pheasant.canCapture(bear));
    assertFalse("Shouldn't be able to capture a duck", pheasant.canCapture(duck));
    assertFalse("Shouldn't be able to capture a fox", pheasant.canCapture(fox));
    assertFalse("Shouldn't be able to capture a hunter", pheasant.canCapture(hunter));
    assertFalse("Shouldn't be able to capture a lumberjack", pheasant.canCapture(lumberjack));
    assertFalse("Shouldn't be able to capture a pheasant", pheasant.canCapture(new Pheasant()));
    assertFalse("Shouldn't be able to capture a tree", pheasant.canCapture(tree));
  }

  /**
   * Tests that pheasants have the correct name
   */
  public void testGetName() {
    assertEquals("Pheasant", pheasant.getName());
  }
  
  /**
   * Tests that pheasants are on the correct team
   */
  public void testGetTeam() {
    assertEquals(Team.NEUTRAL, pheasant.getTeam());
  }

  /**
   * Tests that pheasants have the correct types of prey
   */
  public void testGetPrey() {
    Class[] prey = pheasant.getPrey();
    assertNotNull("Prey array shouldn't be null", prey);
    assertEquals(0, prey.length);
  }
}