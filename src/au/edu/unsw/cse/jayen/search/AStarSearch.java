package au.edu.unsw.cse.jayen.search;

import au.edu.unsw.cse.jayen.util.PriorityQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
    * @param <State>    the type of wrapped state
    */
   private static class StateWrapper<State> implements Comparable<StateWrapper<State>> {
      /**
       * the wrapped state
       */
      private final ActionStatePair<State> actionStatePair;

       /**
        * the costs for state
        */
       private final double f, g, h;

       /**
        * the previous state
        */
       private final StateWrapper<State> predecessor;

      /**
       * @param actionStatePair    the wrapped state
       * @param f        the total cost from initial to goal going through state
       * @param g        the cost from initial to state
       * @param h        the underestimated cost from state to goal
       * @param predecessor             the previous state
       */
      private StateWrapper(final ActionStatePair<State> actionStatePair,
                           final double f,
                           final double g,
                           final double h,
                           final StateWrapper<State> predecessor) {
         this.actionStatePair = actionStatePair;
         this.f = f;
         this.g = g;
         this.h = h;
         this.predecessor = predecessor;
      }

      /**
       * dummy constructor to create equal hashable wrappers that are not to be used for comparison
       *
       * @param actionStatePair    the wrapped state
       */
      private StateWrapper(final ActionStatePair<State> actionStatePair) {
         this(actionStatePair, 0, 0, 0, null);
      }

      @Override
      public int compareTo(final StateWrapper<State> o) {
         final int compare = Double.compare(f, o.f);
         if (compare != 0) {
            return compare;
         } else {
            return Double.compare(h, o.h);
         }
      }

      @Override
      public boolean equals(final Object o) {
         final StateWrapper<?> that = (StateWrapper<?>) o;
         final ActionStatePair other = (ActionStatePair) that.actionStatePair;
         return actionStatePair.state.equals(other.state);
      }

      @Override
      public int hashCode() {
         return Objects.hash(actionStatePair);
      }
   }

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
      final PriorityQueue<StateWrapper<State>> openSet = new PriorityQueue<>();
      closedSet = new HashSet<>();
      for (final State state : sssp.initialStates()) {
         final double h = justAboveOne * heuristic.heuristic(state);
         final ActionStatePair<State> actionStatePair = new ActionStatePair<>(null, state);
         final StateWrapper<State> stateWrapper = new StateWrapper<>(actionStatePair, h, 0, h, null);
         openSet.add(stateWrapper);
      }
      while (!openSet.isEmpty()) {
         StateWrapper<State> currentWrapper = openSet.remove();
         final State currentState = currentWrapper.actionStatePair.state;
         closedSet.add(currentState);
         if (sssp.isGoal(currentState)) {
            final List<Action> path = new ArrayList<>();
            for (;currentWrapper.actionStatePair.action != null; currentWrapper = currentWrapper.predecessor)
               path.add(currentWrapper.actionStatePair.action);
            Collections.reverse(path);
            return path;
         }
         for (final ActionStatePair<State> successor : sssp.successor(currentState)) {
            if (closedSet.contains(successor.state))
               continue;
            final StateWrapper<State> successorWrapper = openSet.get(new StateWrapper<>(successor));
            final double newG = currentWrapper.g + successor.action.cost();
            if (successorWrapper == null) {
               final double h = justAboveOne
                     * heuristic.heuristic(successor.state);

               openSet.add(new StateWrapper<>(successor, newG + h, newG, h, currentWrapper));
            } else {
               final Double oldG = successorWrapper.g;
               if (newG < oldG) {
                  openSet.remove(successorWrapper);
                  final double h = successorWrapper.h;
                  openSet.add(new StateWrapper<>(successor, newG + h, newG, h, currentWrapper));
               }
            }
         }
      }
      return null;
   }
}
