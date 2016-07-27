package au.edu.unsw.cse.jayen.search;

import au.edu.unsw.cse.jayen.util.PriorityQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * implements the A* search algorithm
 * 
 * @author jayen
 */
public class AStarSearch<State> implements Search<State> {

   /**
    * A wrapper class to store the best f & g for a given state, and also store h.
    * 
    * @param <ActionStatePair>    the type of wrapped state
    */
   private static class StateWrapper<ActionStatePair> implements Comparable<StateWrapper<ActionStatePair>> {
      /**
       * the wrapped state
       */
      private final ActionStatePair actionStatePair;

       /**
        * the costs for state
        */
       private final double f, g, h;

      /**
       * @param actionStatePair    the wrapped state
       * @param f        the total cost from initial to goal going through state
       * @param g        the cost from initial to state
       * @param h        the underestimated cost from state to goal
       */
      private StateWrapper(final ActionStatePair actionStatePair, final double f, final double g, final double h) {
         this.actionStatePair = actionStatePair;
         this.f = f;
         this.g = g;
         this.h = h;
      }

      /**
       * dummy constructor to create equal hashable wrappers that are not to be used for comparison
       *
       * @param actionStatePair    the wrapped state
       */
      private StateWrapper(final ActionStatePair actionStatePair) {
         this(actionStatePair, 0, 0, 0);
      }

      @Override
      public int compareTo(final StateWrapper<ActionStatePair> o) {
         final int compare = Double.compare(f, o.f);
         if (compare != 0) {
            return compare;
         } else {
            return Double.compare(h, o.h);
         }
      }

      @Override
      public boolean equals(final Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         final StateWrapper<?> that = (StateWrapper<?>) o;
         return Objects.equals(actionStatePair, that.actionStatePair);
      }

      @Override
      public int hashCode() {
         return Objects.hash(actionStatePair);
      }
   }

   // This would be faster if I stored visited in State, but that's bad design
   /**
    * the set of expanded states
    */
   private Set<State> closedSet;
   /**
    * the heuristic used by A*
    */
   private final Heuristic<State> heuristic;

   /**
    * A multiplier for the heuristic, so we expand fewer states
    */
   private final double justAboveOne;

   /**
    * @param heuristic
    *           the heuristic to be used by A*
    */
   public AStarSearch(final Heuristic<State> heuristic) {
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
   public AStarSearch(final Heuristic<State> heuristic, final double justAboveOne) {
      this.heuristic = heuristic;
      this.justAboveOne = justAboveOne;
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
      final Map<ActionStatePair<State>, ActionStatePair<State>> parent = new HashMap<>();
      final PriorityQueue<StateWrapper<ActionStatePair<State>>> openSet = new PriorityQueue<>();
      closedSet = new HashSet<>();
      for (final State state : sssp.initialStates()) {
         final double h = justAboveOne * heuristic.heuristic(state);
         final ActionStatePair<State> actionStatePair = new ActionStatePair<>(null, state);
         final StateWrapper<ActionStatePair<State>> stateWrapper = new StateWrapper<>(actionStatePair, h, 0, h);
         openSet.add(stateWrapper);
      }
      while (!openSet.isEmpty()) {
         StateWrapper<ActionStatePair<State>> currentWrapper = openSet.remove();
         ActionStatePair<State> current = currentWrapper.actionStatePair;
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
            final StateWrapper<ActionStatePair<State>> neighborWrapper = openSet.get(new StateWrapper<>(neighbor));
            final double newG = currentWrapper.g + neighbor.action.cost();
            if (neighborWrapper == null) {
               final double h = justAboveOne
                     * heuristic.heuristic(neighbor.state);
               parent.put(neighbor, current);

               openSet.add(new StateWrapper<>(neighbor, newG + h, newG, h));
            } else {
               final Double oldG = neighborWrapper.g;
               if (newG < oldG) {
                  openSet.remove(neighborWrapper);
                  final double h = neighborWrapper.h;
                  parent.put(neighbor, current);
                  openSet.add(new StateWrapper<>(neighbor, newG + h, newG, h));
               }
            }
         }
      }
      return null;
   }
}
