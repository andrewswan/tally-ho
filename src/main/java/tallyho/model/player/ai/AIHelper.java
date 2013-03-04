/*
 * Created on 6/09/2004
 */
package tallyho.model.player.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import tallyho.model.Board;
import tallyho.model.Team;
import tallyho.model.tile.Tile;
import tallyho.model.turn.Flip;
import tallyho.model.turn.Move;
import tallyho.model.turn.Turn;

/**
 * Contains methods for reuse by various AI implementations
 */
class AIHelper {

  /**
   * Returns the first flippable tile on the board
   * 
   * @param board can't be <code>null</code>
   * @return null if there are no flippable tiles
   */
  static Flip getFirstFlip(Board board) {
    if (board == null)
      throw new IllegalArgumentException("BoardImpl can't be null");

    // Find the first flip x/y
    for (int y = 0; y <= board.getMaxIndex(); y++) {
      for (int x = 0; x <= board.getMaxIndex(); x++) {
        Tile tile = board.getTile(x, y);
        if (tile != null && !tile.isFaceUp()) {
          return new Flip(Team.NEUTRAL, x, y, tile); // team doesn't matter
        }
      }
    }
    // No flips found
    return null;
  }

  /**
   * Returns the first possible move for the given team on the given board
   * 
   * @param board can't be <code>null</code>
   * @param team one of Tile.TEAM_HUMANS or Tile.TEAM_PREDATORS
   * @return <code>null</code> if no moves are possible for this team
   */
  static Move getRandomMoveOfFirstMovableTile(Board board, Team team) {
    // Check inputs
    if (board == null) {
      throw new IllegalArgumentException("BoardImpl can't be null");
    }
    if (Team.NEUTRAL.equals(team)) {
      throw new IllegalArgumentException("Invalid team: " + team);
    }

    // Find the first possible move
    for (int fromY = 0; fromY <= board.getMaxIndex(); fromY++) {
      for (int fromX = 0; fromX <= board.getMaxIndex(); fromX++) {
        Collection<Move> moves = board.getPossibleMoves(team, fromX, fromY);
        if (moves != null && !moves.isEmpty()) {
          // Return a random one
          List<Move> list = new ArrayList<Move>(moves);
          Collections.shuffle(list);
          return list.get(0);
        }
      }
    }
    // No moves found
    return null;
  }

  /**
   * Prints out the given collection of turn options
   * 
   * @param turns can't be <code>null</code>
   */
  static void debugTurns(Collection turns) {
    System.out.print("Choosing from ");
    System.out.print(turns.size());
    System.out.println(" turns:");
    Iterator iter = turns.iterator();
    while (iter.hasNext()) {
      System.out.println(iter.next());
    }
  }
  
  /**
   * Returns the highest-scoring turns for the given board position (without
   * any look-ahead)
   * 
   * @param board can't be <code>null</code>
   * @param team
   * @return a non-<code>null</code> List, which might be empty if there are no
   *   available turns for the given team
   */
  static List<Turn> getHighestScoringTurns(Board board, Team team) {
    // Store these internally as a List so we can check the first one
    List<Turn> highestScoringTurns = new ArrayList<Turn>();
    Iterator iter = board.getPossibleTurns(team).iterator();
    while (iter.hasNext()) {
      Turn turn = (Turn) iter.next();
      if (highestScoringTurns.isEmpty()) {
        highestScoringTurns.add(turn);
      }
      else {
        // There are already one or more turns in the list - get the first one
        Turn firstListedTurn = highestScoringTurns.get(0);
        if (turn.getScore() == firstListedTurn.getScore()) {
          highestScoringTurns.add(turn);
        }
        else if (turn.getScore() > firstListedTurn.getScore()) {
          highestScoringTurns.clear();
          highestScoringTurns.add(turn);
        }
      }
    }
    return highestScoringTurns;
  }
}
