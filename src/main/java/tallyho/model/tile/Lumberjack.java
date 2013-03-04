/*
 * Created on 18/08/2004
 */
package tallyho.model.tile;

/**
 * Model for a lumberjack (woodsman) in Tally Ho
 */
public final class Lumberjack extends Human {

	/**
	 * Constructor
	 */
	public Lumberjack() {
    super();
	}

	/**
	 * @see tallyho.model.tile.Tile#getRange()
	 */
	public int getRange() {
		return 1;
	}

	/**
	 * @see tallyho.model.tile.Tile#getPrey()
	 */
	public Class[] getPrey() {
        Class[] prey = new Class[1];
        prey[0] = Tree.class;
		return prey;
	}

  /**
   * @see tallyho.model.tile.Tile#canCapture(tallyho.model.tile.Tile)
   */
  public boolean canCapture(Tile otherTile) {
    // Lumberjacks can only capture trees
    return otherTile instanceof Tree;
  }
}
