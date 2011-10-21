package au.edu.unsw.cse.jayen.bisearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.edu.unsw.cse.jayen.search.Action;
import au.edu.unsw.cse.jayen.search.ActionStatePair;
import au.edu.unsw.cse.jayen.util.HMap;

/**
 * an implementation of the breadth-first search algorithm
 * 
 * @author jayen
 */
public class BreadthFirstSearch implements Search {
   /**
    * the set of expanded states from the goal states
    */
   private Set<Object> closedSetGoal;

   /**
    * the set of expanded states from the initial states
    */
   private Set<Object> closedSetInitial;

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
      final HMap<ActionStatePair, ActionStatePair> parentInitial = new HMap<ActionStatePair, ActionStatePair>();
      final HMap<ActionStatePair, ActionStatePair> parentGoal = new HMap<ActionStatePair, ActionStatePair>();
      // could be done with two openSets
      final Map<Object, Boolean> forward = new java.util.HashMap<Object, Boolean>();
      final LinkedHashSet<ActionStatePair> openSet = new LinkedHashSet<ActionStatePair>();
      closedSetInitial = new HashSet<Object>();
      closedSetGoal = new HashSet<Object>();
      for (final Object state : sssp.initialStates()) {
         openSet.add(new ActionStatePair(null, state));
         forward.put(state, true);
      }
      for (final Object state : sssp.goalStates()) {
         openSet.add(new ActionStatePair(null, state));
         forward.put(state, false);
      }
      while (!openSet.isEmpty()) {
         final ActionStatePair current = openSet.iterator().next();
         openSet.remove(current);
         if (forward.get(current.state)) {
            closedSetInitial.add(current.state);
            if (closedSetGoal.contains(current.state))
               return calculatePath(parentInitial, parentGoal, current);
            for (final ActionStatePair neighbor : sssp.successor(current.state)) {
               if (closedSetInitial.contains(neighbor.state))
                  continue;
               if (!openSet.contains(neighbor)) {
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
               if (!openSet.contains(neighbor)) {
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
