/*
 * Created on 27/08/2004
 */
package tallyho.model.player;

/**
 * Represents a real person (not the computer) playing the game
 */
public class RealPlayer extends AbstractPlayer {

  /**
   * Constructor - parameterless to enable instantiation using Reflection
   */
  public RealPlayer() {
    super();
  }

  /**
   * @see tallyho.model.player.Player#getType()
   */
  public String getType() {
    return "Real Person";
  }
}