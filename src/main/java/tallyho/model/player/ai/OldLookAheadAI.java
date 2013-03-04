/*
 * Created on 24/09/2004
 */
package tallyho.model.player.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tallyho.model.Board;
import tallyho.model.IllegalMoveException;
import tallyho.model.Team;
import tallyho.model.player.AbstractPlayer;
import tallyho.model.player.Player;
import tallyho.model.turn.Turn;

/**
 * An AI that looks ahead a given number of moves
 */
public class OldLookAheadAI extends AbstractPlayer implements ComputerPlayer {

  // Constants
  private static final int DEFAULT_LOOK_AHEAD_DEPTH = 2;
  private static final boolean DEBUG = false;
  private static final Log LOG = LogFactory.getLog(LookAheadAI.class);
  
  // Properties
  // -- the number of turns after the current one to look ahead
  private final int turnsToLookAhead;
  
  // -- a dummy opponent whose scoring we factor into the net worth of a move
  private Player _opponent;
  
  /**
   * Constructor that looks ahead the default number of turns
   */
  public OldLookAheadAI() {
    this(DEFAULT_LOOK_AHEAD_DEPTH);
  }
  
  /**
   * Constructor
   * 
   * @param turnsToLookAhead the number of turns to look ahead, including turns
   *   by the other player. Can't be negative.
   */
  public OldLookAheadAI(int turnsToLookAhead) {
    if (turnsToLookAhead < 0)
      throw new IllegalArgumentException("Can't look ahead a negative number of turns");
    
    this.turnsToLookAhead = turnsToLookAhead;
    LOG.info("Constructed look-ahead AI with depth of " + turnsToLookAhead);
  }
  
  /**
   * Overridden because our opponent is determined by which team we are on
   * 
   * @see tallyho.model.player.Player#setTeam(Team)
   */
  public void setTeam(Team team) {
    super.setTeam(team);
    this._opponent = getOpponent();
  }
  
  /**
   * Creates a short-sighted (i.e. no look-ahead) opponent so that we can
   * factor the points it scores into our overall point value calculations.
   * 
   * @return see above
   */
  private Player getOpponent() {
    Player opponent = new ShortSightedAI();
    switch (getTeam()) {
      case HUMANS:
        opponent.setTeam(Team.PREDATORS);
        break;
      case PREDATORS:
        opponent.setTeam(Team.HUMANS);
        break;
      default:
        throw new IllegalStateException("Invalid team: " + getTeam());
    }
    return opponent;
  }

  /**
   * @see tallyho.model.player.Player#getType()
   */
  public String getType() {
    return "Look-Ahead AI";
  }
  
  /**
   * @see tallyho.model.player.ai.ComputerPlayer#getTurn(tallyho.model.BoardImpl)
   */
  public Turn getTurn(Board board) {
    LOG.info(">>> Getting turn for board:\n\n" + board);
    
    // Get the highest-scoring Paths out of those available
    List<Path> bestPaths = getBestPaths(board);
    if (bestPaths.isEmpty()) {
      throw new IllegalStateException("AI should have at least one valid move");
    }
    
    // Convert this list of Paths to a Set of turns, to prevent some Turns
    // being more likely to be chosen than others.
    Set<Turn> bestTurnSet = new HashSet<Turn>();
    Iterator<Path> paths = bestPaths.iterator();
    while (paths.hasNext()) {
      Path path = paths.next();
      LOG.info("One best path = " + path + ", first turn = " + path.getFirstTurn());
      bestTurnSet.add(path.getFirstTurn());
    }
    
    // Choose one of the best first turns at random
    List<Turn> bestTurns = new ArrayList<Turn>(bestTurnSet);
    Collections.shuffle(bestTurns);
    return bestTurns.get(0);
  }

  /**
   * Returns the best paths for the current board position; has package access
   * so it can be unit-tested.
   * 
   * @param board can't be <code>null</code>
   * @return a non-<code>null</code> List of Paths
   */
  List<Path> getBestPaths(Board board) {
    // Declare a List of the best scoring paths
    List<Path> bestPaths = new ArrayList<Path>();
    
    // Generate all possible paths of turns to the required depth
    Collection<Path> allPaths = getAllPaths(board);
    LOG.info("\nNumber of possible paths = " + allPaths.size());
    // Collect the highest scoring ones
    Iterator<Path> allPathsIter = allPaths.iterator();
    while (allPathsIter.hasNext()) {
      Path path = allPathsIter.next();
      LOG.info("Considering path: " + path);
      if (bestPaths.isEmpty()) {
        // This is the only path so far => it's the best
        bestPaths.add(path);
      }
      else {
        // There are one or more paths already in the List
        Path firstBestPath = bestPaths.get(0);
        if (path.getScore() == firstBestPath.getScore()) {
          // This path is the same value as the best ones - just add it
          bestPaths.add(path);
        }
        else if (path.getScore() > firstBestPath.getScore()) {
          // This path is better than any found so far
          bestPaths.clear();
          bestPaths.add(path);
        }
      }
    }
    return bestPaths;
  }
  
