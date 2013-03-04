/*
 * Created on 18/08/2004
 */
package tallyho.model.tile;

/**
 * Abstract implementation of a game bird in Tally Ho
 */
public abstract class Bird extends NeutralTile {

  /**
   * @see tallyho.model.tile.Tile#getRange()
   */
  public int getRange() {
    return Integer.MAX_VALUE;
  }
}
