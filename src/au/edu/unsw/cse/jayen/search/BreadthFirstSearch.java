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
public class BreadthFirstSearch implements Search {
   /**
    * the set of expanded states
    */
   private Set<Object> closedSet;

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
      final Map<ActionStatePair, ActionStatePair> parent = new HashMap<ActionStatePair, ActionStatePair>();
      final LinkedHashSet<ActionStatePair> openSet = new LinkedHashSet<ActionStatePair>();
      closedSet = new HashSet<Object>();
      for (final Object state : sssp.initialStates())
         openSet.add(new ActionStatePair(null, state));
      while (!openSet.isEmpty()) {
         ActionStatePair current = openSet.iterator().next();
         openSet.remove(current);
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
               parent.put(neighbor, current);
               openSet.add(neighbor);
            }
         }
      }
      return null;
   }
}
