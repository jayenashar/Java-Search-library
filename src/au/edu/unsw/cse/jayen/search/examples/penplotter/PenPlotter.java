package au.edu.unsw.cse.jayen.search.examples.penplotter;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

import au.edu.unsw.cse.jayen.search.AStarSearch;
import au.edu.unsw.cse.jayen.search.Action;
import au.edu.unsw.cse.jayen.search.Search;
import au.edu.unsw.cse.jayen.search.StateSpaceSearchProblem;

/**
 * implements the pen plotter from UNSW's COMP2911 11s2 ass2.
 * 
 * see {@link PenPlotterHeuristics} for analyses
 * 
 * @see PenPlotterHeuristics
 * @author jayen
 * 
 */
public class PenPlotter {
   /**
    * takes a file containing lines and finds the cheapest set of actions to
    * draw those lines
    * 
    * @param args
    *           the file
    */
   public static void main(final String[] args) {
      final Collection<Line2D> lines = PenPlotter.parseFile(args);
      final StateSpaceSearchProblem sssp = new PenPlotterStateSpaceSearchProblem(
            lines);
      final Search search = new AStarSearch(
            new PenPlotterHeuristics.PointSpanningTree());
      final List<Action> actions = search.search(sssp);
      PenPlotter.printOutput(search, actions);
   }

   /**
    * converts a file to a collection of lines
    * 
    * @param args
    *           the file
    * @return the collection of lines
    */
   private static Collection<Line2D> parseFile(final String[] args) {
      Scanner fileScanner = null;
      try {
         fileScanner = new Scanner(new FileReader(args[0]));
      } catch (final FileNotFoundException exception) {
         exception.printStackTrace();
         System.exit(10);
      }
      final Collection<Line2D> lines = new ArrayList<Line2D>();
      while (fileScanner.hasNextLine()) {
         final Scanner lineScanner = new Scanner(fileScanner.nextLine());
         lineScanner.next(); // no checking of input
         lineScanner.next(); // no checking of input
         final Point2D point1 = new Point(lineScanner.nextInt(), lineScanner
               .nextInt());
         lineScanner.next(); // no checking of input
         final Point2D point2 = new Point(lineScanner.nextInt(), lineScanner
               .nextInt());
         final Line2D line = new Line(point1, point2);
         lines.add(line);
      }
      return lines;
   }

   /**
    * prints out the actions found
    * 
    * @param search
    *           the search performed
    * @param actions
    *           the actions found
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
}
