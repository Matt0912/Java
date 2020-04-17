package mm16507;

import java.io.*;
import java.net.*;
import java.util.*;

class StagServer {
    private List<Location> locationList = new ArrayList<>();
    private List<Action> actionList = new ArrayList<>();
    private static int numberPlayers = 0;

    public static void main(String args[]) throws Exception {
        // Setup game with entities and actions inputs
        if(args.length != 2) System.out.println("Usage: java StagServer <entity-file> <action-file>");
        else new StagServer(args[0], args[1], 8888);
    }

    public StagServer(String entityFilename, String actionFilename, int portNumber) throws Exception {
        // Parse entities.dot file
        EntityParser.parse(entityFilename, locationList);
        // Parse actions.json file
        ActionParser.parse(actionFilename, actionList);
        try {
            ServerSocket ss = new ServerSocket(portNumber);
            System.out.println("Server Listening");
            while(true) acceptNextConnection(ss);
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void acceptNextConnection(ServerSocket ss) throws Exception {
        try {
            // Next line will block until a connection is received
            Socket socket = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            processNextCommand(in, out);
            out.close();
            in.close();
            socket.close();
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void processNextCommand(BufferedReader in, BufferedWriter out) throws Exception {
        String input = in.readLine();
        String[] splitInput = input.split(" ");
        String playerName = splitInput[0].substring(0, splitInput[0].length() - 1);

        // All new players must have different names to be registered as new player
        if (isNewPlayer(playerName)) {
            numberPlayers++;
            Player newPlayer = new Player(playerName, "Player " + numberPlayers, 3);
            locationList.get(0).addPlayer(newPlayer);
            out.write("Welcome to the Java Adventure Game " + playerName + "!\n" +
                    "Try using 'look' to take a look around and see where you are!\n" +
                    "Other commands are: 'inv' = open inventory, 'get' = get item, 'drop' = drop item\n" +
                    "                    'goto' = go to location, 'health' = display health\n");
        }

        UserInputParser parser = new UserInputParser(input, playerName, locationList, actionList);
        // Check for inbuilt special commands
        String output = parser.checkSpecialCommands();
        // If user input contains no special commands, check for actions
        if (output == null) {
            output = parser.checkActions();
        }
        out.write(output);
    }

    private boolean isNewPlayer(String playerName) {
        for (Location Loc : locationList) {
            if (Loc.getPlayer(playerName) != null) {
                return false;
            }
        }
        return true;
    }
}
