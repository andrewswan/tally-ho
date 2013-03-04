/*
 * Created on 27/09/2004
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
import tallyho.model.tile.Duck;
import tallyho.model.tile.Fox;
import tallyho.model.tile.Tree;
import tallyho.model.turn.Move;
import tallyho.model.turn.Rescue;
import tallyho.model.turn.Turn;

/**
 * Tests the look-ahead AI, looking ahead two turns, for a total of two
 * predator turns and one human turn.
 */
public class LookAheadTwoTurnsTest extends TestCase {

  // Fixture
  ComputerPlayer lookAheadTwo;
  Board board;
  int centre;

  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    lookAheadTwo = new LookAheadAI(2);
    lookAheadTwo.setTeam(Team.PREDATORS);

    // This player only needed for the game constructor
    Player realPlayer = new RealPlayer();
    realPlayer.setTeam(Team.HUMANS);

    Game game = new Game(lookAheadTwo, realPlayer);
    board = new BoardImpl(game);  // makes an empty board
    centre = board.getMaxIndex() / 2;
  }
  
  /**
   * Test on an empty board
   */
  public void testEmptyBoard() {
    try {
      lookAheadTwo.getTurn(board);
      fail("Should be no turns on an empty board");
    }
    catch (IllegalStateException expected) {
      // Success
    }
  }

  /**
   * Tests that a bear can be rescued in one turn
   */
  public void testRescueBearInOneTurn() {
    int bearX = centre;
    int bearY = board.getMaxIndex();
    
    // Add the bear where it could be rescued
    board.addTile(new Bear(), bearX, bearY);
    board.flipTile(Team.HUMANS, bearX, bearY);
    
    // Ask the AI to find the best next (predator) turn
    Turn turn = lookAheadTwo.getTurn(board);
    
    // Check it's moving the bear one square towards the exit
    assertNotNull("Should find a turn", turn);
    assertEquals(Rescue.class, turn.getClass());
    Rescue rescue = (Rescue) turn;
    assertEquals(bearX, rescue.getFromX());
    assertEquals(bearY, rescue.getFromY());
    assertEquals(bearX, rescue.getToX());
    assertEquals(bearY + 1, rescue.getToY());
  }
  
  /**
   * Tests that a bear can be rescued in two turns
   */
  public void testRescueBearInTwoTurns() {
    int bearX = centre;
    int bearY = board.getMaxIndex() - 1;
    
    // Add the bear, one square away from where it could be rescued
    board.addTile(new Bear(), bearX, bearY);
    board.flipTile(Team.PREDATORS, bearX, bearY);
    // -- Add trees to restrict it
    board.addTile(new Tree(), bearX, bearY - 1);
    board.flipTile(Team.HUMANS, bearX, bearY - 1);
    board.addTile(new Tree(), bearX + 1, bearY);
    board.flipTile(Team.PREDATORS, bearX + 1, bearY);
    board.addTile(new Tree(), bearX - 1, bearY);
    board.flipTile(Team.HUMANS, bearX - 1, bearY);
    
    // Ask the AI to find the best next (predator) turn
    Turn turn = lookAheadTwo.getTurn(board);
    
    // Check it's moving the bear one square towards the exit
    assertNotNull("Should find a turn", turn);
    assertEquals(Move.class, turn.getClass());  // i.e. not a Rescue
    Move move = (Move) turn;
    assertEquals(bearX, move.getFromX());
    assertEquals(bearY, move.getFromY());
    assertEquals(bearX, move.getToX());
    assertEquals(bearY + 1, move.getToY());
  }
  
  /**
   * Tests that it's better to take two turns rescuing a bear than to eat a
   * duck on the first turn and get no points on the second turn
   */
  public void testRescueBearVsEatDuck() {
    int bearX = centre;
    int bearY = board.getMaxIndex() - 1;
    int foxX = 0;
    int foxY = 0;
    int duckX = foxX + 1;
    int duckY = foxY;
    
    // Add the bear, one square away from where it could be rescued
    board.addTile(new Bear(), bearX, bearY);
    board.flipTile(Team.HUMANS, bearX, bearY);
    // -- Add trees to restrict it
    board.addTile(new Tree(), bearX, bearY - 1);
    board.flipTile(Team.PREDATORS, bearX, bearY - 1);
    board.addTile(new Tree(), bearX + 1, bearY);
    board.flipTile(Team.HUMANS, bearX + 1, bearY);
    board.addTile(new Tree(), bearX - 1, bearY);
    board.flipTile(Team.PREDATORS, bearX - 1, bearY);
    
    // Add the fox where it can't be rescued
    board.addTile(new Fox(), foxX, foxY);
    board.flipTile(Team.HUMANS, foxX, foxY);
    // -- Add a tree to restrict it
    board.addTile(new Tree(), foxX, foxY + 1);
    board.flipTile(Team.PREDATORS, foxX, foxY + 1); // tree
    
    // Add the duck where the fox can eat it in one move and the human player
    // can't use it to obstruct the bear
    board.addTile(new Duck(), duckX, duckY);
    board.flipTile(Team.HUMANS, duckX, duckY);
    // -- Add trees to restrict it
    board.addTile(new Tree(), duckX, duckY + 1);
    board.flipTile(Team.PREDATORS, duckX, duckY + 1); // tree
    board.addTile(new Tree(), duckX + 1, duckY);
    board.flipTile(Team.HUMANS, duckX + 1, duckY); // tree
    
    // Ask the AI to find the best next (predator) turn
    Turn turn = lookAheadTwo.getTurn(board);
    
    // Check it's moving the bear one square towards the exit
    assertNotNull("Should find a turn", turn);
    assertEquals(Move.class, turn.getClass());  // i.e. not a Rescue
    Move move = (Move) turn;
    assertEquals(bearX, move.getFromX());
    assertEquals(bearY, move.getFromY());
    assertEquals(bearX, move.getToX());
    assertEquals(bearY + 1, move.getToY());
  }
}
