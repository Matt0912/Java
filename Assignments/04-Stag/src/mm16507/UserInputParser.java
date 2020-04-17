import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.lang.String;

public class UserInputParser {
    private String input;
    private String[] splitInput;
    private List<Location> locationList;
    private List<Action> actionList;
    private Location currentLocation;
    private Player currentPlayer;

    public UserInputParser(String input, String playerName, List<Location> locationList, List<Action> actionList) {
        this.input = input;
        this.locationList = locationList;
        this.actionList = actionList;
        splitInput = input.split(" ");
        splitInput = Arrays.copyOfRange(splitInput, 1, splitInput.length);

        for (Location Loc : locationList) {
            currentPlayer = Loc.getPlayer(playerName);
            if (currentPlayer != null) {
                currentLocation = Loc;
                return;
            }
        }
    }

    public String checkSpecialCommands() {
        if (input.contains("health")) {
            return currentPlayer.getName() + "'s Health = " + currentPlayer.getHealth();
        }

        if (input.contains("inv")) {
            String output = "Inventory: ";
            if (currentPlayer.returnInventory().size() == 0) {
                return "Inventory is empty";
            }
            for (Artefact art : currentPlayer.returnInventory()) {
                output = output + art.getName() + ", ";
            }
            return output.substring(0, output.length() - 2);
        }

        if (input.contains("get")) {
            Artefact newItem = null;
            for (String str : splitInput) {
                newItem = currentLocation.removeArtefact(str);
                if (newItem != null) {
                    currentPlayer.addInventory(newItem);
                    return currentPlayer.getName() + " picked up " + newItem.getName();
                }
            }
            return "Couldn't pick up the item you were looking for";
        }

        if (input.contains("drop")) {
            Artefact dropItem = null;
            for (String str : splitInput) {
                dropItem = currentPlayer.removeInventory(str);
                if (dropItem != null) {
                    currentLocation.addArtefact(dropItem);
                    return currentPlayer.getName() + " dropped " + dropItem.getName() + " on the floor";
                }
            }
            return "You're not carrying that item!";
        }

        if (input.contains("goto")) {
            for (String str : splitInput) {
                if (currentLocation.searchPathList(str)) {
                    for (Location Loc : locationList) {
                        if (Loc.getName().equals(str)) {
                            currentLocation.removePlayer(currentPlayer.getName());
                            Loc.addPlayer(currentPlayer);
                            return "You move from the " + currentLocation.getName()
                                    + " -> " + Loc.getDescription();
                        }
                    }
                }
            }
            return "Couldn't find destination - possible destinations are: " + currentLocation.getPathList();
        }

        if (input.contains("look")) {
            return "You are in: " + currentLocation.getDescription() +
                    "\nItems: " + currentLocation.getArtefacts()  +
                    "\nFurniture: " + currentLocation.getFurniture() +
                    "\nCharacters: " + currentLocation.getCharacter() +
                    "\nAvailable Locations: " + currentLocation.getPathList();
        }

        return null;
    }

    public String checkActions() {
        boolean trigCheck = false;
        Action currentAction = null;

        // Check every action in the list - if one action doesn't have all subjects, keep searching
        for (int i = 0; i < actionList.size(); i++) {
            Action act = actionList.get(i);
            for (String str : splitInput) {
                // If user input matches action triggers, use that action
                if (act.searchTriggers(str)) {
                    trigCheck = true;
                    // If all subjects found, exit loop - else keep searching triggers
                    if (searchList(act.getSubjects())) {
                        currentAction = act;
                        i = actionList.size();
                    }
                }
            }
        }

        if (!trigCheck) {
            return "No trigger words found";
        }
        // If no action has been assigned, subject requirements weren't met
        if (currentAction == null) {
            return "Are you sure you have everything that is required?";
        }

        // Check all consumed items exist
        if (searchList(currentAction.getConsumed())) {
            consumeItems(currentAction);
            if (currentPlayer.getHealth() <= 0) {
                resetPlayer();
                return "You died! \nAll of your items have been dropped and you have been sent back to the start!";
            }
        }
        else {
            return "Couldn't find consumed item: check game files!";
        }

        if (produceItems(currentAction)) {
            return currentAction.getNarration();
        }
        else {
            return "You can't do that again!";
        }
    }

    // Search location + inventory for subjects in a list
    // Based on the last correspondence from Si(m)on, my code now accepts any user input as
    // long as it contains the trigger word AND all subjects are present
    // e.g. 'open', 'open key', 'open door' all work
    public boolean searchList(List<String> list) {
        int count = 0;
        for (String subject : list) {
            if (currentLocation.searchEntities(subject) || currentPlayer.searchInventory(subject)
                    || subject.equals("health")) {
                count++;
            }
            else {
                return false;
            }
        }
        // If all subjects are found: true, if not all found: false
        return count == list.size();
    }

    // Search location + inventory for consumed items (or player health)
    public void consumeItems(Action action) {
        if (action.getConsumed().size() == 0) {
            return;
        }
        for (String str : action.getConsumed()) {
            currentLocation.removeArtefact(str);
            currentLocation.removeFurniture(str);
            currentLocation.removeCharacter(str);
            currentLocation.removePath(str);
            currentPlayer.removeInventory(str);
            // Check if consumed item is player health
            if (str.equals("health")) {
                currentPlayer.setHealth(currentPlayer.getHealth() - 1);
            }
        }
    }


    public boolean produceItems(Action action) {
        if (action == null) {
            return false;
        }
        if (action.getProduced().size() == 0) {
            return true;
        }
        Location unplaced = null;
        // Find unplaced location - if game files don't have unplaced, throws error
        for (Location Loc : locationList) {
            if (Loc.getName().equals("unplaced")) {
                unplaced = Loc;
            }
        }
        // If valid action and unplaced exists
        if (unplaced != null) {
            // Search for Locations to check the new path is valid
            // Search for artefacts, characters, furniture in unplaced, add to inventory/location
            // Health added to player
            Iterator<String> iterator = action.getProduced().iterator();
            while (iterator.hasNext()) {
                String str = iterator.next();
                // If produced string is location name, add to  available paths
                for (Location Loc : locationList) {
                    if (Loc.getName().equals(str)) {
                        currentLocation.addPath(str);
                        if (!iterator.hasNext()) return true;
                    }
                }
                if (unplaced.searchArtefacts(str)) {
                    currentPlayer.addInventory(unplaced.removeArtefact(str));
                    if (!iterator.hasNext()) return true;
                }
                else if (unplaced.searchFurniture(str)) {
                    currentLocation.addFurniture(unplaced.removeFurniture(str));
                    if (!iterator.hasNext()) return true;
                }
                else if (unplaced.searchCharacters(str)) {
                    currentLocation.addCharacter(unplaced.removeCharacter(str));
                    if (!iterator.hasNext()) return true;
                }
                else if (str.equals("health")) {
                    currentPlayer.setHealth(currentPlayer.getHealth() + 1);
                    if (!iterator.hasNext()) return true;
                }
                else {
                    return false;
                }
            }
        }
        return false;
    }

    // Drops all player items in current location, sends player back to start
    private void resetPlayer() {
        List<Artefact> inventory = currentPlayer.returnInventory();
        Artefact item;
        int i = inventory.size();

        while (i > 0) {
            item = currentPlayer.removeInventory(inventory.get(i - 1).getName());
            currentLocation.addArtefact(item);
            i--;
        }
        currentLocation.removePlayer(currentPlayer.getName());
        locationList.get(0).addPlayer(currentPlayer);
        currentPlayer.setHealth(3);
    }

}
