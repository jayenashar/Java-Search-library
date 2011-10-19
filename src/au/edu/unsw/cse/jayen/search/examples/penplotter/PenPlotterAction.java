package au.edu.unsw.cse.jayen.search.examples.penplotter;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import au.edu.unsw.cse.jayen.search.Action;

/**
 * represents a move action followed by a draw action of a pen plotter
 * 
 * @author jayen
 * 
 */
public class PenPlotterAction implements Action {

   /**
    * the line drawn
    */
   private final Line2D line;
   /**
    * the point moved from
    */
   private final Point2D point;

   /**
    * @param point
    *           the point this action would move from
    * @param line
    *           the line this action would draw
    */
   public PenPlotterAction(final Point2D point, final Line2D line) {
      this.point = point;
      this.line = line;
   }

   /*
    * (non-Javadoc)
    * 
    * @see Action#cost()
    */
   @Override
   public double cost() {
      final Point2D point2 = line.getP1();
      return point.distance(point2) + point2.distance(line.getP2());
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      String string = "";
      if (!point.equals(line.getP1()))
         string += "Move from " + point + " to " + line.getP1() + "\n";
      string += "Draw from " + line;
      return string;
   }
}
