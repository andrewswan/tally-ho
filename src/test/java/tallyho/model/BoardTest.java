/*
 * Created on 18/08/2004
 */
package tallyho.model;

import java.awt.Point;
import java.util.Iterator;

import junit.framework.TestCase;
import tallyho.model.player.Player;
import tallyho.model.player.RealPlayer;
import tallyho.model.tile.Bear;
import tallyho.model.tile.Directional;
import tallyho.model.tile.Duck;
import tallyho.model.tile.Fox;
import tallyho.model.tile.Hunter;
import tallyho.model.tile.Lumberjack;
import tallyho.model.tile.Tile;
import tallyho.model.tile.Tree;

/**
 * Tests the data model for a Tally Ho game board
 */
public class BoardTest extends TestCase {

  // Constants
  private static final int
    BOARD_SIZE = 7, // same as default size, coincidentally
    CENTRE = (BOARD_SIZE - 1) / 2,
    HUNTER_DIRECTION = Directional.BIG_Y;
  
  // Fixture
  private Game game;
  private Player predatorsPlayer;
  private Player humansPlayer;
  private BoardImpl board;
  private Tile bear;
  private Tile duck;
  private Tile fox;
  private Hunter hunter;
  private Tile lumberjack;
  private Tile tree;
  private int maxBoardIndex;

  /**
   * Constructor
   * 
   * @param testMethod can't be <code>null</code>
   */
  public BoardTest(String testMethod) {
    super(testMethod);
  }

  /**
   * @see junit.framework.TestCase#setUp()
   */
  public void setUp() {
    bear = new Bear();
    predatorsPlayer = new RealPlayer();
    humansPlayer = new RealPlayer();
    game = new Game(predatorsPlayer, humansPlayer);
    board = new BoardImpl(game, BOARD_SIZE);    // starts empty
    duck = new Duck();
    fox = new Fox();
    // Generate a hunter facing in a known direction
    do {
      hunter = new Hunter();
    } while (hunter.getDirection() != HUNTER_DIRECTION);
    lumberjack = new Lumberjack();
    tree = new Tree();
    maxBoardIndex = board.getMaxIndex();

    // Player 1
    predatorsPlayer.setName("Player 1");
    predatorsPlayer.setTeam(Team.PREDATORS);
    game.setPlayer(1, predatorsPlayer);

    // Player 2
    humansPlayer.setName("Player 2");
    humansPlayer.setTeam(Team.HUMANS);
    game.setPlayer(2, humansPlayer);
  }

  /**
   * Tests that you can't add a <code>null</code> Tile
   */
  public void testAddNullTile() {
    try {
      board.addTile(null, 0, 0);
      fail("Shouldn't be allowed to add a null Tile.");
    }
    catch (IllegalArgumentException expected) {
      // Success
    }
  }

  /**
   * Tests that the same tile can't be added twice to the board
   */
  public void testAddSameTileTwice() {
    board.addTile(tree, 0, 0);
    try {
      board.addTile(tree, maxBoardIndex, maxBoardIndex);
      fail("Shouldn't be allowed to add the same tile twice.");
    }
    catch (IllegalArgumentException expected) {
      // Success
    }
  }

  /**
   * Tests adding a tile with too small an X value
   */
  public void testAddTileNegativeX() {
    try {
      board.addTile(tree, -1, 0);
      fail("Shouldn't be able to add a tile at X = -1.");
    }
    catch (IllegalArgumentException expected) {
      // Success
    }
  }

  /**
   * Tests adding a tile with too small a Y value
   */
  public void testAddTileNegativeY() {
    try {
      board.addTile(tree, 0, -1);
      fail("Shouldn't be able to add a tile at Y = -1.");
    }
    catch (IllegalArgumentException expected) {
      // Success
    }
  }

  /**
   * Tests adding a tile with too big an X value
   */
  public void testAddTileTooBigX() {
    try {
      int illegalX = maxBoardIndex + 1;
      board.addTile(tree, illegalX, 0);
      fail("Shouldn't be able to add a tile at X = " + illegalX);
    }
    catch (IllegalArgumentException expected) {
      // Success
    }
  }

  /**
   * Tests adding a tile with too big a Y value
   */
  public void testAddTileTooBigY() {
    try {
      int tooBigY = maxBoardIndex + 1;
      board.addTile(tree, 0, tooBigY);
      fail("Shouldn't be able to add a tile at Y = " + tooBigY);
    }
    catch (IllegalArgumentException expected) {
      // Success
    }
  }

