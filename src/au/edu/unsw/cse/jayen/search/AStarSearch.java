package au.edu.unsw.cse.jayen.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import au.edu.unsw.cse.jayen.util.PriorityQueue;

/**
 * implements the A* search algorithm
 * 
 * @author jayen
 */
public class AStarSearch implements Search {

   /**
    * used by a priority queue in a* to compare f scores
    * 
    * @author jayen
    */
   private class Comparator implements java.util.Comparator<ActionStatePair> {
      /**
       * the table of f scores
       */
      private final Map<Object, Double> f;

      /**
       * the table of h scores
       */
      private final Map<Object, Double> h;

      /**
       * @param f
       *           the table of f scores
       * @param h
       *           the table of h scores
       */
      public Comparator(final Map<Object, Double> f, final Map<Object, Double> h) {
         this.f = f;
         this.h = h;
      }

      /*
       * (non-Javadoc)
       * 
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      @Override
      public int compare(final ActionStatePair o1, final ActionStatePair o2) {
         double diff = f.get(o1.state) - f.get(o2.state);
         if (diff < 0)
            return -2;
         if (diff > 0)
            return 2;
         diff = h.get(o1.state) - h.get(o2.state);
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
    * the heuristic used by A*
    */
   private final Heuristic heuristic;

   /**
    * A multiplier for the heuristic, so we expand fewer states
    */
   private final double justAboveOne;

   /**
    * @param heuristic
    *           the heuristic to be used by A*
    */
   public AStarSearch(final Heuristic heuristic) {
      this(heuristic, 1);
   }

   /**
    * @param heuristic
    *           the heuristic to be used by A*
    * @param justAboveOne
    *           a number just above one, such that <code>h(s<sub>1</sub>) &lt;
    *           h(s<sub>2</sub>) &harr; h(s<sub>1</sub>) * justAboveOne &lt;
    *           h(s<sub>2</sub>)</code>. this is used for optimising the search
    */
   public AStarSearch(final Heuristic heuristic, final double justAboveOne) {
      this.heuristic = heuristic;
      this.justAboveOne = justAboveOne;
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
      // This would be faster if I stored f & g in State, but that's bad design
      final Map<Object, Double> g = new HashMap<Object, Double>();
      final Map<Object, Double> h = new HashMap<Object, Double>();
      final Map<Object, Double> f = new HashMap<Object, Double>();
      final Map<ActionStatePair, ActionStatePair> parent = new HashMap<ActionStatePair, ActionStatePair>();
      final Queue<ActionStatePair> openSet = new PriorityQueue<ActionStatePair>(
            1, new Comparator(f, h));
      closedSet = new HashSet<Object>();
      for (final Object state : sssp.initialStates()) {
         g.put(state, 0.);
         final double h2 = justAboveOne * heuristic.heuristic(state);
         h.put(state, h2);
         f.put(state, h2);
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
            final Double oldG = g.get(neighbor.state);
            final double newG = g.get(current.state) + neighbor.action.cost();
            if (oldG == null) {
               g.put(neighbor.state, newG);
               final double h2 = justAboveOne
                     * heuristic.heuristic(neighbor.state);
               h.put(neighbor.state, h2);
               f.put(neighbor.state, newG + h2);
               parent.put(neighbor, current);
               openSet.add(neighbor);
            } else if (newG < oldG) {
               openSet.remove(neighbor);
               g.put(neighbor.state, newG);
               f.put(neighbor.state, newG + h.get(neighbor.state));
               parent.put(neighbor, current);
               openSet.add(neighbor);
            }
         }
      }
      return null;
   }
}
