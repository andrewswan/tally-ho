/*
 * Created on 22/08/2004
 */
package tallyho.model.player;

import java.util.Observable;

import tallyho.model.Team;

/**
 * Default implementation of a Player in the game of Tally Ho!
 */
public abstract class AbstractPlayer extends Observable implements Player {

  // Properties
  private String name;
  private int score;
  private Team team;
  
  /**
   * @see tallyho.model.player.Player#addToScore(int)
   */
  public void addToScore(int points) {
    if (points <= 0)
      throw new IllegalArgumentException("Points must be greater than zero.");
    score += points;
    setChanged();
    notifyObservers(SCORE);
  }
  
  /**
   * @see tallyho.model.player.Player#getName()
   */
  public String getName() {
    return name;
  }

  /**
   * @see tallyho.model.player.Player#setName(String)
   */
  public void setName(String name) {
    // Check input
    if (name == null || name.length() > MAX_NAME_LENGTH)
      throw new IllegalArgumentException("Invalid player name: " + name);
    this.name = name;
  }

  /**
   * @see tallyho.model.player.Player#getScore()
   */
  public int getScore() {
    return score;
  }
  
  /**
   * @see tallyho.model.player.Player#getTeam()
   */
  public Team getTeam() {
    return team;
  }
  
  /**
   * @see tallyho.model.player.Player#setTeam(Team)
   * @param team either Tile.TEAM_HUMANS or Tile.TEAM_PREDATORS
   */
  public void setTeam(Team team) {
    if (Team.NEUTRAL.equals(team)) {
      throw new IllegalArgumentException("A player can't be neutral.");
    }
    this.team = team;
    setChanged();
    notifyObservers(TEAM);
  }
}