  /**
   * Tests adding a tile where one already is
   */
  public void testAddTileSamePosition() {
    board.addTile(tree, 0, 0);
    try {
      board.addTile(tree, 0, 0);
      fail("Shouldn't be able to add a tile where there already is one.");
    }
    catch (IllegalArgumentException expected) {
      // Success
    }
  }

  /**
   * Tests the logic that determines whether all the tiles are face up
   */
  public void testAllTilesFaceUp() {
    assertTrue("Empty board should have no face down tiles", board
        .areAllTilesFaceUp());

    int treeX = 0;
    int treeY = 0;
    board.addTile(tree, treeX, treeY);
    assertFalse("Tree should initially be face down", tree.isFaceUp());

    int foxX = 1;
    int foxY = 1;
    board.addTile(fox, foxX, foxY);
    assertFalse("Fox should initially be face down", fox.isFaceUp());

    assertFalse("All tiles should still be face down",
        board.areAllTilesFaceUp());

    board.flipTile(Team.HUMANS, treeX, treeY);
    assertFalse("Fox should still be face down", board.areAllTilesFaceUp());

    board.flipTile(Team.PREDATORS, foxX, foxY);
    assertTrue("All tiles should now be face up", board.areAllTilesFaceUp());
  }

  /**
   * Tests the logic that determines whether both teams are represented
   */
  public void testBothTeamsRepresented() {
    // Add a neutral tile
    board.addTile(duck, 0, 0);
    assertFalse("Shouldn't be true with no tiles on the board",
        board.areBothTeamsRepresented());
    
    // Add one team (predators)
    board.addTile(bear, 1, 1);
    assertFalse("Shouldn't be true with no humans on the board",
        board.areBothTeamsRepresented());
    
    // Add the other team (humans)
    board.addTile(hunter, 2, 2);
    assertTrue("Should be true with both teams on the board",
        board.areBothTeamsRepresented());
  }
  
  /**
   * Tests the method that reports whether the board contains a given tile
   */
  public void testContains() {
    // Test that tileA isn't on the board
    assertFalse("BoardImpl shouldn't contain the tile A", board.contains(tree));

    // Add the tile and check it's there
    board.addTile(tree, 0, 0);
    assertTrue("BoardImpl should contain tree A", board.contains(tree));
  }

  /**
   * Tests that an empty board has the correct behaviour
   */
  public void testEmptyBoard() {
    // Check there's no tiles
    Iterator iter = board.getIterator();
    while (iter.hasNext()) {
      if (iter.next() != null)
        fail("BoardImpl isn't empty");
    }
    
    // Check some other board behaviour
    assertTrue("There are no tiles to be face down", board.areAllTilesFaceUp());
    assertFalse("Neither team is represented", board.areBothTeamsRepresented());
    assertEquals(BOARD_SIZE - 1, board.getMaxIndex());
    assertFalse("Predators shouldn't be able to move",
        board.isMovementPossible(Team.PREDATORS, new Point(0, 0)));
    assertFalse("Humans shouldn't be able to move",
        board.isMovementPossible(Team.HUMANS, new Point(0, 0)));
  }

  /**
   * Tests the "flip a tile" functionality
   */
  public void testFlipTile() {
    int duckX = 0;
    int duckY = 0;
    assertFalse("Duck should start face-down", duck.isFaceUp());
    
    // Try to flip the empty square
    try {
      board.flipTile(Team.HUMANS, duckX, duckY);
      fail("Shouldn't be able to flip an empty square");
    }
    catch (IllegalArgumentException expected) {
      // Success
    }
    
    // Put the duck on face-down
    board.addTile(duck, duckX, duckY);
    assertFalse("Duck should still be face-down", duck.isFaceUp());
    
    // Flip it and check it's now face-up
    board.flipTile(Team.HUMANS, duckX, duckY);
    assertTrue("Duck should now be face-up", duck.isFaceUp());
  }

  /**
   * Tests that the board returns a null Tile for coordinates where there is no
   * Tile
   */
  public void testGetNullTile() {
    // Place a tile in one spot
    board.addTile(tree, 1, 1);
    // Try to get a tile from another spot
    Tile tile = board.getTile(0, 0);
    assertNull("Retrieved tile is not null.", tile);
  }

