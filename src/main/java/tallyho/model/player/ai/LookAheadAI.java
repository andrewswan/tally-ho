/*
 * Created on 24/09/2004
 */
package tallyho.model.player.ai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tallyho.model.Board;
import tallyho.model.player.AbstractPlayer;
import tallyho.model.turn.Turn;

/**
 * An AI that looks ahead a given number of moves
 */
public class LookAheadAI extends AbstractPlayer implements ComputerPlayer {

  // Constants
  private static final int DEFAULT_LOOK_AHEAD_DEPTH = 2;
  private static final Log LOG = LogFactory.getLog(LookAheadAI.class);
  
  // Properties
  // -- the number of turns after the current one to look ahead
  private final int turnsToLookAhead;
  
  /**
   * Constructor that looks ahead the default number of turns
   */
  public LookAheadAI() {
    this(DEFAULT_LOOK_AHEAD_DEPTH);
  }
  
  /**
   * Constructor
   * 
   * @param turnsToLookAhead the number of turns to look ahead, including turns
   *   by the other player. Can't be negative.
   */
  public LookAheadAI(int turnsToLookAhead) {
    if (turnsToLookAhead < 0) {
      throw new IllegalArgumentException("Can't look ahead a negative number of turns");
    }
    
    this.turnsToLookAhead = turnsToLookAhead;
    LOG.info("Constructed look-ahead AI with depth of " + turnsToLookAhead);
  }

  /*
   * @see tallyho.model.player.Player#getType()
   */
  public String getType() {
    return "Look-Ahead AI";
  }
  
  /*
   * @see tallyho.model.player.ai.ComputerPlayer
   *   #getTurn(tallyho.model.BoardImpl)
   */
  public Turn getTurn(Board board) {
    LOG.info(">>> Getting turn for board:\n\n" + board);
    // Get a min/max tree of the possible moves
    MinMaxTree possibleMoves =
      new MinMaxTreeImpl(getTeam(), board, turnsToLookAhead);
    return possibleMoves.getBestTurn();
  }
}
