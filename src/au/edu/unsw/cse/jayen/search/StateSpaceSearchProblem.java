package au.edu.unsw.cse.jayen.search;

/**
 * represents a state-space search problem
 * 
 * @author jayen
 * 
 */
public interface StateSpaceSearchProblem<State> {
   /**
    * the initial states
    * 
    * @return the initial states
    */
   Iterable<State> initialStates();

   /**
    * checks if the given state is a goal state
    * 
    * @param state
    *           the state to check
    * @return true, if the given state is a goal state; false, otherwise
    */
   boolean isGoal(State state);

   /**
    * the actions and states that can be succeeded after the given one
    * 
    * @param state
    *           the state to succeed
    * @return the actions and states that can be succeeded after the given one
    */
   Iterable<ActionStatePair<State>> successor(State state);
}
