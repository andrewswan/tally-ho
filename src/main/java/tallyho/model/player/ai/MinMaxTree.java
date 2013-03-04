/*
 * Created on 19/07/2005
 */
package tallyho.model.player.ai;

import tallyho.model.turn.Turn;

/**
 * A tree of Turns starting from a given board position and extending to a
 * specified maximum depth
 * 
 * @author Andrew Swan
 */
public interface MinMaxTree {

  /**
   * Gets the best turn to make next
   * 
   * @return an immediate child of the root node
   */
  Turn getBestTurn();
}
