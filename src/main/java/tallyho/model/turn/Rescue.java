/*
 * Created on 19/09/2004
 */
package tallyho.model.turn;

import tallyho.model.Board;
import tallyho.model.Team;

/**
 * A move that involves a tile moving off the board (being rescued)
 */
public class Rescue extends Move {
  
  /**
   * Constructor
   * 
   * @param team one of Team.HUMANS or Team.PREDATORS
   * @param fromX
   * @param fromY
   * @param toX
   * @param toY
   * @param score
   */
  public Rescue(Team team, int fromX, int fromY, int toX, int toY, int score) {
    super(team, fromX, fromY, toX, toY, score);
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object object) {
    if (object == null)
      return false;
    
    if (!(object instanceof Rescue))
      return false;
    
    Rescue other = (Rescue) object;
    
    // Two rescues are the same if they are from the same point
    return other.getFromX() == getFromX() && other.getFromY() == getFromY();
  }
  
  /**
   * Ensure that two unequal rescues have different hash codes
   * 
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return (getFromX() * Board.MAX_SIZE) + getFromY();
  }
  
  /**
   * For debugging
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuffer text = new StringBuffer();
    if (Team.HUMANS.equals(getTeam())) {
      text.append("H");
    }
    else {
      text.append("P");
    }
    text.append(" rescue: ");
    text.append(getFromX());
    text.append(",");
    text.append(getFromY());
    text.append(" (");
    text.append(getScore());
    text.append(")");
    return text.toString();
  }
}
