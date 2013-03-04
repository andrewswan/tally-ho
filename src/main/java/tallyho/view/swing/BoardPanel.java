/*
 * Created on 25/08/2004
 */
package tallyho.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import tallyho.model.Board;
import tallyho.model.Game;
import tallyho.model.IllegalMoveException;
import tallyho.model.Team;
import tallyho.model.player.Player;
import tallyho.model.tile.Directional;
import tallyho.model.tile.Tile;
import tallyho.model.turn.Flip;
import tallyho.model.turn.Move;
import tallyho.model.turn.Pass;
import tallyho.model.turn.Rescue;
import tallyho.model.turn.Turn;

/**
 * The Swing view of the TallyHo board
 */
public class BoardPanel extends JPanel implements ActionListener, Observer
{
  // Constants
  /* package */ static final Color
    COLOR_BOARD = new Color(120, 180, 120); // light green
  
  private static final int
    BORDER_SIZE = 4,      // around outside of board
    HORIZONTAL_GAP = 2,   // between tiles
    VERTICAL_GAP = 2;     // between tiles
    
  // Properties
  private final Game game;
  private final GameFrame gameFrame;
  private final int boardSize;
  private final TileButton[][] tileButtons;
  private GridLayout gridLayout;
  private JPanel gridPanel; // to hold the tiles
  private TileButton selectedTileButton;
    
  /**
   * Constructor
   * 
   * @param game the model for the Tally Ho game, can't be
   *   <code>null</code>, nor can its "board" property
   * @param gameFrame the GameFrame with which this panel is associated
   */
  public BoardPanel(Game game, GameFrame gameFrame) {
    // Check inputs
    if (game == null || game.getBoard() == null)
      throw new IllegalArgumentException("Invalid game: " + game);
    if (gameFrame == null)
      throw new IllegalArgumentException("Invalid GameFrame");
    
    // Store parameters
    this.game = game;
    this.gameFrame = gameFrame;
    boardSize = game.getBoard().getMaxIndex() + 1;
    tileButtons = new TileButton[boardSize][boardSize];
    game.getBoard().addObserver(this);
    
    // Set up GUI components
    initialiseGUI();
  }

