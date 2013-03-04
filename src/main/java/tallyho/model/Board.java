/*
 * Created on 25/07/2005
 */
package tallyho.model;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import tallyho.model.player.Player;
import tallyho.model.tile.Tile;
import tallyho.model.turn.Move;
import tallyho.model.turn.Turn;

/**
 * Defines the behaviour of a Tally Ho game board
 */
public interface Board extends Cloneable, Observable {

  /**
   * The maximum size of a board
   */
  int MAX_SIZE = 19; // sensible maximum

  /**
   * Adds the given face-down Tile at the given empty square
   * 
   * @param tile can't be <code>null</code>, face-up, or already
   *   on the board
   * @param xPos zero-indexed, must be on the board
   * @param yPos zero-indexed, must be on the board
   * @throws IllegalArgumentException if an invalid parameter is
   *   passed or if there's already a Tile there.
   */
  void addTile(Tile tile, int xPos, int yPos);

  /**
   * Reports whether all tiles on the board are face up.
   * Public so that AIs can check this.
   * 
   * @return true if there are no tiles on the board
   */
  boolean areAllTilesFaceUp();

  /**
   * Clones this board
   * 
   * @return the clone
   * @throws CloneNotSupportedException
   */
  Object clone() throws CloneNotSupportedException;
  
  /**
   * Flips the Tile at the given board position
   * 
   * @param flippingTeam one of Team.HUMANS or Team.PREDATORS
   * @param position can't be <code>null</code>, x & y must
   *    be valid zero-indexed board coordinates
   */
  void flipTile(Team flippingTeam, Point2D position);

  /**
   * Flips the Tile at the given coordinates
   *  
   * @param flippingTeam one of Team.HUMANS or Team.PREDATORS
   * @param xPos zero-indexed
   * @param yPos zero-indexed
   * @throws IllegalArgumentException if the given coordinates are invalid
   */
  void flipTile(Team flippingTeam, int xPos, int yPos);

  /**
   * Returns the neutral tile that was flipped last turn (if any)
   * 
   * @return null if no neutral tile was flipped last turn
   */
  Tile getJustFlippedNeutralTile();

  /**
   * @return Returns the size of the board
   */
  int getMaxIndex();

  /**
   * Returns the Tile at the given board position
   * 
   * @param point can't be <code>null</code>, x & y are zero-indexed
   * @return null if there is no tile there or the location is off-board
   */
  Tile getTile(Point2D point);

  /**
   * Returns the Tile at the given board position
   * 
   * @param xPos zero-indexed
   * @param yPos zero-indexed
   * @return null if there is no tile there or the location is off-board
   */
  Tile getTile(int xPos, int yPos);

  /**
   * Executes the given turn for the game's active player
   * 
   * @param player can't be <code>null</code>
   * @param turn can't be <code>null</code> or a Pass
   * @throws IllegalMoveException
   */
  void haveTurn(Player player, Turn turn) throws IllegalMoveException;

  /**
   * Reports whether the given team can do anything on their turn
   * 
   * @param team the team moving the tile, one of Team.HUMANS or Team.PREDATORS
   * @return see above
   */
  boolean isTurnPossible(Team team);

  /**
   * Returns all possible turns for the given team
   * 
   * @param team one of Team.HUMANS or Team.PREDATORS
   * @return a non-<code>null</code> Collection of Moves
   */
  Collection<Turn> getPossibleTurns(Team team);

  /**
   * Returns the possible moves of the tile at the given position
   * 
   * @param movingTeam the team trying to move the tile, one of Team.HUMANS or
   *   Team.PREDATORS
   * @param fromX zero-indexed, from 0 to maxIndex
   * @param fromY zero-indexed, from 0 to maxIndex
   * @return see above
   */
  Collection<Move> getPossibleMoves(Team movingTeam, int fromX, int fromY);

  /**
   * Moves the tile at the given "from" coordinates to the given "to"
   * coordinates, capturing the Tile (if any) at the latter coordinates. 
   * 
   * @param movingPlayer
   * @param from can't be <code>null</code>, x & y are zero-indexed
   * @param to can't be <code>null</code>, x & y are zero-indexed
   * @throws IllegalMoveException if the move is invalid
   */
  void moveTile(Player movingPlayer, Point2D from, Point2D to)
      throws IllegalMoveException;

  /**
   * Moves the tile at the given "from" coordinates to the given "to"
   * coordinates, capturing the Tile (if any) at the latter coordinates. 
   * 
   * @param movingPlayer
   * @param fromX zero-indexed
   * @param fromY zero-indexed
   * @param toX zero-indexed
   * @param toY zero-indexed
   * @throws IllegalMoveException if the move is invalid
   */
  void moveTile(Player movingPlayer, int fromX, int fromY,
      int toX, int toY) throws IllegalMoveException;

  /**
   * Checks that the tile at the "from" position can legally be
   * moved to the "to" position, including checking whether the
   * tile at the destination can legally be captured.
   * 
   * @param movingTeam the team moving the tile, one of Team.HUMANS or
   *   Team.PREDATORS
   * @param fromX zero-indexed
   * @param fromY zero-indexed
   * @param toX zero-indexed
   * @param toY zero-indexed
   * @throws IllegalMoveException if the move is invalid
   */
  void validateMove(Team movingTeam, int fromX, int fromY,
      int toX, int toY) throws IllegalMoveException;

  /**
   * Returns an iterator across all the spaces on this board
   * 
   * @return see above
   */
  Iterator getIterator();

  /**
   * Reports whether the given team can move the tile at the given
   * position (to anywhere)
   * 
   * @param team the team moving the tile, one of Team.HUMANS or Team.PREDATORS
   * @param from the point from which the tile is being moved
   * @return see above
   */
  boolean isMovementPossible(Team team, Point2D from);

}