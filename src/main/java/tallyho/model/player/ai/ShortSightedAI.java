/*
 * Created on 20/09/2004
 */
package tallyho.model.player.ai;

import java.util.Collections;
import java.util.List;

import tallyho.model.Board;
import tallyho.model.player.AbstractPlayer;
import tallyho.model.turn.Turn;

/**
 * An AI that simply finds the highest-scoring moves for the current board
 * position, and picks one of them at random.
 */
public class ShortSightedAI extends AbstractPlayer implements ComputerPlayer {

  /**
   * @see tallyho.model.player.ai.ComputerPlayer#getTurn(tallyho.model.BoardImpl)
   */
  public Turn getTurn(Board board) {
    // Go through all possible turns for this team, looking for the ones with
    // the biggest score, and choose one at random from them
    List highestScoringTurns =
      AIHelper.getHighestScoringTurns(board, getTeam());
    if (highestScoringTurns.isEmpty()) {
      // This can happen when we become the active player but have no turns
      return null;
    }

    // Return a random selection from the highest scoring turns
    // ComputerHelper.debugTurns(highestScoringTurns);
    Collections.shuffle(highestScoringTurns);
    return (Turn) highestScoringTurns.get(0);
  }

  /**
   * @see tallyho.model.player.Player#getType()
   */
  public String getType() {
    return "Short Sighted AI";
  }
}
