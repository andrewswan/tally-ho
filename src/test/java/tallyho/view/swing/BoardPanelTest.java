/*
 * Created on 29/08/2004
 */
package tallyho.view.swing;

import java.awt.Component;

import junit.framework.TestCase;
import tallyho.model.Game;
import tallyho.model.Team;
import tallyho.model.player.Player;
import tallyho.model.player.RealPlayer;

/**
 * Tests the Swing representation of the game board
 */
public class BoardPanelTest extends TestCase {

  // Fixture
  private BoardPanel boardPanel;
  
  /*
   * @see TestCase#setUp()
   */
  protected void setUp() {
    // Initialise the fixture
    Player playerOne = new RealPlayer();
    playerOne.setTeam(Team.PREDATORS);
    Player playerTwo = new RealPlayer();
    playerTwo.setTeam(Team.HUMANS);
    Game game = new Game(playerOne, playerTwo);
    GameFrame gameFrame = new GameFrame(game);
    boardPanel = new BoardPanel(game, gameFrame);
  }

  /**
   * Tests that a new board panel is in the expected state
   */
  public void testInitialState() {
    // Get the components on the board
    Component[] components = boardPanel.getComponents();

    // Check there are five - the grid panel plus four exit buttons
    assertEquals(5, components.length);
  }
}
