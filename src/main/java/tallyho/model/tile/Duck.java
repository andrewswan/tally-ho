/*
 * Created on 18/08/2004
 */
package tallyho.model.tile;

/**
 * @author AS
 */
public final class Duck extends Bird {

  /**
   * Constructor
   */
  public Duck() {
    super();
  }

  /**
   * @see tallyho.model.tile.Tile#getValue()
   */
  public int getValue() {
    return 2;
  }
}