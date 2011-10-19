package au.edu.unsw.cse.jayen.search;

/**
 * a heuristic to be used by informed search algorithms, namely best-first and
 * A*
 * 
 * @author jayen
 */
public interface Heuristic {

   // 244 nodes in the sample
   /**
    * the always-admissible zero heuristic
    * 
    * @author jayen
    */
   public static class Zero implements Heuristic {
      @Override
      public double heuristic(final Object state) {
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
   double heuristic(Object state);
}
