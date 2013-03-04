/*
 * Created on 6/09/2004
 */
package tallyho.model.player.ai;

import tallyho.model.Board;
import tallyho.model.player.Player;
import tallyho.model.turn.Turn;

/**
 * Defines the behaviour of a computer player in Tally Ho
 */
public interface ComputerPlayer extends Player {

  /**
   * Returns the move that this AI wishes to make, given the board position and
   * the team for which they are playing.
   * 
   * @param board can't be <code>null</code>
   * @return a Turn that represents either a flip or a movement, or
   *   <code>null</code> if no turn is possible
   */
  Turn getTurn(Board board);
}
