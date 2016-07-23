package au.edu.unsw.cse.jayen.search;

/**
 * a heuristic to be used by informed search algorithms, namely best-first and
 * A*
 * 
 * @author jayen
 */
public interface Heuristic<State> {

   // 244 nodes in the sample
   /**
    * the always-admissible zero heuristic
    * 
    * @author jayen
    */
   class Zero<State> implements Heuristic<State> {
      @Override
      public double heuristic(final State state) {
         return 0;
      }
   }

   /**
    * the heuristic cost to reach any goal state
    * 
    * @param state
    *           the state to act from
    * @return an underestimating guess at the cost of the cheapest set of
    *         actions from the given state to any goal state
    */
   double heuristic(State state);
}
