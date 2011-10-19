package au.edu.unsw.cse.jayen.search.examples.graph;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * a basic weighted graph
 * 
 * @author jayen
 */
public class Graph {
   /**
    * the underlying representation of the graph, mapping from nodes to
    * neighboring node/weight pairs
    */
   private final Map<Object, Map<Object, Integer>> graph;// TODO: use List<Edge>

   Graph() {
      graph = new HashMap<Object, Map<Object, Integer>>();
   }

   /**
    * the neighbors of a node
    * 
    * @param node
    *           the node
    * @return the neighbors, as pairings of nodes to weights
    */
   public Map<Object, Integer> neighbors(final Object node) {
      return graph.get(node);
   }

   /**
    * adds a directed edge between two nodes
    * 
    * @param node1
    *           the source node of this edge
    * @param node2
    *           the destination node of this edge
    * @param weight
    *           the weight of the edge
    */
   void addDirectedEdge(final Object node1, final Object node2, final int weight) {
      Map<Object, Integer> neighbors = graph.get(node1);
      if (neighbors == null) {
         neighbors = new LinkedHashMap<Object, Integer>();
         graph.put(node1, neighbors);
      }
      neighbors.put(node2, weight);
   }

   /**
    * adds directed edges in opposite directions between two nodes
    * 
    * @param node1
    *           one node to connect
    * @param node2
    *           the other node to connect
    * @param weight
    *           the weight of traversing between them
    */
   void addUndirectedEdge(final Object node1, final Object node2,
         final int weight) {
      addDirectedEdge(node1, node2, weight);
      addDirectedEdge(node2, node1, weight);
   }
}