  /**
   * Returns all paths of turns for the given board position, to the depth of
   * this AI's look-ahead depth.
   * 
   * @param board can't be <code>null</code>
   * @return a non-<code>null</code> Collection of Path objects
   */
  private Collection<Path> getAllPaths(Board board) {
    // Check input
    if (board == null) {
      throw new IllegalArgumentException("Board can't be null");
    }

    // Declare the collection to be returned
    Collection<Path> allPaths = new ArrayList<Path>();
    
    // Find our initial set of possible turns on the board
    Iterator<Turn> firstTurns = board.getPossibleTurns(getTeam()).iterator();
    while (firstTurns.hasNext()) {
      Turn firstTurn = firstTurns.next();
      // Get all the paths from this first turn
      Collection<Path> allPathsFromThisTurn =
        getAllPaths(board, firstTurn, 0, new Path(firstTurn));
      // Add them to the collection
      allPaths.addAll(allPathsFromThisTurn);
    }
    
    // Return the assembled paths
    return allPaths;
  }
  
//  /**
//   * Returns all paths of turns for the given board position, to the depth of
//   * this AI's look-ahead depth.
//   * 
//   * @param board can't be <code>null</code>
//   * @return a non-<code>null</code> Collection of Path objects
//   */
//  private Collection getAllPaths(BoardImpl board) {
//    // Check input
//    if (board == null)
//      throw new IllegalArgumentException("BoardImpl can't be null");
//
//    // Declare the collection to be returned
//    Collection allPaths = new ArrayList();
//    
//    // Find our initial set of possible turns on the board
//    Iterator firstTurns = board.getPossibleTurns(getTeam()).iterator();
//    while (firstTurns.hasNext()) {
//      // Get the next "first turn"
//      Turn firstTurn = (Turn) firstTurns.next();
//      // Find the lowest scoring paths leading from this turn, i.e. the paths
//      // that guarantee us at least a certain score whatever the other player does
//      List lowestScoringPaths = new ArrayList();
//      Iterator iter =
//        getAllPaths(board, firstTurn, 0, new Path(firstTurn)).iterator();
//      if (DEBUG)
//        System.out.println("For first turn = " + firstTurn + ", found paths:");
//      while (iter.hasNext()) {
//        Path path = (Path) iter.next();
//        if (DEBUG)
//          System.out.println("-- " + path);
//        if (lowestScoringPaths.isEmpty()) {
//          lowestScoringPaths.add(path);
//        }
//        else {
//          Path lowestScoringPath = (Path) lowestScoringPaths.get(0);
//          if (path.getScore() == lowestScoringPath.getScore()) {
//            // As low as those already in the list
//            lowestScoringPaths.add(path);
//          }
//          else if (path.getScore() < lowestScoringPath.getScore()) {
//            // We have reached a new low
//            lowestScoringPaths.clear();
//            lowestScoringPaths.add(path);
//          }
//        }
//      }
//      // Add it to the collection
//      allPaths.addAll(lowestScoringPaths);
//    }
//    
//    // Return the assembled paths
//    return allPaths;
//  }

  /**
   * Returns all the paths leading from the given turn
   * 
   * @param board can't be <code>null</code>
   * @param turn can be <code>null</code>
   * @param depth the depth of the given turn, zero-indexed
   * @param parentPath the path leading to the given turn
   * @return see above
   */
  private Collection<Path> getAllPaths(
      Board board, Turn turn, int depth, Path parentPath)
  {
    // Check input
    if (board == null)
      throw new IllegalArgumentException("BoardImpl can't be null");
    
    // We always need to add the given turn to the parent path
    if (depth > 0)
      parentPath.addTurn(turn);

    // Declare the collection to be returned
    Collection<Path> allPaths = new ArrayList<Path>();
    
    if (depth == turnsToLookAhead) {
      // The given turn is a leaf - just add the path down to it
      if (DEBUG) {
        System.out.println("Adding leaf path " + parentPath);
      }
      allPaths.add(parentPath);
    }
    else {
      try {
        // The given turn isn't a leaf - make a copy of the board
        Board boardCopy = (Board) board.clone();
        if (turn != null) {
          // A non-null turn was given - make it on the copy board
          boardCopy.haveTurn(getPlayer(depth), turn);
        }
        // Find all the turns at the next depth (for the other player)
        Player nextPlayer = getPlayer(depth + 1);
        
        Collection<Turn> nextTurns =
          boardCopy.getPossibleTurns(nextPlayer.getTeam());
        if (DEBUG) {
          System.out.println("After " + turn + ", found next turns x " + nextTurns.size());
          System.out.println("  for player " + nextPlayer);
        }
        if (nextTurns.isEmpty()) {
          allPaths.addAll(getAllPaths(boardCopy, null, depth + 1, parentPath));
        }
        else {
          // There are one or more next turns - add each one's path to the
          // given parent path
          Iterator<Turn> iter = nextTurns.iterator();
          while (iter.hasNext()) {
            Turn nextTurn = iter.next();
            if (DEBUG)
              System.out.println("Next turn = " + nextTurn);
            Path copyPath = (Path) parentPath.clone();
            allPaths.addAll(getAllPaths(boardCopy, nextTurn, depth + 1, copyPath));
          }
        }
      }
      catch (CloneNotSupportedException ex) {
        // Unexpected since BoardImpl implements Clonable
        throw new RuntimeException(ex);
      }
      catch (IllegalMoveException ex) {
        // Unexpected since the BoardImpl gave us the turn
        throw new RuntimeException(ex);
      }
    }
    
    // Return the assembled paths
    return allPaths;
  }
  
  /**
   * Returns the player whose turn it is at the given search depth
   * 
   * @param depth zero-index, alternating, zero is us
   * @return see above
   */
  private Player getPlayer(int depth) {
    if (DEBUG)
      System.out.println("Getting player at depth " + depth);
    if (depth % 2 == 0) {
      // Even depth => our turn
      return this;
    }
    
    return _opponent;
  }
}
