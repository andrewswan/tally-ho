/*
 * Created on 21/08/2004
 */
package tallyho.model.player;

import tallyho.model.Team;

/**
 * Represents a player in the game of Tally Ho
 */
public interface Player {

  // ------------------------------- Constants --------------------------------
  /**
   * The maximum length of a player's name
   */
  int MAX_NAME_LENGTH = 20;
  
  /**
   * Argument passed to Observers when this player's score changes
   */
  String SCORE = "Score";

  /**
   * Argument passed to Observers when this player's team changes
   */
  String TEAM = "Team";

  // Methods
  
  /**
   * Adds the given number of points to the player's score
   * 
   * @param points must be greater than zero
   */
  void addToScore(int points);
  
  /**
   * Returns this player's name
   * 
   * @return a non-null String of up to MAX_NAME_LENGTH characters
   */
  String getName();
  
  /**
   * Sets this player's name
   * 
   * @param name a non-null String of up to MAX_NAME_LENGTH characters
   */
  void setName(String name);
  
  /**
   * Returns this player's current score
   * 
   * @return a number from zero to the maximum possible score
   */
  int getScore();
  
  /**
   * Returns the team for which this player is currently playing.
   * Note this changes during the course of a game (i.e. per round).
   * 
   * @return one of Team.HUMANS or Team.PREDATORS
   */
  Team getTeam();
  
  /**
   * Sets the team for which this player is currently playing.
   * 
   * @param team one of Team.HUMANS or Team.PREDATORS
   */
  void setTeam(Team team);
  
  /**
   * Returns this player's type (e.g. "Dumb AI")
   * 
   * @return see above
   */
  String getType();
}