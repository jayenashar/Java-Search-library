package au.edu.unsw.cse.jayen.search.examples.penplotter;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;

import au.edu.unsw.cse.jayen.search.ActionStatePair;
import au.edu.unsw.cse.jayen.search.StateSpaceSearchProblem;

/**
 * represents the pen plotter problem as a state space search problem
 * 
 * @author jayen
 * 
 */
public class PenPlotterStateSpaceSearchProblem implements
      StateSpaceSearchProblem {

   /**
    * the initial state of the pen plotter
    */
   private final Object initialState;

   /**
    * @param lines
    *           the lines to draw
    */
   public PenPlotterStateSpaceSearchProblem(final Collection<Line2D> lines) {
      this(new PenPlotterState(new Point(0, 0), lines.toArray(new Line2D[0])));
   }

   /**
    * @param state
    *           the initial state
    */
   public PenPlotterStateSpaceSearchProblem(Object state) {
      initialState = state;
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
      final PenPlotterState penPlotterState = (PenPlotterState) state;
      return penPlotterState.isEmpty();
   }

   /*
    * (non-Javadoc)
    * 
    * @see StateSpaceSearchProblem#successor(java.lang.Object)
    */
   @Override
   public Iterable<ActionStatePair> successor(final Object state) {
      final PenPlotterState penPlotterState = (PenPlotterState) state;
      return penPlotterState.successor();
   }

}
