/*
 * Created on 19/09/2004
 */
package tallyho.model.turn;

import tallyho.model.Team;

/**
 * Convenience implementation of a Turn
 */
public class AbstractTurn implements Turn {

  // Properties
  private final int score;
  private final Team team;
  
  /**
   * Constructor
   * 
   * @param team the team who made the turn
   * @param score
   */
  public AbstractTurn(Team team, int score) {
    this.team = team;
    this.score = score;
  }

  /**
   * @see tallyho.model.turn.Turn#getTeam()
   */
  public Team getTeam() {
    return team;
  }

  /**
   * @return Returns the score.
   */
  public int getScore() {
    return score;
  }
}
