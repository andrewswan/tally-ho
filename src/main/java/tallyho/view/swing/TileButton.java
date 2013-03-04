/*
 * Created on 24/08/2004
 */
package tallyho.view.swing;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.Border;

import tallyho.model.Game;
import tallyho.model.Team;
import tallyho.model.player.RealPlayer;
import tallyho.model.tile.Bear;
import tallyho.model.tile.Directional;
import tallyho.model.tile.Duck;
import tallyho.model.tile.Fox;
import tallyho.model.tile.Human;
import tallyho.model.tile.Hunter;
import tallyho.model.tile.Lumberjack;
import tallyho.model.tile.Pheasant;
import tallyho.model.tile.Tile;
import tallyho.model.tile.Tree;

/**
 * Generic view for a Tile
 */
public class TileButton extends JButton implements Observer {

  // Constants
  private static final Icon
    ICON_BEAR,
    ICON_DUCK,
    ICON_FACE_DOWN,
    ICON_FOX,
    ICON_HUNTER_UP,
    ICON_HUNTER_RIGHT,
    ICON_HUNTER_DOWN,
    ICON_HUNTER_LEFT,
    ICON_LUMBERJACK,
    ICON_PHEASANT,
    ICON_TREE;

  private static final AudioClip
    AUDIO_AAGH,
    AUDIO_BEAR,
    AUDIO_DUCK,
    AUDIO_FOX,
    AUDIO_PHEASANT,
    AUDIO_RIFLE,
    AUDIO_TREE,
    AUDIO_WALK;

  static {
    // Load the tile images
    ICON_BEAR = Utils.getImageIcon("images/bear.jpg", "A bear");
    ICON_DUCK = Utils.getImageIcon("images/duck.jpg", "A duck");
    ICON_FACE_DOWN = Utils.getImageIcon("images/face_down.jpg", "A face-down tile");
    ICON_FOX = Utils.getImageIcon("images/fox.jpg", "A fox");
    ICON_HUNTER_UP = Utils.getImageIcon("images/hunter_up.jpg", "A hunter pointing up");
    ICON_HUNTER_RIGHT = Utils.getImageIcon("images/hunter_right.jpg", "A hunter pointing right");
    ICON_HUNTER_DOWN = Utils.getImageIcon("images/hunter_down.jpg", "A hunter pointing down");
    ICON_HUNTER_LEFT = Utils.getImageIcon("images/hunter_left.jpg", "A hunter pointing left");
    ICON_LUMBERJACK = Utils.getImageIcon("images/lumberjack.jpg", "A lumberjack");
    ICON_PHEASANT = Utils.getImageIcon("images/pheasant.jpg", "A pheasant");
    ICON_TREE = Utils.getImageIcon("images/tree.jpg", "A tree");
    try {
      // Load the sounds
      AUDIO_AAGH = Applet.newAudioClip(new URL(GameFrame.SOUNDS_URL, "aah.au"));
      AUDIO_BEAR = Applet.newAudioClip(new URL(GameFrame.SOUNDS_URL, "bear.wav"));
      AUDIO_DUCK = Applet.newAudioClip(new URL(GameFrame.SOUNDS_URL, "duck.au"));
      AUDIO_FOX = Applet.newAudioClip(new URL(GameFrame.SOUNDS_URL, "fox.au"));
      AUDIO_PHEASANT = Applet.newAudioClip(new URL(GameFrame.SOUNDS_URL, "flapping.wav"));
      AUDIO_RIFLE = Applet.newAudioClip(new URL(GameFrame.SOUNDS_URL, "rifle.wav"));
      AUDIO_TREE = Applet.newAudioClip(new URL(GameFrame.SOUNDS_URL, "sawing.wav"));
      AUDIO_WALK = Applet.newAudioClip(new URL(GameFrame.SOUNDS_URL, "walking.wav"));
    }
    catch (MalformedURLException ex) {
      // Sound file couldn't be found
      throw new RuntimeException(ex);
    }
  }

  /* package */ static final Color
    COLOR_HUMANS = new Color(175, 130, 60),     // tan
    COLOR_NEUTRAL = new Color(35, 120, 25),     // dark green
    COLOR_PREDATORS = new Color(150, 210, 250), // sky blue
    COLOR_FACE_DOWN = new Color(35, 120, 25, 120);  // same as COLOR_NEUTRAL but a bit transparent
  
  // Properties
  private Tile tile;
  private final int boardX, boardY; 
  private final GameFrame gameFrame;

