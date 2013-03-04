/*
 * Created on 25/09/2004
 */
package tallyho.model.player.ai;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import tallyho.model.turn.Turn;

/**
 * Represents a sequence of turns in TallyHo
 */
public class Path implements Cloneable {

  // Properties
  private List<Turn> turns;
  
  /**
   * Constructor
   * 
   * @param firstTurn can't be <code>null</code>
   */
  public Path(Turn firstTurn) {
    if (firstTurn == null)
      throw new IllegalArgumentException("First turn can't be null");
    
    turns = new ArrayList<Turn>();
    turns.add(firstTurn);
  }
  
  /**
   * Adds the given turn to the end of this path
   * 
   * @param turn can be <code>null</code>
   */
  public void addTurn(Turn turn) {
    turns.add(turn);
  }
  
  /**
   * Returns the first turn in this path
   * 
   * @return a non-<code>null</code> Turn
   */
  public Turn getFirstTurn() {
    return turns.get(0);
  }
  
  /**
   * Returns the point value of this path for the player making the first turn
   * 
   * @return see above
   */
  public int getScore() {
    int score = 0;
    for (int i = 0; i < turns.size(); i++) {
      Turn turn = turns.get(i);
      if (turn != null) {
        if (i % 2 == 0) {
          // This player's turn
          score += turn.getScore();
        }
        else {
          score -= turn.getScore();
        }
      }
    }
    return score;
  }
  
  /**
   * Returns the length of this path, in turns
   * 
   * @return see above
   */
  public int getLength() {
    return turns.size();
  }
  
  /**
   * @see java.lang.Object#clone()
   */
  protected Object clone() throws CloneNotSupportedException {
    // Clone the basic properties
    Path clone = (Path) super.clone();
    
    // Clone the List of turns
    clone.turns = new ArrayList<Turn>();
    clone.turns.addAll(turns);
    
    return clone;
  }
  
  /**
   * For debugging
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuffer text = new StringBuffer();
    for (Iterator iter = turns.iterator(); iter.hasNext();) {
      text.append(iter.next());
      if (iter.hasNext())
        text.append(" -> ");
    }
    text.append(" [");
    text.append(getScore());
    text.append("]");
    return text.toString();
  }
}
