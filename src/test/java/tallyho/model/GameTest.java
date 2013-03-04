/*
 * Created on 28/08/2004
 */
package tallyho.model;

import junit.framework.TestCase;
import tallyho.model.player.Player;
import tallyho.model.player.RealPlayer;

/**
 * Tests the Game model class
 */
public class GameTest extends TestCase {

  // Fixture
  private Game game;
  private final Player player1 = new RealPlayer();
  private final Player player2 = new RealPlayer();
  
  /**
   * @see TestCase#setUp()
   */
  public void setUp() {
    // Configure the players
    player1.setName("One");
    player1.setTeam(Team.PREDATORS);
    player2.setName("Two");
    player2.setTeam(Team.HUMANS);

    // Configure the game
    game = new Game(player1, player2);
  }

  /**
   * Tests that the initial setup of the game is correct
   */
  public void testInitialSetup() {
    // Test the game is correct
    assertEquals(Integer.MAX_VALUE, game.getTurnsLeft());
    assertEquals(1, game.getActivePlayerNumber());
    assertEquals(player1, game.getActivePlayer());
    assertFalse("Game shouldn't be over before it's begun", game.isOver());

    // Test the players are correct
    assertEquals("Player 1 should start as the predators",
        Team.PREDATORS, player1.getTeam());
    assertEquals("Player 2 should start as the humans",
        Team.HUMANS, player2.getTeam());
  }
}
