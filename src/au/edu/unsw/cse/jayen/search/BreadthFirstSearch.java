package au.edu.unsw.cse.jayen.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * an implementation of the breadth-first search algorithm
 * 
 * @author jayen
 */
public class BreadthFirstSearch<State> implements Search<State> {
   /**
    * the set of expanded states
    */
   private Set<State> closedSet;

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
      final LinkedHashSet<ActionStatePair<State>> openSet = new LinkedHashSet<>();
      closedSet = new HashSet<>();
      for (final State state : sssp.initialStates())
         openSet.add(new ActionStatePair<>(null, state));
      while (!openSet.isEmpty()) {
         ActionStatePair<State> current = openSet.iterator().next();
         openSet.remove(current);
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
               parent.put(neighbor, current);
               openSet.add(neighbor);
            }
         }
      }
      return null;
   }
}
