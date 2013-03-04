/**
 * Created on 18/08/2004
 */
package tallyho.model.tile;

import tallyho.model.Team;

/**
 * Represents a tile placed on the game board
 */
public interface Tile {

  /**
   * Reports whether this Tile can legally capture the given Tile,
   * purely based on its Class
   *  
   * @param otherTile can be <code>null</code>
   * @return false if the given tile is null
   */
  boolean canCapture(Tile otherTile);
    
  /**
   * Returns the display name of this tile
   * 
   * @return a non-null String
   */
  String getName();
  
	/**
	 * Returns the types of tile that this tile can "eat"
	 * 
	 * @return an array of Classes that implement the Tile interface
	 */
	Class[] getPrey();
    
  /**
   * Returns the movement range of this tile, in squares
   * 
   * @return a number from 0 to Integer.MAX_VALUE (meaning no limit)
   */
  int getRange();
    
  /**
   * Returns the team this tile belongs to
   * 
   * @return a non-<code>null</code> Team
   */
  Team getTeam();

  /**
   * Returns the point value of this tile
   * 
   * @return see above
   */
  int getValue();
    
  /**
   * Reports whether the tile is face up (otherwise face-down)
   * 
   * @return true if the tile is not on the board
   */
  boolean isFaceUp();
   
  /**
   * If this tile is face down, turns it face up
   */
  void setFaceUp();
}