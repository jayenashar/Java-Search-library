package au.edu.unsw.cse.jayen.examples.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.edu.unsw.cse.jayen.search.Action;
import au.edu.unsw.cse.jayen.search.ActionStatePair;
import au.edu.unsw.cse.jayen.search.StateSpaceSearchProblem;

/**
 * a thin layer to convert a graph into a state space search problem
 * 
 * @author jayen
 */
public class GraphStateSpaceSearchProblem implements StateSpaceSearchProblem,
      au.edu.unsw.cse.jayen.bisearch.StateSpaceSearchProblem {

   /**
    * the goal nodes
    */
   private final Set<Object> goalStates;
   /**
    * the graph
    */
   private final Graph graph;
   /**
    * the initial node
    */
   private final Object initialState;

   /**
    * @param graph
    *           the graph
    * @param initial
    *           the initial node
    * @param goal
    *           the goal node
    */
   public GraphStateSpaceSearchProblem(final Graph graph, final Object initial,
         final Object goal) {
      this.graph = graph;
      initialState = initial;
      goalStates = new HashSet<Object>();
      goalStates.add(goal);
   }

   /*
    * (non-Javadoc)
    * 
    * @see au.edu.unsw.cse.jayen.bisearch.StateSpaceSearchProblem#goalStates()
    */
   @Override
   public Iterable<Object> goalStates() {
      return goalStates;
   }

   /*
    * (non-Javadoc)
    * 
    * @see StateSpaceSearchProblem#initialStates()
    */
   @Override
   public Iterable<Object> initialStates() {
      final Collection<Object> states = new ArrayList<Object>();
      states.add(initialState);
      return states;
   }

   /*
    * (non-Javadoc)
    * 
    * @see StateSpaceSearchProblem#isGoal(java.lang.Object)
    */
   @Override
   public boolean isGoal(final Object state) {
      return goalStates.contains(state);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * au.edu.unsw.cse.jayen.bisearch.StateSpaceSearchProblem#predecessor(java
    * .lang.Object)
    */
   @Override
   public Iterable<ActionStatePair> predecessor(final Object state) {
      final Collection<ActionStatePair> predecessors = new ArrayList<ActionStatePair>();
      for (final Map.Entry<Object, Integer> neighbor : graph.neighbors(state)
            .entrySet()) {
         final Action action = new GraphAction(neighbor.getKey(), state,
               neighbor.getValue());
         predecessors.add(new ActionStatePair(action, neighbor.getKey()));
      }
      return predecessors;
   }

   /*
    * (non-Javadoc)
    * 
    * @see StateSpaceSearchProblem#successor(java.lang.Object)
    */
   @Override
   public Iterable<ActionStatePair> successor(final Object state) {
      final Collection<ActionStatePair> successors = new ArrayList<ActionStatePair>();
      for (final Map.Entry<Object, Integer> neighbor : graph.neighbors(state)
            .entrySet()) {
         final Action action = new GraphAction(state, neighbor.getKey(),
               neighbor.getValue());
         successors.add(new ActionStatePair(action, neighbor.getKey()));
      }
      return successors;
   }
}
