/*
 * Created on 18/08/2004
 */
package tallyho.model.tile;

import tallyho.model.Team;

/**
 * Abstract model for a human in Tally Ho
 */
public abstract class Human extends AbstractTile {

  /**
   * @see Tile#getTeam()
   */
	public Team getTeam() {
		return Team.HUMANS;
	}

  /**
   * @see Tile#getValue()
   */
	public int getValue() {
		return 5;
	}
}
