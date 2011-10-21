package au.edu.unsw.cse.jayen.bisearch;

import java.util.List;

import au.edu.unsw.cse.jayen.search.Action;

/**
 * a bi-directional state space search algorithm
 * 
 * @author jayen
 * 
 */
public interface Search {
   /**
    * the number of nodes expanded by the search() function
    * 
    * @return the number of nodes expanded by the search() function
    */
   int nodesExplored();

   /**
    * searches between the initial states and goal states
    * 
    * @param sssp
    *           the definition of the state-space search problem
    * @return a list of actions from an initial state to a goal state
    */
   List<Action> search(StateSpaceSearchProblem sssp);
}