  /**
   * Tests that a tile placed on the board can be retrieved
   */
  public void testGetValidTile() {
    int testX = 0;
    int testY = 0;
    board.addTile(tree, testX, testY);
    Tile retrievedTile = board.getTile(testX, testY);
    assertSame(tree, retrievedTile);
  }

  /**
   * Tests that the board Iterator returns the correct number of elements
   */
  public void testHasNext() {
    int boardSize = maxBoardIndex + 1;
    int squares = boardSize * boardSize;
    Iterator iter = board.getIterator();
    int square;
    for (square = 1; square <= squares; square++) {
      assertTrue("Iterator should have a next item", iter.hasNext());
      iter.next();
    }
    // Now looking at last square
    assertFalse("Iterator should not have a next item", iter.hasNext());
  }

  /**
   * Tests that tiles can't move diagonally
   */
  public void testMoveDiagonally() {
    board.addTile(duck, 0, 0);
    try {
      duck.setFaceUp();
      board.moveTile(humansPlayer, 0, 0, 1, 1);
      fail("Shouldn't be able to move a tile diagonally");
    }
    catch (IllegalMoveException expected) {
      // Success
    }
  }

  /**
   * Tests that you can't move an empty square
   */
  public void testMoveEmptySquare() {
    try {
      board.moveTile(humansPlayer, 0, 0, 1, 1);
      fail("Shouldn't be able to move an empty square");
    }
    catch (IllegalMoveException expected) {
      // Success
    }
  }

  /**
   * Tests that you can't move a face-down tile
   */
  public void testMoveFaceDownTile() {
    int bearX = 0;
    int bearY = 0;
    board.addTile(bear, bearX, bearY);
    assertFalse("Bear should start face-down", bear.isFaceUp());
    try {
      board.moveTile(predatorsPlayer, bearX, bearY, bearX + 1, bearY);
      fail("Shouldn't be allowed to move a face-down tile");
    }
    catch (IllegalMoveException expected) {
      // Success
    }
  }

  /**
   * Tests that a hunter can move sideways when not capturing
   */
  public void testHunterMovingToEmptySquare() {
    // Set up a hunter, face-up, pointing towards BIG_Y
    int hunterX = 0;
    int hunterY = 0;
    board.addTile(hunter, hunterX, hunterY);
    board.flipTile(Team.PREDATORS, hunterX, hunterY);
    try {
      board.moveTile(humansPlayer, hunterX, hunterY, hunterX + 1, hunterY);
    }
    catch (IllegalMoveException ex) {
      fail("Hunter should be able to move sideways to empty square");
    }
  }

  /**
   * Tests that a hunter can capture when facing forwards
   */
  public void testHunterCapturingForwards() {
    // Set up a hunter, face-up, pointing towards BIG_Y
    int hunterX = 0;
    int hunterY = 1;
    board.addTile(hunter, hunterX, hunterY);
    assertEquals(Directional.BIG_Y, hunter.getDirection());
    
    // Place a duck face-up right in front of the hunter
    int duckX = hunterX;
    int duckY = hunterY + 1;
    board.addTile(duck, duckX, duckY);
    
    // Place a tree face-down in the far corner
    board.addTile(tree, maxBoardIndex, maxBoardIndex);
    
    // Make the moves
    board.flipTile(Team.PREDATORS, duckX, duckY);
    board.flipTile(Team.HUMANS, hunterX, hunterY);
    board.flipTile(Team.PREDATORS, maxBoardIndex, maxBoardIndex);
    
    try {
      // 4. Humans' move
      board.moveTile(humansPlayer, hunterX, hunterY, duckX, duckY);
      // Check the hunter is where the duck was
      assertSame(hunter, board.getTile(duckX, duckY));
    }
    catch (IllegalMoveException unexpected) {
      fail("Should be able to capture forwards");
    }
  }
  
  /**
   * Tests that a hunter can't capture sideways
   */
  public void testHunterCapturingSideways() {
    // Set up a hunter, face-up, pointing towards BIG_Y
    int hunterX = 1;
    int hunterY = 1;
    board.addTile(hunter, hunterX, hunterY);
    board.flipTile(Team.HUMANS, hunterX, hunterY);
    assertEquals(Directional.BIG_Y, hunter.getDirection());
    
    // Place a face-up duck immediately to the left of the hunter
    int duckX = hunterX - 1;
    int duckY = hunterY;
    board.addTile(duck, duckX, duckY);
    board.flipTile(Team.PREDATORS, duckX, duckY);
    
    // Capture the duck (illegal)
    try {
      board.moveTile(humansPlayer, hunterX, hunterY, duckX, duckY);
      fail("Hunters aren't allowed to capture sideways");
    }
    catch (IllegalMoveException expected) {
      // Success
    }
  }
  
