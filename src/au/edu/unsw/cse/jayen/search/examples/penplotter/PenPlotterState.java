package au.edu.unsw.cse.jayen.search.examples.penplotter;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import au.edu.unsw.cse.jayen.search.Action;
import au.edu.unsw.cse.jayen.search.ActionStatePair;

/**
 * the state of a pen plotter
 * 
 * @author jayen
 * 
 */
public class PenPlotterState {
   /**
    * store the distances between any pair of lines
    */
   private static Map<Line2D, Map<Line2D, Double>> distances = null;
   /**
    * the lines left to draw
    */
   private final Line2D[] lines;

   /**
    * the point the pen is at
    */
   private final Point2D point;

   /**
    * @param point
    *           the point the pen would be at in this state
    * @param lines
    *           the lines the plotter would have left to draw
    */
   public PenPlotterState(final Point2D point, final Line2D[] lines) {
      this.point = point;
      this.lines = lines;
      // assume the first state has all the lines
      if (PenPlotterState.distances == null) {
         PenPlotterState.distances = new HashMap<Line2D, Map<Line2D, Double>>();
         for (final Line2D line1 : lines) {
            final Map<Line2D, Double> map1 = new LinkedHashMap<Line2D, Double>();
            PenPlotterState.distances.put(line1, map1);
            // TODO: figure out how to do this with line2 from line1 to lines
            for (final Line2D line2 : lines) {
               if (line2 == line1)
                  continue;
               double min = Double.POSITIVE_INFINITY;
               min = minDist(line1.getP1(), line2.getP1(), min);
               min = minDist(line1.getP1(), line2.getP2(), min);
               min = minDist(line1.getP2(), line2.getP1(), min);
               min = minDist(line1.getP2(), line2.getP2(), min);
               map1.put(line2, min);
            }
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(final Object obj) {
      if (this == obj)
         return true;
      // if (obj == null)
      // return false;
      // if (getClass() != obj.getClass())
      // return false;
      final PenPlotterState other = (PenPlotterState) obj;
      if (!Arrays.equals(lines, other.lines))
         return false;
      // if (point == null) {
      // if (other.point != null)
      // return false;
      // } else
      if (!point.equals(other.point))
         return false;
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(lines);
      result = prime * result + point.hashCode();
      return result;
   }

   /**
    * if there are no remaining lines
    * 
    * @return true, if there are no remaining lines; false, otherwise
    */
   public boolean isEmpty() {
      return lines.length == 0;
   }

   /**
    * the sum of the length of the remaining lines
    * 
    * @return the sum of the length of the remaining lines
    */
   public double linesSum() {
      double sum = 0;
      for (final Line2D line : lines)
         sum += line.getP1().distance(line.getP2());
      return sum;
   }

   /**
    * the distance of the pen to the closest remaining line
    * 
    * @return the distance of the pen to the closest remaining line
    */
   public double pointToClosestLine() {
      if (lines.length == 0)
         return 0;
      double min = Double.POSITIVE_INFINITY;
      for (final Line2D line : lines) {
         double d = point.distance(line.getP1());
         if (d < min)
            min = d;
         d = point.distance(line.getP2());
         if (d < min)
            min = d;
      }
      return min;
   }

   /**
    * the distance of the pen to the farthest endpoint of the remaining lines
    * 
    * @return the distance of the pen to the farthest endpoint of the remaining
    *         lines
    */
   public double pointToFarthestEndpoint() {
      if (lines.length == 0)
         return 0;
      double max = Double.NEGATIVE_INFINITY;
      for (final Line2D line : lines) {
         double d = point.distance(line.getP1());
         if (d > max)
            max = d;
         d = point.distance(line.getP2());
         if (d > max)
            max = d;
      }
      return max;
   }

   /**
    * the states that can be reached from this state, and their associated
    * action
    * 
    * @return action-state pairs that can be performed from this state
    */
   public Iterable<ActionStatePair> successor() {
      final Collection<ActionStatePair> successors = new ArrayList<ActionStatePair>();
      for (int lineIndex = 0; lineIndex < lines.length; ++lineIndex) {
         final Line2D line = lines[lineIndex];
         Point2D endPoint = line.getP2();
         final Line2D[] lines = new Line2D[this.lines.length - 1];
         for (int lineIndex2 = 0; lineIndex2 < this.lines.length; ++lineIndex2)
            if (lineIndex < lineIndex2)
               lines[lineIndex2 - 1] = this.lines[lineIndex2];
            else if (lineIndex2 < lineIndex)
               lines[lineIndex2] = this.lines[lineIndex2];
         Action action = new PenPlotterAction(point, line);
         Object state = new PenPlotterState(endPoint, lines);
         successors.add(new ActionStatePair(action, state));
         // and now the line in reverse
         endPoint = line.getP1();
         action = new PenPlotterAction(point, new Line(line.getP2(), line
               .getP1()));// TODO: improve memory by not recreating the line
         state = new PenPlotterState(endPoint, lines);
         successors.add(new ActionStatePair(action, state));
      }
      return successors;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return "PenPlotterState [lines=" + lines + ", point=" + point + "]";
   }

   /**
    * the length of the minimum spanning tree connecting the remaining lines
    * 
    * @return the length of the minimum spanning tree connecting the remaining
    *         lines
    */
   public double treeSum() {
      double sum = 0;
      if (lines.length > 0) {
         // variant of prim's
         final Map<Line2D, Double> disconnected = new LinkedHashMap<Line2D, Double>();
         int linesIndex = 0;
         final Line2D root = lines[linesIndex++];
         while (linesIndex < lines.length) {
            final Line2D disconnectedLine = lines[linesIndex++];
            disconnected.put(disconnectedLine, PenPlotterState.distances.get(
                  root).get(disconnectedLine));
         }
         while (!disconnected.isEmpty()) {
            double closestLineDistance = Double.POSITIVE_INFINITY;
            Line2D closestLine = null;
            for (final Map.Entry<Line2D, Double> disconnectedLine : disconnected
                  .entrySet()) {
               final double disconnectedDistance = disconnectedLine.getValue();
               if (disconnectedDistance < closestLineDistance) {
                  closestLineDistance = disconnectedDistance;
                  closestLine = disconnectedLine.getKey();
               }
            }
            disconnected.remove(closestLine);
            for (final Map.Entry<Line2D, Double> disconnectedLine : disconnected
                  .entrySet()) {
               final double disconnectedDistance = PenPlotterState.distances
                     .get(closestLine).get(disconnectedLine.getKey());
               if (disconnectedDistance < disconnectedLine.getValue())
                  disconnectedLine.setValue(disconnectedDistance);
            }
            sum += closestLineDistance;
         }
      }
      return sum + linesSum();
   }

   /**
    * returns the minimum of the given minimum and the distance between the two
    * points.
    * 
    * @param point1
    *           the first point
    * @param point2
    *           the second point
    * @param min
    *           the given minimum
    * @return <code>min</code>, if <code>min <</code> the distance; the
    *         distance, otherwise
    */
   private double minDist(final Point2D point1, final Point2D point2, double min) {
      final double distance = point1.distance(point2);
      if (distance < min)
         min = distance;
      return min;
   }
}
