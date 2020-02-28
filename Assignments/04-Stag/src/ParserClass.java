
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Id;
import com.alexmerz.graphviz.objects.Node;

public class ParserClass {

    public static void main(String[] args) {
        FileReader in=null;
        File f = new File( "C:\\Users\\Matt\\Documents\\CompSciLinux\\Java\\Assignments\\04-Stag\\src\\data/entities.dot" );

        try {
            in = new FileReader(f);
            Parser p = new Parser();
            p.parse(in);
        } catch (FileNotFoundException e) {
            System.out.println();
        } catch (ParseException e) {
            // do something if the parser caused a parser error
        }

        // everything ok
        ArrayList<Graph> gl = p.getGraphs();

        // do something with the Graph objects
    }
}