  /**
   * Tests that a hunter can't capture backwards
   */
  public void testHunterCapturingBackwards() {
    // Set up a hunter, face-up, pointing towards BIG_Y
    int hunterX = 1;
    int hunterY = 1;
    board.addTile(hunter, hunterX, hunterY);
    board.flipTile(Team.HUMANS, hunterX, hunterY);
    assertEquals(Directional.BIG_Y, hunter.getDirection());
    
    // Place a face-up duck immediately behind the hunter
    int duckX = hunterX;
    int duckY = hunterY - 1;
    board.addTile(duck, duckX, duckY);
    board.flipTile(Team.PREDATORS, duckX, duckY);
    
    // Capture the duck (illegal)
    try {
      board.moveTile(humansPlayer, hunterX, hunterY, duckX, duckY);
      fail("Hunters aren't allowed to capture backwards");
    }
    catch (IllegalMoveException expected) {
      // Success
    }
  }
  
  /**
   * Tests that you can't move the neutral tile flipped on the previous turn
   */
  public void testMoveJustFlippedNeutralTile() {
    // Set up a bear
    int bearX = maxBoardIndex;
    int bearY = maxBoardIndex;
    board.addTile(bear, bearX, bearY);
    board.flipTile(Team.PREDATORS, bearX, bearY);
    
    // Set up a duck (neutral)
    int duckX = 0;
    int duckY = 0;
    board.addTile(duck, duckX, duckY);
    
    // Test sequence - first flip the neutral tile (duck)
    board.flipTile(Team.HUMANS, duckX, duckY);
    try {
      // Move the duck one square in the Y-axis (team doesn't matter)
      board.moveTile(predatorsPlayer, duckX, duckY, duckX, duckY + 1);
      fail("Shouldn't be allowed to move a just-flipped neutral tile");
    }
    catch (IllegalMoveException expected) {
      // Success, move was disallowed; now move the bear
      try {
        board.moveTile(predatorsPlayer, bearX, bearY, bearX, bearY - 1);
        // Try again to move the duck one square in the Y-axis
        board.moveTile(humansPlayer, duckX, duckY, duckX, duckY + 1);
      }
      catch (IllegalMoveException unexpected) {
        fail("Should be allowed to move these tiles");
      }
    }
  }
  
  /**
   * Tests that a move can't have zero distance
   */
  public void testMoveNowhere() {
    board.addTile(duck, 0, 0);
    duck.setFaceUp();
    try {
      board.moveTile(humansPlayer, 0, 0, 0, 0);
      fail("Shouldn't be able to move a tile no spaces");
    }
    catch (IllegalMoveException expected) {
      // Success
    }
  }

  /**
   * Tests that a tile can't be moved off the board, not through
   * an exit, even during the end game.
   */
  public void testMoveOutOfBounds() {
    // Place the bear, not on an exit axis
    int bearX = maxBoardIndex;
    int bearY = maxBoardIndex;
    
    board.addTile(bear, bearX, bearY);
    board.flipTile(Team.HUMANS, bearX, bearY);
    
    // Check we are in the end game
    assertTrue("All tiles should be face-up", board.areAllTilesFaceUp());
    
    // X-axis
    try {
      board.moveTile(predatorsPlayer, bearX, bearY, bearX + 1, bearY);
      fail("Shouldn't be able to move a tile out of bounds");
    }
    catch (IllegalMoveException expected) {
      // Success
    }
    // Y-axis
    try {
      board.moveTile(predatorsPlayer, bearX, bearY, bearX, bearY + 1);
      fail("Shouldn't be able to move a tile out of bounds");
    }
    catch (IllegalMoveException unexpected) {
      // Success
    }
  }

  /**
   * Tests that you can't move a tile that's on the other team
   */
  public void testMoveOtherTeamsTile() {
    board.addTile(bear, 0, 0);
    board.flipTile(Team.PREDATORS, 0, 0);
    try {
      board.moveTile(humansPlayer, 0, 0, 1, 0);
      fail("Human team shouldn't be able to move a bear");
    }
    catch (IllegalMoveException expected) {
      // Success
    }
  }

