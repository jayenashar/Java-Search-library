package au.edu.unsw.cse.jayen.search.examples.graph;

import java.util.Formatter;
import java.util.List;

import au.edu.unsw.cse.jayen.search.AStarSearch;
import au.edu.unsw.cse.jayen.search.Action;
import au.edu.unsw.cse.jayen.search.Search;
import au.edu.unsw.cse.jayen.search.StateSpaceSearchProblem;

/**
 * implements the map of romania search from russell & norvig
 * 
 * @author jayen
 * 
 */
public class Romania {
   /**
    * finds the shortest path from Arad to Bucharest
    * 
    * @param args
    *           unused
    */
   public static void main(final String[] args) {
      final Graph graph = Romania.romaniaMap();
      final StateSpaceSearchProblem sssp = new GraphStateSpaceSearchProblem(
            graph, "Arad", "Bucharest");
      final Search search = new AStarSearch(new RomaniaHeuristics.Bucharest());
      final List<Action> actions = search.search(sssp);
      Romania.printOutput(search, actions);
   }

   /**
    * prints the path found
    * 
    * @param search
    *           the search performed
    * @param actions
    *           the actions to reach the goal
    */
   private static void printOutput(final Search search,
         final List<Action> actions) {
      System.out.println(search.nodesExplored() + " nodes explored");
      double cost = 0;
      for (final Action action : actions)
         cost += action.cost();
      System.out.println("cost = " + new Formatter().format("%.2f", cost));
      for (final Action action : actions)
         System.out.println(action);
   }

   /**
    * creates a map of romania as a Graph
    * 
    * @return a graph of romania
    */
   private static Graph romaniaMap() {
      final Graph graph = new Graph();
      graph.addUndirectedEdge("Oradea", "Zerind", 71);
      graph.addUndirectedEdge("Zerind", "Arad", 75);
      graph.addUndirectedEdge("Arad", "Sibiu", 140);
      graph.addUndirectedEdge("Sibiu", "Oradea", 151);
      graph.addUndirectedEdge("Timisoara", "Arad", 118);
      graph.addUndirectedEdge("Timisoara", "Lugoj", 111);
      graph.addUndirectedEdge("Lugoj", "Mehadia", 70);
      graph.addUndirectedEdge("Mehadia", "Dobreta", 75);
      graph.addUndirectedEdge("Dobreta", "Craiova", 120);
      graph.addUndirectedEdge("Sibiu", "Fagaras", 99);
      graph.addUndirectedEdge("Fagaras", "Bucharest", 211);
      graph.addUndirectedEdge("Sibiu", "Rimnicu Vilcea", 80);
      graph.addUndirectedEdge("Pitesti", "Rimnicu Vilcea", 97);
      graph.addUndirectedEdge("Craiova", "Rimnicu Vilcea", 146);
      graph.addUndirectedEdge("Craiova", "Pitesti", 136);
      graph.addUndirectedEdge("Pitesti", "Bucharest", 101);
      graph.addUndirectedEdge("Bucharest", "Giurgiu", 90);
      graph.addUndirectedEdge("Bucharest", "Urziceni", 85);
      graph.addUndirectedEdge("Urziceni", "Hirsova", 98);
      graph.addUndirectedEdge("Hirsova", "Eforie", 86);
      graph.addUndirectedEdge("Urziceni", "Vaslui", 142);
      graph.addUndirectedEdge("Vaslui", "Iasi", 92);
      graph.addUndirectedEdge("Neamt", "Iasi", 87);
      return graph;
   }
}
