/*
 * Created on 18/08/2004
 */
package tallyho.model.tile;

/**
 * Model for a fox in Tally Ho
 */
public final class Fox extends Predator {

  /**
   * Constructor
   */
  public Fox() {
    super();
  }

  /**
   * @see tallyho.model.tile.Tile#getRange()
   */
  public int getRange() {
      return Integer.MAX_VALUE;
  }

  /**
   * @see tallyho.model.tile.Tile#getValue()
   */
  public int getValue() {
      return 5;
  }

  /**
   * @see tallyho.model.tile.Tile#getPrey()
   */
  public Class[] getPrey() {
      Class[] prey = new Class[1];
      prey[0] = Bird.class;
      return prey;
  }

  /**
   * @see tallyho.model.tile.Tile#canCapture(tallyho.model.tile.Tile)
   */
  public boolean canCapture(Tile otherTile) {
      // Foxes can only capture Birds
      return otherTile instanceof Bird;
  }
}
