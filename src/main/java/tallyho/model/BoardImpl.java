/**
 * Created on 18/08/2004
 */
package tallyho.model;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import tallyho.model.player.Player;
import tallyho.model.tile.AbstractTile;
import tallyho.model.tile.Bear;
import tallyho.model.tile.Directional;
import tallyho.model.tile.Duck;
import tallyho.model.tile.Fox;
import tallyho.model.tile.Hunter;
import tallyho.model.tile.Lumberjack;
import tallyho.model.tile.Pheasant;
import tallyho.model.tile.Tile;
import tallyho.model.tile.Tree;
import tallyho.model.turn.Flip;
import tallyho.model.turn.Move;
import tallyho.model.turn.Pass;
import tallyho.model.turn.Rescue;
import tallyho.model.turn.Turn;

/**
 * Represents the board in a game of Tally Ho
 */
public class BoardImpl extends Observable implements Board, tallyho.model.Observable {

  /**
   * Allows iteration through the Tiles on a BoardImpl
   */
  /* inner */ class BoardIterator implements Iterator {
  
    // Properties
    private final BoardImpl board;
    private final int maxBoardIndex;
    private int xPos;
    private int yPos;
    
    /**
     * Constructor
     * 
     * @param board can't be null
     */
    public BoardIterator(BoardImpl board) {
      if (board == null)
        throw new IllegalArgumentException("Can't iterate over a null board");
      this.board = board;
      this.maxBoardIndex = board.getMaxIndex();
      this.xPos = -1;
      this.yPos = 0;
    }
  
    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() {
      board.removeTile(xPos, yPos);
    }
  
    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
      return xPos < maxBoardIndex || yPos < maxBoardIndex;
    }
  
