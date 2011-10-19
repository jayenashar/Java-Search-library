package au.edu.unsw.cse.jayen.search.examples.penplotter;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * a line segment connecting two points
 * 
 * @author jayen
 * 
 */
public class Line extends Line2D {
   /**
    * the point to go from
    */
   private final Point2D p1;
   /**
    * the point to go to
    */
   private final Point2D p2;

   /**
    * @param point1
    *           the point to go from
    * @param point2
    *           the point to go to
    */
   public Line(final Point2D point1, final Point2D point2) {
      p1 = point1;
      p2 = point2;
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
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final Line other = (Line) obj;
      if (p1 == null) {
         if (other.p1 != null)
            return false;
      } else if (!p1.equals(other.p1))
         return false;
      if (p2 == null) {
         if (other.p2 != null)
            return false;
      } else if (!p2.equals(other.p2))
         return false;
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.Shape#getBounds2D()
    */
   @Override
   public Rectangle2D getBounds2D() {
      throw new UnsupportedOperationException();
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.geom.Line2D#getP1()
    */
   @Override
   public Point2D getP1() {
      return p1;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.geom.Line2D#getP2()
    */
   @Override
   public Point2D getP2() {
      return p2;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.geom.Line2D#getX1()
    */
   @Override
   public double getX1() {
      return p1.getX();
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.geom.Line2D#getX2()
    */
   @Override
   public double getX2() {
      return p2.getX();
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.geom.Line2D#getY1()
    */
   @Override
   public double getY1() {
      return p1.getY();
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.geom.Line2D#getY2()
    */
   @Override
   public double getY2() {
      return p2.getY();
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
      result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
      result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
      return result;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.geom.Line2D#setLine(double, double, double, double)
    */
   @Override
   public void setLine(final double x1, final double y1, final double x2,
         final double y2) {
      throw new UnsupportedOperationException();
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return p1 + " to " + p2;
   }

}
