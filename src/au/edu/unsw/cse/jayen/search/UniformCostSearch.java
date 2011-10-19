package au.edu.unsw.cse.jayen.search;

public class UniformCostSearch extends AStarSearch {
   public UniformCostSearch() {
      super(new Heuristic.Zero());
   }
}
