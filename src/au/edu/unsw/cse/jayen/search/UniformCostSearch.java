package au.edu.unsw.cse.jayen.search;

public class UniformCostSearch<State> extends AStarSearch<State> {
   public UniformCostSearch() {
      super(new Heuristic.Zero<>());
   }
}
