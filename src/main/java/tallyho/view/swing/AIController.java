/*
 * Created on 8/09/2004
 */
package tallyho.view.swing;

import java.awt.Toolkit;
import java.util.Observable;
import java.util.Observer;

import tallyho.model.Game;
import tallyho.model.IllegalMoveException;
import tallyho.model.player.Player;
import tallyho.model.player.ai.ComputerPlayer;
import tallyho.model.turn.Turn;

/**
 * Waits for it to be an AI's turn, then makes that player's turn. Acts as a
 * Controller in the M-V-C architecture. Intended to be run in its own Thread.
 */
public class AIController extends Observable implements Observer, Runnable {
    
  /**
   * The name of a change that tells Observers the AI has started thinking
   */
  public static final String THINKING_STARTED = "StartThinking";
  
  /**
   * The name of a change that tells Observers the AI has finished thinking
   */
  public static final String THINKING_FINISHED = "FinishedThinking";
  
  // Properties
  private final Game game;

  /**
   * Constructor
   * 
   * @param game the game to be controlled, can't be <code>null</code>
   */
  AIController(Game game) {
    if (game == null)
      throw new IllegalArgumentException("Game can't be null");
    
    this.game = game;
    game.addObserver(this);
  }

  /**
   * @see java.lang.Runnable#run()
   */
  public void run() {
    // Check if the first turn is an AI turn
    checkComputerTurn();
    // Now wait for game updates (as an Observer)
  }

  /**
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   * @param observable the object reporting a change
   * @param propertyName the name of its changed property
   */
  public void update(Observable observable, Object propertyName) {
    if (observable instanceof Game && Game.ACTIVE_PLAYER.equals(propertyName)) {
      // The active player changed
      checkComputerTurn();  // assume this is the game we are already observing
    }
    else if (observable instanceof Game && Game.NEW_ROUND.equals(propertyName)) {
      // The round ended
      checkComputerTurn();  // assume this is the game we are already observing
    }
  }

  /**
   * Checks whether the active player is an AI, and if so, finds out its
   * desired move and makes it
   */
  private void checkComputerTurn() {
    Player activePlayer = game.getActivePlayer();
    if (activePlayer instanceof ComputerPlayer) {
      ComputerPlayer computerPlayer = (ComputerPlayer) activePlayer;
      
      // Notify observers we've started thinking
      setChanged();
      notifyObservers(THINKING_STARTED);
      
      // This can take some time
      Turn computerTurn = 
        computerPlayer.getTurn(game.getBoard());

      // Notify observers we've stopped thinking
      setChanged();
      notifyObservers(THINKING_FINISHED);
      
      try {
        if (computerTurn != null) {
          // Not passing (the game knows if we have to pass, and does so)
          game.getBoard().haveTurn(activePlayer, computerTurn);
          Toolkit.getDefaultToolkit().beep();
        }
      }
      catch (IllegalMoveException ex) {
        // AI submitted an invalid move
        throw new RuntimeException("Illegal AI turn: " + computerTurn);
      }
    }
  }
}
