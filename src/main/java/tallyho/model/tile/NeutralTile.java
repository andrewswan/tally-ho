/*
 * Created on 18/08/2004
 */
package tallyho.model.tile;

import tallyho.model.Team;

/**
 * Represents a neutral tile, namely one initially not belonging to either
 * player.
 */
public abstract class NeutralTile extends AbstractTile {

  /**
   * Constructor
   */
  public NeutralTile() {
    super();
  }

  /**
   * @see tallyho.model.tile.Tile#canCapture(Tile)
   */
  public boolean canCapture(Tile otherTile) {
    // Neutral tiles can't capture anything
    return false;
  }

  /**
   * @see tallyho.model.tile.Tile#getTeam()
   */
  public Team getTeam() {
    return Team.NEUTRAL;
  }

  /**
   * @see tallyho.model.tile.Tile#getPrey()
   */
  public Class[] getPrey() {
    return new Class[0];
  }
}