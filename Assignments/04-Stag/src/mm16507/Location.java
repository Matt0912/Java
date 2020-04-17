import java.util.ArrayList;
import java.util.List;

public class Location  extends Entity {
    private String name;
    private String description;
    private List<Artefact> artefactList = new ArrayList<>();
    private List<Furniture> furnitureList = new ArrayList<>();
    private List<Character> characterList = new ArrayList<>();
    private List<String> pathList = new ArrayList<>();
    private List<Player> playerList = new ArrayList<>();

    public Location(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }

    // Add entities to their respective lists
    public void addArtefact(Artefact newArtefact) {
        artefactList.add(newArtefact);
    }
    public void addFurniture(Furniture newFurniture) {
        furnitureList.add(newFurniture);
    }
    public void addCharacter(Character newCharacter) {
        characterList.add(newCharacter);
    }
    public void addPath(String newPath) {
        pathList.add(newPath);
    }
    public void addPlayer(Player newPlayer) {
        playerList.add(newPlayer);
    }

    // Return list of entities as a string (for printing to user)
    public String getArtefacts() {
        String output = "";
        if (artefactList.size() == 0) {
            return "None";
        }
        for (Artefact temp : artefactList) {
            output = output + temp.getName() + ", ";
        }
        return output.substring(0, output.length() - 2);
    }
    public String getFurniture() {
        String output = "";
        if (furnitureList.size() == 0) {
            return "None";
        }
        for (Furniture temp : furnitureList) {
            output = output + temp.getName() + ", ";
        }
        return output.substring(0, output.length() - 2);
    }
    public String getCharacter() {
        String output = "";
        if (characterList.size() == 0) {
            return "None";
        }
        for (Character temp : characterList) {
            output = output + temp.getName() + ", ";
        }
        return output.substring(0, output.length() - 2);
    }
    public String getPathList() {
        String output = "";
        if (pathList.size() == 0) {
            return "None";
        }
        for (String str : pathList) {
            output = output + str + ", ";
        }
        return output.substring(0, output.length() - 2);
    }

    public Player getPlayer(String name) {
        for (Player temp : playerList) {
            if (temp.getName().equals(name)) {
                return temp;
            }
        }
        return null;
    }

    public Artefact removeArtefact(String name) {
        for (Artefact temp : artefactList) {
            if (temp.getName().equals(name)) {
                artefactList.remove(temp);
                return temp;
            }
        }
        return null;
    }
    public Furniture removeFurniture(String name) {
        for (Furniture temp : furnitureList) {
            if (temp.getName().equals(name)) {
                furnitureList.remove(temp);
                return temp;
            }
        }
        return null;
    }
    public Character removeCharacter(String name) {
        for (Character temp : characterList) {
            if (temp.getName().equals(name)) {
                characterList.remove(temp);
                return temp;
            }
        }
        return null;
    }
    public String removePath(String name) {
        for (String temp : pathList) {
            if (temp.equals(name)) {
                pathList.remove(temp);
                return temp;
            }
        }
        return null;
    }
    public Player removePlayer(String name) {
        for (Player temp : playerList) {
            if (temp.getName().equals(name)) {
                playerList.remove(temp);
                return temp;
            }
        }
        return null;
    }

    public boolean searchArtefacts(String input) {
        for (Artefact temp : artefactList) {
            if (temp.getName().equals(input)) {
                return true;
            }
        }
        return false;
    }
    public boolean searchFurniture(String input) {
        for (Furniture temp : furnitureList) {
            if (temp.getName().equals(input)) {
                return true;
            }
        }
        return false;
    }
    public boolean searchCharacters(String input) {
        for (Character temp : characterList) {
            if (temp.getName().equals(input)) {
                return true;
            }
        }
        return false;
    }
    public boolean searchPathList(String input) {
        return pathList.contains(input);
    }
    public boolean searchEntities(String input) {
        return searchArtefacts(input) || searchFurniture(input) ||
                searchCharacters(input) || searchPathList(input);
    }



}
