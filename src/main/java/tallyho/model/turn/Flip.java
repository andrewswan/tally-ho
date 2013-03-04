/*
 * Created on 19/09/2004
 */
package tallyho.model.turn;

import tallyho.model.Team;
import tallyho.model.tile.Tile;

/**
 * Represents a turn that consists of flipping a tile
 */
public class Flip extends AbstractTurn {

  // Properties
  private final int x, y;
  private final Tile tile;
  
  /**
   * Constructor
   * 
   * @param team can't be <code>null</code>
   * @param x the x-coordinate of the flipped tile, zero-indexed
   * @param y the y-coordinate of the flipped tile, zero-indexed
   * @param tile the tile being flipped
   */
  public Flip(Team team, double x, double y, Tile tile) {
    super(team, 0);
    this.x = (int) x;
    this.y = (int) y;
    this.tile = tile;
  }

  /**
   * @return Returns the x.
   */
  public int getX() {
    return x;
  }
  
  /**
   * @return Returns the y.
   */
  public int getY() {
    return y;
  }

  /**
   * @return Returns the tile being flipped
   */
  public Tile getTile() {
    return tile;
  }
  
  /**
   * For debugging
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuffer text = new StringBuffer("Flip: ");
    text.append(x);
    text.append(",");
    text.append(y);
    return text.toString();
  }
}