  /**
   * Sets up the visual components of the board
   */
  private void initialiseGUI() {
    // This panel
    setBackground(COLOR_BOARD);
    setBorder(BorderFactory.createEmptyBorder(
        BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
    setLayout(new BorderLayout());
    
    // Grid
    gridLayout = new GridLayout(boardSize, boardSize);
    gridLayout.setHgap(HORIZONTAL_GAP);
    gridLayout.setVgap(VERTICAL_GAP);
    gridPanel = new JPanel();
    gridPanel.setBackground(COLOR_BOARD);
    gridPanel.setLayout(gridLayout);
    add(gridPanel, BorderLayout.CENTER);

    // Exit Buttons
    // -- North
    ExitButton btnExitNorth = new ExitButton(game, Directional.SMALL_Y);
    btnExitNorth.addActionListener(this);
    add(btnExitNorth, BorderLayout.NORTH);
    // -- East
    ExitButton btnExitEast = new ExitButton(game, Directional.BIG_X);
    btnExitEast.addActionListener(this);
    add(btnExitEast, BorderLayout.EAST);
    // -- South
    ExitButton btnExitSouth = new ExitButton(game, Directional.BIG_Y);
    btnExitSouth.addActionListener(this);
    add(btnExitSouth, BorderLayout.SOUTH);
    // -- West
    ExitButton btnExitWest = new ExitButton(game, Directional.SMALL_X);
    btnExitWest.addActionListener(this);
    add(btnExitWest, BorderLayout.WEST);
    
    // Tile Buttons
    for (int y = 0; y < boardSize; y++) {
      for (int x = 0; x < boardSize; x++) {
        TileButton tileButton = new TileButton(game, gameFrame, x, y);
        gridPanel.add(tileButton);
        tileButtons[x][y] = tileButton;
        // Ensure we find out when a tile button is clicked
        tileButton.addActionListener(this);
      }
    }
  }
  
  /**
   * Handles players clicking tile buttons on the board.
   * 
   * @see ActionListener#actionPerformed(java.awt.event.ActionEvent)
   * @param event
   */
  public void actionPerformed(ActionEvent event) {
    Object eventSource = event.getSource();
    if (eventSource instanceof TileButton) {
      handleTileButton((TileButton) eventSource);
    }
    else if (eventSource instanceof ExitButton) {
      handleExitButton((ExitButton) eventSource);
    }
    else {
      throw new UnsupportedOperationException(
          "Can't handle events from a " + eventSource.getClass().getName());
    }
  }

  /**
   * Handles the user clicking an exit button
   * 
   * @param button can't be <code>null</code>
   */
  private void handleExitButton(ExitButton button) {
    // Check input
    if (button == null)
      throw new IllegalArgumentException("ExitButton can't be null");
    
    // Check a tile is currently selected
    if (selectedTileButton != null) {
      Point2D from = selectedTileButton.getBoardPosition();
      // Calculate the destination x/y based on which button was clicked
      int toX = (int) from.getX();
      int toY = (int) from.getY();
      // Make destination one square off the board in the relevant direction
      switch (button.getDirection()) {
        case Directional.BIG_X:
          toX = boardSize;
          break;
        case Directional.BIG_Y:
          toY = boardSize;
          break;
        case Directional.SMALL_X:
          toX = -1;
          break;
        case Directional.SMALL_Y:
          toY = -1;
          break;
        default:
          throw new IllegalStateException(
              "Invalid direction: " + button.getDirection());
      }
      try {
        Player movingPlayer = game.getActivePlayer();
        game.getBoard().moveTile(movingPlayer, from, new Point(toX, toY));
        // The move succeeded
      }
      catch (IllegalMoveException ex) {
        // The move was invalid
        rejectMove(ex.getMessage());
      }
    }
  }

  /**
   * Handles an event (e.g. click) from the given button
   * 
   * @param button can't be <code>null</code>
   */
  private void handleTileButton(TileButton button) {
    if (button == null)
      throw new IllegalArgumentException("Button can't be null");
    Point2D buttonPosition = button.getBoardPosition();
    Tile tile = game.getBoard().getTile(buttonPosition);
    if (tile == null) {
      handleEmptySquare(button);
    }
    else if (tile.isFaceUp()) {
      handleFaceUpTile(button);
    }
    else {
      handleFaceDownTile(buttonPosition);
    }
  }

  /**
   * Handles the user clicking an empty board square.
   * 
   * @param emptySquare
   */
  private void handleEmptySquare(TileButton emptySquare) {
    // Only do anything if a button is currently selected
    if (selectedTileButton != null) {
      Point2D moveFrom = selectedTileButton.getBoardPosition();
      try {
        // Move the selected button to the given empty square
        Player movingPlayer = game.getActivePlayer();
        game.getBoard().moveTile(
            movingPlayer, moveFrom, emptySquare.getBoardPosition());
      }
      catch (IllegalMoveException ex) {
        rejectMove(ex.getMessage());
      }
    }
  }

  /**
   * Handles the user clicking a face-down tile (to flip it)
   * 
   * @param buttonPosition can't be <code>null</code>
   */
  private void handleFaceDownTile(Point2D buttonPosition) {
    if (buttonPosition == null) {
      throw new IllegalArgumentException("Button position can't be null");
    }
    Team flippingTeam = game.getActivePlayer().getTeam();
    game.getBoard().flipTile(flippingTeam, buttonPosition);
  }
  
  private void handleFaceUpTile(TileButton tileButton) {
    if (selectedTileButton == null) {
      // No tile is selected - try to select the given one
      selectButton(tileButton);
    }
    else {
      // A tile is already selected - is it this one?
      if (selectedTileButton.equals(tileButton)) {
        // Yes - just deselect it
        selectButton(null);
      }
      else {
        // Some other tile is already selected - try to move it to the square
        // that was just clicked (capturing any tile at that square)
        TileButton capturingTile = selectedTileButton;
        try {
          Player movingPlayer = game.getActivePlayer();
          game.getBoard().moveTile(movingPlayer,
              capturingTile.getBoardPosition(), tileButton.getBoardPosition());
        }
        catch (IllegalMoveException ex) {
          rejectMove(ex.getMessage());
        }
      }
    }
  }

  /**
   * Selects the button at the given coordinates, and de-selects
   * the previously selected button, if any.
   * 
   * @param tileButton can be <code>null</code> if the caller
   *   doesn't want any button selected
   */
  private void selectButton(TileButton tileButton) {
    // Clear the current selection (if any)
    if (selectedTileButton != null) {
      selectedTileButton.setSelected(false);
      selectedTileButton = null;
    }
    
    // Set the selection to the given Tile (if there is one
    // and it doesn't belong to the other team)
    if (tileButton != null) {
      Board board = game.getBoard();
      Point2D tilePosition = tileButton.getBoardPosition();
      Tile tile = board.getTile(tilePosition);
      Team movingTeam = game.getActivePlayer().getTeam();
      if (tile != null) {
        if (board.isMovementPossible(movingTeam, tilePosition)) {
          selectedTileButton = tileButton;
          tileButton.setSelected(true);
        }
        else {
          String message = "You can't move that tile.";
          if (tile.getRange() == 0)
            message = "That type of tile can't be moved.";
          rejectMove(message);
        }
      }
    }
  }

  /**
   * Perform any GUI actions arising from an invalid move,
   * e.g. beep, show an error dialog, etc.
   * 
   * @param message a message to be shown to the user, can be <code>null</code>
   */
  private void rejectMove(String message) {
    // Beep
    Toolkit.getDefaultToolkit().beep();
    // Show an error message (using the already-created dialog)
    MessageDialog messageDialog = gameFrame.getMessageDialog();
    messageDialog.setTitle("Invalid Move");
    messageDialog.setMessage(message);
    messageDialog.setVisible(true);
  }

  /**
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  public void update(Observable observable, Object change) {
    if (observable instanceof Board &&
        (change instanceof Move || change instanceof Flip))
    {
      // A tile was moved or flipped
      updateTileButtons((Turn) change);
    }
  }

  /**
   * Updates the appearance of the board after a turn (either a real player's
   * turn or an AI turn).
   * 
   * @param turn can't be <code>null</code> or a Pass
   */
  private void updateTileButtons(Turn turn) {
    if (turn == null || turn instanceof Pass)
      throw new IllegalArgumentException("Turn can't be null or a pass");

    if (turn instanceof Flip) {
      Flip flip = (Flip) turn;
      TileButton flipped = tileButtons[flip.getX()][flip.getY()];
      flipped.setIcon(flipped.getIcon());
    }
    else {
      // A tile was moved
      Move move = (Move) turn;
      TileButton from = tileButtons[move.getFromX()][move.getFromY()];
      TileButton to = null;  // assume off-board
      if (!(move instanceof Rescue)) {
        to = tileButtons[move.getToX()][move.getToY()];
      }
      
      if (gameFrame.playTileNoises()) {
        // Make the noise for the moving tile
        from.playSound(to != null && to.getTile() != null);
      }
      
      // Update the "to" square (if any) to contain the moving tile
      if (to != null) {
        // Make the captured tile's sound, if any
        if (gameFrame.playTileNoises() && to.getTile() != null)
          to.playCapturedSound();
        to.setTile(from.getTile());
      }
      
      // Empty the "from" square where the capturing tile was
      from.setTile(null);
    }

    // Clear any currently selected tile button
    selectButton(null);
    
    validate();
    paintAll(getGraphics());
  }
}