  /**
   * Constructor
   * 
   * @param game can't be <code>null</code>
   * @param gameFrame can't be <code>null</code>
   * @param xPos the x-coordinate of the given Tile 
   * @param yPos the y-coordinate of the given Tile
   */
  public TileButton(Game game, GameFrame gameFrame, int xPos, int yPos) {
    if (game == null)
      throw new IllegalArgumentException("Game can't be null");
    if (gameFrame == null)
      throw new IllegalArgumentException("Game frame can't be null");
    
    this.gameFrame = gameFrame;
    this.tile = game.getBoard().getTile(xPos, yPos); // might be null
    this.boardX = xPos;
    this.boardY = yPos;
    game.addObserver(this); // ensure we are notified if the game state changes
    
    initialiseGUI(game);
  }

  /**
   * Sets the static graphical properties of this object
   * 
   * @param game
   */
  private void initialiseGUI(Game game) {
    setEnabled(game.getActivePlayer() instanceof RealPlayer);
    setPreferredSize(new Dimension(ICON_BEAR.getIconWidth(), ICON_BEAR.getIconHeight()));
    setToolTipText("");
  }

  /**
   * Plays the audio clip associated with the current tile. Plays no sound if
   * the board square is empty.
   * 
   * @param capturing whether the tile whose sound is being played is capturing
   *   another tile
   */
  void playSound(boolean capturing) {
    if (tile != null) {
      if (tile instanceof Bear)
        AUDIO_BEAR.play();
      else if (tile instanceof Duck)
        AUDIO_DUCK.play();
      else if (tile instanceof Fox)
        AUDIO_FOX.play();
      else if (tile instanceof Hunter) {
        if (capturing)
          AUDIO_RIFLE.play();
        else
          AUDIO_WALK.play();
      }
      else if (tile instanceof Lumberjack && !capturing)
        AUDIO_WALK.play();
      else if (tile instanceof Pheasant)
        AUDIO_PHEASANT.play();
    }
  }
  
  /**
   * Plays the sound when a tile at this square is captured
   */
  public void playCapturedSound() {
    if (tile != null) {
      if (tile instanceof Bear) {
        AUDIO_BEAR.play();
      }
      else if (tile instanceof Duck) {
        AUDIO_DUCK.play();
      }
      else if (tile instanceof Fox) {
        AUDIO_FOX.play();
      }
      else if (tile instanceof Human) {
        AUDIO_AAGH.play();
      }
      else if (tile instanceof Pheasant) {
        AUDIO_PHEASANT.play();
      }
      else if (tile instanceof Tree) {
        AUDIO_TREE.play();
      }
    }
  }
  
  /**
   * Returns the icon to be shown on this TileButton
   * 
   * @see javax.swing.AbstractButton#getIcon()
   * @return <code>null</code> if this square of the board is empty
   */
  public Icon getIcon() {
    if (tile == null) {
      return null;
    }
    else if (!tile.isFaceUp()) {
      return ICON_FACE_DOWN;
    }
    else if (tile instanceof Bear) {
      return ICON_BEAR;
    }
    else if (tile instanceof Duck) {
      return ICON_DUCK;
    }
    else if (tile instanceof Fox) {
      return ICON_FOX;
    }
    else if (tile instanceof Hunter) {
      // Hunter's icon depends on direction
      return getHunterIcon((Hunter) tile);
    }
    else if (tile instanceof Lumberjack) {
      return ICON_LUMBERJACK;
    }
    else if (tile instanceof Pheasant) {
      return ICON_PHEASANT;
    }
    else if (tile instanceof Tree) {
      return ICON_TREE;
    }
    else {
      throw new IllegalStateException(
          "Unknown tile class " + tile.getClass().getName());
    }
  }

  /**
   * Returns the icon to be shown for the given hunter, based
   * on the direction in which he is facing
   * 
   * @param hunter can't be <code>null</code>
   * @return see above
   */
  private Icon getHunterIcon(Hunter hunter) {
    switch (hunter.getDirection()) {
      case Directional.BIG_X:
        return ICON_HUNTER_RIGHT;
      case Directional.BIG_Y:
        return ICON_HUNTER_DOWN;
      case Directional.SMALL_X:
        return ICON_HUNTER_LEFT;
      case Directional.SMALL_Y:
        return ICON_HUNTER_UP;
      default:
        throw new IllegalStateException(
            "Unknown hunter direction " + hunter.getDirection());
    }
  }

  /**
   * Returns the background colour for this icon
   * 
   * @see java.awt.Component#getBackground()
   * @return see above
   */
  public Color getBackground() {
    if (tile == null) {
      return BoardPanel.COLOR_BOARD;
    }
    else if (!tile.isFaceUp()) {
      return COLOR_FACE_DOWN; 
    }
    else if (Team.HUMANS.equals(tile.getTeam())) {
      return COLOR_HUMANS;
    }
    else if (Team.NEUTRAL.equals(tile.getTeam())) {
      return COLOR_NEUTRAL;
    }
    else if (Team.PREDATORS.equals(tile.getTeam())) {
      return COLOR_PREDATORS;
    }
    else {
      throw new IllegalStateException("Tile colour can't be determined");
    }
  }
  
