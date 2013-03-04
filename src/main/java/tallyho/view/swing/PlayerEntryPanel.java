/*
 * Created on 27/08/2004
 */
package tallyho.view.swing;

import java.awt.Font;
import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import tallyho.model.player.Player;
import tallyho.model.player.RealPlayer;
import tallyho.model.player.ai.TrivialAI;
import tallyho.model.player.ai.LookAheadAI;
import tallyho.model.player.ai.ShortSightedAI;

/**
 * Panel for entering the details of one player
 */
public class PlayerEntryPanel extends JPanel {

  // Constants
  private static final PlayerClass[] PLAYER_CLASSES = {
    new PlayerClass(RealPlayer.class),
    new PlayerClass(TrivialAI.class),
    new PlayerClass(ShortSightedAI.class),
    new PlayerClass(LookAheadAI.class)
  };

  private static final int
    BORDER_WIDTH = 5,
    TITLE_WEIGHT = Font.BOLD,
    TITLE_POINT_SIZE = 15;
  
  private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(
      BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH);
  private static final Border COMPOUND_BORDER =
    BorderFactory.createCompoundBorder(Utils.BORDER_RAISED_BEVEL, EMPTY_BORDER);

  // Properties
  private JTextField tfPlayerName;
  private JComboBox ddPlayerType;
  
  /**
   * Constructor
   * 
   * @param playerNumber 1 or 2
   */
  PlayerEntryPanel(int playerNumber) {
    super(new GridLayout(3, 2));  // 3 rows, 2 columns
    if (playerNumber < 1 || playerNumber > 2)
      throw new IllegalArgumentException("Invalid player number: " + playerNumber);
    
    initialiseGUI(playerNumber);
  }
  
  /**
   * Sets the graphical properties of this panel
   * 
   * @param playerNumber the one-indexed number of the player to whom this
   *   panel relates
   */
  private void initialiseGUI(int playerNumber) {
    // Panel properties
    setBorder(COMPOUND_BORDER);
    setOpaque(false);
    
    // "Player Number" label
    StringBuffer playerNumberText = new StringBuffer("Player ");
    playerNumberText.append(playerNumber);
    playerNumberText.append(":");
    JLabel lblPlayerNumber = new JLabel(playerNumberText.toString());
    lblPlayerNumber.setFont(new Font(null, TITLE_WEIGHT, TITLE_POINT_SIZE));
    add(lblPlayerNumber);
    add(new JLabel(""));
    
    // "Player Name" label & text field
    add(new JLabel("Name: "));
    tfPlayerName = new JTextField();
    StringBuffer defaultName = new StringBuffer("Player ");
    defaultName.append(playerNumber);
    tfPlayerName.setText(defaultName.toString());
    add(tfPlayerName);
    
    // "Player Type" label & drop-down
    add(new JLabel("Type: "));
    ddPlayerType = new JComboBox(PLAYER_CLASSES);
    add(ddPlayerType);
  }
  
  /**
   * Returns the Player details from this panel
   * @return a non-null Player object
   */
  String getPlayerName() {
    return tfPlayerName.getText();
  }

  /**
   * Returns the Class of this player
   * @return a Class that implements Player
   */
  Class getPlayerClass() {
    PlayerClass selectedClass = (PlayerClass) ddPlayerType.getSelectedItem();
    return selectedClass.getPlayerClass();
  }
}

/**
 * Represents a player class displayed in a drop-down.
 * Exists so we can override its toString() method
 */
class PlayerClass {

  // Properties
  private final Class playerClass;

  /**
   * Constructor
   * 
   * @param playerClass the class of the player
   */
  PlayerClass(Class playerClass) {
    this.playerClass = playerClass;
  }
  
  Class getPlayerClass() {
    return playerClass;
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    try {
      // Invoke the static toString() method of the player class 
      Method displayNameField = playerClass.getMethod("getType", (Class[]) null);
      Player player = (Player) playerClass.newInstance();
      return (String) displayNameField.invoke(player, (Object[]) null);
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
    catch (IllegalArgumentException ex) {
      ex.printStackTrace();
      throw ex;
    }
    catch (InstantiationException ex) {
      // The player couldn't be instantiated
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
    catch (InvocationTargetException ex) {
      // Problem calling the getType() method on the player
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
    catch (NoSuchMethodException ex) {
      // The given player class doesn't have a getType() method.
      // This should never happen if the class is an instance of Player.
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
    catch (SecurityException ex) {
      ex.printStackTrace();
      throw ex;
    }
  }
}
