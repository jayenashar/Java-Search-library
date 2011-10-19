package au.edu.unsw.cse.jayen.search.examples.penplotter;
import au.edu.unsw.cse.jayen.search.Heuristic;

/**
 * a collection of heuristics that can be used by the pen plotter
 * 
 * @author jayen
 * 
 */
public class PenPlotterHeuristics {
   // 107 nodes in the sample
   /**
    * the cost of drawing the remaining lines. O(n) where n is the number of
    * remaining lines. Admissible because you have to at least draw the
    * remaining lines.
    * 
    * @author jayen
    * 
    */
   public static class Lines implements Heuristic {
      /*
       * (non-Javadoc)
       * 
       * @see Heuristic#heuristic(java.lang.Object)
       */
      @Override
      public double heuristic(final Object state) {
         return ((PenPlotterState) state).linesSum();
      }
   }

   // 223 nodes in the sample
   /**
    * the cost of moving from where the pen is to the nearest remaining line.
    * O(n) where n is the number of remaining lines. Admissible because you have
    * to at least move to the remaining lines.
    * 
    * 
    * @author jayen
    * 
    */
   public static class Point implements Heuristic {
      /*
       * (non-Javadoc)
       * 
       * @see Heuristic#heuristic(java.lang.Object)
       */
      @Override
      public double heuristic(final Object state) {
         return ((PenPlotterState) state).pointToClosestLine();
      }
   }

   // 182 nodes in the sample
   /**
    * the cost of moving from where the pen is to the farthest remaining line
    * endpoint. O(n) where n is the number of remaining lines. Admissible
    * because you have to at least move to the farthest point.
    * 
    * @author jayen
    * 
    */
   public static class Point2 implements Heuristic {
      /*
       * (non-Javadoc)
       * 
       * @see Heuristic#heuristic(java.lang.Object)
       */
      @Override
      public double heuristic(final Object state) {
         return ((PenPlotterState) state).pointToFarthestEndpoint();
      }
   }

   // 41 nodes in the sample
   /**
    * the cost of moving from where the pen is to the nearest remaining line
    * plus the cost of drawing the remaining lines. O(n) where n is the number
    * of remaining lines. Admissible because you have to at least move to and
    * draw the remaining lines.
    * 
    * 
    * @author jayen
    * 
    */
   public static class PointLines implements Heuristic {
      /*
       * (non-Javadoc)
       * 
       * @see Heuristic#heuristic(java.lang.Object)
       */
      @Override
      public double heuristic(final Object state) {
         final PenPlotterState penPlotterState = (PenPlotterState) state;
         return penPlotterState.pointToClosestLine()
               + penPlotterState.linesSum();
      }
   }

   // 41 nodes in the sample
   /**
    * the cost of moving from where the pen is to the nearest remaining line
    * plus the cost of the minimum spanning tree connecting the remaining lines.
    * O(n^3) where n is the number of remaining lines. Admissible because you
    * have to at least move to and draw the remaining lines and move between
    * them.
    * 
    * 
    * @author jayen
    * 
    */
   public static class PointSpanningTree implements Heuristic {
      /*
       * (non-Javadoc)
       * 
       * @see Heuristic#heuristic(java.lang.Object)
       */
      @Override
      public double heuristic(final Object state) {
         final PenPlotterState penPlotterState = (PenPlotterState) state;
         return penPlotterState.pointToClosestLine()
               + penPlotterState.treeSum();
      }
   }

   // 107 nodes in the sample
   /**
    * the cost of the minimum spanning tree connecting the remaining lines.
    * O(n^3) where n is the number of remaining lines. Admissible because you
    * have to at least draw the remaining lines and move between them.
    * 
    * 
    * @author jayen
    * 
    */
   public static class SpanningTree implements Heuristic {
      /*
       * (non-Javadoc)
       * 
       * @see Heuristic#heuristic(java.lang.Object)
       */
      @Override
      public double heuristic(final Object state) {
         return ((PenPlotterState) state).treeSum();
      }
   }
}
