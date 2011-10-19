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
 * implements the A* search algorithm
 * 
 * @author jayen
 */
public class AStar implements Search {

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
       * @param f
       *           the table of f scores
       */
      public Comparator(final Map<Object, Double> f) {
         this.f = f;
      }

      /*
       * (non-Javadoc)
       * 
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      @Override
      public int compare(final ActionStatePair o1, final ActionStatePair o2) {
         final double diff = f.get(o1.state) - f.get(o2.state);
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
    * @param heuristic
    *           the heuristic to be used by A*
    */
   public AStar(final Heuristic heuristic) {
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
      // This would be faster if I stored f & g in State, but that's bad design
      final Map<Object, Double> g = new HashMap<Object, Double>();
      final Map<Object, Double> h = new HashMap<Object, Double>();
      final Map<Object, Double> f = new HashMap<Object, Double>();
      final Map<ActionStatePair, ActionStatePair> parent = new HashMap<ActionStatePair, ActionStatePair>();
      final Queue<ActionStatePair> openSet = new PQueue<ActionStatePair>(
            1, new Comparator(f));
      closedSet = new HashSet<Object>();
      for (final Object state : sssp.initialStates()) {
         g.put(state, 0.);
         final double h2 = heuristic.heuristic(state);
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
               final double h2 = heuristic.heuristic(neighbor.state);
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