    /**
     * @see java.util.Iterator#next()
     */
    public Object next() {
      xPos++;
      if (xPos > maxBoardIndex) {
        xPos = 0;
        yPos++;
        if (yPos > maxBoardIndex)
          throw new IllegalStateException("Trying to iterate past end of board.");
      }
      return board.getTile(xPos, yPos); 
    }
  }
  
  private static final int
    // -- Number of tiles
    BEARS = 2,
    FOXES = 6,
    LUMBERJACKS = 2,
    HUNTERS = 8,
    PHEASANTS = 8,
    DUCKS = 7,
    TREES = 15,
	  // -- Default board size
    DEFAULT_SIZE = 7;	// of published game board
	
  private static final String
    MSG_WRONG_DIRECTION = "Hunters can only capture the way they are facing.";
  
	// Properties
  private Tile[][] tiles; // needs not to be final so we can clone it
  private final int centre;
  private final int maxIndex; // i.e. size - 1

  private Tile justFlippedNeutralTile;
  // -- The moves each player made of one of their own tiles last turn.
  // -- Null if the relevant player moved a neutral tile last turn.
  private Move justMovedHuman;
  private Move justMovedPredator;
  private boolean allTilesFaceUp;
  private boolean isClone;
    	
	/**
	 * Constructor for an empty board of the default size 
   * 
   * @param game can't be <code>null</code>
	 */
	public BoardImpl(Game game) {
    this(game, DEFAULT_SIZE);
	}

  /**
   * Constructor for an empty board of the given size 
   * 
   * @param game can't be <code>null</code>
   * @param size must be odd and positive
   */
  public BoardImpl(Game game, int size) {
    // Check given game is valid
    if (game == null)
      throw new IllegalArgumentException("Game can't be null");
    // Check given size is odd (so there can be a centre axis)
    if (size <= 0 || size % 2 == 0) {
      throw new IllegalArgumentException(
          "BoardImpl size must be a positive odd number.");
    }
    if (size > MAX_SIZE) {
      throw new IllegalArgumentException(
          "BoardImpl size can't be more than " + MAX_SIZE);
    }
    
    maxIndex = size - 1;
    tiles = new Tile[size][size];
    allTilesFaceUp = true;
    centre = (size - 1) / 2;
  }

  /* (non-Javadoc)
   * @see tallyho.model.Board#addTile(tallyho.model.tile.Tile, int, int)
   */
  public void addTile(Tile tile, int xPos, int yPos) {
    // Validate the inputs
    try {
      validateCoordsOnBoard(xPos, yPos);
    }
    catch (IllegalMoveException ex) {
      // The caller should know better
      throw new IllegalArgumentException(ex.getMessage());
    }
    
    if (tiles[xPos][yPos] != null) {
      throw new IllegalArgumentException(
        "Already a tile at x,y = " + xPos + "," + yPos);
    }
    if (tile == null)
      throw new IllegalArgumentException("Can't add a null tile");
    if (tile.isFaceUp())
      throw new IllegalArgumentException("Can't add a face-up tile");
    if (contains(tile))
      throw new IllegalArgumentException("This tile is already on the board.");
        
    // Inputs correct - place the tile
    tiles[xPos][yPos] = tile;
    allTilesFaceUp = false;
  }

  /**
   * Reports whether the board contains the given Tile
   * 
   * @param tile can't be null
   * @return see above
   */
  boolean contains(Tile tile) {
    // Check the input
    if (tile == null)
      throw new IllegalArgumentException("Can't look for a null tile.");
    
    // Look for the given tile on the board
    Iterator iter = getIterator();
    while (iter.hasNext()) {
      Tile thisTile = (Tile) iter.next();
      if (thisTile == tile)
        return true;
    }

    // Looked everywhere but didn't find it
    return false;
  }

  /* (non-Javadoc)
   * @see tallyho.model.Board#areAllTilesFaceUp()
   */
  public boolean areAllTilesFaceUp() {
    return allTilesFaceUp;
  }
  
  /**
   * Checks that the given "from" coordinates are within the boundaries
   * of the board.
   * 
   * @param xPos zero-indexed
   * @param yPos zero-indexed
   * @throws IllegalMoveException if they are not
   */
  void validateCoordsOnBoard(int xPos, int yPos)
    throws IllegalMoveException
  {
    if (xPos < 0 || yPos < 0 || xPos > maxIndex || yPos > maxIndex) {
      throw new IllegalMoveException(
        "Invalid x,y coords: " + xPos + "," + yPos);
    }
  }
  
  /**
   * Checks that the given coordinates represent a valid destination
   * for a tile. Note that during the end-game, i.e. once all tiles
   * are face-up, tiles are allowed to be moved off the board
   * 
   * @param toX zero-indexed
   * @param toY zero-indexed
   * @throws IllegalMoveException if the destination is invalid
   */
  void validateDestination(int toX, int toY) throws IllegalMoveException
  {
    if (allTilesFaceUp) {
      // The end game is underway - the destination should either
      // be on the board or on one of the central axes
      try {
        validateCoordsOnBoard(toX, toY);
        // If we get here, move is on board and therefore legal
      }
      catch (IllegalMoveException ex) {
        // Moving off board - check the destination is on an exit axis
        if (toX != centre && toY != centre) {
          throw new IllegalMoveException(
              "Can only move off the board through an exit square.");
        }
      }
    }
    else {
      // Some tiles face-down => end game hasn't begun yet
      validateCoordsOnBoard(toX, toY);
    }
  }

  /* (non-Javadoc)
   * @see tallyho.model.Board#flipTile(int, java.awt.geom.Point2D)
   */
  public void flipTile(Team flippingTeam, Point2D position) {
    flipTile(flippingTeam, (int) position.getX(), (int) position.getY());
  }
  
  /* (non-Javadoc)
   * @see tallyho.model.Board#flipTile(int, int, int)
   */
  public void flipTile(Team flippingTeam, int xPos, int yPos) {
    // Check inputs
    if (Team.NEUTRAL.equals(flippingTeam)) {
      throw new IllegalArgumentException("Flipping team can't be neutral");
    }
    try {
      validateCoordsOnBoard(xPos, yPos);
    }
    catch (IllegalMoveException ex) {
      // Caller should know better than to flip an off-board tile
      throw new IllegalArgumentException(ex.getMessage());
    }
    
    Tile tile = getTile(xPos, yPos);
    if (tile == null)
      throw new IllegalArgumentException(
          "There is no tile at those coordinates.");
    tile.setFaceUp();
    
    // Update the just-flipped neutral tile
    if (Team.NEUTRAL.equals(tile.getTeam())) {
      // This tile is neutral - remember it was just flipped
      justFlippedNeutralTile = tile;
    }
    else {
      // The tile wasn't neutral - remember no neutral tile was just flipped
      justFlippedNeutralTile = null;
    }

    // Remember this team didn't just move one of their own tiles
    if (Team.HUMANS.equals(flippingTeam)) {
      justMovedHuman = null;
    }
    else {
      justMovedPredator = null;
    }
    
    // Perform the end-of-turn processing
    endTurn(new Flip(flippingTeam, xPos, yPos, tile));
  }

  /**
   * The given team skips their turn. Only possible if
   * there are no valid moves.
   * 
   * @param passingTeam one of Team.HUMANS or Team.PREDATORS
   */
  void pass(Team passingTeam) {
    // Clear the just-flipped neutral tile
    justFlippedNeutralTile = null;
    
    // Clear the record of any own tile this team last moved
    switch (passingTeam) {
      case HUMANS:
        justMovedHuman = null;
        break;
      case PREDATORS:
        justMovedPredator = null;
        break;
      default:
        throw new IllegalArgumentException("Invalid team: " + passingTeam);
    }
    
    endTurn(null);
  }
  
  /**
   * Performs any processing needed at the end of a turn (that is, after a tile
   * has been flipped or moved, or a player has passed).
   * 
   * @param turn the action (flip or move) that happened this turn. Can't be a
   * pass.
   */
  private void endTurn(Turn turn) {
    if (turn instanceof Pass)
      throw new IllegalArgumentException("Turn can't be a pass");
    
    // Check if all tiles are now face up
    checkAllTilesFaceUp();

    // Notify the observers that the board position changed
    if (!isClone) {
      setChanged();
      notifyObservers(turn);
    }
  }
  
  /**
   * Check if all tiles on the board are face-up, and sets
   * the local flag accordingly 
   */
  private void checkAllTilesFaceUp() {
    for (int x = 0; x <= maxIndex; x++) {
      for (int y = 0; y <= maxIndex; y++) {
        Tile tile = getTile(x, y);
        if (tile != null && !tile.isFaceUp()) {
          // Found a face-down tile
          allTilesFaceUp = false;
          // No need to look further
          return;
        }
      }
    }
    allTilesFaceUp = true;
  }
  
  /* (non-Javadoc)
   * @see tallyho.model.Board#getJustFlippedNeutralTile()
   */
  public Tile getJustFlippedNeutralTile() {
    return justFlippedNeutralTile;
  }
  
	/* (non-Javadoc)
   * @see tallyho.model.Board#getMaxIndex()
   */
	public int getMaxIndex() {
		return maxIndex;
	}

  /* (non-Javadoc)
   * @see tallyho.model.Board#getTile(java.awt.geom.Point2D)
   */
  public Tile getTile(Point2D point) {
    if (point == null)
      throw new IllegalArgumentException("Point can't be null");
    return getTile((int) point.getX(), (int) point.getY());
  }
  
  /* (non-Javadoc)
   * @see tallyho.model.Board#getTile(int, int)
   */
  public Tile getTile(int xPos, int yPos) {
    try {
      validateCoordsOnBoard(xPos, yPos);
      // Coordinates are on the board - return the tile, if any
      return tiles[xPos][yPos];
    }
    catch (IllegalMoveException ex) {
      // Coordinates are off the board
      return null;
    }
  }

  /* (non-Javadoc)
   * @see tallyho.model.Board#haveTurn(tallyho.model.player.Player, tallyho.model.turn.Turn)
   */
  public void haveTurn(Player player, Turn turn) throws IllegalMoveException {
    // Check input
    if (turn == null || turn instanceof Pass) {
      throw new IllegalArgumentException("Turn can't be null");
    }
    else if (player == null) {
      throw new IllegalArgumentException("Player can't be null");
    }
    else if (turn instanceof Flip) {
      Flip flip = (Flip) turn;
      flipTile(player.getTeam(), flip.getX(), flip.getY());
    }
    else {
      // Must be a movement
      Move move = (Move) turn;
      moveTile(player, move.getFromX(), move.getFromY(),
          move.getToX(), move.getToY());
    }
  }
  
  /**
   * Reports whether both the Human and Predator teams have
   * tiles left on the board (this is a game-ending condition).
   * Does not check if such tiles are face-up, because this is
   * irrelevant for this game-ending condition, which only
   * comes into force during the end-game when all tiles are
   * known to be face-up anyway.
   * 
   * @return see above
   */
  boolean areBothTeamsRepresented() {
    boolean humansRepresented = false;
    boolean predatorsRepresented = false;
    // Loop through the tiles on the board
    Iterator iter = getIterator();
    while (iter.hasNext()) {
      Tile tile = (Tile) iter.next();
      if (tile != null) {
        Team team = tile.getTeam();
        if (Team.HUMANS.equals(team)) {
          humansRepresented = true;
        }
        else if (Team.PREDATORS.equals(team)) {
          predatorsRepresented = true;
        }
        if (humansRepresented && predatorsRepresented) {
          // No need to check any further
          return true;
        }
      }
    }
    return false;
  }
  
  /* (non-Javadoc)
   * @see tallyho.model.Board#isTurnPossible(Team)
   */
  public boolean isTurnPossible(Team team) {
    // Check input
    if (Team.NEUTRAL.equals(team)) {
      throw new IllegalArgumentException("Neutral team does not have turns.");
    }
    
    if (allTilesFaceUp) {
      // Flipping isn't possible - check movement
      return isMovementPossible(team);
    }

    // Some tiles are face down - a turn is possible
    return true;
  }
  
  /**
   * Reports whether the given team can legally move a tile
   * 
   * @param team the team moving the tile, either Team.HUMANS or Team.PREDATORS
   * @return see above
   */
  private boolean isMovementPossible(Team team) {
    // Loop through all the squares on the board
    for (int y = 0; y <= maxIndex; y++) {
      for (int x = 0; x <= maxIndex; x++) {
        if (isMovementPossible(team, new Point(x, y)))
          return true;
      }
    }
    // Looked through all the tiles but none were movable
    return false;
  }

  /* (non-Javadoc)
   * @see tallyho.model.Board#getPossibleTurns(Team)
   */
  public Collection<Turn> getPossibleTurns(Team team) {
    Collection<Turn> allMoves = new ArrayList<Turn>();
    for (int y = 0; y <= maxIndex; y++) {
      for (int x = 0; x <= maxIndex; x++) {
        Tile tile = getTile(x, y);
        if (tile != null) {
          if (!tile.isFaceUp()) {
            allMoves.add(new Flip(team, x ,y, tile));
          }
          else {
            allMoves.addAll(getPossibleMoves(team, x, y));
          }
        }
      }
    }
    return allMoves;
  }
  
  /* (non-Javadoc)
   * @see tallyho.model.Board#getPossibleMoves(int, int, int)
   */
  public Collection<Move> getPossibleMoves(
      Team movingTeam, int fromX, int fromY)
  {
    Tile tile = getTile(fromX, fromY);
    return getPossibleMoves(movingTeam, tile, fromX, fromY);
  }
  
  /**
   * Returns the moves (not flips) that the given tile can legally make, purely
   * based on the board position, i.e. ignoring who owns it and whether it's
   * the just-moved neutral tile.
   * 
   * @param movingTeam the team moving the tile, one of Team.HUMANS or
   *   Team.PREDATORS
   * @param tile if <code>null</code> or face-down, the returned Collection will
   *   be empty
   * @param fromX zero-indexed, from 0 to maxIndex
   * @param fromY zero-indexed, from 0 to maxIndex
   * @return a non-null Collection of Move objects
   * @throws IllegalArgumentException if the given coordinates aren't on the
   *   board
   */
  Collection<Move> getPossibleMoves(
      Team movingTeam, Tile tile, int fromX, int fromY)
  {
    // Check given origin is valid
    try {
      validateCoordsOnBoard(fromX, fromY);
    }
    catch (IllegalMoveException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }

    // Create a collection to hold the moves
    Collection<Move> moves = new ArrayList<Move>();
    if (tile != null && tile.isFaceUp() && tile.getRange() > 0) {
      // Add x-axis moves
      moves.addAll(getPossibleMoves(movingTeam, fromX, fromY, true));
      // Add y-axis moves
      moves.addAll(getPossibleMoves(movingTeam, fromX, fromY, false));
    }
    return moves;
  }

  /**
   * Returns the point value of moving the tile from/to the given coordinates.
   * Assumes such a move is legal (including moving off-board in endgame).
   * 
   * @param fromX
   * @param fromY
   * @param toX
   * @param toY
   * @return see above
   */
  private int getScore(int fromX, int fromY, int toX, int toY) {
    Tile movingTile = getTile(fromX, fromY);
    if (movingTile == null)
      throw new IllegalArgumentException("No tile at 'from' location");
    return getScore(movingTile, toX, toY);
  }
  
  /**
   * Returns the point value of the given tile moving to the given coordinates.
   * Assumes such a move is legal (including moving off-board in endgame).
   * 
   * @param movingTile can't be null
   * @param toX
   * @param toY
   * @return see above
   */
  private int getScore(Tile movingTile, int toX, int toY) {
    // Check input
    if (movingTile == null)
      throw new IllegalArgumentException("Moving tile can't be null");
    
    // Check if moving off-board
    try {
      Tile capturedTile = tiles[toX][toY];
      // Moving on-board
      if (capturedTile == null)
        return 0;
      return capturedTile.getValue(); 
    }
    catch (ArrayIndexOutOfBoundsException e) {
      // Off-board -> the moving tile is being rescued in the end-game
      return movingTile.getValue();
    }
  }
  
  /**
   * Returns the moves that the given tile can legally make, in one axis.
   * 
   * @see BoardImpl#getPossibleMoves(Team, Tile, int, int)
   * @param movingTeam the team moving the tile, either Team.HUMANS or
   *   Team.PREDATORS
   * @param fromX zero-indexed, from 0 to maxIndex
   * @param fromY zero-indexed, from 0 to maxIndex
   * @param xAxis if false, checks in the Y-axis
   * @return a non-null Collection of Move objects
   */
  private Collection<Move> getPossibleMoves(Team movingTeam, int fromX,
      int fromY, boolean xAxis)
  {
    // Create a collection to hold the moves
    Collection<Move> moves = new ArrayList<Move>();
    
    // Get the moves in the given axis; N.B. we also check for
    // moving off the board
    if (xAxis) {
      // x-axis
      for (int toX = -1; toX <= maxIndex + 1; toX++) {
        try {
          validateMove(movingTeam, fromX, fromY, toX, fromY);
          // The move is valid - build it and add it to the List
          int score = getScore(fromX, fromY, toX, fromY);
          boolean isRescue = toX < 0 || toX > maxIndex;
          if (isRescue) {
            moves.add(new Rescue(movingTeam, fromX, fromY, toX, fromY, score));
          }
          else {
            moves.add(new Move(movingTeam, fromX, fromY, toX, fromY, score));
          }
        }
        catch (IllegalMoveException ex) {
          // No problem - just don't add this move to the list
        }
      }
    }
    else {
      // y-axis
      for (int toY = -1; toY <= maxIndex + 1; toY++) {
        try {
          validateMove(movingTeam, fromX, fromY, fromX, toY);
          int score = getScore(fromX, fromY, fromX, toY);
          boolean isRescue = toY < 0 || toY > maxIndex;
          if (isRescue)
            moves.add(new Rescue(movingTeam, fromX, fromY, fromX, toY, score));
          else
            moves.add(new Move(movingTeam, fromX, fromY, fromX, toY, score));
        }
        catch (IllegalMoveException ex) {
          // No problem - just don't add this move to the list
        }
      }
    }
    
    return moves;
  }
  
  /* (non-Javadoc)
   * @see tallyho.model.Board#moveTile(tallyho.model.player.Player, java.awt.geom.Point2D, java.awt.geom.Point2D)
   */
  public void moveTile(Player movingPlayer, Point2D from, Point2D to)
    throws IllegalMoveException
  {
    // Check the inputs that are unique to this method signature
    if (from == null || to == null)
      throw new IllegalArgumentException("From and to can't be null");
    moveTile(movingPlayer,
        (int) from.getX(), (int) from.getY(), (int) to.getX(), (int) to.getY());
  }
  
  /* (non-Javadoc)
   * @see tallyho.model.Board#moveTile(tallyho.model.player.Player, int, int, int, int)
   */
  public void moveTile(Player movingPlayer, int fromX, int fromY, int toX, int toY)
    throws IllegalMoveException
  {
    validateMove(movingPlayer.getTeam(), fromX, fromY, toX, toY);

    // The move is valid - get the tiles
    Tile capturedTile = removeTile(toX, toY);
    Tile movingTile = getTile(fromX, fromY);
    int score = 0;
    boolean isRescue = false;
   
    // Move the moving tile
    removeTile(fromX, fromY);
    try {
      tiles[toX][toY] = movingTile;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      // Tile is being moved off board, i.e. rescued
      score = movingTile.getValue();
      movingPlayer.addToScore(score);
      isRescue = true;
    }
    
    // Remember that the previous move didn't flip a neutral tile
    justFlippedNeutralTile = null;
    
    // Remember whether either player moved one of their own tiles
    setPreviouslyMovedOwnTile(
        movingPlayer.getTeam(), movingTile, fromX, fromY, toX, toY);

    if (capturedTile != null) {
      // Increment the player's score
      score = capturedTile.getValue();
      movingPlayer.addToScore(score);
    }
    
    // Perform the end-of-turn processing
    if (isRescue)
      endTurn(new Rescue(movingPlayer.getTeam(), fromX, fromY, toX, toY, score));
    else
      endTurn(new Move(movingPlayer.getTeam(), fromX, fromY, toX, toY, score));
  }
  
  /**
   * Sets up the tiles on the board ready for a new round to start
   */
  void setUpTiles() {
    Iterator iter = getShuffledTiles().iterator();
    for (int y = 0; y <= maxIndex; y++) {
      for (int x = 0; x <= maxIndex; x++) {
        if (x == centre && y == centre) {
          // Clear this square
          tiles[x][y] = null;
        }
        else {
          // Not the centre square
          if (iter.hasNext()) {
            // There is a tile to place - place it
            tiles[x][y] = (Tile) iter.next();
          }
        }
      }
    }
    
    // Reset the flags and pointers
    allTilesFaceUp = false;
    justFlippedNeutralTile = null;
    justMovedHuman = null;
    justMovedPredator = null;
  }

  /**
   * Returns a shuffled List of the full set of tiles
   * 
   * @return see above
   */
  private List<Tile> getShuffledTiles() {
    List<Tile> shuffledTiles = new ArrayList<Tile>();
    for (int i = 0; i < BEARS; i++)
      shuffledTiles.add(new Bear());
    for (int i = 0; i < FOXES; i++)
      shuffledTiles.add(new Fox());
    for (int i = 0; i < LUMBERJACKS; i++)
      shuffledTiles.add(new Lumberjack());
    for (int i = 0; i < HUNTERS; i++)
      shuffledTiles.add(new Hunter());
    for (int i = 0; i < PHEASANTS; i++)
      shuffledTiles.add(new Pheasant());
    for (int i = 0; i < DUCKS; i++)
      shuffledTiles.add(new Duck());
    for (int i = 0; i < TREES; i++)
      shuffledTiles.add(new Tree());
    Collections.shuffle(shuffledTiles);
    return shuffledTiles;
  }
  
  /**
   * Updates the pointers storing the locations (if any) from where
   * each player moved one of their own tiles on their previous turn
   * (unless they moved a neutral tile, in which case the pointer is
   * cleared)
   * 
   * @param movingTeam the team moving the tile, either HUMANS or PREDATORS
   * @param movingTile the tile being moved, assumed not to be null
   * @param fromX zero-indexed, assumed to be valid
   * @param fromY zero-indexed, assumed to be valid
   * @param toX zero-indexed, assumed to be valid
   * @param toY zero-indexed, assumed to be valid
   */
  private void setPreviouslyMovedOwnTile(
      Team movingTeam, Tile movingTile, int fromX, int fromY, int toX, int toY)
  {
    // Update the pointers
    switch (movingTeam) {
      case HUMANS:
        if (Team.HUMANS.equals(movingTile.getTeam())) {
          justMovedHuman = new Move(movingTeam, fromX, fromY, toX, toY, 0);
        }
        else {
          justMovedHuman = null;
        }
        break;
      case PREDATORS:
        if (Team.PREDATORS.equals(movingTile.getTeam())) {
          justMovedPredator = new Move(movingTeam, fromX, fromY, toX, toY, 0);
        }
        else {
          justMovedPredator = null;
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid team: " + movingTeam);
    }
  }

  /* (non-Javadoc)
   * @see tallyho.model.Board#validateMove(int, int, int, int, int)
   */
  public void validateMove(Team movingTeam, int fromX, int fromY, int toX, int toY)
    throws IllegalMoveException
  {
    // Check that inputs make sense at all
    validateCoordsOnBoard(fromX, fromY);
    validateDestination(toX, toY);
    if (Team.NEUTRAL.equals(movingTeam)) {
      throw new IllegalArgumentException("Moving team can't be neutral.");
    }
    
    // Check there is a tile to move
    if (getTile(fromX, fromY) == null)
      throw new IllegalMoveException("Can't move a tile that isn't there.");
    
    // Check the moving tile is face-up
    if (!getTile(fromX, fromY).isFaceUp())
      throw new IllegalMoveException("Can't move a face-down tile.");
    
    // Check the tile is moving to another square
    if (fromX == toX && fromY == toY)
      throw new IllegalMoveException("Tile must move at least one square.");
    
    // Check the move is in the same rank or file (i.e. straight)
    // N.B. some of the subsequent checks depend on this assumption
    if (fromX != toX && fromY != toY)
      throw new IllegalMoveException("Tiles can't move diagonally.");

    // Check the tile has sufficient range
    validateRange(fromX, fromY, toX, toY);

    // Check there's no tiles in the way
    validateNoInterveningTiles(fromX, fromY, toX, toY);
    
    // Check the destination tile is valid prey
    validatePrey(fromX, fromY, toX, toY);
    
    // Check the tile doesn't belong to the other player
    validateNotOtherTeamsTile(movingTeam, fromX, fromY);
    
    // Check the tile isn't a neutral tile that was just flipped
    validateNotJustFlippedNeutralTile(fromX, fromY);
    
    // Check the player isn't reversing a previous move of their own tile
    validateNotReversingPreviousOwnTile(movingTeam, fromX, fromY, toX, toY);
    
    // Check the player isn't trying to rescue a neutral tile
    validateNotRescuingNeutralTile(fromX, fromY, toX, toY);
  }
  
  /**
   * Checks that the given move isn't a neutral tile being rescued (that is,
   * moved off the board)
   * 
   * @param fromX zero-indexed
   * @param fromY zero-indexed
   * @param toX zero-indexed
   * @param toY zero-indexed
   * @throws IllegalMoveException if the move is invalid
   */
  private void validateNotRescuingNeutralTile(
      int fromX, int fromY, int toX, int toY)
    throws IllegalMoveException
  {
    try {
      validateCoordsOnBoard(toX, toY);
      // Destination is on-board - do nothing else
    }
    catch (IllegalMoveException ex) {
      // Given destination is off-board - check the tile isn't neutral
      Tile movingTile = getTile(fromX, fromY);
      if (Team.NEUTRAL.equals(movingTile.getTeam())) {
        throw new IllegalMoveException("Can't rescue a neutral tile.");
      }
    }
  }

  /**
   * Checks that the given move doesn't reverse this player's previous
   * move of one of their own tiles (which the rules don't allow)
   * 
   * @param movingTeam the team moving the tile, one of Team.HUMANS or
   *   Team.PREDATORS
   * @param fromX zero-indexed
   * @param fromY zero-indexed
   * @param toX zero-indexed
   * @param toY zero-indexed
   * @throws IllegalMoveException if the move is invalid
   */
  private void validateNotReversingPreviousOwnTile(
      Team movingTeam, int fromX, int fromY, int toX, int toY)
    throws IllegalMoveException
  {
    Tile movingTile = getTile(fromX, fromY);
    if (movingTile.getTeam() == movingTeam) {
      // The active player owns the moving tile - check it's not
      // a reversal of the last time they moved one of their own tiles
      Move previousMove = justMovedHuman;
      if (Team.PREDATORS.equals(movingTeam)) {
        previousMove = justMovedPredator;
      }
      
      Move thisMove = new Move(movingTeam, fromX, fromY, toX, toY, 0);

      if (thisMove.reverses(previousMove)) {
        throw new IllegalMoveException(
          "Can't immediately reverse a move of your own tile.");
      }
    }
  }

  /**
   * Checks that the tile at the given coordinates isn't a neutral
   * tile that was flipped on the previous turn. The rules don't allow
   * such a tile to be moved on the next turn.
   *  
   * @param xPos
   * @param yPos
   * @throws IllegalMoveException
   */
  private void validateNotJustFlippedNeutralTile(int xPos, int yPos)
    throws IllegalMoveException
  {
    Tile tile = getTile(xPos, yPos);
    if (tile == justFlippedNeutralTile) {
      throw new IllegalMoveException(
        "Can't move a neutral tile flipped on the previous turn.");
    }
  }

  /**
   * Checks that the tile at the given coordinates doesn't belong to
   * the other player.
   * 
   * @param movingTeam the team moving the tile, one of Team.HUMANS or
   *   Team.PREDATORS
   * @param xPos zero-indexed
   * @param yPos zero-indexed
   * @throws IllegalMoveException if the tile is owned by the other team
   */
  private void validateNotOtherTeamsTile(Team movingTeam, int xPos, int yPos)
    throws IllegalMoveException
  {
    Tile tile = getTile(xPos, yPos);
    if (tile != null) {
      Team tileTeam = tile.getTeam();
      if (!Team.NEUTRAL.equals(tileTeam) && !movingTeam.equals(tileTeam)) {
        throw new IllegalMoveException(
          "Can't move a tile owned by the other player.");
      }
    }
  }

  /**
   * Checks that the tile at the given "from" coordinates is allowed
   * to capture the tile (if any) at the given "to" coordinates.
   * 
   * @param fromX zero-indexed
   * @param fromY zero-indexed
   * @param toX zero-indexed
   * @param toY zero-indexed
   * @throws IllegalMoveException if the capture is not legal
   */
  private void validatePrey(int fromX, int fromY, int toX, int toY)
    throws IllegalMoveException
  {
    Tile capturedTile = getTile(toX, toY);
    if (capturedTile != null) {
      if (!capturedTile.isFaceUp())
        throw new IllegalMoveException("Can't capture a face-down tile.");
      
      Tile capturingTile = getTile(fromX, fromY);
      if (!capturingTile.canCapture(capturedTile)) {
        StringBuffer errorMessage = new StringBuffer("A ");
        errorMessage.append(capturingTile.getName());
        errorMessage.append(" can't capture a ");
        errorMessage.append(capturedTile.getName());
        errorMessage.append(".");
        throw new IllegalMoveException(errorMessage.toString());
      }
      
      // If directional, check facing right way for capture
      if (capturingTile instanceof Directional) {
        Directional directionalTile = (Directional) capturingTile; 
        switch (directionalTile.getDirection()) {
          case Directional.BIG_X:
            if (fromX >= toX)
              throw new IllegalMoveException(MSG_WRONG_DIRECTION);
            break;
          case Directional.BIG_Y:
            if (fromY >= toY)
              throw new IllegalMoveException(MSG_WRONG_DIRECTION);
            break;
          case Directional.SMALL_X:
            if (fromX <= toX)
              throw new IllegalMoveException(MSG_WRONG_DIRECTION);
            break;
          case Directional.SMALL_Y:
            if (fromY <= toY)
              throw new IllegalMoveException(MSG_WRONG_DIRECTION);
            break;
          default:
            throw new IllegalStateException(
                "Invalid direction: " + directionalTile.getDirection());
        }
      }
    }
  }

  /**
   * Checks that the tile at the given "from" coordinates has sufficient
   * range to move to the given "to" coordinates. Assumes that the given
   * coordinates represent a move along a single rank or file.
   *
   * @param fromX zero-indexed
   * @param fromY zero-indexed
   * @param toX zero-indexed
   * @param toY zero-indexed
   * @throws IllegalMoveException if the tile doesn't have the range
   */
  private void validateRange(int fromX, int fromY, int toX, int toY)
    throws IllegalMoveException
  {
    int distance = Math.abs(fromX - toX);
    if (distance == 0)
      distance = Math.abs(fromY - toY);
    Tile movingTile = getTile(fromX, fromY);
    if (distance > movingTile.getRange()) {
      StringBuffer errorMessage = new StringBuffer("Can't move a ");
      errorMessage.append(movingTile.getName());
      errorMessage.append(" ");
      errorMessage.append(distance);
      errorMessage.append(" ");
      errorMessage.append(" spaces; maximum range is ");
      errorMessage.append(movingTile.getRange());
      errorMessage.append(".");
      throw new IllegalMoveException(errorMessage.toString());
    }
  }
  
  /**
   * Checks there are no tiles in between the given start and end
   * coordinates. Assumes these coordinates represent a move along
   * a single rank or file.
   * 
   * @param fromX zero-indexed
   * @param fromY zero-indexed
   * @param toX zero-indexed
   * @param toY zero-indexed
   * @throws IllegalMoveException if there are any tiles in between
   */
  void validateNoInterveningTiles(
    int fromX, int fromY, int toX, int toY) throws IllegalMoveException
  {
    if (fromX == toX) {
      // Check Y-axis
      for (int y = Math.min(fromY, toY) + 1; y < Math.max(fromY, toY); y++) {
        Tile interveningTile = getTile(fromX, y); 
        if (interveningTile != null) {
          // Generate an error message
          StringBuffer errorMessage = new StringBuffer("There is ");
          if (interveningTile.isFaceUp()) {
            errorMessage.append("a ");
            errorMessage.append(interveningTile.getName());
          }
          else {
            errorMessage.append("something");
          }
          errorMessage.append(" in the way.");
          throw new IllegalMoveException(errorMessage.toString());
        }
      }
    }
    else if (fromY == toY) {
      // Check X-axis
      for (int x = Math.min(fromX, toX) + 1; x < Math.max(fromX, toX); x++) {
        Tile interveningTile = getTile(x, fromY); 
        if (interveningTile != null) {
          throw new IllegalMoveException(
            "There is a " + interveningTile.getName() + " in the way.");
        }
      }
    }
    else {
      throw new IllegalArgumentException("Move isn't straight.");
    }
  }
  
  /**
   * Removes the Tile at the given coordinates. For internal use only,
   * because during the game, tiles are removed only by capture (in which
   * case the moveTile() method is called) or by rescue (in which case the
   * rescueTile() method is called).
   * 
   * @param xPos zero-indexed
   * @param yPos zero-indexed
   * @return the removed Tile - null if there wasn't one or the location was off-board
   */
  private Tile removeTile(int xPos, int yPos) {
    try {
      Tile removedTile = tiles[xPos][yPos];
      tiles[xPos][yPos] = null;
      return removedTile;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      // Coords were off-board
      return null;
    }
  }
  
  /* (non-Javadoc)
   * @see tallyho.model.Board#getIterator()
   */
  public Iterator getIterator() {
    return new BoardIterator(this);
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuffer buf = new StringBuffer();
    for (int yPos = 0; yPos <= maxIndex; yPos++) {
      for (int xPos = 0; xPos <= maxIndex; xPos++) {
        Tile tile = getTile(xPos, yPos);
        String tileStr = "(empty)";
        if (tile != null)
          tileStr = tile.getName();
        buf.append(tileStr);
        if (xPos < maxIndex) {
          // Pad with spaces to a length of 11
          for (int i = 0; i < 11 - tileStr.length(); i++)
              buf.append(' ');
        }
      }
      // End of row
      if (yPos < maxIndex)
        buf.append('\n');
    }
    return buf.toString();
  }

  /* (non-Javadoc)
   * @see tallyho.model.Board#isMovementPossible(int, java.awt.geom.Point2D)
   */
  public boolean isMovementPossible(Team team, Point2D from) {
    int fromX = (int) from.getX();
    int fromY = (int) from.getY();
    Tile tile = getTile(fromX, fromY);
    // We check these conditions from quickest to slowest
    return
      tile != null
      && tile != justFlippedNeutralTile
      && (team.equals(tile.getTeam()) || Team.NEUTRAL.equals(tile.getTeam()))
      && !getPossibleMoves(team, tile, fromX, fromY).isEmpty();
  }
  
  /**
   * @see java.lang.Object#clone()
   * @throws CloneNotSupportedException only if the superclass does
   */
  public Object clone() throws CloneNotSupportedException {
    // Perform any cloning done by the superclass
    BoardImpl clone = (BoardImpl) super.clone();
    
    // Clone the array of tiles
    clone.tiles = new Tile[maxIndex + 1][maxIndex + 1];
    for (int y = 0; y < tiles.length; y++) {
      for (int x = 0; x < tiles.length; x++) {
        AbstractTile originalTile = (AbstractTile) tiles[x][y];
        if (originalTile != null)
          clone.tiles[x][y] = (Tile) originalTile.clone();
      }
    }
    
    // N.B. because Observable.clone() is poorly implemented, the clone gets
    // the same observers as the original. The observers therefore need to be
    // careful to check which instance is notifying them of a change. See
    // http://www.adtmag.com/java/articleold.asp?id=223 for more info.
    clone.isClone = true;
    
    // Return the clone
    return clone;
  }
  
  /**
   * Reports whether this board is a clone - only necessary for unit testing
   * 
   * @return Returns the isClone.
   */
  boolean isClone() {
    return isClone;
  }
}