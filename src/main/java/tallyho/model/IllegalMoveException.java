/*
 * Created on 22/08/2004
 */
package tallyho.model;

/**
 * Exception representing an illegal move in Tally Ho
 */
public class IllegalMoveException extends Exception {
  
  /**
   * Constructor
   * 
   * @param message
   */
  public IllegalMoveException(String message) {
    super(message);
  }
}