  /**
   * Tests the rule that a player can't reverse the movement
   * of one of their OWN tiles last turn.
   */
  public void testMoveOwnTileTwice() {
    // Add a human (hunter) in one corner
    int hunterX = 0;
    int hunterY = 0;
    board.addTile(hunter, hunterX, hunterY);
    
    // Add a neutral (duck) in the far corner
    int duckX = maxBoardIndex;
    int duckY = maxBoardIndex;
    board.addTile(duck, duckX, duckY);

    // Make the moves
    board.flipTile(Team.PREDATORS, duckX, duckY);     // Predators 1
    board.flipTile(Team.HUMANS, hunterX, hunterY); // Humans 1
    
    try {
      // Predators 2 - move the duck (legal), 3 squares down Y axis
      board.moveTile(predatorsPlayer, duckX, duckY, duckX, duckY - 3);
      // Humans 2 - move the human (legal), 2 squares up X axis 
      board.moveTile(humansPlayer, hunterX, hunterY, hunterX + 2, hunterY);
      // Predators 3 - move the duck back (legal), 3 squares up Y axis
      board.moveTile(predatorsPlayer, duckX, duckY - 3, duckX, duckY);
    }
    catch (IllegalMoveException ex) {
      fail("Should be able to move the hunter and duck the first time");
    }
    
    // Humans 3 - move the human back to same spot (illegal)
    try {
      board.moveTile(humansPlayer, hunterX + 2, hunterY, hunterX, hunterY);
      fail("Shouldn't be able to move hunter back to original square");
    }
    catch (IllegalMoveException expected) {
      // Success
    }
    
    // Humans 3 retry - instead moves the hunter back only one square (legal)
    try {
      board.moveTile(humansPlayer, hunterX + 2, hunterY, hunterX + 1, hunterY);
    }
    catch (IllegalMoveException ex) {
      fail("Should be able to move the hunter back to a different square");
    }
  }
  
  /**
   * Tests the "move exists" logic for a human in the X-axis
   */
  public void testHumanMoveExistsXAxis() {
    // Get ready to put the lumberjack in a corner and two animals next to him
    int lumberjackX = maxBoardIndex;
    int lumberjackY = maxBoardIndex;
    int bearX = lumberjackX;
    int bearY = lumberjackY - 1;
    int foxX = lumberjackX - 1;
    int foxY = lumberjackY;

    // Check the answer for an empty board
    assertFalse("No human move should exist", board.isTurnPossible(Team.HUMANS));
    
    // Add a bear (face-down) next to where the lumberjack will go
    board.addTile(bear, bearX, bearY);
    assertTrue("Human can flip the bear", board.isTurnPossible(Team.HUMANS));
    
    // Flip the bear
    board.flipTile(Team.HUMANS, bearX, bearY);
    assertFalse("No human move should exist", board.isTurnPossible(Team.HUMANS));
    
    // Put the lumberjack in the corner, face-down
    board.addTile(lumberjack, lumberjackX, lumberjackY);
    assertTrue("Human can flip the lumberjack", board.isTurnPossible(Team.HUMANS));

    // Flip the lumberjack
    board.flipTile(Team.PREDATORS, lumberjackX, lumberjackY);
    assertTrue("Human can move the lumberjack", board.isTurnPossible(Team.HUMANS));
    
    // Put a fox on the other side of him (face-down)
    board.addTile(fox, foxX, foxY);
    assertTrue("Human can flip the fox", board.isTurnPossible(Team.HUMANS));
    
    // Flip the fox
    board.flipTile(Team.HUMANS, foxX, foxY);
    assertFalse("No human move should exist", board.isTurnPossible(Team.HUMANS));
    
    // Add a duck (neutral) diagonally next to the lumberjack (face-down)
    int duckX = lumberjackX - 1;
    int duckY = lumberjackY - 1;
    board.addTile(duck, duckX, duckY);
    assertTrue("Human can flip the duck", board.isTurnPossible(Team.HUMANS));
    
    // Flip the duck (just-flipped neutral tile => can't be moved)
    board.flipTile(Team.PREDATORS, duckX, duckY);
    assertFalse("No human move should exist", board.isTurnPossible(Team.HUMANS));
    
    // Move some other tile
    try {
      board.moveTile(predatorsPlayer, bearX, bearY, bearX, bearY - 1);
    }
    catch (IllegalMoveException e) {
      fail("Should be allowed to move the bear -1 in y-axis");
    }
    
    // Check the duck can now be moved
    assertTrue("Human can move the duck", board.isTurnPossible(Team.HUMANS));
  }
 
