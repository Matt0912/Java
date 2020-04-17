package mm16507;

import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class EntityParser {

    public static void parse(String entityFile, List<Location> locationList) throws InvalidEntitiesFileException {
        Location tempLocation;
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(entityFile);
            parser.parse(reader);
            ArrayList<Graph> graphs = parser.getGraphs();
            ArrayList<Graph> subGraphs = graphs.get(0).getSubgraphs();
            for (Graph g : subGraphs){
                ArrayList<Graph> subGraphs1 = g.getSubgraphs();
                for (Graph g1 : subGraphs1){
                    ArrayList<Node> nodesLoc = g1.getNodes(false);
                    Node nLoc = nodesLoc.get(0);
                    // Create new location entity then add to the list
                    tempLocation = new Location(nLoc.getId().getId(), nLoc.getAttribute("description"));
                    locationList.add(tempLocation);
                    ArrayList<Graph> subGraphs2 = g1.getSubgraphs();
                    for (Graph g2 : subGraphs2) {
                        ArrayList<Node> nodesEnt = g2.getNodes(false);
                        // Add artefact to list of artefacts in location
                        if (g2.getId().getId().equals("artefacts")) {
                            for (Node nEnt : nodesEnt) {
                                tempLocation.addArtefact(new Artefact(nEnt.getId().getId(), nEnt.getAttribute("description")));
                            }
                        }
                        // Add furniture to list of furniture in location
                        if (g2.getId().getId().equals("furniture")) {
                            for (Node nEnt : nodesEnt) {
                                tempLocation.addFurniture(new Furniture(nEnt.getId().getId(), nEnt.getAttribute("description")));
                            }
                        }
                        // Add character to list of characters in location
                        if (g2.getId().getId().equals("characters")) {
                            for (Node nEnt : nodesEnt) {
                                tempLocation.addCharacter(new Character(nEnt.getId().getId(), nEnt.getAttribute("description")));
                            }
                        }
                    }
                }

                // Iterate through list of paths
                ArrayList<Edge> edges = g.getEdges();
                for (Edge e : edges) {
                    // Search through list of locations, if the start of the path matches, add the destination to
                    // the list of available paths in that location
                    for (Location L : locationList) {
                        if (L.getName().equals(e.getSource().getNode().getId().getId())) {
                            L.addPath(e.getTarget().getNode().getId().getId());
                        }
                    }
                }
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe);
        } catch (com.alexmerz.graphviz.ParseException pe) {
            System.out.println(pe);
        }

        try {
            checkUnplaced(locationList);
        } catch (InvalidEntitiesFileException iefe) {
            System.out.println(iefe);
            System.exit(1);
        }
    }

    public static void checkUnplaced(List<Location> locationList) throws InvalidEntitiesFileException {
        for (Location Loc : locationList) {
            if (Loc.getName().equals("unplaced")) {
                return;
            }
        }
        throw new InvalidEntitiesFileException("unplaced");
    }
}
