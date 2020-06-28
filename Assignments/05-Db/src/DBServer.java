import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;

public class DBServer {
    final static char EOT = 4;

    public static void main(String[] args) {
        DBServer server = new DBServer(8888);
    }

    public DBServer(int portNumber) {
        try {
            ServerSocket ss = new ServerSocket(portNumber);
            System.out.println("Server Listening");
            while (true) acceptConnection(ss);
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void acceptConnection(ServerSocket ss) {
        try {
            Socket clientSocket = ss.accept();
            System.out.println("Connected to client");
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String input = in.readLine();
            while (input != null) {
                processCommand(input, out);
                input = in.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void processCommand(String input, PrintWriter out) {
        DBMS databaseManager = DBMS.getDBMS();
        Parser parser = Parser.getParser();
        try {
            parser.parse(input);
            // If user input is parsed correctly (i.e. parse method doesn't throw parseException),
            // then print the values stored in DBMS query output
            out.println(databaseManager.getQueryOutput());
            out.println(EOT);
        }
        // All exceptions thrown from parser + database handled here and printed to client
        catch(ParseException | IOException | ClassNotFoundException e) {
            out.println(e.getMessage() + "\n" + EOT);
        }
        catch (RuntimeException e) {
            out.println("ERROR: Invalid query" + "\n" + EOT);
        }
    }
}
