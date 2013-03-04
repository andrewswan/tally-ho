/*
 * Created on 26/09/2004
 */
package tallyho.view.swing;

import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.JButton;

import tallyho.model.Game;
import tallyho.model.tile.Directional;

/**
 * A button that allows a tile to exit the board
 */
public class ExitButton extends JButton implements Observer {
  
  // Constants
  private static final Icon
    ICON_NORTH,
    ICON_SOUTH,
    ICON_EAST,
    ICON_WEST;
  
  static {
    // Load the tile images
    ICON_NORTH = Utils.getImageIcon("images/arrow_north.jpg", "Arrow north");
    ICON_SOUTH = Utils.getImageIcon("images/arrow_south.jpg", "Arrow south");
    ICON_EAST = Utils.getImageIcon("images/arrow_east.jpg", "Arrow east");
    ICON_WEST = Utils.getImageIcon("images/arrow_west.jpg", "Arrow west");
  }
  
  // Properties
  private final int direction;

  /**
   * Constructor
   * 
   * @param game can't be <code>null</code>
   * @param direction the direction of this button from the middle of the
   *   board, i.e. if it had an arrow, the direction it would point.
   */
  ExitButton(Game game, int direction) {
    // Check the inputs
    if (direction != Directional.BIG_X && direction != Directional.BIG_Y
        && direction != Directional.SMALL_X && direction != Directional.SMALL_Y)
    {
      throw new IllegalArgumentException("Invalid direction: " + direction);
    }
    if (game == null)
      throw new IllegalArgumentException("Game can't be null");

    // Store the direction
    this.direction = direction;
    
    // Make sure we are notified of changes in the game model
    game.addObserver(this);
    
    // Set the graphical appearance
    initialiseGUI();
  }
  
  /**
   * Sets the graphical properties of this ExitButton
   */
  private void initialiseGUI() {
    setBackground(BoardPanel.COLOR_BOARD);
    Icon icon = getIcon();
    setIcon(null);
    setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    setEnabled(false);
    setToolTipText("Once the end-game has begun, you can move your own tiles" +
        " over these arrows to score points.");
  }

  /**
   * Returns the icon for this button, purely based on its direction, not
   * whether it's enabled or not.
   * 
   * @return see above
   */
  public Icon getIcon() {
    if (!isEnabled())
      return null;

    // Enabled - set the icon based on the direction
    switch (direction) {
      case Directional.BIG_X:
        return ICON_EAST;
      case Directional.BIG_Y:
        return ICON_SOUTH;
      case Directional.SMALL_X:
        return ICON_WEST;
      case Directional.SMALL_Y:
        return ICON_NORTH;
      default:
        throw new IllegalStateException("Invalid direction: " + direction);
    }
  }
  
  /**
   * @return Returns the direction of this button
   */
  int getDirection() {
    return direction;
  }

  /**
   * Handles a change in an observed object (e.g. the game model)
   * 
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  public void update(Observable observable, Object change) {
    if (observable instanceof Game && Game.END_GAME_STARTED.equals(change)) {
      // The end-game has started
      setEnabled(true);
    }
    else if (observable instanceof Game && Game.NEW_ROUND.equals(change)) {
      // A new round has started
      setEnabled(false);
    }
  }
}