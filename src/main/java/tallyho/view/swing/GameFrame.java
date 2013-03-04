/*
 * Created on 24/08/2004
 */
package tallyho.view.swing;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import tallyho.model.Game;
import tallyho.model.player.Player;

/**
 * Swing display of a game of Tally Ho
 */
public class GameFrame extends JFrame
  implements ActionListener, Observer, Runnable
{
  // Constants
  static final URL SOUNDS_URL = ClassLoader.getSystemResource("sounds");

  private static final AudioClip AUDIO_FOREST;

  static {
    // Load the forest sound
    try {
      AUDIO_FOREST = Applet.newAudioClip(new URL(SOUNDS_URL, "forest.aiff"));
    }
    catch (MalformedURLException ex) {
      // Sound file couldn't be found
      throw new RuntimeException(ex);
    }
  }
  
  // Properties:
  
  // -- Help texts
  private final String textAbout, textStrategy;
  
  // -- Model
  private PlayerStatusPanel[] playerStatusPanels;
  
  // -- GUI Components
  private BoardPanel boardPanel;
  private MessageDialog messageDialog;
  private JMenuItem fileNew;
  private JMenuItem fileSave;
  private JMenuItem fileExit;
  private JCheckBoxMenuItem settingsPlayBackgroundNoises;
  private JCheckBoxMenuItem settingsPlayTileNoises;
  private JCheckBoxMenuItem settingsShowTileTips;
  private JMenuItem helpRules;
  private JMenuItem helpStrategy;
  private JMenuItem helpAbout;
  
  /**
   * Constructor
   * 
   * @param game the game to be displayed, can't be <code>null</code>
   */
  public GameFrame(Game game) {
    // Check input
    Game.validate(game);
    
    // Make sure we hear about relevant changes in the game state
    game.addObserver(this);
    
    // Set up the GUI
    initialiseGUI(game);
    
    // Set up the help texts
    textAbout = getAboutText(game);
    textStrategy = getStrategyTips();
  }

  /**
   * Initialises the graphical components of this game.
   * 
   * @param game
   */
  private void initialiseGUI(Game game) {
    // Frame
    getContentPane().setLayout(new BorderLayout());
    setTitle(game);
    
    // Menu Bar
    initialiseMenu();
    
    // BoardImpl Panel
    boardPanel = new BoardPanel(game, this);
    getContentPane().setBackground(boardPanel.getBackground());
    getContentPane().add(boardPanel, BorderLayout.CENTER);

    // Player Status Panels
    playerStatusPanels = new PlayerStatusPanel[game.getPlayers()];
    playerStatusPanels[0] = new PlayerStatusPanel(game, 1);
    getContentPane().add(playerStatusPanels[0], BorderLayout.WEST);
    playerStatusPanels[1] = new PlayerStatusPanel(game, 2);
    getContentPane().add(playerStatusPanels[1], BorderLayout.EAST);

    // Do final sizing & positioning
    pack();
    setResizable(false);
    Utils.centre(this);
    
    // Create reusable message dialog
    messageDialog = new MessageDialog(this, null, null);
  }
  
  /**
   * Sets up the game menu
   */
  private void initialiseMenu() {
    // Menu bar
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    
    // - "File" menu
    JMenu file = new JMenu("File");
    file.setMnemonic('F');
    menuBar.add(file);
    
    // -- "File -> New" menu item
    fileNew = new JMenuItem("New...");
    fileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
    fileNew.setMnemonic('N');
    file.add(fileNew);
    fileNew.addActionListener(this);
    
    // -- "File -> Save" menu item
    fileSave = new JMenuItem("Save...");
    fileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
    fileSave.setMnemonic('S');
    file.add(fileSave);
    fileSave.addActionListener(this);
    
    // -- "File -> Exit" menu item
    file.addSeparator();
    fileExit = new JMenuItem("Exit");
    fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK));
    fileExit.setMnemonic('x');
    file.add(fileExit);
    fileExit.addActionListener(this);
    
    // - "Settings" menu
    JMenu settings = new JMenu("Settings");
    settings.setMnemonic('S');
    menuBar.add(settings);
    
    // -- "Settings -> Play Background Noises" menu item
    settingsPlayBackgroundNoises = new JCheckBoxMenuItem("Play Background Noises");
    settingsPlayBackgroundNoises.setMnemonic('B');
    settings.add(settingsPlayBackgroundNoises);
    settingsPlayBackgroundNoises.addActionListener(this);

    // -- "Settings -> Play Tile Noises" menu item
    settingsPlayTileNoises = new JCheckBoxMenuItem("Play Tile Noises");
    settingsPlayTileNoises.setMnemonic('T');
    settings.add(settingsPlayTileNoises);
    settingsPlayTileNoises.addActionListener(this);
    
    // -- "Settings -> Show Tile Tips" menu item
    settingsShowTileTips = new JCheckBoxMenuItem("Show Tile Tips");
    settingsShowTileTips.setMnemonic('S');
    settings.add(settingsShowTileTips);
    settingsShowTileTips.addActionListener(this);

    // - "Help" menu
    JMenu help = new JMenu("Help");
    help.setMnemonic('H');
    menuBar.add(help);
    
    // -- "Help -> Rules" menu item
    helpRules = new JMenuItem("Rules...");
    helpRules.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
    helpRules.setMnemonic('R');
    help.add(helpRules);
    helpRules.addActionListener(this);

    // -- "Help -> Strategy" menu item
    helpStrategy = new JMenuItem("Strategy...");
    helpStrategy.setMnemonic('S');
    help.add(helpStrategy);
    helpStrategy.addActionListener(this);

    // -- "Help -> About" menu item
    help.addSeparator();
    helpAbout = new JMenuItem("About...");
    helpAbout.setMnemonic('A');
    help.add(helpAbout);
    helpAbout.addActionListener(this);
  }

  /**
   * Sets the title of this frame based on the given Game data
   * 
   * @param game can't be <code>null</code>
   */
  private void setTitle(Game game) {
    if (game == null)
      throw new IllegalArgumentException("Game can't be null");
      
    StringBuffer title = new StringBuffer(game.getNameAndRound());
    int turnsLeft = game.getTurnsLeft();
    switch (turnsLeft) {
      case 0:
        title.append(" (round over)");
        break;
      case 1:
        title.append(" (1 turn left)");
        break;
      case Integer.MAX_VALUE:
        // In mid-game - do nothing
        break;
      default:
        // In end-game - show how many turns left
        title.append(" (");
        title.append(turnsLeft);
        title.append(" turns left)");
    }
    setTitle(title.toString());
  }

  /**
   * Returns the message to be shown at the end of the game
   * 
   * @param game
   * @return a non-<code>null</code> string
   */
  private String getEndGameMessage(Game game) {
    StringBuffer message = new StringBuffer("<html>");
    message.append("That's the end of the game.<br>");
    Player winner = game.getWinner();
    if (winner == null) {
      // It's a draw
      message.append("It's a draw - ");
      message.append(game.getActivePlayer().getScore());
      message.append(" all!");
    }
    else {
      // Someone won
      Player loser = game.getLoser();
      message.append(winner.getName()).append(" wins ");
      message.append(winner.getScore()).append(" - ").append(loser.getScore()).append(".");
    }
    message.append("<br>Click OK to exit.</html>");
    return message.toString();
  }
  
  /**
   * Returns this frame's reusable MessageDialog. To use it,
   * callers need only set its message and make it visible.
   * 
   * @return a non-null MessageDialog ready for setting and display
   */
  MessageDialog getMessageDialog() {
    return messageDialog;
  }

  /**
   * Closes down the game
   */
  private void exit() {
    System.exit(0);
  }
  
  /**
   * Returns the Help -> About text
   * 
   * @param game
   * @return see above
   */
  private String getAboutText(Game game) {
    StringBuffer aboutText = new StringBuffer(game.getName());
    aboutText.append("<br>Game design &copy; Rudi Hoffman");
    aboutText.append("<br>Java implementation by Andrew Swan");
    aboutText.append("<br>Some sound effects from http://www.grsites.com");
    return aboutText.toString();
  }
  
  /**
   * Shows the Help -> About dialog
   */
  private void helpAbout() {
    messageDialog.setTitle("Help -> About");
    messageDialog.setMessage(textAbout);
    messageDialog.setVisible(true);
  }
  
  /**
   * Handles actions for which this frame is a registered listener. This should
   * only be items on the menu. Note that some of the binary menu options (for
   * example "Play Tile Noises") don't need explicit handling.
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent event) {
    // Expecting menu items
    if (event.getSource() == fileNew) {
      newGame();
    }
    else if (event.getSource() == fileSave) {
      save();
    }
    else if (event.getSource() == fileExit) {
      exit();
    }
    else if (event.getSource() == settingsPlayBackgroundNoises) {
      toggleBackgroundNoises();
    }
    else if (event.getSource() == helpRules) {
      helpRules();
    }
    else if (event.getSource() == helpStrategy) {
      helpStrategy();
    }
    else if (event.getSource() == helpAbout) {
      helpAbout();
    }
  }

  /**
   * Shows the user the rules of the game
   */
  private void helpRules() {
    messageDialog.setTitle("Help -> Rules");
    messageDialog.setMessage("(rules coming soon)");
    messageDialog.setVisible(true);
  }

  /**
   * Returns some tips for playing the game well
   * 
   * @return see above
   */
  private String getStrategyTips() {
    StringBuffer tips = new StringBuffer("<div align='left'>");
    tips.append("Tally Ho involves a lot of luck, but you can improve");
    tips.append("<br>your chances of winning by noting these tips:<ul>");
    tips.append("<li>Hunters work better in open spaces.</li>");
    tips.append("<li>Bears work better in confined spaces.</li>");
    tips.append("<li>Until both bears have been found, flip tiles only in");
    tips.append("<br>places where you would want to find one (see above).</li>");
    tips.append("<li>Consider moving birds into the path of your fox or hunter.</li>");
    tips.append("<li>If your opponent moved one of their own tiles last turn,");
    tips.append("<br>they can't reverse that move on their next turn - use that");
    tips.append("<br>fact to trap his tiles (especially bears and lumberjacks).</li>");
    tips.append("<li>Start positioning your own tiles to be rescued, before");
    tips.append("<br>the end-game actually begins.</li>");
    tips.append("<li>Don't rescue a tile just because you can. It's worth more");
    tips.append("<br>points to capture two pheasants than to take two turns rescuing");
    tips.append("<br>a fox or hunter.</li>");
    tips.append("</ul>");
    tips.append("</div>");
    return tips.toString();
  }

  /**
   * Shows the user some tips for playing the game well
   */
  private void helpStrategy() {
    messageDialog.setTitle("Help -> Strategy");
    messageDialog.setMessage(textStrategy);
    messageDialog.setVisible(true);
  }

  /**
   * Saves the current state of the game to a file
   */
  private void save() {
    // TODO save game
    messageDialog.setTitle("Save Game");
    messageDialog.setMessage("Sorry, saving of games hasn't been implemented yet.");
    messageDialog.setVisible(true);
  }

  /**
   * Handles the user choosing to start a new game
   */
  private void newGame() {
    messageDialog.setTitle("New Game");
    messageDialog.setMessage("Sorry, starting a new game hasn't been implemented yet.");
    // messageDialog.setMessage("Are you sure you want to start a new game?");
    messageDialog.setVisible(true);
    // When we get here, the user has answered the dialog
    // TODO get actual answer
    if (false) {
      // TODO start new game
    }
  }

  /**
   * Toggles whether background noises are being played or not
   */
  private void toggleBackgroundNoises() {
    if (settingsPlayBackgroundNoises.isSelected()) {
      AUDIO_FOREST.loop();
    }
    else {
      AUDIO_FOREST.stop();
    }
  }
  
  /**
   * Reports whether tile noises should be played
   * 
   * @return see above
   */
  boolean playTileNoises() {
    return settingsPlayTileNoises.isSelected();
  }

  /**
   * Reports whether tile tips should be shown
   * 
   * @return see above
   */
  boolean showTileTips() {
    return settingsShowTileTips.isSelected();
  }
  
  /**
   * @see java.lang.Runnable#run()
   */
  public void run() {
    // Wait for user actions or game updates
  }

  /**
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  public void update(Observable observable, Object change) {
    if (observable instanceof Game && Game.ACTIVE_PLAYER.equals(change)) {
      Game game = (Game) observable;
      // A turn finished - refresh the frame title
      setTitle(game);
      validate();
    }
    else if (observable instanceof Game && Game.NEW_ROUND.equals(change)) {
      // The next round has begun - refresh the frame title
      Game game = (Game) observable;
      setTitle(game);
      validate();
      // Tell the human player(s), if any
      if (game.isEitherPlayerReal()) {
        messageDialog.setTitle("Round 1 Over");
        messageDialog.setMessage(
            "<html>Round 1 is over.<br>Players swap sides for Round 2.</html>");
        messageDialog.setVisible(true);
        // When we get here, user has clicked OK
      }
    }
    else if (observable instanceof Game && Game.GAME_OVER.equals(change)) {
      Game game = (Game) observable;
      // The game has finished (i.e. round two is over) - refresh the title
      setTitle(game);
      validate();
      // Inform the user
      messageDialog.setTitle("Game Over");
      messageDialog.setMessage(getEndGameMessage(game));
      messageDialog.setVisible(true);
      // When we get here, user has clicked OK
      exit();
    }
    else if (observable instanceof Game && Game.END_GAME_STARTED.equals(change)) {
      // The end-game just commenced - refresh the frame title
      Game game = (Game) observable;
      setTitle(game);
      validate();
      // Tell the human player(s), if any
      if (game.isEitherPlayerReal()) {
        messageDialog.setTitle("This Round Will End Soon");
        StringBuffer message = new StringBuffer("<html>There are ");
        message.append(Game.END_GAME_TURNS);
        message.append(" turns left in this round.");
        message.append("<br>You may move your own tiles off the board for points.</html>");
        messageDialog.setMessage(message.toString());
        messageDialog.setVisible(true);
        // User clicked OK on the dialog
      }
    }
    else if (observable instanceof AIController) {
      // The AI is telling us it has started or stopped thinking
      if (AIController.THINKING_STARTED.equals(change)) {
        // TODO show dialog
      }
      else if (AIController.THINKING_STARTED.equals(change)) {
        // TODO hide dialog
      }
    }
  }
}
