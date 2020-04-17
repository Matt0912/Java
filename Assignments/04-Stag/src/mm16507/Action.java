import java.util.ArrayList;
import java.util.List;

public class Action {
    private List<String> triggers = new ArrayList<>();
    private List<String> subjects = new ArrayList<>();
    private List<String> consumed = new ArrayList<>();
    private List<String> produced = new ArrayList<>();
    private String narration;

    public Action() {

    }

    public void addTrigger(String newTrigger) {
        triggers.add(newTrigger);
    }
    public void addSubject(String newSubject) {
        subjects.add(newSubject);
    }
    public void addConsumed(String newConsumed) {
        consumed.add(newConsumed);
    }
    public void addProduced(String newProduced) {
        produced.add(newProduced);
    }
    public void addNarration(String newNarration) {
        narration = newNarration;
    }

    public boolean searchTriggers(String input) {
        return triggers.contains(input);
    }

    public List<String> getTriggers() { return triggers; }

    public List<String> getSubjects() {
        return subjects;
    }

    public List<String> getConsumed() {
        return consumed;
    }

    public List<String> getProduced() {
        return produced;
    }

    public String getNarration() {
        return narration;
    }
}
