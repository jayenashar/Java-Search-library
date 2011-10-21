package au.edu.unsw.cse.jayen.bisearch;

import au.edu.unsw.cse.jayen.search.ActionStatePair;

/**
 * represents a bi-directional state-space search problem
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
    * the goal states
    * 
    * @return the goal states
    */
   Iterable<Object> goalStates();

   /**
    * the actions and states that can be succeeded after the given one. it is
    * guaranteed that this function will only be called with states succeeding
    * the initial states.
    * 
    * @param state
    *           the state to succeed
    * @return the actions and states that can be succeeded after the given one
    */
   Iterable<ActionStatePair> successor(Object state);

   /**
    * the actions and states that can be preceded before the given one. it is
    * guaranteed that this function will only be called with states preceding
    * the goal states.
    * 
    * @param state
    *           the state to precede
    * @return the actions and states that can be preceded before the given one
    */
   Iterable<ActionStatePair> predecessor(Object state);
}
