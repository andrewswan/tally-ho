/*
 * Created on 28/08/2004
 */
package tallyho.view.swing;

import junit.framework.TestCase;
import tallyho.model.Game;
import tallyho.model.player.Player;
import tallyho.model.player.RealPlayer;

/**
 * Tests the PlayerNameLabel class in this package
 */
public class PlayerNameLabelTest extends TestCase {

  // Fixture
  private Game game;
  private Player playerOne;
  private Player playerTwo;
  private PlayerNameLabel label1, label2;

  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() {
    playerOne = new RealPlayer();
    playerTwo = new RealPlayer();
    game = new Game(playerOne, playerTwo);
    label1 = new PlayerNameLabel(game, playerOne);
    label2 = new PlayerNameLabel(game, playerTwo);
  }

  /**
   * Tests that a PlayerNameLabel can be constructed
   */
  public void testConstructor() {
    // Test game can't be null
    try {
      new PlayerNameLabel(null, playerOne);
      fail("Game can't be null");
    }
    catch (IllegalArgumentException expected) {
      // Success
    }
    
    // Test player can't be null
    try {
      new PlayerNameLabel(game, null);
      fail("Player can't be null");
    }
    catch (IllegalArgumentException expected) {
      // Success
    }
  }
  
  /**
   * Tests that this label has the correct text for a given player
   */
  public void testGetText() {
    // Test with the default players (from the setUp() method)
    assertEquals("<html><br>(Predators)</html>", label1.getText());
    assertEquals("<html><br>(Humans)</html>", label2.getText());
    
    // Test with players that have no names
    playerOne.setName("Andrew");
    playerTwo.setName("Gerri");
    label1.setText(playerOne);
    label2.setText(playerTwo);
    
    // Test with named players
    assertEquals("<html>Andrew<br>(Predators)</html>", label1.getText());
    assertEquals("<html>Gerri<br>(Humans)</html>", label2.getText());
  }
}
