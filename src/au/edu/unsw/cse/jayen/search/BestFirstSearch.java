package au.edu.unsw.cse.jayen.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * implements the best-first search algorithm
 * 
 * @author jayen
 */
public class BestFirstSearch implements Search {

   /**
    * used by a priority queue in best-first to compare h scores
    * 
    * @author jayen
    */
   private class Comparator implements java.util.Comparator<ActionStatePair> {
      /**
       * the table of h scores
       */
      private final Map<Object, Double> h;

      /**
       * @param h
       *           the table of h scores
       */
      public Comparator(final Map<Object, Double> h) {
         this.h = h;
      }

      /*
       * (non-Javadoc)
       * 
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      @Override
      public int compare(final ActionStatePair o1, final ActionStatePair o2) {
         final double diff = h.get(o1.state) - h.get(o2.state);
         if (diff < 0)
            return -1;
         if (diff > 0)
            return 1;
         return 0;
      }
   }

   // This would be faster if I stored visited in State, but that's bad design
   /**
    * the set of expanded states
    */
   private Set<Object> closedSet;
   /**
    * the heuristic used by best-first
    */
   private final Heuristic heuristic;

   /**
    * @param heuristic
    *           the heuristic to be used by best-first
    */
   public BestFirstSearch(final Heuristic heuristic) {
      this.heuristic = heuristic;
   }

   /*
    * (non-Javadoc)
    * 
    * @see Search#nodesExplored()
    */
   @Override
   public int nodesExplored() {
      return closedSet.size();
   }

   /*
    * (non-Javadoc)
    * 
    * @see Search#search(StateSpaceSearchProblem)
    */
   @Override
   public List<Action> search(final StateSpaceSearchProblem sssp) {
      final Map<Object, Double> h = new HashMap<Object, Double>();
      final Map<ActionStatePair, ActionStatePair> parent = new HashMap<ActionStatePair, ActionStatePair>();
      final Queue<ActionStatePair> openSet = new PQueue<ActionStatePair>(1,
            new Comparator(h));
      closedSet = new HashSet<Object>();
      for (final Object state : sssp.initialStates()) {
         final double h2 = heuristic.heuristic(state);
         h.put(state, h2);
         openSet.add(new ActionStatePair(null, state));
      }
      while (!openSet.isEmpty()) {
         ActionStatePair current = openSet.remove();
         closedSet.add(current.state);
         if (sssp.isGoal(current.state)) {
            final List<Action> path = new ArrayList<Action>();
            for (; current.action != null; current = parent.get(current))
               path.add(current.action);
            Collections.reverse(path);
            return path;
         }
         for (final ActionStatePair neighbor : sssp.successor(current.state)) {
            if (closedSet.contains(neighbor.state))
               continue;
            if (!openSet.contains(neighbor)) {
               h.put(neighbor.state, heuristic.heuristic(neighbor.state));
               parent.put(neighbor, current);
               openSet.add(neighbor);
            }
         }
      }
      return null;
   }
}