  /**
   * Tests that a tile can't be moved through another tile, even
   * one that is face-down.
   */
  public void testMoveThroughTile() {
    int foxX = 0;
    int foxY = 0;
    board.addTile(fox, foxX, foxY);
    board.flipTile(Team.HUMANS, foxX, foxY);
    board.addTile(tree, foxX + 1, foxY);
    assertFalse("Tree should be face-down", tree.isFaceUp());
    try {
      // Move fox 2 spaces along the X-axis
      board.moveTile(predatorsPlayer, foxX, foxY, foxX + 2, foxY);
      fail("Shouldn't be able to move a tile through another tile");
    }
    catch (IllegalMoveException expected) {
      // Success
    }
  }

  /**
   * Tests that you can't move a tile beyond its range
   */
  public void testMoveTooFar() {
    int bearX = 0;
    int bearY = 0;
    board.addTile(bear, bearX, bearY);
    board.flipTile(Team.HUMANS, bearX, bearY);
    try {
      board.moveTile(predatorsPlayer, bearX, bearY, bearX + 2, bearY);
      fail("Shouldn't be able to move a bear two spaces");
    }
    catch (IllegalMoveException expected) {
      // Success
    }
  }

  /**
   * Tests that a moving tile can capture
   */
  public void testMoveWithCapture() {
    // Place a fox face-up
    int foxX = 0;
    int foxY = 0;
    board.addTile(fox, foxX, foxY);
    board.flipTile(Team.PREDATORS, foxX, foxY);
    
    // Place a face-up duck on the same X-axis as the fox
    int duckX = foxX;
    int duckY = maxBoardIndex;
    board.addTile(duck, duckX, duckY);
    board.flipTile(Team.HUMANS, duckX, duckY);
    
    // Fox eats duck?
    try {
      board.moveTile(predatorsPlayer, foxX, foxY, duckX, duckY);
      // Check the fox is now where the duck was
      assertSame(fox, board.getTile(duckX, duckY));
    }
    catch (Exception unexpected) {
      fail("Fox should be able to capture duck on same X-axis");
    }
  }
  
  /**
   * Tests the "intervening tiles" logic
   */
  public void testNoInterveningTiles() {
    // Place a tree in the middle
    board.addTile(tree, CENTRE, CENTRE);
    
    try {
      // Check illegal arguments aren't accepted
      board.validateNoInterveningTiles(CENTRE - 1, CENTRE - 1, CENTRE + 1, CENTRE);
      fail("Method shouldn't accept diagonal moves");
    }
    catch (IllegalArgumentException expected) {
      // Success
    }
    catch (IllegalMoveException ex) {
      fail("Shouldn't get this exception here");
    }
    
    try {
      // Check it's intervening between the squares on either side (X+)
      board.validateNoInterveningTiles(CENTRE - 1, CENTRE, CENTRE + 1, CENTRE);
      fail("Tree should be intervening in x-axis");
    }
    catch (IllegalMoveException expected) {
      // Success
    }
    
    try {
      // Check it's intervening between the squares on either side (Y-)
      board.validateNoInterveningTiles(CENTRE, CENTRE + 1, CENTRE, CENTRE - 1);
      fail("Tree should be intervening in y-axis");
    }
    catch (IllegalMoveException expected) {
      // Success
    }

    try {
      // Check it's not intervening on a different row (X)
      board.validateNoInterveningTiles(CENTRE - 1, CENTRE + 1, CENTRE + 1, CENTRE + 1);
    }
    catch (IllegalMoveException ex) {
      fail("Tree shouldn't be intervening in y-axis");
    }

    try {
      // Check it's not intervening on a different column (Y)
      board.validateNoInterveningTiles(CENTRE + 1, CENTRE - 1, CENTRE + 1, CENTRE + 1);
    }
    catch (IllegalMoveException ex) {
      fail("Tree shouldn't be intervening in y-axis");
    }
  }
  
