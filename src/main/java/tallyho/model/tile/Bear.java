/*
 * Created on 18/08/2004
 */
package tallyho.model.tile;

/**
 * Implements a Bear in the game of Tally Ho
 */
public final class Bear extends Predator {

	/**
   * Constructor
	 */
	public Bear() {
    super();
	}

	/**
	 * @see tallyho.model.tile.Tile#getRange()
	 */
	public int getRange() {
		return 1;
	}

	/**
	 * @see tallyho.model.tile.Tile#getValue()
	 */
	public int getValue() {
		return 10;
	}

	/**
	 * @see tallyho.model.tile.Tile#getPrey()
	 */
	public Class[] getPrey() {
		Class[] prey = new Class[1];
		prey[0] = Human.class;
		return prey;
	}

  /**
   * @see tallyho.model.tile.Tile#canCapture(tallyho.model.tile.Tile)
   */
  public boolean canCapture(Tile otherTile) {
    // Bears can only capture Humans
    return otherTile instanceof Human;
  }
}