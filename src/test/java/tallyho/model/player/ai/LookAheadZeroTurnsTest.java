/*
 * Created on 24/09/2004
 */
package tallyho.model.player.ai;

import junit.framework.TestCase;
import tallyho.model.Board;
import tallyho.model.BoardImpl;
import tallyho.model.Game;
import tallyho.model.Team;
import tallyho.model.player.Player;
import tallyho.model.player.RealPlayer;
import tallyho.model.tile.Bear;
import tallyho.model.tile.Fox;
import tallyho.model.turn.Turn;

/**
 * Tests the look-ahead AI, looking ahead no turns
 */
public class LookAheadZeroTurnsTest extends TestCase {

  // Fixture
  ComputerPlayer zeroLookAhead;
  Board board;
  int centre;

  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    zeroLookAhead = new LookAheadAI(0);
    zeroLookAhead.setTeam(Team.PREDATORS);

    // This player only needed for the game constructor
    Player realPlayer = new RealPlayer();
    realPlayer.setTeam(Team.HUMANS);

    Game game = new Game(zeroLookAhead, realPlayer);
    board = new BoardImpl(game);  // makes an empty board
    centre = board.getMaxIndex() / 2;
  }
  
  /**
   * Test on an empty board
   */
  public void testEmptyBoard() {
    try {
      zeroLookAhead.getTurn(board);
      fail("Should be no turns on an empty board");
    }
    catch (IllegalStateException expected) {
      // Success
    }
  }
  
  /**
   * Test with only a rescuable bear
   */
  public void testWithOnlyRescueableBear() {
    Bear bear = new Bear();
    int bearX = 0;
    int bearY = centre;  // board centre axis
    board.addTile(bear, bearX, bearY);
    board.flipTile(Team.HUMANS, bearX, bearY);
    Turn turn = zeroLookAhead.getTurn(board);
    // Check the best turn (rescuing the bear) was found
    assertEquals(bear.getValue(), turn.getScore());
    // Check the bear hasn't actually been moved
    assertSame(bear, board.getTile(bearX, bearY));
  }
  
  /**
   * Test with a rescuable bear and fox
   */
  public void testWithRescueableBearAndFox() {
    // Set up bear
    Bear bear = new Bear();
    int bearX = 0;
    int bearY = centre;
    board.addTile(bear, bearX, bearY);
    board.flipTile(Team.PREDATORS, bearX, bearY);
    
    // Set up fox
    Fox fox = new Fox();
    int foxX = centre;
    int foxY = 1;
    board.addTile(fox, foxX, foxY);
    board.flipTile(Team.HUMANS, foxX, foxY);
    
    Turn turn = zeroLookAhead.getTurn(board);
    // Check the best turn (rescuing the bear) was found
    assertEquals(bear.getValue(), turn.getScore());
    // Check neither tile has actually been moved
    assertSame(bear, board.getTile(bearX, bearY));
    assertSame(fox, board.getTile(foxX, foxY));
  }
  
  /**
   * Test with a bear and rescuable fox
   */
  public void testWithBearAndRescueableFox() {
    // Set up bear
    Bear bear = new Bear();
    int bearX = 1;
    int bearY = centre;
    board.addTile(bear, bearX, bearY);
    board.flipTile(Team.PREDATORS, bearX, bearY);
    
    // Set up fox
    Fox fox = new Fox();
    int foxX = centre;
    int foxY = 2;
    board.addTile(fox, foxX, foxY);
    board.flipTile(Team.HUMANS, foxX, foxY);
    
    Turn turn = zeroLookAhead.getTurn(board);
    // Check the best turn (rescuing the fox) was found
    assertEquals(fox.getValue(), turn.getScore());
    // Check neither tile has actually been moved
    assertSame(bear, board.getTile(bearX, bearY));
    assertSame(fox, board.getTile(foxX, foxY));
  }
}
