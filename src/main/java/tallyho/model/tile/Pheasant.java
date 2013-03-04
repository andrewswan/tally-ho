/*
 * Created on 18/08/2004
 */
package tallyho.model.tile;

/**
 * Model for a pheasant in Tally Ho
 */
public final class Pheasant extends Bird {

  /**
   * Constructor
   */
  public Pheasant() {
    super();
  }

  /**
   * @see tallyho.model.tile.Tile#getValue()
   */
  public int getValue() {
    return 3;
  }
}
