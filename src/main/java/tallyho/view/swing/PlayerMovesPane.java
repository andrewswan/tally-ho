/*
 * Created on 12/09/2004
 */
package tallyho.view.swing;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextArea;

import tallyho.model.Board;
import tallyho.model.Game;
import tallyho.model.Team;
import tallyho.model.player.Player;
import tallyho.model.turn.Flip;
import tallyho.model.turn.Move;
import tallyho.model.turn.Pass;
import tallyho.model.turn.Rescue;
import tallyho.model.turn.Turn;

/**
 * A pane showing the moves that a player has made
 */
public class PlayerMovesPane extends JTextArea implements Observer {

  // Properties
  private final Player player;
  private int moveCount;
  
  /**
   * Constructor
   * 
   * @param game can't be <code>null</code>
   * @param player can't be <code>null</code>
   */
  public PlayerMovesPane(Game game, Player player) {
    // Check inputs
    if (game == null)
      throw new IllegalArgumentException("Game can't be null");
    if (player == null)
      throw new IllegalArgumentException("Player can't be null");
    
    this.player = player;
    
    // Make sure we're notified of relevant changes to the model
    game.addObserver(this);
    game.getBoard().addObserver(this);
    ((Observable) player).addObserver(this);

    initialiseGUI();
  }

  /**
   * Sets up the graphical properties of this label
   */
  private void initialiseGUI() {
    setBackground(player.getTeam());
    setEditable(false);
    setText("");
  }
  
  /**
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  public void update(Observable observable, Object change) {
    if (observable instanceof Board && change instanceof Turn) {
      // A turn was had - was it by our player's team?
      Turn turn = (Turn) change;
      if (turn.getTeam() == player.getTeam()) {
        if (turn instanceof Pass)
          addPassText();
        else if (turn instanceof Flip)
          addFlipText((Flip) turn);
        else
          addMovementText((Move) turn); // includes Rescues
      }
    }
    else if (observable instanceof Game && Game.NEW_ROUND.equals(change)) {
      // A new round started
      setText("");
      moveCount = 0;
    }
    else if (observable instanceof Player && Player.TEAM.equals(change)) {
      // The players changed teams
      setBackground(player.getTeam());
    }
  }

  /**
   * Adds a description of the given movement to this text area
   * 
   * @param move can't be <code>null</code>
   */
  private void addMovementText(Move move) {
    StringBuffer text = new StringBuffer(getText());
    text.append("\n ");
    text.append(moveCount++);
    text.append(": ");
    text.append(move.getFromX());
    text.append(",");
    text.append(move.getFromY());
    text.append(" > ");
    if (move instanceof Rescue) {
      text.append("off");
    }
    else {
      text.append(move.getToX());
      text.append(",");
      text.append(move.getToY());
    }
    if (move.getScore() > 0) {
      text.append(" (");
      text.append(move.getScore());
      text.append(")");
    }
    setText(text.toString());
  }

  /**
   * Adds text to this pane to show the given flip
   * 
   * @param flip can't be <code>null</code>
   */
  private void addFlipText(Flip flip) {
    if (flip == null)
      throw new IllegalArgumentException("Flip can't be null");
    
    StringBuffer text = new StringBuffer(getText());
    text.append("\n ");
    text.append(moveCount++);
    text.append(": ");
    text.append(flip.getX());
    text.append(",");
    text.append(flip.getY());
    text.append(" (");
    text.append(flip.getTile().getName());
    text.append(")");
    setText(text.toString());
  }

  /**
   * Adds text to this pane to show the player passed
   */
  private void addPassText() {
    StringBuffer text = new StringBuffer(getText());
    text.append("\n ");
    text.append(moveCount++);
    text.append(": Pass");
    setText(text.toString());
  }

  /**
   * Sets the background colour for this panel, based on the given team
   * 
   * @param team one of Team.HUMANS or Team.PREDATORS
   */
  private void setBackground(Team team) {
    if (Team.HUMANS.equals(team)) {
      setBackground(TileButton.COLOR_HUMANS);
    }
    else if (Team.PREDATORS.equals(team)) {
      setBackground(TileButton.COLOR_PREDATORS);
    }
    else {
      throw new IllegalArgumentException("Unexpected team: " + team);
    }
  }
}
