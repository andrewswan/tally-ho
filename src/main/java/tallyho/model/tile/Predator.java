/*
 * Created on 18/08/2004
 */
package tallyho.model.tile;

import tallyho.model.Team;

/**
 * Abstract model for a predator (currently foxes and bears) in Tally Ho
 */
public abstract class Predator extends AbstractTile {

	/**
	 * @see tallyho.model.tile.Tile#getTeam()
	 */
	public Team getTeam() {
		return Team.PREDATORS;
	}
}
