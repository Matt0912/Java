import java.util.ArrayList;
import java.util.List;

public class Player extends Character {
    private int health;
    private List<Artefact> inventory = new ArrayList<>();

    public Player(String name, String description, int health) {
        super(name, description);
        this.health = health;
    }

    public int getHealth() {
        return health;
    }
    public void setHealth(int newHealth) {
        health = newHealth;
    }

    public void addInventory(Artefact newArtefact) {
        inventory.add(newArtefact);
    }

    public boolean searchInventory(String input) {
        for (Artefact temp : inventory) {
            if (temp.getName().equals(input)) {
                return true;
            }
        }
        return false;
    }

    public List<Artefact> returnInventory() {
        return inventory;
    }

    public Artefact removeInventory(String input) {
        for (Artefact temp : inventory) {
            if (temp.getName().equals(input)) {
                inventory.remove(temp);
                return temp;
            }
        }
        return null;
    }

}
