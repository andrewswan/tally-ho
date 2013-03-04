/*
 * Created on 25/08/2004
 */
package tallyho.view.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import tallyho.model.Game;
import tallyho.model.Team;
import tallyho.model.player.Player;

/**
 * The label showing a player's name
 */
public class PlayerNameLabel extends JLabel implements Observer {

  // Constants
  private static final int
    FONT_POINT_SIZE = 15,
    FONT_STYLE = Font.BOLD,
    HEIGHT = 50,
    WIDTH = 115;  // wide enough so moves can be shown in the PlayerMovesPane
  
  private static final Border
    BORDER_COMPOUND = BorderFactory.createCompoundBorder(
        Utils.BORDER_BLACK_LINE_2, Utils.BORDER_EMPTY_10);

  // Properties
  private final Player player; // remember which player we are for
  
  /**
   * Constructor
   * 
   * @param game can't be <code>null</code>
   * @param player can't be <code>null</code>
   */
  public PlayerNameLabel(Game game, Player player) {
    // Check inputs
    if (game == null) {
      throw new IllegalArgumentException("Game can't be null");
    }
    if (player == null) {
      throw new IllegalArgumentException("Player can't be null");
    }
    this.player = player;
    
    // Make sure we find out about relevant changes in the model
    game.addObserver(this);
    ((Observable) player).addObserver(this);
    
    // Set constant GUI attributes
    initialiseGUI(game);
  }
  
  /**
   * Sets the graphical properties of this label
   * 
   * @param game
   */
  private void initialiseGUI(Game game) {
    setBorder(game.getActivePlayer().equals(player));
    setFont(new Font(null, FONT_STYLE, FONT_POINT_SIZE));
    setHorizontalAlignment(SwingConstants.CENTER);
    setText(player);
    setPreferredSize(new Dimension(WIDTH, HEIGHT));
  }
  
  /**
   * Returns the text for this label based on the given player data
   * 
   * @param player
   */
  void setText(Player player) {
    StringBuffer text = new StringBuffer("<html>");
    // Name
    if (player.getName() != null) {
      text.append(player.getName());
    }
    // Team
    if (Team.HUMANS.equals(player.getTeam())) {
      text.append("<br>(Humans)");
    }
    else if (Team.PREDATORS.equals(player.getTeam())) {
      text.append("<br>(Predators)");
    }
    text.append("</html>");
    setText(text.toString());
  }
    
  /**
   * Returns the border for this label, based on whether this player is the
   * active player in the game
   * 
   * @param isActivePlayer
   */
  private void setBorder(boolean isActivePlayer) {
    if (isActivePlayer) {
      setBorder(BORDER_COMPOUND);
    }
    else {
      setBorder(Utils.BORDER_EMPTY_10);
    }
  }
  
  /**
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   * @param observable the object reporting a change
   * @param propertyName the name of its changed property
   */
  public void update(Observable observable, Object propertyName) {
    if (observable instanceof Game &&
        (Game.ACTIVE_PLAYER.equals(propertyName) || Game.NEW_ROUND.equals(propertyName))) {
      // The active player changed or a new round started
      Game game = (Game) observable;
      setBorder(game.getActivePlayer().equals(player));
    }
    else if (observable instanceof Player && Player.TEAM.equals(propertyName)) {
      // The players changed teams
      setText(player);
    }
  }
}
