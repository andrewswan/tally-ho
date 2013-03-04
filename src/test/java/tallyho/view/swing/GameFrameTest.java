/*
 * Created on 9/07/2005
 */
package tallyho.view.swing;

import tallyho.model.Game;
import tallyho.model.player.Player;
import tallyho.model.player.RealPlayer;
import junit.framework.TestCase;

/**
 * Test case for the game frame.
 */
public class GameFrameTest extends TestCase {

  /**
   * Tests that the static reference to the sounds folder is valid
   */
  public void testSoundsFolderReachable() {
    assertNotNull("Sounds folder should be reachable", GameFrame.SOUNDS_URL);
  }
  
  /**
   * Test method for 'tallyho.view.swing.GameFrame.GameFrame(Game)'
   */
  public void testGameFrame() {
    Player playerOne = new RealPlayer();
    Player playerTwo = new RealPlayer();
    Game game = new Game(playerOne, playerTwo);
    new GameFrame(game);
  }
}
