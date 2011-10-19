package au.edu.unsw.cse.jayen.search.examples.graph;

import au.edu.unsw.cse.jayen.search.Action;

/**
 * the act of traversing an edge in a graph
 * 
 * @author jayen
 */
public class GraphAction implements Action {

   /**
    * the cost of traversing
    */
   private final Integer cost;
   /**
    * the node we will end up in after this action
    */
   private final Object destination;
   /**
    * the node we were in before this action
    */
   private final Object source;

   /**
    * @param source
    *           the node we would have been in before this action
    * @param destination
    *           the node we would be in after this action
    * @param cost
    *           the cost of this action
    */
   public GraphAction(final Object source, final Object destination,
         final Integer cost) {
      this.source = source;
      this.destination = destination;
      this.cost = cost;
   }

   /*
    * (non-Javadoc)
    * 
    * @see Action#cost()
    */
   @Override
   public double cost() {
      return cost;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return "Move from " + source + " to " + destination;
   }
}
