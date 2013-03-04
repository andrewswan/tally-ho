/*
 * Created on 18/08/2004
 */
package tallyho.model.tile;

/**
 * Default implementation of a Tile
 */
public abstract class AbstractTile implements Tile, Cloneable {

	// Properties
	private boolean isFaceUp;	

  /**
   * Constructor
   */
	public AbstractTile() {
		isFaceUp = false;	// tiles are initially face-down
	}
	
	/**
	 * @see tallyho.model.tile.Tile#isFaceUp()
	 */
	public boolean isFaceUp() {
    return isFaceUp;
	}
    
  /**
   * @see tallyho.model.tile.Tile#setFaceUp()
   */
  public void setFaceUp() {
    isFaceUp = true;
  }
  
  /**
   * Returns the display name of this tile
   * 
   * @return a non-null String
   */
  public String getName() {
    String className = getClass().getName(); 
    int lastDot = className.lastIndexOf('.');
    return className.substring(lastDot + 1);
  }
  
  /**
   * @see java.lang.Object#clone()
   */
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
