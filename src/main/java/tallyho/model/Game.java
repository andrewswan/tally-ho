/**
 * Created on 18/08/2004
 */
package tallyho.model;

import java.util.Observable;
import java.util.Observer;

import tallyho.model.player.Player;
import tallyho.model.player.RealPlayer;
import tallyho.model.turn.Flip;
import tallyho.model.turn.Move;

/**
 * Represents a single game (two rounds) of Tally Ho 
 */
public class Game extends Observable implements Observer {

  // Constants
  /**
   * The total number of turns in the end-game.
   */
  public static final int END_GAME_TURNS = 5 * 2;   // 5 each (from the rules)
  
  // Names of properties interesting to observing classes
  /**
   * Argument passed to Observers when active player changes
   */
  public static final String ACTIVE_PLAYER = "ActivePlayer";
  
  /**
   * Argument passed to Observers when end-game commences
   */
  public static final String END_GAME_STARTED = "EndGame";

  /**
   * Argument passed to Observers when the game is over
   */
  public static final String GAME_OVER = "GameOver";

  /**
   * Argument passed to Observers when new round commences
   */
  public static final String NEW_ROUND = "NewRound";

  private static final int
    // -- Number of players
    PLAYERS = 2;
  
  private static final String
    NAME = "Tally Ho",
    VERSION = "0.1";

  // Properties
  private final BoardImpl board;
  private final Player[] players;
  private int activePlayerIndex; // index to "players" array
  private int roundNumber;    // the roundNumber (1 or 2)
  private int turnsLeft;
  
  /**
   * Constructor - sets the game up with a default size board,
   * ready to start round one
   * 
   * @param playerOne
   * @param playerTwo
   */
  public Game(Player playerOne, Player playerTwo) {
    players = new Player[PLAYERS];
    players[0] = playerOne;
    players[1] = playerTwo;
    board = new BoardImpl(this);
    board.addObserver(this);
    prepareRound(1);
  }

  /**
   * Perform the required post-game processing
   */
  public void finalise() {
    // e.g. show/save results
  }
  
  /**
   * Prepare everything for the given round of play
   * 
   * @param round the number of the round being prepared, one-indexed
   */
  private void prepareRound(int round) {
    // Check input
    if (round != 1 && round != 2)
      throw new IllegalArgumentException("Invalid round number " + round);

    // Bump the round number
    this.roundNumber = round;
    
    // Update the players' teams (based on the given round number)
    for (int playerIndex = 0; playerIndex < players.length; playerIndex++) {
      setPlayerTeam(playerIndex + 1, players[playerIndex]);
    }
    
    // Set the active player index (0 for round 1, 1 for round 2)
    activePlayerIndex = round - 1;

    // Set up the tiles ready for this new round
    board.setUpTiles();

    // Reset the turns counter
    turnsLeft = Integer.MAX_VALUE;
  }
  
  /**
   * Returns the display name of this game, followed by the round number
   * 
   * @return a non-null string
   */
  public String getNameAndRound() {
    StringBuffer name = new StringBuffer(getName());
    name.append(" - Round ");
    name.append(roundNumber);
    name.append(" of 2");
    return name.toString();
  }

  /**
   * Returns the display name of this game
   * 
   * @return a non-null string
   */
  public String getName() {
    StringBuffer name = new StringBuffer();
    name.append(NAME);
    name.append(" ");
    name.append(VERSION);
    return name.toString();
  }

  /**
   * Reports whether this game is over (both rounds completed)
   * 
   * @return see above
   */
  public boolean isOver() {
    return roundNumber == 2 && isRoundOver();
  }
  
  /**
   * Returns the board for this game
   * 
   * @return see above
   */
  public Board getBoard() {
    return board;
  }
  
  /**
   * Returns the Player of the given number
   * 
   * @param playerNo a number from 1 to PLAYERS
   * @return a non-null Player
   */
  public Player getPlayer(int playerNo) {
    try {
      return players[playerNo - 1];
    }
    catch (ArrayIndexOutOfBoundsException ex) {
      throw new IllegalArgumentException("Invalid player number: " + playerNo);
    }
  }
  
  /**
   * Checks that the given game model is valid
   * 
   * @param game the game to be validated
   * @throws IllegalArgumentException if it's invalid
   */
  public static void validate(Game game) {
    if (game == null)
      throw new IllegalArgumentException("Game can't be null");
    if (game.getBoard() == null)
      throw new IllegalArgumentException("BoardImpl can't be null");
  }
  
  /**
   * Sets the player of the given number to the given Player.
   * Ignores any given team number and sets the player to play
   * for the appropriate team according to the current round.
   * 
   * @param playerNumber one-indexed, e.g. 1, 2
   * @param player can't be <code>null</code>
   */
  public void setPlayer(int playerNumber, Player player) {
    // Check inputs
    if (player == null)
      throw new IllegalArgumentException("Player can't be null");
    if (player.getName() == null)
      throw new IllegalArgumentException("Player's name can't be null");
    
    try {
      // Store this player in our internal array
      players[playerNumber - 1] = player;
      // Set this player to the appropriate team for the round number
      setPlayerTeam(playerNumber, player);
    }
    catch (ArrayIndexOutOfBoundsException ex) {
      throw new IllegalArgumentException(
          "Invalid player number: " + playerNumber);
    }
  }

