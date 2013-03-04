/*
 * Created on 27/08/2004
 */
package tallyho.view.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import tallyho.model.player.Player;

/**
 * Prompts the user to enter the details of both players
 */
public class PlayersDialog extends JDialog implements ActionListener {

  // Constants
  private static final int
    TITLE_WEIGHT = Font.BOLD,
    TITLE_POINT_SIZE = 20;

  // Properties
  private PlayerEntryPanel pnlPlayer1;
  private PlayerEntryPanel pnlPlayer2;
  private JButton btnOK;
  private JButton btnCancel;
  private boolean submitted;  // e.g. user clicked "OK"
  
  /**
   * Constructor
   */
  public PlayersDialog() {
    super((Frame) null, "Choose Players", true); // modal
    initialiseGUI();
  }

  /**
   * Sets the visible properties of this dialog
   */
  private void initialiseGUI() {
    // Dialog
    Container contentPane = getContentPane();
    contentPane.setBackground(BoardPanel.COLOR_BOARD);
    BorderLayout layout = new BorderLayout(10, 0);
    contentPane.setLayout(layout);

    // Heading Text
    JLabel lblHeading = new JLabel("Who is playing?");
    lblHeading.setFont(new Font(null, TITLE_WEIGHT, TITLE_POINT_SIZE));
    lblHeading.setHorizontalAlignment(SwingConstants.CENTER);
    contentPane.add(lblHeading, BorderLayout.NORTH);
    
    // Player Details
    JPanel pnlDetails = new JPanel();
    pnlDetails.setBorder(Utils.BORDER_EMPTY_5);
    pnlDetails.setLayout(new BorderLayout());
    pnlDetails.setOpaque(false);
    contentPane.add(pnlDetails, BorderLayout.CENTER);
    // -- Player 1
    pnlPlayer1 = new PlayerEntryPanel(1);
    pnlDetails.add(pnlPlayer1, BorderLayout.WEST);
    // -- Player 2
    pnlPlayer2 = new PlayerEntryPanel(2);
    pnlDetails.add(pnlPlayer2, BorderLayout.EAST);
    
    // Button Panel
    JPanel pnlButtons = new JPanel();
    pnlButtons.setOpaque(false);
    contentPane.add(pnlButtons, BorderLayout.SOUTH);
    
    // OK Button
    btnOK = new JButton("Start Game");
    btnOK.addActionListener(this);
    btnOK.setOpaque(false);
    pnlButtons.add(btnOK);
    
    // Cancel Button
    btnCancel = new JButton("Cancel Game");
    btnCancel.addActionListener(this);
    btnCancel.setOpaque(false);
    pnlButtons.add(btnCancel);
    
    // Size it for good
    pack();
    setResizable(false);
    Utils.centre(this);
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == btnOK) {
      handleSubmit();
    }
    else if (event.getSource() == btnCancel) {
      handleCancel();
    }
    else {
      throw new IllegalStateException("Unhandled event: " + event);
    }
  }
  
  /**
   * Handles the user submitting this dialog
   */
  private void handleSubmit() {
    submitted = true;
    dispose();
  }

  /**
   * Handles the user cancelling the dialog
   */
  private void handleCancel() {
    // Just close the dialog
    dispose();
  }

  /**
   * @return a flag indicating whether the user submitted this
   *   dialog (as oppsoed to cancelling it)
   */
  boolean isSubmitted() {
    return submitted;
  }

  /**
   * Return a Player with the properties entered for player 1
   * 
   * @return a non-null player (team not set)
   */
  public Player getPlayerOne() {
    return getPlayer(pnlPlayer1);
  }

  /**
   * Return a Player with the properties entered for player 2
   * 
   * @return a non-null player (team not set)
   */
  public Player getPlayerTwo() {
    return getPlayer(pnlPlayer2);
  }
  
  /**
   * Returns a player based on the data in the given entry panel
   * 
   * @param playerEntryPanel can't be <code>null</code>
   * @return a non-null player (team not set)
   */
  private Player getPlayer(PlayerEntryPanel playerEntryPanel) {
    // Check inputs
    if (playerEntryPanel == null)
      throw new IllegalArgumentException("PlayerEntryPanel can't be null");
    
    Class playerClass = playerEntryPanel.getPlayerClass();
    try {
      Player player = (Player) playerClass.newInstance();
      // Successfully instantiated the object - set its properties
      player.setName(playerEntryPanel.getPlayerName());
      return player;
    }
    catch (IllegalAccessException ex) {
      // The class or its nullary constructor is not accessible
      System.err.println("Couldn't create a player of type " + playerClass);
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
    catch (InstantiationException ex) {
      // e.g. the class isn't concrete or doesn't have a nullary constructor
      System.err.println("Couldn't create a player of type " + playerClass);
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }
}
