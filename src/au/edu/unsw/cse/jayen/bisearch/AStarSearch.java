package au.edu.unsw.cse.jayen.bisearch;

import au.edu.unsw.cse.jayen.search.Action;
import au.edu.unsw.cse.jayen.search.ActionStatePair;
import au.edu.unsw.cse.jayen.search.Heuristic;
import au.edu.unsw.cse.jayen.util.HMap;
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
 * implements the A* search algorithm
 * 
 * @author jayen
 */
public class AStarSearch<State> implements Search<State> {

   /**
    * used by a priority queue in a* to compare f scores
    * 
    * @author jayen
    */
   private class Comparator implements java.util.Comparator<ActionStatePair<State>> {
      /**
       * the table of f scores
       */
      private final Map<State, Double> f;

      /**
       * the table of h scores
       */
      private final Map<State, Double> h;

      /**
       * @param f
       *           the table of f scores
       * @param h
       *           the table of h scores
       */
      public Comparator(final Map<State, Double> f, final Map<State, Double> h) {
         this.f = f;
         this.h = h;
      }

      /*
       * (non-Javadoc)
       * 
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      @Override
      public int compare(final ActionStatePair<State> o1, final ActionStatePair<State> o2) {
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
   private Set<State> closedSetGoal;

   /**
    * the set of expanded states from the initial states
    */
   private Set<State> closedSetInitial;
   /**
    * the heuristic used by A* from the goal states
    */
   private final Heuristic<State> heuristicGoal;
   /**
    * the heuristic used by A* from the initial states
    */
   private final Heuristic<State> heuristicInitial;

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
   public AStarSearch(final Heuristic<State> heuristicInitial,
         final Heuristic<State> heuristicGoal) {
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
   public AStarSearch(final Heuristic<State> heuristicInitial,
         final Heuristic<State> heuristicGoal, final double justAboveOneInitial,
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
   public List<Action> search(final StateSpaceSearchProblem<State> sssp) {
      // This would be faster if I stored f & g in State, but that's bad design
      final Map<State, Double> g = new HashMap<>();
      final Map<State, Double> h = new HashMap<>();
      final Map<State, Double> f = new HashMap<>();
      final HMap<ActionStatePair, ActionStatePair> parentInitial = new HMap<>();
      final HMap<ActionStatePair, ActionStatePair> parentGoal = new HMap<>();
      // could be done with two openSets
      final Map<State, Boolean> forward = new java.util.HashMap<>();
      final Queue<ActionStatePair<State>> openSet = new PriorityQueue<>(
              1, new Comparator(f, h));
      closedSetInitial = new HashSet<>();
      closedSetGoal = new HashSet<>();
      for (final State state : sssp.initialStates()) {
         g.put(state, 0.);
         final double h2 = justAboveOneInitial
               * heuristicInitial.heuristic(state);
         h.put(state, h2);
         f.put(state, h2);
         openSet.add(new ActionStatePair<>(null, state));
         forward.put(state, true);
      }
      for (final State state : sssp.goalStates()) {
         g.put(state, 0.);
         final double h2 = justAboveOneInitial * heuristicGoal.heuristic(state);
         h.put(state, h2);
         f.put(state, h2);
         openSet.add(new ActionStatePair<>(null, state));
         forward.put(state, false);
      }
      while (!openSet.isEmpty()) {
         final ActionStatePair<State> current = openSet.remove();
         if (forward.get(current.state)) {
            closedSetInitial.add(current.state);
            if (closedSetGoal.contains(current.state))
               return calculatePath(parentInitial, parentGoal, current);
            for (final ActionStatePair<State> neighbor : sssp.successor(current.state)) {
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
            for (final ActionStatePair<State> neighbor : sssp
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
      final List<Action> path = new ArrayList<>();
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
