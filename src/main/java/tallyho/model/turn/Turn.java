/*
 * Created on 19/09/2004
 */
package tallyho.model.turn;

import tallyho.model.Team;

/**
 * Represents a player's turn in Tally Ho
 */
public interface Turn {

  /**
   * Returns the team who had the turn
   * 
   * @return a non-<code>null</code> Team
   */
  Team getTeam();
  
  /**
   * Returns the point value of this turn
   * 
   * @return see above
   */
  int getScore();
}