  /**
   * Tests removing all tiles from the board
   */
  public void testRemoveAll() {
    // Put a tile in each corner
    board.addTile(duck, 0, 0);
    board.addTile(fox, 0, maxBoardIndex);
    board.addTile(hunter, maxBoardIndex, 0);
    board.addTile(tree, maxBoardIndex, maxBoardIndex);

    // Remove everything
    Iterator iter = board.getIterator();
    while (iter.hasNext()) {
      iter.next();
      iter.remove();
    }

    // Check the tiles are gone
    assertNull("Tile at (0,0) should be gone", board.getTile(0, 0));
    assertNull("Tile at (0," + maxBoardIndex + ") should be gone", board
        .getTile(0, maxBoardIndex));
    assertNull("Tile at (" + maxBoardIndex + ",0) should be gone", board
        .getTile(maxBoardIndex, 0));
    assertNull("Tile at (" + maxBoardIndex + "," + maxBoardIndex
        + ") should be gone", board.getTile(maxBoardIndex, maxBoardIndex));
  }
  
  /**
   * Tests that you can't move a tile off the board before the end-game has
   * commenced
   */
  public void testRescueTileBeforeEndGame() {
    int hunterX = 3;
    int hunterY = 0;
    board.addTile(hunter, hunterX, hunterY);
    board.flipTile(Team.PREDATORS, hunterX, hunterY);
    board.addTile(tree, 0, 0);
    assertFalse("Tree should begin face-down", tree.isFaceUp());
    try {
      board.moveTile(humansPlayer, hunterX, hunterY, hunterX, -1);
      fail("Shouldn't be allowed to move own tile off board before end game");
    }
    catch (IllegalMoveException expected) {
      // Success
    }
  }

  /**
   * Tests that you can move a tile off the board through an exit square once
   * the end-game has commenced
   */
  public void testRescueTileDuringEndGame() {
    int hunterX = 3;
    int hunterY = 0;
    board.addTile(hunter, hunterX, hunterY);
    board.flipTile(Team.PREDATORS, hunterX, hunterY);
    try {
      board.moveTile(humansPlayer, hunterX, hunterY, hunterX, -1);
    }
    catch (IllegalMoveException ex) {
      fail("Should be allowed to move own tile off board in end game");
    }
  }
  
  /**
   * Tests that the board is in the correct state
   * after having its tiles set up.
   */
  public void testSetUpTiles() {
    board.setUpTiles();

    // Check there's face-down tiles in all the right spots
    for (int y = 0; y < BOARD_SIZE; y++) {
      for (int x = 0; x < BOARD_SIZE; x++) {
        Tile tile = board.getTile(x, y);
        if (x == CENTRE && y == CENTRE) {
          assertNull("Centre tile should be null", tile);
        }
        else {
          assertNotNull("Non-centre tiles shouldn't be null", tile);
          assertFalse("Tiles should start face-down", tile.isFaceUp());
        }
      }
    }
    
    // Check some other board behaviour
    assertFalse("All the tiles should be face down", board.areAllTilesFaceUp());
    assertTrue("Both teams should be represented", board.areBothTeamsRepresented());
    assertFalse("Predators shouldn't be able to move",
        board.isMovementPossible(Team.PREDATORS, new Point(0, 0)));
    assertFalse("Humans shouldn't be able to move",
        board.isMovementPossible(Team.HUMANS, new Point(0, 0)));
  }

  /**
   * Tests that the board can be cloned (important for AI look-ahead)
   * 
   * @throws CloneNotSupportedException
   * @throws IllegalMoveException
   */
  public void testClone()
    throws CloneNotSupportedException, IllegalMoveException
  {
    // Add a face-up bear in the middle of one edge
    int bearX = CENTRE;
    int bearY = maxBoardIndex;
    board.addTile(bear, bearX, bearY);
    board.flipTile(Team.HUMANS, bearX, bearY);
    
    // Clone the board
    BoardImpl clone = (BoardImpl) board.clone();
    
    // Check the "isClone" flag on both objects
    assertFalse("Original shouldn't be a clone", board.isClone());
    assertTrue("Clone should be a clone", clone.isClone());
    
    // Check the placed bear has also been cloned
    assertNotSame(bear, clone.getTile(bearX, bearY));
    
    // Move the bear on the clone and check it didn't move on the original
    clone.moveTile(predatorsPlayer, bearX, bearY, bearX + 1, bearY);
    assertEquals(Bear.class, clone.getTile(bearX + 1, bearY).getClass());
    assertSame(bear, board.getTile(bearX, bearY));
  }
}