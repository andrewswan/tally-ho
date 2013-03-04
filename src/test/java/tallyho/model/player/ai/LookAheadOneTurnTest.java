/*
 * Created on 25/09/2004
 */
package tallyho.model.player.ai;

import junit.framework.TestCase;
import tallyho.model.Board;
import tallyho.model.BoardImpl;
import tallyho.model.Game;
import tallyho.model.Team;
import tallyho.model.player.Player;
import tallyho.model.player.RealPlayer;
import tallyho.model.tile.Directional;
import tallyho.model.tile.Fox;
import tallyho.model.tile.Hunter;
import tallyho.model.turn.Rescue;
import tallyho.model.turn.Turn;

/**
 * Tests the look-ahead AI, looking ahead one turn, i.e. a total of one
 * predator turn and one human turn.
 */
public class LookAheadOneTurnTest extends TestCase {

  // Fixture
  private LookAheadAI lookAheadOne;
  private Board board;
  private int centre;

  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    lookAheadOne = new LookAheadAI(1);
    lookAheadOne.setTeam(Team.PREDATORS);

    // This player only needed for the game constructor
    Player realPlayer = new RealPlayer();
    realPlayer.setTeam(Team.HUMANS);

    Game game = new Game(lookAheadOne, realPlayer);
    board = new BoardImpl(game);  // makes an empty board
    centre = board.getMaxIndex() / 2;
  }
  
//  /**
//   * Test on an empty board
//   */
//  public void testEmptyBoard() {
//    try {
//      lookAheadOne.getTurn(board);
//      fail("Should be no turns on an empty board");
//    }
//    catch (IllegalStateException expected) {
//      // Success
//    }
//  }
  
//  /**
//   * Test with a bear and rescuable fox
//   */
//  public void testWithBearAndRescueableFox() {
//    // Set up bear
//    Bear bear = new Bear();
//    int bearX = 1;
//    int bearY = centre;
//    board.addTile(bear, bearX, bearY);
//    board.flipTile(Team.HUMANS, bearX, bearY);
//    
//    // Set up duck (for the human to move on their turn)
//    Duck duck = new Duck();
//    int duckX = 0;
//    int duckY = 0;
//    board.addTile(duck, duckX, duckY);
//    board.flipTile(Team.PREDATORS, duckX, duckY);
//    
//    // Set up fox
//    Fox fox = new Fox();
//    int foxX = centre;
//    int foxY = 2;
//    board.addTile(fox, foxX, foxY);
//    board.flipTile(Team.HUMANS, foxX, foxY);
//    
//    // Ask the AI to find the best turn
//    Turn turn = lookAheadOne.getTurn(board);
//    
//    // Check the best turn (rescuing the fox) was found
//    assertEquals(fox.getValue(), turn.getScore());
//    // Check neither tile has actually been moved
//    assertSame(bear, board.getTile(bearX, bearY));
//    assertSame(fox, board.getTile(foxX, foxY));
//  }

  /**
   * Test with two foxes, one threatened
   */
  public void testWithTwoFoxes() {
    // Set up fox 1
    Fox fox1 = new Fox();
    int foxX1 = centre;
    int foxY1 = 0;
    board.addTile(fox1, foxX1, foxY1);
    board.flipTile(Team.PREDATORS, foxX1, foxY1);
    
    // Set up hunter pointing to big X
    Hunter hunter;
    do {
      hunter = new Hunter();
    } while (hunter.getDirection() != Directional.BIG_X);
    assertEquals(Directional.BIG_X, hunter.getDirection());
    int hunterX = foxX1 - 2;
    int hunterY = foxY1;
    board.addTile(hunter, hunterX, hunterY);
    board.flipTile(Team.HUMANS, hunterX, hunterY);
    
    // Set up fox 2
    Fox fox2 = new Fox();
    int foxX2 = board.getMaxIndex();
    int foxY2 = centre;
    board.addTile(fox2, foxX2, foxY2);
    board.flipTile(Team.PREDATORS, foxX2, foxY2);
    
    // Ask the AI to find the best turn
    Turn bestTurn = lookAheadOne.getTurn(board);
    
    // Check the best turn (rescuing the threatened fox) was found, note it can
    // be rescued either towards SMALL_Y or BIG_Y => two best paths
    assertTrue("Best turn should be a rescue", bestTurn instanceof Rescue);
    Rescue rescue = (Rescue) bestTurn;
    assertEquals(foxX1, rescue.getFromX());
    assertEquals(foxY1, rescue.getFromY());
    
    // Check none of the tiles have actually been moved
    assertSame(fox1, board.getTile(foxX1, foxY1));
    assertSame(fox2, board.getTile(foxX2, foxY2));
    assertSame(hunter, board.getTile(hunterX, hunterY));
  }

//  /**
//   * Tests the AI can look ahead when there are no next turns (i.e. for the
//   * other player)
//   */
//  public void testWithNoNextTurn() {
//    // Set up bear in middle of left edge
//    Bear bear = new Bear();
//    int bearX = 0;
//    int bearY = centre;
//    board.addTile(bear, bearX, bearY);
//    board.flipTile(Team.HUMANS, bearX, bearY);
//    
//    // Ask the AI to find the best turn
//    Turn turn = lookAheadOne.getTurn(board);
//    
//    // Check that it is rescuing the bear we placed
//    assertEquals(
//        new Rescue(Team.PREDATORS, bearX, bearY, bearX - 1, bearY, bear.getValue()),
//        turn);
//  }
}
