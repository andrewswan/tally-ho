/*
 * Created on 19/09/2004
 */
package tallyho.model.turn;

import tallyho.model.Board;
import tallyho.model.Team;

/**
 * Represents a turn in which a tile is moved from one location on the board
 * to another. Not for turns in which a tile is rescued by moving it off the
 * board.
 */
public class Move extends AbstractTurn {

  // Properties
  private final int fromX, fromY, toX, toY;
  
  /**
   * Constructor
   * 
   * @param team
   * @param fromX
   * @param fromY
   * @param toX
   * @param toY
   * @param score the points scored by this move
   */
  public Move(Team team, int fromX, int fromY, int toX, int toY, int score) {
    super(team, score);
    this.fromX = fromX;
    this.fromY = fromY;
    this.toX = toX;
    this.toY = toY;
  }

  /**
   * Reports whether the given Move is the opposite of this Move
   * 
   * @param otherMove can be null
   * @return false if the given Move is null
   */
  public boolean reverses(Move otherMove) {
    if (otherMove == null) {
      return false;
    }

    return fromX == otherMove.toX && fromY == otherMove.toY
      && toX == otherMove.fromX && toY == otherMove.fromY;
  }
  
  /**
   * @return Returns the fromX.
   */
  public int getFromX() {
    return fromX;
  }
  
  /**
   * @return Returns the fromY.
   */
  public int getFromY() {
    return fromY;
  }
  
  /**
   * @return Returns the toX.
   */
  public int getToX() {
    return toX;
  }
  
  /**
   * @return Returns the toY.
   */
  public int getToY() {
    return toY;
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
    text.append(" move: ");
    text.append(fromX);
    text.append(",");
    text.append(fromY);
    text.append(" > ");
    text.append(toX);
    text.append(",");
    text.append(toY);
    if (getScore() > 0) {
      text.append(" (");
      text.append(getScore());
      text.append(")");
    }
    return text.toString();
  }
  
  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object object) {
    if (object == null)
      return false;
    
    if (!(object instanceof Move))
      return false;
    
    Move other = (Move) object;
    
    // Two moves are the same if they are to & from the same points
    return other.fromX == fromX && other.fromY == fromY
      && other.toX == toX && other.toY == toY;
  }
  
  /**
   * Ensures that unequal Moves have different hash codes
   * 
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    int hashCode = fromX;
    hashCode += fromY * Board.MAX_SIZE;
    hashCode += toX * Board.MAX_SIZE * Board.MAX_SIZE;
    hashCode += toY * Board.MAX_SIZE * Board.MAX_SIZE * Board.MAX_SIZE;
    return hashCode;
  }
}
