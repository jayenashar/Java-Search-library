package au.edu.unsw.cse.jayen.search;

import au.edu.unsw.cse.jayen.util.PriorityQueue;

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
public class BestFirstSearch<State> implements Search<State> {

   /**
    * used by a priority queue in best-first to compare h scores
    * 
    * @author jayen
    */
   private class Comparator implements java.util.Comparator<ActionStatePair<State>> {
      /**
       * the table of h scores
       */
      private final Map<State, Double> h;

      /**
       * @param h
       *           the table of h scores
       */
      public Comparator(final Map<State, Double> h) {
         this.h = h;
      }

      /*
       * (non-Javadoc)
       * 
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      @Override
      public int compare(final ActionStatePair<State> o1, final ActionStatePair<State> o2) {
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
   private Set<State> closedSet;
   /**
    * the heuristic used by best-first
    */
   private final Heuristic<State> heuristic;

   /**
    * @param heuristic
    *           the heuristic to be used by best-first
    */
   public BestFirstSearch(final Heuristic<State> heuristic) {
      this.heuristic = heuristic;
   }

   /*
    * (non-Javadoc)
    * 
    * @see Search#statesExplored()
    */
   @Override
   public int statesExplored() {
      return closedSet.size();
   }

   /*
    * (non-Javadoc)
    * 
    * @see Search#search(StateSpaceSearchProblem)
    */
   @Override
   public List<Action> search(final StateSpaceSearchProblem<State> sssp) {
      final Map<State, Double> h = new HashMap<>();
      final Map<ActionStatePair<State>, ActionStatePair<State>> parent = new HashMap<>();
      final Queue<ActionStatePair<State>> openSet = new PriorityQueue<>(
              1, new Comparator(h));
      closedSet = new HashSet<>();
      for (final State state : sssp.initialStates()) {
         final double h2 = heuristic.heuristic(state);
         h.put(state, h2);
         openSet.add(new ActionStatePair<>(null, state));
      }
      while (!openSet.isEmpty()) {
         ActionStatePair<State> current = openSet.remove();
         closedSet.add(current.state);
         if (sssp.isGoal(current.state)) {
            final List<Action> path = new ArrayList<>();
            for (; current.action != null; current = parent.get(current))
               path.add(current.action);
            Collections.reverse(path);
            return path;
         }
         for (final ActionStatePair<State> neighbor : sssp.successor(current.state)) {
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
