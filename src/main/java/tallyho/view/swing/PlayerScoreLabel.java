/*
 * Created on 27/08/2004
 */
package tallyho.view.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import tallyho.model.player.Player;

/**
 * The Swing representation of a player's score
 */
public class PlayerScoreLabel extends JLabel implements Observer {

  // Constants
  private static final int
    FONT_POINT_SIZE = 15,
    FONT_STYLE = Font.BOLD,
    HEIGHT = 40,
    WIDTH = 100;
  
  /**
   * Constructor
   * 
   * @param player can't be <code>null</code>, must extend Observable
   */
  public PlayerScoreLabel(Player player) {
    // Check inputs
    if (player == null || !(player instanceof Observable))
      throw new IllegalArgumentException("Player must be an Observable object");
    
    // Make sure we are notified of changes to this player
    ((Observable) player).addObserver(this);

    // Set GUI properties
    setBorder(Utils.BORDER_LOWERED_BEVEL);
    setFont(new Font(null, FONT_STYLE, FONT_POINT_SIZE));
    setHorizontalAlignment(SwingConstants.CENTER);
    setPreferredSize(new Dimension(WIDTH, HEIGHT));
    setText(getText(player.getScore()));
  }

  /**
   * Returns the displayable text for a given player score
   * 
   * @param score
   * @return non-<code>null</code> text
   */
  private String getText(int score) {
    StringBuffer text = new StringBuffer("Score: ");
    text.append(score);
    return text.toString();
  }

  /**
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   * @param observable the object reporting a change
   * @param propertyName the name of its changed property
   */
  public void update(Observable observable, Object propertyName) {
    if (observable instanceof Player && Player.SCORE.equals(propertyName)) {
      // A player is telling us their score changed
      Player player = (Player) observable;
      setText(getText(player.getScore()));
    }
  }
}
