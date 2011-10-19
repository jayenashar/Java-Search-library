package au.edu.unsw.cse.jayen.search;
/**
 * represents a state-space search problem
 * 
 * @author jayen
 * 
 */
public interface StateSpaceSearchProblem {
   /**
    * the initial states
    * 
    * @return the initial states
    */
   Iterable<Object> initialStates();

   /**
    * checks if the given state is a goal state
    * 
    * @param state
    *           the state to check
    * @return true, if the given state is a goal state; false, otherwise
    */
   boolean isGoal(Object state);

   /**
    * the actions and states that can be succeeded after the given one
    * 
    * @param state
    *           the state to succeed
    * @return the actions and states that can be succeeded after the given one
    */
   Iterable<ActionStatePair> successor(Object state);
}
