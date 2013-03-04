/*
 * Created on 24/08/2004
 */
package tallyho.view.swing;

import javax.swing.SwingUtilities;

import tallyho.model.Game;

/**
 * Runs the Swing GUI for the game of Tally Ho
 */
public class Main {

  /**
   * Runs the game in a thread-safe way
   * 
   * @see <a href="http://java.sun.com/docs/books/tutorial/uiswing/learn/example1.html"></a>
   * @param args not used
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          // Pop up a players dialog
          PlayersDialog playersDialog = new PlayersDialog();
          playersDialog.setVisible(true);
          // When we get here, dialog was answered
          if (playersDialog.isSubmitted()) {
            // e.g. user clicked OK - set up the game and its players
            Game game = new Game(
                playersDialog.getPlayerOne(), playersDialog.getPlayerTwo());
            
            // Prepare the various threads
            GameFrame gameFrame = new GameFrame(game);
            gameFrame.setVisible(true);
            Thread viewThread = new Thread(gameFrame);
            AIController aiController = new AIController(game);
            aiController.addObserver(gameFrame);
            Thread aiControllerThread = new Thread(aiController);
            
            // Start the threads
            viewThread.start();
            aiControllerThread.start();
          }
        }
      }
    );
  }
}