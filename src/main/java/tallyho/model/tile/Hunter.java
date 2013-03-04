/*
 * Created on 18/08/2004
 */
package tallyho.model.tile;

/**
 * Implements the Hunter tile
 */
public final class Hunter extends Human implements Directional {

  // Properties
  private final int direction;
  
  /**
   * Constructor
   */
	public Hunter() {
    // Pick a random direction
    direction = ((int) (Math.random() * DIRECTIONS.length)) + 1;
	}

	/**
	 * @see tallyho.model.tile.Tile#getRange()
	 */
	public int getRange() {
		return Integer.MAX_VALUE;
	}

	/**
	 * @see tallyho.model.tile.Tile#getPrey()
	 */
	public Class[] getPrey() {
		Class[] prey = new Class[2];
    prey[0] = Bird.class;
		prey[1] = Predator.class;
		return prey;
	}

  /**
   * @see tallyho.model.tile.Tile#canCapture(tallyho.model.tile.Tile)
   */
  public boolean canCapture(Tile otherTile) {
    if (otherTile == null) {
      return false;
    }

    // Hunters can capture any predator or bird
    return (otherTile instanceof Predator) || (otherTile instanceof Bird);
  }

  /**
   * @see tallyho.model.tile.Directional#getDirection()
   */
  public int getDirection() {
    return direction;
  }
}
