package au.edu.unsw.cse.jayen.search;

/**
 * an action to transition from one state to another state
 * 
 * @author jayen
 */
public interface Action {
   /**
    * @return the cost of performing this action
    */
   double cost();
}
