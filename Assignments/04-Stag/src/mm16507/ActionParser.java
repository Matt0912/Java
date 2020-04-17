import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ActionParser {

    public static void parse(String actionsFile, List<Action> actionList) {
        JSONParser actionsParser = new JSONParser();

        try (FileReader reader = new FileReader(actionsFile)) {
            // Read JSON file and typecast to JSONObject
            JSONObject obj = (JSONObject) actionsParser.parse(reader);
            JSONArray actions = (JSONArray) obj.get("actions");

            // Iterate over objects inside JSON 'actions' array (each object is one action)
            for (Object tempObject : actions) {
                try {
                    actionList.add(parseJSONObject((JSONObject) tempObject));
                } catch (InvalidActionsFileException iafe) {
                    System.err.println(iafe);
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static Action parseJSONObject(JSONObject tempObject) throws InvalidActionsFileException {
        Action newAction = new Action();
        // Assign each JSON array to each action category
        JSONArray triggers = (JSONArray) tempObject.get("triggers");
        JSONArray subjects = (JSONArray) tempObject.get("subjects");
        JSONArray consumed = (JSONArray) tempObject.get("consumed");
        JSONArray produced = (JSONArray) tempObject.get("produced");
        // Add each element to the action object
        for (Object item : triggers) {
            newAction.addTrigger(item.toString());
        }
        for (Object item : subjects) {
            newAction.addSubject(item.toString());
        }
        for (Object item : consumed) {
            newAction.addConsumed(item.toString());
        }
        for (Object item : produced) {
            newAction.addProduced(item.toString());
        }
        String narration = (String) tempObject.get("narration");
        newAction.addNarration(narration);

        if (newAction.getTriggers().size() == 0) {
            throw new InvalidActionsFileException("triggers");
        }

        return newAction;
    }
}