  /** 
   * Returns the border for this TileButton
   * 
   * @see javax.swing.JComponent#getBorder()
   * @return see above
   */
  public Border getBorder() {
    if (isSelected())
      return Utils.BORDER_BLACK_LINE_2;
    
    return Utils.BORDER_RAISED_BEVEL;
  }
  
  /**
   * @see java.awt.Component#isVisible()
   */
  public boolean isVisible() {
    return tile != null;
  }
  
  Point2D getBoardPosition() {
    return new Point(boardX, boardY);
  }
  
  /**
   * Returns the x-coordinate of this button on the board
   * 
   * @return a zero-indexed number
   */
  int getBoardX() {
    return boardX;
  }
  
  /**
   * Returns the y-coordinate of this button on the board
   * 
   * @return a zero-indexed number
   */
  int getBoardY() {
    return boardY;
  }
  
  /**
   * Sets the tile with which this button is associated
   * 
   * @param tile The tile to set - can be <code>null</code>,
   *   indicating this square of the board is empty
   */
  void setTile(Tile tile) {
    this.tile = tile;
    // Update the appearance of this button
    setBackground(getBackground());
    Icon icon = getIcon();
    setIcon(icon);
    setDisabledIcon(icon);
    setToolTipText(getToolTipText());
    setVisible(isVisible());
    paintComponent(getGraphics());
    validate();
  }
  
  /**
   * @see Object#equals(java.lang.Object)
   */
  public boolean equals(Object other) {
    if (other == null)
      return false;
    else if (!(other instanceof TileButton))
      return false;
    else
      return ((TileButton) other).getBoardPosition().equals(getBoardPosition());
  }

  /**
   * Returns the Tile with which this button is associated
   * @return see above, can be <code>null</code> if no Tile is present
   */
  public Tile getTile() {
    return tile;
  }
  
  /**
   * For debugging
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuffer text = new StringBuffer();
    text.append(getClass().getName());
    text.append(": posn=").append(getBoardPosition());
    text.append(", visible=").append(isVisible());
    text.append(", tile=").append(tile);
    text.append(", icon=").append(getIcon());
    text.append(", bkgrd=").append(getBackground());
    return text.toString();
  }
  
  /**
   * @see javax.swing.JComponent#getToolTipText(MouseEvent)
   */
  public String getToolTipText(MouseEvent event) {
    if (tile == null || !tile.isFaceUp() || !gameFrame.showTileTips()) {
      return null;
    }
    StringBuffer text = new StringBuffer("<html>");
    // Name (type)
    text.append("<b>").append(tile.getName()).append("</b>");
    // Team
    text.append("<br>Team: ");
    switch (tile.getTeam()) {
      case HUMANS:
        text.append("Humans");
        break;
      case PREDATORS:
        text.append("Predators");
        break;
      case NEUTRAL:
        text.append("Neutral");
    }
    text.append("<br>Range: ");
    // Range
    if (tile.getRange() == Integer.MAX_VALUE)
      text.append("Unlimited");
    else
      text.append(tile.getRange());
    // Prey
    text.append("<br>Prey: ");
    Class[] preyClasses = tile.getPrey();
    if (preyClasses == null || preyClasses.length == 0) {
      text.append("None");
    }
    else {
      for (int i = 0; i < preyClasses.length; i++) {
        String className = preyClasses[i].getName();
        text.append(className.substring(className.lastIndexOf('.') + 1));
        text.append("s");
        if (i < preyClasses.length - 1)
          text.append(", ");
      }
    }
    // Value
    text.append("<br>Value: ").append(tile.getValue());
    // End
    text.append("</html>");
    return text.toString();
  }

  /**
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   * @param observable the object reporting a change
   * @param change its changed property
   */
  public void update(Observable observable, Object change) {
    System.out.println("Updating button " + boardX + "," + boardY);
    if (observable instanceof Game && Game.ACTIVE_PLAYER.equals(change)) {
      // The active player changed
      Game game = (Game) observable;
      // Enable this button if it's a real person's turn
      setEnabled(game.getActivePlayer() instanceof RealPlayer);
    }
    else if (observable instanceof Game && Game.NEW_ROUND.equals(change)) {
      // A new round has started
      Game game = (Game) observable;
      setTile(game.getBoard().getTile(getBoardPosition()));
      // Enable this button if it's a real person's turn
      setEnabled(game.getActivePlayer() instanceof RealPlayer);
    }
  }

  /**
   * @see javax.swing.AbstractButton#getDisabledIcon()
   */
  public Icon getDisabledIcon() {
    // TODO think about disabling the whole game panel
    return getIcon();
  }
}