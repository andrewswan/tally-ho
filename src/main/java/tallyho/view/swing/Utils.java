/*
 * Created on 27/08/2004
 */
package tallyho.view.swing;

import java.awt.Color;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.border.Border;

/**
 * Handy utilities for Swing classes
 */
public class Utils {

  // GUI Constants
  static final Border
    BORDER_BLACK_LINE_2 = BorderFactory.createLineBorder(Color.BLACK, 2),
    BORDER_EMPTY_5 = BorderFactory.createEmptyBorder(5, 5, 5, 5),
    BORDER_EMPTY_10 = BorderFactory.createEmptyBorder(10, 10, 10, 10),
    BORDER_LOWERED_BEVEL = BorderFactory.createLoweredBevelBorder(),
    BORDER_RAISED_BEVEL = BorderFactory.createRaisedBevelBorder();
  
  /**
   * Centres the given java.awt.Container on the screen
   * 
   * @param container e.g. a JFrame or JDialog
   */
  static void centre(Container container) {
    Rectangle maxScreenSize =
      GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    container.setLocation((maxScreenSize.width - container.getWidth())/2,
        (maxScreenSize.height - container.getHeight())/2);
  }

  /**
   * Returns an ImageIcon, or null if the path was invalid.
   * 
   * @param path can't be <code>null</code> or whitespace, use
   *    forward slashes even on Windows systems
   * @param description
   * @return see above
   */
  static ImageIcon getImageIcon(String path, String description) {
    // Check path is valid
    if (path == null || path.trim().length() == 0) {
      throw new IllegalArgumentException("Invalid file path: " + path);
    }
    
    URL imgURL = ClassLoader.getSystemResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL, description);
    }
    
    throw new IllegalArgumentException("Couldn't find file: " + path);
  }
}