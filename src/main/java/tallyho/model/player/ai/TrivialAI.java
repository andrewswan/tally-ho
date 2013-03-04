/*
 * Created on 22/08/2004
 */
package tallyho.model.player.ai;

import tallyho.model.Board;
import tallyho.model.player.AbstractPlayer;
import tallyho.model.turn.Flip;
import tallyho.model.turn.Turn;

/**
 * A trivial implementation of an AI for Tally Ho. Flips tiles if there are any
 * face-down, then makes a random move of the first movable tile it finds.
 */
public class TrivialAI extends AbstractPlayer implements ComputerPlayer {

  /**
   * Constructor - parameterless to enable instantiation using Reflection
   */
  public TrivialAI() {
    super();
  }

  /**
   * @see tallyho.model.player.ai.ComputerPlayer#getTurn(tallyho.model.BoardImpl)
   */
  public Turn getTurn(Board board) {
    // First try to flip
    Flip firstFlip = AIHelper.getFirstFlip(board);
    if (firstFlip != null) {
      return firstFlip;
    }

    // Try to move a tile (or pass if there are no moves)
    return AIHelper.getRandomMoveOfFirstMovableTile(board, getTeam());
  }

  /**
   * @see tallyho.model.player.Player#getType()
   */
  public String getType() {
    return "Trivial AI";
  }
}
