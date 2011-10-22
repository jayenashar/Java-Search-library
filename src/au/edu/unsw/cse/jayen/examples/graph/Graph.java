package au.edu.unsw.cse.jayen.examples.graph;

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
    * successor neighboring node/weight pairs
    */
   private final Map<Object, Map<Object, Integer>> tails;// TODO: use List<Edge>

   /**
    * the underlying representation of the graph, mapping from nodes to
    * predecessor neighboring node/weight pairs
    */
   private final Map<Object, Map<Object, Integer>> heads;// TODO: use List<Edge>

   Graph() {
      tails = new HashMap<Object, Map<Object, Integer>>();
      heads = new HashMap<Object, Map<Object, Integer>>();
   }

   /**
    * the successor neighbors of a node
    * 
    * @param node
    *           the node
    * @return the successor neighbors, as pairings of nodes to weights
    */
   public Map<Object, Integer> successorNeighbors(final Object node) {
      return tails.get(node);
   }

   /**
    * the predecessor neighbors of a node
    * 
    * @param node
    *           the node
    * @return the predecessor neighbors, as pairings of nodes to weights
    */
   public Map<Object, Integer> predecessorNeighbors(final Object node) {
      return heads.get(node);
   }

   /**
    * adds a directed edge between two nodes
    * 
    * @param head
    *           the source node of this edge
    * @param tail
    *           the destination node of this edge
    * @param weight
    *           the weight of the edge
    */
   void addDirectedEdge(final Object head, final Object tail, final int weight) {
      Map<Object, Integer> neighbors = tails.get(head);
      if (neighbors == null) {
         neighbors = new LinkedHashMap<Object, Integer>();
         tails.put(head, neighbors);
      }
      neighbors.put(tail, weight);
      
      neighbors = heads.get(tail);
      if (neighbors == null) {
         neighbors = new LinkedHashMap<Object, Integer>();
         heads.put(tail, neighbors);
      }
      neighbors.put(head, weight);
   }

   /**
    * adds symmetric edges between two nodes
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
