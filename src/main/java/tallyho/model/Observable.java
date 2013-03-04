/*
 * Created on 26/07/2005
 */
package tallyho.model;

import java.util.Observer;

/**
 * Exists because java.util.Observable is a class
 */
public interface Observable {

  /**
   * Adds the given observer to this object's set of observers
   * 
   * @param observer
   */
  void addObserver(Observer observer);
}
