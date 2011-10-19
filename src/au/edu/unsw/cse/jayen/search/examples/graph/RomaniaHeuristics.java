package au.edu.unsw.cse.jayen.search.examples.graph;
import java.util.HashMap;
import java.util.Map;

import au.edu.unsw.cse.jayen.search.Heuristic;

/**
 * a collection of heuristics for the map of romania from russell & norvig
 * 
 * @author jayen
 * 
 */
public class RomaniaHeuristics {

   /**
    * the heurstic distance to bucharest
    * 
    * @author jayen
    * 
    */
   static public class Bucharest implements Heuristic {

      /**
       * a map to store the distances
       */
      private final Map<Object, Integer> distanceToBucharest;

      Bucharest() {
         distanceToBucharest = new HashMap<Object, Integer>();
         distanceToBucharest.put("Arad", 366);
         distanceToBucharest.put("Bucharest", 0);
         distanceToBucharest.put("Craiova", 160);
         distanceToBucharest.put("Dobreta", 242);
         distanceToBucharest.put("Eforie", 161);
         distanceToBucharest.put("Fagaras", 178);
         distanceToBucharest.put("Giurgiu", 77);
         distanceToBucharest.put("Hirsova", 151);
         distanceToBucharest.put("Iasi", 226);
         distanceToBucharest.put("Lugoj", 244);
         distanceToBucharest.put("Mehadia", 241);
         distanceToBucharest.put("Neamt", 234);
         distanceToBucharest.put("Oradea", 380);
         distanceToBucharest.put("Pitesti", 93);
         distanceToBucharest.put("Rimnicu Vilcea", 193);
         distanceToBucharest.put("Sibiu", 253);
         distanceToBucharest.put("Timisoara", 329);
         distanceToBucharest.put("Urziceni", 80);
         distanceToBucharest.put("Vaslui", 199);
         distanceToBucharest.put("Zerind", 374);
      }

      /*
       * (non-Javadoc)
       * 
       * @see Heuristic#heuristic(java.lang.Object)
       */
      @Override
      public double heuristic(final Object state) {
         return distanceToBucharest.get(state);
      }

   }

}
