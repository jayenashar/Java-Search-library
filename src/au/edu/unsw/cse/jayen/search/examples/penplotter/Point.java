package au.edu.unsw.cse.jayen.search.examples.penplotter;
import java.awt.geom.Point2D;

/**
 * represents a point on the pen plotter
 * 
 * @author jayen
 * 
 */
public class Point extends Point2D {
   /**
    * the x-coordinate
    */
   private final int x;
   /**
    * the y-coordinate
    */
   private final int y;

   /**
    * @param x
    *           the x-coordinate
    * @param y
    *           the y-coordinate
    */
   public Point(final int x, final int y) {
      this.x = x;
      this.y = y;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.geom.Point2D#equals(java.lang.Object)
    */
   @Override
   public boolean equals(final Object obj) {
      if (this == obj)
         return true;
      // if (!super.equals(obj))
      // return false;
      // if (getClass() != obj.getClass())
      // return false;
      final Point other = (Point) obj;
      if (x != other.x)
         return false;
      if (y != other.y)
         return false;
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.geom.Point2D#getX()
    */
   @Override
   public double getX() {
      return x;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.geom.Point2D#getY()
    */
   @Override
   public double getY() {
      return y;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.geom.Point2D#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;// super.hashCode();
      result = prime * result + x;
      result = prime * result + y;
      return result;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.awt.geom.Point2D#setLocation(double, double)
    */
   @Override
   public void setLocation(final double x, final double y) {
      throw new UnsupportedOperationException();
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return x + " " + y;
   }

}
