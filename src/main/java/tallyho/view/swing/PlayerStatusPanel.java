/*
 * Created on 25/08/2004
 */
package tallyho.view.swing;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tallyho.model.Game;
import tallyho.model.Team;
import tallyho.model.player.Player;

/**
 * The Swing view of a player in TallyHo
 */
public class PlayerStatusPanel extends JPanel implements Observer {

  /**
   * Constructor
   * 
   * @param game can't be <code>null</code>
   * @param playerNumber must be 1 or 2
   */
  public PlayerStatusPanel(Game game, int playerNumber) {
    // Check inputs
    if (game == null)
      throw new IllegalArgumentException("Game can't be null");
    if (playerNumber < 1 || playerNumber > 2)
      throw new IllegalArgumentException("Invalid player number: " + playerNumber);
    
    // Make sure we're notified of changes to this player
    ((Observable) game.getPlayer(playerNumber)).addObserver(this);
    
    // Set up the GUI
    initialiseGUI(game, playerNumber);
  }

  /**
   * Sets up the GUI components of this player panel
   * 
   * @param game can't be <code>null</code>
   * @param playerNumber 1 or 2
   */
  private void initialiseGUI(Game game, int playerNumber) {
    Player player = game.getPlayer(playerNumber);
    
    // This panel
    setBackground(player.getTeam());
    setBorder(Utils.BORDER_RAISED_BEVEL);
    setLayout(new BorderLayout());
    
    // Name label
    add(new PlayerNameLabel(game, player), BorderLayout.NORTH);
    
    // Moves label (in a scroll pane)
    PlayerMovesPane playerMovesLabel = new PlayerMovesPane(game, player);
    add(new JScrollPane(playerMovesLabel), BorderLayout.CENTER);

    // Score label
    add(new PlayerScoreLabel(player), BorderLayout.SOUTH);
  }

  /**
   * Sets the background colour for this panel, based on the given team
   * 
   * @param team one of Tile.TEAM_HUMANS or Tile.TEAM_PREDATORS
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

  /**
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   * @param observable the object reporting a change
   * @param propertyName the name of its changed property
   */
  public void update(Observable observable, Object propertyName) {
    if (observable instanceof Player && Player.TEAM.equals(propertyName)) {
      // Our player is telling us their team changed
      Player player = (Player) observable;
      setBackground(player.getTeam());
    }
  }
}