  private void setPlayerTeam(int playerNumber, Player player) {
    // Round 1: 1 = Predators, 2 = Humans
    // Round 2: 1 = Humans, 2 = Predators
    if (playerNumber == roundNumber) {
      player.setTeam(Team.PREDATORS);
    }
    else {
      player.setTeam(Team.HUMANS);
    }
  }
  
  /**
   * @return Returns the active player
   */
  public Player getActivePlayer() {
    return players[activePlayerIndex];
  }

  /**
   * Returns the number of the active player, if any
   * 
   * @return a one-indexed number, or zero if the round is over
   */
  public int getActivePlayerNumber() {
    if (turnsLeft == 0)
      return 0;
    
    return activePlayerIndex + 1;
  }

  /**
   * Returns the number of players in this game
   * 
   * @return a number two or more
   */
  public int getPlayers() {
    return PLAYERS;
  }
  
  /**
   * Returns the number of turns left in the game
   * 
   * @return Integer.MAX_VALUE if the number is unknown (e.g. mid-game)
   */
  public int getTurnsLeft() {
    return turnsLeft;
  }

  /**
   * Returns the player who won the game.
   * 
   * @return <code>null</code> if the game isn't over yet or it's a draw
   */
  public Player getWinner() {
    if (players == null || players[0] == null || players[1] == null || !isOver()) {
      // The winner can't be determined yet
      return null;
    }
    else if (players[0].getScore() > players[1].getScore()) {
      return players[0];
    }
    else if (players[1].getScore() > players[0].getScore()) {
      return players[1];
    }
    else {
      // It's a draw
      return null;
    }
  }

  /**
   * Returns the player who lost the game.
   * 
   * @return <code>null</code> if the game isn't over yet or it's a draw
   */
  public Player getLoser() {
    Player winner = getWinner();
    if (winner == null)
      return null;
    else if (winner == players[0])
      return players[1];
    else
      return players[0];
  }

  /**
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  public void update(Observable observable, Object change) {
    if (observable instanceof Board &&
        (change instanceof Flip || change instanceof Move))
    {
      // A move or flip happened
      handleBoardChange();
    }
  }

  /**
   * Decrements the number of turns left. If the round is not yet over, changes
   * the active player. If this player must pass, switches back to the original
   * player and if the round is still not over, gives them another turn. If
   * they too have no valid turns, the round ends. Informs the observers:
   * <ul>
   * <li>each time the active player changes</li>
   * <li>if the whole game is over</li>
   * <li>if the end-game commences</li>
   * </ul>
   */
  private void handleBoardChange() {
    endTurn();
    
    // Is the round over?
    if (isRoundOver()) {
      // The round is over
      endRound();
    }
    else {
      // One or more turns are left in this round
      toggleActivePlayer();  // notifies observers
      
      // Check whether the new active player has any valid turns
      if (!board.isTurnPossible(getActivePlayer().getTeam())) {
        board.pass(getActivePlayer().getTeam());
        endTurn();
        if (isRoundOver()) {
          endRound();
        }
        else {
          // Go back to the original player
          toggleActivePlayer();  // notifies observers
          if (!board.isTurnPossible(getActivePlayer().getTeam())) {
            endRound();
          }
        }
      }
    }
  }
  
  /**
   * Performs any necessary processing after a player's turn
   */
  private void endTurn() {
    if (turnsLeft <= END_GAME_TURNS)
      turnsLeft--;
    if (turnsLeft > END_GAME_TURNS && board.areAllTilesFaceUp()) {
      // Start the end-game and notify observers
      turnsLeft = END_GAME_TURNS;
      setChanged();
      notifyObservers(END_GAME_STARTED);
    }
  }

  /**
   * Performs any necessary processing at the end of a round
   */
  private void endRound() {
    if (roundNumber == 2) {
      // Game over
      setChanged();
      notifyObservers(GAME_OVER);
    }
    else {
      // Start round 2
      prepareRound(2);
      setChanged();
      notifyObservers(NEW_ROUND);
    }
  }
  
  /**
   * Switches to the next player in the turn sequence and notifies observers
   * that the active player has changed.
   */
  private void toggleActivePlayer() {
    // Update the index to the active player in the array
    if (++activePlayerIndex == PLAYERS)
      activePlayerIndex = 0;
    
    // Let the observers know
    setChanged();
    notifyObservers(ACTIVE_PLAYER);
  }
  
  /**
   * Reports whether the current round is over. This happens when all the tiles
   * on the board are face-up and either:
   * - both players have had five more turns each, or
   * - either player has none of his own tiles left on the board.
   * Note this method does not say the round is over if neither player has a
   * valid move, even though that would effectively be the end of the round.
   * 
   * @return see above
   */
  private boolean isRoundOver() {
    // No turns left => round is over
    if (turnsLeft == 0)
      return true;
    
    // All tiles are face-up and either team has no tiles on the board => over
    if (board.areAllTilesFaceUp() && !board.areBothTeamsRepresented())
      return true;

    // Otherwise, the round is still going
    return false;
  }
  
  /**
   * Reports whether either of the players is a real person (as opposed to an
   * AI player)
   * 
   * @return see above
   */
  public boolean isEitherPlayerReal() {
    for (int i = 0; i < players.length; i++) {
      if (players[i] instanceof RealPlayer)
        return true;
    }
    return false;
  }
}