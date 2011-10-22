package au.edu.unsw.cse.jayen.bisearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import au.edu.unsw.cse.jayen.search.Action;
import au.edu.unsw.cse.jayen.search.ActionStatePair;
import au.edu.unsw.cse.jayen.search.Heuristic;
import au.edu.unsw.cse.jayen.util.HMap;
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
    * the set of expanded states from the goal states
    */
   private Set<Object> closedSetGoal;

   /**
    * the set of expanded states from the initial states
    */
   private Set<Object> closedSetInitial;
   /**
    * the heuristic used by A* from the goal states
    */
   private final Heuristic heuristicGoal;
   /**
    * the heuristic used by A* from the initial states
    */
   private final Heuristic heuristicInitial;

   /**
    * A multiplier for the heuristic from the goal states, so we expand fewer
    * states
    */
   private final double justAboveOneGoal;

   /**
    * A multiplier for the heuristic from the initial states, so we expand fewer
    * states
    */
   private final double justAboveOneInitial;

   /**
    * @param heuristicInitial
    *           the heuristic to be used by A* from the initial states
    * @param heuristicGoal
    *           the heuristic to be used by A* from the goal states
    */
   public AStarSearch(final Heuristic heuristicInitial,
         final Heuristic heuristicGoal) {
      this(heuristicInitial, heuristicGoal, 1, 1);
   }

   /**
    * @param heuristicInitial
    *           the heuristic to be used by A* from the initial states
    * @param heuristicGoal
    *           the heuristic to be used by A* from the goal states
    * @param justAboveOneInitial
    *           a number just above one, such that <code>hi(s<sub>1</sub>) &lt;
    *           hi(s<sub>2</sub>) &harr; hi(s<sub>1</sub>) * justAboveOneInitial &lt;
    *           hi(s<sub>2</sub>)</code>. this is used for optimising the search
    * @param justAboveOneGoal
    *           a number just above one, such that <code>hg(s<sub>1</sub>) &lt;
    *           hg(s<sub>2</sub>) &harr; hg(s<sub>1</sub>) * justAboveOneGoal &lt;
    *           hg(s<sub>2</sub>)</code>. this is used for optimising the search
    */
   public AStarSearch(final Heuristic heuristicInitial,
         final Heuristic heuristicGoal, final double justAboveOneInitial,
         final double justAboveOneGoal) {
      this.heuristicInitial = heuristicInitial;
      this.heuristicGoal = heuristicGoal;
      this.justAboveOneInitial = justAboveOneInitial;
      this.justAboveOneGoal = justAboveOneGoal;
   }

   /*
    * (non-Javadoc)
    * 
    * @see Search#nodesExplored()
    */
   @Override
   public int nodesExplored() {
      return closedSetInitial.size() + closedSetGoal.size();
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
      final HMap<ActionStatePair, ActionStatePair> parentInitial = new HMap<ActionStatePair, ActionStatePair>();
      final HMap<ActionStatePair, ActionStatePair> parentGoal = new HMap<ActionStatePair, ActionStatePair>();
      // could be done with two openSets
      final Map<Object, Boolean> forward = new java.util.HashMap<Object, Boolean>();
      final Queue<ActionStatePair> openSet = new PriorityQueue<ActionStatePair>(
            1, new Comparator(f, h));
      closedSetInitial = new HashSet<Object>();
      closedSetGoal = new HashSet<Object>();
      for (final Object state : sssp.initialStates()) {
         g.put(state, 0.);
         final double h2 = justAboveOneInitial
               * heuristicInitial.heuristic(state);
         h.put(state, h2);
         f.put(state, h2);
         openSet.add(new ActionStatePair(null, state));
         forward.put(state, true);
      }
      for (final Object state : sssp.goalStates()) {
         g.put(state, 0.);
         final double h2 = justAboveOneInitial * heuristicGoal.heuristic(state);
         h.put(state, h2);
         f.put(state, h2);
         openSet.add(new ActionStatePair(null, state));
         forward.put(state, false);
      }
      while (!openSet.isEmpty()) {
         final ActionStatePair current = openSet.remove();
         if (forward.get(current.state)) {
            closedSetInitial.add(current.state);
            if (closedSetGoal.contains(current.state))
               return calculatePath(parentInitial, parentGoal, current);
            for (final ActionStatePair neighbor : sssp.successor(current.state)) {
               if (closedSetInitial.contains(neighbor.state))
                  continue;
               final Double oldG = g.get(neighbor.state);
               final double newG = g.get(current.state)
                     + neighbor.action.cost();
               if (oldG == null || !openSet.contains(neighbor)) {
                  g.put(neighbor.state, newG);
                  final double h2 = justAboveOneInitial
                        * heuristicInitial.heuristic(neighbor.state);
                  h.put(neighbor.state, h2);
                  f.put(neighbor.state, newG + h2);
                  parentInitial.put(neighbor, current);
                  openSet.add(neighbor);
                  forward.put(neighbor.state, true);
               } else if (newG < oldG) {
                  openSet.remove(neighbor);
                  g.put(neighbor.state, newG);
                  f.put(neighbor.state, newG + h.get(neighbor.state));
                  parentInitial.put(neighbor, current);
                  openSet.add(neighbor);
                  forward.put(neighbor.state, true);
               } else if (!parentInitial.containsKey(neighbor))
                  parentInitial.put(neighbor, current);
            }
         } else {
            closedSetGoal.add(current.state);
            if (closedSetInitial.contains(current.state))
               return calculatePath(parentInitial, parentGoal, current);
            for (final ActionStatePair neighbor : sssp
                  .predecessor(current.state)) {
               if (closedSetGoal.contains(neighbor.state))
                  continue;
               final Double oldG = g.get(neighbor.state);
               final double newG = g.get(current.state)
                     + neighbor.action.cost();
               if (oldG == null || !openSet.contains(neighbor)) {
                  g.put(neighbor.state, newG);
                  final double h2 = justAboveOneGoal
                        * heuristicGoal.heuristic(neighbor.state);
                  h.put(neighbor.state, h2);
                  f.put(neighbor.state, newG + h2);
                  parentGoal.put(neighbor, current);
                  openSet.add(neighbor);
                  forward.put(neighbor.state, false);
               } else if (newG < oldG) {
                  openSet.remove(neighbor);
                  g.put(neighbor.state, newG);
                  f.put(neighbor.state, newG + h.get(neighbor.state));
                  parentGoal.put(neighbor, current);
                  openSet.add(neighbor);
                  forward.put(neighbor.state, false);
               } else if (!parentGoal.containsKey(neighbor))
                  parentGoal.put(neighbor, current);
            }
         }
      }
      return null;
   }

   /**
    * traverses the parentInitial and parentGoal maps and returns a path
    * 
    * @param parentInitial
    *           a mapping from states to their predecessor
    * @param parentGoal
    *           a mapping from states to their successor
    * @param current
    *           the node to traverse from
    * @return a path from an initial state to a goal state
    */
   private List<Action> calculatePath(
         final HMap<ActionStatePair, ActionStatePair> parentInitial,
         final HMap<ActionStatePair, ActionStatePair> parentGoal,
         ActionStatePair current) {
      final List<Action> path = new ArrayList<Action>();
      for (ActionStatePair currentInitial = parentInitial.getKey(current); currentInitial.action != null; currentInitial = parentInitial
            .get(currentInitial))
         path.add(currentInitial.action);
      Collections.reverse(path);
      for (current = parentGoal.getKey(current); current.action != null; current = parentGoal
            .get(current))
         path.add(current.action);
      return path;
   }
}
