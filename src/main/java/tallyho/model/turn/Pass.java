/*
 * Created on 19/09/2004
 */
package tallyho.model.turn;

import tallyho.model.Team;

/**
 * Represents a turn that consists of passing
 */
public class Pass extends AbstractTurn {

  /**
   * Constructor
   * 
   * @param team
   */
  public Pass(Team team) {
    super(team, 0);
  }
  
  /**
   * For debugging
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return "Pass";
  }
}
